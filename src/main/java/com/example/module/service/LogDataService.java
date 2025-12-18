package com.example.module.service;

import com.example.module.entity.mongodb.LogData;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.util.List;

public interface LogDataService {
    
    LogData saveLogData(LogData logData);
    
    LogData getLogDataById(String id);
    
    List<LogData> getAllLogData();
    
    Page<LogData> getLogDataByPage(Pageable pageable);
    
    List<LogData> getLogDataByLevel(String level);
    
    List<LogData> getLogDataByTimeRange(LocalDateTime start, LocalDateTime end);
    
    void deleteLogData(String id);
    
    long countByLevel(String level);
}