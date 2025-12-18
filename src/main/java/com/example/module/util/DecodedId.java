package com.example.module.util;

import lombok.Data;
import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 解码后的ID信息
 */
@Data
public class DecodedId implements Serializable {
    
    /**
     * 原始36位ID
     */
    private String originalId;

    /**
     * 地理码（第1-12位）
     */
    private String geoCode;

    /**
     * 时间码（第13-26位，格式：YYYYMMDDHHMMSS）
     */
    private String timeCode;

    /**
     * 解析后的时间
     */
    private LocalDateTime dateTime;

    /**
     * 来源码大类（第27位）
     */
    private String sourceCategoryCode;

    /**
     * 来源码子类（第28-29位）
     */
    private String sourceSubcategoryCode;

    /**
     * 来源码大类名称
     */
    private String sourceCategoryName;

    /**
     * 来源码子类名称
     */
    private String sourceSubcategoryName;

    /**
     * 载体码（第30位）
     */
    private String carrierCode;

    /**
     * 载体名称
     */
    private String carrierName;

    /**
     * 灾情码（第31-36位）
     */
    private String disasterCode;

    /**
     * 灾害大类代码（第31位）
     */
    private String disasterCategoryCode;

    /**
     * 灾害子类代码（第32-33位）
     */
    private String disasterSubcategoryCode;

    /**
     * 灾情指标代码（第34-36位）
     */
    private String disasterIndicatorCode;

    /**
     * 灾害大类名称
     */
    private String disasterCategoryName;

    /**
     * 灾害子类名称
     */
    private String disasterSubcategoryName;

    /**
     * 灾情指标名称
     */
    private String disasterIndicatorName;

    /**
     * 完整描述
     */
    private String description;
}

