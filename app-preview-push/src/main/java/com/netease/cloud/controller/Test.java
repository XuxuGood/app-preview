package com.netease.cloud.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * @Author xiaoxuxuy
 * @Date 2024年03月05日
 * @Version: 1.0
 */
@Component
public class Test {

    @Value(value = "${hotswap.name}")
    private String name;

    public String test() {
        return name;
    }

}
