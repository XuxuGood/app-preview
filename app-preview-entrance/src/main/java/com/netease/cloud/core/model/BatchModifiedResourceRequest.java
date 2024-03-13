package com.netease.cloud.core.model;

/**
 * @Author xiaoxuxuy
 * @Date 2024年03月04日
 * @Version: 1.0
 */
public class BatchModifiedResourceRequest {

    private String relativePath;
    private String content;

    public String getRelativePath() {
        return relativePath;
    }

    public void setRelativePath(String relativePath) {
        this.relativePath = relativePath;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

}
