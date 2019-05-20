package com.baicai.pojo.dto;

import java.time.LocalDateTime;

/**
 * 秒杀开启前后的暴露信息
 * @Description
 * @Author yuzhou
 * @Date 19-5-17
 */
public class ExposeResult {

    /* 秒杀商品id */
    private long skproductId;
    /* 是否开启秒杀 */
    private boolean exposed;
    /* 对秒杀地址进行加密措施 */
    private String md5;
    /* 当前时间 */
    private LocalDateTime now;
    /* 秒杀开启时间 */
    private LocalDateTime start;
    /* 秒杀结束时间 */
    private LocalDateTime end;

    public ExposeResult() {
    }

    public ExposeResult(boolean exposed, long skproductId) {
        this.exposed = exposed;
        this.skproductId = skproductId;
    }

    public ExposeResult(boolean exposed, String md5, long skproductId) {
        this.exposed = exposed;
        this.md5 = md5;
        this.skproductId = skproductId;
    }

    public ExposeResult(boolean exposed, long skproductId, LocalDateTime now, LocalDateTime start, LocalDateTime end) {
        this.exposed = exposed;
        this.skproductId = skproductId;
        this.now = now;
        this.start = start;
        this.end = end;
    }

    public boolean isExposed() {
        return exposed;
    }

    public void setExposed(boolean exposed) {
        this.exposed = exposed;
    }

    public String getMd5() {
        return md5;
    }

    public void setMd5(String md5) {
        this.md5 = md5;
    }

    public long getSkproductId() {
        return skproductId;
    }

    public void setSkproductId(long skproductId) {
        this.skproductId = skproductId;
    }

    public LocalDateTime getNow() {
        return now;
    }

    public void setNow(LocalDateTime now) {
        this.now = now;
    }

    public LocalDateTime getStart() {
        return start;
    }

    public void setStart(LocalDateTime start) {
        this.start = start;
    }

    public LocalDateTime getEnd() {
        return end;
    }

    public void setEnd(LocalDateTime end) {
        this.end = end;
    }

    @Override
    public String toString() {
        return "ExposeResult{" +
                "exposed=" + exposed +
                ", md5='" + md5 + '\'' +
                ", skproductId=" + skproductId +
                ", now=" + now +
                ", start=" + start +
                ", end=" + end +
                '}';
    }
}