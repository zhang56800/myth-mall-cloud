
package com.myth.mall.cloud.entity;

import lombok.Data;

@Data
public class LoginAdminUser {
    private Long adminUserId;

    private String loginUserName;

    private String loginPassword;

    private String nickName;

    private Byte locked;
}