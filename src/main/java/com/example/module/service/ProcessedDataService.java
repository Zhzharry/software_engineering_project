package com.example.module.service;

import com.example.module.entity.mongodb.ProcessedData;
import java.util.List;
import java.util.Optional;

public interface ProcessedDataService {
    
    ProcessedData save(ProcessedData processedData);
    
    Optional<ProcessedData> findById(String id);
    
    List<ProcessedData> findAll();
    
    List<ProcessedData> findByProcessType(String processType);
    
    List<ProcessedData> findByRawDataId(String rawDataId);
    
    List<ProcessedData> findByConfidenceScoreGreaterThan(Double minScore);
    
    List<ProcessedData> findByConfidenceScoreBetween(Double minScore, Double maxScore);
    
    List<ProcessedData> findByProcessedContentField(String field, Object value);
    
    void deleteById(String id);
    
    ProcessedData updateConfidenceScore(String id, Double confidenceScore);
}