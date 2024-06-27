/**
 * 严肃声明：
 * 开源版本请务必保留此注释头信息，若删除我方将保留所有法律责任追究！
 * 本软件已申请软件著作权，受国家版权局知识产权以及国家计算机软件著作权保护！
 * 可正常分享和学习源码，不得用于违法犯罪活动，违者必究！
 * Copyright (c) 2022 程序员十三 all rights reserved.
 * 版权所有，侵权必究！
 */
package com.myth.mall.cloud.config.handler;

import com.myth.mall.cloud.config.annotation.TokenToAdminUser;
import com.myth.mall.cloud.dao.MythAdminUserTokenMapper;
import com.myth.mall.cloud.entity.AdminUserToken;
import com.myth.mall.cloud.exception.MythMallException;
import org.springframework.core.MethodParameter;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;
import javax.annotation.Resource;

@Component
public class TokenToAdminUserMethodArgumentResolver implements HandlerMethodArgumentResolver {


    @Resource
    private  MythAdminUserTokenMapper mythAdminUserTokenMapper;

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
                AdminUserToken adminUserToken = mythAdminUserTokenMapper.selectByToken(token);
                if (adminUserToken == null) {
                    MythMallException.fail("ADMIN_NOT_LOGIN_ERROR");
                } else if (adminUserToken.getExpireTime().getTime() <= System.currentTimeMillis()) {
                    MythMallException.fail("ADMIN_TOKEN_EXPIRE_ERROR");
                }
                return adminUserToken;
            } else {
                MythMallException.fail("ADMIN_NOT_LOGIN_ERROR");
            }
        }
        return null;
    }

}
