package com.olivia.xioayi.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.olivia.xioayi.dao.Coupon;

public interface CouponService {

    Page<Coupon> listCoupons(int page, int size);

    Coupon getCouponById(Long id);

    Coupon createCoupon(Coupon coupon);

    Coupon updateCoupon(Long id, Coupon coupon);

    void deleteCoupon(Long id);
}
