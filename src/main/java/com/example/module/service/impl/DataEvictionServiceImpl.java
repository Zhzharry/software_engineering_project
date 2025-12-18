package com.example.module.service.impl;

import com.example.module.entity.mongodb.ProcessedData;
import com.example.module.entity.mongodb.RawData;
import com.example.module.entity.mysql.StorageStrategy;
import com.example.module.repository.mongodb.ProcessedDataRepository;
import com.example.module.repository.mongodb.RawDataRepository;
import com.example.module.repository.mysql.StorageStrategyRepository;
import com.example.module.service.DataEvictionService;
import com.example.module.util.Result;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class DataEvictionServiceImpl implements DataEvictionService {
    
    private final RawDataRepository rawDataRepository;
    private final ProcessedDataRepository processedDataRepository;
    private final StorageStrategyRepository storageStrategyRepository;
    
    @Override
    @Transactional
    public Result<Integer> evictExpiredRawData() {
        try {
            LocalDateTime now = LocalDateTime.now();
            List<RawData> allRawData = rawDataRepository.findAll();
            int evictedCount = 0;
            
            for (RawData data : allRawData) {
                // 如果设置了过期时间且已过期，则删除
                if (data.getExpireTime() != null && data.getExpireTime().isBefore(now)) {
                    rawDataRepository.deleteById(data.getId());
                    evictedCount++;
                    log.debug("删除过期原始数据: {}", data.getId());
                }
                // 如果没有设置过期时间但有保留天数，则根据创建时间计算
                else if (data.getExpireTime() == null && data.getRetentionDays() != null && data.getRetentionDays() > 0) {
                    LocalDateTime expireTime = data.getCreateTime().plusDays(data.getRetentionDays());
                    if (expireTime.isBefore(now)) {
                        rawDataRepository.deleteById(data.getId());
                        evictedCount++;
                        log.debug("根据保留天数删除原始数据: {}", data.getId());
                    }
                }
            }
            
            log.info("清理过期原始数据完成，共删除 {} 条", evictedCount);
            return Result.success("清理完成", evictedCount);
        } catch (Exception e) {
            log.error("清理过期原始数据失败", e);
            return Result.error(500, "清理失败: " + e.getMessage());
        }
    }
    
    @Override
    @Transactional
    public Result<Integer> evictExpiredProcessedData() {
        try {
            LocalDateTime now = LocalDateTime.now();
            List<ProcessedData> allProcessedData = processedDataRepository.findAll();
            int evictedCount = 0;
            
            for (ProcessedData data : allProcessedData) {
                // 如果设置了过期时间且已过期，则删除
                if (data.getExpireTime() != null && data.getExpireTime().isBefore(now)) {
                    processedDataRepository.deleteById(data.getId());
                    evictedCount++;
                    log.debug("删除过期处理数据: {}", data.getId());
                }
                // 如果没有设置过期时间但有保留天数，则根据创建时间计算
                else if (data.getExpireTime() == null && data.getRetentionDays() != null && data.getRetentionDays() > 0) {
                    LocalDateTime expireTime = data.getCreateTime().plusDays(data.getRetentionDays());
                    if (expireTime.isBefore(now)) {
                        processedDataRepository.deleteById(data.getId());
                        evictedCount++;
                        log.debug("根据保留天数删除处理数据: {}", data.getId());
                    }
                }
            }
            
            log.info("清理过期处理数据完成，共删除 {} 条", evictedCount);
            return Result.success("清理完成", evictedCount);
        } catch (Exception e) {
            log.error("清理过期处理数据失败", e);
            return Result.error(500, "清理失败: " + e.getMessage());
        }
    }
    
    @Override
    @Transactional
    public Result<Integer> evictAllExpiredData() {
        try {
            Result<Integer> rawDataResult = evictExpiredRawData();
            Result<Integer> processedDataResult = evictExpiredProcessedData();
            
            int totalEvicted = rawDataResult.getData() + processedDataResult.getData();
            log.info("清理所有过期数据完成，共删除 {} 条", totalEvicted);
            return Result.success("清理完成", totalEvicted);
        } catch (Exception e) {
            log.error("清理所有过期数据失败", e);
            return Result.error(500, "清理失败: " + e.getMessage());
        }
    }
    
    @Override
    @Transactional
    public Result<Integer> evictDataByStrategy(String dataType) {
        try {
            // 查找该数据类型的存储策略
            List<StorageStrategy> strategies = storageStrategyRepository.findByDataType(dataType);
            if (strategies.isEmpty()) {
                return Result.error(404, "未找到该数据类型的存储策略");
            }
            
            StorageStrategy strategy = strategies.stream()
                    .filter(s -> s.getStatus() != null && s.getStatus() == 1)
                    .findFirst()
                    .orElse(strategies.get(0));
            
            if (strategy.getRetentionDays() == null || strategy.getRetentionDays() <= 0) {
                return Result.success("该策略未设置保留天数，跳过清理", 0);
            }
            
            LocalDateTime expireTime = LocalDateTime.now().minusDays(strategy.getRetentionDays());
            int evictedCount = 0;
            
            // 清理原始数据
            List<RawData> rawDataList = rawDataRepository.findByDataType(dataType);
            for (RawData data : rawDataList) {
                if (data.getCreateTime() != null && data.getCreateTime().isBefore(expireTime)) {
                    rawDataRepository.deleteById(data.getId());
                    evictedCount++;
                }
            }
            
            // 清理处理数据（通过关联的原始数据）
            // 注意：这里简化处理，实际可能需要更复杂的关联查询
            
            log.info("根据存储策略清理数据完成，数据类型: {}, 共删除 {} 条", dataType, evictedCount);
            return Result.success("清理完成", evictedCount);
        } catch (Exception e) {
            log.error("根据存储策略清理数据失败", e);
            return Result.error(500, "清理失败: " + e.getMessage());
        }
    }
}

