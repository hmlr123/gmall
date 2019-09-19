package com.hmlr123.gmall;

import com.alibaba.dubbo.config.spring.context.annotation.EnableDubbo;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;
import tk.mybatis.spring.annotation.MapperScan;

@SpringBootApplication
@MapperScan(basePackages = "com.hmlr123.gmall.search.mapper")
@EnableAsync//启动异步任务
@EnableDubbo//开启dubbo注解
public class GmallSearchServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(GmallSearchServiceApplication.class, args);
    }

}
