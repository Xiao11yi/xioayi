CREATE TABLE IF NOT EXISTS `coupon` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `name` varchar(100) NOT NULL COMMENT '优惠券名称',
  `code` varchar(50) NOT NULL COMMENT '优惠券编码',
  `type` tinyint NOT NULL DEFAULT 0 COMMENT '类型: 0=百分比, 1=固定金额',
  `value` decimal(10,2) NOT NULL COMMENT '折扣值',
  `min_amount` decimal(10,2) DEFAULT 0.00 COMMENT '最低使用金额',
  `max_amount` decimal(10,2) DEFAULT NULL COMMENT '最高抵扣金额',
  `start_time` datetime NOT NULL COMMENT '生效时间',
  `end_time` datetime NOT NULL COMMENT '过期时间',
  `usage_limit` int DEFAULT 0 COMMENT '使用次数限制(0=不限制)',
  `used_count` int DEFAULT 0 COMMENT '已使用次数',
  `status` tinyint DEFAULT 0 COMMENT '状态: 0=启用, 1=过期, 2=禁用',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP,
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_code` (`code`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE IF NOT EXISTS `user_coupon` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `coupon_id` bigint NOT NULL COMMENT '优惠券ID',
  `user_id` bigint NOT NULL COMMENT '用户ID',
  `grab_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '领取时间',
  `used` tinyint DEFAULT 0 COMMENT '是否已使用: 0=未使用, 1=已使用',
  `used_time` datetime DEFAULT NULL COMMENT '使用时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_user_coupon` (`user_id`, `coupon_id`),
  KEY `idx_coupon_id` (`coupon_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE IF NOT EXISTS `orders` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `order_no` varchar(32) NOT NULL COMMENT '订单号',
  `product_id` bigint NOT NULL COMMENT '商品ID',
  `product_name` varchar(100) DEFAULT NULL COMMENT '商品名称快照',
  `user_id` bigint NOT NULL COMMENT '用户ID',
  `amount` decimal(10,2) NOT NULL COMMENT '支付金额',
  `status` tinyint DEFAULT 0 COMMENT '状态: 0=待支付, 1=已支付, 2=已过期',
  `alipay_trade_no` varchar(64) DEFAULT NULL COMMENT '支付宝交易号',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP,
  `expire_time` datetime DEFAULT NULL COMMENT '过期时间（创建后30分钟）',
  `pay_time` datetime DEFAULT NULL COMMENT '支付时间',
  `update_time` datetime DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_order_no` (`order_no`),
  KEY `idx_user_id` (`user_id`),
  KEY `idx_status` (`status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
