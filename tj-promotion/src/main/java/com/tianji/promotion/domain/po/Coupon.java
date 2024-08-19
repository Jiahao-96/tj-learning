package com.tianji.promotion.domain.po;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.tianji.promotion.enums.CouponStatus;
import com.tianji.promotion.enums.DiscountType;
import com.tianji.promotion.enums.ObtainType;

import java.time.LocalDateTime;
import java.io.Serializable;

/**
 * <p>
 * 优惠券的规则信息
 * </p>
 *
 * @author jiahao
 * @since 2024-08-18
 */
@TableName("coupon")
public class Coupon implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 优惠券id
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 优惠券名称，可以和活动名称保持一致
     */
    @TableField("name")
    private String name;

    /**
     * 优惠券类型，1：普通券。目前就一种，保留字段
     */
    private Integer type;

    /**
     * 折扣类型，1：每满减，2：折扣，3：无门槛，4：满减
     */
    private DiscountType discountType;

    /**
     * 是否限定作用范围，false：不限定，true：限定。默认false
     */
    @TableField("specific")
    private Boolean specific;

    /**
     * 折扣值，如果是满减则存满减金额，如果是折扣，则存折扣率，8折就是存80
     */
    private Integer discountValue;

    /**
     * 使用门槛，0：表示无门槛，其他值：最低消费金额
     */
    private Integer thresholdAmount;

    /**
     * 最高优惠金额，满减最大，0：表示没有限制，不为0，则表示该券有金额的限制
     */
    private Integer maxDiscountAmount;

    /**
     * 获取方式：1：手动领取，2：兑换码
     */
    private ObtainType obtainWay;

    /**
     * 开始发放时间
     */
    private LocalDateTime issueBeginTime;

    /**
     * 结束发放时间
     */
    private LocalDateTime issueEndTime;

    /**
     * 优惠券有效期天数，0：表示有效期是指定有效期的
     */
    private Integer termDays;

    /**
     * 优惠券有效期开始时间
     */
    private LocalDateTime termBeginTime;

    /**
     * 优惠券有效期结束时间
     */
    private LocalDateTime termEndTime;

    /**
     * 优惠券配置状态，1：待发放，2：未开始   3：进行中，4：已结束，5：暂停
     */
    private CouponStatus status;

    /**
     * 总数量，不超过5000
     */
    private Integer totalNum;

    /**
     * 已发行数量，用于判断是否超发
     */
    private Integer issueNum;

    /**
     * 已使用数量
     */
    private Integer usedNum;

    /**
     * 每个人限领的数量，默认1
     */
    private Integer userLimit;

    /**
     * 拓展参数字段，保留字段
     */
    private String extParam;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;

    /**
     * 更新时间
     */
    private LocalDateTime updateTime;

    /**
     * 创建人
     */
    private Long creater;

    /**
     * 更新人
     */
    private Long updater;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
    public Integer getType() {
        return type;
    }

    public void setType(Integer type) {
        this.type = type;
    }
    public DiscountType getDiscountType() {
        return discountType;
    }

    public void setDiscountType(DiscountType discountType) {
        this.discountType = discountType;
    }
    public Boolean getSpecific() {
        return specific;
    }

    public void setSpecific(Boolean specific) {
        this.specific = specific;
    }
    public Integer getDiscountValue() {
        return discountValue;
    }

    public void setDiscountValue(Integer discountValue) {
        this.discountValue = discountValue;
    }
    public Integer getThresholdAmount() {
        return thresholdAmount;
    }

    public void setThresholdAmount(Integer thresholdAmount) {
        this.thresholdAmount = thresholdAmount;
    }
    public Integer getMaxDiscountAmount() {
        return maxDiscountAmount;
    }

    public void setMaxDiscountAmount(Integer maxDiscountAmount) {
        this.maxDiscountAmount = maxDiscountAmount;
    }
    public ObtainType getObtainWay() {
        return obtainWay;
    }

    public void setObtainWay(ObtainType obtainWay) {
        this.obtainWay = obtainWay;
    }
    public LocalDateTime getIssueBeginTime() {
        return issueBeginTime;
    }

    public void setIssueBeginTime(LocalDateTime issueBeginTime) {
        this.issueBeginTime = issueBeginTime;
    }
    public LocalDateTime getIssueEndTime() {
        return issueEndTime;
    }

    public void setIssueEndTime(LocalDateTime issueEndTime) {
        this.issueEndTime = issueEndTime;
    }
    public Integer getTermDays() {
        return termDays;
    }

    public void setTermDays(Integer termDays) {
        this.termDays = termDays;
    }
    public LocalDateTime getTermBeginTime() {
        return termBeginTime;
    }

    public void setTermBeginTime(LocalDateTime termBeginTime) {
        this.termBeginTime = termBeginTime;
    }
    public LocalDateTime getTermEndTime() {
        return termEndTime;
    }

    public void setTermEndTime(LocalDateTime termEndTime) {
        this.termEndTime = termEndTime;
    }
    public CouponStatus getStatus() {
        return status;
    }

    public void setStatus(CouponStatus status) {
        this.status = status;
    }
    public Integer getTotalNum() {
        return totalNum;
    }

    public void setTotalNum(Integer totalNum) {
        this.totalNum = totalNum;
    }
    public Integer getIssueNum() {
        return issueNum;
    }

    public void setIssueNum(Integer issueNum) {
        this.issueNum = issueNum;
    }
    public Integer getUsedNum() {
        return usedNum;
    }

    public void setUsedNum(Integer usedNum) {
        this.usedNum = usedNum;
    }
    public Integer getUserLimit() {
        return userLimit;
    }

    public void setUserLimit(Integer userLimit) {
        this.userLimit = userLimit;
    }
    public String getExtParam() {
        return extParam;
    }

    public void setExtParam(String extParam) {
        this.extParam = extParam;
    }
    public LocalDateTime getCreateTime() {
        return createTime;
    }

    public void setCreateTime(LocalDateTime createTime) {
        this.createTime = createTime;
    }
    public LocalDateTime getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(LocalDateTime updateTime) {
        this.updateTime = updateTime;
    }
    public Long getCreater() {
        return creater;
    }

    public void setCreater(Long creater) {
        this.creater = creater;
    }
    public Long getUpdater() {
        return updater;
    }

    public void setUpdater(Long updater) {
        this.updater = updater;
    }

    @Override
    public String toString() {
        return "Coupon{" +
            "id=" + id +
            ", name=" + name +
            ", type=" + type +
            ", discountType=" + discountType +
            ", specific=" + specific +
            ", discountValue=" + discountValue +
            ", thresholdAmount=" + thresholdAmount +
            ", maxDiscountAmount=" + maxDiscountAmount +
            ", obtainWay=" + obtainWay +
            ", issueBeginTime=" + issueBeginTime +
            ", issueEndTime=" + issueEndTime +
            ", termDays=" + termDays +
            ", termBeginTime=" + termBeginTime +
            ", termEndTime=" + termEndTime +
            ", status=" + status +
            ", totalNum=" + totalNum +
            ", issueNum=" + issueNum +
            ", usedNum=" + usedNum +
            ", userLimit=" + userLimit +
            ", extParam=" + extParam +
            ", createTime=" + createTime +
            ", updateTime=" + updateTime +
            ", creater=" + creater +
            ", updater=" + updater +
        "}";
    }
}
