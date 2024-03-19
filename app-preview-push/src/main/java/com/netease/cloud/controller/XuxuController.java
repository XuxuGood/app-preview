package com.netease.cloud.controller;

import com.netease.cloud.model.Xuxu;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * @Author xiaoxuxuy
 * @Date 2024年03月19日
 * @Version: 1.0
 */
@RestController
public class XuxuController {

    @Autowired
    private XuxuService xuxuService;

    @GetMapping("/xuxu")
    public List<Xuxu> getXuxu(){
        return xuxuService.getXuxu();
    }
}
