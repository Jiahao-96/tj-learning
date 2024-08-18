package com.tianji.learning.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.tianji.common.utils.CollUtils;
import com.tianji.common.utils.DateUtils;
import com.tianji.common.utils.UserContext;
import com.tianji.learning.constants.RedisConstants;
import com.tianji.learning.domain.po.PointsRecord;
import com.tianji.learning.domain.vo.PointsStatisticsVO;
import com.tianji.learning.enums.PointsRecordType;
import com.tianji.learning.mapper.PointsRecordMapper;
import com.tianji.learning.service.IPointsRecordService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;


@Service
@RequiredArgsConstructor
public class PointsRecordServiceImpl extends ServiceImpl<PointsRecordMapper, PointsRecord> implements IPointsRecordService {
    private StringRedisTemplate stringRedisTemplate;
    @Override
    public void addPointRecord(Long userId, int point, PointsRecordType recordType) {

        int maxPoints = recordType.getMaxPoints();
        LocalDateTime now = LocalDateTime.now();
        int realPoints = point;
        // 1-判断是否有积分上线
        if (maxPoints > 0) {
            // 2-有，则需要判断当天积分是否超过上限
            // 2.1 查询今日已得积分(需要sum求和，mp本身不支持，这里我们可以查询出来lamda求和[这种大家都会]，或者采用这种方式)
            LocalDateTime begin = DateUtils.getDayStartTime(now);
            LocalDateTime end = DateUtils.getDayEndTime(now);
            int currentPoints = queryUserPointsByTypeAndDate(userId, recordType, begin, end);

            // 2.2 判断是否超过上线
            if (currentPoints >= maxPoints) {
                // 2.3 超过，结束
                return;
            }

            // 2.4 没超过(有上限但没到上限)，保存积分，需要计算实际应得积分 避免超限
            if (point + currentPoints > maxPoints) {
                realPoints = maxPoints - currentPoints;
            }
        }
        //算完之后，本次行为不能记录分值了，就直接结束
        if (realPoints <= 0) {
            return;
        }
        // 3-没有，直接保存积分记录(没上限直接保存)
        PointsRecord pointsRecord = new PointsRecord();
        pointsRecord.setType(recordType);
        pointsRecord.setUserId(userId);
        pointsRecord.setPoints(realPoints);
        save(pointsRecord);

        //3. 保存到Redis里统计榜单排名
        String boardCacheKey = RedisConstants.POINTS_BOARD_KEY_PREFIX + now.format(DateUtils.POINTS_BOARD_SUFFIX_FORMATTER);
        stringRedisTemplate.opsForZSet().incrementScore(boardCacheKey, userId.toString(), realPoints);
    }

    private int queryUserPointsByTypeAndDate(Long userId,
                                             PointsRecordType type,
                                             LocalDateTime begin,
                                             LocalDateTime end) {
        // 1.查询条件
        QueryWrapper<PointsRecord> wrapper = new QueryWrapper<>();
        wrapper.lambda()
                .eq(PointsRecord::getUserId, userId)
                .eq(type != null, PointsRecord::getType, type)
                .between(begin != null && end != null, PointsRecord::getCreateTime, begin, end);
        // 2.调用mapper，查询结果
        Integer points = getBaseMapper().queryUserPointsByTypeAndDate(wrapper);
        // 3.判断并返回
        return points == null ? 0 : points;
    }

    @Override
    public List<PointsStatisticsVO> queryMyPointsToday() {
        Long userId = UserContext.getUser();
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime start = DateUtils.getDayStartTime(now);
        LocalDateTime end = DateUtils.getDayEndTime(now);
        //统计当前用户今天的累计积分
        List<PointsRecord> recordList = baseMapper.queryPointsStatistics(userId, start, end);
        if (CollUtils.isEmpty(recordList)) {
            return CollUtils.emptyList();
        }

        //封装结果
        return recordList.stream()
                .map(record->{
                    PointsStatisticsVO vo = new PointsStatisticsVO();
                    vo.setType(record.getType().getDesc());
                    vo.setMaxPoints(record.getType().getMaxPoints());
                    vo.setPoints(record.getPoints());
                    return vo;
                })
                .collect(Collectors.toList());
    }

}
