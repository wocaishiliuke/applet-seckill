package com.baicai.pojo;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * @Description 秒杀记录表
 * @Author yuzhou
 * @Date 19-5-17
 */
public class SuccessKilled implements Serializable {

    private static final long serialVersionUID = -6921147668978649157L;
    /* 秒杀记录主键 */
    private long seckillId;
    /* 用户手机号 */
    private long userPhone;
    /* 秒杀状态：-1无效 0成功 1已付款 */
    private short state;
    /* 创建时间*/
    private LocalDateTime createTime;
    /* 秒杀的商品 */
    private Seckill seckill;

    public SuccessKilled() {}

    public SuccessKilled(long seckillId, long userPhone, short state, LocalDateTime createTime, Seckill seckill) {
        this.seckillId = seckillId;
        this.userPhone = userPhone;
        this.state = state;
        this.createTime = createTime;
        this.seckill = seckill;
    }

    public long getSeckillId() {
        return seckillId;
    }

    public void setSeckillId(long seckillId) {
        this.seckillId = seckillId;
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

    public Seckill getSeckill() {
        return seckill;
    }

    public void setSeckill(Seckill seckill) {
        this.seckill = seckill;
    }

    @Override
    public String toString() {
        return "SuccessKilled{" +
                "seckillId=" + seckillId +
                ", userPhone=" + userPhone +
                ", state=" + state +
                ", createTime=" + createTime +
                ", seckill=" + seckill +
                '}';
    }
}