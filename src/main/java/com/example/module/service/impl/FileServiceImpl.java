package com.example.module.service.impl;

import com.example.module.entity.mongodb.FileMetadata;
import com.example.module.repository.mongodb.FileMetadataRepository;
import com.example.module.service.FileService;
import com.example.module.util.Result;
import com.mongodb.client.gridfs.model.GridFSFile;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.gridfs.GridFsResource;
import org.springframework.data.mongodb.gridfs.GridFsTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class FileServiceImpl implements FileService {
    
    private final FileMetadataRepository fileMetadataRepository;
    private final GridFsTemplate gridFsTemplate;
    
    @Value("${file.access.url:/api/files/access/}")
    private String accessUrl;
    
    // 支持的文件类型
    private static final Map<String, List<String>> ALLOWED_TYPES = new HashMap<>();
    static {
        ALLOWED_TYPES.put("image", Arrays.asList("jpg", "jpeg", "png", "gif", "bmp", "webp"));
        ALLOWED_TYPES.put("audio", Arrays.asList("mp3", "wav", "wma", "aac", "ogg", "flac"));
        ALLOWED_TYPES.put("video", Arrays.asList("mp4", "avi", "mov", "wmv", "flv", "mkv", "webm"));
        ALLOWED_TYPES.put("document", Arrays.asList("pdf", "doc", "docx", "xls", "xlsx", "txt", "csv"));
    }
    
    // 支持预览的文件类型
    private static final List<String> PREVIEWABLE_TYPES = Arrays.asList(
        "image/jpeg", "image/jpg", "image/png", "image/gif", "image/bmp", "image/webp",
        "text/plain", "text/html", "text/css", "text/javascript",
        "application/pdf"
    );
    
    @Override
    public Result<FileMetadata> uploadFile(MultipartFile file, String fileType, String relatedDataId, 
                                        String relatedDataType, Long userId, String description) {
        try {
            // 验证文件
            if (file.isEmpty()) {
                return Result.error(400, "文件不能为空");
            }
            
            // 获取文件扩展名
            String originalFilename = file.getOriginalFilename();
            if (originalFilename == null || originalFilename.isEmpty()) {
                return Result.error(400, "文件名不能为空");
            }
            
            String extension = getFileExtension(originalFilename).toLowerCase();
            
            // 验证文件类型
            if (!isAllowedFileType(fileType, extension)) {
                return Result.error(400, "不支持的文件类型: " + extension);
            }
            
            // 生成唯一文件名
            String uniqueFileName = UUID.randomUUID().toString() + "." + extension;
            
            // 存储文件到 GridFS
            ObjectId gridFsId = gridFsTemplate.store(
                file.getInputStream(),
                uniqueFileName,
                file.getContentType(),
                null
            );
            
            // 创建文件元数据
            FileMetadata fileMetadata = new FileMetadata();
            fileMetadata.setGridFsId(gridFsId.toString());
            fileMetadata.setFileName(uniqueFileName);
            fileMetadata.setOriginalName(originalFilename);
            fileMetadata.setFileType(fileType);
            fileMetadata.setMimeType(file.getContentType());
            fileMetadata.setFileSize(file.getSize());
            fileMetadata.setFileExtension(extension);
            fileMetadata.setRelatedDataId(relatedDataId);
            fileMetadata.setRelatedDataType(relatedDataType);
            fileMetadata.setUploadUserId(userId);
            fileMetadata.setDescription(description);
            fileMetadata.setStatus(1);
            fileMetadata.setCreateTime(LocalDateTime.now());
            fileMetadata.setUpdateTime(LocalDateTime.now());
            
            if (fileMetadata.getAccessLevel() == null) {
                fileMetadata.setAccessLevel(1);
            }
            
            FileMetadata saved = fileMetadataRepository.save(fileMetadata);
            
            log.info("文件上传成功: {}, GridFS ID: {}", saved.getId(), gridFsId);
            return Result.success("文件上传成功", saved);
            
        } catch (IOException e) {
            log.error("文件上传失败", e);
            return Result.error(500, "文件上传失败: " + e.getMessage());
        }
    }
    
    @Override
    public Result<FileMetadata> getFileById(String id) {
        Optional<FileMetadata> file = fileMetadataRepository.findById(id);
        if (file.isPresent() && file.get().getStatus() == 1) {
            return Result.success(file.get());
        }
        return Result.error(404, "文件不存在");
    }
    
    @Override
    public Result<List<FileMetadata>> getAllFiles() {
        List<FileMetadata> files = fileMetadataRepository.findByStatus(1);
        return Result.success(files);
    }
    
    @Override
    public Result<List<FileMetadata>> getFilesByType(String fileType) {
        List<FileMetadata> files = fileMetadataRepository.findByFileTypeAndStatus(fileType, 1);
        return Result.success(files);
    }
    
    @Override
    public Result<List<FileMetadata>> getFilesByRelatedData(String relatedDataId, String relatedDataType) {
        List<FileMetadata> files = fileMetadataRepository.findByRelatedDataIdAndRelatedDataType(relatedDataId, relatedDataType);
        files = files.stream().filter(f -> f.getStatus() == 1).collect(Collectors.toList());
        return Result.success(files);
    }
    
    @Override
    public Result<List<FileMetadata>> getUserFiles(Long userId) {
        List<FileMetadata> files = fileMetadataRepository.findByUploadUserId(userId);
        files = files.stream().filter(f -> f.getStatus() == 1).collect(Collectors.toList());
        return Result.success(files);
    }
    
    @Override
    public Result<Boolean> deleteFile(String id) {
        Optional<FileMetadata> fileOpt = fileMetadataRepository.findById(id);
        if (!fileOpt.isPresent()) {
            return Result.error(404, "文件不存在");
        }
        
        FileMetadata file = fileOpt.get();
        file.setStatus(0);
        file.setUpdateTime(LocalDateTime.now());
        fileMetadataRepository.save(file);
        
        // 可选：从 GridFS 删除文件
        try {
            gridFsTemplate.delete(new Query(Criteria.where("_id").is(new ObjectId(file.getGridFsId()))));
        } catch (Exception e) {
            log.warn("删除 GridFS 文件失败: {}", file.getGridFsId(), e);
        }
        
        return Result.success("文件删除成功", true);
    }
    
    @Override
    public Result<FileMetadata> updateFileInfo(String id, String description, Integer accessLevel) {
        Optional<FileMetadata> fileOpt = fileMetadataRepository.findById(id);
        if (!fileOpt.isPresent()) {
            return Result.error(404, "文件不存在");
        }
        
        FileMetadata file = fileOpt.get();
        if (description != null) {
            file.setDescription(description);
        }
        if (accessLevel != null) {
            file.setAccessLevel(accessLevel);
        }
        file.setUpdateTime(LocalDateTime.now());
        
        FileMetadata updated = fileMetadataRepository.save(file);
        return Result.success("文件信息更新成功", updated);
    }
    
    @Override
    public Result<byte[]> downloadFile(String id) {
        Optional<FileMetadata> fileOpt = fileMetadataRepository.findById(id);
        if (!fileOpt.isPresent() || fileOpt.get().getStatus() != 1) {
            return Result.error(404, "文件不存在");
        }
        
        FileMetadata file = fileOpt.get();
        try {
            GridFSFile gridFSFile = gridFsTemplate.findOne(
                new Query(Criteria.where("_id").is(new ObjectId(file.getGridFsId())))
            );
            
            if (gridFSFile == null) {
                return Result.error(404, "文件不存在");
            }
            
            GridFsResource resource = gridFsTemplate.getResource(gridFSFile);
            InputStream inputStream = resource.getInputStream();
            
            ByteArrayOutputStream buffer = new ByteArrayOutputStream();
            byte[] data = new byte[1024];
            int nRead;
            while ((nRead = inputStream.read(data, 0, data.length)) != -1) {
                buffer.write(data, 0, nRead);
            }
            buffer.flush();
            
            return Result.success(buffer.toByteArray());
        } catch (IOException e) {
            log.error("读取文件失败: {}", file.getGridFsId(), e);
            return Result.error(500, "读取文件失败");
        }
    }
    
    @Override
    public Result<String> getFileUrl(String id) {
        Optional<FileMetadata> fileOpt = fileMetadataRepository.findById(id);
        if (!fileOpt.isPresent() || fileOpt.get().getStatus() != 1) {
            return Result.error(404, "文件不存在");
        }
        String url = accessUrl + id;
        return Result.success(url);
    }
    
    @Override
    public Result<Map<String, Object>> getFileStatistics() {
        List<FileMetadata> allFiles = fileMetadataRepository.findByStatus(1);
        
        Map<String, Object> stats = new HashMap<>();
        stats.put("totalFiles", allFiles.size());
        stats.put("totalSize", allFiles.stream().mapToLong(FileMetadata::getFileSize).sum());
        
        Map<String, Long> typeCount = new HashMap<>();
        Map<String, Long> typeSize = new HashMap<>();
        
        for (FileMetadata file : allFiles) {
            String type = file.getFileType();
            typeCount.put(type, typeCount.getOrDefault(type, 0L) + 1);
            typeSize.put(type, typeSize.getOrDefault(type, 0L) + file.getFileSize());
        }
        
        stats.put("typeCount", typeCount);
        stats.put("typeSize", typeSize);
        
        return Result.success(stats);
    }
    
    /**
     * 检查文件是否支持预览
     */
    public boolean isPreviewable(String mimeType) {
        return PREVIEWABLE_TYPES.contains(mimeType != null ? mimeType.toLowerCase() : "");
    }
    
    private String getFileExtension(String filename) {
        int lastDot = filename.lastIndexOf('.');
        return lastDot > 0 ? filename.substring(lastDot + 1) : "";
    }
    
    private boolean isAllowedFileType(String fileType, String extension) {
        List<String> allowed = ALLOWED_TYPES.get(fileType.toLowerCase());
        return allowed != null && allowed.contains(extension.toLowerCase());
    }
}
