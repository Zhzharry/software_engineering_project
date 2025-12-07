package com.example.module.service;

import com.example.module.entity.mongodb.RawData;
import com.example.module.util.Result;

import java.time.LocalDateTime;
import java.util.List;

public interface RawDataService {
    Result<List<RawData>> getAllRawData();

    Result<RawData> saveRawData(RawData rawData);

    Result<RawData> getRawDataById(String id);

    Result<List<RawData>> getRawDataByType(String dataType);

    Result<List<RawData>> getUnprocessedData();

    Result<List<RawData>> getRawDataByTimeRange(LocalDateTime start, LocalDateTime end);

    Result<Boolean> markAsProcessed(String id);

    Result<Long> getDataCountByType(String dataType);
}
