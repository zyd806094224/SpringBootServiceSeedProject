package com.zyd.springbootserviceseedproject.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.xxl.job.core.handler.annotation.XxlJob;
import com.zyd.springbootserviceseedproject.bean.LoginUser;
import com.zyd.springbootserviceseedproject.cache.RedisCache;
import com.zyd.springbootserviceseedproject.common.Result;
import com.zyd.springbootserviceseedproject.entity.UserEntity;
import com.zyd.springbootserviceseedproject.manager.JwtTokenManager;
import com.zyd.springbootserviceseedproject.service.UserService;
import com.zyd.springbootserviceseedproject.utils.JwtUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.List;
import java.util.Objects;

/**
 * @author zhaoyudong
 * @version 1.0
 * @description 用户Controller
 * @date 2025/9/23 20:18
 */
@Slf4j
@RestController
@RequestMapping("/user")
public class UserController {

    @Autowired
    private UserService userService;

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private RedisCache redisCache;

    @Autowired
    private JwtTokenManager jwtTokenManager;

    @Autowired
    private RestTemplate restTemplate;

    @RequestMapping("/list")
    public Result list() {
        List<UserEntity> list = userService.list();
        return Result.success(list);
    }

    @PostMapping("/login")
    public Result login(@RequestBody UserEntity user) {
        //AuthenticationManager authenticate进行用户认证
        UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(user.getNo(), user.getPassword());
        Authentication authenticate = authenticationManager.authenticate(authenticationToken);
        //如果认证没通过，给出对应的提示
        if (Objects.isNull(authenticate)) {
            throw new RuntimeException("登录失败");
        }
        //如果认证通过了，使用userid生成一个jwt jwt存入ResponseResult返回
        LoginUser loginUser = (LoginUser) authenticate.getPrincipal();
        UserEntity userEntity = loginUser.getUser();
        String userId = userEntity.getId() + "";
        String jwtTokenByUserId = jwtTokenManager.getJwtTokenByUserId(userId); //redis中获取是否用户有历史token
        if (org.springframework.util.StringUtils.hasText(jwtTokenByUserId)) {
            jwtTokenManager.addTokenToBlacklist(jwtTokenByUserId);  //之前这个userId登录的账号有历史token ，token加入黑名单
        }
        String jwt = JwtUtil.createJWT(userId);
        jwtTokenManager.addJwtTokenByUserId(userId, jwt); //用户登录对应的有效新token存入redis
        //把完整的用户信息存入redis  userid作为key
        redisCache.setCacheObject("login:" + userId, loginUser);
        HashMap<String, Object> res = new HashMap<>();
        res.put("user", userEntity);
        res.put("token", jwt);
        return Result.success(res);
    }

    /**
     * 注解权限测试
     * @return
     */
    @PreAuthorize("hasRole('USER')")
    @GetMapping("/test")
    public Result testVisitPermission() {
        log.info("用户权限测试");
        restTemplate.postForObject("https://www.baidu.com", JSON.toJSONString(new JSONObject()), String.class);
        return Result.success();
    }

    @XxlJob(value = "testXXlJob")
    public void testXXlJob() {
        log.info("测试XxlJob");
    }

}
