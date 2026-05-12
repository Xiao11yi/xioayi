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
