package com.zyd.springbootserviceseedproject;

import java.util.TimeZone;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * 启动程序
 *
 * @author zyd
 */
@EnableScheduling
@SpringBootApplication(exclude = { DataSourceAutoConfiguration.class })
public class SeedApplication
{
    public static void main(String[] args)
    {
        // 显式固定 JVM 默认时区为东八区，保证 LocalDate.now()、@Scheduled、jackson 序列化
        // 与 MySQL serverTimezone=GMT+8 / time_zone='+08:00' 完全一致，避免容器化或跨机房部署时提醒漂移。
        // 必须在 SpringApplication.run 之前调用，确保 Spring 容器初始化时就已生效。
        TimeZone.setDefault(TimeZone.getTimeZone("Asia/Shanghai"));
        SpringApplication.run(SeedApplication.class, args);
        System.out.println("(♥◠‿◠)ﾉﾞ  SeedProject启动成功   ლ(´ڡ`ლ)ﾞ");
    }
}
