package com.netease.cloud.core.service;

import com.netease.cloud.core.model.BatchModifiedClassRequest;
import com.netease.cloud.core.model.HotSwapResponse;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.List;

/**
 * @Author xiaoxuxuy
 * @Date 2024年02月29日
 * @Version: 1.0
 */
public interface IHotDeployService extends Remote {

    /**
     * 上传资源文件
     *
     * @param path
     * @param content
     * @return
     * @throws RemoteException
     */
    HotSwapResponse uploadResourceFile(String path, String content) throws RemoteException;

    /**
     * 批量热更新修改的java
     * 批量热更新不支持前置与后置处理
     *
     * @param requestList
     * @return
     * @throws RemoteException
     */
    HotSwapResponse batchHotswapModifiedJava(List<BatchModifiedClassRequest> requestList) throws RemoteException;

}
