package com.example.module.controller;

import com.example.module.service.DashboardService;
import com.example.module.util.Result;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/dashboard")
@RequiredArgsConstructor
public class DashboardController {
    
    private final DashboardService dashboardService;
    
    @GetMapping
    public Result<Map<String, Object>> getDashboardData() {
        return dashboardService.getDashboardData();
    }
    
    @GetMapping("/statistics")
    public Result<Map<String, Object>> getDataStatistics() {
        return dashboardService.getDataStatistics();
    }
    
    @GetMapping("/files")
    public Result<Map<String, Object>> getFileStatistics() {
        return dashboardService.getFileStatistics();
    }
    
    @GetMapping("/activities")
    public Result<Map<String, Object>> getRecentActivities() {
        return dashboardService.getRecentActivities();
    }
    
    /**
     * 获取数据可视化图表数据
     * 包括统计图表、趋势图等
     */
    @GetMapping("/charts")
    public Result<Map<String, Object>> getChartData() {
        return dashboardService.getChartData();
    }
    
    /**
     * 获取数据类型分布图表数据（饼图）
     */
    @GetMapping("/charts/data-type-distribution")
    public Result<Map<String, Object>> getDataTypeDistribution() {
        return dashboardService.getDataTypeDistribution();
    }
    
    /**
     * 获取数据趋势图表数据（折线图）
     */
    @GetMapping("/charts/trend")
    public Result<Map<String, Object>> getTrendChart(
            @org.springframework.web.bind.annotation.RequestParam(defaultValue = "7") int days) {
        return dashboardService.getTrendChart(days);
    }
}

