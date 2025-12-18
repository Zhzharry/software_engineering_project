package com.example.module.entity.mysql;

import lombok.Data;
import javax.persistence.*;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "file_entity")
public class FileEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "file_name", nullable = false)
    private String fileName;
    
    @Column(name = "original_name", nullable = false)
    private String originalName;
    
    @Column(name = "file_path", nullable = false, length = 500)
    private String filePath;
    
    @Column(name = "file_type", nullable = false)
    private String fileType; // image, audio, video, document
    
    @Column(name = "mime_type")
    private String mimeType;
    
    @Column(name = "file_size")
    private Long fileSize; // 字节
    
    @Column(name = "file_extension")
    private String fileExtension;
    
    @Column(name = "related_data_id")
    private String relatedDataId; // 关联的灾情数据ID
    
    @Column(name = "related_data_type")
    private String relatedDataType; // raw_data, processed_data
    
    @Column(name = "upload_user_id")
    private Long uploadUserId;
    
    @Column(name = "access_level")
    private Integer accessLevel; // 0-公开, 1-内部, 2-私有
    
    @Column(name = "description", columnDefinition = "TEXT")
    private String description;
    
    @Column(name = "status")
    private Integer status; // 0-已删除, 1-正常
    
    @Column(name = "create_time")
    private LocalDateTime createTime;
    
    @Column(name = "update_time")
    private LocalDateTime updateTime;
    
    @PrePersist
    protected void onCreate() {
        createTime = LocalDateTime.now();
        updateTime = LocalDateTime.now();
        if (status == null) {
            status = 1;
        }
        if (accessLevel == null) {
            accessLevel = 1;
        }
    }
    
    @PreUpdate
    protected void onUpdate() {
        updateTime = LocalDateTime.now();
    }
}

