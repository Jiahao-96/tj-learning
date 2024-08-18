package com.tianji.learning.domain.po;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import java.io.Serializable;

/**
 * <p>
 * 学霸天梯榜
 * </p>
 *
 * @author jiahao
 * @since 2024-08-15
 */
@TableName("points_board")
public class PointsBoard implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 榜单id
     */
    @TableId(value = "id", type = IdType.INPUT)
    private Long id;

    /**
     * 学生id
     */
    private Long userId;

    /**
     * 积分值
     */
    private Integer points;

    /**
     * 名次，只记录赛季前100
     */
    @TableField(exist = false)
    private Integer rank;

    /**
     * 赛季，例如 1,就是第一赛季，2-就是第二赛季
     */
    @TableField(exist = false)
    private Integer season;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }
    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }
    public Integer getPoints() {
        return points;
    }

    public void setPoints(Integer points) {
        this.points = points;
    }
    public Integer getRank() {
        return rank;
    }

    public void setRank(Integer rank) {
        this.rank = rank;
    }
    public Integer getSeason() {
        return season;
    }

    public void setSeason(Integer season) {
        this.season = season;
    }

    @Override
    public String toString() {
        return "PointsBoard{" +
            "id=" + id +
            ", userId=" + userId +
            ", points=" + points +
            ", rank=" + rank +
            ", season=" + season +
        "}";
    }
}
