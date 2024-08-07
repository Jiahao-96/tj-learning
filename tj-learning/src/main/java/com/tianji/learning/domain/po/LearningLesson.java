package com.tianji.learning.domain.po;

import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.annotation.IdType;
import java.time.LocalDateTime;
import com.baomidou.mybatisplus.annotation.TableId;
import java.io.Serializable;

/**
 * <p>
 * 学生课程表
 * </p>
 *
 * @author jiahao
 * @since 2024-08-06
 */
@TableName("learning_lesson")
public class LearningLesson implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 主键
     */
    @TableId(value = "id", type = IdType.ASSIGN_ID)
    private Long id;

    /**
     * 学员id
     */
    private Long userId;

    /**
     * 课程id
     */
    private Long courseId;

    /**
     * 课程状态，0-未学习，1-学习中，2-已学完，3-已失效
     */
    private Integer status;

    /**
     * 每周学习频率，例如每周学习6小节，则频率为6
     */
    private Integer weekFreq;

    /**
     * 学习计划状态，0-没有计划，1-计划进行中
     */
    private Integer planStatus;

    /**
     * 已学习小节数量
     */
    private Integer learnedSections;

    /**
     * 最近一次学习的小节id
     */
    private Long latestSectionId;

    /**
     * 最近一次学习的时间
     */
    private LocalDateTime latestLearnTime;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;

    /**
     * 过期时间
     */
    private LocalDateTime expireTime;

    /**
     * 更新时间
     */
    private LocalDateTime updateTime;

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
    public Long getCourseId() {
        return courseId;
    }

    public void setCourseId(Long courseId) {
        this.courseId = courseId;
    }
    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }
    public Integer getWeekFreq() {
        return weekFreq;
    }

    public void setWeekFreq(Integer weekFreq) {
        this.weekFreq = weekFreq;
    }
    public Integer getPlanStatus() {
        return planStatus;
    }

    public void setPlanStatus(Integer planStatus) {
        this.planStatus = planStatus;
    }
    public Integer getLearnedSections() {
        return learnedSections;
    }

    public void setLearnedSections(Integer learnedSections) {
        this.learnedSections = learnedSections;
    }
    public Long getLatestSectionId() {
        return latestSectionId;
    }

    public void setLatestSectionId(Long latestSectionId) {
        this.latestSectionId = latestSectionId;
    }
    public LocalDateTime getLatestLearnTime() {
        return latestLearnTime;
    }

    public void setLatestLearnTime(LocalDateTime latestLearnTime) {
        this.latestLearnTime = latestLearnTime;
    }
    public LocalDateTime getCreateTime() {
        return createTime;
    }

    public void setCreateTime(LocalDateTime createTime) {
        this.createTime = createTime;
    }
    public LocalDateTime getExpireTime() {
        return expireTime;
    }

    public void setExpireTime(LocalDateTime expireTime) {
        this.expireTime = expireTime;
    }
    public LocalDateTime getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(LocalDateTime updateTime) {
        this.updateTime = updateTime;
    }

    @Override
    public String toString() {
        return "LearningLesson{" +
            "id=" + id +
            ", userId=" + userId +
            ", courseId=" + courseId +
            ", status=" + status +
            ", weekFreq=" + weekFreq +
            ", planStatus=" + planStatus +
            ", learnedSections=" + learnedSections +
            ", latestSectionId=" + latestSectionId +
            ", latestLearnTime=" + latestLearnTime +
            ", createTime=" + createTime +
            ", expireTime=" + expireTime +
            ", updateTime=" + updateTime +
        "}";
    }
}
