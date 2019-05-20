package com.baicai.pojo.dto;

/**
 * @Description 通用返回类（方便返回JSON格式）
 * @Author yuzhou
 * @Date 19-5-20
 */
public class CommonResult<T> {

    private boolean success;

    private T data;

    private String error;

    public CommonResult() {
    }

    public CommonResult(boolean success, T data) {
        this.success = success;
        this.data = data;
    }

    public CommonResult(boolean success, String error) {
        this.success = success;
        this.error = error;
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }

    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }

    @Override
    public String toString() {
        return "CommonResult{" +
                "success=" + success +
                ", data=" + data +
                ", error='" + error + '\'' +
                '}';
    }
}
