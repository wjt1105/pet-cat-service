-- ============================================================
-- 城市上门喂猫预约平台  MySQL 8 建表脚本
-- 字符集：utf8mb4   引擎：InnoDB
-- ============================================================

CREATE DATABASE IF NOT EXISTS `petsitter`
    DEFAULT CHARACTER SET utf8mb4
    DEFAULT COLLATE utf8mb4_general_ci;

USE `petsitter`;

-- ------------------------------------------------------------
-- 1. 用户表
-- ------------------------------------------------------------
DROP TABLE IF EXISTS `user`;
CREATE TABLE `user` (
    `id`          BIGINT       NOT NULL AUTO_INCREMENT COMMENT '用户ID',
    `username`    VARCHAR(50)  NOT NULL COMMENT '用户名',
    `password`    VARCHAR(100) NOT NULL COMMENT '密码（BCrypt 加密）',
    `nickname`    VARCHAR(50)           DEFAULT NULL COMMENT '昵称',
    `phone`       VARCHAR(20)           DEFAULT NULL COMMENT '手机号',
    `avatar`      VARCHAR(255)          DEFAULT NULL COMMENT '头像地址',
    `role`        TINYINT      NOT NULL DEFAULT 1 COMMENT '角色：0-管理员 1-普通用户 2-上门铲屎官',
    `status`      TINYINT      NOT NULL DEFAULT 1 COMMENT '状态：0-禁用 1-正常',
    `create_time` DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `is_deleted`  TINYINT      NOT NULL DEFAULT 0 COMMENT '逻辑删除：0-未删除 1-已删除',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_username` (`username`),
    KEY `idx_phone` (`phone`),
    KEY `idx_role_status` (`role`, `status`)
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COMMENT = '用户表';

-- ------------------------------------------------------------
-- 2. 喂猫服务表
-- ------------------------------------------------------------
DROP TABLE IF EXISTS `pet_service`;
CREATE TABLE `pet_service` (
    `id`            BIGINT        NOT NULL AUTO_INCREMENT COMMENT '服务ID',
    `sitter_id`     BIGINT        NOT NULL COMMENT '铲屎官用户ID',
    `title`         VARCHAR(100)  NOT NULL COMMENT '服务标题',
    `description`   VARCHAR(500)           DEFAULT NULL COMMENT '服务描述',
    `service_items` VARCHAR(255)  NOT NULL COMMENT '服务项目（逗号分隔）',
    `price`         DECIMAL(10, 2) NOT NULL DEFAULT 0.00 COMMENT '服务价格（元）',
    `duration`      INT           NOT NULL COMMENT '服务时长（分钟）',
    `city`          VARCHAR(50)   NOT NULL COMMENT '所在城市',
    `district`      VARCHAR(50)            DEFAULT NULL COMMENT '所在区县',
    `cover_url`     VARCHAR(255)           DEFAULT NULL COMMENT '封面图地址',
    `stock`         INT           NOT NULL DEFAULT 0 COMMENT '库存（可预约次数）',
    `status`        TINYINT       NOT NULL DEFAULT 1 COMMENT '状态：0-下架 1-上架',
    `create_time`   DATETIME      NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time`   DATETIME      NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `is_deleted`    TINYINT       NOT NULL DEFAULT 0 COMMENT '逻辑删除：0-未删除 1-已删除',
    PRIMARY KEY (`id`),
    KEY `idx_sitter` (`sitter_id`),
    KEY `idx_city_status` (`city`, `status`)
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COMMENT = '上门喂猫服务表';

-- ------------------------------------------------------------
-- 3. 订单表
-- ------------------------------------------------------------
DROP TABLE IF EXISTS `orders`;
CREATE TABLE `orders` (
    `id`               BIGINT        NOT NULL AUTO_INCREMENT COMMENT '订单ID',
    `order_no`         VARCHAR(32)   NOT NULL COMMENT '订单号',
    `user_id`          BIGINT        NOT NULL COMMENT '下单用户ID',
    `service_id`       BIGINT        NOT NULL COMMENT '服务ID',
    `sitter_id`        BIGINT        NOT NULL COMMENT '铲屎官用户ID',
    `service_title`    VARCHAR(100)  NOT NULL COMMENT '服务标题快照',
    `service_price`    DECIMAL(10, 2) NOT NULL COMMENT '服务单价快照（元）',
    `appointment_time` DATETIME      NOT NULL COMMENT '预约上门时间',
    `service_address`  VARCHAR(255)  NOT NULL COMMENT '服务地址',
    `remark`           VARCHAR(255)           DEFAULT NULL COMMENT '备注',
    `amount`           DECIMAL(10, 2) NOT NULL COMMENT '订单总金额（元）',
    `status`           TINYINT       NOT NULL DEFAULT 0 COMMENT '状态：0-待支付 1-待服务 2-已完成 3-已取消',
    `pay_time`         DATETIME               DEFAULT NULL COMMENT '支付时间',
    `finish_time`      DATETIME               DEFAULT NULL COMMENT '完成时间',
    `cancel_time`      DATETIME               DEFAULT NULL COMMENT '取消时间',
    `create_time`      DATETIME      NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time`      DATETIME      NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `is_deleted`       TINYINT       NOT NULL DEFAULT 0 COMMENT '逻辑删除：0-未删除 1-已删除',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_order_no` (`order_no`),
    KEY `idx_user` (`user_id`),
    KEY `idx_sitter` (`sitter_id`),
    KEY `idx_status` (`status`)
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COMMENT = '订单表';

-- ------------------------------------------------------------
-- 4. 支付表
-- ------------------------------------------------------------
DROP TABLE IF EXISTS `payment`;
CREATE TABLE `payment` (
    `id`          BIGINT        NOT NULL AUTO_INCREMENT COMMENT '支付单ID',
    `payment_no`  VARCHAR(32)   NOT NULL COMMENT '支付单号',
    `order_id`    BIGINT        NOT NULL COMMENT '订单ID',
    `order_no`    VARCHAR(32)   NOT NULL COMMENT '订单号',
    `user_id`     BIGINT        NOT NULL COMMENT '支付用户ID',
    `amount`      DECIMAL(10, 2) NOT NULL COMMENT '支付金额（元）',
    `pay_type`    TINYINT       NOT NULL COMMENT '支付方式：1-微信 2-支付宝',
    `status`      TINYINT       NOT NULL DEFAULT 0 COMMENT '状态：0-待支付 1-支付成功 2-支付失败 3-已退款',
    `trade_no`    VARCHAR(64)            DEFAULT NULL COMMENT '第三方交易流水号',
    `pay_time`    DATETIME               DEFAULT NULL COMMENT '支付时间',
    `create_time` DATETIME      NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` DATETIME      NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `is_deleted`  TINYINT       NOT NULL DEFAULT 0 COMMENT '逻辑删除：0-未删除 1-已删除',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_payment_no` (`payment_no`),
    KEY `idx_order_no` (`order_no`),
    KEY `idx_user` (`user_id`)
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COMMENT = '支付表';

-- ------------------------------------------------------------
-- 5. Seata AT 模式 undo_log 表
-- ------------------------------------------------------------
DROP TABLE IF EXISTS `undo_log`;
CREATE TABLE `undo_log` (
    `branch_id`     BIGINT       NOT NULL COMMENT '分支事务ID',
    `xid`           VARCHAR(128) NOT NULL COMMENT '全局事务ID',
    `context`       VARCHAR(128) NOT NULL COMMENT '上下文（如序列化方式）',
    `rollback_info` LONGBLOB     NOT NULL COMMENT '回滚信息',
    `log_status`    INT          NOT NULL COMMENT '状态：0-正常 1-全局事务已完成',
    `log_created`   DATETIME(6)  NOT NULL COMMENT '创建时间',
    `log_modified`  DATETIME(6)  NOT NULL COMMENT '修改时间',
    UNIQUE KEY `ux_undo_log` (`xid`, `branch_id`)
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COMMENT = 'Seata AT 模式事务回滚日志表';
