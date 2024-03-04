package com.netease.cloud.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @Author xiaoxuxuy
 * @Date 2024年02月26日
 * @Version: 1.0.0
 */
@RestController
public class TestController {

    @Value(value = "${hotswap.name}")
    private String name;

    @RequestMapping("/hello")
    public String hello() {
        return "hello world";
    }

    @RequestMapping("/hello1")
    public String hello1() {
        return name;
    }

}
