package com.baicai.pojo.dto;

import com.baicai.enums.SeckillStatusEnum;
import com.baicai.pojo.SkRecord;

/**
 * @Description 秒杀结果
 * @Author yuzhou
 * @Date 19-5-17
 */
public class SeckillResult {

    /* 秒杀商品ID */
    private Long seckillId;
    /* 秒杀结果的状态 */
    private Integer state;
    /* 状态结果的说明 */
    private String stateInfo;
    /* 秒杀成功时的秒杀记录 */
    private SkRecord skRecord;

    /* 秒杀成功时，调用的构造 */
    public SeckillResult(Long seckillId, SeckillStatusEnum statusEnum, SkRecord skRecord) {
        this.seckillId = seckillId;
        this.state = statusEnum.getStatus();
        this.stateInfo = statusEnum.getInfo();
        this.skRecord = skRecord;
    }

    /* 秒杀失败时，调用的构造 */
    public SeckillResult(Long seckillId, SeckillStatusEnum statusEnum) {
        this.seckillId = seckillId;
        this.state = statusEnum.getStatus();
        this.stateInfo = statusEnum.getInfo();
    }

    public Long getSeckillId() {
        return seckillId;
    }

    public void setSeckillId(Long seckillId) {
        this.seckillId = seckillId;
    }

    public Integer getState() {
        return state;
    }

    public void setState(Integer state) {
        this.state = state;
    }

    public String getStateInfo() {
        return stateInfo;
    }

    public void setStateInfo(String stateInfo) {
        this.stateInfo = stateInfo;
    }

    public SkRecord getSkRecord() {
        return skRecord;
    }

    public void setSkRecord(SkRecord skRecord) {
        this.skRecord = skRecord;
    }

    @Override
    public String toString() {
        return "SeckillResult{" +
                "seckillId=" + seckillId +
                ", state=" + state +
                ", stateInfo='" + stateInfo + '\'' +
                ", skRecord=" + skRecord +
                '}';
    }
}