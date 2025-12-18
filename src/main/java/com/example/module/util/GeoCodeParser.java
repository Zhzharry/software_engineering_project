package com.example.module.util;

import lombok.Data;

/**
 * 地理码解析器
 * 用于解析12位地理码的层级结构
 * 
 * 结构说明：
 * - 前2位：省、直辖市、自治区行政区划代码
 * - 第3-4位：地市行政区划代码
 * - 第5-6位：县区行政区划代码
 * - 第7-9位：乡镇或街道代码
 * - 第10-12位：行政村或社区代码
 */
public class GeoCodeParser {

    /**
     * 解析12位地理码
     * 
     * @param geoCode 12位地理码
     * @return 解析结果
     */
    public static GeoCodeInfo parse(String geoCode) {
        if (geoCode == null || geoCode.length() != 12) {
            throw new IllegalArgumentException("地理码必须为12位");
        }

        GeoCodeInfo info = new GeoCodeInfo();
        info.setGeoCode(geoCode);
        
        // 解析各个层级
        info.setProvinceCode(geoCode.substring(0, 2));           // 前2位：省
        info.setCityCode(geoCode.substring(2, 4));              // 第3-4位：市
        info.setDistrictCode(geoCode.substring(4, 6));          // 第5-6位：县区
        info.setTownshipCode(geoCode.substring(6, 9));          // 第7-9位：乡镇
        info.setVillageCode(geoCode.substring(9, 12));         // 第10-12位：村/社区
        
        // 构建各级行政区划代码
        info.setProvinceLevelCode(info.getProvinceCode() + "0000000000");  // 省级代码（12位）
        info.setCityLevelCode(info.getProvinceCode() + info.getCityCode() + "00000000");  // 市级代码（12位）
        info.setDistrictLevelCode(info.getProvinceCode() + info.getCityCode() + info.getDistrictCode() + "000000");  // 县级代码（12位）
        info.setTownshipLevelCode(info.getProvinceCode() + info.getCityCode() + info.getDistrictCode() + info.getTownshipCode() + "000");  // 乡镇级代码（12位）
        
        return info;
    }

    /**
     * 验证地理码格式
     */
    public static boolean isValid(String geoCode) {
        if (geoCode == null || geoCode.length() != 12) {
            return false;
        }
        return geoCode.matches("\\d{12}");
    }

    /**
     * 地理码信息类
     */
    @Data
    public static class GeoCodeInfo {
        /**
         * 完整地理码（12位）
         */
        private String geoCode;

        /**
         * 省、直辖市、自治区代码（2位）
         */
        private String provinceCode;

        /**
         * 地市代码（2位）
         */
        private String cityCode;

        /**
         * 县区代码（2位）
         */
        private String districtCode;

        /**
         * 乡镇或街道代码（3位）
         */
        private String townshipCode;

        /**
         * 行政村或社区代码（3位）
         */
        private String villageCode;

        /**
         * 省级完整代码（12位，后10位补0）
         */
        private String provinceLevelCode;

        /**
         * 市级完整代码（12位，后8位补0）
         */
        private String cityLevelCode;

        /**
         * 县级完整代码（12位，后6位补0）
         */
        private String districtLevelCode;

        /**
         * 乡镇级完整代码（12位，后3位补0）
         */
        private String townshipLevelCode;
    }
}

