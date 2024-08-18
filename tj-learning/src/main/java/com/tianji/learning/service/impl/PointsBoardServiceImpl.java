package com.tianji.learning.service.impl;

import com.tianji.api.client.user.UserClient;
import com.tianji.api.dto.user.UserDTO;
import com.tianji.common.utils.CollUtils;
import com.tianji.common.utils.DateUtils;
import com.tianji.common.utils.StringUtils;
import com.tianji.common.utils.UserContext;
import com.tianji.learning.constants.RedisConstants;
import com.tianji.learning.domain.po.PointsBoard;
import com.tianji.learning.domain.query.PointsBoardQuery;
import com.tianji.learning.domain.vo.PointsBoardItemVO;
import com.tianji.learning.domain.vo.PointsBoardVO;
import com.tianji.learning.mapper.PointsBoardMapper;
import com.tianji.learning.service.IPointsBoardService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import jodd.util.StringUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.BoundZSetOperations;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.format.number.money.CurrencyUnitFormatter;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

/**
 * <p>
 * 学霸天梯榜 服务实现类
 * </p>
 *
 * @author liuyp
 * @since 2023-07-18
 */
@Service
@RequiredArgsConstructor
public class PointsBoardServiceImpl extends ServiceImpl<PointsBoardMapper, PointsBoard> implements IPointsBoardService {

    private final StringRedisTemplate redisTemplate;
    private final UserClient userClient;

    @Override
    public PointsBoardVO queryPointsBoard(PointsBoardQuery query) {
        Long season = query.getSeason();
        boolean currentSeason = season == null || season == 0;
        LocalDate now = LocalDate.now();

        PointsBoardVO result = new PointsBoardVO();
        //1. 查询我的榜单排名和我的积分值
        String key = RedisConstants.POINTS_BOARD_KEY_PREFIX + now.format(DateUtils.POINTS_BOARD_SUFFIX_FORMATTER);
        PointsBoard myPointsBoard = currentSeason ? queryMyCurrentBoard(key) : queryMyHistoryBoard(query);
        if (myPointsBoard != null) {
            result.setRank(myPointsBoard.getRank());
            result.setPoints(myPointsBoard.getPoints());
        }

        //2. 查询榜单列表
        List<PointsBoard> pointsBoardList = currentSeason ?
                queryCurrentBoardsList(key, query.getPageNo(), query.getPageSize()) :
                queryHistoryBoardsList(query);
        if (CollUtils.isEmpty(pointsBoardList)) {
            return result;
        }

        Set<Long> userIdSet = pointsBoardList.stream().map(PointsBoard::getUserId).collect(Collectors.toSet());
        List<UserDTO> userList = userClient.queryUserByIds(userIdSet);
        Map<Long, String> userNameMap = userList.stream().collect(Collectors.toMap(UserDTO::getId, UserDTO::getName));

        List<PointsBoardItemVO> voList = new ArrayList<>();
        for (PointsBoard pointsBoard : pointsBoardList) {
            PointsBoardItemVO itemVO = new PointsBoardItemVO();
            itemVO.setRank(pointsBoard.getRank()+1);
            itemVO.setPoints(pointsBoard.getPoints());
            itemVO.setName(userNameMap.get(pointsBoard.getUserId()));
            voList.add(itemVO);
        }
        result.setBoardList(voList);

        return result;
    }
    @Override
    public void createTable(Integer seasonId) {
        String tableName = "points_board_" + seasonId;
        baseMapper.createTable(tableName);
    }
    /**
     * 分页查询榜单列表-从Redis里查
     * @param key 缓存的key
     * @return
     */
    public List<PointsBoard> queryCurrentBoardsList(String key, Integer pageNo, Integer pageSize) {
        int start = (pageNo-1)*pageSize;
        Set<ZSetOperations.TypedTuple<String>> tuples = redisTemplate.opsForZSet().reverseRangeWithScores(key, start, start+pageSize-1);
        if (CollUtils.isEmpty(tuples)) {
            return Collections.emptyList();
        }


        List<PointsBoard> list = new ArrayList<>();
        for (ZSetOperations.TypedTuple<String> tuple : tuples) {
            String userId = tuple.getValue();
            Double points = tuple.getScore();

            if (StringUtils.isBlank(userId) || points == null) {
                continue;
            }

            PointsBoard pointsBoard = new PointsBoard();
            pointsBoard.setPoints(points.intValue());
            pointsBoard.setRank(++start);
            pointsBoard.setUserId(Long.parseLong(userId));
            list.add(pointsBoard);
        }

        return list;
    }

    /**
     * 查询我的榜单信息-从Redis里查
     * @param key 缓存的key
     * @return 我的榜单信息
     */
    private PointsBoard queryMyCurrentBoard(String key) {
        BoundZSetOperations<String, String> ops = redisTemplate.boundZSetOps(key);
        Long userId = UserContext.getUser();
        //取当前用户的排名
        Long rank = ops.reverseRank(userId.toString());
        //取当前用户的分值
        Double points = ops.score(userId.toString());

        PointsBoard board = new PointsBoard();
        board.setRank(rank == null ? 0 : rank.intValue()+1);
        board.setPoints(points == null ? 0 : points.intValue());
        return board;
    }

    /**
     * 查询我的历史榜单信息-从MySQL里查
     * @param query
     * @return
     */
    private PointsBoard queryMyHistoryBoard(PointsBoardQuery query) {
        //todo
        return null;
    }

    /**
     * 分页查询历史榜单列表-从MySQL里查
     * @param query
     * @return
     */
    private List<PointsBoard> queryHistoryBoardsList(PointsBoardQuery query) {
        //todo
        return null;
    }
}