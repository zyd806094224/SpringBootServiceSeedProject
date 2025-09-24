package com.zyd.springbootserviceseedproject.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * @author zhaoyudong
 * @version 1.0
 * @description 系统日志表
 * @date 2025/9/24 09:10
 */

@Data
@TableName("sys_log")
public class SysLogEntity {
    /**
     * 日志主键
     * 对应数据库字段：id，自增主键
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 日志内容
     * 对应数据库字段：log_content
     */
    private String logContent;

    /**
     * 日志记录时间
     * 对应数据库字段：log_time
     */
    private LocalDateTime logTime;
}
