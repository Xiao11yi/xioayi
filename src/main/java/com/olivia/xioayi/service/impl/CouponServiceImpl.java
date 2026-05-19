package com.olivia.xioayi.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.olivia.xioayi.dao.Coupon;
import com.olivia.xioayi.mapper.CouponMapper;
import com.olivia.xioayi.service.CouponService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.NoSuchElementException;

@Service
@RequiredArgsConstructor
@Transactional(rollbackFor = Exception.class)
public class CouponServiceImpl implements CouponService {

    private final CouponMapper couponMapper;

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
}
