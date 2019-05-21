package com.baicai.enums;

/**
 * @Description 秒杀状态枚举
 * @Author yuzhou
 * @Date 19-5-17
 */
public enum SeckillStatusEnum {

    // status最好和存储过程的返回一致
    NO_PHONE(-4, "缺少手机号"),
    DATE_REWRITE(-3, "数据篡改"),
    INNER_ERROR(-2, "系统异常"),
    REPEAT_KILL(-1, "重复秒杀"),
    CLOSED(0, "秒杀结束"),
    SUCCESS(1, "秒杀成功");

    private Integer status;
    private String info;

    SeckillStatusEnum() {}

    SeckillStatusEnum(Integer status, String info) {
        this.status = status;
        this.info = info;
    }

    public Integer getStatus() {
        return status;
    }

    public String getInfo() {
        return info;
    }

    public static String info(Integer index) {
        for (SeckillStatusEnum statusEnum : values()) {
            if (statusEnum.getStatus() == index) {
                return statusEnum.getInfo();
            }
        }
        return null;
    }
}
