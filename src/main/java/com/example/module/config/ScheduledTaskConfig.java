package com.example.module.config;

import com.example.module.service.DataEvictionService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@EnableScheduling
@RequiredArgsConstructor
public class ScheduledTaskConfig {
    
    private final DataEvictionService dataEvictionService;
    
    /**
     * 每天凌晨2点执行数据清理任务
     * cron表达式: 秒 分 时 日 月 周
     */
    @Scheduled(cron = "0 0 2 * * ?")
    public void scheduledEvictExpiredData() {
        log.info("开始执行定时数据清理任务...");
        try {
            dataEvictionService.evictAllExpiredData();
            log.info("定时数据清理任务执行完成");
        } catch (Exception e) {
            log.error("定时数据清理任务执行失败", e);
        }
    }
    
    /**
     * 每小时执行一次数据清理任务（可选，用于测试）
     * 生产环境建议使用每天执行一次
     */
    // @Scheduled(cron = "0 0 * * * ?")
    // public void hourlyEvictExpiredData() {
    //     log.info("开始执行每小时数据清理任务...");
    //     try {
    //         dataEvictionService.evictAllExpiredData();
    //         log.info("每小时数据清理任务执行完成");
    //     } catch (Exception e) {
    //         log.error("每小时数据清理任务执行失败", e);
    //     }
    // }
}

