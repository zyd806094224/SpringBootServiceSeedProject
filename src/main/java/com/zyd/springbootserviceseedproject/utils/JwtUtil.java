package com.zyd.springbootserviceseedproject.utils;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtBuilder;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;

import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.Date;
import java.util.UUID;

/**
 * @author zhaoyudong
 * @version 1.0
 * @description JWT工具类（适配JJWT 0.11.0+版本）
 * @date 2025/9/24 10:51
 */
public class JwtUtil {

    // 有效期为1小时
    public static final Long JWT_TTL = 60 * 60 * 1000L;
    // 注意：新版本要求密钥至少32个字符
    public static final String JWT_KEY = "yiluxiangbei1234567890abcdefghijklmn";

    public static String getUUID() {
        return UUID.randomUUID().toString().replaceAll("-", "");
    }

    /**
     * 生成jwt
     *
     * @param subject token中要存放的数据（json格式）
     * @return 生成的token
     */
    public static String createJWT(String subject) {
        JwtBuilder builder = getJwtBuilder(subject, null, getUUID());
        return builder.compact();
    }

    /**
     * 生成jwt
     *
     * @param subject   token中要存放的数据（json格式）
     * @param ttlMillis token超时时间
     * @return 生成的token
     */
    public static String createJWT(String subject, Long ttlMillis) {
        JwtBuilder builder = getJwtBuilder(subject, ttlMillis, getUUID());
        return builder.compact();
    }

    /**
     * 创建token
     *
     * @param id        唯一标识
     * @param subject   主题内容
     * @param ttlMillis 过期时间
     * @return 生成的token
     */
    public static String createJWT(String id, String subject, Long ttlMillis) {
        JwtBuilder builder = getJwtBuilder(subject, ttlMillis, id);
        return builder.compact();
    }

    private static JwtBuilder getJwtBuilder(String subject, Long ttlMillis, String uuid) {
        // 生成密钥（新版本推荐方式）
        Key key = Keys.hmacShaKeyFor(JWT_KEY.getBytes(StandardCharsets.UTF_8));

        long nowMillis = System.currentTimeMillis();
        Date now = new Date(nowMillis);

        // 设置过期时间
        long expMillis = nowMillis + (ttlMillis != null ? ttlMillis : JWT_TTL);
        Date expDate = new Date(expMillis);

        return Jwts.builder()
                .setId(uuid)              // 唯一的ID
                .setSubject(subject)      // 主题（可以是JSON数据）
                .setIssuer("ylxb")        // 签发者
                .setIssuedAt(now)         // 签发时间
                .signWith(key)            // 新版本无需指定算法，会根据密钥自动推断
                .setExpiration(expDate);  // 过期时间
    }

    /**
     * 解析token
     *
     * @param jwt 待解析的token
     * @return 解析后的Claims
     */
    public static Claims parseJWT(String jwt) {
        // 生成密钥
        Key key = Keys.hmacShaKeyFor(JWT_KEY.getBytes(StandardCharsets.UTF_8));

        return Jwts.parserBuilder()       // 新版本使用parserBuilder
                .setSigningKey(key)       // 设置签名密钥
                .build()                  // 构建解析器
                .parseClaimsJws(jwt)      // 解析token
                .getBody();               // 获取负载内容
    }

    public static void main(String[] args) throws Exception {
        String jwt = createJWT("2123");
        System.out.println("生成的token: " + jwt);

        Claims claims = parseJWT(jwt);
        System.out.println("解析的主题: " + claims.getSubject());
    }
}
