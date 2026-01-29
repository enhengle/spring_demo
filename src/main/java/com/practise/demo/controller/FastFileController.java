package com.practise.demo.controller;

import com.practise.demo.common.constant.ErrorCode;
import com.practise.demo.response.Response;
import com.practise.demo.service.FastFileService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.util.List;

/**
 * 快速文件处理控制器
 * 提供高性能的文件处理接口
 * 
 * @author system
 */
@RestController
@RequestMapping("/api/fast-file")
@Tag(name = "快速文件处理", description = "使用高性能组件进行文件切割、合并等操作")
public class FastFileController {
    
    @Autowired
    private FastFileService fastFileService;
    
    /**
     * 快速按大小切割文件
     */
    @PostMapping("/split/size")
    @Operation(summary = "快速按大小切割文件", description = "使用NIO和内存映射技术快速切割文件")
    public Response<List<String>> fastSplitBySize(
            @Parameter(description = "要切割的文件") @RequestParam("file") MultipartFile file,
            @Parameter(description = "每个文件的最大大小（MB）") @RequestParam("maxSizeMB") double maxSizeMB) {
        
        if (file.isEmpty()) {
            return Response.error(ErrorCode.PARAM_ERROR.getCode(), "文件不能为空");
        }
        
        if (maxSizeMB <= 0) {
            return Response.error(ErrorCode.PARAM_ERROR.getCode(), "文件大小必须大于0");
        }
        
        try {
            List<String> outputFiles = fastFileService.fastSplitBySize(file, maxSizeMB);
            return Response.ok(outputFiles);
        } catch (Exception e) {
            return Response.error(ErrorCode.OPERATION_FAILED.getCode(), "快速切割文件失败: " + e.getMessage());
        }
    }
    
    /**
     * 快速按行数切割文件
     */
    @PostMapping("/split/lines")
    @Operation(summary = "快速按行数切割文件", description = "使用大缓冲区快速切割文件")
    public Response<List<String>> fastSplitByLines(
            @Parameter(description = "要切割的文件") @RequestParam("file") MultipartFile file,
            @Parameter(description = "每个文件的行数") @RequestParam("linesPerFile") int linesPerFile) {
        
        if (file.isEmpty()) {
            return Response.error(ErrorCode.PARAM_ERROR.getCode(), "文件不能为空");
        }
        
        if (linesPerFile <= 0) {
            return Response.error(ErrorCode.PARAM_ERROR.getCode(), "行数必须大于0");
        }
        
        try {
            List<String> outputFiles = fastFileService.fastSplitByLines(file, linesPerFile);
            return Response.ok(outputFiles);
        } catch (Exception e) {
            return Response.error(ErrorCode.OPERATION_FAILED.getCode(), "快速切割文件失败: " + e.getMessage());
        }
    }
    
    /**
     * 快速合并文件
     */
    @PostMapping("/merge")
    @Operation(summary = "快速合并文件", description = "使用NIO零拷贝技术快速合并文件")
    public Response<String> fastMergeFiles(
            @Parameter(description = "要合并的文件列表") @RequestParam("files") MultipartFile[] files,
            @Parameter(description = "输出文件名") @RequestParam("outputFileName") String outputFileName) {
        
        if (files == null || files.length == 0) {
            return Response.error(ErrorCode.PARAM_ERROR.getCode(), "文件列表不能为空");
        }
        
        if (outputFileName == null || outputFileName.trim().isEmpty()) {
            return Response.error(ErrorCode.PARAM_ERROR.getCode(), "输出文件名不能为空");
        }
        
        try {
            String outputFilePath = fastFileService.fastMergeFiles(files, outputFileName);
            return Response.ok(outputFilePath);
        } catch (Exception e) {
            return Response.error(ErrorCode.OPERATION_FAILED.getCode(), "快速合并文件失败: " + e.getMessage());
        }
    }
    
    /**
     * 下载文件
     */
    @GetMapping("/download")
    @Operation(summary = "下载文件", description = "下载处理后的文件")
    public ResponseEntity<Resource> downloadFile(
            @Parameter(description = "文件路径") @RequestParam("filePath") String filePath) {
        
        try {
            File file = new File(filePath);
            if (!file.exists()) {
                return ResponseEntity.notFound().build();
            }
            
            Resource resource = new FileSystemResource(file);
            return ResponseEntity.ok()
                    .contentType(MediaType.APPLICATION_OCTET_STREAM)
                    .header(HttpHeaders.CONTENT_DISPOSITION, 
                            "attachment; filename=\"" + file.getName() + "\"")
                    .body(resource);
                    
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }
}
