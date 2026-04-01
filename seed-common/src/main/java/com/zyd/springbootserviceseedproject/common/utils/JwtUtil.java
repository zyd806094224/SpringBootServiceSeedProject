package com.zyd.springbootserviceseedproject.common.utils;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtBuilder;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

import javax.crypto.spec.SecretKeySpec;
import java.security.Key;
import java.util.Base64;
import java.util.Date;
import java.util.UUID;

/**
 * @author zhaoyudong
 * @version 1.0
 * @description JWT工具类
 * @date 2025/9/24 10:51
 */
public class JwtUtil {

    // 有效期为1小时
    public static final Long JWT_TTL = 60 * 60 * 1000L;
    // 密钥
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
        Key key = new SecretKeySpec(Base64.getDecoder().decode(JWT_KEY), SignatureAlgorithm.HS256.getJcaName());

        long nowMillis = System.currentTimeMillis();
        Date now = new Date(nowMillis);

        long expMillis = nowMillis + (ttlMillis != null ? ttlMillis : JWT_TTL);
        Date expDate = new Date(expMillis);

        return Jwts.builder()
                .setId(uuid)
                .setSubject(subject)
                .setIssuer("ylxb")
                .setIssuedAt(now)
                .signWith(SignatureAlgorithm.HS256, key)
                .setExpiration(expDate);
    }

    /**
     * 解析token
     *
     * @param jwt 待解析的token
     * @return 解析后的Claims
     */
    public static Claims parseJWT(String jwt) {
        Key key = new SecretKeySpec(Base64.getDecoder().decode(JWT_KEY), SignatureAlgorithm.HS256.getJcaName());
        return Jwts.parser()
                .setSigningKey(key)
                .parseClaimsJws(jwt)
                .getBody();
    }

    public static void main(String[] args) throws Exception {
        String jwt = createJWT("2123");
        System.out.println("生成的token: " + jwt);

        Claims claims = parseJWT(jwt);
        System.out.println("解析的主题: " + claims.getSubject());
    }
}
