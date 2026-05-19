package com.olivia.xioayi.aspect;

import com.olivia.xioayi.annotation.Idempotent;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.security.Principal;
import java.util.concurrent.TimeUnit;

@Aspect
@Component
@RequiredArgsConstructor
public class IdempotentAspect {

    private final StringRedisTemplate redisTemplate;

    @Before("@annotation(idempotent)")
    public void checkIdempotent(JoinPoint joinPoint, Idempotent idempotent) {
        String key = buildKey(joinPoint, idempotent.prefix());
        boolean success = Boolean.TRUE.equals(
                redisTemplate.opsForValue().setIfAbsent(key, "1", idempotent.ttl(), TimeUnit.SECONDS));
        if (!success) {
            throw new IllegalArgumentException("操作过于频繁，请稍后重试");
        }
    }

    private String buildKey(JoinPoint joinPoint, String prefix) {
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        String methodName = signature.getMethod().getName();
        Object[] args = joinPoint.getArgs();
        StringBuilder sb = new StringBuilder("idempotent:").append(prefix).append(":").append(methodName);

        // 提取 Principal 的 name 作为用户标识
        for (Object arg : args) {
            if (arg instanceof Principal p) {
                sb.append(":").append(p.getName());
            } else if (arg instanceof String s) {
                sb.append(":").append(s);
            } else if (arg instanceof Long l) {
                sb.append(":").append(l);
            } else if (arg instanceof Integer i) {
                sb.append(":").append(i);
            }
        }

        // 如果从参数拿不到用户，尝试从请求属性拿 token
        String username = org.springframework.security.core.context.SecurityContextHolder.getContext()
                .getAuthentication() != null
                ? org.springframework.security.core.context.SecurityContextHolder.getContext().getAuthentication().getName()
                : "anonymous";
        if (!sb.toString().contains(username)) {
            sb.append(":").append(username);
        }

        return sb.toString();
    }
}
