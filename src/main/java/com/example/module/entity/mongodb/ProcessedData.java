package com.example.module.entity.mongodb;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;
import java.time.LocalDateTime;
import java.util.Map;

@Document(collection = "processed_data")
@Data
public class ProcessedData {
    @Id
    private String id;
    
    @Field("raw_data_id")
    private Long rawDataId;
    
    @Field("processed_content")
    private Map<String, Object> processedContent;
    
    @Field("process_type")
    private String processType;
    
    @Field("confidence_score")
    private Double confidenceScore;
    
    @Field("create_time")
    private LocalDateTime createTime;
    
    @Field("metadata")
    private Map<String, Object> metadata;
}