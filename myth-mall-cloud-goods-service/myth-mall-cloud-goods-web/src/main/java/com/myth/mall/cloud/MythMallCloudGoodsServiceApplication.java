package com.myth.mall.cloud;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@SpringBootApplication
@EnableDiscoveryClient
@MapperScan("com.myth.mall.cloud.dao")
public class MythMallCloudGoodsServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(MythMallCloudGoodsServiceApplication.class, args);
    }

}
