package com.netease.cloud.extension;

/**
 * @Author xiaoxuxuy
 * @Date 2024年02月29日
 * @Version: 1.0
 */
public interface IHotExtHandler {

    /**
     * before you hotswap file
     *
     * @param classLoader
     * @param path
     * @param content
     * @return your modified bytes
     */
    default byte[] preHandle(ClassLoader classLoader, String path, byte[] content) {
        return content;
    }

    /**
     * after you hotswap file
     *
     * @param classLoader
     * @param classz      : if file is not java class ,classz is null
     * @param path        : upload to path
     * @param content
     */
    void afterHandle(ClassLoader classLoader, Class<?> classz, String path, byte[] content);

}
