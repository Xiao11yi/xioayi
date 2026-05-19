package com.olivia.xioayi.dao;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@TableName("orders")
public class Order {

    @TableId(type = IdType.AUTO)
    @Schema(description = "订单 ID")
    private Long id;

    @Schema(description = "订单号")
    private String orderNo;

    @Schema(description = "商品 ID")
    private Long productId;

    @Schema(description = "商品名称快照")
    private String productName;

    @Schema(description = "用户 ID")
    private Long userId;

    @Schema(description = "支付金额")
    private BigDecimal amount;

    @Schema(description = "状态: 0=待支付, 1=已支付, 2=已过期")
    private Integer status;

    @Schema(description = "支付宝交易号")
    private String alipayTradeNo;

    @Schema(description = "创建时间")
    private LocalDateTime createTime;

    @Schema(description = "过期时间（创建后30分钟）")
    private LocalDateTime expireTime;

    @Schema(description = "支付时间")
    private LocalDateTime payTime;

    @Schema(description = "更新时间")
    private LocalDateTime updateTime;
}
