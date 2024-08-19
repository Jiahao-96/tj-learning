package com.tianji.promotion.domain.po;

import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.annotation.IdType;
import java.time.LocalDateTime;
import com.baomidou.mybatisplus.annotation.TableId;
import com.tianji.promotion.enums.ExchangeCodeStatus;

import java.io.Serializable;

/**
 * <p>
 * 兑换码
 * </p>
 *
 * @author jiahao
 * @since 2024-08-18
 */
@TableName("exchange_code")
public class ExchangeCode implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 兑换码id
     */
    @TableId(value = "id", type = IdType.ASSIGN_ID)
    private Integer id;

    /**
     * 兑换码
     */
    private String code;

    /**
     * 兑换码状态， 1：待兑换，2：已兑换，3：兑换活动已结束
     */
    private ExchangeCodeStatus status;

    /**
     * 兑换人
     */
    private Long userId;

    /**
     * 兑换类型，1：优惠券，以后再添加其它类型
     */
    private Integer type;

    /**
     * 兑换码目标id，例如兑换优惠券，该id则是优惠券的配置id
     */
    private Long exchangeTargetId;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;

    /**
     * 兑换码过期时间
     */
    private LocalDateTime expiredTime;

    /**
     * 更新时间
     */
    private LocalDateTime updateTime;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }
    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }
    public ExchangeCodeStatus getStatus() {
        return status;
    }

    public void setStatus(ExchangeCodeStatus status) {
        this.status = status;
    }
    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }
    public Integer getType() {
        return type;
    }

    public void setType(Integer type) {
        this.type = type;
    }
    public Long getExchangeTargetId() {
        return exchangeTargetId;
    }

    public void setExchangeTargetId(Long exchangeTargetId) {
        this.exchangeTargetId = exchangeTargetId;
    }
    public LocalDateTime getCreateTime() {
        return createTime;
    }

    public void setCreateTime(LocalDateTime createTime) {
        this.createTime = createTime;
    }
    public LocalDateTime getExpiredTime() {
        return expiredTime;
    }

    public void setExpiredTime(LocalDateTime expiredTime) {
        this.expiredTime = expiredTime;
    }
    public LocalDateTime getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(LocalDateTime updateTime) {
        this.updateTime = updateTime;
    }

    @Override
    public String toString() {
        return "ExchangeCode{" +
            "id=" + id +
            ", code=" + code +
            ", status=" + status +
            ", userId=" + userId +
            ", type=" + type +
            ", exchangeTargetId=" + exchangeTargetId +
            ", createTime=" + createTime +
            ", expiredTime=" + expiredTime +
            ", updateTime=" + updateTime +
        "}";
    }
}
