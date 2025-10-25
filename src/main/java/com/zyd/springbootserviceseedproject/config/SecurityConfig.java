package com.zyd.springbootserviceseedproject.config;

import com.zyd.springbootserviceseedproject.filter.JwtAuthenticationTokenFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import javax.annotation.Resource;
import java.util.Arrays;

/**
 * @author zhaoyudong
 * @version 1.0
 * @description 最新版Spring Security配置类（适配5.7+版本）、移除了已废弃的WebSecurityConfigurerAdapter，采用组件式配置
 * @date 2025/9/24 13:32
 */
@Configuration
@EnableWebSecurity  // 启用Web安全配置
@EnableMethodSecurity  // 启用方法级安全注解
public class SecurityConfig {

    @Resource
    private JwtAuthenticationTokenFilter jwtAuthenticationTokenFilter;

    @Resource
    private AuthenticationEntryPoint authenticationEntryPoint;

    @Resource
    private AccessDeniedHandler accessDeniedHandler;

    @Resource
    private UserDetailsService userDetailsService;  // 需要注入自定义的用户详情服务

    // 创建BCryptPasswordEncoder注入容器
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    /**
     * 配置安全过滤器链
     */
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                // 关闭csrf
                .csrf(csrf -> csrf.disable())
                // 不通过Session获取SecurityContext
                .sessionManagement(session -> session
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                )
                // 配置URL权限
                .authorizeHttpRequests(auth -> auth
                        // 登录接口允许匿名访问
                        .requestMatchers("/user/login").anonymous()
                        // 文件上传接口允许匿名访问
                        .requestMatchers("/file/**").anonymous()
                        // 文件下载接口允许匿名访问
                        .requestMatchers("/uploads/**").anonymous()
                        //用户列表接口允许管理员和超级管理员访问
                        .requestMatchers("/user/list").hasAnyRole("ADMIN", "SUPER_ADMIN")
                        // 日志接口允许超级管理员访问
                        .requestMatchers("/syslog/list").hasRole("SUPER_ADMIN")
                        // 除上面外的所有请求全部需要鉴权认证
                        .anyRequest().authenticated()
                )
                // 配置异常处理器
                .exceptionHandling(ex -> ex
                        // 配置认证失败处理器
                        .authenticationEntryPoint(authenticationEntryPoint)
                        // 配置权限不足处理器
                        .accessDeniedHandler(accessDeniedHandler)
                )
                // 配置跨域
                .cors(cors -> cors.configurationSource(corsConfigurationSource()));

        // 添加JWT过滤器，放在UsernamePasswordAuthenticationFilter之前
        http.addFilterBefore(jwtAuthenticationTokenFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    /**
     * 配置AuthenticationManager
     */
    @Bean
    public AuthenticationManager authenticationManager() {
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
        provider.setUserDetailsService(userDetailsService);
        provider.setPasswordEncoder(passwordEncoder());
        return new ProviderManager(provider);
    }

    /**
     * 配置跨域资源共享
     */
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        // 允许的源（根据实际情况调整 生产环境需要指定域名 不建议用*）
        //setAllowedOriginPatterns支持模式匹配（类似正则表达式）
        //更灵活的域名匹配方式
        //与 allowCredentials(true) 兼容
        //使用 setAllowedOriginPatterns 时，Spring会在运行时根据实际请求源动态设置 Access-Control-Allow-Origin 头部
        configuration.setAllowedOriginPatterns(Arrays.asList("*"));
        //setAllowedOrigins  不使用这个是因为当设置为*时，与设置setAllowCredentials(true) 这个冲突
        //configuration.setAllowedOrigins(Arrays.asList("*"));
        // 允许的请求方法
        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        // 允许的请求头
        configuration.setAllowedHeaders(Arrays.asList("*"));
        // 允许暴露响应头
        configuration.setExposedHeaders(Arrays.asList("Authorization"));
        // 允许携带cookie
        configuration.setAllowCredentials(true);
        // 预检请求的缓存时间
        configuration.setMaxAge(3600L);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }
}
