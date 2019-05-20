package com.baicai.exception;

/**
 * @Description 秒杀业务中异常的基类
 * @Author yuzhou
 * @Date 19-5-17
 */
public class SeckillException extends RuntimeException {

  public SeckillException(String message) {
      super(message);
  }

  public SeckillException(String message, Throwable cause) {
      super(message, cause);
  }
}