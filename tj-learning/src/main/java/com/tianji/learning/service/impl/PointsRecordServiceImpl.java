package com.tianji.learning.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.tianji.common.utils.DateUtils;
import com.tianji.learning.domain.po.PointsRecord;
import com.tianji.learning.enums.PointsRecordType;
import com.tianji.learning.mapper.PointsRecordMapper;
import com.tianji.learning.service.IPointsRecordService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;


@Service
public class PointsRecordServiceImpl extends ServiceImpl<PointsRecordMapper, PointsRecord> implements IPointsRecordService {

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

        // 3-没有，直接保存积分记录(没上限直接保存)
        PointsRecord pointsRecord = new PointsRecord();
        pointsRecord.setType(recordType);
        pointsRecord.setUserId(userId);
        pointsRecord.setPoints(realPoints);

        save(pointsRecord);
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
}
