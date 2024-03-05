package com.netease.cloud.hotswap;

/**
 * @Author xiaoxuxuy
 * @Date 2024年02月29日
 * @Version: 1.0
 */
public class BatchModifiedClassRequest {

    private String className;
    private byte[] bytes;

    public String getClassName() {
        return className;
    }

    public void setClassName(String className) {
        this.className = className;
    }

    public byte[] getBytes() {
        return bytes;
    }

    public void setBytes(byte[] bytes) {
        this.bytes = bytes;
    }

}
