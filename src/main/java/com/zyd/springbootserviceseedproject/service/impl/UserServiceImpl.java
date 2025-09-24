package com.zyd.springbootserviceseedproject.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zyd.springbootserviceseedproject.entity.UserEntity;
import com.zyd.springbootserviceseedproject.mapper.UserMapper;
import com.zyd.springbootserviceseedproject.service.UserService;
import org.springframework.stereotype.Service;

/**
 * @author zhaoyudong
 * @version 1.0
 * @description 用户服务实现类
 * @date 2025/9/23 20:16
 */

@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, UserEntity> implements UserService {

}
