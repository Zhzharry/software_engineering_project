package com.example.module.controller;

import com.example.module.entity.mongodb.LogData;
import com.example.module.service.LogDataService;
import com.example.module.util.Result;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/log-data")
@RequiredArgsConstructor
public class LogDataController {
    
    private final LogDataService logDataService;
    
    @PostMapping
    public Result<LogData> createLogData(@RequestBody LogData logData) {
        LogData savedData = logDataService.saveLogData(logData);
        return Result.success("Log data created successfully", savedData);
    }
    
    @GetMapping("/{id}")
    public Result<LogData> getLogData(@PathVariable String id) {
        LogData logData = logDataService.getLogDataById(id);
        if (logData != null) {
            return Result.success(logData);
        } else {
            return Result.error(404, "Log data not found");
        }
    }
    
    @GetMapping
    public Result<List<LogData>> getAllLogData() {
        List<LogData> logDataList = logDataService.getAllLogData();
        return Result.success(logDataList);
    }
    
    @GetMapping("/page")
    public Result<Page<LogData>> getLogDataByPage(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<LogData> logDataPage = logDataService.getLogDataByPage(pageable);
        return Result.success(logDataPage);
    }
    
    @GetMapping("/level/{level}")
    public Result<List<LogData>> getLogDataByLevel(@PathVariable String level) {
        List<LogData> logDataList = logDataService.getLogDataByLevel(level);
        return Result.success(logDataList);
    }
    
    @GetMapping("/time-range")
    public Result<List<LogData>> getLogDataByTimeRange(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime start,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime end) {
        List<LogData> logDataList = logDataService.getLogDataByTimeRange(start, end);
        return Result.success(logDataList);
    }
    
    @DeleteMapping("/{id}")
    public Result<Void> deleteLogData(@PathVariable String id) {
        logDataService.deleteLogData(id);
        return Result.success("Log data deleted successfully", null);
    }
    
    @GetMapping("/count/level/{level}")
    public Result<Long> countByLevel(@PathVariable String level) {
        long count = logDataService.countByLevel(level);
        return Result.success(count);
    }
}