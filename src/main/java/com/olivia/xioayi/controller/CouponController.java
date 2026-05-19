package com.olivia.xioayi.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.olivia.xioayi.annotation.BusinessType;
import com.olivia.xioayi.annotation.Log;
import com.olivia.xioayi.common.ApiResponse;
import com.olivia.xioayi.dao.Coupon;
import com.olivia.xioayi.service.CouponService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@Tag(name = "优惠券管理", description = "优惠券的增删改查操作")
@RestController
@RequestMapping("/api/coupons")
@RequiredArgsConstructor
public class CouponController {

    private final CouponService couponService;

    @Operation(summary = "优惠券分页列表", description = "公开接口，无需登录，返回分页优惠券数据")
    @GetMapping
    public ApiResponse<Page<Coupon>> list(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size) {
        return ApiResponse.success(couponService.listCoupons(page, size));
    }

    @Operation(summary = "查询优惠券详情", description = "公开接口，按 ID 查询优惠券")
    @GetMapping("/{id}")
    public ApiResponse<Coupon> get(@PathVariable Long id) {
        return ApiResponse.success(couponService.getCouponById(id));
    }

    @Operation(summary = "新增优惠券", description = "需要登录，创建并返回新优惠券")
    @Log(title = "优惠券管理", businessType = BusinessType.INSERT)
    @PostMapping
    public ApiResponse<Coupon> create(@RequestBody Coupon coupon) {
        return ApiResponse.success(couponService.createCoupon(coupon));
    }

    @Operation(summary = "更新优惠券", description = "需要登录，按 ID 更新优惠券信息")
    @Log(title = "优惠券管理", businessType = BusinessType.UPDATE)
    @PutMapping("/{id}")
    public ApiResponse<Coupon> update(@PathVariable Long id, @RequestBody Coupon coupon) {
        return ApiResponse.success(couponService.updateCoupon(id, coupon));
    }

    @Operation(summary = "删除优惠券", description = "需要登录，按 ID 删除优惠券，不存在则返回 404")
    @Log(title = "优惠券管理", businessType = BusinessType.DELETE)
    @DeleteMapping("/{id}")
    public ApiResponse<Void> delete(@PathVariable Long id) {
        couponService.deleteCoupon(id);
        return ApiResponse.success();
    }
}
