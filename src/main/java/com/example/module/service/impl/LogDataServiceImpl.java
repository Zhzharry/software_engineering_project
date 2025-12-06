package com.example.module.service.impl;

import com.example.module.entity.mongodb.LogData;
import com.example.module.repository.mongodb.LogDataRepository;
import com.example.module.service.LogDataService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class LogDataServiceImpl implements LogDataService {
    
    private final LogDataRepository logDataRepository;
    
    @Override
    public LogData saveLogData(LogData logData) {
        logData.setCreateTime(LocalDateTime.now());
        return logDataRepository.save(logData);
    }
    
    @Override
    public LogData getLogDataById(String id) {
        Optional<LogData> logData = logDataRepository.findById(id);
        return logData.orElse(null);
    }
    
    @Override
    public List<LogData> getAllLogData() {
        return logDataRepository.findAll();
    }
    
    @Override
    public Page<LogData> getLogDataByPage(Pageable pageable) {
        return logDataRepository.findAll(pageable);
    }
    
    @Override
    public List<LogData> getLogDataByLevel(String level) {
        return logDataRepository.findByLevel(level);
    }
    
    @Override
    public List<LogData> getLogDataByTimeRange(LocalDateTime start, LocalDateTime end) {
        return logDataRepository.findByTimestampBetween(start, end);
    }
    
    @Override
    public void deleteLogData(String id) {
        logDataRepository.deleteById(id);
    }
    
    @Override
    public long countByLevel(String level) {
        return logDataRepository.countByLevel(level);
    }
}