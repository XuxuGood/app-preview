package com.netease.cloud;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@MapperScan(basePackages = "com.netease.cloud.dao")
@SpringBootApplication(scanBasePackages = "com.netease.cloud")
public class MavenApplication {

    public static void main(String[] args) {
        SpringApplication.run(MavenApplication.class, args);
    }

}
