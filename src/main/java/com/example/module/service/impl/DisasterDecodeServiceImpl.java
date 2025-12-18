package com.example.module.service.impl;

import com.example.module.service.DisasterDecodeService;
import com.example.module.util.DecodedId;
import com.example.module.util.DisasterIdDecoder;
import com.example.module.util.Result;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * 灾情ID解码服务实现类
 */
@Slf4j
@Service
public class DisasterDecodeServiceImpl implements DisasterDecodeService {

    @Override
    public Result<DecodedId> decodeId(String id) {
        try {
            if (id == null || id.trim().isEmpty()) {
                return Result.error("ID不能为空");
            }

            DecodedId decoded = DisasterIdDecoder.decode(id.trim());
            return Result.success("解码成功", decoded);
        } catch (IllegalArgumentException e) {
            log.error("ID解码失败: {}", e.getMessage());
            return Result.error(400, e.getMessage());
        } catch (Exception e) {
            log.error("ID解码异常: ", e);
            return Result.error("解码过程中发生异常: " + e.getMessage());
        }
    }

    @Override
    public Result<List<DecodedId>> batchDecodeIds(List<String> ids) {
        try {
            if (ids == null || ids.isEmpty()) {
                return Result.error("ID列表不能为空");
            }

            List<DecodedId> decodedList = new ArrayList<>();
            List<String> errors = new ArrayList<>();

            for (int i = 0; i < ids.size(); i++) {
                String id = ids.get(i);
                if (id == null || id.trim().isEmpty()) {
                    errors.add("第" + (i + 1) + "个ID为空");
                    continue;
                }

                try {
                    DecodedId decoded = DisasterIdDecoder.decode(id.trim());
                    decodedList.add(decoded);
                } catch (Exception e) {
                    errors.add("第" + (i + 1) + "个ID解码失败: " + e.getMessage());
                    log.warn("批量解码中第{}个ID失败: {}", i + 1, id);
                }
            }

            if (decodedList.isEmpty() && !errors.isEmpty()) {
                return Result.error(400, "所有ID解码失败: " + String.join("; ", errors));
            }

            String message = "成功解码" + decodedList.size() + "个ID";
            if (!errors.isEmpty()) {
                message += "，失败" + errors.size() + "个";
            }

            return Result.success(message, decodedList);
        } catch (Exception e) {
            log.error("批量解码异常: ", e);
            return Result.error("批量解码过程中发生异常: " + e.getMessage());
        }
    }

    @Override
    public Result<Boolean> validateId(String id) {
        try {
            if (id == null || id.trim().isEmpty()) {
                return Result.success("ID为空", false);
            }

            boolean isValid = DisasterIdDecoder.isValid(id.trim());
            return Result.success(isValid ? "ID格式正确" : "ID格式不正确", isValid);
        } catch (Exception e) {
            log.error("ID验证异常: ", e);
            return Result.error("验证过程中发生异常: " + e.getMessage());
        }
    }
}

