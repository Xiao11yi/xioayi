package com.olivia.xioayi.service;

import com.olivia.xioayi.dao.Order;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public interface OrderService {

    Order createOrder(Long productId, Long userId);

    Order getByOrderNo(String orderNo);

    String payForm(String orderNo);

    void handlePaidNotify(String orderNo, String alipayTradeNo, LocalDateTime payTime);

    void expireOrders();
}
