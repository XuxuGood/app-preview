package com.netease.cloud.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * @Author xiaoxuxuy
 * @Date 2024年03月19日
 * @Version: 1.0
 */
@Component
public class Test3 {
    @Autowired
    private Test4 test4;
    public String test3(){
        System.out.println("test3");
        System.out.println(test4.test());
        return "asdasdasda";
    }

}
