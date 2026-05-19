package com.olivia.xioayi.dao;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("user_coupon")
public class UserCoupon {

    @TableId(type = IdType.AUTO)
    @Schema(description = "记录 ID")
    private Long id;

    @Schema(description = "优惠券 ID")
    private Long couponId;

    @Schema(description = "用户 ID")
    private Long userId;

    @Schema(description = "领取时间")
    private LocalDateTime grabTime;

    @Schema(description = "是否已使用: 0=未使用, 1=已使用")
    private Integer used;

    @Schema(description = "使用时间")
    private LocalDateTime usedTime;
}
