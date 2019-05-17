package com.baicai.pojo;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * @Description 秒杀商品（库存）表
 * @Author yuzhou
 * @Date 19-5-17
 */
public class Seckill implements Serializable {

    private static final long serialVersionUID = 2912164127598660137L;
    /* 秒杀商品主键*/
    private long seckillId;
    /* 秒杀商品名 */
    private String name;
    /* 秒杀商品编号 */
    private int number;
    /* 秒杀开始时间 */
    private LocalDateTime startTime;
    /* 秒杀结束时间 */
    private LocalDateTime endTime;
    /* 创建时间 */
    private LocalDateTime createTIme;

    public Seckill() {
    }

    public Seckill(long seckillId, String name, int number, LocalDateTime startTime, LocalDateTime endTime, LocalDateTime createTIme) {
        this.seckillId = seckillId;
        this.name = name;
        this.number = number;
        this.startTime = startTime;
        this.endTime = endTime;
        this.createTIme = createTIme;
    }

    public long getSeckillId() {
        return seckillId;
    }

    public void setSeckillId(long seckillId) {
        this.seckillId = seckillId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getNumber() {
        return number;
    }

    public void setNumber(int number) {
        this.number = number;
    }

    public LocalDateTime getStartTime() {
        return startTime;
    }

    public void setStartTime(LocalDateTime startTime) {
        this.startTime = startTime;
    }

    public LocalDateTime getEndTime() {
        return endTime;
    }

    public void setEndTime(LocalDateTime endTime) {
        this.endTime = endTime;
    }

    public LocalDateTime getCreateTIme() {
        return createTIme;
    }

    public void setCreateTIme(LocalDateTime createTIme) {
        this.createTIme = createTIme;
    }

    @Override
    public String toString() {
        return "Seckill{" +
                "seckillId=" + seckillId +
                ", name='" + name + '\'' +
                ", number=" + number +
                ", startTime=" + startTime +
                ", endTime=" + endTime +
                ", createTIme=" + createTIme +
                '}';
    }
}