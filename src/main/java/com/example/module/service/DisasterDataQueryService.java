package com.example.module.service;

import com.example.module.entity.mongodb.RawData;
import com.example.module.util.DecodedId;
import com.example.module.util.Result;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 灾情数据查询服务接口
 * 支持按解码后的字段进行查询
 */
public interface DisasterDataQueryService {
    
    /**
     * 根据灾害大类查询
     * 
     * @param category 灾害大类（如：房屋破坏、人员伤亡等）
     * @return 数据列表
     */
    Result<List<RawData>> queryByDisasterCategory(String category);

    /**
     * 根据灾害子类查询
     * 
     * @param subcategory 灾害子类（如：砖木、框架等）
     * @return 数据列表
     */
    Result<List<RawData>> queryByDisasterSubcategory(String subcategory);

    /**
     * 根据来源查询
     * 
     * @param source 来源（如：后方指挥部、互联网感知等）
     * @return 数据列表
     */
    Result<List<RawData>> queryBySource(String source);

    /**
     * 根据载体类型查询
     * 
     * @param carrierType 载体类型（文字/图像/音频/视频）
     * @return 数据列表
     */
    Result<List<RawData>> queryByCarrierType(String carrierType);

    /**
     * 根据地理码查询
     * 
     * @param geoCode 地理码（12位）
     * @return 数据列表
     */
    Result<List<RawData>> queryByGeoCode(String geoCode);

    /**
     * 根据时间范围查询
     * 
     * @param start 开始时间
     * @param end 结束时间
     * @return 数据列表
     */
    Result<List<RawData>> queryByTimeRange(LocalDateTime start, LocalDateTime end);

    /**
     * 综合查询
     * 
     * @param queryParams 查询参数
     * @return 数据列表
     */
    Result<List<RawData>> queryByMultipleConditions(DisasterQueryParams queryParams);

    /**
     * 获取解码后的数据详情（包含解码信息）
     * 
     * @param id 数据ID
     * @return 包含解码信息的数据对象
     */
    Result<DisasterDataDetail> getDataDetailWithDecode(String id);

    /**
     * 查询参数类
     */
    class DisasterQueryParams {
        private String disasterCategory;
        private String disasterSubcategory;
        private String source;
        private String carrierType;
        private String geoCode;
        private LocalDateTime startTime;
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

    /**
     * 包含解码信息的数据详情
     */
    class DisasterDataDetail {
        private RawData rawData;
        private DecodedId decodedId;

        public RawData getRawData() {
            return rawData;
        }

        public void setRawData(RawData rawData) {
            this.rawData = rawData;
        }

        public DecodedId getDecodedId() {
            return decodedId;
        }

        public void setDecodedId(DecodedId decodedId) {
            this.decodedId = decodedId;
        }
    }
}

