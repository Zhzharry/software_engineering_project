package com.example.module.service.impl;

import com.example.module.service.GeoLocationService;
import com.example.module.util.GeoCodeParser;
import com.example.module.util.Result;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;

/**
 * 地理信息服务实现类
 * 集成高德地图API进行地理信息解析
 */
@Slf4j
@Service
public class GeoLocationServiceImpl implements GeoLocationService {

    @Value("${amap.api.key:514cde08eadb88096bcf0fe0a11f5e88}")
    private String amapApiKey;

    @Value("${amap.api.security:bed34790b035008203b5ea72cb23920d}")
    private String amapSecurity;

    private final RestTemplate restTemplate = new RestTemplate();
    private final ObjectMapper objectMapper = new ObjectMapper();

    // 高德API基础URL
    private static final String AMAP_GEOCODE_URL = "https://restapi.amap.com/v3/geocode/geo";
    private static final String AMAP_REGEOCODE_URL = "https://restapi.amap.com/v3/geocode/regeo";
    private static final String AMAP_DISTRICT_URL = "https://restapi.amap.com/v3/config/district";

    @Override
    public Result<GeoLocationInfo> getLocationByGeoCode(String geoCode) {
        // 调用新的定位方法
        return locateByGeoCode(geoCode);
    }

    @Override
    public Result<GeoLocationInfo> locateByGeoCode(String geoCode) {
        try {
            if (geoCode == null || geoCode.length() != 12) {
                return Result.error("地理码必须为12位");
            }

            // 解析地理码的层级结构
            GeoCodeParser.GeoCodeInfo geoCodeInfo = GeoCodeParser.parse(geoCode);
            
            GeoLocationInfo info = new GeoLocationInfo();
            info.setGeoCode(geoCode);
            info.setProvinceCode(geoCodeInfo.getProvinceCode());
            info.setCityCode(geoCodeInfo.getCityCode());
            info.setDistrictCode(geoCodeInfo.getDistrictCode());
            info.setTownshipCode(geoCodeInfo.getTownshipCode());
            info.setVillageCode(geoCodeInfo.getVillageCode());

            // 使用高德地图行政区划查询API获取行政区划信息
            // 首先查询省级信息
            Result<GeoLocationInfo> provinceResult = queryDistrictByCode(geoCodeInfo.getProvinceLevelCode(), 1);
            if (provinceResult.getCode() == 200 && provinceResult.getData() != null) {
                GeoLocationInfo provinceInfo = provinceResult.getData();
                info.setProvince(provinceInfo.getProvince());
            }

            // 查询市级信息
            Result<GeoLocationInfo> cityResult = queryDistrictByCode(geoCodeInfo.getCityLevelCode(), 2);
            if (cityResult.getCode() == 200 && cityResult.getData() != null) {
                GeoLocationInfo cityInfo = cityResult.getData();
                info.setCity(cityInfo.getCity());
            }

            // 查询县级信息
            Result<GeoLocationInfo> districtResult = queryDistrictByCode(geoCodeInfo.getDistrictLevelCode(), 3);
            if (districtResult.getCode() == 200 && districtResult.getData() != null) {
                GeoLocationInfo districtInfo = districtResult.getData();
                info.setDistrict(districtInfo.getDistrict());
            }

            // 查询乡镇级信息
            Result<GeoLocationInfo> townshipResult = queryDistrictByCode(geoCodeInfo.getTownshipLevelCode(), 4);
            if (townshipResult.getCode() == 200 && townshipResult.getData() != null) {
                GeoLocationInfo townshipInfo = townshipResult.getData();
                info.setTownship(townshipInfo.getTownship());
            }

            // 构建完整地址并获取经纬度
            StringBuilder addressBuilder = new StringBuilder();
            if (info.getProvince() != null && !info.getProvince().isEmpty()) {
                addressBuilder.append(info.getProvince());
            }
            if (info.getCity() != null && !info.getCity().isEmpty()) {
                addressBuilder.append(info.getCity());
            }
            if (info.getDistrict() != null && !info.getDistrict().isEmpty()) {
                addressBuilder.append(info.getDistrict());
            }
            if (info.getTownship() != null && !info.getTownship().isEmpty()) {
                addressBuilder.append(info.getTownship());
            }

            String address = addressBuilder.toString();
            if (!address.isEmpty()) {
                // 使用地理编码API获取经纬度
                Result<GeoLocationInfo> geocodeResult = geocodeByAddress(address);
                if (geocodeResult.getCode() == 200 && geocodeResult.getData() != null) {
                    GeoLocationInfo geocodeInfo = geocodeResult.getData();
                    info.setLongitude(geocodeInfo.getLongitude());
                    info.setLatitude(geocodeInfo.getLatitude());
                    info.setFormattedAddress(geocodeInfo.getFormattedAddress());
                }
            }

            // 构建格式化地址
            if (info.getFormattedAddress() == null || info.getFormattedAddress().isEmpty()) {
                info.setFormattedAddress(address);
            }

            return Result.success("地理码定位成功", info);
        } catch (Exception e) {
            log.error("地理码定位异常: ", e);
            return Result.error("地理码定位失败: " + e.getMessage());
        }
    }

    /**
     * 根据行政区划代码查询行政区划信息
     * 
     * @param districtCode 行政区划代码（12位）
     * @param level 级别（1-省，2-市，3-县，4-乡镇）
     * @return 地理位置信息
     */
    private Result<GeoLocationInfo> queryDistrictByCode(String districtCode, int level) {
        try {
            // 构建请求参数
            Map<String, String> params = new HashMap<>();
            params.put("key", amapApiKey);
            params.put("keywords", districtCode);
            params.put("subdistrict", "0");
            params.put("extensions", "base");
            params.put("output", "json");

            // 构建URL
            StringBuilder urlBuilder = new StringBuilder(AMAP_DISTRICT_URL);
            urlBuilder.append("?");
            for (Map.Entry<String, String> entry : params.entrySet()) {
                urlBuilder.append(entry.getKey())
                        .append("=")
                        .append(URLEncoder.encode(entry.getValue(), "UTF-8"))
                        .append("&");
            }
            String url = urlBuilder.toString();

            // 调用高德API
            ResponseEntity<String> response = restTemplate.getForEntity(url, String.class);
            String responseBody = response.getBody();

            // 解析响应
            JsonNode jsonNode = objectMapper.readTree(responseBody);
            String status = jsonNode.path("status").asText();

            if (!"1".equals(status)) {
                String info = jsonNode.path("info").asText();
                log.warn("高德行政区划API调用失败: {}", info);
                return Result.error("高德API调用失败: " + info);
            }

            // 解析行政区划信息
            JsonNode districts = jsonNode.path("districts");
            if (districts.isArray() && districts.size() > 0) {
                JsonNode district = districts.get(0);
                String name = district.path("name").asText();
                String center = district.path("center").asText(); // 格式：经度,纬度

                GeoLocationInfo info = new GeoLocationInfo();
                
                // 根据级别设置对应的字段
                switch (level) {
                    case 1:
                        info.setProvince(name);
                        break;
                    case 2:
                        info.setCity(name);
                        break;
                    case 3:
                        info.setDistrict(name);
                        break;
                    case 4:
                        info.setTownship(name);
                        break;
                }

                // 解析中心点坐标
                if (center != null && !center.isEmpty() && center.contains(",")) {
                    String[] coordinates = center.split(",");
                    if (coordinates.length == 2) {
                        info.setLongitude(Double.parseDouble(coordinates[0]));
                        info.setLatitude(Double.parseDouble(coordinates[1]));
                    }
                }

                return Result.success("查询成功", info);
            }

            return Result.error("未找到匹配的行政区划");
        } catch (Exception e) {
            log.warn("查询行政区划信息异常: {}", e.getMessage());
            return Result.error("查询失败: " + e.getMessage());
        }
    }

    @Override
    public Result<GeoLocationInfo> getLocationByCoordinates(Double longitude, Double latitude) {
        try {
            if (longitude == null || latitude == null) {
                return Result.error("经纬度不能为空");
            }

            // 构建请求参数
            Map<String, String> params = new HashMap<>();
            params.put("key", amapApiKey);
            params.put("location", longitude + "," + latitude);
            params.put("output", "json");
            params.put("radius", "1000");
            params.put("extensions", "all");

            // 构建URL
            StringBuilder urlBuilder = new StringBuilder(AMAP_REGEOCODE_URL);
            urlBuilder.append("?");
            for (Map.Entry<String, String> entry : params.entrySet()) {
                urlBuilder.append(entry.getKey()).append("=").append(entry.getValue()).append("&");
            }
            String url = urlBuilder.toString();

            // 调用高德API
            ResponseEntity<String> response = restTemplate.getForEntity(url, String.class);
            String responseBody = response.getBody();

            // 解析响应
            JsonNode jsonNode = objectMapper.readTree(responseBody);
            String status = jsonNode.path("status").asText();

            if (!"1".equals(status)) {
                String info = jsonNode.path("info").asText();
                return Result.error("高德API调用失败: " + info);
            }

            // 解析地理信息
            JsonNode regeocode = jsonNode.path("regeocode");
            JsonNode addressComponent = regeocode.path("addressComponent");
            JsonNode formattedAddress = regeocode.path("formatted_address");

            GeoLocationInfo info = new GeoLocationInfo();
            info.setLongitude(longitude);
            info.setLatitude(latitude);
            info.setProvince(addressComponent.path("province").asText());
            info.setCity(addressComponent.path("city").asText());
            info.setDistrict(addressComponent.path("district").asText());
            info.setAddress(addressComponent.path("township").asText() + addressComponent.path("neighborhood").path("name").asText());
            info.setFormattedAddress(formattedAddress.asText());

            return Result.success("获取地理位置信息成功", info);
        } catch (Exception e) {
            log.error("逆地理编码异常: ", e);
            return Result.error("逆地理编码失败: " + e.getMessage());
        }
    }

    /**
     * 根据地址进行地理编码（获取经纬度）
     * 
     * @param address 地址
     * @return 地理位置信息
     */
    public Result<GeoLocationInfo> geocodeByAddress(String address) {
        try {
            if (address == null || address.trim().isEmpty()) {
                return Result.error("地址不能为空");
            }

            // 构建请求参数
            Map<String, String> params = new HashMap<>();
            params.put("key", amapApiKey);
            params.put("address", address);
            params.put("output", "json");

            // 构建URL
            StringBuilder urlBuilder = new StringBuilder(AMAP_GEOCODE_URL);
            urlBuilder.append("?");
            for (Map.Entry<String, String> entry : params.entrySet()) {
                urlBuilder.append(entry.getKey()).append("=").append(entry.getValue()).append("&");
            }
            String url = urlBuilder.toString();

            // 调用高德API
            ResponseEntity<String> response = restTemplate.getForEntity(url, String.class);
            String responseBody = response.getBody();

            // 解析响应
            JsonNode jsonNode = objectMapper.readTree(responseBody);
            String status = jsonNode.path("status").asText();

            if (!"1".equals(status)) {
                String info = jsonNode.path("info").asText();
                return Result.error("高德API调用失败: " + info);
            }

            // 解析地理信息
            JsonNode geocodes = jsonNode.path("geocodes");
            if (geocodes.isArray() && geocodes.size() > 0) {
                JsonNode firstResult = geocodes.get(0);
                String location = firstResult.path("location").asText();
                String[] coordinates = location.split(",");
                
                GeoLocationInfo info = new GeoLocationInfo();
                info.setLongitude(Double.parseDouble(coordinates[0]));
                info.setLatitude(Double.parseDouble(coordinates[1]));
                info.setFormattedAddress(firstResult.path("formatted_address").asText());
                info.setProvince(firstResult.path("province").asText());
                info.setCity(firstResult.path("city").asText());
                info.setDistrict(firstResult.path("district").asText());
                info.setAddress(address);

                return Result.success("地理编码成功", info);
            }

            return Result.error("未找到匹配的地理位置");
        } catch (Exception e) {
            log.error("地理编码异常: ", e);
            return Result.error("地理编码失败: " + e.getMessage());
        }
    }
}

