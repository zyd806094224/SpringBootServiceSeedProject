package com.zyd.springbootserviceseedproject.handler;

import com.alibaba.fastjson.JSON;
import com.zyd.springbootserviceseedproject.common.Result;
import com.zyd.springbootserviceseedproject.utils.WebUtils;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;

/**
 * @author zhaoyudong
 * @version 1.0
 * @description 403 权限不足
 * @date 2025/9/24 13:52
 */
@Component
public class AccessDeniedHandlerImpl implements AccessDeniedHandler {
    @Override
    public void handle(HttpServletRequest request, HttpServletResponse response, AccessDeniedException accessDeniedException) {
        Result result = Result.fail(HttpStatus.FORBIDDEN.value(), "您的权限不足，无法访问 " + request.getRequestURI());
        String json = JSON.toJSONString(result);
        //处理异常
        WebUtils.renderString(response, json);
    }
}
