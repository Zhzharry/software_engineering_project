package com.example.module.service;

import com.example.module.entity.mongodb.RawData;
import com.example.module.util.Result;

/**
 * 灾情数据处理服务接口
 * 用于自动解码ID并更新数据
 */
public interface DisasterDataProcessService {
    
    /**
     * 处理并解码RawData中的ID
     * 如果数据中包含36位ID，自动解码并更新相关字段
     * 
     * @param rawData 原始数据
     * @return 处理后的数据
     */
    Result<RawData> processAndDecode(RawData rawData);

    /**
     * 批量处理并解码
     * 
     * @param rawDataList 原始数据列表
     * @return 处理后的数据列表
     */
    Result<java.util.List<RawData>> batchProcessAndDecode(java.util.List<RawData> rawDataList);

    /**
     * 根据ID字符串处理数据
     * 从ID字符串中提取36位ID并解码
     * 
     * @param rawData 原始数据
     * @param idString 包含36位ID的字符串（可能是文件名、数据内容等）
     * @return 处理后的数据
     */
    Result<RawData> processByIdString(RawData rawData, String idString);
}

