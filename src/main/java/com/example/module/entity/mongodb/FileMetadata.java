package com.example.module.entity.mongodb;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@Data
@Document(collection = "file_metadata")
public class FileMetadata {
    @Id
    private String id;
    
    private String gridFsId; // GridFS 文件ID
    
    private String fileName;
    
    private String originalName;
    
    private String fileType; // image, audio, video, document
    
    private String mimeType;
    
    private Long fileSize; // 字节
    
    private String fileExtension;
    
    private String relatedDataId; // 关联的灾情数据ID
    
    private String relatedDataType; // raw_data, processed_data
    
    private Long uploadUserId;
    
    private Integer accessLevel; // 0-公开, 1-内部, 2-私有
    
    private String description;
    
    private Integer status; // 0-已删除, 1-正常
    
    private LocalDateTime createTime;
    
    private LocalDateTime updateTime;
}

