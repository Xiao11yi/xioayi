package com.olivia.xioayi.service;

import com.olivia.xioayi.dao.Coupon;
import com.olivia.xioayi.mapper.CouponMapper;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
public class CouponCacheService {

    private static final String STOCK_PREFIX = "coupon:stock:";
    private static final long CACHE_TTL_HOURS = 2;

    private final StringRedisTemplate redisTemplate;
    private final CouponMapper couponMapper;

    @PostConstruct
    public void init() {
        List<Coupon> coupons = couponMapper.selectList(null);
        for (Coupon c : coupons) {
            if (c.getStatus() == 0 && c.getUsageLimit() != null && c.getUsageLimit() > 0) {
                long remaining = c.getUsageLimit() - (c.getUsedCount() != null ? c.getUsedCount() : 0);
                redisTemplate.opsForValue()
                        .set(stockKey(c.getId()), String.valueOf(Math.max(remaining, 0)),
                                CACHE_TTL_HOURS, TimeUnit.HOURS);
            }
        }
    }

    /** 原子扣减，仅在库存 > 0 时减 1，返回剩余库存（<=0 表示已抢完） */
    public long decrStock(Long couponId) {
        String key = stockKey(couponId);
        String lua = "if redis.call('GET', KEYS[1]) and tonumber(redis.call('GET', KEYS[1])) > 0 " +
                     "then return redis.call('DECR', KEYS[1]) " +
                     "else return -1 end";
        Long result = redisTemplate.execute(new DefaultRedisScript<>(lua, Long.class), List.of(key));
        return result != null ? result : -1;
    }

    public void incrStock(Long couponId) {
        redisTemplate.opsForValue().increment(stockKey(couponId));
    }

    public long getStock(Long couponId) {
        String val = redisTemplate.opsForValue().get(stockKey(couponId));
        return val != null ? Long.parseLong(val) : -1;
    }

    public void initStock(Long couponId) {
        Coupon c = couponMapper.selectById(couponId);
        if (c != null && c.getStatus() == 0 && c.getUsageLimit() != null && c.getUsageLimit() > 0) {
            long remaining = c.getUsageLimit() - (c.getUsedCount() != null ? c.getUsedCount() : 0);
            redisTemplate.opsForValue()
                    .set(stockKey(couponId), String.valueOf(Math.max(remaining, 0)),
                            CACHE_TTL_HOURS, TimeUnit.HOURS);
        }
    }

    private String stockKey(Long couponId) {
        return STOCK_PREFIX + couponId;
    }
}
