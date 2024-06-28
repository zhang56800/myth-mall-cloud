package com.myth.mall.cloud.openFeign;

import com.myth.mall.cloud.dto.Result;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

/**
 * {@link  }.
 *
 * @author Jarvis
 * @version v1.0
 * @date 2024/6/27-21:42
 */

@FeignClient(value = "myth-mall-cloud-user-service" , path = "/users")
public interface MythCloudAdminUserServiceFeign {

    @GetMapping(value = "/admin/{token}")
    Result getAdminUserByToken(@PathVariable(value = "token") String token);
}
