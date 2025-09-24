package com.zyd.springbootserviceseedproject.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.zyd.springbootserviceseedproject.bean.LoginUser;
import com.zyd.springbootserviceseedproject.entity.RoleEntity;
import com.zyd.springbootserviceseedproject.entity.UserEntity;
import com.zyd.springbootserviceseedproject.mapper.UserMapper;
import com.zyd.springbootserviceseedproject.mapper.UserRoleMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * @author zhaoyudong
 * @version 1.0
 * @description security登录用户服务实现
 * @date 2025/9/24 11:20
 */
@Service
public class UserDetailsServiceImpl implements UserDetailsService {

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private UserRoleMapper userRoleMapper;


    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        //查询用户信息
        LambdaQueryWrapper<UserEntity> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(UserEntity::getNo, username);
        UserEntity user = userMapper.selectOne(queryWrapper);
        //如果没有查询到用户就抛出异常
        if (Objects.isNull(user)) {
            throw new RuntimeException("用户名或者密码错误");
        }
        //根据用户ID查询角色列表
        List<RoleEntity> roles = userRoleMapper.selectRolesByUserId(user.getId());

        //把角色信息转换为权限列表
        List<String> permissions = roles.stream()
                .map(role -> "ROLE_" + role.getRoleKey().toUpperCase())
                .collect(Collectors.toList());

        //把数据封装成UserDetails返回
        return new LoginUser(user, permissions);
    }
}
