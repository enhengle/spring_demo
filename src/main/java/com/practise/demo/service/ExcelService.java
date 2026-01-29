package com.practise.demo.service;

import com.practise.demo.util.ColumnFormat;
import com.practise.demo.util.ExcelUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.util.List;
import java.util.Map;

/**
 * Excel 服务类
 * 
 * @author system
 */
@Service
public class ExcelService {
    
    private static final Logger logger = LoggerFactory.getLogger(ExcelService.class);
    
    /**
     * 读取上传的 Excel 文件
     * 
     * @param file 上传的文件
     * @param hasHeader 是否包含表头
     * @return 数据列表
     */
    public List<Map<String, Object>> readExcel(MultipartFile file, boolean hasHeader) {
        return readExcel(file, hasHeader, 0);
    }
    
    /**
     * 读取上传的 Excel 文件指定工作表
     * 
     * @param file 上传的文件
     * @param hasHeader 是否包含表头
     * @param sheetIndex 工作表索引
     * @return 数据列表
     */
    public List<Map<String, Object>> readExcel(MultipartFile file, boolean hasHeader, int sheetIndex) {
        try {
            // 保存临时文件
            String tempFilePath = saveTempFile(file);
            try {
                return ExcelUtil.readExcel(tempFilePath, hasHeader, sheetIndex);
            } finally {
                // 删除临时文件
                deleteTempFile(tempFilePath);
            }
        } catch (IOException e) {
            logger.error("读取 Excel 文件失败", e);
            throw new RuntimeException("读取 Excel 文件失败: " + e.getMessage(), e);
        }
    }
    
    /**
     * 读取 Excel 文件为对象列表
     * 
     * @param file 上传的文件
     * @param clazz 目标类型
     * @param hasHeader 是否包含表头
     * @return 对象列表
     */
    public <T> List<T> readExcel(MultipartFile file, Class<T> clazz, boolean hasHeader) {
        try {
            String tempFilePath = saveTempFile(file);
            try {
                return ExcelUtil.readExcel(tempFilePath, clazz, hasHeader);
            } finally {
                deleteTempFile(tempFilePath);
            }
        } catch (IOException e) {
            logger.error("读取 Excel 文件失败", e);
            throw new RuntimeException("读取 Excel 文件失败: " + e.getMessage(), e);
        }
    }
    
    /**
     * 导出数据到 Excel 文件
     * 
     * @param outputStream 输出流
     * @param dataList 数据列表
     * @param headers 表头数组
     * @param sheetName 工作表名称
     */
    public void exportExcel(OutputStream outputStream, List<Map<String, Object>> dataList, 
                           String[] headers, String sheetName) {
        ExcelUtil.exportToStream(outputStream, dataList, headers, sheetName);
    }
    
    /**
     * 导出数据到 Excel 文件（支持列格式配置）
     * 
     * @param outputStream 输出流
     * @param dataList 数据列表
     * @param headers 表头数组
     * @param sheetName 工作表名称
     * @param columnFormats 列格式配置 Map，key 为列名（表头），value 为格式配置
     */
    public void exportExcel(OutputStream outputStream, List<Map<String, Object>> dataList, 
                           String[] headers, String sheetName, Map<String, ColumnFormat> columnFormats) {
        ExcelUtil.exportToStream(outputStream, dataList, headers, sheetName, columnFormats);
    }
    
    /**
     * 导出数据到 Excel 文件（使用 Map 的 key 作为表头）
     * 
     * @param outputStream 输出流
     * @param dataList 数据列表
     * @param sheetName 工作表名称
     */
    public void exportExcel(OutputStream outputStream, List<Map<String, Object>> dataList, String sheetName) {
        if (dataList == null || dataList.isEmpty()) {
            throw new IllegalArgumentException("数据列表不能为空");
        }
        
        // 从第一个 Map 中提取所有 key 作为表头
        String[] headers = dataList.get(0).keySet().toArray(new String[0]);
        ExcelUtil.exportToStream(outputStream, dataList, headers, sheetName);
    }
    
    /**
     * 导出数据到 Excel 文件（使用 Map 的 key 作为表头，支持列格式配置）
     * 
     * @param outputStream 输出流
     * @param dataList 数据列表
     * @param sheetName 工作表名称
     * @param columnFormats 列格式配置 Map，key 为列名（表头），value 为格式配置
     */
    public void exportExcel(OutputStream outputStream, List<Map<String, Object>> dataList, String sheetName,
                           Map<String, ColumnFormat> columnFormats) {
        if (dataList == null || dataList.isEmpty()) {
            throw new IllegalArgumentException("数据列表不能为空");
        }
        
        // 从第一个 Map 中提取所有 key 作为表头
        String[] headers = dataList.get(0).keySet().toArray(new String[0]);
        ExcelUtil.exportToStream(outputStream, dataList, headers, sheetName, columnFormats);
    }
    
    /**
     * 获取 Excel 文件的工作表名称列表
     * 
     * @param file 上传的文件
     * @return 工作表名称列表
     */
    public List<String> getSheetNames(MultipartFile file) {
        try {
            String tempFilePath = saveTempFile(file);
            try {
                return ExcelUtil.getSheetNames(tempFilePath);
            } finally {
                deleteTempFile(tempFilePath);
            }
        } catch (IOException e) {
            logger.error("获取工作表名称失败", e);
            throw new RuntimeException("获取工作表名称失败: " + e.getMessage(), e);
        }
    }
    
    /**
     * 获取工作表行数
     * 
     * @param file 上传的文件
     * @param sheetIndex 工作表索引
     * @return 行数
     */
    public int getRowCount(MultipartFile file, int sheetIndex) {
        try {
            String tempFilePath = saveTempFile(file);
            try {
                return ExcelUtil.getRowCount(tempFilePath, sheetIndex);
            } finally {
                deleteTempFile(tempFilePath);
            }
        } catch (IOException e) {
            logger.error("获取行数失败", e);
            throw new RuntimeException("获取行数失败: " + e.getMessage(), e);
        }
    }
    
    /**
     * 保存临时文件
     */
    private String saveTempFile(MultipartFile file) throws IOException {
        String tempDir = System.getProperty("java.io.tmpdir");
        String fileName = "excel_" + System.currentTimeMillis() + "_" + file.getOriginalFilename();
        String tempFilePath = tempDir + File.separator + fileName;
        
        File tempFile = new File(tempFilePath);
        file.transferTo(tempFile);
        
        return tempFilePath;
    }
    
    /**
     * 删除临时文件
     */
    private void deleteTempFile(String filePath) {
        try {
            File file = new File(filePath);
            if (file.exists()) {
                file.delete();
            }
        } catch (Exception e) {
            logger.warn("删除临时文件失败: {}", filePath, e);
        }
    }
}
