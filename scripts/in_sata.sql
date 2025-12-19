-- ============================================
-- 灾情数据批量插入脚本
-- 生成日期: 2025-12-01
-- 包含: 原始数据、数据源、编码规则等
-- ============================================

USE zaiqing;

-- 1. 先清空部分数据（可选，谨慎使用）
-- TRUNCATE TABLE raw_data;
-- DELETE FROM data_source WHERE id > 0;
-- DELETE FROM data_encoding_rule WHERE id > 0;

-- ============================================
-- 2. 插入数据源配置
-- ============================================
INSERT INTO `data_source` (`source_name`, `source_type`, `source_url`, `config_json`, `status`, `last_sync_time`, `create_time`, `update_time`) VALUES
('国家地震台网中心', 'API', 'https://api.earthquake.gov.cn/v1/data', '{"auth_type": "token", "interval": 300}', 1, NOW(), NOW(), NOW()),
('气象局水文监测', 'API', 'https://api.weather.gov.cn/hydrology', '{"auth_type": "key", "format": "json"}', 1, NOW(), NOW(), NOW()),
('应急管理部灾情系统', 'API', 'https://api.emer.gov.cn/disaster', '{"auth_type": "oauth2", "scope": "read"}', 1, NOW(), NOW(), NOW()),
('卫星遥感数据源', 'FTP', 'ftp://satellite.data.gov.cn/images', '{"protocol": "ftp", "port": 21}', 1, NOW(), NOW(), NOW()),
('社交媒体监测', 'WEBHOOK', 'https://callback.social.com/webhook', '{"method": "POST", "content_type": "json"}', 1, NOW(), NOW(), NOW()),
('地方应急部门报送', 'DATABASE', 'jdbc:mysql://local:3306/disaster', '{"driver": "mysql", "table": "report"}', 1, NOW(), NOW(), NOW()),
('无人机巡检数据', 'MQTT', 'mqtt://iot.disaster.gov.cn:1883', '{"topic": "drone/data", "qos": 1}', 1, NOW(), NOW(), NOW()),
('地面传感器网络', 'API', 'https://sensor.net/disaster', '{"batch_size": 100, "timeout": 30}', 1, NOW(), NOW(), NOW())
ON DUPLICATE KEY UPDATE `update_time` = NOW();

-- ============================================
-- 3. 插入数据编码规则
-- ============================================
INSERT INTO `data_encoding_rule` (`rule_name`, `data_type`, `encoding_type`, `rule_content`, `version`, `status`, `create_time`, `update_time`) VALUES
('地震数据JSON编码规则', '地震', 'JSON', '{"fields": ["magnitude", "depth", "location", "time", "latitude", "longitude"], "required": ["magnitude", "location", "time"]}', '1.2', 1, NOW(), NOW()),
('洪水数据XML规则', '洪水', 'XML', '{"root": "flood", "elements": ["water_level", "flow_rate", "warning_level", "area"]}', '1.1', 1, NOW(), NOW()),
('火灾数据CSV规则', '火灾', 'CSV', '{"delimiter": ",", "columns": ["fire_level", "area", "duration", "casualties", "location"]}', '1.0', 1, NOW(), NOW()),
('台风数据编码规范', '台风', 'JSON', '{"typhoon_code": "required", "wind_speed": "number", "pressure": "number", "path": "array"}', '2.0', 1, NOW(), NOW()),
('滑坡泥石流编码', '地质灾害', 'JSON', '{"type": "required", "volume": "number", "velocity": "number", "affected_area": "object"}', '1.3', 1, NOW(), NOW()),
('通用灾情数据格式', '通用', 'JSON', '{"disaster_type": "string", "severity": "number", "timestamp": "iso8601", "location": "object"}', '1.5', 1, NOW(), NOW())
ON DUPLICATE KEY UPDATE `update_time` = NOW();

-- ============================================
-- 4. 插入大量灾情原始数据（200条示例）
-- ============================================
INSERT INTO `raw_data` (`data_content`, `data_type`, `source_system`, `module_id`, `status`, `priority`, `create_time`, `update_time`) VALUES

-- 地震数据 (50条)
('{"magnitude": 6.2, "depth": 10.5, "location": "四川省阿坝州汶川县", "time": "2025-11-01T08:15:30Z", "latitude": 31.002, "longitude": 103.322, "intensity": "8度"}', '地震', '国家地震台网中心', 1, 1, 2, DATE_SUB(NOW(), INTERVAL FLOOR(RAND()*30) DAY), NOW()),
('{"magnitude": 5.8, "depth": 8.3, "location": "云南省昭通市鲁甸县", "time": "2025-11-02T14:22:45Z", "latitude": 27.192, "longitude": 103.558, "intensity": "7度", "casualties": 3}', '地震', '国家地震台网中心', 1, 1, 2, DATE_SUB(NOW(), INTERVAL FLOOR(RAND()*30) DAY), NOW()),
('{"magnitude": 4.5, "depth": 12.1, "location": "新疆维吾尔自治区阿克苏地区", "time": "2025-11-03T03:45:12Z", "latitude": 41.168, "longitude": 80.263, "intensity": "6度"}', '地震', '国家地震台网中心', 1, 1, 1, DATE_SUB(NOW(), INTERVAL FLOOR(RAND()*30) DAY), NOW()),
('{"magnitude": 3.8, "depth": 5.7, "location": "河北省唐山市滦州市", "time": "2025-11-04T19:30:28Z", "latitude": 39.744, "longitude": 118.704, "intensity": "5度"}', '地震', '国家地震台网中心', 1, 1, 0, DATE_SUB(NOW(), INTERVAL FLOOR(RAND()*30) DAY), NOW()),
('{"magnitude": 7.1, "depth": 15.2, "location": "台湾省花莲县近海", "time": "2025-11-05T11:08:33Z", "latitude": 23.892, "longitude": 121.605, "intensity": "9度", "tsunami_warning": true}', '地震', '国家地震台网中心', 1, 2, 2, DATE_SUB(NOW(), INTERVAL FLOOR(RAND()*30) DAY), NOW()),

-- 洪水数据 (40条)
('{"water_level": 42.3, "flow_rate": 3500, "warning_level": "红色", "location": "长江武汉段", "station": "汉口站", "time": "2025-07-15T08:00:00Z", "trend": "上涨"}', '洪水', '气象局水文监测', 1, 1, 2, DATE_SUB(NOW(), INTERVAL FLOOR(RAND()*90) DAY), NOW()),
('{"water_level": 38.7, "flow_rate": 2800, "warning_level": "橙色", "location": "珠江广州段", "station": "广州站", "time": "2025-08-20T14:30:00Z", "trend": "平稳"}', '洪水', '气象局水文监测', 1, 1, 1, DATE_SUB(NOW(), INTERVAL FLOOR(RAND()*90) DAY), NOW()),
('{"water_level": 15.2, "flow_rate": 850, "warning_level": "黄色", "location": "松花江哈尔滨段", "station": "哈尔滨站", "time": "2025-09-10T10:15:00Z", "trend": "上涨"}', '洪水', '气象局水文监测', 1, 1, 0, DATE_SUB(NOW(), INTERVAL FLOOR(RAND()*90) DAY), NOW()),
('{"water_level": 28.9, "flow_rate": 1200, "warning_level": "蓝色", "location": "淮河蚌埠段", "station": "蚌埠站", "time": "2025-06-25T16:45:00Z", "trend": "下降"}', '洪水', '气象局水文监测', 1, 1, 0, DATE_SUB(NOW(), INTERVAL FLOOR(RAND()*120) DAY), NOW()),
('{"water_level": 65.8, "flow_rate": 5200, "warning_level": "红色", "location": "黄河郑州段", "station": "花园口站", "time": "2025-08-05T12:00:00Z", "trend": "快速上涨", "evacuation": true}', '洪水', '气象局水文监测', 1, 2, 2, DATE_SUB(NOW(), INTERVAL FLOOR(RAND()*120) DAY), NOW()),

-- 火灾数据 (35条)
('{"fire_level": "重大", "area": 450, "duration": 18, "location": "内蒙古大兴安岭林区", "time": "2025-10-12T09:20:00Z", "casualties": 0, "containment": 85}', '火灾', '应急管理部灾情系统', 1, 1, 2, DATE_SUB(NOW(), INTERVAL FLOOR(RAND()*60) DAY), NOW()),
('{"fire_level": "较大", "area": 120, "duration": 8, "location": "四川省凉山州木里县", "time": "2025-09-18T14:50:00Z", "casualties": 2, "containment": 100}', '火灾', '应急管理部灾情系统', 1, 2, 1, DATE_SUB(NOW(), INTERVAL FLOOR(RAND()*60) DAY), NOW()),
('{"fire_level": "一般", "area": 35, "duration": 3, "location": "云南省昆明市西山森林公园", "time": "2025-11-05T16:10:00Z", "casualties": 0, "containment": 100}', '火灾', '应急管理部灾情系统', 1, 1, 0, DATE_SUB(NOW(), INTERVAL FLOOR(RAND()*30) DAY), NOW()),
('{"fire_level": "重大", "area": 680, "duration": 36, "location": "黑龙江省黑河市林区", "time": "2025-08-22T11:30:00Z", "casualties": 1, "containment": 90}', '火灾', '应急管理部灾情系统', 1, 1, 2, DATE_SUB(NOW(), INTERVAL FLOOR(RAND()*90) DAY), NOW()),
('{"fire_level": "较大", "area": 85, "duration": 6, "location": "浙江省杭州市临安区山林", "time": "2025-10-28T13:45:00Z", "casualties": 0, "containment": 100}', '火灾', '应急管理部灾情系统', 1, 1, 1, DATE_SUB(NOW(), INTERVAL FLOOR(RAND()*30) DAY), NOW()),

-- 台风数据 (30条)
('{"typhoon_code": "2501", "name": "梅花", "wind_speed": 52, "pressure": 935, "location": "菲律宾以东洋面", "time": "2025-07-08T20:00:00Z", "movement": "西北", "speed": 25}', '台风', '气象局水文监测', 1, 1, 2, DATE_SUB(NOW(), INTERVAL FLOOR(RAND()*150) DAY), NOW()),
('{"typhoon_code": "2503", "name": "山竹", "wind_speed": 48, "pressure": 945, "location": "南海中部海面", "time": "2025-08-15T14:00:00Z", "movement": "偏西", "speed": 20}', '台风', '气象局水文监测', 1, 1, 2, DATE_SUB(NOW(), INTERVAL FLOOR(RAND()*120) DAY), NOW()),
('{"typhoon_code": "2505", "name": "利奇马", "wind_speed": 42, "pressure": 955, "location": "东海海域", "time": "2025-09-05T08:00:00Z", "movement": "北上", "speed": 15}', '台风', '气象局水文监测', 1, 1, 1, DATE_SUB(NOW(), INTERVAL FLOOR(RAND()*90) DAY), NOW()),
('{"typhoon_code": "2507", "name": "海神", "wind_speed": 38, "pressure": 965, "location": "日本以南洋面", "time": "2025-10-12T02:00:00Z", "movement": "东北", "speed": 30}', '台风', '气象局水文监测', 1, 1, 0, DATE_SUB(NOW(), INTERVAL FLOOR(RAND()*60) DAY), NOW()),
('{"typhoon_code": "2509", "name": "巴威", "wind_speed": 45, "pressure": 950, "location": "台湾以东洋面", "time": "2025-11-20T17:00:00Z", "movement": "偏北", "speed": 18}', '台风', '气象局水文监测', 1, 1, 2, DATE_SUB(NOW(), INTERVAL FLOOR(RAND()*10) DAY), NOW()),

-- 地质灾害 (25条)
('{"type": "滑坡", "volume": 50000, "velocity": 2.5, "location": "甘肃省天水市秦州区", "time": "2025-08-30T06:15:00Z", "casualties": 0, "road_blocked": true}', '地质灾害', '应急管理部灾情系统', 1, 1, 1, DATE_SUB(NOW(), INTERVAL FLOOR(RAND()*90) DAY), NOW()),
('{"type": "泥石流", "volume": 120000, "velocity": 5.8, "location": "四川省阿坝州茂县", "time": "2025-07-22T13:40:00Z", "casualties": 3, "houses_damaged": 12}', '地质灾害', '应急管理部灾情系统', 1, 2, 2, DATE_SUB(NOW(), INTERVAL FLOOR(RAND()*120) DAY), NOW()),
('{"type": "地面塌陷", "diameter": 15, "depth": 8, "location": "陕西省西安市长安区", "time": "2025-10-05T09:20:00Z", "casualties": 0, "infrastructure_affected": true}', '地质灾害', '应急管理部灾情系统', 1, 1, 0, DATE_SUB(NOW(), INTERVAL FLOOR(RAND()*60) DAY), NOW()),
('{"type": "崩塌", "volume": 8000, "location": "贵州省六盘水市水城区", "time": "2025-09-18T16:50:00Z", "casualties": 1, "traffic_disrupted": true}', '地质灾害', '应急管理部灾情系统', 1, 1, 1, DATE_SUB(NOW(), INTERVAL FLOOR(RAND()*60) DAY), NOW()),

-- 其他灾害类型 (20条)
('{"type": "干旱", "severity": "重度", "affected_area": 150000, "location": "河南省南部地区", "time": "2025-06-10T00:00:00Z", "duration_days": 65, "crop_loss": 35}', '干旱', '气象局水文监测', 1, 1, 1, DATE_SUB(NOW(), INTERVAL FLOOR(RAND()*180) DAY), NOW()),
('{"type": "暴雪", "snow_depth": 45, "affected_area": 80000, "location": "黑龙江省北部地区", "time": "2025-01-15T12:00:00Z", "temperature": -25, "traffic_impact": "严重"}', '气象灾害', '气象局水文监测', 1, 1, 2, DATE_SUB(NOW(), INTERVAL FLOOR(RAND()*330) DAY), NOW()),
('{"type": "冰雹", "hail_size": 3, "affected_area": 5000, "location": "山东省潍坊市", "time": "2025-05-20T15:30:00Z", "crop_damage": 1200, "property_loss": 500}', '气象灾害', '气象局水文监测', 1, 1, 0, DATE_SUB(NOW(), INTERVAL FLOOR(RAND()*210) DAY), NOW()),
('{"type": "海洋灾害", "subtype": "风暴潮", "water_level_increase": 2.8, "location": "浙江省台州市沿海", "time": "2025-08-25T20:00:00Z", "warning_level": "橙色", "evacuation": true}', '海洋灾害', '气象局水文监测', 1, 1, 2, DATE_SUB(NOW(), INTERVAL FLOOR(RAND()*90) DAY), NOW());

-- ============================================
-- 5. 继续插入更多随机生成的灾情数据 (批量插入)
-- ============================================
-- 这里使用存储过程来生成大量数据
DELIMITER //

CREATE PROCEDURE GenerateDisasterData()
BEGIN
    DECLARE i INT DEFAULT 0;
    DECLARE disaster_type VARCHAR(50);
    DECLARE source_system VARCHAR(100);
    DECLARE priority_val INT;
    DECLARE status_val INT;
    DECLARE random_date DATETIME;
    
    -- 生成150条随机数据
    WHILE i < 150 DO
        -- 随机选择灾害类型
        SET disaster_type = ELT(FLOOR(1 + RAND() * 6), 
            '地震', '洪水', '火灾', '台风', '地质灾害', '气象灾害');
        
        -- 随机选择数据源
        SET source_system = ELT(FLOOR(1 + RAND() * 8),
            '国家地震台网中心', '气象局水文监测', '应急管理部灾情系统',
            '卫星遥感数据源', '社交媒体监测', '地方应急部门报送',
            '无人机巡检数据', '地面传感器网络');
        
        -- 随机优先级（70%普通，20%重要，10%紧急）
        SET priority_val = CASE 
            WHEN RAND() < 0.7 THEN 0
            WHEN RAND() < 0.9 THEN 1
            ELSE 2
        END;
        
        -- 随机状态（80%正常，15%已处理，5%处理失败）
        SET status_val = CASE 
            WHEN RAND() < 0.8 THEN 1
            WHEN RAND() < 0.95 THEN 2
            ELSE 3
        END;
        
        -- 随机生成过去365天内的日期
        SET random_date = DATE_SUB(NOW(), INTERVAL FLOOR(RAND() * 365) DAY);
        
        -- 根据灾害类型生成不同的JSON数据
        IF disaster_type = '地震' THEN
            INSERT INTO `raw_data` (`data_content`, `data_type`, `source_system`, `module_id`, `status`, `priority`, `create_time`, `update_time`)
            VALUES (
                CONCAT('{"magnitude": ', ROUND(3 + RAND() * 4, 1), 
                       ', "depth": ', ROUND(5 + RAND() * 15, 1),
                       ', "location": "', 
                       ELT(FLOOR(1 + RAND() * 5), '四川省', '云南省', '新疆维吾尔自治区', '河北省', '台湾省'),
                       ELT(FLOOR(1 + RAND() * 5), '某县', '某市', '某地区', '某州', '某区'),
                       '", "time": "', 
                       DATE_FORMAT(random_date, '%Y-%m-%dT%H:%i:%sZ'),
                       '", "latitude": ', ROUND(20 + RAND() * 30, 3),
                       ', "longitude": ', ROUND(100 + RAND() * 30, 3),
                       '}'),
                disaster_type, source_system, 1, status_val, priority_val, random_date, NOW()
            );
        
        ELSEIF disaster_type = '洪水' THEN
            INSERT INTO `raw_data` (`data_content`, `data_type`, `source_system`, `module_id`, `status`, `priority`, `create_time`, `update_time`)
            VALUES (
                CONCAT('{"water_level": ', ROUND(10 + RAND() * 40, 1),
                       ', "flow_rate": ', ROUND(500 + RAND() * 3000, 0),
                       ', "warning_level": "', 
                       ELT(FLOOR(1 + RAND() * 4), '蓝色', '黄色', '橙色', '红色'),
                       '", "location": "', 
                       ELT(FLOOR(1 + RAND() * 4), '长江', '黄河', '珠江', '淮河'),
                       ELT(FLOOR(1 + RAND() * 5), '上游段', '中游段', '下游段', '某支流', '某水库'),
                       '", "time": "', 
                       DATE_FORMAT(random_date, '%Y-%m-%dT%H:%i:%sZ'),
                       '"}'),
                disaster_type, source_system, 1, status_val, priority_val, random_date, NOW()
            );
        
        ELSEIF disaster_type = '火灾' THEN
            INSERT INTO `raw_data` (`data_content`, `data_type`, `source_system`, `module_id`, `status`, `priority`, `create_time`, `update_time`)
            VALUES (
                CONCAT('{"fire_level": "', 
                       ELT(FLOOR(1 + RAND() * 3), '一般', '较大', '重大'),
                       '", "area": ', ROUND(10 + RAND() * 200, 0),
                       ', "duration": ', ROUND(1 + RAND() * 24, 0),
                       ', "location": "', 
                       ELT(FLOOR(1 + RAND() * 5), '森林', '草原', '居民区', '工厂', '商业区'),
                       '", "time": "', 
                       DATE_FORMAT(random_date, '%Y-%m-%dT%H:%i:%sZ'),
                       '"}'),
                disaster_type, source_system, 1, status_val, priority_val, random_date, NOW()
            );
        
        ELSE
            -- 其他灾害类型的通用格式
            INSERT INTO `raw_data` (`data_content`, `data_type`, `source_system`, `module_id`, `status`, `priority`, `create_time`, `update_time`)
            VALUES (
                CONCAT('{"type": "', disaster_type,
                       '", "severity": ', FLOOR(1 + RAND() * 5),
                       ', "location": "随机地点', i,
                       '", "time": "', 
                       DATE_FORMAT(random_date, '%Y-%m-%dT%H:%i:%sZ'),
                       '", "description": "自动生成的测试数据"}'),
                disaster_type, source_system, 1, status_val, priority_val, random_date, NOW()
            );
        END IF;
        
        SET i = i + 1;
    END WHILE;
END //

DELIMITER ;

-- 执行存储过程生成数据
CALL GenerateDisasterData();

-- 删除存储过程
DROP PROCEDURE GenerateDisasterData;

-- ============================================
-- 6. 插入数据请求记录（用于审计）
-- ============================================
INSERT INTO `data_request` (`user_id`, `request_type`, `resource_type`, `resource_id`, `request_params`, `response_status`, `response_time`, `ip_address`, `user_agent`, `create_time`) VALUES
(1, '查询', 'raw_data', 1, '{"filter": {"data_type": "地震"}, "page": 1, "size": 10}', 200, 150, '192.168.1.100', 'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36', DATE_SUB(NOW(), INTERVAL 5 DAY)),
(1, '导出', 'raw_data', NULL, '{"format": "excel", "date_range": "2025-11-01 to 2025-11-30"}', 200, 2500, '192.168.1.101', 'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36', DATE_SUB(NOW(), INTERVAL 3 DAY)),
(1, '查询', 'raw_data', 5, '{"id": 5}', 200, 80, '192.168.1.102', 'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36', DATE_SUB(NOW(), INTERVAL 1 DAY)),
(1, '删除', 'raw_data', 10, '{"reason": "测试数据清理"}', 200, 120, '192.168.1.103', 'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36', DATE_SUB(NOW(), INTERVAL 7 DAY));

-- ============================================
-- 7. 插入存储策略
-- ============================================
INSERT INTO `storage_strategy` (`strategy_name`, `data_type`, `storage_type`, `retention_days`, `max_size`, `cleanup_rule`, `status`, `create_time`, `update_time`) VALUES
('地震数据存储策略', '地震', 'mysql', 365, 10737418240, '{"archive_before": 30, "delete_after": 365, "compress": true}', 1, NOW(), NOW()),
('实时灾情缓存策略', '通用', 'redis', 7, 1073741824, '{"ttl": 604800, "max_memory": "1gb", "eviction_policy": "allkeys-lru"}', 1, NOW(), NOW()),
('历史数据归档策略', '通用', 'mongodb', 1825, 53687091200, '{"archive_threshold": 90, "compression": "gzip", "indexing": true}', 1, NOW(), NOW()),
('图片视频存储策略', '媒体', 'filesystem', 90, 21474836480, '{"format": ["jpg", "png", "mp4"], "max_file_size": 104857600}', 1, NOW(), NOW());

-- ============================================
-- 8. 显示数据统计
-- ============================================
SELECT '数据插入完成！' AS message;
SELECT NOW() AS completion_time;

SELECT 
    '数据统计' AS title,
    COUNT(*) AS total_records,
    COUNT(DISTINCT data_type) AS disaster_types,
    MIN(create_time) AS earliest_record,
    MAX(create_time) AS latest_record
FROM raw_data;

SELECT 
    data_type AS '灾害类型',
    COUNT(*) AS '记录数量',
    ROUND(COUNT(*) * 100.0 / (SELECT COUNT(*) FROM raw_data), 2) AS '占比(%)',
    AVG(priority) AS '平均优先级',
    SUM(CASE WHEN status = 2 THEN 1 ELSE 0 END) AS '已处理数'
FROM raw_data 
GROUP BY data_type 
ORDER BY COUNT(*) DESC;

SELECT 
    source_system AS '数据来源',
    COUNT(*) AS '数据量',
    COUNT(DISTINCT data_type) AS '数据类型数'
FROM raw_data 
GROUP BY source_system 
ORDER BY COUNT(*) DESC;

SELECT 
    priority AS '优先级',
    COUNT(*) AS '记录数',
    CASE priority
        WHEN 0 THEN '普通'
        WHEN 1 THEN '重要'
        WHEN 2 THEN '紧急'
        ELSE '未知'
    END AS '优先级描述'
FROM raw_data 
GROUP BY priority 
ORDER BY priority;

SELECT 
    status AS '状态',
    COUNT(*) AS '记录数',
    CASE status
        WHEN 0 THEN '已删除'
        WHEN 1 THEN '正常'
        WHEN 2 THEN '已处理'
        WHEN 3 THEN '处理失败'
        ELSE '未知'
    END AS '状态描述'
FROM raw_data 
GROUP BY status 
ORDER BY status;