package com.tianji.promotion.domain.po;

import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import java.io.Serializable;

/**
 * <p>
 * 优惠券作用范围信息
 * </p>
 *
 * @author jiahao
 * @since 2024-08-18
 */
@TableName("coupon_scope")
public class CouponScope implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 范围限定类型：1-分类，2-课程，等等
     */
    private Integer type;

    /**
     * 优惠券id
     */
    private Long couponId;

    /**
     * 优惠券作用范围的业务id，例如分类id、课程id
     */
    private Long bizId;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }
    public Integer getType() {
        return type;
    }

    public void setType(Integer type) {
        this.type = type;
    }
    public Long getCouponId() {
        return couponId;
    }

    public void setCouponId(Long couponId) {
        this.couponId = couponId;
    }
    public Long getBizId() {
        return bizId;
    }

    public void setBizId(Long bizId) {
        this.bizId = bizId;
    }

    @Override
    public String toString() {
        return "CouponScope{" +
            "id=" + id +
            ", type=" + type +
            ", couponId=" + couponId +
            ", bizId=" + bizId +
        "}";
    }
}
