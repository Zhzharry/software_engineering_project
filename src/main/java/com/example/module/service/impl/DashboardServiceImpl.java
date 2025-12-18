package com.example.module.service.impl;

import com.example.module.entity.mongodb.LogData;
import com.example.module.entity.mongodb.ProcessedData;
import com.example.module.entity.mongodb.RawData;
import com.example.module.entity.mysql.FileEntity;
import com.example.module.repository.mongodb.LogDataRepository;
import com.example.module.repository.mongodb.ProcessedDataRepository;
import com.example.module.repository.mongodb.RawDataRepository;
import com.example.module.repository.mysql.FileRepository;
import com.example.module.repository.mysql.ModuleRepository;
import com.example.module.repository.mysql.UserRepository;
import com.example.module.service.DashboardService;
import com.example.module.util.Result;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class DashboardServiceImpl implements DashboardService {
    
    private final UserRepository userRepository;
    private final ModuleRepository moduleRepository;
    private final RawDataRepository rawDataRepository;
    private final ProcessedDataRepository processedDataRepository;
    private final LogDataRepository logDataRepository;
    private final FileRepository fileRepository;
    
    @Override
    public Result<Map<String, Object>> getDashboardData() {
        Map<String, Object> dashboard = new HashMap<>();
        
        // 基础统计
        dashboard.put("statistics", getDataStatistics().getData());
        
        // 文件统计
        dashboard.put("fileStatistics", getFileStatistics().getData());
        
        // 最近活动
        dashboard.put("recentActivities", getRecentActivities().getData());
        
        // 趋势数据（最近7天）
        dashboard.put("trends", getTrendData());
        
        return Result.success(dashboard);
    }
    
    @Override
    public Result<Map<String, Object>> getDataStatistics() {
        Map<String, Object> stats = new HashMap<>();
        
        // 用户统计
        long userCount = userRepository.count();
        long activeUserCount = userRepository.findAll().stream()
                .filter(u -> u.getStatus() != null && u.getStatus() == 1)
                .count();
        stats.put("totalUsers", userCount);
        stats.put("activeUsers", activeUserCount);
        
        // 模块统计
        long moduleCount = moduleRepository.count();
        long activeModuleCount = moduleRepository.findAll().stream()
                .filter(m -> m.getStatus() != null && m.getStatus() == 1)
                .count();
        stats.put("totalModules", moduleCount);
        stats.put("activeModules", activeModuleCount);
        
        // 原始数据统计
        long rawDataCount = rawDataRepository.count();
        long unprocessedCount = rawDataRepository.findByProcessed(false).size();
        stats.put("totalRawData", rawDataCount);
        stats.put("unprocessedData", unprocessedCount);
        stats.put("processedData", rawDataCount - unprocessedCount);
        
        // 处理数据统计
        long processedDataCount = processedDataRepository.count();
        stats.put("totalProcessedData", processedDataCount);
        
        // 日志统计
        long logCount = logDataRepository.count();
        Map<String, Long> logLevelCount = new HashMap<>();
        logLevelCount.put("INFO", logDataRepository.countByLevel("INFO"));
        logLevelCount.put("WARN", logDataRepository.countByLevel("WARN"));
        logLevelCount.put("ERROR", logDataRepository.countByLevel("ERROR"));
        logLevelCount.put("DEBUG", logDataRepository.countByLevel("DEBUG"));
        stats.put("totalLogs", logCount);
        stats.put("logLevelCount", logLevelCount);
        
        return Result.success(stats);
    }
    
    @Override
    public Result<Map<String, Object>> getFileStatistics() {
        List<FileEntity> allFiles = fileRepository.findAllActive();
        
        Map<String, Object> stats = new HashMap<>();
        stats.put("totalFiles", allFiles.size());
        stats.put("totalSize", allFiles.stream().mapToLong(FileEntity::getFileSize).sum());
        
        Map<String, Long> typeCount = new HashMap<>();
        Map<String, Long> typeSize = new HashMap<>();
        
        for (FileEntity file : allFiles) {
            String type = file.getFileType();
            typeCount.put(type, typeCount.getOrDefault(type, 0L) + 1);
            typeSize.put(type, typeSize.getOrDefault(type, 0L) + (file.getFileSize() != null ? file.getFileSize() : 0));
        }
        
        stats.put("typeCount", typeCount);
        stats.put("typeSize", typeSize);
        
        return Result.success(stats);
    }
    
    @Override
    public Result<Map<String, Object>> getRecentActivities() {
        Map<String, Object> activities = new HashMap<>();
        
        // 最近上传的文件（最近10个）
        List<FileEntity> recentFiles = fileRepository.findAllActive().stream()
                .sorted((a, b) -> b.getCreateTime().compareTo(a.getCreateTime()))
                .limit(10)
                .collect(Collectors.toList());
        activities.put("recentFiles", recentFiles);
        
        // 最近的日志（最近20条）
        List<LogData> recentLogs = logDataRepository.findAll().stream()
                .sorted((a, b) -> {
                    LocalDateTime timeA = a.getTimestamp() != null ? a.getTimestamp() : a.getCreateTime();
                    LocalDateTime timeB = b.getTimestamp() != null ? b.getTimestamp() : b.getCreateTime();
                    return timeB.compareTo(timeA);
                })
                .limit(20)
                .collect(Collectors.toList());
        activities.put("recentLogs", recentLogs);
        
        // 最近处理的原始数据（最近10条）
        List<RawData> recentProcessed = rawDataRepository.findByProcessed(true).stream()
                .sorted((a, b) -> b.getUpdateTime().compareTo(a.getUpdateTime()))
                .limit(10)
                .collect(Collectors.toList());
        activities.put("recentProcessed", recentProcessed);
        
        return Result.success(activities);
    }
    
    private Map<String, Object> getTrendData() {
        Map<String, Object> trends = new HashMap<>();
        
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime sevenDaysAgo = now.minusDays(7);
        
        // 最近7天的原始数据趋势
        List<RawData> recentRawData = rawDataRepository.findByCreateTimeBetween(sevenDaysAgo, now);
        Map<String, Long> rawDataTrend = new HashMap<>();
        for (int i = 6; i >= 0; i--) {
            LocalDateTime dayStart = now.minusDays(i).withHour(0).withMinute(0).withSecond(0);
            LocalDateTime dayEnd = dayStart.plusDays(1);
            long count = recentRawData.stream()
                    .filter(d -> {
                        LocalDateTime createTime = d.getCreateTime();
                        return createTime != null && createTime.isAfter(dayStart) && createTime.isBefore(dayEnd);
                    })
                    .count();
            rawDataTrend.put(dayStart.toLocalDate().toString(), count);
        }
        trends.put("rawDataTrend", rawDataTrend);
        
        // 最近7天的处理数据趋势
        List<ProcessedData> allProcessed = processedDataRepository.findAll();
        List<ProcessedData> recentProcessed = allProcessed.stream()
                .filter(p -> {
                    LocalDateTime createTime = p.getCreateTime();
                    return createTime != null && createTime.isAfter(sevenDaysAgo) && createTime.isBefore(now);
                })
                .collect(Collectors.toList());
        Map<String, Long> processedDataTrend = new HashMap<>();
        for (int i = 6; i >= 0; i--) {
            LocalDateTime dayStart = now.minusDays(i).withHour(0).withMinute(0).withSecond(0);
            LocalDateTime dayEnd = dayStart.plusDays(1);
            long count = recentProcessed.stream()
                    .filter(p -> {
                        LocalDateTime createTime = p.getCreateTime();
                        return createTime != null && createTime.isAfter(dayStart) && createTime.isBefore(dayEnd);
                    })
                    .count();
            processedDataTrend.put(dayStart.toLocalDate().toString(), count);
        }
        trends.put("processedDataTrend", processedDataTrend);
        
        return trends;
    }
    
    @Override
    public Result<Map<String, Object>> getChartData() {
        Map<String, Object> charts = new HashMap<>();
        
        // 数据类型分布（饼图数据）
        charts.put("dataTypeDistribution", getDataTypeDistribution().getData());
        
        // 趋势图数据（最近7天）
        charts.put("trendChart", getTrendChart(7).getData());
        
        // 数据源分布（如果有source字段）
        Map<String, Long> sourceDistribution = new HashMap<>();
        List<RawData> allRawData = rawDataRepository.findAll();
        for (RawData data : allRawData) {
            String source = data.getSource() != null ? data.getSource() : "未知";
            sourceDistribution.put(source, sourceDistribution.getOrDefault(source, 0L) + 1);
        }
        charts.put("sourceDistribution", sourceDistribution);
        
        // 处理状态分布
        long total = rawDataRepository.count();
        long processed = rawDataRepository.findByProcessed(true).size();
        long unprocessed = total - processed;
        Map<String, Long> processStatusDistribution = new HashMap<>();
        processStatusDistribution.put("已处理", processed);
        processStatusDistribution.put("未处理", unprocessed);
        charts.put("processStatusDistribution", processStatusDistribution);
        
        return Result.success(charts);
    }
    
    @Override
    public Result<Map<String, Object>> getDataTypeDistribution() {
        Map<String, Object> distribution = new HashMap<>();
        
        List<RawData> allRawData = rawDataRepository.findAll();
        Map<String, Long> typeCount = new HashMap<>();
        
        for (RawData data : allRawData) {
            String type = data.getDataType() != null ? data.getDataType() : "未知";
            typeCount.put(type, typeCount.getOrDefault(type, 0L) + 1);
        }
        
        distribution.put("labels", new ArrayList<>(typeCount.keySet()));
        distribution.put("values", new ArrayList<>(typeCount.values()));
        distribution.put("total", allRawData.size());
        
        return Result.success(distribution);
    }
    
    @Override
    public Result<Map<String, Object>> getTrendChart(int days) {
        Map<String, Object> trend = new HashMap<>();
        
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime startDate = now.minusDays(days);
        
        // 原始数据趋势
        List<RawData> recentRawData = rawDataRepository.findByCreateTimeBetween(startDate, now);
        Map<String, Long> rawDataTrend = new HashMap<>();
        for (int i = days - 1; i >= 0; i--) {
            LocalDateTime dayStart = now.minusDays(i).withHour(0).withMinute(0).withSecond(0);
            LocalDateTime dayEnd = dayStart.plusDays(1);
            long count = recentRawData.stream()
                    .filter(d -> {
                        LocalDateTime createTime = d.getCreateTime();
                        return createTime != null && createTime.isAfter(dayStart) && createTime.isBefore(dayEnd);
                    })
                    .count();
            rawDataTrend.put(dayStart.toLocalDate().toString(), count);
        }
        
        // 处理数据趋势
        List<ProcessedData> allProcessed = processedDataRepository.findAll();
        List<ProcessedData> recentProcessed = allProcessed.stream()
                .filter(p -> {
                    LocalDateTime createTime = p.getCreateTime();
                    return createTime != null && createTime.isAfter(startDate) && createTime.isBefore(now);
                })
                .collect(Collectors.toList());
        Map<String, Long> processedDataTrend = new HashMap<>();
        for (int i = days - 1; i >= 0; i--) {
            LocalDateTime dayStart = now.minusDays(i).withHour(0).withMinute(0).withSecond(0);
            LocalDateTime dayEnd = dayStart.plusDays(1);
            long count = recentProcessed.stream()
                    .filter(p -> {
                        LocalDateTime createTime = p.getCreateTime();
                        return createTime != null && createTime.isAfter(dayStart) && createTime.isBefore(dayEnd);
                    })
                    .count();
            processedDataTrend.put(dayStart.toLocalDate().toString(), count);
        }
        
        trend.put("dates", new ArrayList<>(rawDataTrend.keySet()));
        trend.put("rawData", new ArrayList<>(rawDataTrend.values()));
        trend.put("processedData", new ArrayList<>(processedDataTrend.values()));
        trend.put("days", days);
        
        return Result.success(trend);
    }
}

