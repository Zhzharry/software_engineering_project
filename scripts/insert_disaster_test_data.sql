-- 为disaster-data页面插入测试数据
-- 这些数据包含完整的灾情字段，用于测试disaster-data页面显示

USE zaiqing;

-- 插入包含完整灾情信息的测试数据
INSERT INTO `raw_data` (
    `data_content`, 
    `data_type`, 
    `disaster_category`,
    `disaster_subcategory`,
    `disaster_indicator`,
    `disaster_id`,
    `disaster_date_time`,
    `geo_code`,
    `source_system`,
    `source_category`,
    `carrier_type`,
    `decoded_description`,
    `module_id`, 
    `status`, 
    `processed`,
    `priority`, 
    `create_time`, 
    `update_time`
) VALUES
-- 震情数据
('{"magnitude": 6.2, "depth": 10.5, "location": "四川省阿坝州汶川县", "latitude": 31.002, "longitude": 103.322}', 
 '地震数据', 
 '震情', 
 NULL, 
 NULL,
 '510000202512191200000000000000000000',
 '2025-12-19 12:00:00',
 '510000000000',
 '国家地震台网中心',
 '国家地震台网中心',
 '文字',
 '四川省阿坝州汶川县发生6.2级地震，震源深度10.5公里',
 1, 1, 0, 2, NOW(), NOW()),

-- 人员伤亡及失踪数据
('{"magnitude": 5.8, "depth": 8.3, "location": "云南省昭通市鲁甸县", "casualties": 3, "injured": 5}', 
 '地震数据', 
 '人员伤亡及失踪', 
 '死亡', 
 '死亡人数',
 '530000202512191300000000000000000000',
 '2025-12-19 13:00:00',
 '530000000000',
 '前方指挥部',
 '前方指挥部',
 '文字',
 '云南省昭通市鲁甸县发生5.8级地震，造成3人死亡，5人受伤',
 1, 1, 0, 2, NOW(), NOW()),

-- 房屋破坏数据
('{"magnitude": 4.5, "depth": 12.1, "location": "北京市", "damaged_houses": 10}', 
 '地震数据', 
 '房屋破坏', 
 '砖木', 
 '一般损坏面积',
 '110000202512191400000000000000000000',
 '2025-12-19 14:00:00',
 '110000000000',
 '现场调查',
 '现场调查',
 '图像',
 '北京市发生4.5级地震，造成10间砖木结构房屋一般损坏',
 1, 1, 0, 1, NOW(), NOW()),

-- 生命线工程灾情数据
('{"magnitude": 3.8, "depth": 5.7, "location": "江苏省南京市", "affected_facilities": 2}', 
 '地震数据', 
 '生命线工程灾情', 
 '交通', 
 '受灾设施数',
 '320000202512191500000000000000000000',
 '2025-12-19 15:00:00',
 '320000000000',
 '互联网感知',
 '互联网感知',
 '文字',
 '江苏省南京市发生3.8级地震，影响2处交通设施',
 1, 1, 0, 1, NOW(), NOW()),

-- 次生灾害数据
('{"magnitude": 5.2, "depth": 9.8, "location": "广东省广州市", "secondary_disaster": "滑坡"}', 
 '地震数据', 
 '次生灾害', 
 '滑坡', 
 '灾害范围',
 '440000202512191600000000000000000000',
 '2025-12-19 16:00:00',
 '440000000000',
 '传感器监测',
 '传感器监测',
 '文字',
 '广东省广州市发生5.2级地震，引发滑坡次生灾害',
 1, 1, 0, 1, NOW(), NOW()),

-- 更多测试数据
('{"magnitude": 4.8, "depth": 11.2, "location": "河北省唐山市"}', 
 '地震数据', 
 '震情', 
 NULL, 
 NULL,
 '130000202512191700000000000000000000',
 '2025-12-19 17:00:00',
 '130000000000',
 '国家地震台网中心',
 '国家地震台网中心',
 '文字',
 '河北省唐山市发生4.8级地震',
 1, 1, 1, 0, DATE_SUB(NOW(), INTERVAL 1 DAY), NOW()),

('{"magnitude": 5.5, "depth": 9.5, "location": "新疆维吾尔自治区阿克苏地区", "casualties": 1}', 
 '地震数据', 
 '人员伤亡及失踪', 
 '受伤', 
 '受伤人数',
 '650000202512191800000000000000000000',
 '2025-12-19 18:00:00',
 '650000000000',
 '后方指挥部',
 '后方指挥部',
 '文字',
 '新疆维吾尔自治区阿克苏地区发生5.5级地震，造成1人受伤',
 1, 1, 0, 2, DATE_SUB(NOW(), INTERVAL 2 DAY), NOW()),

('{"magnitude": 4.2, "depth": 7.8, "location": "台湾省花莲县", "damaged_houses": 5}', 
 '地震数据', 
 '房屋破坏', 
 '框架', 
 '严重损坏面积',
 '710000202512191900000000000000000000',
 '2025-12-19 19:00:00',
 '710000000000',
 '卫星遥感',
 '卫星遥感',
 '图像',
 '台湾省花莲县发生4.2级地震，造成5间框架结构房屋严重损坏',
 1, 1, 1, 1, DATE_SUB(NOW(), INTERVAL 3 DAY), NOW());

-- 查看插入的数据
SELECT 
    id, 
    disaster_category, 
    disaster_subcategory,
    disaster_indicator,
    source_category,
    carrier_type,
    geo_code,
    disaster_date_time,
    decoded_description,
    create_time 
FROM `raw_data` 
WHERE disaster_category IS NOT NULL
ORDER BY create_time DESC 
LIMIT 10;

