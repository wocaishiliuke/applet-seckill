package com.baicai.exception;

/**
 * @Description 秒杀已关闭异常
 * @Author yuzhou
 * @Date 19-5-17
 */
public class SeckillClosedException extends SeckillException {

    public SeckillClosedException(String message) {
        super(message);
    }

    public SeckillClosedException(String message, Throwable cause) {
        super(message, cause);
    }
}