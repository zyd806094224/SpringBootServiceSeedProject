package com.zyd.springbootserviceseedproject.controller;

import com.zyd.springbootserviceseedproject.common.Result;
import com.zyd.springbootserviceseedproject.entity.UserEntity;
import com.zyd.springbootserviceseedproject.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

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
    @RequestMapping("/list")
    public Result list() {
        List<UserEntity> list = userService.list();
        return Result.success(list);
    }

}
