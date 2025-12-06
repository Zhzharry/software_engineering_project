package com.example.module.service.impl;

import com.example.module.entity.mongodb.ProcessedData;
import com.example.module.repository.mongodb.ProcessedDataRepository;
import com.example.module.service.ProcessedDataService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class ProcessedDataServiceImpl implements ProcessedDataService {
    
    private final ProcessedDataRepository processedDataRepository;
    
    @Override
    public ProcessedData save(ProcessedData processedData) {
        log.info("Saving processed data: {}", processedData);
        return processedDataRepository.save(processedData);
    }
    
    @Override
    public Optional<ProcessedData> findById(String id) {
        log.debug("Finding processed data by id: {}", id);
        return processedDataRepository.findById(id);
    }
    
    @Override
    public List<ProcessedData> findAll() {
        log.debug("Finding all processed data");
        return processedDataRepository.findAll();
    }
    
    @Override
    public List<ProcessedData> findByProcessType(String processType) {
        log.debug("Finding processed data by process type: {}", processType);
        return processedDataRepository.findByProcessType(processType);
    }
    
    @Override
    public List<ProcessedData> findByRawDataId(Long rawDataId) {
        log.debug("Finding processed data by raw data id: {}", rawDataId);
        return processedDataRepository.findByRawDataId(rawDataId);
    }
    
    @Override
    public List<ProcessedData> findByConfidenceScoreGreaterThan(Double minScore) {
        log.debug("Finding processed data with confidence score greater than: {}", minScore);
        return processedDataRepository.findByConfidenceScoreGreaterThan(minScore);
    }
    
    @Override
    public List<ProcessedData> findByConfidenceScoreBetween(Double minScore, Double maxScore) {
        log.debug("Finding processed data with confidence score between {} and {}", minScore, maxScore);
        return processedDataRepository.findByConfidenceScoreBetween(minScore, maxScore);
    }
    
    @Override
    public List<ProcessedData> findByProcessedContentField(String field, Object value) {
        log.debug("Finding processed data by field: {} with value: {}", field, value);
        return processedDataRepository.findByProcessedContentField(field, value);
    }
    
    @Override
    public void deleteById(String id) {
        log.info("Deleting processed data by id: {}", id);
        processedDataRepository.deleteById(id);
    }
    
    @Override
    public ProcessedData updateConfidenceScore(String id, Double confidenceScore) {
        log.info("Updating confidence score for processed data id: {} to {}", id, confidenceScore);
        Optional<ProcessedData> processedDataOpt = processedDataRepository.findById(id);
        if (processedDataOpt.isPresent()) {
            ProcessedData processedData = processedDataOpt.get();
            processedData.setConfidenceScore(confidenceScore);
            return processedDataRepository.save(processedData);
        }
        throw new RuntimeException("ProcessedData not found with id: " + id);
    }
}