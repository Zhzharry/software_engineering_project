package com.example.module.repository.mysql;

import com.example.module.entity.mysql.FileEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface FileRepository extends JpaRepository<FileEntity, Long> {
    List<FileEntity> findByFileType(String fileType);
    
    List<FileEntity> findByFileTypeAndStatus(String fileType, Integer status);
    
    List<FileEntity> findByRelatedDataIdAndRelatedDataType(String relatedDataId, String relatedDataType);
    
    List<FileEntity> findByUploadUserId(Long uploadUserId);
    
    List<FileEntity> findByStatus(Integer status);
    
    Optional<FileEntity> findByFileName(String fileName);
    
    @Query("SELECT f FROM FileEntity f WHERE f.status = 1 ORDER BY f.createTime DESC")
    List<FileEntity> findAllActive();
}

