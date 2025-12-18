package com.example.module.service.impl;

import com.example.module.entity.mongodb.RawData;
import com.example.module.repository.mongodb.RawDataRepository;
import com.example.module.service.DisasterDataProcessService;
import com.example.module.util.DecodedId;
import com.example.module.util.DisasterIdDecoder;
import com.example.module.util.Result;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 灾情数据处理服务实现类
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class DisasterDataProcessServiceImpl implements DisasterDataProcessService {

    private final RawDataRepository rawDataRepository;
    
    // 用于从字符串中提取36位ID的正则表达式
    private static final Pattern ID_PATTERN = Pattern.compile("\\d{36}");

    @Override
    public Result<RawData> processAndDecode(RawData rawData) {
        try {
            if (rawData == null) {
                return Result.error("数据不能为空");
            }

            // 如果已经有disasterId，直接解码
            if (rawData.getDisasterId() != null && rawData.getDisasterId().length() == 36) {
                return decodeAndUpdate(rawData, rawData.getDisasterId());
            }

            // 尝试从dataContent中提取ID
            if (rawData.getDataContent() != null) {
                String id = extractIdFromString(rawData.getDataContent());
                if (id != null) {
                    return decodeAndUpdate(rawData, id);
                }
            }

            // 尝试从id字段中提取（如果是36位）
            if (rawData.getId() != null && rawData.getId().length() == 36) {
                return decodeAndUpdate(rawData, rawData.getId());
            }

            log.warn("未找到36位ID，无法解码: {}", rawData.getId());
            return Result.success("数据中未包含36位ID，跳过解码", rawData);
        } catch (Exception e) {
            log.error("数据处理失败: ", e);
            return Result.error("数据处理失败: " + e.getMessage());
        }
    }

    @Override
    public Result<List<RawData>> batchProcessAndDecode(List<RawData> rawDataList) {
        try {
            if (rawDataList == null || rawDataList.isEmpty()) {
                return Result.error("数据列表不能为空");
            }

            List<RawData> processedList = new ArrayList<>();
            int successCount = 0;
            int failCount = 0;

            for (RawData rawData : rawDataList) {
                Result<RawData> result = processAndDecode(rawData);
                if (result.getCode() == 200 && result.getData() != null) {
                    processedList.add(result.getData());
                    successCount++;
                } else {
                    failCount++;
                    log.warn("批量处理中数据解码失败: {}", rawData.getId());
                }
            }

            String message = String.format("批量处理完成：成功%d条，失败%d条", successCount, failCount);
            return Result.success(message, processedList);
        } catch (Exception e) {
            log.error("批量处理异常: ", e);
            return Result.error("批量处理失败: " + e.getMessage());
        }
    }

    @Override
    public Result<RawData> processByIdString(RawData rawData, String idString) {
        try {
            if (rawData == null) {
                return Result.error("数据不能为空");
            }
            if (idString == null || idString.trim().isEmpty()) {
                return Result.error("ID字符串不能为空");
            }

            String id = extractIdFromString(idString);
            if (id == null) {
                return Result.error("未在字符串中找到36位ID");
            }

            return decodeAndUpdate(rawData, id);
        } catch (Exception e) {
            log.error("根据ID字符串处理失败: ", e);
            return Result.error("处理失败: " + e.getMessage());
        }
    }

    /**
     * 解码并更新数据
     */
    private Result<RawData> decodeAndUpdate(RawData rawData, String disasterId) {
        try {
            // 验证ID格式
            if (!DisasterIdDecoder.isValid(disasterId)) {
                return Result.error("ID格式不正确: " + disasterId);
            }

            // 解码ID
            DecodedId decoded = DisasterIdDecoder.decode(disasterId);

            // 更新RawData字段
            rawData.setDisasterId(disasterId);
            rawData.setGeoCode(decoded.getGeoCode());
            rawData.setDisasterDateTime(decoded.getDateTime());
            rawData.setSourceCategory(decoded.getSourceCategoryName());
            rawData.setSourceSubcategory(decoded.getSourceSubcategoryName());
            rawData.setCarrierType(decoded.getCarrierName());
            rawData.setDisasterCategory(decoded.getDisasterCategoryName());
            rawData.setDisasterSubcategory(decoded.getDisasterSubcategoryName());
            rawData.setDisasterIndicator(decoded.getDisasterIndicatorName());
            rawData.setDecodedDescription(decoded.getDescription());

            // 保存到数据库
            RawData saved = rawDataRepository.save(rawData);
            log.info("数据解码并更新成功: {}", saved.getId());

            return Result.success("解码并更新成功", saved);
        } catch (Exception e) {
            log.error("解码并更新失败: ", e);
            return Result.error("解码并更新失败: " + e.getMessage());
        }
    }

    /**
     * 从字符串中提取36位ID
     */
    private String extractIdFromString(String str) {
        if (str == null) {
            return null;
        }

        Matcher matcher = ID_PATTERN.matcher(str);
        if (matcher.find()) {
            return matcher.group();
        }

        return null;
    }
}

