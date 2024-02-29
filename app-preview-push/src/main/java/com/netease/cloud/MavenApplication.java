package com.netease.cloud;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages = "com.netease.cloud")
public class MavenApplication {

    public static void main(String[] args) {
        SpringApplication.run(MavenApplication.class, args);
    }

}
