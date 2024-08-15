package com.tianji.learning.domain.po;

import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.annotation.IdType;
import java.time.LocalDate;
import com.baomidou.mybatisplus.annotation.TableId;
import java.io.Serializable;

/**
 * <p>
 * 
 * </p>
 *
 * @author jiahao
 * @since 2024-08-15
 */
@TableName("points_board_season")
public class PointsBoardSeason implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 自增长id，season标示
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    /**
     * 赛季名称，例如：第1赛季
     */
    private String name;

    /**
     * 赛季开始时间
     */
    private LocalDate beginTime;

    /**
     * 赛季结束时间
     */
    private LocalDate endTime;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
    public LocalDate getBeginTime() {
        return beginTime;
    }

    public void setBeginTime(LocalDate beginTime) {
        this.beginTime = beginTime;
    }
    public LocalDate getEndTime() {
        return endTime;
    }

    public void setEndTime(LocalDate endTime) {
        this.endTime = endTime;
    }

    @Override
    public String toString() {
        return "PointsBoardSeason{" +
            "id=" + id +
            ", name=" + name +
            ", beginTime=" + beginTime +
            ", endTime=" + endTime +
        "}";
    }
}
