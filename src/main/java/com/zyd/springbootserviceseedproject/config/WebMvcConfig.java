package com.zyd.springbootserviceseedproject.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * @author zhaoyudong
 * @version 1.0
 * @description Web MVC配置类
 * @date 2025/10/25
 */
@Configuration
public class WebMvcConfig implements WebMvcConfigurer {

    @Autowired
    private FileUploadConfig fileUploadConfig;

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // 配置静态资源访问，使上传的文件可以通过URL访问
        String uploadPath = fileUploadConfig.getPath();
        if (uploadPath != null && !uploadPath.isEmpty()) {
            // 将文件系统路径映射到URL路径
            String resourceLocation = "file:" + uploadPath + "/";
            registry.addResourceHandler("/uploads/**")
                    .addResourceLocations(resourceLocation);
        }
    }
}