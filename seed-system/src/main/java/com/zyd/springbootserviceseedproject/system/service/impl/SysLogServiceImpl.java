package com.zyd.springbootserviceseedproject.system.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zyd.springbootserviceseedproject.common.annotation.DataSource;
import com.zyd.springbootserviceseedproject.common.enums.DataSourceType;
import com.zyd.springbootserviceseedproject.system.domain.SysLogEntity;
import com.zyd.springbootserviceseedproject.system.mapper.SysLogMapper;
import com.zyd.springbootserviceseedproject.system.service.SysLogService;
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
