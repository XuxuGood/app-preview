package com.netease.cloud.model;

import java.io.Serializable;

/**
 * @Author xiaoxuxuy
 * @Date 2024年02月29日
 * @Version: 1.0
 */
public class HotSwapResponse implements Serializable {

    private static final long serialVersionUID = 2023L;

    private boolean success;
    private int code;
    private String message;
    private Object data;


    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    @SuppressWarnings("unchecked")
    public <T> T getResult() {
        return (T) data;
    }

    public void setData(String data) {
        this.data = data;
    }

}
