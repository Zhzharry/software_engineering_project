package com.example.module.service.impl;

import com.example.module.entity.mongodb.FileMetadata;
import com.example.module.repository.mongodb.FileMetadataRepository;
import com.example.module.service.FileDecodeService;
import com.example.module.service.FileService;
import com.example.module.util.DecodedId;
import com.example.module.util.DisasterIdDecoder;
import com.example.module.util.Result;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * 文件解码服务实现类
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class FileDecodeServiceImpl implements FileDecodeService {

    private final FileMetadataRepository fileMetadataRepository;
    private final FileService fileService;

    // 用于从文件名中提取36位ID的正则表达式
    private static final Pattern ID_PATTERN = Pattern.compile("\\d{36}");

    @Override
    public Result<FileDecodeResult> decodeFileName(String fileName) {
        try {
            if (fileName == null || fileName.trim().isEmpty()) {
                return Result.error("文件名不能为空");
            }

            FileDecodeResult result = new FileDecodeResult();
            result.setFileName(fileName);

            // 从文件名中提取36位ID
            String extractedId = extractIdFromString(fileName);
            result.setExtractedId(extractedId);

            if (extractedId != null) {
                result.setHasValidId(true);
                try {
                    // 解码ID
                    DecodedId decoded = DisasterIdDecoder.decode(extractedId);
                    result.setDecodedId(decoded);
                    return Result.success("文件名解码成功", result);
                } catch (Exception e) {
                    log.warn("文件名中的ID解码失败: {}", e.getMessage());
                    result.setHasValidId(false);
                    return Result.success("文件名中包含36位数字，但解码失败: " + e.getMessage(), result);
                }
            } else {
                result.setHasValidId(false);
                return Result.success("文件名中未找到36位ID", result);
            }
        } catch (Exception e) {
            log.error("文件名解码异常: ", e);
            return Result.error("文件名解码失败: " + e.getMessage());
        }
    }

    @Override
    public Result<List<FileDecodeResult>> batchDecodeFileNames(List<String> fileNames) {
        try {
            if (fileNames == null || fileNames.isEmpty()) {
                return Result.error("文件名列表不能为空");
            }

            List<FileDecodeResult> results = new ArrayList<>();
            int successCount = 0;
            int failCount = 0;

            for (String fileName : fileNames) {
                Result<FileDecodeResult> result = decodeFileName(fileName);
                if (result.getCode() == 200 && result.getData() != null) {
                    results.add(result.getData());
                    if (result.getData().getHasValidId() != null && result.getData().getHasValidId()) {
                        successCount++;
                    } else {
                        failCount++;
                    }
                } else {
                    failCount++;
                }
            }

            String message = String.format("批量解码完成：成功%d个，失败%d个", successCount, failCount);
            return Result.success(message, results);
        } catch (Exception e) {
            log.error("批量文件名解码异常: ", e);
            return Result.error("批量解码失败: " + e.getMessage());
        }
    }

    @Override
    public Result<ExcelDecodeResult> decodeExcelContent(MultipartFile file, Integer idColumnIndex, Integer descriptionColumnIndex) {
        try {
            if (file == null || file.isEmpty()) {
                return Result.error("Excel文件不能为空");
            }

            String fileName = file.getOriginalFilename();
            if (fileName == null || (!fileName.endsWith(".xlsx") && !fileName.endsWith(".xls"))) {
                return Result.error("文件必须是Excel格式（.xlsx或.xls）");
            }

            InputStream inputStream = file.getInputStream();
            return processExcelFile(inputStream, fileName, idColumnIndex, descriptionColumnIndex);
        } catch (Exception e) {
            log.error("Excel文件解码异常: ", e);
            return Result.error("Excel文件解码失败: " + e.getMessage());
        }
    }

    @Override
    public Result<ExcelDecodeResult> decodeExcelContentFromPath(String filePath, Integer idColumnIndex, Integer descriptionColumnIndex) {
        try {
            if (filePath == null || filePath.trim().isEmpty()) {
                return Result.error("文件路径不能为空");
            }

            File file = new File(filePath);
            if (!file.exists()) {
                return Result.error("文件不存在: " + filePath);
            }

            String fileName = file.getName();
            if (!fileName.endsWith(".xlsx") && !fileName.endsWith(".xls")) {
                return Result.error("文件必须是Excel格式（.xlsx或.xls）");
            }

            InputStream inputStream = new FileInputStream(file);
            return processExcelFile(inputStream, fileName, idColumnIndex, descriptionColumnIndex);
        } catch (Exception e) {
            log.error("Excel文件解码异常: ", e);
            return Result.error("Excel文件解码失败: " + e.getMessage());
        }
    }

    /**
     * 处理Excel文件
     */
    private Result<ExcelDecodeResult> processExcelFile(InputStream inputStream, String fileName, 
                                                        Integer idColumnIndex, Integer descriptionColumnIndex) {
        Workbook workbook = null;
        try {
            // 根据文件扩展名创建Workbook
            if (fileName.endsWith(".xlsx")) {
                workbook = new XSSFWorkbook(inputStream);
            } else {
                workbook = new HSSFWorkbook(inputStream);
            }

            Sheet sheet = workbook.getSheetAt(0); // 读取第一个工作表
            List<ExcelRowDecodeResult> rowResults = new ArrayList<>();
            int successCount = 0;
            int failCount = 0;

            // 如果未指定ID列索引，尝试自动查找
            if (idColumnIndex == null) {
                idColumnIndex = findIdColumnIndex(sheet);
                if (idColumnIndex == -1) {
                    return Result.error("未找到包含36位ID的列，请手动指定ID列索引");
                }
                log.info("自动检测到ID列索引: {}", idColumnIndex);
            }

            // 遍历所有行（跳过表头）
            for (int rowIndex = 1; rowIndex <= sheet.getLastRowNum(); rowIndex++) {
                Row row = sheet.getRow(rowIndex);
                if (row == null) {
                    continue;
                }

                ExcelRowDecodeResult rowResult = new ExcelRowDecodeResult();
                rowResult.setRowIndex(rowIndex + 1); // Excel行号从1开始

                try {
                    // 读取ID列
                    Cell idCell = row.getCell(idColumnIndex);
                    String id = getCellValueAsString(idCell);
                    
                    if (id == null || id.trim().isEmpty()) {
                        rowResult.setSuccess(false);
                        rowResult.setErrorMessage("ID列为空");
                        failCount++;
                        rowResults.add(rowResult);
                        continue;
                    }

                    // 从ID字符串中提取36位ID
                    String extractedId = extractIdFromString(id);
                    if (extractedId == null) {
                        rowResult.setId(id);
                        rowResult.setSuccess(false);
                        rowResult.setErrorMessage("未找到36位ID");
                        failCount++;
                        rowResults.add(rowResult);
                        continue;
                    }

                    rowResult.setId(extractedId);

                    // 读取描述列（如果指定）
                    if (descriptionColumnIndex != null) {
                        Cell descCell = row.getCell(descriptionColumnIndex);
                        String description = getCellValueAsString(descCell);
                        rowResult.setDescription(description);
                    }

                    // 解码ID
                    DecodedId decoded = DisasterIdDecoder.decode(extractedId);
                    rowResult.setDecodedId(decoded);
                    rowResult.setSuccess(true);
                    successCount++;

                } catch (Exception e) {
                    rowResult.setSuccess(false);
                    rowResult.setErrorMessage("解码失败: " + e.getMessage());
                    failCount++;
                    log.warn("第{}行解码失败: {}", rowIndex + 1, e.getMessage());
                }

                rowResults.add(rowResult);
            }

            ExcelDecodeResult result = new ExcelDecodeResult();
            result.setFileName(fileName);
            result.setTotalRows(rowResults.size());
            result.setSuccessCount(successCount);
            result.setFailCount(failCount);
            result.setRows(rowResults);

            String message = String.format("Excel解码完成：共%d行，成功%d行，失败%d行", 
                    rowResults.size(), successCount, failCount);
            return Result.success(message, result);

        } catch (Exception e) {
            log.error("处理Excel文件异常: ", e);
            return Result.error("处理Excel文件失败: " + e.getMessage());
        } finally {
            try {
                if (workbook != null) {
                    workbook.close();
                }
                if (inputStream != null) {
                    inputStream.close();
                }
            } catch (Exception e) {
                log.error("关闭文件流异常: ", e);
            }
        }
    }

    /**
     * 自动查找包含36位ID的列索引
     */
    private int findIdColumnIndex(Sheet sheet) {
        Row headerRow = sheet.getRow(0);
        if (headerRow == null) {
            return -1;
        }

        // 检查表头行
        for (int colIndex = 0; colIndex < headerRow.getLastCellNum(); colIndex++) {
            Cell cell = headerRow.getCell(colIndex);
            String cellValue = getCellValueAsString(cell);
            if (cellValue != null) {
                // 检查表头是否包含"ID"、"id"、"编码"等关键词
                String lowerValue = cellValue.toLowerCase();
                if (lowerValue.contains("id") || lowerValue.contains("编码") || 
                    lowerValue.contains("code") || lowerValue.contains("标识")) {
                    // 检查该列的前几行数据是否包含36位ID
                    for (int rowIndex = 1; rowIndex <= Math.min(10, sheet.getLastRowNum()); rowIndex++) {
                        Row row = sheet.getRow(rowIndex);
                        if (row != null) {
                            Cell cell2 = row.getCell(colIndex);
                            String value = getCellValueAsString(cell2);
                            if (value != null && extractIdFromString(value) != null) {
                                return colIndex;
                            }
                        }
                    }
                }
            }
        }

        // 如果表头未找到，检查前几行数据
        for (int colIndex = 0; colIndex < 10; colIndex++) {
            for (int rowIndex = 1; rowIndex <= Math.min(10, sheet.getLastRowNum()); rowIndex++) {
                Row row = sheet.getRow(rowIndex);
                if (row != null) {
                    Cell cell = row.getCell(colIndex);
                    String value = getCellValueAsString(cell);
                    if (value != null && extractIdFromString(value) != null) {
                        return colIndex;
                    }
                }
            }
        }

        return -1;
    }

    /**
     * 获取单元格值作为字符串
     */
    private String getCellValueAsString(Cell cell) {
        if (cell == null) {
            return null;
        }

        switch (cell.getCellType()) {
            case STRING:
                return cell.getStringCellValue();
            case NUMERIC:
                if (DateUtil.isCellDateFormatted(cell)) {
                    return cell.getDateCellValue().toString();
                } else {
                    // 处理数字，避免科学计数法
                    double numericValue = cell.getNumericCellValue();
                    if (numericValue == (long) numericValue) {
                        return String.valueOf((long) numericValue);
                    } else {
                        return String.valueOf(numericValue);
                    }
                }
            case BOOLEAN:
                return String.valueOf(cell.getBooleanCellValue());
            case FORMULA:
                return cell.getCellFormula();
            default:
                return null;
        }
    }

    /**
     * 从字符串中提取36位ID
     */
    private String extractIdFromString(String str) {
        if (str == null) {
            return null;
        }

        Matcher matcher = ID_PATTERN.matcher(str);
        if (matcher.find()) {
            return matcher.group();
        }

        return null;
    }

    // ========== 新增：基于已上传文件的解码功能 ==========

    @Override
    public Result<List<FileMetadata>> getDecodableFiles() {
        try {
            // 获取所有文件
            List<FileMetadata> allFiles = fileMetadataRepository.findByStatus(1);

            // 过滤出文件名包含36位数字的文件
            List<FileMetadata> decodableFiles = allFiles.stream()
                    .filter(file -> {
                        String fileName = file.getOriginalName();
                        if (fileName == null) {
                            fileName = file.getFileName();
                        }
                        return fileName != null && extractIdFromString(fileName) != null;
                    })
                    .collect(Collectors.toList());

            return Result.success("获取可解码文件列表成功", decodableFiles);
        } catch (Exception e) {
            log.error("获取可解码文件列表失败: ", e);
            return Result.error("获取可解码文件列表失败: " + e.getMessage());
        }
    }

    @Override
    public Result<List<FileMetadata>> getExcelFiles() {
        try {
            // 获取所有文件
            List<FileMetadata> allFiles = fileMetadataRepository.findByStatus(1);

            // 过滤出Excel文件
            List<FileMetadata> excelFiles = allFiles.stream()
                    .filter(file -> {
                        String ext = file.getFileExtension();
                        if (ext == null) {
                            String fileName = file.getOriginalName();
                            if (fileName != null) {
                                ext = fileName.substring(fileName.lastIndexOf('.') + 1).toLowerCase();
                            }
                        }
                        return "xlsx".equalsIgnoreCase(ext) || "xls".equalsIgnoreCase(ext);
                    })
                    .collect(Collectors.toList());

            return Result.success("获取Excel文件列表成功", excelFiles);
        } catch (Exception e) {
            log.error("获取Excel文件列表失败: ", e);
            return Result.error("获取Excel文件列表失败: " + e.getMessage());
        }
    }

    @Override
    public Result<FileDecodeResult> decodeFileById(String fileId) {
        try {
            if (fileId == null || fileId.trim().isEmpty()) {
                return Result.error("文件ID不能为空");
            }

            // 获取文件元数据
            Result<FileMetadata> fileResult = fileService.getFileById(fileId);
            if (fileResult.getCode() != 200 || fileResult.getData() == null) {
                return Result.error("文件不存在: " + fileId);
            }

            FileMetadata file = fileResult.getData();
            String fileName = file.getOriginalName();
            if (fileName == null || fileName.isEmpty()) {
                fileName = file.getFileName();
            }

            // 解码文件名
            Result<FileDecodeResult> decodeResult = decodeFileName(fileName);
            if (decodeResult.getCode() == 200 && decodeResult.getData() != null) {
                // 添加文件ID到结果
                FileDecodeResult result = decodeResult.getData();
                result.setFileId(fileId);
                return Result.success("文件名解码成功", result);
            }

            return decodeResult;
        } catch (Exception e) {
            log.error("根据文件ID解码失败: ", e);
            return Result.error("根据文件ID解码失败: " + e.getMessage());
        }
    }

    @Override
    public Result<List<FileDecodeResult>> decodeFilesByIds(List<String> fileIds) {
        try {
            if (fileIds == null || fileIds.isEmpty()) {
                return Result.error("文件ID列表不能为空");
            }

            List<FileDecodeResult> results = new ArrayList<>();
            int successCount = 0;
            int failCount = 0;

            for (String fileId : fileIds) {
                Result<FileDecodeResult> result = decodeFileById(fileId);
                if (result.getCode() == 200 && result.getData() != null) {
                    results.add(result.getData());
                    if (result.getData().getHasValidId() != null && result.getData().getHasValidId()) {
                        successCount++;
                    } else {
                        failCount++;
                    }
                } else {
                    failCount++;
                }
            }

            String message = String.format("批量解码完成：成功%d个，失败%d个", successCount, failCount);
            return Result.success(message, results);
        } catch (Exception e) {
            log.error("批量文件ID解码失败: ", e);
            return Result.error("批量解码失败: " + e.getMessage());
        }
    }

    @Override
    public Result<ExcelDecodeResult> decodeExcelById(String fileId, Integer idColumnIndex,
                                                      Integer descriptionColumnIndex, Integer startRow, Integer endRow) {
        try {
            if (fileId == null || fileId.trim().isEmpty()) {
                return Result.error("文件ID不能为空");
            }

            // 获取文件元数据
            Result<FileMetadata> fileResult = fileService.getFileById(fileId);
            if (fileResult.getCode() != 200 || fileResult.getData() == null) {
                return Result.error("文件不存在: " + fileId);
            }

            FileMetadata file = fileResult.getData();
            String fileName = file.getOriginalName();
            if (fileName == null) {
                fileName = file.getFileName();
            }

            // 检查是否为Excel文件
            if (fileName == null || (!fileName.endsWith(".xlsx") && !fileName.endsWith(".xls"))) {
                return Result.error("文件必须是Excel格式（.xlsx或.xls）");
            }

            // 下载文件内容
            Result<byte[]> downloadResult = fileService.downloadFile(fileId);
            if (downloadResult.getCode() != 200 || downloadResult.getData() == null) {
                return Result.error("无法读取文件内容");
            }

            byte[] fileBytes = downloadResult.getData();
            InputStream inputStream = new ByteArrayInputStream(fileBytes);

            // 处理Excel文件（带行范围参数）
            return processExcelFileWithRange(inputStream, fileName, idColumnIndex, descriptionColumnIndex, startRow, endRow);
        } catch (Exception e) {
            log.error("根据文件ID解码Excel失败: ", e);
            return Result.error("Excel解码失败: " + e.getMessage());
        }
    }

    /**
     * 处理Excel文件（支持行范围）
     */
    private Result<ExcelDecodeResult> processExcelFileWithRange(InputStream inputStream, String fileName,
                                                                  Integer idColumnIndex, Integer descriptionColumnIndex,
                                                                  Integer startRow, Integer endRow) {
        Workbook workbook = null;
        try {
            // 根据文件扩展名创建Workbook
            if (fileName.endsWith(".xlsx")) {
                workbook = new XSSFWorkbook(inputStream);
            } else {
                workbook = new HSSFWorkbook(inputStream);
            }

            Sheet sheet = workbook.getSheetAt(0);
            List<ExcelRowDecodeResult> rowResults = new ArrayList<>();
            int successCount = 0;
            int failCount = 0;

            // 如果未指定ID列索引，尝试自动查找
            if (idColumnIndex == null) {
                idColumnIndex = findIdColumnIndex(sheet);
                if (idColumnIndex == -1) {
                    return Result.error("未找到包含36位ID的列，请手动指定ID列索引");
                }
                log.info("自动检测到ID列索引: {}", idColumnIndex);
            }

            // 确定行范围
            int actualStartRow = (startRow != null && startRow > 1) ? startRow : 2; // 默认从第2行开始（跳过表头）
            int actualEndRow = (endRow != null && endRow > 0) ? endRow : sheet.getLastRowNum() + 1;

            // 遍历指定范围的行
            for (int rowIndex = actualStartRow - 1; rowIndex < actualEndRow && rowIndex <= sheet.getLastRowNum(); rowIndex++) {
                Row row = sheet.getRow(rowIndex);
                if (row == null) {
                    continue;
                }

                ExcelRowDecodeResult rowResult = new ExcelRowDecodeResult();
                rowResult.setRowIndex(rowIndex + 1); // Excel行号从1开始

                try {
                    // 读取ID列
                    Cell idCell = row.getCell(idColumnIndex);
                    String id = getCellValueAsString(idCell);

                    if (id == null || id.trim().isEmpty()) {
                        rowResult.setSuccess(false);
                        rowResult.setErrorMessage("ID列为空");
                        failCount++;
                        rowResults.add(rowResult);
                        continue;
                    }

                    // 从ID字符串中提取36位ID
                    String extractedId = extractIdFromString(id);
                    if (extractedId == null) {
                        rowResult.setId(id);
                        rowResult.setSuccess(false);
                        rowResult.setErrorMessage("未找到36位ID");
                        failCount++;
                        rowResults.add(rowResult);
                        continue;
                    }

                    rowResult.setId(extractedId);

                    // 读取描述列（如果指定）
                    if (descriptionColumnIndex != null) {
                        Cell descCell = row.getCell(descriptionColumnIndex);
                        String description = getCellValueAsString(descCell);
                        rowResult.setDescription(description);
                    }

                    // 解码ID
                    DecodedId decoded = DisasterIdDecoder.decode(extractedId);
                    rowResult.setDecodedId(decoded);
                    rowResult.setSuccess(true);
                    successCount++;

                } catch (Exception e) {
                    rowResult.setSuccess(false);
                    rowResult.setErrorMessage("解码失败: " + e.getMessage());
                    failCount++;
                    log.warn("第{}行解码失败: {}", rowIndex + 1, e.getMessage());
                }

                rowResults.add(rowResult);
            }

            ExcelDecodeResult result = new ExcelDecodeResult();
            result.setFileName(fileName);
            result.setTotalRows(rowResults.size());
            result.setSuccessCount(successCount);
            result.setFailCount(failCount);
            result.setRows(rowResults);

            String message = String.format("Excel解码完成：共%d行，成功%d行，失败%d行",
                    rowResults.size(), successCount, failCount);
            return Result.success(message, result);

        } catch (Exception e) {
            log.error("处理Excel文件异常: ", e);
            return Result.error("处理Excel文件失败: " + e.getMessage());
        } finally {
            try {
                if (workbook != null) {
                    workbook.close();
                }
                if (inputStream != null) {
                    inputStream.close();
                }
            } catch (Exception e) {
                log.error("关闭文件流异常: ", e);
            }
        }
    }
}

