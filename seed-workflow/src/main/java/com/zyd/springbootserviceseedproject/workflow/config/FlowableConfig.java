package com.zyd.springbootserviceseedproject.workflow.config;

import org.flowable.engine.impl.db.DbIdGenerator;
import org.flowable.spring.SpringProcessEngineConfiguration;
import org.flowable.spring.boot.EngineConfigurationConfigurer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Flowable工作流引擎配置
 */
@Configuration
public class FlowableConfig {

    @Bean
    public EngineConfigurationConfigurer<SpringProcessEngineConfiguration> processEngineConfigurationConfigurer() {
        return engineConfiguration -> {
            // 数据库表自动更新
            engineConfiguration.setDatabaseSchemaUpdate("true");
            // 关闭异步执行器
            engineConfiguration.setAsyncExecutorActivate(false);
            // 中文字体（流程图生成用）
            engineConfiguration.setActivityFontName("宋体");
            engineConfiguration.setLabelFontName("宋体");
            engineConfiguration.setAnnotationFontName("宋体");
            // ID生成器
            engineConfiguration.setIdGenerator(new DbIdGenerator());
        };
    }
}
