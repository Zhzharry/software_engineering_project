package com.example.module.entity.mongodb;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.time.LocalDateTime;
import java.util.Map;

@Data
@Document(collection = "log_data")
public class LogData {
    @Id
    private String id;
    
    @Field("level")
    private String level;
    
    @Field("message")
    private String message;
    
    @Field("timestamp")
    private LocalDateTime timestamp;
    
    @Field("metadata")
    private Map<String, Object> metadata;
    
    @Field("create_time")
    private LocalDateTime createTime;
}