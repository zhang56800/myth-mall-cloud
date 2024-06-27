package com.myth.mall.cloud.controller;

import com.myth.mall.cloud.config.annotation.TokenToAdminUser;
import com.myth.mall.cloud.controller.param.AdminLoginParam;
import com.myth.mall.cloud.controller.param.UpdateAdminNameParam;
import com.myth.mall.cloud.controller.param.UpdateAdminPasswordParam;
import com.myth.mall.cloud.dto.Result;
import com.myth.mall.cloud.dto.ResultGenerator;
import com.myth.mall.cloud.entity.AdminUser;
import com.myth.mall.cloud.entity.AdminUserToken;
import com.myth.mall.cloud.service.AdminUserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
public class MythMallCloudAdminUserController {

    private static final Logger logger = LoggerFactory.getLogger(MythMallCloudAdminUserController.class);

    @GetMapping("/users/admin/test/{userId}")
    public String test(@PathVariable("userId") int userId) {
        String userName = "user:" + userId;
        // 返回信息给调用端
        return userName;
    }

    @Autowired
    private AdminUserService adminUserService;

    @RequestMapping(value = "/adminUser/login",method = RequestMethod.POST)
    public Result<String> login(@RequestBody@Valid AdminLoginParam adminLoginParam){
        String userName = adminLoginParam.getUserName();
        String passwordMd5 = adminLoginParam.getPasswordMd5();
        String loginResult = adminUserService.login(userName, passwordMd5);
        logger.info("manage login api,adminName={},loginResult={}", adminLoginParam.getUserName(), loginResult);
        //登录成功
        if (!StringUtils.isEmpty(loginResult) && loginResult.length() == 32) {
            Result result = ResultGenerator.genSuccessResult();
            result.setData(loginResult);
            return result;
        }
        //登录失败
        return ResultGenerator.genFailResult(loginResult);

    }


    @RequestMapping(value = "/adminUser/profile", method = RequestMethod.POST)
    public Result profile(@TokenToAdminUser AdminUserToken adminUser) {
        logger.info("adminUser:{}", adminUser.toString());
        AdminUser adminUserEntity = adminUserService.getUserDetailById(adminUser.getAdminUserId());
        if (adminUserEntity != null) {
            adminUserEntity.setLoginPassword("******");
            Result result = ResultGenerator.genSuccessResult();
            result.setData(adminUserEntity);
            return result;
        }
        return ResultGenerator.genFailResult("无此用户数据");
    }

    @RequestMapping(value = "/adminUser/password", method = RequestMethod.PUT)
    public Result passwordUpdate(@RequestBody @Valid UpdateAdminPasswordParam adminPasswordParam, @TokenToAdminUser AdminUserToken adminUser) {
        logger.info("adminUser:{}", adminUser.toString());
        if (adminUserService.updatePassword(adminUser.getAdminUserId(), adminPasswordParam.getOriginalPassword(), adminPasswordParam.getNewPassword())) {
            return ResultGenerator.genSuccessResult();
        } else {
            return ResultGenerator.genFailResult("DB ERROR");
        }
    }

    @RequestMapping(value = "/adminUser/name", method = RequestMethod.PUT)
    public Result nameUpdate(@RequestBody @Valid UpdateAdminNameParam adminNameParam, @TokenToAdminUser AdminUserToken adminUser) {
        logger.info("adminUser:{}", adminUser.toString());
        if (adminUserService.updateName(adminUser.getAdminUserId(), adminNameParam.getLoginUserName(), adminNameParam.getNickName())) {
            return ResultGenerator.genSuccessResult();
        } else {
            return ResultGenerator.genFailResult("DB ERROR");
        }
    }

    @RequestMapping(value = "/logout", method = RequestMethod.DELETE)
    public Result logout(@TokenToAdminUser AdminUserToken adminUser) {
        logger.info("adminUser:{}", adminUser.toString());
        adminUserService.logout( adminUser.getToken());
        return ResultGenerator.genSuccessResult();
    }
}
