package com.example.module.repository.mysql;

import com.example.module.entity.mysql.StorageStrategy;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface StorageStrategyRepository extends JpaRepository<StorageStrategy, Long> {
    List<StorageStrategy> findByDataType(String dataType);
    
    List<StorageStrategy> findByStatus(Integer status);
    
    Optional<StorageStrategy> findByStrategyName(String strategyName);
    
    List<StorageStrategy> findByStorageType(String storageType);
}

