
package com.myth.mall.cloud.config;


import com.myth.mall.cloud.dto.Result;
import com.myth.mall.cloud.exception.MythMallException;
import org.springframework.validation.BindException;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import javax.servlet.http.HttpServletRequest;
import java.util.Objects;

/**
 * 全局异常处理
 */
@RestControllerAdvice
public class GoodsServiceExceptionHandler {

    @ExceptionHandler(BindException.class)
    public Object bindException(BindException e) {
        Result result = new Result();
        result.setResultCode(510);
        BindingResult bindingResult = e.getBindingResult();
        result.setMessage(Objects.requireNonNull(bindingResult.getFieldError()).getDefaultMessage());
        return result;
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public Object bindException(MethodArgumentNotValidException e) {
        Result result = new Result();
        result.setResultCode(510);
        BindingResult bindingResult = e.getBindingResult();
        result.setMessage(Objects.requireNonNull(bindingResult.getFieldError()).getDefaultMessage());
        return result;
    }

    @ExceptionHandler(Exception.class)
    public Object handleException(Exception e, HttpServletRequest req) {
        Result result = new Result();
        result.setResultCode(500);
        //区分是否为自定义异常
        if (e instanceof MythMallException) {
            result.setMessage(e.getMessage());
            if (e.getMessage().equals("ADMIN_NOT_LOGIN_ERROR") || e.getMessage().equals("ADMIN_TOKEN_EXPIRE_ERROR")) {
                result.setResultCode(419);
            }
        } else {
            e.printStackTrace();
            result.setMessage("未知异常，请查看控制台日志并检查配置文件。");
        }
        return result;

    }
}
