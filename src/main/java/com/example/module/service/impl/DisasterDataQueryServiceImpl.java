package com.example.module.service.impl;

import com.example.module.entity.mysql.RawData;
import com.example.module.repository.mysql.MySqlRawDataRepository;
import com.example.module.service.DisasterDataQueryService;
import com.example.module.service.DisasterDecodeService;
import com.example.module.util.DecodedId;
import com.example.module.util.Result;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import javax.persistence.criteria.Predicate;
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

    private final MySqlRawDataRepository rawDataRepository;
    private final DisasterDecodeService disasterDecodeService;

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
            List<RawData> dataList = rawDataRepository.findBySourceCategory(source);
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
            // 使用JPA Specification构建查询
            Specification<RawData> spec = (root, query, cb) -> {
                List<Predicate> predicates = new ArrayList<>();

                // 添加查询条件（只有当条件不为空时才添加）
                if (StringUtils.hasText(queryParams.getDisasterCategory())) {
                    predicates.add(cb.equal(root.get("disasterCategory"), queryParams.getDisasterCategory()));
                }
                if (StringUtils.hasText(queryParams.getDisasterSubcategory())) {
                    predicates.add(cb.equal(root.get("disasterSubcategory"), queryParams.getDisasterSubcategory()));
                }
                if (StringUtils.hasText(queryParams.getSource())) {
                    predicates.add(cb.equal(root.get("sourceCategory"), queryParams.getSource()));
                }
                if (StringUtils.hasText(queryParams.getCarrierType())) {
                    predicates.add(cb.equal(root.get("carrierType"), queryParams.getCarrierType()));
                }
                if (StringUtils.hasText(queryParams.getGeoCode())) {
                    // 支持地理码前缀匹配（使用LIKE）
                    predicates.add(cb.like(root.get("geoCode"), queryParams.getGeoCode() + "%"));
                }

                // 时间范围查询
                if (queryParams.getStartTime() != null && queryParams.getEndTime() != null) {
                    predicates.add(cb.between(root.get("disasterDateTime"), 
                            queryParams.getStartTime(), queryParams.getEndTime()));
                } else if (queryParams.getStartTime() != null) {
                    predicates.add(cb.greaterThanOrEqualTo(root.get("disasterDateTime"), queryParams.getStartTime()));
                } else if (queryParams.getEndTime() != null) {
                    predicates.add(cb.lessThanOrEqualTo(root.get("disasterDateTime"), queryParams.getEndTime()));
                }

                // 如果没有查询条件，返回所有数据
                if (predicates.isEmpty()) {
                    return cb.conjunction(); // 返回true条件，即查询所有
                }
                
                return cb.and(predicates.toArray(new Predicate[0]));
            };

            // 分页处理
            List<RawData> dataList;
            if (queryParams.getPage() != null && queryParams.getSize() != null) {
                int page = queryParams.getPage() > 0 ? queryParams.getPage() - 1 : 0;
                int size = queryParams.getSize() > 0 ? queryParams.getSize() : 10;
                Pageable pageable = PageRequest.of(page, size);
                Page<RawData> pageResult = rawDataRepository.findAll(spec, pageable);
                dataList = pageResult.getContent();
            } else {
                // 不分页，返回所有数据
                dataList = rawDataRepository.findAll(spec);
            }

            return Result.success(dataList);
        } catch (Exception e) {
            log.error("综合查询失败: {}", e.getMessage());
            return Result.error("查询失败: " + e.getMessage());
        }
    }

    @Override
    public Result<DisasterDataDetail> getDataDetailWithDecode(Long id) {
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

