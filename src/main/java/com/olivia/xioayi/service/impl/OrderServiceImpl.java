package com.olivia.xioayi.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.olivia.xioayi.dao.Order;
import com.olivia.xioayi.dao.Product;
import com.olivia.xioayi.mapper.OrderMapper;
import com.olivia.xioayi.service.AlipayService;
import com.olivia.xioayi.service.OrderService;
import com.olivia.xioayi.service.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.NoSuchElementException;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService {

    private final OrderMapper orderMapper;
    private final ProductService productService;
    private final AlipayService alipayService;

    @Override
    @Transactional
    public Order createOrder(Long productId, Long userId) {
        Product product = productService.getProductById(productId);

        Order order = new Order();
        order.setOrderNo(generateOrderNo());
        order.setProductId(productId);
        order.setProductName(product.getName());
        order.setUserId(userId);
        order.setAmount(product.getPrice());
        order.setStatus(0);
        order.setCreateTime(LocalDateTime.now());
        order.setExpireTime(LocalDateTime.now().plusMinutes(30));
        orderMapper.insert(order);
        return order;
    }

    @Override
    public Order getByOrderNo(String orderNo) {
        Order order = orderMapper.selectOne(new QueryWrapper<Order>().eq("order_no", orderNo));
        if (order == null) {
            throw new NoSuchElementException("订单不存在: " + orderNo);
        }
        return order;
    }

    @Override
    @Transactional
    public String payForm(String orderNo) {
        Order order = getByOrderNo(orderNo);

        if (order.getStatus() != 0) {
            throw new IllegalArgumentException("订单状态不正确");
        }
        if (order.getExpireTime() != null && LocalDateTime.now().isAfter(order.getExpireTime())) {
            order.setStatus(2);
            orderMapper.updateById(order);
            throw new IllegalArgumentException("订单已过期，请重新下单");
        }

        try {
            return alipayService.createPayForm(orderNo, order.getAmount(), order.getProductName());
        } catch (Exception e) {
            throw new RuntimeException("调用支付宝失败: " + e.getMessage());
        }
    }

    @Override
    @Transactional
    public void handlePaidNotify(String orderNo, String alipayTradeNo, LocalDateTime payTime) {
        int updated = orderMapper.markPaid(orderNo, alipayTradeNo);
        if (updated == 0) {
            Order order = orderMapper.selectOne(new QueryWrapper<Order>().eq("order_no", orderNo));
            if (order != null && order.getStatus() == 1) {
                return;
            }
            throw new IllegalArgumentException("订单状态异常: " + orderNo);
        }
    }

    @Override
    @Transactional
    public void expireOrders() {
        orderMapper.expireOrders();
    }

    private String generateOrderNo() {
        return LocalDateTime.now().format(java.time.format.DateTimeFormatter.ofPattern("yyyyMMddHHmmss"))
                + UUID.randomUUID().toString().replace("-", "").substring(0, 10).toUpperCase();
    }
}
