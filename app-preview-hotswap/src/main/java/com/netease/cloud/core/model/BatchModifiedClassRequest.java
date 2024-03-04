package com.netease.cloud.core.model;

import java.io.Serializable;

/**
 * @Author xiaoxuxuy
 * @Date 2024年02月29日
 * @Version: 1.0
 */
public class BatchModifiedClassRequest implements Serializable {

    private static final long serialVersionUID = 2024L;

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
