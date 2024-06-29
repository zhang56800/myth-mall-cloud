package com.myth.mall.cloud.config.handler;

import com.myth.mall.cloud.config.annotation.TokenAdminUser;
import com.myth.mall.cloud.dto.Result;
import com.myth.mall.cloud.entity.LoginAdminUser;
import com.myth.mall.cloud.exception.MythMallException;
import com.myth.mall.cloud.openFeign.MythCloudAdminUserServiceFeign;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.MethodParameter;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

import javax.annotation.Resource;
import java.util.LinkedHashMap;

/**
 * {@link TokenToAdminUserMethodArgumentResolver }.
 *
 * @author Jarvis
 * @version v1.0
 * @date 2024/6/29-15:23
 */


@Component
public class TokenToAdminUserMethodArgumentResolver implements HandlerMethodArgumentResolver {

    @Autowired
    MythCloudAdminUserServiceFeign mythCloudAdminUserService;


    @Override
    public boolean supportsParameter(MethodParameter parameter) {
        if (parameter.hasParameterAnnotation(TokenAdminUser.class)){
            return true;
        }
        return false;
    }

    @Override
    public Object resolveArgument(MethodParameter parameter, ModelAndViewContainer mavContainer, NativeWebRequest webRequest, WebDataBinderFactory binderFactory) throws Exception {
        if (parameter.getParameterAnnotation(TokenAdminUser.class) instanceof TokenAdminUser){
            String token = webRequest.getHeader("token");
            if (token != null && !"".equals(token) && token.length() == 32){
                Result adminUserByToken = mythCloudAdminUserService.getAdminUserByToken(token);
                // 通过用户中心获取用户信息
                Result result = mythCloudAdminUserService.getAdminUserByToken(token);

                if (result == null || result.getResultCode() != 200 || result.getData() == null) {
                    MythMallException.fail("ADMIN_NOT_LOGIN_ERROR");
                }

                LinkedHashMap resultData = (LinkedHashMap) result.getData();

                // 将返回的字段封装到LoginAdminUser对象中
                LoginAdminUser loginAdminUser = new LoginAdminUser();
                loginAdminUser.setAdminUserId(Long.valueOf(resultData.get("adminUserId").toString()));
                loginAdminUser.setLoginUserName((String) resultData.get("loginUserName"));
                loginAdminUser.setNickName((String) resultData.get("nickName"));
                loginAdminUser.setLocked(Byte.valueOf(resultData.get("locked").toString()));
                return loginAdminUser;
            } else {
                MythMallException.fail("ADMIN_NOT_LOGIN_ERROR");
            }

         }
        return null;
    }

}
