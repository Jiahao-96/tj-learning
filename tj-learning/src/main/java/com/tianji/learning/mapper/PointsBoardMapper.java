package com.tianji.learning.mapper;

import com.tianji.learning.domain.po.PointsBoard;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;

/**
 * <p>
 * 学霸天梯榜 Mapper 接口
 * </p>
 *
 * @author liuyp
 * @since 2023-07-18
 */
public interface PointsBoardMapper extends BaseMapper<PointsBoard> {

    /**
     * 创建榜单表
     * @param tableName 表名称
     */
    void createTable(String tableName);
}