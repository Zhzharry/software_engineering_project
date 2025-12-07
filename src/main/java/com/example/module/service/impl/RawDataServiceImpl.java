package com.example.module.service.impl;

import com.example.module.entity.mongodb.RawData;
import com.example.module.repository.mongodb.RawDataRepository;
import com.example.module.service.RawDataService;
import com.example.module.util.Result;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class RawDataServiceImpl implements RawDataService {
    
    private final RawDataRepository rawDataRepository;

    @Override
    public Result<List<RawData>> getAllRawData() {
        try {
            List<RawData> dataList = rawDataRepository.findAll();
            return Result.success(dataList);
        } catch (Exception e) {
            log.error("获取所有原始数据失败: {}", e.getMessage());
            return Result.error("获取所有原始数据失败: " + e.getMessage());
        }
    }

    @Override
    public Result<RawData> saveRawData(RawData rawData) {
        try {
            LocalDateTime now = LocalDateTime.now();
            if (rawData.getCreateTime() == null) {
                rawData.setCreateTime(now);
            }
            rawData.setUpdateTime(now);
            if (rawData.getVersion() == null) {
                rawData.setVersion(1);
            }
            if (rawData.getProcessed() == null) {
                rawData.setProcessed(false);
            }
            RawData savedData = rawDataRepository.save(rawData);
            log.info("原始数据保存成功, ID: {}", savedData.getId());
            return Result.success("原始数据保存成功", savedData);
        } catch (Exception e) {
            log.error("保存原始数据失败: {}", e.getMessage());
            return Result.error("保存原始数据失败: " + e.getMessage());
        }
    }

    @Override
    public Result<RawData> getRawDataById(String id) {
        try {
            Optional<RawData> rawData = rawDataRepository.findById(id);
            return rawData.map(data -> Result.success(data))
                         .orElse(Result.error("数据不存在"));
        } catch (Exception e) {
            log.error("获取原始数据失败: {}", e.getMessage());
            return Result.error("获取原始数据失败: " + e.getMessage());
        }
    }

    @Override
    public Result<List<RawData>> getRawDataByType(String dataType) {
        try {
            List<RawData> dataList = rawDataRepository.findByDataType(dataType);
            return Result.success(dataList);
        } catch (Exception e) {
            log.error("根据类型获取原始数据失败: {}", e.getMessage());
            return Result.error("根据类型获取原始数据失败: " + e.getMessage());
        }
    }

    @Override
    public Result<List<RawData>> getUnprocessedData() {
        try {
            List<RawData> dataList = rawDataRepository.findByProcessed(false);
            return Result.success(dataList);
        } catch (Exception e) {
            log.error("获取未处理数据失败: {}", e.getMessage());
            return Result.error("获取未处理数据失败: " + e.getMessage());
        }
    }

    @Override
    public Result<List<RawData>> getRawDataByTimeRange(LocalDateTime start, LocalDateTime end) {
        try {
            List<RawData> dataList = rawDataRepository.findByCreateTimeBetween(start, end);
            return Result.success(dataList);
        } catch (Exception e) {
            log.error("根据时间范围获取数据失败: {}", e.getMessage());
            return Result.error("根据时间范围获取数据失败: " + e.getMessage());
        }
    }

    @Override
    public Result<Boolean> markAsProcessed(String id) {
        try {
            Optional<RawData> rawDataOpt = rawDataRepository.findById(id);
            if (rawDataOpt.isPresent()) {
                RawData rawData = rawDataOpt.get();
                rawData.setProcessed(true);
                rawData.setUpdateTime(LocalDateTime.now());
                rawDataRepository.save(rawData);
                log.info("数据标记为已处理: {}", id);
                return Result.success("数据处理状态更新成功", true);
            }
            return Result.error("数据不存在");
        } catch (Exception e) {
            log.error("更新数据处理状态失败: {}", e.getMessage());
            return Result.error("更新数据处理状态失败: " + e.getMessage());
        }
    }

    @Override
    public Result<Long> getDataCountByType(String dataType) {
        try {
            long count = rawDataRepository.countByDataType(dataType);
            return Result.success(count);
        } catch (Exception e) {
            log.error("统计数据类型数量失败: {}", e.getMessage());
            return Result.error("统计数据类型数量失败: " + e.getMessage());
        }
    }
}
