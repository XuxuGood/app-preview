package com.netease.cloud.extension.jacoco;


/**
 * jacoco数据持久化和读取
 */
public interface IJacocoDataStorage {

    boolean storage();

    boolean readData();
}
