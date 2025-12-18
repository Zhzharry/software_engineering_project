package com.example.module.controller;

import com.example.module.entity.mongodb.FileMetadata;
import com.example.module.service.FileService;
import com.example.module.util.Result;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/files")
@RequiredArgsConstructor
public class FileController {
    
    private final FileService fileService;
    
    @PostMapping("/upload")
    public Result<FileMetadata> uploadFile(
            @RequestParam("file") MultipartFile file,
            @RequestParam("fileType") String fileType,
            @RequestParam(value = "relatedDataId", required = false) String relatedDataId,
            @RequestParam(value = "relatedDataType", required = false) String relatedDataType,
            @RequestParam(value = "userId", required = false) Long userId,
            @RequestParam(value = "description", required = false) String description) {
        return fileService.uploadFile(file, fileType, relatedDataId, relatedDataType, userId, description);
    }
    
    @GetMapping("/{id}")
    public Result<FileMetadata> getFileById(@PathVariable String id) {
        return fileService.getFileById(id);
    }
    
    @GetMapping
    public Result<List<FileMetadata>> getAllFiles() {
        return fileService.getAllFiles();
    }
    
    @GetMapping("/type/{fileType}")
    public Result<List<FileMetadata>> getFilesByType(@PathVariable String fileType) {
        return fileService.getFilesByType(fileType);
    }
    
    @GetMapping("/related")
    public Result<List<FileMetadata>> getFilesByRelatedData(
            @RequestParam String relatedDataId,
            @RequestParam String relatedDataType) {
        return fileService.getFilesByRelatedData(relatedDataId, relatedDataType);
    }
    
    @GetMapping("/user/{userId}")
    public Result<List<FileMetadata>> getUserFiles(@PathVariable Long userId) {
        return fileService.getUserFiles(userId);
    }
    
    @DeleteMapping("/{id}")
    public Result<Boolean> deleteFile(@PathVariable String id) {
        return fileService.deleteFile(id);
    }
    
    @PutMapping("/{id}")
    public Result<FileMetadata> updateFileInfo(
            @PathVariable String id,
            @RequestParam(value = "description", required = false) String description,
            @RequestParam(value = "accessLevel", required = false) Integer accessLevel) {
        return fileService.updateFileInfo(id, description, accessLevel);
    }
    
    @GetMapping("/{id}/download")
    public ResponseEntity<byte[]> downloadFile(@PathVariable String id) {
        Result<byte[]> result = fileService.downloadFile(id);
        if (result.getCode() != 200) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
        
        Result<FileMetadata> fileResult = fileService.getFileById(id);
        if (fileResult.getCode() != 200) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
        
        FileMetadata file = fileResult.getData();
        byte[] fileBytes = result.getData();
        
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.parseMediaType(file.getMimeType()));
        headers.setContentDispositionFormData("attachment", file.getOriginalName());
        headers.setContentLength(fileBytes.length);
        
        return new ResponseEntity<>(fileBytes, headers, HttpStatus.OK);
    }
    
    @GetMapping("/{id}/preview")
    public ResponseEntity<byte[]> previewFile(@PathVariable String id) {
        Result<FileMetadata> fileResult = fileService.getFileById(id);
        if (fileResult.getCode() != 200) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
        
        FileMetadata file = fileResult.getData();
        
        // 检查是否支持预览
        if (!fileService.isPreviewable(file.getMimeType())) {
            return ResponseEntity.status(HttpStatus.UNSUPPORTED_MEDIA_TYPE)
                    .body("该文件类型不支持预览".getBytes());
        }
        
        Result<byte[]> result = fileService.downloadFile(id);
        if (result.getCode() != 200) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
        
        byte[] fileBytes = result.getData();
        
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.parseMediaType(file.getMimeType()));
        headers.setContentLength(fileBytes.length);
        // 预览模式，不强制下载
        headers.set("Content-Disposition", "inline; filename=\"" + file.getOriginalName() + "\"");
        
        return new ResponseEntity<>(fileBytes, headers, HttpStatus.OK);
    }
    
    @GetMapping("/{id}/url")
    public Result<String> getFileUrl(@PathVariable String id) {
        return fileService.getFileUrl(id);
    }
    
    @GetMapping("/{id}/access")
    public ResponseEntity<byte[]> accessFile(@PathVariable String id) {
        Result<byte[]> result = fileService.downloadFile(id);
        if (result.getCode() != 200) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
        
        Result<FileMetadata> fileResult = fileService.getFileById(id);
        if (fileResult.getCode() != 200) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
        
        FileMetadata file = fileResult.getData();
        byte[] fileBytes = result.getData();
        
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.parseMediaType(file.getMimeType()));
        headers.setContentLength(fileBytes.length);
        
        return new ResponseEntity<>(fileBytes, headers, HttpStatus.OK);
    }
    
    @GetMapping("/statistics")
    public Result<Map<String, Object>> getFileStatistics() {
        return fileService.getFileStatistics();
    }
}

