package com.tianji.learning.service;

import com.tianji.learning.domain.po.PointsRecord;
import com.baomidou.mybatisplus.extension.service.IService;
import com.tianji.learning.domain.vo.PointsStatisticsVO;
import com.tianji.learning.enums.PointsRecordType;

import java.util.List;

/**
 * <p>
 * 学习积分记录，每个月底清零 服务类
 * </p>
 *
 * @author jiahao
 * @since 2024-08-15
 */
public interface IPointsRecordService extends IService<PointsRecord> {

    /**
     * 查询当前用户的今日积分
     * @return 当前用户的今天积分结果
     */
    List<PointsStatisticsVO> queryMyPointsToday();
    void addPointRecord(Long userId, int point, PointsRecordType recordType);
}
