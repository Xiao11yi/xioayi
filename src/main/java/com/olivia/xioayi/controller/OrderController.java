package com.olivia.xioayi.controller;

import com.olivia.xioayi.annotation.BusinessType;
import com.olivia.xioayi.annotation.Log;
import com.olivia.xioayi.common.ApiResponse;
import com.olivia.xioayi.dao.Order;
import com.olivia.xioayi.service.OrderService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;

@Tag(name = "订单管理", description = "商品下单与支付")
@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;
    private final com.olivia.xioayi.mapper.UserMapper userMapper;

    @Operation(summary = "创建订单", description = "需要登录，按商品 ID 创建订单，30分钟内未支付自动过期")
    @Log(title = "订单管理", businessType = BusinessType.INSERT)
    @PostMapping("/product/{productId}")
    public ApiResponse<Order> create(@PathVariable Long productId, Principal principal) {
        Long userId = getUserId(principal);
        return ApiResponse.success(orderService.createOrder(productId, userId));
    }

    @Operation(summary = "获取支付表单", description = "返回支付宝跳转表单 HTML，前端自动提交即可")
    @PostMapping("/{orderNo}/pay")
    public ApiResponse<String> pay(@PathVariable String orderNo) {
        String form = orderService.payForm(orderNo);
        return ApiResponse.success(form);
    }

    @Operation(summary = "查询订单", description = "按订单号查询订单状态")
    @GetMapping("/{orderNo}")
    public ApiResponse<Order> get(@PathVariable String orderNo) {
        return ApiResponse.success(orderService.getByOrderNo(orderNo));
    }

    private Long getUserId(Principal principal) {
        com.olivia.xioayi.dao.User user = userMapper.selectOne(
                new com.baomidou.mybatisplus.core.conditions.query.QueryWrapper<com.olivia.xioayi.dao.User>()
                        .eq("username", principal.getName()));
        return user != null ? user.getId() : 0L;
    }
}
