package com.olivia.xioayi.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.olivia.xioayi.dao.Coupon;
import com.olivia.xioayi.dao.User;
import com.olivia.xioayi.dao.UserCoupon;
import com.olivia.xioayi.mapper.CouponMapper;
import com.olivia.xioayi.mapper.UserCouponMapper;
import com.olivia.xioayi.mapper.UserMapper;
import com.olivia.xioayi.service.CouponService;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.NoSuchElementException;

@Service
@RequiredArgsConstructor
@Transactional(rollbackFor = Exception.class)
public class CouponServiceImpl implements CouponService {

    private final CouponMapper couponMapper;
    private final UserCouponMapper userCouponMapper;
    private final UserMapper userMapper;

    @Override
    @Transactional(readOnly = true)
    public Page<Coupon> listCoupons(int page, int size) {
        Page<Coupon> pageParam = new Page<>(page, size);
        LambdaQueryWrapper<Coupon> wrapper = new LambdaQueryWrapper<>();
        wrapper.orderByDesc(Coupon::getCreateTime);
        return couponMapper.selectPage(pageParam, wrapper);
    }

    @Override
    @Transactional(readOnly = true)
    public Coupon getCouponById(Long id) {
        Coupon coupon = couponMapper.selectById(id);
        if (coupon == null) {
            throw new NoSuchElementException("优惠券不存在: id=" + id);
        }
        return coupon;
    }

    @Override
    public Coupon createCoupon(Coupon coupon) {
        coupon.setUsedCount(0);
        coupon.setCreateTime(LocalDateTime.now());
        coupon.setUpdateTime(LocalDateTime.now());
        couponMapper.insert(coupon);
        return coupon;
    }

    @Override
    public Coupon updateCoupon(Long id, Coupon coupon) {
        getCouponById(id);
        coupon.setId(id);
        coupon.setUpdateTime(LocalDateTime.now());
        couponMapper.updateById(coupon);
        return couponMapper.selectById(id);
    }

    @Override
    public void deleteCoupon(Long id) {
        getCouponById(id);
        couponMapper.deleteById(id);
    }

    @Override
    public Coupon grabCoupon(Long couponId, String username) {
        // 1. 查询优惠券
        Coupon coupon = getCouponById(couponId);

        // 2. 校验状态
        if (coupon.getStatus() != 0) {
            throw new IllegalArgumentException("优惠券已失效");
        }

        // 3. 校验时间
        LocalDateTime now = LocalDateTime.now();
        if (now.isBefore(coupon.getStartTime())) {
            throw new IllegalArgumentException("优惠券还未开始领取");
        }
        if (now.isAfter(coupon.getEndTime())) {
            throw new IllegalArgumentException("优惠券已过期");
        }

        // 4. 查询用户
        User user = userMapper.selectOne(new QueryWrapper<User>().eq("username", username));
        if (user == null) {
            throw new NoSuchElementException("用户不存在: " + username);
        }

        // 5. 原子递增 used_count，先占住名额（并发安全）
        int updated = couponMapper.incrementUsedCount(couponId);
        if (updated == 0) {
            throw new IllegalArgumentException("优惠券已被抢完");
        }

        // 6. 记录领取（由 DB 唯一约束防止重复，而非先查后插）
        UserCoupon uc = new UserCoupon();
        uc.setCouponId(couponId);
        uc.setUserId(user.getId());
        uc.setGrabTime(now);
        uc.setUsed(0);
        try {
            userCouponMapper.insert(uc);
        } catch (DuplicateKeyException e) {
            // 唯一约束冲突：同一用户重复领取 → 回退名额
            couponMapper.decrementUsedCount(couponId);
            throw new IllegalArgumentException("已领取过该优惠券，不能重复领取");
        }

        return couponMapper.selectById(couponId);
    }
}
