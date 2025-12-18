package com.example.module.service.impl;

import com.example.module.entity.mongodb.RawData;
import com.example.module.repository.mongodb.RawDataRepository;
import com.example.module.service.GeoLocationService;
import com.example.module.service.MapService;
import com.example.module.util.Result;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationResults;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 地图可视化服务实现
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class MapServiceImpl implements MapService {

    private final RawDataRepository rawDataRepository;
    private final MongoTemplate mongoTemplate;
    private final GeoLocationService geoLocationService;

    // 省份中心坐标缓存（简化版，实际可从数据库或配置加载）
    private static final Map<String, double[]> PROVINCE_CENTERS = new HashMap<>();
    static {
        PROVINCE_CENTERS.put("51", new double[]{104.065735, 30.659462}); // 四川
        PROVINCE_CENTERS.put("11", new double[]{116.405285, 39.904989}); // 北京
        PROVINCE_CENTERS.put("31", new double[]{121.472644, 31.231706}); // 上海
        PROVINCE_CENTERS.put("44", new double[]{113.280637, 23.125178}); // 广东
        PROVINCE_CENTERS.put("33", new double[]{120.153576, 30.287459}); // 浙江
        PROVINCE_CENTERS.put("32", new double[]{118.767413, 32.041544}); // 江苏
        PROVINCE_CENTERS.put("37", new double[]{117.000923, 36.675807}); // 山东
        PROVINCE_CENTERS.put("41", new double[]{113.665412, 34.757975}); // 河南
        PROVINCE_CENTERS.put("42", new double[]{114.298572, 30.584355}); // 湖北
        PROVINCE_CENTERS.put("43", new double[]{112.982279, 28.19409});  // 湖南
        PROVINCE_CENTERS.put("50", new double[]{106.504962, 29.533155}); // 重庆
        PROVINCE_CENTERS.put("61", new double[]{108.948024, 34.263161}); // 陕西
        PROVINCE_CENTERS.put("52", new double[]{106.713478, 26.578343}); // 贵州
        PROVINCE_CENTERS.put("53", new double[]{102.712251, 25.040609}); // 云南
        PROVINCE_CENTERS.put("62", new double[]{103.823557, 36.058039}); // 甘肃
        PROVINCE_CENTERS.put("64", new double[]{106.278179, 38.46637});  // 宁夏
        PROVINCE_CENTERS.put("63", new double[]{101.778916, 36.623178}); // 青海
        PROVINCE_CENTERS.put("54", new double[]{91.132212, 29.660361});  // 西藏
        PROVINCE_CENTERS.put("65", new double[]{87.617733, 43.792818});  // 新疆
        PROVINCE_CENTERS.put("15", new double[]{111.670801, 40.818311}); // 内蒙古
        PROVINCE_CENTERS.put("21", new double[]{123.429096, 41.796767}); // 辽宁
        PROVINCE_CENTERS.put("22", new double[]{125.3245, 43.886841});   // 吉林
        PROVINCE_CENTERS.put("23", new double[]{126.642464, 45.756967}); // 黑龙江
        PROVINCE_CENTERS.put("13", new double[]{114.502461, 38.045474}); // 河北
        PROVINCE_CENTERS.put("14", new double[]{112.549248, 37.857014}); // 山西
        PROVINCE_CENTERS.put("34", new double[]{117.283042, 31.86119});  // 安徽
        PROVINCE_CENTERS.put("35", new double[]{119.306239, 26.075302}); // 福建
        PROVINCE_CENTERS.put("36", new double[]{115.892151, 28.676493}); // 江西
        PROVINCE_CENTERS.put("45", new double[]{108.320004, 22.82402});  // 广西
        PROVINCE_CENTERS.put("46", new double[]{110.33119, 20.031971});  // 海南
        PROVINCE_CENTERS.put("12", new double[]{117.190182, 39.125596}); // 天津
    }

    @Override
    public Result<List<DisasterPoint>> getDisasterPoints(String disasterCategory, String source, String carrierType) {
        try {
            Query query = new Query();

            // 只查询有地理码的数据
            query.addCriteria(Criteria.where("geo_code").exists(true).ne(null).ne(""));

            // 添加筛选条件
            if (StringUtils.hasText(disasterCategory)) {
                query.addCriteria(Criteria.where("disaster_category").is(disasterCategory));
            }
            if (StringUtils.hasText(source)) {
                query.addCriteria(Criteria.where("source_category").is(source));
            }
            if (StringUtils.hasText(carrierType)) {
                query.addCriteria(Criteria.where("carrier_type").is(carrierType));
            }

            List<RawData> rawDataList = mongoTemplate.find(query, RawData.class);
            List<DisasterPoint> points = new ArrayList<>();

            for (RawData data : rawDataList) {
                DisasterPoint point = convertToDisasterPoint(data);
                if (point != null && point.getLongitude() != null && point.getLatitude() != null) {
                    points.add(point);
                }
            }

            return Result.success(points);
        } catch (Exception e) {
            log.error("获取灾情点位数据失败", e);
            return Result.error("获取灾情点位数据失败: " + e.getMessage());
        }
    }

    @Override
    public Result<List<RegionStatistics>> getRegionStatistics(String level) {
        try {
            String groupField;
            int codeLength;

            switch (level != null ? level.toLowerCase() : "province") {
                case "city":
                    groupField = "cityCode";
                    codeLength = 4;
                    break;
                case "district":
                    groupField = "districtCode";
                    codeLength = 6;
                    break;
                case "province":
                default:
                    groupField = "provinceCode";
                    codeLength = 2;
                    break;
            }

            // 查询所有有地理码的数据
            Query query = new Query();
            query.addCriteria(Criteria.where("geo_code").exists(true).ne(null).ne(""));
            List<RawData> rawDataList = mongoTemplate.find(query, RawData.class);

            // 按区域分组统计
            Map<String, List<RawData>> regionGroups = new HashMap<>();
            for (RawData data : rawDataList) {
                String geoCode = data.getGeoCode();
                if (geoCode != null && geoCode.length() >= codeLength) {
                    String regionCode = geoCode.substring(0, codeLength);
                    regionGroups.computeIfAbsent(regionCode, k -> new ArrayList<>()).add(data);
                }
            }

            // 生成统计结果
            List<RegionStatistics> statistics = new ArrayList<>();
            for (Map.Entry<String, List<RawData>> entry : regionGroups.entrySet()) {
                RegionStatistics stat = new RegionStatistics();
                stat.setRegionCode(entry.getKey());
                stat.setLevel(level != null ? level : "province");
                stat.setTotalCount(entry.getValue().size());

                // 按灾害类型统计
                Map<String, Integer> categoryCount = new HashMap<>();
                for (RawData data : entry.getValue()) {
                    String category = data.getDisasterCategory();
                    if (category != null) {
                        categoryCount.merge(category, 1, Integer::sum);
                    }
                }
                stat.setCategoryCount(categoryCount);

                // 设置区域中心坐标
                if (codeLength == 2 && PROVINCE_CENTERS.containsKey(entry.getKey())) {
                    double[] center = PROVINCE_CENTERS.get(entry.getKey());
                    stat.setCenterLongitude(center[0]);
                    stat.setCenterLatitude(center[1]);
                }

                // 尝试获取区域名称
                stat.setRegionName(getRegionName(entry.getKey(), level));

                statistics.add(stat);
            }

            // 按数量排序
            statistics.sort((a, b) -> b.getTotalCount().compareTo(a.getTotalCount()));

            return Result.success(statistics);
        } catch (Exception e) {
            log.error("获取区域统计数据失败", e);
            return Result.error("获取区域统计数据失败: " + e.getMessage());
        }
    }

    @Override
    public Result<List<ClusterPoint>> getClusterPoints(Integer zoomLevel, String bounds) {
        try {
            // 解析边界
            double minLng = 73.0, minLat = 3.0, maxLng = 136.0, maxLat = 54.0;
            if (StringUtils.hasText(bounds)) {
                String[] parts = bounds.split(",");
                if (parts.length == 4) {
                    minLng = Double.parseDouble(parts[0]);
                    minLat = Double.parseDouble(parts[1]);
                    maxLng = Double.parseDouble(parts[2]);
                    maxLat = Double.parseDouble(parts[3]);
                }
            }

            // 根据缩放级别计算聚合半径（度数）
            double clusterRadius = calculateClusterRadius(zoomLevel != null ? zoomLevel : 5);

            // 获取所有点位
            Result<List<DisasterPoint>> pointsResult = getDisasterPoints(null, null, null);
            if (!pointsResult.isSuccess()) {
                return Result.error(pointsResult.getMessage());
            }

            List<DisasterPoint> allPoints = pointsResult.getData();

            // 过滤边界内的点
            List<DisasterPoint> filteredPoints = allPoints.stream()
                    .filter(p -> p.getLongitude() >= minLng && p.getLongitude() <= maxLng
                            && p.getLatitude() >= minLat && p.getLatitude() <= maxLat)
                    .collect(Collectors.toList());

            // 执行聚合
            List<ClusterPoint> clusters = performClustering(filteredPoints, clusterRadius);

            return Result.success(clusters);
        } catch (Exception e) {
            log.error("获取聚合点位数据失败", e);
            return Result.error("获取聚合点位数据失败: " + e.getMessage());
        }
    }

    @Override
    public Result<List<TimelinePoint>> getTimelineData(String startTime, String endTime) {
        try {
            Query query = new Query();
            query.addCriteria(Criteria.where("geo_code").exists(true).ne(null).ne(""));
            query.addCriteria(Criteria.where("disaster_date_time").exists(true).ne(null));

            // 时间范围筛选
            if (StringUtils.hasText(startTime) && StringUtils.hasText(endTime)) {
                LocalDateTime start = LocalDateTime.parse(startTime, DateTimeFormatter.ISO_DATE_TIME);
                LocalDateTime end = LocalDateTime.parse(endTime, DateTimeFormatter.ISO_DATE_TIME);
                query.addCriteria(Criteria.where("disaster_date_time").gte(start).lte(end));
            }

            List<RawData> rawDataList = mongoTemplate.find(query, RawData.class);
            List<TimelinePoint> timelinePoints = new ArrayList<>();

            for (RawData data : rawDataList) {
                DisasterPoint dp = convertToDisasterPoint(data);
                if (dp != null && dp.getLongitude() != null && dp.getLatitude() != null) {
                    TimelinePoint tp = new TimelinePoint();
                    tp.setId(data.getId());
                    tp.setLongitude(dp.getLongitude());
                    tp.setLatitude(dp.getLatitude());
                    tp.setDisasterCategory(data.getDisasterCategory());
                    tp.setDescription(data.getDecodedDescription());

                    if (data.getDisasterDateTime() != null) {
                        tp.setDisasterDateTime(data.getDisasterDateTime().format(DateTimeFormatter.ISO_DATE_TIME));
                        tp.setTimestamp(data.getDisasterDateTime().toInstant(ZoneOffset.UTC).toEpochMilli());
                    }

                    timelinePoints.add(tp);
                }
            }

            // 按时间排序
            timelinePoints.sort(Comparator.comparing(TimelinePoint::getTimestamp, Comparator.nullsLast(Comparator.naturalOrder())));

            return Result.success(timelinePoints);
        } catch (Exception e) {
            log.error("获取时间轴数据失败", e);
            return Result.error("获取时间轴数据失败: " + e.getMessage());
        }
    }

    @Override
    public Result<MapOverview> getMapOverview() {
        try {
            MapOverview overview = new MapOverview();

            // 查询有地理码的数据
            Query query = new Query();
            query.addCriteria(Criteria.where("geo_code").exists(true).ne(null).ne(""));
            List<RawData> rawDataList = mongoTemplate.find(query, RawData.class);

            overview.setTotalPoints(rawDataList.size());

            // 统计受影响的省份和城市
            Set<String> provinces = new HashSet<>();
            Set<String> cities = new HashSet<>();
            Map<String, Integer> categoryDist = new HashMap<>();
            Map<String, Integer> sourceDist = new HashMap<>();
            LocalDateTime lastUpdate = null;

            for (RawData data : rawDataList) {
                String geoCode = data.getGeoCode();
                if (geoCode != null) {
                    if (geoCode.length() >= 2) {
                        provinces.add(geoCode.substring(0, 2));
                    }
                    if (geoCode.length() >= 4) {
                        cities.add(geoCode.substring(0, 4));
                    }
                }

                // 灾害类型分布
                if (data.getDisasterCategory() != null) {
                    categoryDist.merge(data.getDisasterCategory(), 1, Integer::sum);
                }

                // 来源分布
                if (data.getSourceCategory() != null) {
                    sourceDist.merge(data.getSourceCategory(), 1, Integer::sum);
                }

                // 最后更新时间
                if (data.getUpdateTime() != null) {
                    if (lastUpdate == null || data.getUpdateTime().isAfter(lastUpdate)) {
                        lastUpdate = data.getUpdateTime();
                    }
                }
            }

            overview.setProvincesAffected(provinces.size());
            overview.setCitiesAffected(cities.size());
            overview.setCategoryDistribution(categoryDist);
            overview.setSourceDistribution(sourceDist);

            if (lastUpdate != null) {
                overview.setLastUpdateTime(lastUpdate.format(DateTimeFormatter.ISO_DATE_TIME));
            }

            return Result.success(overview);
        } catch (Exception e) {
            log.error("获取地图概览数据失败", e);
            return Result.error("获取地图概览数据失败: " + e.getMessage());
        }
    }

    /**
     * 将RawData转换为DisasterPoint
     */
    private DisasterPoint convertToDisasterPoint(RawData data) {
        if (data == null || data.getGeoCode() == null || data.getGeoCode().isEmpty()) {
            return null;
        }

        DisasterPoint point = new DisasterPoint();
        point.setId(data.getId());
        point.setGeoCode(data.getGeoCode());
        point.setDisasterCategory(data.getDisasterCategory());
        point.setDisasterSubcategory(data.getDisasterSubcategory());
        point.setDisasterIndicator(data.getDisasterIndicator());
        point.setSource(data.getSourceCategory());
        point.setCarrierType(data.getCarrierType());
        point.setDescription(data.getDecodedDescription());
        point.setDisasterId(data.getDisasterId());

        if (data.getDisasterDateTime() != null) {
            point.setDisasterDateTime(data.getDisasterDateTime().format(DateTimeFormatter.ISO_DATE_TIME));
        }

        // 尝试从地理码获取经纬度
        try {
            Result<GeoLocationService.GeoLocationInfo> geoResult = geoLocationService.locateByGeoCode(data.getGeoCode());
            if (geoResult.isSuccess() && geoResult.getData() != null) {
                GeoLocationService.GeoLocationInfo geoInfo = geoResult.getData();
                point.setLongitude(geoInfo.getLongitude());
                point.setLatitude(geoInfo.getLatitude());
                point.setProvince(geoInfo.getProvince());
                point.setCity(geoInfo.getCity());
                point.setDistrict(geoInfo.getDistrict());
                point.setAddress(geoInfo.getFormattedAddress());
            } else {
                // 使用省份中心点作为备用
                String provinceCode = data.getGeoCode().substring(0, 2);
                if (PROVINCE_CENTERS.containsKey(provinceCode)) {
                    double[] center = PROVINCE_CENTERS.get(provinceCode);
                    point.setLongitude(center[0]);
                    point.setLatitude(center[1]);
                }
            }
        } catch (Exception e) {
            log.warn("解析地理码失败: {}", data.getGeoCode(), e);
            // 使用省份中心点作为备用
            if (data.getGeoCode().length() >= 2) {
                String provinceCode = data.getGeoCode().substring(0, 2);
                if (PROVINCE_CENTERS.containsKey(provinceCode)) {
                    double[] center = PROVINCE_CENTERS.get(provinceCode);
                    point.setLongitude(center[0]);
                    point.setLatitude(center[1]);
                }
            }
        }

        return point;
    }

    /**
     * 根据缩放级别计算聚合半径
     */
    private double calculateClusterRadius(int zoomLevel) {
        // 缩放级别越大，聚合半径越小
        return 5.0 / Math.pow(2, zoomLevel - 3);
    }

    /**
     * 执行点位聚合
     */
    private List<ClusterPoint> performClustering(List<DisasterPoint> points, double radius) {
        List<ClusterPoint> clusters = new ArrayList<>();
        boolean[] visited = new boolean[points.size()];

        for (int i = 0; i < points.size(); i++) {
            if (visited[i]) continue;

            DisasterPoint p = points.get(i);
            List<String> clusterIds = new ArrayList<>();
            clusterIds.add(p.getId());
            double sumLng = p.getLongitude();
            double sumLat = p.getLatitude();
            Map<String, Integer> categoryCount = new HashMap<>();
            if (p.getDisasterCategory() != null) {
                categoryCount.put(p.getDisasterCategory(), 1);
            }
            visited[i] = true;

            // 查找邻近点
            for (int j = i + 1; j < points.size(); j++) {
                if (visited[j]) continue;
                DisasterPoint other = points.get(j);
                double dist = Math.sqrt(Math.pow(p.getLongitude() - other.getLongitude(), 2)
                        + Math.pow(p.getLatitude() - other.getLatitude(), 2));
                if (dist <= radius) {
                    visited[j] = true;
                    clusterIds.add(other.getId());
                    sumLng += other.getLongitude();
                    sumLat += other.getLatitude();
                    if (other.getDisasterCategory() != null) {
                        categoryCount.merge(other.getDisasterCategory(), 1, Integer::sum);
                    }
                }
            }

            ClusterPoint cluster = new ClusterPoint();
            cluster.setLongitude(sumLng / clusterIds.size());
            cluster.setLatitude(sumLat / clusterIds.size());
            cluster.setCount(clusterIds.size());
            cluster.setPointIds(clusterIds);

            // 找出占比最高的灾害类型
            String dominant = categoryCount.entrySet().stream()
                    .max(Map.Entry.comparingByValue())
                    .map(Map.Entry::getKey)
                    .orElse(null);
            cluster.setDominantCategory(dominant);

            clusters.add(cluster);
        }

        return clusters;
    }

    /**
     * 获取区域名称
     */
    private String getRegionName(String regionCode, String level) {
        // 简化版：实际可从数据库或配置加载
        Map<String, String> provinceNames = new HashMap<>();
        provinceNames.put("11", "北京市");
        provinceNames.put("12", "天津市");
        provinceNames.put("13", "河北省");
        provinceNames.put("14", "山西省");
        provinceNames.put("15", "内蒙古自治区");
        provinceNames.put("21", "辽宁省");
        provinceNames.put("22", "吉林省");
        provinceNames.put("23", "黑龙江省");
        provinceNames.put("31", "上海市");
        provinceNames.put("32", "江苏省");
        provinceNames.put("33", "浙江省");
        provinceNames.put("34", "安徽省");
        provinceNames.put("35", "福建省");
        provinceNames.put("36", "江西省");
        provinceNames.put("37", "山东省");
        provinceNames.put("41", "河南省");
        provinceNames.put("42", "湖北省");
        provinceNames.put("43", "湖南省");
        provinceNames.put("44", "广东省");
        provinceNames.put("45", "广西壮族自治区");
        provinceNames.put("46", "海南省");
        provinceNames.put("50", "重庆市");
        provinceNames.put("51", "四川省");
        provinceNames.put("52", "贵州省");
        provinceNames.put("53", "云南省");
        provinceNames.put("54", "西藏自治区");
        provinceNames.put("61", "陕西省");
        provinceNames.put("62", "甘肃省");
        provinceNames.put("63", "青海省");
        provinceNames.put("64", "宁夏回族自治区");
        provinceNames.put("65", "新疆维吾尔自治区");

        if ("province".equals(level) && provinceNames.containsKey(regionCode)) {
            return provinceNames.get(regionCode);
        }

        return "区域" + regionCode;
    }
}
