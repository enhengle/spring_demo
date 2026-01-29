package com.practise.demo.controller;

import com.practise.demo.common.constant.ErrorCode;
import com.practise.demo.response.Response;
import com.practise.demo.service.RdfFileService;
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
 * RDF-File 文件操作控制器
 * 
 * @author system
 */
@RestController
@RequestMapping("/api/rdf-file")
@Tag(name = "RDF文件操作", description = "使用rdf-file组件进行文件切割和合并")
public class RdfFileController {
    
    @Autowired
    private RdfFileService rdfFileService;
    
    /**
     * 按大小切割文件
     */
    @PostMapping("/split/size")
    @Operation(summary = "按大小切割文件", description = "使用rdf-file组件按指定大小切割文件")
    public Response<List<String>> splitBySize(
            @Parameter(description = "要切割的文件") @RequestParam("file") MultipartFile file,
            @Parameter(description = "每个文件的最大大小（MB）") @RequestParam("maxSizeMB") double maxSizeMB) {
        
        if (file.isEmpty()) {
            return Response.error(ErrorCode.PARAM_ERROR.getCode(), "文件不能为空");
        }
        
        if (maxSizeMB <= 0) {
            return Response.error(ErrorCode.PARAM_ERROR.getCode(), "文件大小必须大于0");
        }
        
        try {
            List<String> outputFiles = rdfFileService.splitBySize(file, maxSizeMB);
            return Response.ok(outputFiles);
        } catch (Exception e) {
            return Response.error(ErrorCode.OPERATION_FAILED.getCode(), "切割文件失败: " + e.getMessage());
        }
    }
    
    /**
     * 按行数切割文件
     */
    @PostMapping("/split/lines")
    @Operation(summary = "按行数切割文件", description = "使用rdf-file组件按指定行数切割文件")
    public Response<List<String>> splitByLines(
            @Parameter(description = "要切割的文件") @RequestParam("file") MultipartFile file,
            @Parameter(description = "每个文件的行数") @RequestParam("linesPerFile") int linesPerFile) {
        
        if (file.isEmpty()) {
            return Response.error(ErrorCode.PARAM_ERROR.getCode(), "文件不能为空");
        }
        
        if (linesPerFile <= 0) {
            return Response.error(ErrorCode.PARAM_ERROR.getCode(), "行数必须大于0");
        }
        
        try {
            List<String> outputFiles = rdfFileService.splitByLines(file, linesPerFile);
            return Response.ok(outputFiles);
        } catch (Exception e) {
            return Response.error(ErrorCode.OPERATION_FAILED.getCode(), "切割文件失败: " + e.getMessage());
        }
    }
    
    /**
     * 合并文件
     */
    @PostMapping("/merge")
    @Operation(summary = "合并文件", description = "使用rdf-file组件合并多个文件")
    public Response<String> mergeFiles(
            @Parameter(description = "要合并的文件列表") @RequestParam("files") MultipartFile[] files,
            @Parameter(description = "输出文件名") @RequestParam("outputFileName") String outputFileName) {
        
        if (files == null || files.length == 0) {
            return Response.error(ErrorCode.PARAM_ERROR.getCode(), "文件列表不能为空");
        }
        
        if (outputFileName == null || outputFileName.trim().isEmpty()) {
            return Response.error(ErrorCode.PARAM_ERROR.getCode(), "输出文件名不能为空");
        }
        
        try {
            String outputFilePath = rdfFileService.mergeFiles(files, outputFileName);
            return Response.ok(outputFilePath);
        } catch (Exception e) {
            return Response.error(ErrorCode.OPERATION_FAILED.getCode(), "合并文件失败: " + e.getMessage());
        }
    }
    
    /**
     * 切割 Excel 文件（按工作表）
     */
    @PostMapping("/excel/split/sheets")
    @Operation(summary = "切割Excel文件", description = "将Excel文件按工作表切割成多个文件")
    public Response<List<String>> splitExcelBySheets(
            @Parameter(description = "Excel文件") @RequestParam("file") MultipartFile file) {
        
        if (file.isEmpty()) {
            return Response.error(ErrorCode.PARAM_ERROR.getCode(), "文件不能为空");
        }
        
        try {
            List<String> outputFiles = rdfFileService.splitExcelBySheets(file);
            return Response.ok(outputFiles);
        } catch (Exception e) {
            return Response.error(ErrorCode.OPERATION_FAILED.getCode(), "切割Excel文件失败: " + e.getMessage());
        }
    }
    
    /**
     * 合并 Excel 文件
     */
    @PostMapping("/excel/merge")
    @Operation(summary = "合并Excel文件", description = "合并多个Excel文件为一个文件")
    public Response<String> mergeExcelFiles(
            @Parameter(description = "要合并的Excel文件列表") @RequestParam("files") MultipartFile[] files,
            @Parameter(description = "输出文件名") @RequestParam("outputFileName") String outputFileName,
            @Parameter(description = "工作表名称") @RequestParam(value = "sheetName", required = false) String sheetName) {
        
        if (files == null || files.length == 0) {
            return Response.error(ErrorCode.PARAM_ERROR.getCode(), "文件列表不能为空");
        }
        
        if (outputFileName == null || outputFileName.trim().isEmpty()) {
            return Response.error(ErrorCode.PARAM_ERROR.getCode(), "输出文件名不能为空");
        }
        
        try {
            String outputFilePath = rdfFileService.mergeExcelFiles(files, outputFileName, sheetName);
            return Response.ok(outputFilePath);
        } catch (Exception e) {
            return Response.error(ErrorCode.OPERATION_FAILED.getCode(), "合并Excel文件失败: " + e.getMessage());
        }
    }
    
    /**
     * 下载文件
     */
    @GetMapping("/download")
    @Operation(summary = "下载文件", description = "下载切割或合并后的文件")
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
