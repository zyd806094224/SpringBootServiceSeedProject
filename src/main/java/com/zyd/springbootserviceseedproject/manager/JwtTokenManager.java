package com.zyd.springbootserviceseedproject.manager;

import com.zyd.springbootserviceseedproject.cache.RedisCache;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

/**
 * @author zhaoyudong
 * @version 1.0
 * @description JwtToken管理
 * @date 2025/9/24 10:50
 */
@Component
public class JwtTokenManager {

    @Autowired
    private RedisCache redisCache;

    public void addTokenToBlacklist(String token) {
        redisCache.setCacheObject("jwt:blacklist:" + token, token);
    }

    public Boolean tokenOnTheBlacklist(String token) {
        return StringUtils.hasText(redisCache.getCacheObject("jwt:blacklist:" + token));
    }

    public void addJwtTokenByUserId(String userId, String token) {
        redisCache.setCacheObject("jwt:" + userId, token);
    }

    public String getJwtTokenByUserId(String userId) {
        return redisCache.getCacheObject("jwt:" + userId);
    }

}
