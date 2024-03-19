package com.netease.cloud.controller;

import com.netease.cloud.dao.XuxuMapper;
import com.netease.cloud.model.Xuxu;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @Author xiaoxuxuy
 * @Date 2024年03月19日
 * @Version: 1.0
 */
@Service
public class XuxuService {
    @Autowired
    private XuxuMapper xuxuMapper;
    @Autowired
    private Test4 test4;
    @Autowired
    private Test3 test3;

    public List<Xuxu> getXuxu() {
        return xuxuMapper.allUserList();
    }

}
