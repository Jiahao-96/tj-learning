package com.tianji.learning.service;

import com.tianji.learning.domain.po.PointsBoard;
import com.baomidou.mybatisplus.extension.service.IService;
import com.tianji.learning.domain.query.PointsBoardQuery;
import com.tianji.learning.domain.vo.PointsBoardVO;

import java.util.List;

/**
 * <p>
 * 学霸天梯榜 服务类
 * </p>
 *
 * @author liuyp
 * @since 2023-07-18
 */
public interface IPointsBoardService extends IService<PointsBoard> {

    /**
     * 查询学霸天梯榜
     * @param query 查询条件
     * @return
     */
    PointsBoardVO queryPointsBoard(PointsBoardQuery query);
    /**
     * 给指定赛季创建积分榜单表
     * @param seasonId 赛季id
     */
    void createTable(Integer seasonId);

    /**
     * 从赛季榜单缓存里，分页查询榜单数据
     * @param pageNo 页码
     * @param pageSize 每页几条
     * @param key 缓存的key
     * @return 榜单里的用户信息列表
     */
    List<PointsBoard> queryCurrentBoardsList(String key, Integer pageNo, Integer pageSize);
}