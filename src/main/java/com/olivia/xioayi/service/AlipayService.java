package com.olivia.xioayi.service;

import com.alipay.api.AlipayClient;
import com.alipay.api.DefaultAlipayClient;
import com.alipay.api.internal.util.AlipaySignature;
import com.alipay.api.request.AlipayTradePagePayRequest;
import com.olivia.xioayi.config.AlipayConfig;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class AlipayService {

    private final AlipayConfig alipayConfig;
    private AlipayClient alipayClient;

    @PostConstruct
    public void init() {
        alipayClient = new DefaultAlipayClient(
                alipayConfig.getGatewayUrl(),
                alipayConfig.getAppId(),
                alipayConfig.getMerchantPrivateKey(),
                "json",
                alipayConfig.getCharset(),
                alipayConfig.getAlipayPublicKey(),
                alipayConfig.getSignType());
    }

    public String createPayForm(String orderNo, BigDecimal amount, String subject) throws Exception {
        AlipayTradePagePayRequest request = new AlipayTradePagePayRequest();
        request.setNotifyUrl(alipayConfig.getNotifyUrl());
        request.setReturnUrl(alipayConfig.getReturnUrl());
        request.setBizContent(String.format(
                "{\"out_trade_no\":\"%s\",\"total_amount\":\"%.2f\",\"subject\":\"%s\",\"product_code\":\"FAST_INSTANT_TRADE_PAY\"}",
                orderNo, amount, subject));
        return alipayClient.pageExecute(request).getBody();
    }

    public boolean verifyNotify(Map<String, String> params) throws Exception {
        return AlipaySignature.rsaCheckV1(params, alipayConfig.getAlipayPublicKey(),
                alipayConfig.getCharset(), alipayConfig.getSignType());
    }
}
