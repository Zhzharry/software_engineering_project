package com.example.module.service;

import com.example.module.util.Result;
import java.util.Map;

public interface DashboardService {
    Result<Map<String, Object>> getDashboardData();
    
    Result<Map<String, Object>> getDataStatistics();
    
    Result<Map<String, Object>> getFileStatistics();
    
    Result<Map<String, Object>> getRecentActivities();
    
    Result<Map<String, Object>> getChartData();
    
    Result<Map<String, Object>> getDataTypeDistribution();
    
    Result<Map<String, Object>> getTrendChart(int days);
}

