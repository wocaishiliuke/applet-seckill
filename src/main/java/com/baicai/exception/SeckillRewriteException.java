package com.baicai.exception;

/**
 * @Description 秒杀被重写异常
 * @Author yuzhou
 * @Date 19-5-21
 */
public class SeckillRewriteException extends SeckillException {

    public SeckillRewriteException(String message) {
        super(message);
    }

    public SeckillRewriteException(String message, Throwable cause) {
        super(message, cause);
    }
}
