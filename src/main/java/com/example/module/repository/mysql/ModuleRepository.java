package com.example.module.repository.mysql;

import com.example.module.entity.mysql.ModuleEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface ModuleRepository extends JpaRepository<ModuleEntity, Long>, JpaSpecificationExecutor<ModuleEntity> {
    Optional<ModuleEntity> findByName(String name);
    List<ModuleEntity> findByStatus(Integer status);
    boolean existsByName(String name);
}