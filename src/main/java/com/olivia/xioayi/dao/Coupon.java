package com.olivia.xioayi.dao;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@TableName("coupon")
public class Coupon {

    @TableId(type = IdType.AUTO)
    @Schema(description = "优惠券 ID")
    private Long id;

    @Schema(description = "优惠券名称", example = "新用户首单优惠")
    private String name;

    @Schema(description = "优惠券编码", example = "NEW50")
    private String code;

    @Schema(description = "类型: 0=百分比, 1=固定金额", example = "0")
    private Integer type;

    @Schema(description = "折扣值（百分比时存百分数如20=20%，固定金额时存金额）", example = "20.00")
    private BigDecimal value;

    @Schema(description = "最低使用金额", example = "100.00")
    private BigDecimal minAmount;

    @Schema(description = "最高抵扣金额（百分比时有效）", example = "50.00")
    private BigDecimal maxAmount;

    @Schema(description = "生效时间")
    private LocalDateTime startTime;

    @Schema(description = "过期时间")
    private LocalDateTime endTime;

    @Schema(description = "使用次数限制（0=不限制）", example = "100")
    private Integer usageLimit;

    @Schema(description = "已使用次数", example = "0")
    private Integer usedCount;

    @Schema(description = "状态: 0=启用, 1=过期, 2=禁用", example = "0")
    private Integer status;

    @Schema(description = "创建时间")
    private LocalDateTime createTime;

    @Schema(description = "更新时间")
    private LocalDateTime updateTime;
}
