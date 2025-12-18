package com.example.module.service;

import com.example.module.util.Result;

/**
 * 地理信息服务接口
 * 用于解析地理码并获取地理位置信息（集成高德API）
 */
public interface GeoLocationService {
    
    /**
     * 根据地理码获取地理位置信息
     * 
     * @param geoCode 12位地理码
     * @return 地理位置信息（包含经纬度、地址等）
     */
    Result<GeoLocationInfo> getLocationByGeoCode(String geoCode);

    /**
     * 根据经纬度获取地理位置信息（逆地理编码）
     * 
     * @param longitude 经度
     * @param latitude 纬度
     * @return 地理位置信息
     */
    Result<GeoLocationInfo> getLocationByCoordinates(Double longitude, Double latitude);

    /**
     * 根据12位地理码进行定位
     * 解析地理码的层级结构（省、市、县、乡镇、村），并获取地理位置信息
     * 
     * @param geoCode 12位地理码
     * @return 地理位置信息（包含经纬度、各级行政区划名称等）
     */
    Result<GeoLocationInfo> locateByGeoCode(String geoCode);

    /**
     * 地理信息类
     */
    class GeoLocationInfo {
        private String geoCode;
        private Double longitude;
        private Double latitude;
        private String province;
        private String city;
        private String district;
        private String address;
        private String formattedAddress;

        // Getters and Setters
        public String getGeoCode() {
            return geoCode;
        }

        public void setGeoCode(String geoCode) {
            this.geoCode = geoCode;
        }

        public Double getLongitude() {
            return longitude;
        }

        public void setLongitude(Double longitude) {
            this.longitude = longitude;
        }

        public Double getLatitude() {
            return latitude;
        }

        public void setLatitude(Double latitude) {
            this.latitude = latitude;
        }

        public String getProvince() {
            return province;
        }

        public void setProvince(String province) {
            this.province = province;
        }

        public String getCity() {
            return city;
        }

        public void setCity(String city) {
            this.city = city;
        }

        public String getDistrict() {
            return district;
        }

        public void setDistrict(String district) {
            this.district = district;
        }

        public String getAddress() {
            return address;
        }

        public void setAddress(String address) {
            this.address = address;
        }

        public String getFormattedAddress() {
            return formattedAddress;
        }

        public void setFormattedAddress(String formattedAddress) {
            this.formattedAddress = formattedAddress;
        }

        // 新增字段：行政区划层级信息
        private String provinceCode;
        private String cityCode;
        private String districtCode;
        private String townshipCode;
        private String villageCode;
        private String township;
        private String village;

        public String getProvinceCode() {
            return provinceCode;
        }

        public void setProvinceCode(String provinceCode) {
            this.provinceCode = provinceCode;
        }

        public String getCityCode() {
            return cityCode;
        }

        public void setCityCode(String cityCode) {
            this.cityCode = cityCode;
        }

        public String getDistrictCode() {
            return districtCode;
        }

        public void setDistrictCode(String districtCode) {
            this.districtCode = districtCode;
        }

        public String getTownshipCode() {
            return townshipCode;
        }

        public void setTownshipCode(String townshipCode) {
            this.townshipCode = townshipCode;
        }

        public String getVillageCode() {
            return villageCode;
        }

        public void setVillageCode(String villageCode) {
            this.villageCode = villageCode;
        }

        public String getTownship() {
            return township;
        }

        public void setTownship(String township) {
            this.township = township;
        }

        public String getVillage() {
            return village;
        }

        public void setVillage(String village) {
            this.village = village;
        }
    }
}

