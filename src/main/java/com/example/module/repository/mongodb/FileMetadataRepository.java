package com.example.module.repository.mongodb;

import com.example.module.entity.mongodb.FileMetadata;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface FileMetadataRepository extends MongoRepository<FileMetadata, String> {
    List<FileMetadata> findByFileTypeAndStatus(String fileType, Integer status);
    
    List<FileMetadata> findByRelatedDataIdAndRelatedDataType(String relatedDataId, String relatedDataType);
    
    List<FileMetadata> findByUploadUserId(Long userId);
    
    List<FileMetadata> findByStatus(Integer status);
}

