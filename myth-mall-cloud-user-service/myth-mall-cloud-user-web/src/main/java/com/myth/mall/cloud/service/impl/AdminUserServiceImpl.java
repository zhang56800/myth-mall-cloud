
package com.myth.mall.cloud.service.impl;

import com.myth.mall.cloud.dao.AdminUserMapper;
import com.myth.mall.cloud.entity.AdminUser;
import com.myth.mall.cloud.pojo.AdminUserToken;
import com.myth.mall.cloud.service.AdminUserService;
import com.myth.mall.cloud.until.NumberUtil;
import com.myth.mall.cloud.until.SystemUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.concurrent.TimeUnit;

@Service
public class AdminUserServiceImpl implements AdminUserService {

    @Resource
    private AdminUserMapper adminUserMapper;

    @Autowired
    private RedisTemplate redisTemplate;

    @Override
    public String login(String userName, String password) {
        AdminUser loginAdminUser = adminUserMapper.login(userName, password);
        if (loginAdminUser != null) {
            //登录后即执行修改token的操作
            String token = getNewToken(System.currentTimeMillis() + "", loginAdminUser.getAdminUserId());

            AdminUserToken adminUserToken = new AdminUserToken();
            adminUserToken.setAdminUserId(loginAdminUser.getAdminUserId());
            adminUserToken.setToken(token);

            ValueOperations<String,AdminUserToken> setToken = redisTemplate.opsForValue();
            setToken.set(token,adminUserToken,2*24*60*60, TimeUnit.SECONDS);

            return token;

        }
        return "登录失败";
    }


    /**
     * 获取token值
     *
     * @param timeStr
     * @param userId
     * @return
     */
    private String getNewToken(String timeStr, Long userId) {
        String src = timeStr + userId + NumberUtil.genRandomNum(6);
        return SystemUtil.genToken(src);
    }


    @Override
    public AdminUser getUserDetailById(Long loginUserId) {
        return adminUserMapper.selectByPrimaryKey(loginUserId);
    }

    @Override
    public Boolean updatePassword(Long loginUserId, String originalPassword, String newPassword) {
        AdminUser adminUser = adminUserMapper.selectByPrimaryKey(loginUserId);
        //当前用户非空才可以进行更改
        if (adminUser != null) {
            //比较原密码是否正确
            if (originalPassword.equals(adminUser.getLoginPassword())) {
                //设置新密码并修改
                adminUser.setLoginPassword(newPassword);
                if (adminUserMapper.updateByPrimaryKeySelective(adminUser) > 0 ) {
                    //修改成功且清空当前token则返回true
                    //redisTemplate.delete(token);
                    return true;
                }
            }
        }
        return false;
    }

    @Override
    public Boolean updateName(Long loginUserId, String loginUserName, String nickName) {
        AdminUser adminUser = adminUserMapper.selectByPrimaryKey(loginUserId);
        //当前用户非空才可以进行更改
        if (adminUser != null) {
            //设置新名称并修改
            adminUser.setLoginUserName(loginUserName);
            adminUser.setNickName(nickName);
            if (adminUserMapper.updateByPrimaryKeySelective(adminUser) > 0) {
                //修改成功则返回true
                return true;
            }
        }
        return false;
    }

    @Override
    public Boolean logout(String token) {
        return redisTemplate.delete(token);
    }

    @Override
    public AdminUser getUserDetailByToken(String token) {
        ValueOperations<String, AdminUserToken> opsForAdminUserToken = redisTemplate.opsForValue();
        AdminUserToken adminUserToken = opsForAdminUserToken.get(token);
        if (adminUserToken != null) {
            return adminUserMapper.selectByPrimaryKey(adminUserToken.getAdminUserId());
        }
        return null;
    }
}
