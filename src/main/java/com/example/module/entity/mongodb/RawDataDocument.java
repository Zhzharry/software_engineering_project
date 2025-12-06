package com.example.module.entity.mongodb;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.time.LocalDateTime;
import java.util.Map;

@Data
@Document(collection = "raw_data")
public class RawDataDocument {
    @Id
    private String id;
    
    @Field("raw_data_id")
    private Long rawDataId;
    
    @Field("data_name")
    private String dataName;
    
    @Field("data_type")
    private String dataType;
    
    @Field("raw_content")
    private Map<String, Object> rawContent;
    
    @Field("metadata")
    private Map<String, Object> metadata;
    
    @Field("create_time")
    private LocalDateTime createTime;
    
    @Field("update_time")
    private LocalDateTime updateTime;
}