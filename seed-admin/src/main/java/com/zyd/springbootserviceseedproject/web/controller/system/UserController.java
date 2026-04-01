package com.zyd.springbootserviceseedproject.web.controller.system;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.xxl.job.core.handler.annotation.XxlJob;
import com.zyd.springbootserviceseedproject.common.core.domain.model.LoginUser;
import com.zyd.springbootserviceseedproject.common.core.domain.entity.SysUser;
import com.zyd.springbootserviceseedproject.common.core.redis.RedisCache;
import com.zyd.springbootserviceseedproject.common.core.domain.Result;
import com.zyd.springbootserviceseedproject.framework.web.service.JwtTokenManager;
import com.zyd.springbootserviceseedproject.common.utils.JwtUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Objects;

/**
 * @author zhaoyudong
 * @version 1.0
 * @description 用户Controller（旧版登录接口，兼容保留）
 * @date 2025/9/23 20:18
 */
@Slf4j
@RestController
@RequestMapping("/user")
public class UserController {

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private RedisCache redisCache;

    @Autowired
    private JwtTokenManager jwtTokenManager;

    @Autowired
    private RestTemplate restTemplate;

    @PostMapping("/login")
    public Result login(@RequestBody HashMap<String, String> loginBody) {
        String username = loginBody.get("username");
        String password = loginBody.get("password");
        UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(username, password);
        Authentication authenticate = authenticationManager.authenticate(authenticationToken);
        if (Objects.isNull(authenticate)) {
            throw new RuntimeException("登录失败");
        }
        LoginUser loginUser = (LoginUser) authenticate.getPrincipal();
        SysUser sysUser = loginUser.getUser();
        String userId = sysUser.getUserId().toString();
        String jwtTokenByUserId = jwtTokenManager.getJwtTokenByUserId(userId);
        if (org.springframework.util.StringUtils.hasText(jwtTokenByUserId)) {
            jwtTokenManager.addTokenToBlacklist(jwtTokenByUserId);
        }
        String jwt = JwtUtil.createJWT(userId);
        jwtTokenManager.addJwtTokenByUserId(userId, jwt);
        redisCache.setCacheObject("login:" + userId, loginUser);
        HashMap<String, Object> res = new HashMap<>();
        res.put("user", sysUser);
        res.put("token", jwt);
        return Result.success(res);
    }

    @GetMapping("/test")
    public Result testVisitPermission() throws InterruptedException {
        log.info("用户权限测试");
        restTemplate.postForObject("https://www.baidu.com", JSON.toJSONString(new JSONObject()), String.class);
        Thread.sleep(15000);
        return Result.success("测试请求数据");
    }

    @XxlJob(value = "testXXlJob")
    public void testXXlJob() {
        log.info("测试XxlJob");
    }

    @GetMapping("/test2")
    public Result test2() {
        return Result.success("测试2请求数据");
    }

}
