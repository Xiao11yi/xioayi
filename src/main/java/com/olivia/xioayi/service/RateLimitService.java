package com.olivia.xioayi.service;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
public class RateLimitService {

    private static final Logger log = LoggerFactory.getLogger(RateLimitService.class);
    private static final String PREFIX_RATE = "ratelimit:";
    private static final String PREFIX_BLACKLIST = "blacklist:";

    /** 用户阈值：超过此值拉黑 30 分钟 */
    private static final long USER_LIMIT = 5;
    /** 设备指纹阈值：超过此值拉黑 30 分钟 */
    private static final long DEVICE_LIMIT = 10;

    private final StringRedisTemplate redisTemplate;

    public void checkGrabLimit(String username) {
        String deviceId = getDeviceFingerprint();

        String userKey = PREFIX_RATE + "grab:user:" + username;
        String deviceKey = PREFIX_RATE + "grab:device:" + deviceId;
        String userBlackKey = PREFIX_BLACKLIST + "grab:user:" + username;
        String deviceBlackKey = PREFIX_BLACKLIST + "grab:device:" + deviceId;

        // 1. 检查用户黑名单
        if (Boolean.TRUE.equals(redisTemplate.hasKey(userBlackKey))) {
            throw new RateLimitException("账号已被限制访问，请30分钟后再试");
        }

        // 2. 检查设备黑名单
        if (Boolean.TRUE.equals(redisTemplate.hasKey(deviceBlackKey))) {
            throw new RateLimitException("设备已被限制访问，请30分钟后再试");
        }

        // 3. 检查用户频率（上限 5 次/分钟），超限拉黑
        long userCount = incrAndGet(userKey, 60);
        if (userCount > USER_LIMIT) {
            redisTemplate.opsForValue().set(userBlackKey, "1", 30, TimeUnit.MINUTES);
            throw new RateLimitException("操作过于频繁，账号已被限制访问30分钟");
        }

        // 4. 检查设备频率（上限 10 次/分钟），超限拉黑
        long deviceCount = incrAndGet(deviceKey, 60);
        if (deviceCount > DEVICE_LIMIT) {
            redisTemplate.opsForValue().set(deviceBlackKey, "1", 30, TimeUnit.MINUTES);
            log.warn("设备触发拉黑: device={}, count={}/min, user={}", deviceId, deviceCount, username);
            throw new RateLimitException("操作过于频繁，设备已被限制访问30分钟");
        }
    }

    /**
     * 获取设备指纹，优先级：
     * 1. 客户端主动上报的 X-Device-Id 头
     * 2. 服务端根据 User-Agent + IP 生成的 fallback 指纹
     */
    public String getDeviceFingerprint() {
        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();

        String deviceId = request.getHeader("X-Device-Id");
        if (deviceId != null && !deviceId.isBlank() && !"null".equals(deviceId)) {
            return "ext:" + deviceId;
        }

        // fallback: User-Agent + IP 的 MD5
        String ua = request.getHeader("User-Agent");
        String ip = getClientIp(request);
        String raw = (ua != null ? ua : "") + "|" + (ip != null ? ip : "");
        return "fb:" + md5(raw);
    }

    private long incrAndGet(String key, int ttlSeconds) {
        Long count = redisTemplate.opsForValue().increment(key);
        if (count != null && count == 1) {
            redisTemplate.expire(key, ttlSeconds, TimeUnit.SECONDS);
        }
        return count != null ? count : 0;
    }

    private String getClientIp(HttpServletRequest request) {
        String ip = request.getHeader("X-Forwarded-For");
        if (ip == null || ip.isBlank()) ip = request.getRemoteAddr();
        return ip;
    }

    private String md5(String input) {
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            byte[] digest = md.digest(input.getBytes());
            StringBuilder sb = new StringBuilder();
            for (byte b : digest) sb.append(String.format("%02x", b));
            return sb.toString();
        } catch (NoSuchAlgorithmException e) {
            return String.valueOf(input.hashCode());
        }
    }

    public static class RateLimitException extends RuntimeException {
        public RateLimitException(String message) {
            super(message);
        }
    }
}
