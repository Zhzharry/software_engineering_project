package com.example.module.service.impl;

import com.example.module.entity.mysql.RawData;
import com.example.module.repository.mysql.MySqlRawDataRepository;
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
    
    private final MySqlRawDataRepository rawDataRepository;

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
            if (rawData.getStatus() == null) {
                rawData.setStatus(1);
            }
            if (rawData.getProcessed() == null) {
                rawData.setProcessed(false);
            }
            
            // 保存到MySQL
            RawData savedData = rawDataRepository.save(rawData);
            log.info("原始数据保存成功, ID: {}", savedData.getId());
            return Result.success("原始数据保存成功", savedData);
        } catch (Exception e) {
            log.error("保存原始数据失败: {}", e.getMessage());
            return Result.error("保存原始数据失败: " + e.getMessage());
        }
    }

    @Override
    public Result<RawData> getRawDataById(Long id) {
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
    public Result<Boolean> markAsProcessed(Long id) {
        try {
            Optional<RawData> rawDataOpt = rawDataRepository.findById(id);
            if (rawDataOpt.isPresent()) {
                RawData rawData = rawDataOpt.get();
                rawData.setProcessed(true);
                rawData.setUpdateTime(LocalDateTime.now());
                rawData.setStatus(2); // 2表示已处理
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

    @Override
    public Result<List<RawData>> getRawDataByDisasterCategory(String disasterCategory) {
        try {
            List<RawData> dataList = rawDataRepository.findByDisasterCategory(disasterCategory);
            return Result.success(dataList);
        } catch (Exception e) {
            log.error("根据灾情类别获取原始数据失败: {}", e.getMessage());
            return Result.error("根据灾情类别获取原始数据失败: " + e.getMessage());
        }
    }

    @Override
    public Result<Boolean> deleteRawData(Long id) {
        try {
            if (!rawDataRepository.existsById(id)) {
                return Result.error("数据不存在");
            }
            rawDataRepository.deleteById(id);
            log.info("删除原始数据成功, ID: {}", id);
            return Result.success("删除成功", true);
        } catch (Exception e) {
            log.error("删除原始数据失败: {}", e.getMessage());
            return Result.error("删除失败: " + e.getMessage());
        }
    }

    @Override
    public Result<RawData> updateRawData(Long id, RawData rawData) {
        try {
            Optional<RawData> existingDataOpt = rawDataRepository.findById(id);
            if (!existingDataOpt.isPresent()) {
                return Result.error("数据不存在");
            }

            RawData existingData = existingDataOpt.get();
            
            // 更新字段（保留ID和创建时间）
            if (rawData.getDataContent() != null) {
                existingData.setDataContent(rawData.getDataContent());
            }
            if (rawData.getDataType() != null) {
                existingData.setDataType(rawData.getDataType());
            }
            if (rawData.getDisasterCategory() != null) {
                existingData.setDisasterCategory(rawData.getDisasterCategory());
            }
            if (rawData.getDisasterSubcategory() != null) {
                existingData.setDisasterSubcategory(rawData.getDisasterSubcategory());
            }
            if (rawData.getDisasterIndicator() != null) {
                existingData.setDisasterIndicator(rawData.getDisasterIndicator());
            }
            if (rawData.getGeoCode() != null) {
                existingData.setGeoCode(rawData.getGeoCode());
            }
            if (rawData.getDisasterId() != null) {
                existingData.setDisasterId(rawData.getDisasterId());
            }
            if (rawData.getDisasterDateTime() != null) {
                existingData.setDisasterDateTime(rawData.getDisasterDateTime());
            }
            if (rawData.getSourceSystem() != null) {
                existingData.setSourceSystem(rawData.getSourceSystem());
            }
            if (rawData.getSourceCategory() != null) {
                existingData.setSourceCategory(rawData.getSourceCategory());
            }
            if (rawData.getCarrierType() != null) {
                existingData.setCarrierType(rawData.getCarrierType());
            }
            if (rawData.getDecodedDescription() != null) {
                existingData.setDecodedDescription(rawData.getDecodedDescription());
            }
            if (rawData.getStatus() != null) {
                existingData.setStatus(rawData.getStatus());
            }
            if (rawData.getProcessed() != null) {
                existingData.setProcessed(rawData.getProcessed());
            }
            
            // 更新时间
            existingData.setUpdateTime(LocalDateTime.now());
            
            RawData updatedData = rawDataRepository.save(existingData);
            log.info("更新原始数据成功, ID: {}", id);
            return Result.success("更新成功", updatedData);
        } catch (Exception e) {
            log.error("更新原始数据失败: {}", e.getMessage());
            return Result.error("更新失败: " + e.getMessage());
        }
    }
}
