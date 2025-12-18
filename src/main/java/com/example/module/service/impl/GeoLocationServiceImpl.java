package com.example.module.service.impl;

import com.example.module.service.GeoLocationService;
import com.example.module.util.Result;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

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

    @Override
    public Result<GeoLocationInfo> getLocationByGeoCode(String geoCode) {
        try {
            if (geoCode == null || geoCode.length() != 12) {
                return Result.error("地理码必须为12位");
            }

            // 注意：12位地理码需要根据实际业务规则转换为具体的地址或坐标
            // 这里假设地理码可以直接作为地址进行地理编码
            // 实际应用中可能需要根据地理码的编码规则进行解析
            
            // 如果地理码是地址编码，可以直接使用地理编码API
            // 如果地理码是坐标编码，需要先解析出经纬度
            
            // 示例：假设地理码前6位是行政区划代码，后6位是其他信息
            // 这里提供一个基础实现，实际需要根据地理码的具体编码规则调整
            
            // 暂时返回一个示例实现
            // 实际应用中，需要根据地理码的编码规则解析出地址或坐标
            log.warn("地理码解析功能需要根据实际编码规则实现，当前地理码: {}", geoCode);
            
            GeoLocationInfo info = new GeoLocationInfo();
            info.setGeoCode(geoCode);
            // TODO: 根据地理码编码规则解析出具体地址或坐标
            
            return Result.success("地理码解析功能待完善", info);
        } catch (Exception e) {
            log.error("地理码解析异常: ", e);
            return Result.error("地理码解析失败: " + e.getMessage());
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

