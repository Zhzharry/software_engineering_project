package com.example.module.service;

import com.example.module.entity.mongodb.FileMetadata;
import com.example.module.util.Result;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;

public interface FileService {
    Result<FileMetadata> uploadFile(MultipartFile file, String fileType, String relatedDataId, String relatedDataType, Long userId, String description);
    
    Result<FileMetadata> getFileById(String id);
    
    Result<List<FileMetadata>> getAllFiles();
    
    Result<List<FileMetadata>> getFilesByType(String fileType);
    
    Result<List<FileMetadata>> getFilesByRelatedData(String relatedDataId, String relatedDataType);
    
    Result<List<FileMetadata>> getUserFiles(Long userId);
    
    Result<Boolean> deleteFile(String id);
    
    Result<FileMetadata> updateFileInfo(String id, String description, Integer accessLevel);
    
    Result<byte[]> downloadFile(String id);
    
    Result<String> getFileUrl(String id);
    
    Result<Map<String, Object>> getFileStatistics();
    
    boolean isPreviewable(String mimeType);
}

