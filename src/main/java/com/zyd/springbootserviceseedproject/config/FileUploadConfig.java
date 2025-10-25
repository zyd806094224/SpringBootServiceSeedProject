package com.zyd.springbootserviceseedproject.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * @author zhaoyudong
 * @version 1.0
 * @description 文件上传配置类
 * @date 2025/10/25
 */
@Data
@Configuration
@ConfigurationProperties(prefix = "file.upload")
public class FileUploadConfig {

    /**
     * 上传目录
     */
    private String path;

    /**
     * 允许的文件类型
     */
    private String allowedTypes;

    /**
     * 最大文件大小（字节）
     */
    private Long maxSize;

    /**
     * 一次最多上传文件数量
     */
    private Integer maxCount;
}