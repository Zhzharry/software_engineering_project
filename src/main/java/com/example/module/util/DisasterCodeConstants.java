package com.example.module.util;

import java.util.HashMap;
import java.util.Map;

/**
 * 灾情编码对照表常量类
 * 用于存储灾情编码的大类、子类、指标的映射关系
 */
public class DisasterCodeConstants {

    /**
     * 灾害大类代码映射
     * 1位：标识灾害的基本类别
     */
    public static final Map<String, String> DISASTER_CATEGORY_MAP = new HashMap<String, String>() {{
        put("1", "震情");
        put("2", "人员伤亡及失踪");
        put("3", "房屋破坏");
        put("4", "生命线工程灾情");
        put("5", "次生灾害");
    }};

    /**
     * 灾害子类代码映射
     * 2位：进一步细化大类下的具体类型
     */
    public static final Map<String, Map<String, String>> DISASTER_SUBCATEGORY_MAP = new HashMap<String, Map<String, String>>() {{
        // 震情 (1)
        put("1", new HashMap<String, String>() {{
            put("01", "震情信息");
        }});

        // 人员伤亡及失踪 (2)
        put("2", new HashMap<String, String>() {{
            put("01", "死亡");
            put("02", "受伤");
            put("03", "失踪");
        }});

        // 房屋破坏 (3)
        put("3", new HashMap<String, String>() {{
            put("01", "土木");
            put("02", "砖木");
            put("03", "砖混");
            put("04", "框架");
            put("05", "其他");
        }});

        // 生命线工程灾情 (4)
        put("4", new HashMap<String, String>() {{
            put("01", "交通");
            put("02", "供水");
            put("03", "电力");
            put("04", "通信");
            put("05", "燃气");
            put("06", "排水");
            put("07", "其他");
        }});

        // 次生灾害 (5)
        put("5", new HashMap<String, String>() {{
            put("01", "崩塌");
            put("02", "滑坡");
            put("03", "泥石流");
            put("04", "岩溶塌陷");
            put("05", "地裂缝");
            put("06", "地面沉降");
            put("07", "其他");
        }});
    }};

    /**
     * 灾情指标代码映射
     * 3位：描述具体的量化或定性指标
     */
    public static final Map<String, Map<String, String>> DISASTER_INDICATOR_MAP = new HashMap<String, Map<String, String>>() {{
        // 震情 (1) 的指标
        put("1", new HashMap<String, String>() {{
            put("001", "震级");
            put("002", "震中位置");
            put("003", "震源深度");
        }});

        // 人员伤亡及失踪 (2) 的指标
        put("2", new HashMap<String, String>() {{
            put("001", "受灾人数");
            put("002", "受灾程度");
        }});

        // 房屋破坏 (3) 的指标
        put("3", new HashMap<String, String>() {{
            put("001", "一般损坏面积");
            put("002", "严重损坏面积");
            put("003", "受灾程度");
        }});

        // 生命线工程灾情 (4) 的指标
        put("4", new HashMap<String, String>() {{
            put("001", "受灾设施数");
            put("002", "受灾范围");
            put("003", "受灾程度");
        }});

        // 次生灾害 (5) 的指标
        put("5", new HashMap<String, String>() {{
            put("001", "灾害损失");
            put("002", "灾害范围");
            put("003", "受灾程度");
        }});
    }};

    /**
     * 来源码大类映射
     * 第27位
     */
    public static final Map<String, String> SOURCE_CATEGORY_MAP = new HashMap<String, String>() {{
        put("0", "系统内部");
        put("1", "外部接口");
    }};

    /**
     * 来源码子类映射
     * 第28-29位
     */
    public static final Map<String, String> SOURCE_SUBCATEGORY_MAP = new HashMap<String, String>() {{
        put("01", "后方指挥部");
        put("02", "前方指挥部");
        put("03", "现场调查");
        put("04", "互联网感知");
        put("05", "传感器监测");
        put("06", "卫星遥感");
        put("07", "其他");
    }};

    /**
     * 载体码映射
     * 第30位
     */
    public static final Map<String, String> CARRIER_MAP = new HashMap<String, String>() {{
        put("0", "文字");
        put("1", "图像");
        put("2", "音频");
        put("3", "视频");
    }};

    /**
     * 根据大类代码获取大类名称
     */
    public static String getCategoryName(String categoryCode) {
        return DISASTER_CATEGORY_MAP.getOrDefault(categoryCode, "未知");
    }

    /**
     * 根据大类代码和子类代码获取子类名称
     */
    public static String getSubcategoryName(String categoryCode, String subcategoryCode) {
        Map<String, String> subcategoryMap = DISASTER_SUBCATEGORY_MAP.get(categoryCode);
        if (subcategoryMap != null) {
            return subcategoryMap.getOrDefault(subcategoryCode, "未知");
        }
        return "未知";
    }

    /**
     * 根据大类代码和指标代码获取指标名称
     */
    public static String getIndicatorName(String categoryCode, String indicatorCode) {
        Map<String, String> indicatorMap = DISASTER_INDICATOR_MAP.get(categoryCode);
        if (indicatorMap != null) {
            return indicatorMap.getOrDefault(indicatorCode, "未知");
        }
        return "未知";
    }

    /**
     * 根据来源码大类获取名称
     */
    public static String getSourceCategoryName(String sourceCategoryCode) {
        return SOURCE_CATEGORY_MAP.getOrDefault(sourceCategoryCode, "未知");
    }

    /**
     * 根据来源码子类获取名称
     */
    public static String getSourceSubcategoryName(String sourceSubcategoryCode) {
        return SOURCE_SUBCATEGORY_MAP.getOrDefault(sourceSubcategoryCode, "未知");
    }

    /**
     * 根据载体码获取载体名称
     */
    public static String getCarrierName(String carrierCode) {
        return CARRIER_MAP.getOrDefault(carrierCode, "未知");
    }
}

