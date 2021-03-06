package org.vuffy.o2o.dto;

/**
 * @author vuffy
 * @version 1.0
 * @description: TODO
 * @date 2021/6/12 3:28 下午
 */
public class Result<T> {
    // 是否成功的标志
    private boolean success;
    // 成功时返回的数据
    private T data;
    // 错误信息
    private String errorMsg;

    private int errorCode;

    public Result() {
    }

    //成功时的构造器
    public Result(boolean success, T data) {
        this.success = success;
        this.data = data;
    }

    //错误时的构造器
    public Result(boolean success, int errorCode, String errorMsg) {
        this.success = success;
        this.errorCode = errorCode;
        this.errorMsg = errorMsg;
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

    public String getErrorMsg() {
        return errorMsg;
    }

    public void setErrorMsg(String errorMsg) {
        this.errorMsg = errorMsg;
    }

    public int getErrorCode() {
        return errorCode;
    }

    public void setErrorCode(int errorCode) {
        this.errorCode = errorCode;
    }
}
