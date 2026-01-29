package com.practise.demo.service;

import com.practise.demo.util.ExcelUtil;
import com.practise.demo.util.RdfFileWrapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * RDF-File 文件操作服务类
 * 使用 rdf-file 组件进行文件切割和合并
 * 
 * @author system
 */
@Service
public class RdfFileService {
    
    private static final Logger logger = LoggerFactory.getLogger(RdfFileService.class);
    
    private static final String TEMP_DIR = System.getProperty("java.io.tmpdir") + File.separator + "rdf_file";
    
    /**
     * 按大小切割文件
     * 
     * @param file 上传的文件
     * @param maxSizeMB 每个文件的最大大小（MB）
     * @return 生成的文件路径列表
     */
    public List<String> splitBySize(MultipartFile file, double maxSizeMB) {
        try {
            File inputFile = saveTempFile(file);
            File outputDir = new File(TEMP_DIR, "split_" + System.currentTimeMillis());
            
            long maxSizeBytes = (long) (maxSizeMB * 1024 * 1024);
            List<String> outputFiles = RdfFileWrapper.splitBySize(inputFile, outputDir, maxSizeBytes);
            
            return outputFiles;
            
        } catch (IOException e) {
            logger.error("按大小切割文件失败", e);
            throw new RuntimeException("按大小切割文件失败: " + e.getMessage(), e);
        }
    }
    
    /**
     * 按行数切割文件
     * 
     * @param file 上传的文件
     * @param linesPerFile 每个文件的行数
     * @return 生成的文件路径列表
     */
    public List<String> splitByLines(MultipartFile file, int linesPerFile) {
        try {
            File inputFile = saveTempFile(file);
            File outputDir = new File(TEMP_DIR, "split_" + System.currentTimeMillis());
            
            List<String> outputFiles = RdfFileWrapper.splitByLines(inputFile, outputDir, linesPerFile);
            
            return outputFiles;
            
        } catch (IOException e) {
            logger.error("按行数切割文件失败", e);
            throw new RuntimeException("按行数切割文件失败: " + e.getMessage(), e);
        }
    }
    
    /**
     * 合并文件
     * 
     * @param files 要合并的文件列表
     * @param outputFileName 输出文件名
     * @return 合并后的文件路径
     */
    public String mergeFiles(MultipartFile[] files, String outputFileName) {
        try {
            List<File> tempFiles = new ArrayList<>();
            
            // 保存所有临时文件
            for (MultipartFile file : files) {
                File tempFile = saveTempFile(file);
                tempFiles.add(tempFile);
            }
            
            // 合并文件
            File outputDir = new File(TEMP_DIR, "merge_" + System.currentTimeMillis());
            outputDir.mkdirs();
            File outputFile = new File(outputDir, outputFileName);
            
            RdfFileWrapper.mergeFiles(tempFiles, outputFile);
            
            return outputFile.getAbsolutePath();
            
        } catch (IOException e) {
            logger.error("合并文件失败", e);
            throw new RuntimeException("合并文件失败: " + e.getMessage(), e);
        }
    }
    
    /**
     * 切割 Excel 文件（按工作表）
     * 
     * @param file Excel 文件
     * @return 每个工作表生成一个文件
     */
    public List<String> splitExcelBySheets(MultipartFile file) {
        try {
            String tempFilePath = saveTempFile(file).getAbsolutePath();
            List<String> sheetNames = ExcelUtil.getSheetNames(tempFilePath);
            List<String> outputFiles = new ArrayList<>();
            
            File outputDir = new File(TEMP_DIR, "excel_split_" + System.currentTimeMillis());
            outputDir.mkdirs();
            
            String baseName = getBaseName(file.getOriginalFilename());
            
            for (int i = 0; i < sheetNames.size(); i++) {
                String sheetName = sheetNames.get(i);
                List<Map<String, Object>> sheetData = ExcelUtil.readExcel(tempFilePath, true, i);
                
                // 生成新文件
                String outputFileName = String.format("%s_sheet%d_%s.xlsx", baseName, i + 1, sheetName);
                String outputPath = new File(outputDir, outputFileName).getAbsolutePath();
                
                // 写入单个工作表的数据
                String[] headers = sheetData.isEmpty() ? new String[0] : 
                    sheetData.get(0).keySet().toArray(new String[0]);
                ExcelUtil.writeExcel(outputPath, sheetData, headers, sheetName);
                
                outputFiles.add(outputPath);
            }
            
            logger.info("Excel 文件切割完成: {} 个工作表", sheetNames.size());
            return outputFiles;
            
        } catch (Exception e) {
            logger.error("切割 Excel 文件失败", e);
            throw new RuntimeException("切割 Excel 文件失败: " + e.getMessage(), e);
        }
    }
    
    /**
     * 合并多个 Excel 文件
     * 
     * @param files Excel 文件列表
     * @param outputFileName 输出文件名
     * @param sheetName 工作表名称
     * @return 合并后的文件路径
     */
    public String mergeExcelFiles(MultipartFile[] files, String outputFileName, String sheetName) {
        try {
            List<Map<String, Object>> allData = new ArrayList<>();
            String[] headers = null;
            
            // 读取所有文件的数据
            for (MultipartFile file : files) {
                String tempFilePath = saveTempFile(file).getAbsolutePath();
                List<Map<String, Object>> data = ExcelUtil.readExcel(tempFilePath, true);
                
                if (!data.isEmpty()) {
                    if (headers == null) {
                        headers = data.get(0).keySet().toArray(new String[0]);
                    }
                    allData.addAll(data);
                }
            }
            
            // 写入合并后的文件
            File outputDir = new File(TEMP_DIR, "excel_merge_" + System.currentTimeMillis());
            outputDir.mkdirs();
            String outputPath = new File(outputDir, outputFileName).getAbsolutePath();
            
            if (headers != null) {
                ExcelUtil.writeExcel(outputPath, allData, headers, sheetName != null ? sheetName : "Sheet1");
            }
            
            logger.info("Excel 文件合并完成: {} 个文件 -> {} 行数据", files.length, allData.size());
            return outputPath;
            
        } catch (Exception e) {
            logger.error("合并 Excel 文件失败", e);
            throw new RuntimeException("合并 Excel 文件失败: " + e.getMessage(), e);
        }
    }
    
    /**
     * 保存临时文件
     */
    private File saveTempFile(MultipartFile file) throws IOException {
        File tempDir = new File(TEMP_DIR);
        if (!tempDir.exists()) {
            tempDir.mkdirs();
        }
        
        File tempFile = new File(tempDir, file.getOriginalFilename());
        file.transferTo(tempFile);
        
        return tempFile;
    }
    
    /**
     * 获取文件名（不含扩展名）
     */
    private String getBaseName(String fileName) {
        int lastDot = fileName.lastIndexOf('.');
        return lastDot > 0 ? fileName.substring(0, lastDot) : fileName;
    }
}
