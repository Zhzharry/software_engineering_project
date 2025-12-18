package com.example.module.service;

import com.example.module.util.DecodedId;
import com.example.module.util.Result;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

/**
 * 文件解码服务接口
 * 用于处理文件名解码和Excel内容解码
 */
public interface FileDecodeService {
    
    /**
     * 解码文件名
     * 从文件名中提取36位ID并解码
     * 
     * @param fileName 文件名（可能包含36位ID）
     * @return 解码结果
     */
    Result<FileDecodeResult> decodeFileName(String fileName);

    /**
     * 批量解码文件名
     * 
     * @param fileNames 文件名列表
     * @return 解码结果列表
     */
    Result<List<FileDecodeResult>> batchDecodeFileNames(List<String> fileNames);

    /**
     * 解码Excel文件内容
     * 读取Excel文件，提取其中的ID列并解码
     * 
     * @param file Excel文件
     * @param idColumnIndex ID列索引（从0开始，如果为null则自动查找）
     * @param descriptionColumnIndex 描述列索引（可选）
     * @return 解码结果列表
     */
    Result<ExcelDecodeResult> decodeExcelContent(MultipartFile file, Integer idColumnIndex, Integer descriptionColumnIndex);

    /**
     * 解码Excel文件内容（从文件路径）
     * 
     * @param filePath 文件路径
     * @param idColumnIndex ID列索引
     * @param descriptionColumnIndex 描述列索引
     * @return 解码结果列表
     */
    Result<ExcelDecodeResult> decodeExcelContentFromPath(String filePath, Integer idColumnIndex, Integer descriptionColumnIndex);

    /**
     * 文件名解码结果
     */
    class FileDecodeResult {
        private String fileName;
        private String extractedId;
        private DecodedId decodedId;
        private Boolean hasValidId;

        public String getFileName() {
            return fileName;
        }

        public void setFileName(String fileName) {
            this.fileName = fileName;
        }

        public String getExtractedId() {
            return extractedId;
        }

        public void setExtractedId(String extractedId) {
            this.extractedId = extractedId;
        }

        public DecodedId getDecodedId() {
            return decodedId;
        }

        public void setDecodedId(DecodedId decodedId) {
            this.decodedId = decodedId;
        }

        public Boolean getHasValidId() {
            return hasValidId;
        }

        public void setHasValidId(Boolean hasValidId) {
            this.hasValidId = hasValidId;
        }
    }

    /**
     * Excel解码结果
     */
    class ExcelDecodeResult {
        private String fileName;
        private Integer totalRows;
        private Integer successCount;
        private Integer failCount;
        private List<ExcelRowDecodeResult> rows;

        public String getFileName() {
            return fileName;
        }

        public void setFileName(String fileName) {
            this.fileName = fileName;
        }

        public Integer getTotalRows() {
            return totalRows;
        }

        public void setTotalRows(Integer totalRows) {
            this.totalRows = totalRows;
        }

        public Integer getSuccessCount() {
            return successCount;
        }

        public void setSuccessCount(Integer successCount) {
            this.successCount = successCount;
        }

        public Integer getFailCount() {
            return failCount;
        }

        public void setFailCount(Integer failCount) {
            this.failCount = failCount;
        }

        public List<ExcelRowDecodeResult> getRows() {
            return rows;
        }

        public void setRows(List<ExcelRowDecodeResult> rows) {
            this.rows = rows;
        }
    }

    /**
     * Excel行解码结果
     */
    class ExcelRowDecodeResult {
        private Integer rowIndex;
        private String id;
        private String description;
        private DecodedId decodedId;
        private Boolean success;
        private String errorMessage;

        public Integer getRowIndex() {
            return rowIndex;
        }

        public void setRowIndex(Integer rowIndex) {
            this.rowIndex = rowIndex;
        }

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getDescription() {
            return description;
        }

        public void setDescription(String description) {
            this.description = description;
        }

        public DecodedId getDecodedId() {
            return decodedId;
        }

        public void setDecodedId(DecodedId decodedId) {
            this.decodedId = decodedId;
        }

        public Boolean getSuccess() {
            return success;
        }

        public void setSuccess(Boolean success) {
            this.success = success;
        }

        public String getErrorMessage() {
            return errorMessage;
        }

        public void setErrorMessage(String errorMessage) {
            this.errorMessage = errorMessage;
        }
    }
}

