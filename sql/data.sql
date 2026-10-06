-- ============================================================
-- 城市上门喂猫预约平台  初始化测试数据
-- 执行前请先执行 schema.sql
-- 所有账号密码统一为：123456（BCrypt 加密存储）
-- ============================================================

USE `petsitter`;

-- ------------------------------------------------------------
-- 用户数据（1 管理员 / 2 普通用户 / 2 铲屎官）
-- ------------------------------------------------------------
INSERT INTO `user` (`id`, `username`, `password`, `nickname`, `phone`, `avatar`, `role`, `status`) VALUES
(1, 'admin',   '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVEFDa', '系统管理员', '13000000000', NULL, 0, 1),
(2, 'meow01',  '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVEFDa', '王爱猫',     '13800000001', NULL, 1, 1),
(3, 'meow02',  '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVEFDa', '张喵奴',     '13800000002', NULL, 1, 1),
(4, 'sitter01', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVEFDa', '李铲屎',    '13900000001', NULL, 2, 1),
(5, 'sitter02', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVEFDa', '周喂猫',    '13900000002', NULL, 2, 1);

-- ------------------------------------------------------------
-- 喂猫服务数据
-- ------------------------------------------------------------
INSERT INTO `pet_service` (`id`, `sitter_id`, `title`, `description`, `service_items`, `price`, `duration`, `city`, `district`, `cover_url`, `stock`, `status`) VALUES
(1, 4, '上门喂猫基础护理', '专业上门添粮换水、清理猫砂，全程拍照录像反馈。', '添粮换水,清理猫砂,拍照反馈',        68.00,  45, '北京', '朝阳区',   NULL, 10, 1),
(2, 4, '多猫家庭豪华套餐', '适合 3 只猫以上家庭，含深度清洁与 30 分钟陪玩。', '添粮换水,清理猫砂,梳毛陪玩,全屋检查', 128.00, 90, '北京', '海淀区',   NULL, 5, 1),
(3, 5, '温柔铲屎喂粮服务', '温柔有耐心，独居猫咪的安心之选。',                 '添粮换水,清理猫砂,陪玩安抚',         58.00,  40, '上海', '浦东新区', NULL, 8, 1);

-- ------------------------------------------------------------
-- 订单数据
-- ------------------------------------------------------------
INSERT INTO `orders` (`id`, `order_no`, `user_id`, `service_id`, `sitter_id`, `service_title`, `service_price`,
                      `appointment_time`, `service_address`, `remark`, `amount`, `status`, `pay_time`) VALUES
(1, 'PS202610030001', 2, 1, 4, '上门喂猫基础护理', 68.00,
    DATE_ADD(NOW(), INTERVAL 1 DAY), '北京市朝阳区建国路 88 号 SOHO 现代城 A 座 1201', '猫咪怕生，请轻声操作', 68.00, 1, DATE_SUB(NOW(), INTERVAL 30 MINUTE)),
(2, 'PS202610030002', 3, 3, 5, '温柔铲屎喂粮服务', 58.00,
    DATE_ADD(NOW(), INTERVAL 2 DAY), '上海市浦东新区陆家嘴环路 1000 号 5 栋 1802', NULL, 58.00, 0, NULL);

-- ------------------------------------------------------------
-- 支付数据
-- ------------------------------------------------------------
INSERT INTO `payment` (`id`, `payment_no`, `order_id`, `order_no`, `user_id`, `amount`, `pay_type`, `status`, `trade_no`, `pay_time`) VALUES
(1, 'PAY202610030001', 1, 'PS202610030001', 2, 68.00, 1, 1, 'WX4200002886202610030001234567', DATE_SUB(NOW(), INTERVAL 30 MINUTE));
