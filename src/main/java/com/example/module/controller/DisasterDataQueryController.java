package com.example.module.controller;

import com.example.module.entity.mongodb.RawData;
import com.example.module.service.DisasterDataQueryService;
import com.example.module.util.Result;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 灾情数据查询控制器
 * 提供基于解码后字段的数据查询接口
 */
@RestController
@RequestMapping("/api/disaster-data")
@RequiredArgsConstructor
public class DisasterDataQueryController {

    private final DisasterDataQueryService disasterDataQueryService;

    /**
     * 根据灾害大类查询
     */
    @GetMapping("/category/{category}")
    public Result<List<RawData>> queryByCategory(@PathVariable String category) {
        return disasterDataQueryService.queryByDisasterCategory(category);
    }

    /**
     * 根据灾害子类查询
     */
    @GetMapping("/subcategory/{subcategory}")
    public Result<List<RawData>> queryBySubcategory(@PathVariable String subcategory) {
        return disasterDataQueryService.queryByDisasterSubcategory(subcategory);
    }

    /**
     * 根据来源查询
     */
    @GetMapping("/source/{source}")
    public Result<List<RawData>> queryBySource(@PathVariable String source) {
        return disasterDataQueryService.queryBySource(source);
    }

    /**
     * 根据载体类型查询
     */
    @GetMapping("/carrier/{carrierType}")
    public Result<List<RawData>> queryByCarrierType(@PathVariable String carrierType) {
        return disasterDataQueryService.queryByCarrierType(carrierType);
    }

    /**
     * 根据地理码查询
     */
    @GetMapping("/geo/{geoCode}")
    public Result<List<RawData>> queryByGeoCode(@PathVariable String geoCode) {
        return disasterDataQueryService.queryByGeoCode(geoCode);
    }

    /**
     * 根据时间范围查询
     */
    @GetMapping("/time-range")
    public Result<List<RawData>> queryByTimeRange(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime start,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime end) {
        return disasterDataQueryService.queryByTimeRange(start, end);
    }

    /**
     * 综合查询
     */
    @PostMapping("/query")
    public Result<List<RawData>> queryByMultipleConditions(@RequestBody DisasterDataQueryController.QueryRequest request) {
        DisasterDataQueryService.DisasterQueryParams params = new DisasterDataQueryService.DisasterQueryParams();
        params.setDisasterCategory(request.getDisasterCategory());
        params.setDisasterSubcategory(request.getDisasterSubcategory());
        params.setSource(request.getSource());
        params.setCarrierType(request.getCarrierType());
        params.setGeoCode(request.getGeoCode());
        params.setStartTime(request.getStartTime());
        params.setEndTime(request.getEndTime());
        params.setPage(request.getPage());
        params.setSize(request.getSize());
        return disasterDataQueryService.queryByMultipleConditions(params);
    }

    /**
     * 获取包含解码信息的数据详情
     */
    @GetMapping("/detail/{id}")
    public Result<DisasterDataQueryService.DisasterDataDetail> getDataDetail(@PathVariable String id) {
        return disasterDataQueryService.getDataDetailWithDecode(id);
    }

    /**
     * 查询请求类
     */
    public static class QueryRequest {
        private String disasterCategory;
        private String disasterSubcategory;
        private String source;
        private String carrierType;
        private String geoCode;
        @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
        private LocalDateTime startTime;
        @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
        private LocalDateTime endTime;
        private Integer page;
        private Integer size;

        // Getters and Setters
        public String getDisasterCategory() {
            return disasterCategory;
        }

        public void setDisasterCategory(String disasterCategory) {
            this.disasterCategory = disasterCategory;
        }

        public String getDisasterSubcategory() {
            return disasterSubcategory;
        }

        public void setDisasterSubcategory(String disasterSubcategory) {
            this.disasterSubcategory = disasterSubcategory;
        }

        public String getSource() {
            return source;
        }

        public void setSource(String source) {
            this.source = source;
        }

        public String getCarrierType() {
            return carrierType;
        }

        public void setCarrierType(String carrierType) {
            this.carrierType = carrierType;
        }

        public String getGeoCode() {
            return geoCode;
        }

        public void setGeoCode(String geoCode) {
            this.geoCode = geoCode;
        }

        public LocalDateTime getStartTime() {
            return startTime;
        }

        public void setStartTime(LocalDateTime startTime) {
            this.startTime = startTime;
        }

        public LocalDateTime getEndTime() {
            return endTime;
        }

        public void setEndTime(LocalDateTime endTime) {
            this.endTime = endTime;
        }

        public Integer getPage() {
            return page;
        }

        public void setPage(Integer page) {
            this.page = page;
        }

        public Integer getSize() {
            return size;
        }

        public void setSize(Integer size) {
            this.size = size;
        }
    }
}

