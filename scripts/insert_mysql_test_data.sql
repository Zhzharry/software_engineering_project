-- MySQL测试数据插入脚本
-- 执行此脚本以在MySQL的raw_data表中插入测试数据

USE zaiqing;

-- 先确保表结构已更新（如果还没有执行alter_raw_data_table.sql）
-- ALTER TABLE `raw_data` 
-- ADD COLUMN IF NOT EXISTS `disaster_category` VARCHAR(100) DEFAULT NULL COMMENT '灾情类别' AFTER `data_type`,
-- ADD COLUMN IF NOT EXISTS `processed` TINYINT(1) DEFAULT 0 COMMENT '是否已处理' AFTER `status`;

-- 插入测试数据
INSERT INTO `raw_data` (
    `data_content`, 
    `data_type`, 
    `disaster_category`,
    `source_system`, 
    `module_id`, 
    `status`, 
    `processed`,
    `priority`, 
    `create_time`, 
    `update_time`
) VALUES
-- 震情数据
('{"magnitude": 6.2, "depth": 10.5, "location": "四川省阿坝州汶川县", "time": "2025-12-19T12:00:00Z", "latitude": 31.002, "longitude": 103.322}', 
 '地震数据', '震情', '国家地震台网中心', 1, 1, 0, 2, NOW(), NOW()),

-- 人员伤亡及失踪数据
('{"magnitude": 5.8, "depth": 8.3, "location": "云南省昭通市鲁甸县", "time": "2025-12-19T13:00:00Z", "latitude": 27.192, "longitude": 103.558, "casualties": 3}', 
 '地震数据', '人员伤亡及失踪', '前方指挥部', 1, 1, 0, 2, NOW(), NOW()),

-- 房屋破坏数据
('{"magnitude": 4.5, "depth": 12.1, "location": "北京市", "time": "2025-12-19T14:00:00Z", "latitude": 39.904, "longitude": 116.407}', 
 '地震数据', '房屋破坏', '现场调查', 1, 1, 0, 1, NOW(), NOW()),

-- 生命线工程灾情数据
('{"magnitude": 3.8, "depth": 5.7, "location": "江苏省南京市", "time": "2025-12-19T15:00:00Z", "latitude": 32.060, "longitude": 118.796}', 
 '地震数据', '生命线工程灾情', '互联网感知', 1, 1, 0, 1, NOW(), NOW()),

-- 次生灾害数据
('{"magnitude": 5.2, "depth": 9.8, "location": "广东省广州市", "time": "2025-12-19T16:00:00Z", "latitude": 23.129, "longitude": 113.264}', 
 '地震数据', '次生灾害', '传感器监测', 1, 1, 0, 1, NOW(), NOW()),

-- 更多测试数据
('{"magnitude": 4.8, "depth": 11.2, "location": "河北省唐山市", "time": "2025-12-19T17:00:00Z", "latitude": 39.630, "longitude": 118.180}', 
 '地震数据', '震情', '国家地震台网中心', 1, 1, 1, 0, DATE_SUB(NOW(), INTERVAL 1 DAY), NOW()),

('{"magnitude": 5.5, "depth": 9.5, "location": "新疆维吾尔自治区阿克苏地区", "time": "2025-12-19T18:00:00Z", "latitude": 41.168, "longitude": 80.263}', 
 '地震数据', '人员伤亡及失踪', '后方指挥部', 1, 1, 0, 2, DATE_SUB(NOW(), INTERVAL 2 DAY), NOW()),

('{"magnitude": 4.2, "depth": 7.8, "location": "台湾省花莲县", "time": "2025-12-19T19:00:00Z", "latitude": 23.973, "longitude": 121.601}', 
 '地震数据', '房屋破坏', '卫星遥感', 1, 1, 1, 1, DATE_SUB(NOW(), INTERVAL 3 DAY), NOW());

-- 查看插入的数据
SELECT 
    id, 
    data_type, 
    disaster_category, 
    source_system, 
    status, 
    processed, 
    create_time 
FROM `raw_data` 
ORDER BY create_time DESC 
LIMIT 10;

