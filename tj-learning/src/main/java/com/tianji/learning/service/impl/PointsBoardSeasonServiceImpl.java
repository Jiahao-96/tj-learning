package com.tianji.learning.service.impl;

import com.tianji.common.utils.BeanUtils;
import com.tianji.common.utils.CollUtils;
import com.tianji.learning.domain.po.PointsBoardSeason;
import com.tianji.learning.domain.vo.PointsBoardSeasonVO;
import com.tianji.learning.mapper.PointsBoardSeasonMapper;
import com.tianji.learning.service.IPointsBoardSeasonService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author liuyp
 * @since 2023-07-16
 */
@Service
public class PointsBoardSeasonServiceImpl extends ServiceImpl<PointsBoardSeasonMapper, PointsBoardSeason> implements IPointsBoardSeasonService {

    @Override
    public List<PointsBoardSeasonVO> queryPointBoardSeasons() {
        LocalDate today = LocalDate.now();
        //查询的必须是当前赛季或之前的
        List<PointsBoardSeason> list = lambdaQuery()
                .le(PointsBoardSeason::getBeginTime, today)
                .list();
        if (CollUtils.isEmpty(list)) {
            return CollUtils.emptyList();
        }

        return BeanUtils.copyList(list, PointsBoardSeasonVO.class);
    }
}