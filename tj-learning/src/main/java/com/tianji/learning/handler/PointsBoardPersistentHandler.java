package com.tianji.learning.handler;

import com.tianji.common.utils.CollUtils;
import com.tianji.common.utils.DateUtils;
import com.tianji.learning.constants.RedisConstants;
import com.tianji.learning.domain.po.PointsBoard;
import com.tianji.learning.domain.po.PointsBoardSeason;
import com.tianji.learning.service.IPointsBoardSeasonService;
import com.tianji.learning.service.IPointsBoardService;
import com.tianji.learning.utils.TableNameContext;
import com.xxl.job.core.context.XxlJobHelper;
import com.xxl.job.core.handler.annotation.XxlJob;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.List;

/**
 * @author liuyp
 * @since 2023/07/19
 */
@Component
@RequiredArgsConstructor
public class PointsBoardPersistentHandler {
    private final IPointsBoardSeasonService seasonService;
    private final IPointsBoardService pointsBoardService;
    private final StringRedisTemplate redisTemplate;
    /**
     * 定时创建上一赛季的榜单表
     */
    @XxlJob("createTableJob")
    public void createPointsBoardTable(){
        //1. 查询上一赛季的id
        //1.1 获取上个月的时间
        LocalDate lastMonth = LocalDate.now().minusMonths(1);

        //1.2 查找上个月的赛季id
        PointsBoardSeason season = seasonService.lambdaQuery()
                .le(PointsBoardSeason::getBeginTime, lastMonth)
                .ge(PointsBoardSeason::getEndTime, lastMonth)
                .one();
        if (season == null) {
            return;
        }

        //2. 给上一赛季创建榜单表
        pointsBoardService.createTable(season.getId());
    }

    /**
     * 上一赛季的积分排行榜持久化
     */
    @XxlJob("savePointsBoard2DB")
    public void savePointsBoard() {
        //1. 从Redis里读取上一赛季的榜单数据-组装redis缓存的key
        LocalDate lastMonth = LocalDate.now().minusMonths(1);
        String key = RedisConstants.POINTS_BOARD_KEY_PREFIX + lastMonth.format(DateUtils.POINTS_BOARD_SUFFIX_FORMATTER);
        //2. 准备榜单的数据库表名
        //2.1 查询上一赛季的id
        PointsBoardSeason season = seasonService.lambdaQuery()
                .le(PointsBoardSeason::getBeginTime, lastMonth)
                .ge(PointsBoardSeason::getEndTime, lastMonth)
                .one();
        if (season == null) {
            return;
        }
        //2.2 生成表名，绑定到当前线程上
        TableNameContext.setTableName("points_board_" + season.getId());

        //3. 从redis查询缓存的榜单数据：因为数据量大，所以需要分页查询 + 分片广播
        int shardTotal = XxlJobHelper.getShardTotal();
        int shardIndex = XxlJobHelper.getShardIndex();
        int pageNo = shardIndex+1;
        int pageSize = 1000;
        while (true) {
            //  当前节点处理：节点索引+1 + 总节点数 的所有页码
            List<PointsBoard> pointsBoardList = pointsBoardService.queryCurrentBoardsList(key, pageNo, pageSize);
            if (CollUtils.isEmpty(pointsBoardList)) {
                break;
            }

            //  处理榜单信息
            pointsBoardList.forEach(pointsBoard -> {
                pointsBoard.setId(pointsBoard.getRank().longValue());
            });
            //  持久化保存到数据库里
            pointsBoardService.saveBatch(pointsBoardList);

            //  循环处理下一批
            pageNo += shardTotal;
        }

        //4. 从当前线程上清理表名
        TableNameContext.removeTableName();
    }

    /**
     * 清理Redis里上一赛季的缓存数据
     */
    @XxlJob("clearPointsBoardFromRedis")
    public void clearPointsBoardFromRedis(){
        //1. 组装key
        LocalDate lastMonth = LocalDate.now().minusMonths(1);
        String key = RedisConstants.POINTS_BOARD_KEY_PREFIX + lastMonth.format(DateUtils.POINTS_BOARD_SUFFIX_FORMATTER);

        //2. 清理缓存(异步删除)
        redisTemplate.unlink(key);
    }

}