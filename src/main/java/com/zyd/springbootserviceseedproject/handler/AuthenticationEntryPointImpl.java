package com.zyd.springbootserviceseedproject.handler;

import com.alibaba.fastjson.JSON;
import com.zyd.springbootserviceseedproject.common.Result;
import com.zyd.springbootserviceseedproject.utils.WebUtils;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;


/**
 * @author zhaoyudong
 * @version 1.0
 * @description 401 认证失败处理
 * @date 2025/9/24 13:50
 */
@Component
public class AuthenticationEntryPointImpl implements AuthenticationEntryPoint {
    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException) {
        Result result = Result.fail(HttpStatus.UNAUTHORIZED.value(), "用户认证失败请查询登录");
        String json = JSON.toJSONString(result);
        //处理异常
        WebUtils.renderString(response, json);
    }
}
