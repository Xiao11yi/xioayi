package com.olivia.xioayi.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
public class TokenService {

    private final StringRedisTemplate redisTemplate;
    private static final String TOKEN_PREFIX = "auth:token:";
    private static final long TOKEN_EXPIRE_HOURS = 1;

    // 登录时存储token -> username
    public void storeToken(String token, String username) {
        redisTemplate.opsForValue()
                .set(TOKEN_PREFIX + token, username, TOKEN_EXPIRE_HOURS, TimeUnit.HOURS);
    }

    // 验证token是否有效（存在即有效）
    public boolean isTokenValid(String token) {
        return Boolean.TRUE.equals(redisTemplate.hasKey(TOKEN_PREFIX + token));
    }

    // 登出时移除token
    public void removeToken(String token) {
        redisTemplate.delete(TOKEN_PREFIX + token);
    }

    // 从token获取用户名（可选）
    public String getUsernameFromToken(String token) {
        return redisTemplate.opsForValue().get(TOKEN_PREFIX + token);
    }
}