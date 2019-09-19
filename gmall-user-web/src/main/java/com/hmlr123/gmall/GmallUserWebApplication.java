package com.hmlr123.gmall;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication
@EnableAsync//启动异步任务
public class GmallUserWebApplication {

    public static void main(String[] args) {
        SpringApplication.run(GmallUserWebApplication.class, args);
    }

}
