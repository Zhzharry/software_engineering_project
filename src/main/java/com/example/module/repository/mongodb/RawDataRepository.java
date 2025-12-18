package com.example.module.repository.mongodb;

import com.example.module.entity.mongodb.RawData;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface RawDataRepository extends MongoRepository<RawData, String> {
    List<RawData> findByDataType(String dataType);
    
    List<RawData> findByProcessed(Boolean processed);
    
    List<RawData> findByCreateTimeBetween(LocalDateTime start, LocalDateTime end);

    List<RawData> findByModuleId(Long moduleId);

    void deleteByModuleId(Long moduleId);
    
    @Query("{ 'content.?0' : ?1 }")
    List<RawData> findByContentField(String fieldName, Object value);
    
    long countByDataType(String dataType);
    
    // ========== 解码后的字段查询方法 ==========
    
    List<RawData> findByDisasterCategory(String disasterCategory);
    
    List<RawData> findByDisasterSubcategory(String disasterSubcategory);
    
    List<RawData> findBySourceSubcategory(String sourceSubcategory);
    
    List<RawData> findByCarrierType(String carrierType);
    
    List<RawData> findByGeoCode(String geoCode);
    
    List<RawData> findByDisasterDateTimeBetween(LocalDateTime start, LocalDateTime end);
    
    List<RawData> findByDisasterId(String disasterId);
}