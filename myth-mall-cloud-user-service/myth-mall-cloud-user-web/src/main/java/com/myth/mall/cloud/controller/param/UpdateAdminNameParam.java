
package com.myth.mall.cloud.controller.param;

import lombok.Data;

import javax.validation.constraints.NotEmpty;

@Data
public class UpdateAdminNameParam {

    @NotEmpty(message = "loginUserName不能为空")
    private String loginUserName;

    @NotEmpty(message = "nickName不能为空")
    private String nickName;
}
