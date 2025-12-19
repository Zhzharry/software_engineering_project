package com.example.module.controller;

import com.example.module.entity.mysql.RawData;
import com.example.module.service.RawDataService;
import com.example.module.util.Result;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/raw-data")
@RequiredArgsConstructor
public class RawDataController {
    
    private final RawDataService rawDataService;

    @GetMapping
    public Result<List<RawData>> getAllRawData() {
        return rawDataService.getAllRawData();
    }

    @PostMapping
    public Result<RawData> saveRawData(@RequestBody RawData rawData) {
        return rawDataService.saveRawData(rawData);
    }

    @GetMapping("/{id}")
    public Result<RawData> getRawDataById(@PathVariable Long id) {
        return rawDataService.getRawDataById(id);
    }

    @GetMapping("/type/{dataType}")
    public Result<List<RawData>> getRawDataByType(@PathVariable String dataType) {
        return rawDataService.getRawDataByType(dataType);
    }

    @GetMapping("/unprocessed")
    public Result<List<RawData>> getUnprocessedData() {
        return rawDataService.getUnprocessedData();
    }

    @GetMapping("/time-range")
    public Result<List<RawData>> getRawDataByTimeRange(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime start,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime end) {
        return rawDataService.getRawDataByTimeRange(start, end);
    }

    @PutMapping("/{id}/processed")
    public Result<Boolean> markAsProcessed(@PathVariable Long id) {
        return rawDataService.markAsProcessed(id);
    }

    @GetMapping("/count/{dataType}")
    public Result<Long> getDataCountByType(@PathVariable String dataType) {
        return rawDataService.getDataCountByType(dataType);
    }

    @GetMapping("/disaster-category/{disasterCategory}")
    public Result<List<RawData>> getRawDataByDisasterCategory(@PathVariable String disasterCategory) {
        return rawDataService.getRawDataByDisasterCategory(disasterCategory);
    }

    @DeleteMapping("/{id}")
    public Result<Boolean> deleteRawData(@PathVariable Long id) {
        return rawDataService.deleteRawData(id);
    }

    @PutMapping("/{id}")
    public Result<RawData> updateRawData(@PathVariable Long id, @RequestBody RawData rawData) {
        return rawDataService.updateRawData(id, rawData);
    }
}
