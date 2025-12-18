package com.example.module.entity.mongodb;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.time.LocalDateTime;
import java.util.Map;

@Data
@Document(collection = "raw_data")
public class RawData {
    @Id
    private String id;

    @Field("module_id")
    private Long moduleId;

    @Field("data_type")
    private String dataType;

    @Field("data_content")
    private String dataContent;

    @Field("content")
    private Map<String, Object> content;

    @Field("source")
    private String source;

    @Field("version")
    private Integer version = 1;

    @Field("create_time")
    private LocalDateTime createTime;

    @Field("update_time")
    private LocalDateTime updateTime;

    @Field("processed")
    private Boolean processed = false;
    
    @Field("expire_time")
    private LocalDateTime expireTime; // 数据过期时间
    
    @Field("retention_days")
    private Integer retentionDays; // 保留天数
    
    // ========== 解码后的字段 ==========
    
    @Field("disaster_id")
    private String disasterId; // 36位灾情ID
    
    @Field("geo_code")
    private String geoCode; // 地理码（12位）
    
    @Field("disaster_date_time")
    private LocalDateTime disasterDateTime; // 灾情发生时间
    
    @Field("source_category")
    private String sourceCategory; // 来源大类
    
    @Field("source_subcategory")
    private String sourceSubcategory; // 来源子类
    
    @Field("carrier_type")
    private String carrierType; // 载体类型（文字/图像/音频/视频）
    
    @Field("disaster_category")
    private String disasterCategory; // 灾害大类
    
    @Field("disaster_subcategory")
    private String disasterSubcategory; // 灾害子类
    
    @Field("disaster_indicator")
    private String disasterIndicator; // 灾情指标
    
    @Field("decoded_description")
    private String decodedDescription; // 解码后的完整描述
}