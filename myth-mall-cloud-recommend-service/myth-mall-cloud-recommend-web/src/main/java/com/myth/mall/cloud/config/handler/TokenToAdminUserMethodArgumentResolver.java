
package com.myth.mall.cloud.config.handler;

import ltd.common.cloud.newbee.dto.Result;
import ltd.common.cloud.newbee.exception.NewBeeMallException;
import ltd.recommend.cloud.newbee.config.annotation.TokenToAdminUser;
import ltd.recommend.cloud.newbee.entity.LoginAdminUser;
import ltd.user.cloud.newbee.openfeign.NewBeeCloudAdminUserServiceFeign;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.MethodParameter;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

import java.util.LinkedHashMap;

@Component
public class TokenToAdminUserMethodArgumentResolver implements HandlerMethodArgumentResolver {

    @Autowired
    private NewBeeCloudAdminUserServiceFeign newBeeCloudAdminUserService;

    public TokenToAdminUserMethodArgumentResolver() {
    }

    public boolean supportsParameter(MethodParameter parameter) {
        if (parameter.hasParameterAnnotation(TokenToAdminUser.class)) {
            return true;
        }
        return false;
    }

    public Object resolveArgument(MethodParameter parameter, ModelAndViewContainer mavContainer, NativeWebRequest webRequest, WebDataBinderFactory binderFactory) {
        if (parameter.getParameterAnnotation(TokenToAdminUser.class) instanceof TokenToAdminUser) {
            String token = webRequest.getHeader("token");
            if (null != token && !"".equals(token) && token.length() == 32) {
                // 通过用户中心获取用户信息
                Result result = newBeeCloudAdminUserService.getAdminUserByToken(token);

                if (result == null || result.getResultCode() != 200 || result.getData() == null) {
                    NewBeeMallException.fail("ADMIN_NOT_LOGIN_ERROR");
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
                NewBeeMallException.fail("ADMIN_NOT_LOGIN_ERROR");
            }
        }
        return null;
    }

}
