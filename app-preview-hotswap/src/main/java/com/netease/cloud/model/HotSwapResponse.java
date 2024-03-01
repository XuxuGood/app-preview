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
    private String msg;
    private Object data;

    public HotSwapResponse(boolean success, Integer code, String msg, Object data) {
        this.success = success;
        this.code = code;
        this.msg = msg;
        this.data = data;
    }

    public static HotSwapResponse success(Object data) {
        return new HotSwapResponse(true, 200, "success", data);
    }

    public static HotSwapResponse errorOf(String msg, Object data) {
        return new HotSwapResponse(false, 400, msg, data);
    }

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

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    @SuppressWarnings("unchecked")
    public <T> T getData() {
        return (T) data;
    }

    public void setData(String data) {
        this.data = data;
    }

}
