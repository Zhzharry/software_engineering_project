package com.example.module.repository.mongodb;

import com.example.module.entity.mongodb.LogData;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface LogDataRepository extends MongoRepository<LogData, String> {
    
    List<LogData> findByLevel(String level);
    
    Page<LogData> findByLevel(String level, Pageable pageable);
    
    @Query("{ 'timestamp': { $gte: ?0, $lte: ?1 } }")
    List<LogData> findByTimestampBetween(LocalDateTime start, LocalDateTime end);
    
    @Query("{ 'message': { $regex: ?0, $options: 'i' } }")
    List<LogData> findByMessageLike(String message);
    
    long countByLevel(String level);
}