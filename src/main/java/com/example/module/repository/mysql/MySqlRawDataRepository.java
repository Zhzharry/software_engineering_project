package com.example.module.repository.mysql;

import com.example.module.entity.mysql.RawData;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface MySqlRawDataRepository extends JpaRepository<RawData, Long>, JpaSpecificationExecutor<RawData> {
    
    List<RawData> findByDataType(String dataType);
    
    List<RawData> findBySourceSystemAndStatus(String sourceSystem, Integer status);
    
    @Query("SELECT r FROM RawData r WHERE r.createTime BETWEEN :startTime AND :endTime")
    List<RawData> findByCreateTimeBetween(@Param("startTime") LocalDateTime startTime, 
                                         @Param("endTime") LocalDateTime endTime);
    
    @Query("SELECT r FROM RawData r WHERE r.dataContent LIKE %:keyword%")
    List<RawData> findByDataContentContaining(@Param("keyword") String keyword);
    
    // 按灾情类别查询
    List<RawData> findByDisasterCategory(String disasterCategory);
    
    // 按处理状态查询
    @Query("SELECT r FROM RawData r WHERE r.processed = :processed")
    List<RawData> findByProcessed(@Param("processed") Boolean processed);
    
    // 按状态查询
    List<RawData> findByStatus(Integer status);
    
    // 统计数据类型数量
    long countByDataType(String dataType);
    
    // 按灾情子类查询
    List<RawData> findByDisasterSubcategory(String disasterSubcategory);
    
    // 按来源类别查询
    List<RawData> findBySourceCategory(String sourceCategory);
    
    // 按载体类型查询
    List<RawData> findByCarrierType(String carrierType);
    
    // 按地理码查询
    List<RawData> findByGeoCode(String geoCode);
    
    // 按灾情发生时间范围查询
    @Query("SELECT r FROM RawData r WHERE r.disasterDateTime BETWEEN :startTime AND :endTime")
    List<RawData> findByDisasterDateTimeBetween(@Param("startTime") LocalDateTime startTime, 
                                                 @Param("endTime") LocalDateTime endTime);
}
