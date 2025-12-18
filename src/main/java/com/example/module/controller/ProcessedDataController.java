package com.example.module.controller;

import com.example.module.entity.mongodb.ProcessedData;
import com.example.module.service.ProcessedDataService;
import com.example.module.util.Result;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/processed-data")
@RequiredArgsConstructor
public class ProcessedDataController {
    
    private final ProcessedDataService processedDataService;
    
    @PostMapping
    public Result<ProcessedData> create(@RequestBody ProcessedData processedData) {
        try {
            ProcessedData savedData = processedDataService.save(processedData);
            return Result.success("Processed data created successfully", savedData);
        } catch (Exception e) {
            return Result.error(500, "Failed to create processed data: " + e.getMessage());
        }
    }
    
    @GetMapping("/{id}")
    public Result<ProcessedData> getById(@PathVariable String id) {
        try {
            return processedDataService.findById(id)
                    .map(data -> Result.success("Processed data found", data))
                    .orElse(Result.error(404, "Processed data not found with id: " + id));
        } catch (Exception e) {
            return Result.error(500, "Failed to get processed data: " + e.getMessage());
        }
    }
    
    @GetMapping
    public Result<List<ProcessedData>> getAll() {
        try {
            List<ProcessedData> processedDataList = processedDataService.findAll();
            return Result.success("Processed data retrieved successfully", processedDataList);
        } catch (Exception e) {
            return Result.error(500, "Failed to get processed data: " + e.getMessage());
        }
    }
    
    @GetMapping("/type/{processType}")
    public Result<List<ProcessedData>> getByProcessType(@PathVariable String processType) {
        try {
            List<ProcessedData> processedDataList = processedDataService.findByProcessType(processType);
            return Result.success("Processed data retrieved by process type", processedDataList);
        } catch (Exception e) {
            return Result.error(500, "Failed to get processed data by process type: " + e.getMessage());
        }
    }
    
    @GetMapping("/raw-data/{rawDataId}")
    public Result<List<ProcessedData>> getByRawDataId(@PathVariable String rawDataId) {
        try {
            List<ProcessedData> processedDataList = processedDataService.findByRawDataId(rawDataId);
            return Result.success("Processed data retrieved by raw data id", processedDataList);
        } catch (Exception e) {
            return Result.error(500, "Failed to get processed data by raw data id: " + e.getMessage());
        }
    }
    
    @GetMapping("/confidence/min/{minScore}")
    public Result<List<ProcessedData>> getByMinConfidence(@PathVariable Double minScore) {
        try {
            List<ProcessedData> processedDataList = processedDataService.findByConfidenceScoreGreaterThan(minScore);
            return Result.success("Processed data retrieved by min confidence", processedDataList);
        } catch (Exception e) {
            return Result.error(500, "Failed to get processed data by confidence score: " + e.getMessage());
        }
    }
    
    @GetMapping("/confidence/range")
    public Result<List<ProcessedData>> getByConfidenceRange(
            @RequestParam Double minScore,
            @RequestParam Double maxScore) {
        try {
            List<ProcessedData> processedDataList = processedDataService.findByConfidenceScoreBetween(minScore, maxScore);
            return Result.success("Processed data retrieved by confidence range", processedDataList);
        } catch (Exception e) {
            return Result.error(500, "Failed to get processed data by confidence range: " + e.getMessage());
        }
    }
    
    @GetMapping("/search/field")
    public Result<List<ProcessedData>> searchByField(
            @RequestParam String field,
            @RequestParam String value) {
        try {
            List<ProcessedData> processedDataList = processedDataService.findByProcessedContentField(field, value);
            return Result.success("Processed data retrieved by field search", processedDataList);
        } catch (Exception e) {
            return Result.error(500, "Failed to search processed data by field: " + e.getMessage());
        }
    }
    
    @PutMapping("/{id}/confidence")
    public Result<ProcessedData> updateConfidenceScore(
            @PathVariable String id,
            @RequestParam Double confidenceScore) {
        try {
            ProcessedData updatedData = processedDataService.updateConfidenceScore(id, confidenceScore);
            return Result.success("Confidence score updated successfully", updatedData);
        } catch (Exception e) {
            return Result.error(500, "Failed to update confidence score: " + e.getMessage());
        }
    }
    
    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable String id) {
        try {
            processedDataService.deleteById(id);
            return Result.success("Processed data deleted successfully", null);
        } catch (Exception e) {
            return Result.error(500, "Failed to delete processed data: " + e.getMessage());
        }
    }
}