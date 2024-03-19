package com.netease.cloud.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;

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

    @Value(value = "${hotswap.name1}")
    private String name1;

    @Autowired
    private Test1 test1;

    @RequestMapping("/hello")
    public String hello() {
        return "hello world";
    }

    @GetMapping("/hello1")
    public String hello1() {
        return name1;
    }

    @GetMapping("/hello2")
    public String hello2() {
        return test1.getName();
    }

    @GetMapping("/hello3")
    public String hello3() {
        return test1.getName();
    }


    @GetMapping("/hello4/{id}")
    public String hello4(@PathVariable String id) {
        return id;
    }

    @GetMapping("/hello5")
    public String hello5() {
        return "id";
    }

    @GetMapping("/hello6")
    public String hello5(@RequestParam String id) {
        return id;
    }

}
