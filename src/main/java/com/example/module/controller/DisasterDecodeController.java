package com.example.module.controller;

import com.example.module.service.DisasterDecodeService;
import com.example.module.service.FileDecodeService;
import com.example.module.service.GeoLocationService;
import com.example.module.util.DecodedId;
import com.example.module.util.Result;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

/**
 * 灾情ID解码控制器
 * 提供ID解码、验证、地理信息查询等接口
 */
@RestController
@RequestMapping("/api/disaster-decode")
@RequiredArgsConstructor
public class DisasterDecodeController {

    private final DisasterDecodeService disasterDecodeService;
    private final GeoLocationService geoLocationService;
    private final FileDecodeService fileDecodeService;

    /**
     * 解码单个ID
     * 
     * @param id 36位一体化编码ID
     * @return 解码结果
     */
    @GetMapping("/decode/{id}")
    public Result<DecodedId> decodeId(@PathVariable String id) {
        return disasterDecodeService.decodeId(id);
    }

    /**
     * 批量解码ID
     * 
     * @param request 包含ID列表的请求体
     * @return 解码结果列表
     */
    @PostMapping("/decode/batch")
    public Result<List<DecodedId>> batchDecodeIds(@RequestBody BatchDecodeRequest request) {
        return disasterDecodeService.batchDecodeIds(request.getIds());
    }

    /**
     * 验证ID格式
     * 
     * @param id 36位一体化编码ID
     * @return 验证结果
     */
    @GetMapping("/validate/{id}")
    public Result<Boolean> validateId(@PathVariable String id) {
        return disasterDecodeService.validateId(id);
    }

    /**
     * 根据地理码获取地理位置信息
     * 
     * @param geoCode 12位地理码
     * @return 地理位置信息
     */
    @GetMapping("/geo/{geoCode}")
    public Result<GeoLocationService.GeoLocationInfo> getLocationByGeoCode(@PathVariable String geoCode) {
        return geoLocationService.getLocationByGeoCode(geoCode);
    }

    /**
     * 根据经纬度获取地理位置信息（逆地理编码）
     * 
     * @param longitude 经度
     * @param latitude 纬度
     * @return 地理位置信息
     */
    @GetMapping("/geo/reverse")
    public Result<GeoLocationService.GeoLocationInfo> getLocationByCoordinates(
            @RequestParam Double longitude,
            @RequestParam Double latitude) {
        return geoLocationService.getLocationByCoordinates(longitude, latitude);
    }

    /**
     * 解码文件名
     * 从文件名中提取36位ID并解码
     * 
     * @param fileName 文件名（可能包含36位ID）
     * @return 解码结果
     */
    @PostMapping("/file/decode-filename")
    public Result<FileDecodeService.FileDecodeResult> decodeFileName(@RequestBody DecodeFileNameRequest request) {
        return fileDecodeService.decodeFileName(request.getFileName());
    }

    /**
     * 批量解码文件名
     * 
     * @param request 包含文件名列表的请求
     * @return 解码结果列表
     */
    @PostMapping("/file/decode-filenames/batch")
    public Result<List<FileDecodeService.FileDecodeResult>> batchDecodeFileNames(@RequestBody BatchDecodeFileNameRequest request) {
        return fileDecodeService.batchDecodeFileNames(request.getFileNames());
    }

    /**
     * 解码Excel文件内容
     * 读取Excel文件，提取其中的ID列并解码
     * 
     * @param file Excel文件
     * @param idColumnIndex ID列索引（从0开始，如果为null则自动查找）
     * @param descriptionColumnIndex 描述列索引（可选）
     * @return 解码结果
     */
    @PostMapping("/file/decode-excel")
    public Result<FileDecodeService.ExcelDecodeResult> decodeExcelContent(
            @RequestParam("file") MultipartFile file,
            @RequestParam(value = "idColumnIndex", required = false) Integer idColumnIndex,
            @RequestParam(value = "descriptionColumnIndex", required = false) Integer descriptionColumnIndex) {
        return fileDecodeService.decodeExcelContent(file, idColumnIndex, descriptionColumnIndex);
    }

    /**
     * 解码Excel文件内容（从文件路径）
     * 
     * @param filePath 文件路径
     * @param idColumnIndex ID列索引
     * @param descriptionColumnIndex 描述列索引
     * @return 解码结果
     */
    @PostMapping("/file/decode-excel/path")
    public Result<FileDecodeService.ExcelDecodeResult> decodeExcelContentFromPath(
            @RequestBody DecodeExcelPathRequest request) {
        return fileDecodeService.decodeExcelContentFromPath(
                request.getFilePath(), 
                request.getIdColumnIndex(), 
                request.getDescriptionColumnIndex());
    }

    /**
     * 批量解码请求体
     */
    public static class BatchDecodeRequest {
        private List<String> ids;

        public List<String> getIds() {
            return ids;
        }

        public void setIds(List<String> ids) {
            this.ids = ids;
        }
    }

    /**
     * 解码文件名请求体
     */
    public static class DecodeFileNameRequest {
        private String fileName;

        public String getFileName() {
            return fileName;
        }

        public void setFileName(String fileName) {
            this.fileName = fileName;
        }
    }

    /**
     * 批量解码文件名请求体
     */
    public static class BatchDecodeFileNameRequest {
        private List<String> fileNames;

        public List<String> getFileNames() {
            return fileNames;
        }

        public void setFileNames(List<String> fileNames) {
            this.fileNames = fileNames;
        }
    }

    /**
     * 解码Excel路径请求体
     */
    public static class DecodeExcelPathRequest {
        private String filePath;
        private Integer idColumnIndex;
        private Integer descriptionColumnIndex;

        public String getFilePath() {
            return filePath;
        }

        public void setFilePath(String filePath) {
            this.filePath = filePath;
        }

        public Integer getIdColumnIndex() {
            return idColumnIndex;
        }

        public void setIdColumnIndex(Integer idColumnIndex) {
            this.idColumnIndex = idColumnIndex;
        }

        public Integer getDescriptionColumnIndex() {
            return descriptionColumnIndex;
        }

        public void setDescriptionColumnIndex(Integer descriptionColumnIndex) {
            this.descriptionColumnIndex = descriptionColumnIndex;
        }
    }
}

