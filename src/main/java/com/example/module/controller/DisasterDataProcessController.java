package com.example.module.controller;

import com.example.module.entity.mongodb.RawData;
import com.example.module.service.DisasterDataProcessService;
import com.example.module.util.Result;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 灾情数据处理控制器
 * 提供数据解码处理相关的接口
 */
@RestController
@RequestMapping("/api/disaster-process")
@RequiredArgsConstructor
public class DisasterDataProcessController {

    private final DisasterDataProcessService disasterDataProcessService;

    /**
     * 处理并解码单个数据
     */
    @PostMapping("/process")
    public Result<RawData> processAndDecode(@RequestBody RawData rawData) {
        return disasterDataProcessService.processAndDecode(rawData);
    }

    /**
     * 批量处理并解码
     */
    @PostMapping("/process/batch")
    public Result<List<RawData>> batchProcessAndDecode(@RequestBody List<RawData> rawDataList) {
        return disasterDataProcessService.batchProcessAndDecode(rawDataList);
    }

    /**
     * 根据ID字符串处理数据
     */
    @PostMapping("/process/by-id-string")
    public Result<RawData> processByIdString(
            @RequestBody ProcessByIdStringRequest request) {
        return disasterDataProcessService.processByIdString(request.getRawData(), request.getIdString());
    }

    /**
     * 处理请求类
     */
    public static class ProcessByIdStringRequest {
        private RawData rawData;
        private String idString;

        public RawData getRawData() {
            return rawData;
        }

        public void setRawData(RawData rawData) {
            this.rawData = rawData;
        }

        public String getIdString() {
            return idString;
        }

        public void setIdString(String idString) {
            this.idString = idString;
        }
    }
}

