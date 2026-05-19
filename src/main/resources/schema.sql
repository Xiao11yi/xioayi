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
