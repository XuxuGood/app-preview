package com.netease.cloud.service;

import com.netease.cloud.model.BatchModifiedRequest;
import com.netease.cloud.model.HotSwapResponse;

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
    String uploadResourceFile(String path, String content) throws RemoteException;


    /**
     * 批量热更新修改的java
     *
     * @param requestList
     * @return
     * @throws RemoteException
     */
    HotSwapResponse batchHotswapModifiedJava(List<BatchModifiedRequest> requestList) throws RemoteException;

}
