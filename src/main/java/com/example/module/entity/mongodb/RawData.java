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
}