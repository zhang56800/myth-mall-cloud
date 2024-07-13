
package com.myth.mall.cloud;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;

/**
 * {@link  }.
 *
 * @author Jarvis
 * @version v1.0
 *
 */

@SpringBootApplication
@EnableDiscoveryClient
@MapperScan("ltd.recommend.cloud.newbee.dao")
@EnableFeignClients(basePackageClasses =
        {com.myth.mall.cloud.openFeign.MythCloudAdminUserServiceFeign .class})
public class MythMallCloudRecommendServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(MythMallCloudRecommendServiceApplication.class, args);
    }

}
