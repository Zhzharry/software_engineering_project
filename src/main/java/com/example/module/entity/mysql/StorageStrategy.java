package com.example.module.entity.mysql;

import lombok.Data;
import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "storage_strategy")
@Data
public class StorageStrategy {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "strategy_name", nullable = false, length = 100)
    private String strategyName;
    
    @Column(name = "data_type", length = 100)
    private String dataType; // 适用的数据类型
    
    @Column(name = "storage_type", length = 50)
    private String storageType; // 存储类型：mysql、mongodb等
    
    @Column(name = "retention_days")
    private Integer retentionDays; // 保留天数
    
    @Column(name = "max_size")
    private Long maxSize; // 最大存储大小（字节）
    
    @Column(name = "cleanup_rule", columnDefinition = "TEXT")
    private String cleanupRule; // 清理规则（JSON格式）
    
    @Column(name = "status")
    private Integer status = 1; // 0-禁用，1-启用
    
    @Column(name = "create_time")
    private LocalDateTime createTime;
    
    @Column(name = "update_time")
    private LocalDateTime updateTime;
    
    @PrePersist
    protected void onCreate() {
        createTime = LocalDateTime.now();
        updateTime = LocalDateTime.now();
        if (status == null) {
            status = 1;
        }
    }
    
    @PreUpdate
    protected void onUpdate() {
        updateTime = LocalDateTime.now();
    }
}

