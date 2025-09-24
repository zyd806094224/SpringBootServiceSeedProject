package com.zyd.springbootserviceseedproject.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.http.converter.ByteArrayHttpMessageConverter;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.web.client.RestTemplate;

import java.nio.charset.StandardCharsets;

/**
 * @author zhaoyudong
 * @version 1.0
 * @description RestTemplate配置 Restful服务的客户端工具
 * @date 2025/9/24 18:05
 */
@Configuration
public class CoreConfiguration {


    @Bean
    public StringHttpMessageConverter stringHttpMessageConverter() {
        return new StringHttpMessageConverter(StandardCharsets.UTF_8);
    }

    @Bean
    public ByteArrayHttpMessageConverter byteArrayHttpMessageConverter() {
        return new ByteArrayHttpMessageConverter();
    }

    @Bean
    @ConditionalOnProperty(
            value = {"spring.rest.connection.connection-request-timeout",
                    "spring.rest.connection.connect-timeout",
                    "spring.rest.connection.read-timeout"}
    )
    @ConfigurationProperties(prefix = "spring.rest.connection")
    public HttpComponentsClientHttpRequestFactory customHttpRequestFactory() {
        return new HttpComponentsClientHttpRequestFactory();
    }

    @Bean
    public RestTemplate restTemplate(@Autowired(required = false) HttpComponentsClientHttpRequestFactory customHttpRequestFactory) {
        RestTemplate restTemplate;
        if (null == customHttpRequestFactory) {
            restTemplate = new RestTemplate();
        } else {
            restTemplate = new RestTemplate(customHttpRequestFactory);
        }
        return restTemplate;
    }

}
