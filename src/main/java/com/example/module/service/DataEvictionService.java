package com.example.module.service;

import com.example.module.util.Result;

public interface DataEvictionService {
    /**
     * 清理过期的原始数据
     */
    Result<Integer> evictExpiredRawData();
    
    /**
     * 清理过期的处理数据
     */
    Result<Integer> evictExpiredProcessedData();
    
    /**
     * 清理所有过期数据
     */
    Result<Integer> evictAllExpiredData();
    
    /**
     * 根据存储策略清理数据
     */
    Result<Integer> evictDataByStrategy(String dataType);
}

