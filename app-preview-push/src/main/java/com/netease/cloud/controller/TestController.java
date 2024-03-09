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

    @Autowired
    private Test test;

    @RequestMapping("/hello")
    public String hello() {
        return test.test();
//        return "hello world";
    }

    @RequestMapping("/hello1")
    public String hello1() {
        return name;
    }

    @RequestMapping("/hello2")
    public String hello2() {
        return name;
    }

    @RequestMapping("/hello3")
    public String hello3() {
        return name;
    }

    @RequestMapping("/hello4")
    public String hello4() {
        return name;
    }
}
