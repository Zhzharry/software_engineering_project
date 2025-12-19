-- 为MySQL的raw_data表添加灾情类别和地图可视化字段
-- 执行此脚本以更新表结构

USE zaiqing;

-- 添加灾情类别字段
ALTER TABLE `raw_data` 
ADD COLUMN IF NOT EXISTS `disaster_category` VARCHAR(100) DEFAULT NULL COMMENT '灾情类别：震情、人员伤亡及失踪、房屋破坏、生命线工程灾情、次生灾害' AFTER `data_type`,
ADD COLUMN IF NOT EXISTS `processed` TINYINT(1) DEFAULT 0 COMMENT '是否已处理：0-未处理，1-已处理' AFTER `status`,
ADD COLUMN IF NOT EXISTS `geo_code` VARCHAR(12) DEFAULT NULL COMMENT '地理码（12位行政区划代码）' AFTER `disaster_category`,
ADD COLUMN IF NOT EXISTS `disaster_id` VARCHAR(36) DEFAULT NULL COMMENT '36位灾情ID' AFTER `geo_code`,
ADD COLUMN IF NOT EXISTS `disaster_date_time` DATETIME DEFAULT NULL COMMENT '灾情发生时间' AFTER `disaster_id`,
ADD COLUMN IF NOT EXISTS `disaster_subcategory` VARCHAR(100) DEFAULT NULL COMMENT '灾情子类' AFTER `disaster_category`,
ADD COLUMN IF NOT EXISTS `disaster_indicator` VARCHAR(100) DEFAULT NULL COMMENT '灾情指标' AFTER `disaster_subcategory`,
ADD COLUMN IF NOT EXISTS `source_category` VARCHAR(100) DEFAULT NULL COMMENT '来源类别' AFTER `source_system`,
ADD COLUMN IF NOT EXISTS `carrier_type` VARCHAR(50) DEFAULT NULL COMMENT '载体类型：文字、图像、音频、视频' AFTER `source_category`,
ADD COLUMN IF NOT EXISTS `decoded_description` TEXT COMMENT '解码后的完整描述' AFTER `carrier_type`,
ADD INDEX IF NOT EXISTS `idx_disaster_category` (`disaster_category`),
ADD INDEX IF NOT EXISTS `idx_processed` (`processed`),
ADD INDEX IF NOT EXISTS `idx_geo_code` (`geo_code`),
ADD INDEX IF NOT EXISTS `idx_disaster_id` (`disaster_id`),
ADD INDEX IF NOT EXISTS `idx_disaster_date_time` (`disaster_date_time`);

-- 如果字段已存在，使用以下语句修改
-- ALTER TABLE `raw_data` MODIFY COLUMN `disaster_category` VARCHAR(100) DEFAULT NULL COMMENT '灾情类别：震情、人员伤亡及失踪、房屋破坏、生命线工程灾情、次生灾害';

