package com.baicai.exception;

/**
 * @Description 重复秒杀异常
 * @Author yuzhou
 * @Date 19-5-17
 */
public class SeckillRepeatedException extends SeckillException{

 public SeckillRepeatedException(String message) {
     super(message);
 }

 public SeckillRepeatedException(String message, Throwable cause) {
     super(message, cause);
 }
}