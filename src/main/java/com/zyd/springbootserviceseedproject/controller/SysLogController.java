package com.zyd.springbootserviceseedproject.controller;

import com.zyd.springbootserviceseedproject.common.Result;
import com.zyd.springbootserviceseedproject.config.db.DataSource;
import com.zyd.springbootserviceseedproject.config.db.DataSourceType;
import com.zyd.springbootserviceseedproject.entity.SysLogEntity;
import com.zyd.springbootserviceseedproject.service.SysLogService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * @author zhaoyudong
 * @version 1.0
 * @description 系统日志Controller
 * @date 2025/9/24 09:19
 */
@Slf4j
@RestController
@RequestMapping("/syslog")
public class SysLogController {

    @Autowired
    private SysLogService sysLogService;

    @DataSource(DataSourceType.LOG)
    @RequestMapping("/list")
    public Result list() {
        List<SysLogEntity> list = sysLogService.list();
        return Result.success(list);
    }

}
