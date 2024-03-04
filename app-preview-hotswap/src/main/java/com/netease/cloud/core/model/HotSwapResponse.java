package com.netease.cloud.core.model;

import java.io.Serializable;

/**
 * @Author xiaoxuxuy
 * @Date 2024年02月29日
 * @Version: 1.0
 */
public class HotSwapResponse {

    private int code;
    private String msg;
    private Object result;

    public HotSwapResponse(int code, String msg, Object result) {
        this.code = code;
        this.msg = msg;
        this.result = result;
    }

    public static HotSwapResponse of(Object result, int code, String msg) {
        return new HotSwapResponse( code, msg, result);
    }

    public static HotSwapResponse success(Object data) {
        return new HotSwapResponse(200, "success", data);
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

    public Object getResult() {
        return result;
    }

    public void setResult(Object result) {
        this.result = result;
    }

}
