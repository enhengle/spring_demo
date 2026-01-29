package com.practise.demo.service;

import com.practise.demo.util.FastExcelUtil;
import com.practise.demo.util.FastFileUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.List;

/**
 * 快速文件处理服务类
 * 使用高性能组件和方法进行文件处理
 * 
 * @author system
 */
@Service
public class FastFileService {
    
    private static final Logger logger = LoggerFactory.getLogger(FastFileService.class);
    
    private static final String TEMP_DIR = System.getProperty("java.io.tmpdir") + File.separator + "fast_file";
    
    /**
     * 快速按大小切割文件
     * 
     * @param file 上传的文件
     * @param maxSizeMB 每个文件的最大大小（MB）
     * @return 生成的文件路径列表
     */
    public List<String> fastSplitBySize(MultipartFile file, double maxSizeMB) {
        try {
            File inputFile = saveTempFile(file);
            File outputDir = new File(TEMP_DIR, "split_" + System.currentTimeMillis());
            
            long maxSizeBytes = (long) (maxSizeMB * 1024 * 1024);
            List<String> outputFiles = FastFileUtil.fastSplitBySize(inputFile, outputDir, maxSizeBytes);
            
            return outputFiles;
            
        } catch (IOException e) {
            logger.error("快速切割文件失败", e);
            throw new RuntimeException("快速切割文件失败: " + e.getMessage(), e);
        }
    }
    
    /**
     * 快速按行数切割文件
     * 
     * @param file 上传的文件
     * @param linesPerFile 每个文件的行数
     * @return 生成的文件路径列表
     */
    public List<String> fastSplitByLines(MultipartFile file, int linesPerFile) {
        try {
            File inputFile = saveTempFile(file);
            File outputDir = new File(TEMP_DIR, "split_" + System.currentTimeMillis());
            
            List<String> outputFiles = FastFileUtil.fastSplitByLines(inputFile, outputDir, linesPerFile);
            
            return outputFiles;
            
        } catch (IOException e) {
            logger.error("快速按行切割文件失败", e);
            throw new RuntimeException("快速按行切割文件失败: " + e.getMessage(), e);
        }
    }
    
    /**
     * 快速合并文件
     * 
     * @param files 要合并的文件列表
     * @param outputFileName 输出文件名
     * @return 合并后的文件路径
     */
    public String fastMergeFiles(MultipartFile[] files, String outputFileName) {
        try {
            List<File> tempFiles = new java.util.ArrayList<>();
            
            // 保存所有临时文件
            for (MultipartFile file : files) {
                File tempFile = saveTempFile(file);
                tempFiles.add(tempFile);
            }
            
            // 快速合并文件
            File outputDir = new File(TEMP_DIR, "merge_" + System.currentTimeMillis());
            outputDir.mkdirs();
            File outputFile = new File(outputDir, outputFileName);
            
            FastFileUtil.fastMergeFiles(tempFiles, outputFile);
            
            return outputFile.getAbsolutePath();
            
        } catch (IOException e) {
            logger.error("快速合并文件失败", e);
            throw new RuntimeException("快速合并文件失败: " + e.getMessage(), e);
        }
    }
    
    /**
     * 快速读取 Excel 文件（使用 EasyExcel）
     * 
     * @param file Excel 文件
     * @param listener 数据监听器
     */
    public void fastReadExcel(MultipartFile file, 
                              com.alibaba.excel.read.listener.ReadListener<?> listener) {
        try {
            FastExcelUtil.fastReadExcel(file.getInputStream(), 1, listener);
        } catch (IOException e) {
            logger.error("快速读取 Excel 文件失败", e);
            throw new RuntimeException("快速读取 Excel 文件失败: " + e.getMessage(), e);
        }
    }
    
    /**
     * 快速写入 Excel 文件（使用 EasyExcel）
     * 
     * @param outputStream 输出流
     * @param headClass 表头类
     * @param dataList 数据列表
     */
    public <T> void fastWriteExcel(java.io.OutputStream outputStream, Class<T> headClass, List<T> dataList) {
        FastExcelUtil.fastWriteExcel(outputStream, headClass, dataList);
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
}
