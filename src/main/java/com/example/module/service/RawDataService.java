package com.example.module.service;

import com.example.module.entity.mysql.RawData;
import com.example.module.util.Result;

import java.time.LocalDateTime;
import java.util.List;

public interface RawDataService {
    Result<List<RawData>> getAllRawData();

    Result<RawData> saveRawData(RawData rawData);

    Result<RawData> getRawDataById(Long id);

    Result<List<RawData>> getRawDataByType(String dataType);

    Result<List<RawData>> getUnprocessedData();

    Result<List<RawData>> getRawDataByTimeRange(LocalDateTime start, LocalDateTime end);

    Result<Boolean> markAsProcessed(Long id);

    Result<Long> getDataCountByType(String dataType);
    
    Result<List<RawData>> getRawDataByDisasterCategory(String disasterCategory);
    
    Result<Boolean> deleteRawData(Long id);
    
    Result<RawData> updateRawData(Long id, RawData rawData);
}
