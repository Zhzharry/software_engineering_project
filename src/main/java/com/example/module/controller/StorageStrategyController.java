package com.example.module.controller;

import com.example.module.entity.mysql.StorageStrategy;
import com.example.module.service.StorageStrategyService;
import com.example.module.util.Result;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/storage-strategy")
@RequiredArgsConstructor
public class StorageStrategyController {
    
    private final StorageStrategyService storageStrategyService;
    
    @PostMapping
    public Result<StorageStrategy> createStrategy(@RequestBody StorageStrategy strategy) {
        return storageStrategyService.createStrategy(strategy);
    }
    
    @PutMapping("/{id}")
    public Result<StorageStrategy> updateStrategy(@PathVariable Long id, @RequestBody StorageStrategy strategy) {
        return storageStrategyService.updateStrategy(id, strategy);
    }
    
    @DeleteMapping("/{id}")
    public Result<Void> deleteStrategy(@PathVariable Long id) {
        return storageStrategyService.deleteStrategy(id);
    }
    
    @GetMapping("/{id}")
    public Result<StorageStrategy> getStrategyById(@PathVariable Long id) {
        return storageStrategyService.getStrategyById(id);
    }
    
    @GetMapping
    public Result<List<StorageStrategy>> getAllStrategies() {
        return storageStrategyService.getAllStrategies();
    }
    
    @GetMapping("/data-type/{dataType}")
    public Result<List<StorageStrategy>> getStrategiesByDataType(@PathVariable String dataType) {
        return storageStrategyService.getStrategiesByDataType(dataType);
    }
    
    @GetMapping("/active")
    public Result<List<StorageStrategy>> getActiveStrategies() {
        return storageStrategyService.getActiveStrategies();
    }
}

