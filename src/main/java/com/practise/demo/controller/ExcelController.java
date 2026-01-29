package com.practise.demo.controller;

import com.practise.demo.common.constant.ErrorCode;
import com.practise.demo.model.dto.ExportExcelRequest;
import com.practise.demo.response.Response;
import com.practise.demo.service.ExcelService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.OutputStream;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Excel 操作控制器
 * 
 * @author system
 */
@RestController
@RequestMapping("/api/excel")
@Tag(name = "Excel操作", description = "Excel文件的读取、导出等功能")
public class ExcelController {
    
    @Autowired
    private ExcelService excelService;
    
    /**
     * 上传并读取 Excel 文件
     * 
     * @param file Excel 文件
     * @param hasHeader 是否包含表头（默认 true）
     * @param sheetIndex 工作表索引（默认 0）
     * @return 数据列表
     */
    @PostMapping("/read")
    @Operation(summary = "读取Excel文件", description = "上传Excel文件并返回数据列表")
    public Response<List<Map<String, Object>>> readExcel(
            @Parameter(description = "Excel文件") @RequestParam("file") MultipartFile file,
            @Parameter(description = "是否包含表头") @RequestParam(value = "hasHeader", defaultValue = "true") boolean hasHeader,
            @Parameter(description = "工作表索引") @RequestParam(value = "sheetIndex", defaultValue = "0") int sheetIndex) {
        
        if (file.isEmpty()) {
            return Response.error(ErrorCode.PARAM_ERROR.getCode(), "文件不能为空");
        }
        
        String fileName = file.getOriginalFilename();
        if (fileName == null || (!fileName.endsWith(".xls") && !fileName.endsWith(".xlsx"))) {
            return Response.error(ErrorCode.PARAM_ERROR.getCode(), "文件格式不正确，仅支持 .xls 和 .xlsx 格式");
        }
        
        try {
            List<Map<String, Object>> dataList = excelService.readExcel(file, hasHeader, sheetIndex);
            return Response.ok(dataList);
        } catch (Exception e) {
            return Response.error(ErrorCode.OPERATION_FAILED.getCode(), "读取Excel文件失败: " + e.getMessage());
        }
    }
    
    /**
     * 获取 Excel 文件的工作表名称列表
     * 
     * @param file Excel 文件
     * @return 工作表名称列表
     */
    @PostMapping("/sheets")
    @Operation(summary = "获取工作表列表", description = "获取Excel文件中的所有工作表名称")
    public Response<List<String>> getSheetNames(
            @Parameter(description = "Excel文件") @RequestParam("file") MultipartFile file) {
        
        if (file.isEmpty()) {
            return Response.error(ErrorCode.PARAM_ERROR.getCode(), "文件不能为空");
        }
        
        try {
            List<String> sheetNames = excelService.getSheetNames(file);
            return Response.ok(sheetNames);
        } catch (Exception e) {
            return Response.error(ErrorCode.OPERATION_FAILED.getCode(), "获取工作表列表失败: " + e.getMessage());
        }
    }
    
    /**
     * 获取工作表行数
     * 
     * @param file Excel 文件
     * @param sheetIndex 工作表索引
     * @return 行数
     */
    @PostMapping("/row-count")
    @Operation(summary = "获取行数", description = "获取指定工作表的行数")
    public Response<Integer> getRowCount(
            @Parameter(description = "Excel文件") @RequestParam("file") MultipartFile file,
            @Parameter(description = "工作表索引") @RequestParam(value = "sheetIndex", defaultValue = "0") int sheetIndex) {
        
        if (file.isEmpty()) {
            return Response.error(ErrorCode.PARAM_ERROR.getCode(), "文件不能为空");
        }
        
        try {
            int rowCount = excelService.getRowCount(file, sheetIndex);
            return Response.ok(rowCount);
        } catch (Exception e) {
            return Response.error(ErrorCode.OPERATION_FAILED.getCode(), "获取行数失败: " + e.getMessage());
        }
    }
    
    /**
     * 导出数据为 Excel 文件（支持格式配置）
     * 
     * @param request 导出请求（包含数据、表头、格式配置等）
     * @param response HTTP响应
     */
    @PostMapping("/export")
    @Operation(summary = "导出Excel文件", description = "将数据导出为Excel文件并下载，支持列格式配置")
    public void exportExcel(
            @Parameter(description = "导出请求（JSON格式）") @RequestBody ExportExcelRequest request,
            HttpServletResponse response) {
        
        if (request == null || request.getDataList() == null || request.getDataList().isEmpty()) {
            try {
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                response.getWriter().write("数据列表不能为空");
            } catch (IOException e) {
                // ignore
            }
            return;
        }
        
        try {
            // 设置响应头
            String fileName = request.getFileName() != null ? request.getFileName() : "export.xlsx";
            response.setContentType(MediaType.APPLICATION_OCTET_STREAM_VALUE);
            response.setHeader(HttpHeaders.CONTENT_DISPOSITION, 
                    "attachment; filename=\"" + URLEncoder.encode(fileName, StandardCharsets.UTF_8.toString()) + "\"");
            
            // 导出Excel
            OutputStream outputStream = response.getOutputStream();
            String[] headers = request.getHeaders();
            String sheetName = request.getSheetName() != null ? request.getSheetName() : "Sheet1";
            
            if (headers != null && headers.length > 0) {
                excelService.exportExcel(outputStream, request.getDataList(), headers, sheetName, request.toColumnFormatMap());
            } else {
                excelService.exportExcel(outputStream, request.getDataList(), sheetName, request.toColumnFormatMap());
            }
            outputStream.flush();
        } catch (Exception e) {
            try {
                response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                response.getWriter().write("导出Excel失败: " + e.getMessage());
            } catch (IOException ioException) {
                // ignore
            }
        }
    }
    
    /**
     * 导出数据为 Excel 文件（简化版，兼容旧接口）
     * 
     * @param dataList 数据列表（JSON格式）
     * @param headers 表头数组（可选，如果不提供则使用Map的key）
     * @param sheetName 工作表名称（可选，默认Sheet1）
     * @param fileName 文件名（可选，默认export.xlsx）
     * @param response HTTP响应
     */
    @PostMapping("/export-simple")
    @Operation(summary = "导出Excel文件（简化版）", description = "将数据导出为Excel文件并下载，不支持格式配置")
    public void exportExcelSimple(
            @Parameter(description = "数据列表（JSON格式）") @RequestBody List<Map<String, Object>> dataList,
            @Parameter(description = "表头数组") @RequestParam(value = "headers", required = false) String[] headers,
            @Parameter(description = "工作表名称") @RequestParam(value = "sheetName", defaultValue = "Sheet1") String sheetName,
            @Parameter(description = "文件名") @RequestParam(value = "fileName", defaultValue = "export.xlsx") String fileName,
            HttpServletResponse response) {
        
        ExportExcelRequest request = new ExportExcelRequest();
        request.setDataList(dataList);
        request.setHeaders(headers);
        request.setSheetName(sheetName);
        request.setFileName(fileName);
        exportExcel(request, response);
    }
    
    /**
     * 导出示例数据（用于测试）
     * 
     * @param response HTTP响应
     */
    @GetMapping("/export-sample")
    @Operation(summary = "导出示例数据", description = "导出示例Excel文件用于测试")
    public void exportSample(HttpServletResponse response) {
        // 创建示例数据
        List<Map<String, Object>> dataList = new java.util.ArrayList<>();
        for (int i = 1; i <= 10; i++) {
            Map<String, Object> row = new HashMap<>();
            row.put("序号", i);
            row.put("姓名", "用户" + i);
            row.put("年龄", 20 + i);
            row.put("邮箱", "user" + i + "@example.com");
            row.put("部门", i % 2 == 0 ? "技术部" : "产品部");
            dataList.add(row);
        }
        
        String[] headers = {"序号", "姓名", "年龄", "邮箱", "部门"};
        
        try {
            response.setContentType(MediaType.APPLICATION_OCTET_STREAM_VALUE);
            response.setHeader(HttpHeaders.CONTENT_DISPOSITION, 
                    "attachment; filename=\"" + URLEncoder.encode("sample.xlsx", StandardCharsets.UTF_8.toString()) + "\"");
            
            OutputStream outputStream = response.getOutputStream();
            excelService.exportExcel(outputStream, dataList, headers, "示例数据");
            outputStream.flush();
        } catch (Exception e) {
            try {
                response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                response.getWriter().write("导出示例数据失败: " + e.getMessage());
            } catch (IOException ioException) {
                // ignore
            }
        }
    }
}
