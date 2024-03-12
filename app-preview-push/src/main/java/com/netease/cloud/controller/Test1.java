package com.netease.cloud.controller;

import org.springframework.context.annotation.Configuration;

/**
 * @Author xiaoxuxuy
 * @Date 2024年03月12日
 * @Version: 1.0
 */
public class Test1 {

    private String name;

    public Test1() {
    }

    public String getName() {
        return name;
    }

    Test1(String name) {
        this.name = name;
    }

}
