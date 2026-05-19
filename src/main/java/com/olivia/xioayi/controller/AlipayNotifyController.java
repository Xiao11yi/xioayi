package com.olivia.xioayi.controller;

import com.olivia.xioayi.service.AlipayService;
import com.olivia.xioayi.service.OrderService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/alipay")
@RequiredArgsConstructor
public class AlipayNotifyController {

    private static final Logger log = LoggerFactory.getLogger(AlipayNotifyController.class);
    private final AlipayService alipayService;
    private final OrderService orderService;

    @PostMapping("/notify")
    public String handleNotify(HttpServletRequest request) {
        Map<String, String> params = getParams(request);
        log.info("Alipay notify received: {}", params);

        try {
            if (!alipayService.verifyNotify(params)) {
                log.warn("Alipay notify signature verification failed");
                return "failure";
            }

            String tradeStatus = params.get("trade_status");
            if (!"TRADE_SUCCESS".equals(tradeStatus)) {
                return "success";
            }

            String orderNo = params.get("out_trade_no");
            String tradeNo = params.get("trade_no");
            LocalDateTime payTime = LocalDateTime.parse(params.get("gmt_payment"),
                    DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));

            orderService.handlePaidNotify(orderNo, tradeNo, payTime);
            log.info("Order {} paid successfully, alipay trade no: {}", orderNo, tradeNo);
            return "success";
        } catch (Exception e) {
            log.error("Alipay notify handling failed", e);
            return "failure";
        }
    }

    private Map<String, String> getParams(HttpServletRequest request) {
        Map<String, String> params = new HashMap<>();
        request.getParameterMap().forEach((key, values) -> params.put(key, values[0]));
        return params;
    }
}
