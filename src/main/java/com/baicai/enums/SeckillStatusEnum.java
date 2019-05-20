package com.baicai.enums;

/**
 * @Description 秒杀状态枚举
 * @Author yuzhou
 * @Date 19-5-17
 */
public enum SeckillStatusEnum {
    SUCCESS(1, "秒杀成功"),
    END(0, "秒杀结束"),
    REPEAT_KILL(-1, "重复秒杀"),
    INNER_ERROR(-2, "系统异常"),
    DATE_REWRITE(-3, "数据篡改");

    private int status;
    private String info;

    SeckillStatusEnum() {}

    SeckillStatusEnum(int status, String info) {
        this.status = status;
        this.info = info;
    }

    public int getStatus() {
        return status;
    }

    public String getInfo() {
        return info;
    }

    public static String info(int index) {
        for (SeckillStatusEnum statusEnum : values()) {
            if (statusEnum.getStatus() == index) {
                return statusEnum.getInfo();
            }
        }
        return null;
    }
}
