package com.tianji.learning.service;

import com.tianji.learning.domain.po.PointsBoardSeason;
import com.baomidou.mybatisplus.extension.service.IService;
import com.tianji.learning.domain.vo.PointsBoardSeasonVO;

import java.util.List;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author liuyp
 * @since 2023-07-16
 */
public interface IPointsBoardSeasonService extends IService<PointsBoardSeason> {

    /**
     * 查询积分赛季列表
     * @return 赛季列表
     */
    List<PointsBoardSeasonVO> queryPointBoardSeasons();
}