package com.baicai.pojo;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * @Description 秒杀记录表
 * @Author yuzhou
 * @Date 19-5-17
 */
public class SkRecord implements Serializable {

    private static final long serialVersionUID = -6921147668978649157L;
    /* 秒杀商品主键 */
    private long skproductId;
    /* 用户手机号 */
    private long userPhone;
    /* 秒杀状态：-1无效 0成功 1已付款 */
    private short state;
    /* 创建时间*/
    private LocalDateTime createTime;
    /* 秒杀的商品 */
    private SkProduct skProduct;

    public SkRecord() {}

    public SkRecord(long skproductId, long userPhone, short state, LocalDateTime createTime, SkProduct skProduct) {
        this.skproductId = skproductId;
        this.userPhone = userPhone;
        this.state = state;
        this.createTime = createTime;
        this.skProduct = skProduct;
    }

    public long getSkproductId() {
        return skproductId;
    }

    public void setSkproductId(long skproductId) {
        this.skproductId = skproductId;
    }

    public long getUserPhone() {
        return userPhone;
    }

    public void setUserPhone(long userPhone) {
        this.userPhone = userPhone;
    }

    public short getState() {
        return state;
    }

    public void setState(short state) {
        this.state = state;
    }

    public LocalDateTime getCreateTime() {
        return createTime;
    }

    public void setCreateTime(LocalDateTime createTime) {
        this.createTime = createTime;
    }

    public SkProduct getSkProduct() {
        return skProduct;
    }

    public void setSkProduct(SkProduct skProduct) {
        this.skProduct = skProduct;
    }

    @Override
    public String toString() {
        return "SkRecord{" +
                "skproductId=" + skproductId +
                ", userPhone=" + userPhone +
                ", state=" + state +
                ", createTime=" + createTime +
                ", skProduct=" + skProduct +
                '}';
    }
}