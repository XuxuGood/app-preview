package com.netease.cloud.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

/**
 * @Author xiaoxuxuy
 * @Date 2024年03月05日
 * @Version: 1.0
 */
@Configuration
public class Test {

    @Value(value = "${hotswap.name}")
    private String name;

    @Bean
    public Test1 test1() {
        return new Test1(name);
    }

}
