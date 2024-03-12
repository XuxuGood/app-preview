package com.netease.cloud.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

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
    private Test1 test1;

    @RequestMapping("/hello")
    public String hello() {
        return "hello world";
    }

    @GetMapping("/hello1")
    public String hello1() {
        return name;
    }

    @GetMapping("/hello2")
    public String hello2() {
        return test1.getName();
    }

}
