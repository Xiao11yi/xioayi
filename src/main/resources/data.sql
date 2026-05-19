INSERT IGNORE INTO `user` (`username`, `password`, `enabled`)
VALUES ('admin', '$2a$10$1ij.zZImynXyQo63p/6KmO/1qtHtfBwYDdcf6Mucn7t3grEF8sLHe', 1);

INSERT IGNORE INTO `product` (`id`, `name`, `description`, `price`, `stock`, `create_time`, `update_time`)
VALUES
(1, 'MacBook Pro', 'Apple M3 Pro 芯片 18GB 内存 512GB 存储', 19999.00, 50, NOW(), NOW()),
(2, 'iPhone 16 Pro', 'A18 Pro 芯片 256GB 存储 钛金属机身', 9999.00, 100, NOW(), NOW()),
(3, 'AirPods Pro 2', '主动降噪 自适应音频 USB-C 充电盒', 1899.00, 200, NOW(), NOW()),
(4, 'iPad Air', 'M2 芯片 11英寸 Liquid 视网膜显示屏', 4799.00, 80, NOW(), NOW()),
(5, 'Apple Watch S10', '全天候视网膜显示屏 血氧检测 心电图', 3199.00, 60, NOW(), NOW());

-- 重置自增值，避免后续新增商品主键冲突
ALTER TABLE `product` AUTO_INCREMENT = 100;

INSERT IGNORE INTO `coupon` (`id`, `name`, `code`, `type`, `value`, `min_amount`, `max_amount`, `start_time`, `end_time`, `usage_limit`, `used_count`, `status`, `create_time`, `update_time`)
VALUES
(1, '新用户首单优惠', 'NEW50', 0, 20.00, 100.00, 50.00, NOW(), '2027-12-31 23:59:59', 100, 0, 0, NOW(), NOW()),
(2, '618 大促券', '618SALE', 1, 30.00, 200.00, NULL, NOW(), '2026-06-30 23:59:59', 500, 10, 0, NOW(), NOW()),
(3, '会员专享折扣', 'VIP10', 0, 10.00, 0.00, 100.00, NOW(), '2026-12-31 23:59:59', 0, 0, 0, NOW(), NOW());

ALTER TABLE `coupon` AUTO_INCREMENT = 100;
