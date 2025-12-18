package com.example.module.controller;

import com.example.module.service.MapService;
import com.example.module.util.Result;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 地图可视化控制器
 * 提供灾情地图展示所需的数据接口
 */
@RestController
@RequestMapping("/api/map")
@RequiredArgsConstructor
public class MapController {

    private final MapService mapService;

    /**
     * 获取所有灾情点位数据
     * 用于地图标注显示
     *
     * @param disasterCategory 灾害大类筛选（可选）
     * @param source 来源筛选（可选）
     * @param carrierType 载体类型筛选（可选）
     * @return 灾情点位列表（包含经纬度）
     */
    @GetMapping("/disaster-points")
    public Result<List<MapService.DisasterPoint>> getDisasterPoints(
            @RequestParam(required = false) String disasterCategory,
            @RequestParam(required = false) String source,
            @RequestParam(required = false) String carrierType) {
        return mapService.getDisasterPoints(disasterCategory, source, carrierType);
    }

    /**
     * 按区域统计灾情数量
     * 用于热力图或区域着色
     *
     * @param level 统计级别: province(省), city(市), district(区)，默认province
     * @return 区域统计数据
     */
    @GetMapping("/region-statistics")
    public Result<List<MapService.RegionStatistics>> getRegionStatistics(
            @RequestParam(defaultValue = "province") String level) {
        return mapService.getRegionStatistics(level);
    }

    /**
     * 获取聚合点位数据
     * 根据缩放级别对临近点位进行聚合
     *
     * @param zoomLevel 地图缩放级别 (1-18)，默认5
     * @param bounds 地图可视范围 (minLng,minLat,maxLng,maxLat)，可选
     * @return 聚合后的点位数据
     */
    @GetMapping("/cluster")
    public Result<List<MapService.ClusterPoint>> getClusterPoints(
            @RequestParam(defaultValue = "5") Integer zoomLevel,
            @RequestParam(required = false) String bounds) {
        return mapService.getClusterPoints(zoomLevel, bounds);
    }

    /**
     * 获取灾情时间轴数据
     * 按时间顺序返回灾情点位，用于时间轴动画
     *
     * @param startTime 开始时间 (ISO格式，如 2023-12-01T00:00:00)
     * @param endTime 结束时间 (ISO格式)
     * @return 时间轴数据
     */
    @GetMapping("/timeline")
    public Result<List<MapService.TimelinePoint>> getTimelineData(
            @RequestParam(required = false) String startTime,
            @RequestParam(required = false) String endTime) {
        return mapService.getTimelineData(startTime, endTime);
    }

    /**
     * 获取地图概览统计
     * 返回地图页面展示的概览数据
     *
     * @return 概览统计数据
     */
    @GetMapping("/overview")
    public Result<MapService.MapOverview> getMapOverview() {
        return mapService.getMapOverview();
    }
}
