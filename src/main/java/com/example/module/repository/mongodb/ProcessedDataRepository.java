package com.example.module.repository.mongodb;

import com.example.module.entity.mongodb.ProcessedData;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface ProcessedDataRepository extends MongoRepository<ProcessedData, String> {
    
    List<ProcessedData> findByProcessType(String processType);
    
    List<ProcessedData> findByRawDataId(String rawDataId);
    
    List<ProcessedData> findByConfidenceScoreGreaterThan(Double minScore);
    
    @Query("{ 'processed_content.?0' : ?1 }")
    List<ProcessedData> findByProcessedContentField(String field, Object value);
    
    @Query("{ 'confidence_score' : { $gte: ?0, $lte: ?1 } }")
    List<ProcessedData> findByConfidenceScoreBetween(Double minScore, Double maxScore);
}