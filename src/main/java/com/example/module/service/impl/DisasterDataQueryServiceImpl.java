package com.example.module.service.impl;

import com.example.module.entity.mongodb.RawData;
import com.example.module.repository.mongodb.RawDataRepository;
import com.example.module.service.DisasterDataQueryService;
import com.example.module.service.DisasterDecodeService;
import com.example.module.util.DecodedId;
import com.example.module.util.Result;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * 灾情数据查询服务实现类
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class DisasterDataQueryServiceImpl implements DisasterDataQueryService {

    private final RawDataRepository rawDataRepository;
    private final DisasterDecodeService disasterDecodeService;
    private final MongoTemplate mongoTemplate;

    @Override
    public Result<List<RawData>> queryByDisasterCategory(String category) {
        try {
            List<RawData> dataList = rawDataRepository.findByDisasterCategory(category);
            return Result.success(dataList);
        } catch (Exception e) {
            log.error("根据灾害大类查询失败: {}", e.getMessage());
            return Result.error("查询失败: " + e.getMessage());
        }
    }

    @Override
    public Result<List<RawData>> queryByDisasterSubcategory(String subcategory) {
        try {
            List<RawData> dataList = rawDataRepository.findByDisasterSubcategory(subcategory);
            return Result.success(dataList);
        } catch (Exception e) {
            log.error("根据灾害子类查询失败: {}", e.getMessage());
            return Result.error("查询失败: " + e.getMessage());
        }
    }

    @Override
    public Result<List<RawData>> queryBySource(String source) {
        try {
            List<RawData> dataList = rawDataRepository.findBySourceSubcategory(source);
            return Result.success(dataList);
        } catch (Exception e) {
            log.error("根据来源查询失败: {}", e.getMessage());
            return Result.error("查询失败: " + e.getMessage());
        }
    }

    @Override
    public Result<List<RawData>> queryByCarrierType(String carrierType) {
        try {
            List<RawData> dataList = rawDataRepository.findByCarrierType(carrierType);
            return Result.success(dataList);
        } catch (Exception e) {
            log.error("根据载体类型查询失败: {}", e.getMessage());
            return Result.error("查询失败: " + e.getMessage());
        }
    }

    @Override
    public Result<List<RawData>> queryByGeoCode(String geoCode) {
        try {
            List<RawData> dataList = rawDataRepository.findByGeoCode(geoCode);
            return Result.success(dataList);
        } catch (Exception e) {
            log.error("根据地理码查询失败: {}", e.getMessage());
            return Result.error("查询失败: " + e.getMessage());
        }
    }

    @Override
    public Result<List<RawData>> queryByTimeRange(LocalDateTime start, LocalDateTime end) {
        try {
            List<RawData> dataList = rawDataRepository.findByDisasterDateTimeBetween(start, end);
            return Result.success(dataList);
        } catch (Exception e) {
            log.error("根据时间范围查询失败: {}", e.getMessage());
            return Result.error("查询失败: " + e.getMessage());
        }
    }

    @Override
    public Result<List<RawData>> queryByMultipleConditions(DisasterQueryParams queryParams) {
        try {
            // 使用MongoTemplate构建查询，正确处理@Field注解的字段名
            Query query = new Query();

            // 添加查询条件（使用MongoDB文档中的实际字段名）
            if (queryParams.getDisasterCategory() != null && !queryParams.getDisasterCategory().isEmpty()) {
                query.addCriteria(Criteria.where("disaster_category").is(queryParams.getDisasterCategory()));
            }
            if (queryParams.getDisasterSubcategory() != null && !queryParams.getDisasterSubcategory().isEmpty()) {
                query.addCriteria(Criteria.where("disaster_subcategory").is(queryParams.getDisasterSubcategory()));
            }
            if (queryParams.getSource() != null && !queryParams.getSource().isEmpty()) {
                query.addCriteria(Criteria.where("source_subcategory").is(queryParams.getSource()));
            }
            if (queryParams.getCarrierType() != null && !queryParams.getCarrierType().isEmpty()) {
                query.addCriteria(Criteria.where("carrier_type").is(queryParams.getCarrierType()));
            }
            if (queryParams.getGeoCode() != null && !queryParams.getGeoCode().isEmpty()) {
                // 支持地理码前缀匹配
                query.addCriteria(Criteria.where("geo_code").regex("^" + queryParams.getGeoCode()));
            }

            // 时间范围查询
            if (queryParams.getStartTime() != null && queryParams.getEndTime() != null) {
                query.addCriteria(Criteria.where("disaster_date_time")
                        .gte(queryParams.getStartTime())
                        .lte(queryParams.getEndTime()));
            } else if (queryParams.getStartTime() != null) {
                query.addCriteria(Criteria.where("disaster_date_time").gte(queryParams.getStartTime()));
            } else if (queryParams.getEndTime() != null) {
                query.addCriteria(Criteria.where("disaster_date_time").lte(queryParams.getEndTime()));
            }

            // 执行查询
            List<RawData> dataList = mongoTemplate.find(query, RawData.class);

            // 分页处理
            if (queryParams.getPage() != null && queryParams.getSize() != null) {
                int page = queryParams.getPage() > 0 ? queryParams.getPage() - 1 : 0;
                int size = queryParams.getSize() > 0 ? queryParams.getSize() : 10;
                int start = page * size;
                int end = Math.min(start + size, dataList.size());
                if (start < dataList.size()) {
                    dataList = dataList.subList(start, end);
                } else {
                    dataList = new ArrayList<>();
                }
            }

            return Result.success(dataList);
        } catch (Exception e) {
            log.error("综合查询失败: {}", e.getMessage());
            return Result.error("查询失败: " + e.getMessage());
        }
    }

    @Override
    public Result<DisasterDataDetail> getDataDetailWithDecode(String id) {
        try {
            Optional<RawData> rawDataOpt = rawDataRepository.findById(id);
            if (!rawDataOpt.isPresent()) {
                return Result.error("数据不存在");
            }

            RawData rawData = rawDataOpt.get();
            DisasterDataDetail detail = new DisasterDataDetail();
            detail.setRawData(rawData);

            // 如果数据中有disasterId，进行解码
            if (rawData.getDisasterId() != null) {
                Result<DecodedId> decodeResult = disasterDecodeService.decodeId(rawData.getDisasterId());
                if (decodeResult.getCode() == 200 && decodeResult.getData() != null) {
                    detail.setDecodedId(decodeResult.getData());
                }
            }

            return Result.success(detail);
        } catch (Exception e) {
            log.error("获取数据详情失败: {}", e.getMessage());
            return Result.error("获取数据详情失败: " + e.getMessage());
        }
    }
}

