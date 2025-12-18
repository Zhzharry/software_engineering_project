package com.example.module.service;

import com.example.module.util.Result;

import java.util.List;
import java.util.Map;

/**
 * 地图可视化服务接口
 * 提供灾情点位数据、区域统计等地图展示所需的数据
 */
public interface MapService {

    /**
     * 获取所有灾情点位数据
     * 返回带有经纬度坐标的灾情点位列表，用于地图标注
     *
     * @param disasterCategory 灾害大类筛选（可选）
     * @param source 来源筛选（可选）
     * @param carrierType 载体类型筛选（可选）
     * @return 灾情点位列表
     */
    Result<List<DisasterPoint>> getDisasterPoints(String disasterCategory, String source, String carrierType);

    /**
     * 按区域统计灾情数量
     * 返回各省/市/区的灾情统计数据，用于热力图或区域着色
     *
     * @param level 统计级别: province(省), city(市), district(区)
     * @return 区域统计数据
     */
    Result<List<RegionStatistics>> getRegionStatistics(String level);

    /**
     * 获取聚合点位数据
     * 在指定缩放级别下，对临近的点位进行聚合
     *
     * @param zoomLevel 地图缩放级别 (1-18)
     * @param bounds 地图可视范围 (minLng,minLat,maxLng,maxLat)
     * @return 聚合后的点位数据
     */
    Result<List<ClusterPoint>> getClusterPoints(Integer zoomLevel, String bounds);

    /**
     * 获取灾情时间轴数据
     * 按时间顺序返回灾情发生的点位，用于时间轴动画
     *
     * @param startTime 开始时间 (ISO格式)
     * @param endTime 结束时间 (ISO格式)
     * @return 时间轴数据
     */
    Result<List<TimelinePoint>> getTimelineData(String startTime, String endTime);

    /**
     * 获取地图概览统计
     * 返回用于地图页面展示的概览统计数据
     *
     * @return 概览统计数据
     */
    Result<MapOverview> getMapOverview();

    /**
     * 灾情点位数据
     */
    class DisasterPoint {
        private String id;
        private Double longitude;
        private Double latitude;
        private String geoCode;
        private String province;
        private String city;
        private String district;
        private String address;
        private String disasterCategory;
        private String disasterSubcategory;
        private String disasterIndicator;
        private String source;
        private String carrierType;
        private String disasterDateTime;
        private String description;
        private String disasterId;

        // Getters and Setters
        public String getId() { return id; }
        public void setId(String id) { this.id = id; }
        public Double getLongitude() { return longitude; }
        public void setLongitude(Double longitude) { this.longitude = longitude; }
        public Double getLatitude() { return latitude; }
        public void setLatitude(Double latitude) { this.latitude = latitude; }
        public String getGeoCode() { return geoCode; }
        public void setGeoCode(String geoCode) { this.geoCode = geoCode; }
        public String getProvince() { return province; }
        public void setProvince(String province) { this.province = province; }
        public String getCity() { return city; }
        public void setCity(String city) { this.city = city; }
        public String getDistrict() { return district; }
        public void setDistrict(String district) { this.district = district; }
        public String getAddress() { return address; }
        public void setAddress(String address) { this.address = address; }
        public String getDisasterCategory() { return disasterCategory; }
        public void setDisasterCategory(String disasterCategory) { this.disasterCategory = disasterCategory; }
        public String getDisasterSubcategory() { return disasterSubcategory; }
        public void setDisasterSubcategory(String disasterSubcategory) { this.disasterSubcategory = disasterSubcategory; }
        public String getDisasterIndicator() { return disasterIndicator; }
        public void setDisasterIndicator(String disasterIndicator) { this.disasterIndicator = disasterIndicator; }
        public String getSource() { return source; }
        public void setSource(String source) { this.source = source; }
        public String getCarrierType() { return carrierType; }
        public void setCarrierType(String carrierType) { this.carrierType = carrierType; }
        public String getDisasterDateTime() { return disasterDateTime; }
        public void setDisasterDateTime(String disasterDateTime) { this.disasterDateTime = disasterDateTime; }
        public String getDescription() { return description; }
        public void setDescription(String description) { this.description = description; }
        public String getDisasterId() { return disasterId; }
        public void setDisasterId(String disasterId) { this.disasterId = disasterId; }
    }

    /**
     * 区域统计数据
     */
    class RegionStatistics {
        private String regionCode;
        private String regionName;
        private String level; // province, city, district
        private Integer totalCount;
        private Map<String, Integer> categoryCount; // 按灾害类型统计
        private Double centerLongitude;
        private Double centerLatitude;

        // Getters and Setters
        public String getRegionCode() { return regionCode; }
        public void setRegionCode(String regionCode) { this.regionCode = regionCode; }
        public String getRegionName() { return regionName; }
        public void setRegionName(String regionName) { this.regionName = regionName; }
        public String getLevel() { return level; }
        public void setLevel(String level) { this.level = level; }
        public Integer getTotalCount() { return totalCount; }
        public void setTotalCount(Integer totalCount) { this.totalCount = totalCount; }
        public Map<String, Integer> getCategoryCount() { return categoryCount; }
        public void setCategoryCount(Map<String, Integer> categoryCount) { this.categoryCount = categoryCount; }
        public Double getCenterLongitude() { return centerLongitude; }
        public void setCenterLongitude(Double centerLongitude) { this.centerLongitude = centerLongitude; }
        public Double getCenterLatitude() { return centerLatitude; }
        public void setCenterLatitude(Double centerLatitude) { this.centerLatitude = centerLatitude; }
    }

    /**
     * 聚合点位数据
     */
    class ClusterPoint {
        private Double longitude;
        private Double latitude;
        private Integer count;
        private List<String> pointIds;
        private String dominantCategory; // 占比最高的灾害类型

        // Getters and Setters
        public Double getLongitude() { return longitude; }
        public void setLongitude(Double longitude) { this.longitude = longitude; }
        public Double getLatitude() { return latitude; }
        public void setLatitude(Double latitude) { this.latitude = latitude; }
        public Integer getCount() { return count; }
        public void setCount(Integer count) { this.count = count; }
        public List<String> getPointIds() { return pointIds; }
        public void setPointIds(List<String> pointIds) { this.pointIds = pointIds; }
        public String getDominantCategory() { return dominantCategory; }
        public void setDominantCategory(String dominantCategory) { this.dominantCategory = dominantCategory; }
    }

    /**
     * 时间轴点位数据
     */
    class TimelinePoint {
        private String id;
        private Double longitude;
        private Double latitude;
        private String disasterDateTime;
        private String disasterCategory;
        private String description;
        private Long timestamp;

        // Getters and Setters
        public String getId() { return id; }
        public void setId(String id) { this.id = id; }
        public Double getLongitude() { return longitude; }
        public void setLongitude(Double longitude) { this.longitude = longitude; }
        public Double getLatitude() { return latitude; }
        public void setLatitude(Double latitude) { this.latitude = latitude; }
        public String getDisasterDateTime() { return disasterDateTime; }
        public void setDisasterDateTime(String disasterDateTime) { this.disasterDateTime = disasterDateTime; }
        public String getDisasterCategory() { return disasterCategory; }
        public void setDisasterCategory(String disasterCategory) { this.disasterCategory = disasterCategory; }
        public String getDescription() { return description; }
        public void setDescription(String description) { this.description = description; }
        public Long getTimestamp() { return timestamp; }
        public void setTimestamp(Long timestamp) { this.timestamp = timestamp; }
    }

    /**
     * 地图概览统计
     */
    class MapOverview {
        private Integer totalPoints;
        private Integer provincesAffected;
        private Integer citiesAffected;
        private Map<String, Integer> categoryDistribution;
        private Map<String, Integer> sourceDistribution;
        private String lastUpdateTime;

        // Getters and Setters
        public Integer getTotalPoints() { return totalPoints; }
        public void setTotalPoints(Integer totalPoints) { this.totalPoints = totalPoints; }
        public Integer getProvincesAffected() { return provincesAffected; }
        public void setProvincesAffected(Integer provincesAffected) { this.provincesAffected = provincesAffected; }
        public Integer getCitiesAffected() { return citiesAffected; }
        public void setCitiesAffected(Integer citiesAffected) { this.citiesAffected = citiesAffected; }
        public Map<String, Integer> getCategoryDistribution() { return categoryDistribution; }
        public void setCategoryDistribution(Map<String, Integer> categoryDistribution) { this.categoryDistribution = categoryDistribution; }
        public Map<String, Integer> getSourceDistribution() { return sourceDistribution; }
        public void setSourceDistribution(Map<String, Integer> sourceDistribution) { this.sourceDistribution = sourceDistribution; }
        public String getLastUpdateTime() { return lastUpdateTime; }
        public void setLastUpdateTime(String lastUpdateTime) { this.lastUpdateTime = lastUpdateTime; }
    }
}
