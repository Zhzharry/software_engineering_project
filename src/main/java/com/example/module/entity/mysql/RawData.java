package com.example.module.entity.mysql;

import lombok.Data;
import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "raw_data")
@Data
public class RawData {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "data_content", columnDefinition = "TEXT")
    private String dataContent;
    
    @Column(name = "data_type")
    private String dataType;
    
    @Column(name = "disaster_category")
    private String disasterCategory;
    
    @Column(name = "disaster_subcategory")
    private String disasterSubcategory;
    
    @Column(name = "disaster_indicator")
    private String disasterIndicator;
    
    @Column(name = "geo_code", length = 12)
    private String geoCode;
    
    @Column(name = "disaster_id", length = 36)
    private String disasterId;
    
    @Column(name = "disaster_date_time")
    private LocalDateTime disasterDateTime;
    
    @Column(name = "source_system")
    private String sourceSystem;
    
    @Column(name = "source_category")
    private String sourceCategory;
    
    @Column(name = "carrier_type")
    private String carrierType;
    
    @Column(name = "decoded_description", columnDefinition = "TEXT")
    private String decodedDescription;
    
    @Column(name = "create_time")
    private LocalDateTime createTime;
    
    @Column(name = "update_time")
    private LocalDateTime updateTime;
    
    @Column(name = "status")
    private Integer status;
    
    @Column(name = "processed")
    private Boolean processed;
    
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