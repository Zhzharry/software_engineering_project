package com.example.module.service;

import com.example.module.entity.mysql.StorageStrategy;
import com.example.module.util.Result;

import java.util.List;

public interface StorageStrategyService {
    Result<StorageStrategy> createStrategy(StorageStrategy strategy);
    
    Result<StorageStrategy> updateStrategy(Long id, StorageStrategy strategy);
    
    Result<Void> deleteStrategy(Long id);
    
    Result<StorageStrategy> getStrategyById(Long id);
    
    Result<List<StorageStrategy>> getAllStrategies();
    
    Result<List<StorageStrategy>> getStrategiesByDataType(String dataType);
    
    Result<List<StorageStrategy>> getActiveStrategies();
    
    Result<StorageStrategy> getStrategyByDataType(String dataType);
}

