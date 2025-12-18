package com.example.module.controller;

import com.example.module.service.DataEvictionService;
import com.example.module.util.Result;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/data-eviction")
@RequiredArgsConstructor
public class DataEvictionController {
    
    private final DataEvictionService dataEvictionService;
    
    @PostMapping("/raw-data")
    public Result<Integer> evictExpiredRawData() {
        return dataEvictionService.evictExpiredRawData();
    }
    
    @PostMapping("/processed-data")
    public Result<Integer> evictExpiredProcessedData() {
        return dataEvictionService.evictExpiredProcessedData();
    }
    
    @PostMapping("/all")
    public Result<Integer> evictAllExpiredData() {
        return dataEvictionService.evictAllExpiredData();
    }
    
    @PostMapping("/strategy/{dataType}")
    public Result<Integer> evictDataByStrategy(@PathVariable String dataType) {
        return dataEvictionService.evictDataByStrategy(dataType);
    }
}

