package com.example.module.service;

import com.example.module.entity.mysql.ModuleEntity;
import com.example.module.entity.mongodb.RawData;
import com.example.module.util.Result;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import java.util.List;
import java.util.Map;

public interface ModuleService {
    Result<ModuleEntity> createModule(ModuleEntity moduleEntity);
    Result<ModuleEntity> updateModule(Long id, ModuleEntity moduleEntity);
    Result<Void> deleteModule(Long id);
    Result<ModuleEntity> getModuleById(Long id);
    Result<Page<ModuleEntity>> getAllModules(Pageable pageable);
    Result<List<ModuleEntity>> getModulesByStatus(Integer status);
    
    Result<RawData> createRawData(RawData rawData);
    Result<RawData> updateRawData(String id, RawData rawData);
    Result<Void> deleteRawData(String id);
    Result<RawData> getRawDataById(String id);
    Result<List<RawData>> getRawDataByModuleId(Long moduleId);
    Result<Map<String, Object>> getCombinedData(Long moduleId);
}