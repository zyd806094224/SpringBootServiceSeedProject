package com.zyd.springbootserviceseedproject.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zyd.springbootserviceseedproject.config.db.DataSource;
import com.zyd.springbootserviceseedproject.config.db.DataSourceType;
import com.zyd.springbootserviceseedproject.entity.SysLogEntity;
import com.zyd.springbootserviceseedproject.mapper.SysLogMapper;
import com.zyd.springbootserviceseedproject.service.SysLogService;
import org.springframework.stereotype.Service;

/**
 * @author zhaoyudong
 * @version 1.0
 * @description 系统日志Service
 * @date 2025/9/24 09:16
 */

@DataSource(DataSourceType.LOG)
@Service
public class SysLogServiceImpl extends ServiceImpl<SysLogMapper, SysLogEntity> implements SysLogService {

}
