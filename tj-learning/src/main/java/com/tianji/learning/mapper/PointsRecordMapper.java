package com.tianji.learning.mapper;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Constants;
import com.tianji.learning.domain.po.PointsRecord;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.time.LocalDateTime;
import java.util.List;

public interface PointsRecordMapper extends BaseMapper<PointsRecord> {

    @Select("SELECT SUM(points) FROM points_record ${ew.customSqlSegment}")
    Integer queryUserPointsByTypeAndDate(@Param(Constants.WRAPPER) QueryWrapper<PointsRecord> wrapper);

    @Select("SELECT type, SUM(points) AS points FROM points_record ${ew.customSqlSegment} GROUP BY type")
    List<PointsRecord> queryUserPointsByDate(@Param(Constants.WRAPPER) QueryWrapper<PointsRecord> wrapper);

    @Select("select type, sum(points) as points from points_record where user_id = #{userId} and  create_time between #{begin} and #{end} group by type")
    List<PointsRecord> queryPointsStatistics(@Param("userId") Long userId,
                                             @Param("begin") LocalDateTime start,
                                             @Param("end") LocalDateTime end);
}
