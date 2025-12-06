package com.example.module.controller;

import com.example.module.entity.mysql.ModuleEntity;
import com.example.module.entity.mongodb.RawData;
import com.example.module.service.ModuleService;
import com.example.module.util.Result;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/modules")
@RequiredArgsConstructor
public class ModuleController {
    
    private final ModuleService moduleService;

    @PostMapping
    public Result<ModuleEntity> createModule(@RequestBody ModuleEntity moduleEntity) {
        return moduleService.createModule(moduleEntity);
    }

    @PutMapping("/{id}")
    public Result<ModuleEntity> updateModule(@PathVariable Long id, @RequestBody ModuleEntity moduleEntity) {
        return moduleService.updateModule(id, moduleEntity);
    }

    @DeleteMapping("/{id}")
    public Result<Void> deleteModule(@PathVariable Long id) {
        return moduleService.deleteModule(id);
    }

    @GetMapping("/{id}")
    public Result<ModuleEntity> getModuleById(@PathVariable Long id) {
        return moduleService.getModuleById(id);
    }

    @GetMapping
    public Result<Page<ModuleEntity>> getAllModules(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "id") String sort) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(sort));
        return moduleService.getAllModules(pageable);
    }

    @GetMapping("/status/{status}")
    public Result<List<ModuleEntity>> getModulesByStatus(@PathVariable Integer status) {
        return moduleService.getModulesByStatus(status);
    }

    @PostMapping("/raw-data")
    public Result<RawData> createRawData(@RequestBody RawData rawData) {
        return moduleService.createRawData(rawData);
    }

    @PutMapping("/raw-data/{id}")
    public Result<RawData> updateRawData(@PathVariable String id, @RequestBody RawData rawData) {
        return moduleService.updateRawData(id, rawData);
    }

    @DeleteMapping("/raw-data/{id}")
    public Result<Void> deleteRawData(@PathVariable String id) {
        return moduleService.deleteRawData(id);
    }

    @GetMapping("/raw-data/{id}")
    public Result<RawData> getRawDataById(@PathVariable String id) {
        return moduleService.getRawDataById(id);
    }

    @GetMapping("/{moduleId}/raw-data")
    public Result<List<RawData>> getRawDataByModuleId(@PathVariable Long moduleId) {
        return moduleService.getRawDataByModuleId(moduleId);
    }

    @GetMapping("/{moduleId}/combined")
    public Result<Map<String, Object>> getCombinedData(@PathVariable Long moduleId) {
        return moduleService.getCombinedData(moduleId);
    }
}