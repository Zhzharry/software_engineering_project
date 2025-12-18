-- ============================================
-- 灾情数据管理系统数据库初始化脚本
-- 数据库名称: zaiqing
-- 创建日期: 2025-12-01
-- 字符集: utf8mb4
-- 排序规则: utf8mb4_unicode_ci
-- ============================================

-- 1. 删除已存在的数据库（谨慎使用，生产环境请注释掉）
-- DROP DATABASE IF EXISTS zaiqing;

-- 2. 创建数据库（如果不存在）
-- MySQL 5.x 不支持在 CREATE DATABASE 中使用 COMMENT，已移除
CREATE DATABASE IF NOT EXISTS zaiqing 
    CHARACTER SET utf8mb4 
    COLLATE utf8mb4_unicode_ci;

-- 3. 使用数据库
USE zaiqing;

-- ============================================
-- 4. 创建用户表 (user)
-- 用途: 存储系统用户信息
-- ============================================
DROP TABLE IF EXISTS `user`;

CREATE TABLE `user` (
    `id` BIGINT(20) NOT NULL AUTO_INCREMENT COMMENT '用户ID',
    `username` VARCHAR(255) NOT NULL COMMENT '用户名',
    `email` VARCHAR(255) DEFAULT NULL COMMENT '邮箱',
    `phone` VARCHAR(20) DEFAULT NULL COMMENT '手机号',
    `status` INT(11) DEFAULT 1 COMMENT '状态：0-禁用，1-启用',
    `create_time` DATETIME DEFAULT NULL COMMENT '创建时间',
    `update_time` DATETIME DEFAULT NULL COMMENT '更新时间',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_username` (`username`),
    KEY `idx_status` (`status`),
    KEY `idx_create_time` (`create_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='用户表';

-- ============================================
-- 5. 创建模块实体表 (module_entity)
-- 用途: 存储系统模块配置信息
-- ============================================
DROP TABLE IF EXISTS `module_entity`;

CREATE TABLE `module_entity` (
    `id` BIGINT(20) NOT NULL AUTO_INCREMENT COMMENT '模块ID',
    `name` VARCHAR(100) NOT NULL COMMENT '模块名称',
    `description` VARCHAR(500) DEFAULT NULL COMMENT '模块描述',
    `status` INT(11) NOT NULL DEFAULT 1 COMMENT '状态：0-禁用，1-启用',
    `create_time` DATETIME NOT NULL COMMENT '创建时间',
    `update_time` DATETIME NOT NULL COMMENT '更新时间',
    PRIMARY KEY (`id`),
    KEY `idx_status` (`status`),
    KEY `idx_create_time` (`create_time`),
    KEY `idx_name` (`name`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='模块实体表';

-- ============================================
-- 6. 创建原始数据表 (raw_data)
-- 用途: 存储从多源接入的原始灾情数据
-- ============================================
DROP TABLE IF EXISTS `raw_data`;

CREATE TABLE `raw_data` (
    `id` BIGINT(20) NOT NULL AUTO_INCREMENT COMMENT '原始数据ID',
    `data_content` TEXT COMMENT '数据内容（JSON格式或文本）',
    `data_type` VARCHAR(100) DEFAULT NULL COMMENT '数据类型：如地震、洪水、火灾等',
    `source_system` VARCHAR(100) DEFAULT NULL COMMENT '来源系统：数据接入源',
    `module_id` BIGINT(20) DEFAULT NULL COMMENT '关联模块ID',
    `status` INT(11) DEFAULT 1 COMMENT '状态：0-已删除，1-正常，2-已处理，3-处理失败',
    `priority` INT(11) DEFAULT 0 COMMENT '优先级：0-普通，1-重要，2-紧急',
    `create_time` DATETIME DEFAULT NULL COMMENT '创建时间',
    `update_time` DATETIME DEFAULT NULL COMMENT '更新时间',
    PRIMARY KEY (`id`),
    KEY `idx_data_type` (`data_type`),
    KEY `idx_source_system` (`source_system`),
    KEY `idx_status` (`status`),
    KEY `idx_module_id` (`module_id`),
    KEY `idx_create_time` (`create_time`),
    KEY `idx_priority` (`priority`),
    CONSTRAINT `fk_raw_data_module` FOREIGN KEY (`module_id`) REFERENCES `module_entity` (`id`) ON DELETE SET NULL ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='原始数据表';

-- ============================================
-- 7. 创建数据接入记录表 (data_source)
-- 用途: 记录数据接入的详细信息，用于数据追踪和管理
-- ============================================
DROP TABLE IF EXISTS `data_source`;

CREATE TABLE `data_source` (
    `id` BIGINT(20) NOT NULL AUTO_INCREMENT COMMENT '数据源ID',
    `source_name` VARCHAR(100) NOT NULL COMMENT '数据源名称',
    `source_type` VARCHAR(50) DEFAULT NULL COMMENT '数据源类型：API、文件、数据库等',
    `source_url` VARCHAR(500) DEFAULT NULL COMMENT '数据源地址',
    `config_json` TEXT COMMENT '配置信息（JSON格式）',
    `status` INT(11) DEFAULT 1 COMMENT '状态：0-禁用，1-启用',
    `last_sync_time` DATETIME DEFAULT NULL COMMENT '最后同步时间',
    `create_time` DATETIME DEFAULT NULL COMMENT '创建时间',
    `update_time` DATETIME DEFAULT NULL COMMENT '更新时间',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_source_name` (`source_name`),
    KEY `idx_source_type` (`source_type`),
    KEY `idx_status` (`status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='数据源配置表';

-- ============================================
-- 8. 创建数据编码规则表 (data_encoding_rule)
-- 用途: 存储数据编码和解码规则
-- ============================================
DROP TABLE IF EXISTS `data_encoding_rule`;

CREATE TABLE `data_encoding_rule` (
    `id` BIGINT(20) NOT NULL AUTO_INCREMENT COMMENT '规则ID',
    `rule_name` VARCHAR(100) NOT NULL COMMENT '规则名称',
    `data_type` VARCHAR(100) DEFAULT NULL COMMENT '适用的数据类型',
    `encoding_type` VARCHAR(50) DEFAULT NULL COMMENT '编码类型：JSON、XML、CSV等',
    `rule_content` TEXT COMMENT '规则内容（JSON格式）',
    `version` VARCHAR(20) DEFAULT '1.0' COMMENT '规则版本',
    `status` INT(11) DEFAULT 1 COMMENT '状态：0-禁用，1-启用',
    `create_time` DATETIME DEFAULT NULL COMMENT '创建时间',
    `update_time` DATETIME DEFAULT NULL COMMENT '更新时间',
    PRIMARY KEY (`id`),
    KEY `idx_data_type` (`data_type`),
    KEY `idx_encoding_type` (`encoding_type`),
    KEY `idx_status` (`status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='数据编码规则表';

-- ============================================
-- 9. 创建数据请求记录表 (data_request)
-- 用途: 记录数据请求和响应信息，用于权限管理和审计
-- ============================================
DROP TABLE IF EXISTS `data_request`;

CREATE TABLE `data_request` (
    `id` BIGINT(20) NOT NULL AUTO_INCREMENT COMMENT '请求ID',
    `user_id` BIGINT(20) DEFAULT NULL COMMENT '请求用户ID',
    `request_type` VARCHAR(50) DEFAULT NULL COMMENT '请求类型：查询、导出、删除等',
    `resource_type` VARCHAR(50) DEFAULT NULL COMMENT '资源类型：raw_data、processed_data等',
    `resource_id` BIGINT(20) DEFAULT NULL COMMENT '资源ID',
    `request_params` TEXT COMMENT '请求参数（JSON格式）',
    `response_status` INT(11) DEFAULT NULL COMMENT '响应状态码',
    `response_time` INT(11) DEFAULT NULL COMMENT '响应时间（毫秒）',
    `ip_address` VARCHAR(50) DEFAULT NULL COMMENT '请求IP地址',
    `user_agent` VARCHAR(500) DEFAULT NULL COMMENT '用户代理',
    `create_time` DATETIME DEFAULT NULL COMMENT '请求时间',
    PRIMARY KEY (`id`),
    KEY `idx_user_id` (`user_id`),
    KEY `idx_request_type` (`request_type`),
    KEY `idx_resource_type` (`resource_type`),
    KEY `idx_create_time` (`create_time`),
    KEY `idx_ip_address` (`ip_address`),
    CONSTRAINT `fk_data_request_user` FOREIGN KEY (`user_id`) REFERENCES `user` (`id`) ON DELETE SET NULL ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='数据请求记录表';

-- ============================================
-- 10. 创建数据存储策略表 (storage_strategy)
-- 用途: 存储数据存储和淘汰策略配置
-- ============================================
DROP TABLE IF EXISTS `storage_strategy`;

CREATE TABLE `storage_strategy` (
    `id` BIGINT(20) NOT NULL AUTO_INCREMENT COMMENT '策略ID',
    `strategy_name` VARCHAR(100) NOT NULL COMMENT '策略名称',
    `data_type` VARCHAR(100) DEFAULT NULL COMMENT '适用的数据类型',
    `storage_type` VARCHAR(50) DEFAULT NULL COMMENT '存储类型：mysql、mongodb、redis等',
    `retention_days` INT(11) DEFAULT NULL COMMENT '保留天数',
    `max_size` BIGINT(20) DEFAULT NULL COMMENT '最大存储大小（字节）',
    `cleanup_rule` TEXT COMMENT '清理规则（JSON格式）',
    `status` INT(11) DEFAULT 1 COMMENT '状态：0-禁用，1-启用',
    `create_time` DATETIME DEFAULT NULL COMMENT '创建时间',
    `update_time` DATETIME DEFAULT NULL COMMENT '更新时间',
    PRIMARY KEY (`id`),
    KEY `idx_data_type` (`data_type`),
    KEY `idx_storage_type` (`storage_type`),
    KEY `idx_status` (`status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='数据存储策略表';

-- ============================================
-- 11. 插入初始数据
-- ============================================

-- 插入默认管理员用户（可选）
INSERT INTO `user` (`username`, `email`, `phone`, `status`, `create_time`, `update_time`) 
VALUES ('admin', 'admin@zaiqing.com', '13800138000', 1, NOW(), NOW())
ON DUPLICATE KEY UPDATE `update_time` = NOW();

-- 插入默认模块
INSERT INTO `module_entity` (`name`, `description`, `status`, `create_time`, `update_time`) VALUES
('多源数据接入模块', '负责从多个数据源接入灾情数据', 1, NOW(), NOW()),
('数据解码与管理系统', '负责数据解码和管理', 1, NOW(), NOW()),
('数据格式支持与编码模块', '支持多种数据格式和编码方式', 1, NOW(), NOW()),
('数据请求响应与权限管理模块', '处理数据请求和权限控制', 1, NOW(), NOW()),
('数据存储与淘汰策略模块', '管理数据存储和淘汰策略', 1, NOW(), NOW())
ON DUPLICATE KEY UPDATE `update_time` = NOW();

-- ============================================
-- 12. 显示创建结果
-- ============================================
SELECT '数据库初始化完成！' AS message;
SELECT '数据库名称: zaiqing' AS info;
SELECT COUNT(*) AS table_count FROM information_schema.tables WHERE table_schema = 'zaiqing';

-- 显示所有表
SHOW TABLES;

-- 显示表结构统计
SELECT 
    TABLE_NAME AS '表名',
    TABLE_COMMENT AS '表说明',
    TABLE_ROWS AS '记录数'
FROM information_schema.TABLES 
WHERE TABLE_SCHEMA = 'zaiqing' 
ORDER BY TABLE_NAME;
