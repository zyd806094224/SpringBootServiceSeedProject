package com.zyd.springbootserviceseedproject;

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
        SpringApplication.run(SeedApplication.class, args);
        System.out.println("(♥◠‿◠)ﾉﾞ  SeedProject启动成功   ლ(´ڡ`ლ)ﾞ");
    }
}
