package com.baicai.exception;

/**
 * @Description 秒杀无手机号异常
 * @Author yuzhou
 * @Date 19-5-21
 */
public class SeckillNoPhoneException extends SeckillException {

    public SeckillNoPhoneException(String message) {
        super(message);
    }

    public SeckillNoPhoneException(String message, Throwable cause) {
        super(message, cause);
    }
}
