package com.example.module.util;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

/**
 * 灾情ID解码器
 * 用于解析36位的一体化编码ID
 * 
 * ID结构：
 * - 第1-12位：地理码（地理信息编码）
 * - 第13-26位：时间码（YYYYMMDDHHMMSS）
 * - 第27位：来源码大类
 * - 第28-29位：来源码子类
 * - 第30位：载体码（0文字, 1图像, 2音频, 3视频）
 * - 第31-36位：灾情码（6位）
 *   - 第31位：灾害大类代码
 *   - 第32-33位：灾害子类代码
 *   - 第34-36位：灾情指标代码
 */
public class DisasterIdDecoder {

    private static final int TOTAL_LENGTH = 36;
    private static final int GEO_CODE_LENGTH = 12;
    private static final int TIME_CODE_LENGTH = 14;
    private static final int SOURCE_CATEGORY_POS = 26; // 第27位（0-based index: 26）
    private static final int SOURCE_SUBCATEGORY_START = 27; // 第28-29位
    private static final int CARRIER_POS = 29; // 第30位
    private static final int DISASTER_CODE_START = 30; // 第31-36位

    /**
     * 解码36位ID
     * 
     * @param id 36位一体化编码ID
     * @return 解码结果对象
     * @throws IllegalArgumentException 如果ID格式不正确
     */
    public static DecodedId decode(String id) {
        if (id == null || id.length() != TOTAL_LENGTH) {
            throw new IllegalArgumentException("ID长度必须为36位，当前长度: " + (id == null ? 0 : id.length()));
        }

        // 验证ID只包含数字
        if (!id.matches("\\d+")) {
            throw new IllegalArgumentException("ID必须只包含数字");
        }

        DecodedId decoded = new DecodedId();
        decoded.setOriginalId(id);

        try {
            // 解析地理码（第1-12位）
            String geoCode = id.substring(0, GEO_CODE_LENGTH);
            decoded.setGeoCode(geoCode);

            // 解析时间码（第13-26位）
            String timeCode = id.substring(GEO_CODE_LENGTH, GEO_CODE_LENGTH + TIME_CODE_LENGTH);
            decoded.setTimeCode(timeCode);
            
            // 解析时间
            LocalDateTime dateTime = parseDateTime(timeCode);
            decoded.setDateTime(dateTime);

            // 解析来源码（第27-29位）
            String sourceCategoryCode = id.substring(SOURCE_CATEGORY_POS, SOURCE_CATEGORY_POS + 1);
            String sourceSubcategoryCode = id.substring(SOURCE_SUBCATEGORY_START, SOURCE_SUBCATEGORY_START + 2);
            decoded.setSourceCategoryCode(sourceCategoryCode);
            decoded.setSourceSubcategoryCode(sourceSubcategoryCode);
            decoded.setSourceCategoryName(DisasterCodeConstants.getSourceCategoryName(sourceCategoryCode));
            decoded.setSourceSubcategoryName(DisasterCodeConstants.getSourceSubcategoryName(sourceSubcategoryCode));

            // 解析载体码（第30位）
            String carrierCode = id.substring(CARRIER_POS, CARRIER_POS + 1);
            decoded.setCarrierCode(carrierCode);
            decoded.setCarrierName(DisasterCodeConstants.getCarrierName(carrierCode));

            // 解析灾情码（第31-36位）
            String disasterCode = id.substring(DISASTER_CODE_START);
            decoded.setDisasterCode(disasterCode);
            
            // 解析灾情码的各个部分
            String categoryCode = disasterCode.substring(0, 1);
            String subcategoryCode = disasterCode.substring(1, 3);
            String indicatorCode = disasterCode.substring(3);
            
            decoded.setDisasterCategoryCode(categoryCode);
            decoded.setDisasterSubcategoryCode(subcategoryCode);
            decoded.setDisasterIndicatorCode(indicatorCode);
            
            decoded.setDisasterCategoryName(DisasterCodeConstants.getCategoryName(categoryCode));
            decoded.setDisasterSubcategoryName(DisasterCodeConstants.getSubcategoryName(categoryCode, subcategoryCode));
            decoded.setDisasterIndicatorName(DisasterCodeConstants.getIndicatorName(categoryCode, indicatorCode));

            // 生成完整描述
            String description = generateDescription(decoded);
            decoded.setDescription(description);

        } catch (Exception e) {
            throw new IllegalArgumentException("ID解析失败: " + e.getMessage(), e);
        }

        return decoded;
    }

    /**
     * 解析时间码为LocalDateTime
     * 格式：YYYYMMDDHHMMSS
     */
    private static LocalDateTime parseDateTime(String timeCode) {
        try {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMddHHmmss");
            return LocalDateTime.parse(timeCode, formatter);
        } catch (DateTimeParseException e) {
            throw new IllegalArgumentException("时间码格式错误: " + timeCode, e);
        }
    }

    /**
     * 生成完整描述
     */
    private static String generateDescription(DecodedId decoded) {
        StringBuilder sb = new StringBuilder();
        sb.append(decoded.getDisasterCategoryName());
        if (!decoded.getDisasterSubcategoryName().equals("未知")) {
            sb.append(" - ").append(decoded.getDisasterSubcategoryName());
        }
        if (!decoded.getDisasterIndicatorName().equals("未知")) {
            sb.append(" - ").append(decoded.getDisasterIndicatorName());
        }
        sb.append(" | 来源: ").append(decoded.getSourceSubcategoryName());
        sb.append(" | 载体: ").append(decoded.getCarrierName());
        sb.append(" | 时间: ").append(decoded.getDateTime().toString());
        return sb.toString();
    }

    /**
     * 验证ID格式
     */
    public static boolean isValid(String id) {
        if (id == null || id.length() != TOTAL_LENGTH) {
            return false;
        }
        if (!id.matches("\\d+")) {
            return false;
        }
        try {
            String timeCode = id.substring(GEO_CODE_LENGTH, GEO_CODE_LENGTH + TIME_CODE_LENGTH);
            parseDateTime(timeCode);
            return true;
        } catch (Exception e) {
            return false;
        }
    }
}

