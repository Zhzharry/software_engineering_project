package com.example.module.service;

import com.example.module.util.DecodedId;
import com.example.module.util.Result;

import java.util.List;

/**
 * 灾情ID解码服务接口
 */
public interface DisasterDecodeService {
    
    /**
     * 解码单个ID
     * 
     * @param id 36位一体化编码ID
     * @return 解码结果
     */
    Result<DecodedId> decodeId(String id);

    /**
     * 批量解码ID
     * 
     * @param ids ID列表
     * @return 解码结果列表
     */
    Result<List<DecodedId>> batchDecodeIds(List<String> ids);

    /**
     * 验证ID格式
     * 
     * @param id 36位一体化编码ID
     * @return 验证结果
     */
    Result<Boolean> validateId(String id);
}

