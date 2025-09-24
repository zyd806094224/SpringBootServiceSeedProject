package com.zyd.springbootserviceseedproject.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.zyd.springbootserviceseedproject.entity.UserEntity;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface UserMapper extends BaseMapper<UserEntity> {

}
