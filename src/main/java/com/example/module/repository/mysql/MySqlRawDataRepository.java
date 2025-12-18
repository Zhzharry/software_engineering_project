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
}
