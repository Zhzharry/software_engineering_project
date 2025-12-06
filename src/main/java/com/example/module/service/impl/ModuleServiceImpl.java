package com.example.module.service.impl;

import com.example.module.entity.mysql.ModuleEntity;
import com.example.module.entity.mongodb.RawData;
import com.example.module.repository.mysql.ModuleRepository;
import com.example.module.repository.mongodb.RawDataRepository;
import com.example.module.service.ModuleService;
import com.example.module.util.Result;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class ModuleServiceImpl implements ModuleService {
    
    private final ModuleRepository moduleRepository;
    private final RawDataRepository rawDataRepository;

    @Override
    @Transactional
    public Result<ModuleEntity> createModule(ModuleEntity moduleEntity) {
        try {
            if (moduleRepository.existsByName(moduleEntity.getName())) {
                return Result.error(400, "Module name already exists");
            }
            ModuleEntity saved = moduleRepository.save(moduleEntity);
            return Result.success("Module created successfully", saved);
        } catch (Exception e) {
            log.error("Failed to create module", e);
            return Result.error("Failed to create module: " + e.getMessage());
        }
    }

    @Override
    @Transactional
    public Result<ModuleEntity> updateModule(Long id, ModuleEntity moduleEntity) {
        try {
            Optional<ModuleEntity> existing = moduleRepository.findById(id);
            // if (existing.isEmpty()) {  // Java 8 不支持 isEmpty()
            if (!existing.isPresent()) {
                return Result.error(404, "Module not found");
            }
            
            ModuleEntity entity = existing.get();
            entity.setName(moduleEntity.getName());
            entity.setDescription(moduleEntity.getDescription());
            entity.setStatus(moduleEntity.getStatus());
            
            ModuleEntity updated = moduleRepository.save(entity);
            return Result.success("Module updated successfully", updated);
        } catch (Exception e) {
            log.error("Failed to update module: {}", id, e);
            return Result.error("Failed to update module: " + e.getMessage());
        }
    }

    @Override
    @Transactional
    public Result<Void> deleteModule(Long id) {
        try {
            if (!moduleRepository.existsById(id)) {
                return Result.error(404, "Module not found");
            }
            moduleRepository.deleteById(id);
            rawDataRepository.deleteByModuleId(id);
            return Result.success("Module deleted successfully", null);
        } catch (Exception e) {
            log.error("Failed to delete module: {}", id, e);
            return Result.error("Failed to delete module: " + e.getMessage());
        }
    }

    @Override
    public Result<ModuleEntity> getModuleById(Long id) {
        try {
            Optional<ModuleEntity> module = moduleRepository.findById(id);
            return module.map(entity -> Result.success(entity))
                       .orElse(Result.error(404, "Module not found"));
        } catch (Exception e) {
            log.error("Failed to get module: {}", id, e);
            return Result.error("Failed to get module: " + e.getMessage());
        }
    }

    @Override
    public Result<Page<ModuleEntity>> getAllModules(Pageable pageable) {
        try {
            Page<ModuleEntity> modules = moduleRepository.findAll(pageable);
            return Result.success(modules);
        } catch (Exception e) {
            log.error("Failed to get modules", e);
            return Result.error("Failed to get modules: " + e.getMessage());
        }
    }

    @Override
    public Result<List<ModuleEntity>> getModulesByStatus(Integer status) {
        try {
            List<ModuleEntity> modules = moduleRepository.findByStatus(status);
            return Result.success(modules);
        } catch (Exception e) {
            log.error("Failed to get modules by status: {}", status, e);
            return Result.error("Failed to get modules: " + e.getMessage());
        }
    }

    @Override
    @Transactional
    public Result<RawData> createRawData(RawData rawData) {
        try {
            if (rawData.getModuleId() == null) {
                return Result.error(400, "Module ID is required");
            }
            if (!moduleRepository.existsById(rawData.getModuleId())) {
                return Result.error(404, "Module not found");
            }
            LocalDateTime now = LocalDateTime.now();
            rawData.setCreateTime(now);
            rawData.setUpdateTime(now);
            if (rawData.getVersion() == null) {
                rawData.setVersion(1);
            }
            RawData saved = rawDataRepository.save(rawData);
            return Result.success("Raw data created successfully", saved);
        } catch (Exception e) {
            log.error("Failed to create raw data", e);
            return Result.error("Failed to create raw data: " + e.getMessage());
        }
    }

    @Override
    @Transactional
    public Result<RawData> updateRawData(String id, RawData rawData) {
        try {
            Optional<RawData> existing = rawDataRepository.findById(id);
            // if (existing.isEmpty()) {  // Java 8 不支持 isEmpty()
            if (!existing.isPresent()) {
                return Result.error(404, "Raw data not found");
            }
            
            RawData data = existing.get();
            data.setDataContent(rawData.getDataContent());
            data.setDataType(rawData.getDataType());
            data.setSource(rawData.getSource());
            data.setContent(rawData.getContent());
            data.setProcessed(rawData.getProcessed());
            data.setVersion(data.getVersion() != null ? data.getVersion() + 1 : 1);
            data.setUpdateTime(LocalDateTime.now());
            
            RawData updated = rawDataRepository.save(data);
            return Result.success("Raw data updated successfully", updated);
        } catch (Exception e) {
            log.error("Failed to update raw data: {}", id, e);
            return Result.error("Failed to update raw data: " + e.getMessage());
        }
    }

    @Override
    @Transactional
    public Result<Void> deleteRawData(String id) {
        try {
            if (!rawDataRepository.existsById(id)) {
                return Result.error(404, "Raw data not found");
            }
            rawDataRepository.deleteById(id);
            return Result.success("Raw data deleted successfully", null);
        } catch (Exception e) {
            log.error("Failed to delete raw data: {}", id, e);
            return Result.error("Failed to delete raw data: " + e.getMessage());
        }
    }

    @Override
    public Result<RawData> getRawDataById(String id) {
        try {
            Optional<RawData> rawData = rawDataRepository.findById(id);
            return rawData.map(data -> Result.success(data))
                         .orElse(Result.error(404, "Raw data not found"));
        } catch (Exception e) {
            log.error("Failed to get raw data: {}", id, e);
            return Result.error("Failed to get raw data: " + e.getMessage());
        }
    }

    @Override
    public Result<List<RawData>> getRawDataByModuleId(Long moduleId) {
        try {
            List<RawData> rawDataList = rawDataRepository.findByModuleId(moduleId);
            return Result.success(rawDataList);
        } catch (Exception e) {
            log.error("Failed to get raw data by module: {}", moduleId, e);
            return Result.error("Failed to get raw data: " + e.getMessage());
        }
    }

    @Override
    public Result<Map<String, Object>> getCombinedData(Long moduleId) {
        try {
            Optional<ModuleEntity> module = moduleRepository.findById(moduleId);
            // if (module.isEmpty()) {  // Java 8 不支持 isEmpty()
            if (!module.isPresent()) {
                return Result.error(404, "Module not found");
            }
            
            List<RawData> rawDataList = rawDataRepository.findByModuleId(moduleId);
            
            Map<String, Object> combinedData = new HashMap<>();
            combinedData.put("module", module.get());
            combinedData.put("rawData", rawDataList);
            
            return Result.success(combinedData);
        } catch (Exception e) {
            log.error("Failed to get combined data for module: {}", moduleId, e);
            return Result.error("Failed to get combined data: " + e.getMessage());
        }
    }
}