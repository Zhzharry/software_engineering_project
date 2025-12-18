package com.example.module.service.impl;

import com.example.module.entity.mysql.StorageStrategy;
import com.example.module.repository.mysql.StorageStrategyRepository;
import com.example.module.service.StorageStrategyService;
import com.example.module.util.Result;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class StorageStrategyServiceImpl implements StorageStrategyService {
    
    private final StorageStrategyRepository storageStrategyRepository;
    
    @Override
    @Transactional
    public Result<StorageStrategy> createStrategy(StorageStrategy strategy) {
        try {
            StorageStrategy saved = storageStrategyRepository.save(strategy);
            log.info("创建存储策略成功: {}", saved.getStrategyName());
            return Result.success("存储策略创建成功", saved);
        } catch (Exception e) {
            log.error("创建存储策略失败", e);
            return Result.error(500, "创建存储策略失败: " + e.getMessage());
        }
    }
    
    @Override
    @Transactional
    public Result<StorageStrategy> updateStrategy(Long id, StorageStrategy strategy) {
        try {
            Optional<StorageStrategy> existing = storageStrategyRepository.findById(id);
            if (!existing.isPresent()) {
                return Result.error(404, "存储策略不存在");
            }
            
            StorageStrategy existingStrategy = existing.get();
            existingStrategy.setStrategyName(strategy.getStrategyName());
            existingStrategy.setDataType(strategy.getDataType());
            existingStrategy.setStorageType(strategy.getStorageType());
            existingStrategy.setRetentionDays(strategy.getRetentionDays());
            existingStrategy.setMaxSize(strategy.getMaxSize());
            existingStrategy.setCleanupRule(strategy.getCleanupRule());
            existingStrategy.setStatus(strategy.getStatus());
            
            StorageStrategy updated = storageStrategyRepository.save(existingStrategy);
            log.info("更新存储策略成功: {}", updated.getStrategyName());
            return Result.success("存储策略更新成功", updated);
        } catch (Exception e) {
            log.error("更新存储策略失败", e);
            return Result.error(500, "更新存储策略失败: " + e.getMessage());
        }
    }
    
    @Override
    @Transactional
    public Result<Void> deleteStrategy(Long id) {
        try {
            if (!storageStrategyRepository.existsById(id)) {
                return Result.error(404, "存储策略不存在");
            }
            storageStrategyRepository.deleteById(id);
            log.info("删除存储策略成功: {}", id);
            return Result.success("存储策略删除成功", null);
        } catch (Exception e) {
            log.error("删除存储策略失败", e);
            return Result.error(500, "删除存储策略失败: " + e.getMessage());
        }
    }
    
    @Override
    public Result<StorageStrategy> getStrategyById(Long id) {
        try {
            Optional<StorageStrategy> strategy = storageStrategyRepository.findById(id);
            if (strategy.isPresent()) {
                return Result.success(strategy.get());
            } else {
                return Result.error(404, "存储策略不存在");
            }
        } catch (Exception e) {
            log.error("查询存储策略失败", e);
            return Result.error(500, "查询存储策略失败: " + e.getMessage());
        }
    }
    
    @Override
    public Result<List<StorageStrategy>> getAllStrategies() {
        try {
            List<StorageStrategy> strategies = storageStrategyRepository.findAll();
            return Result.success(strategies);
        } catch (Exception e) {
            log.error("查询所有存储策略失败", e);
            return Result.error(500, "查询存储策略失败: " + e.getMessage());
        }
    }
    
    @Override
    public Result<List<StorageStrategy>> getStrategiesByDataType(String dataType) {
        try {
            List<StorageStrategy> strategies = storageStrategyRepository.findByDataType(dataType);
            return Result.success(strategies);
        } catch (Exception e) {
            log.error("按数据类型查询存储策略失败", e);
            return Result.error(500, "查询存储策略失败: " + e.getMessage());
        }
    }
    
    @Override
    public Result<List<StorageStrategy>> getActiveStrategies() {
        try {
            List<StorageStrategy> strategies = storageStrategyRepository.findByStatus(1);
            return Result.success(strategies);
        } catch (Exception e) {
            log.error("查询启用状态的存储策略失败", e);
            return Result.error(500, "查询存储策略失败: " + e.getMessage());
        }
    }
    
    @Override
    public Result<StorageStrategy> getStrategyByDataType(String dataType) {
        try {
            List<StorageStrategy> strategies = storageStrategyRepository.findByDataType(dataType);
            // 返回第一个启用的策略，如果没有启用的则返回第一个
            Optional<StorageStrategy> activeStrategy = strategies.stream()
                    .filter(s -> s.getStatus() != null && s.getStatus() == 1)
                    .findFirst();
            
            if (activeStrategy.isPresent()) {
                return Result.success(activeStrategy.get());
            } else if (!strategies.isEmpty()) {
                return Result.success(strategies.get(0));
            } else {
                return Result.error(404, "未找到该数据类型的存储策略");
            }
        } catch (Exception e) {
            log.error("按数据类型查询存储策略失败", e);
            return Result.error(500, "查询存储策略失败: " + e.getMessage());
        }
    }
}

