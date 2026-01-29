package com.practise.demo.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.List;

/**
 * RDF-File 组件包装类
 * 封装 rdf-file 组件的文件切割和合并功能
 * 
 * 注意：这是一个包装类，实际使用时需要替换为真实的 rdf-file 组件 API
 * 
 * @author system
 */
public class RdfFileWrapper {
    
    private static final Logger logger = LoggerFactory.getLogger(RdfFileWrapper.class);
    
    /**
     * 切割文件（按大小）
     * 
     * @param inputFile 输入文件
     * @param outputDir 输出目录
     * @param maxSizeBytes 每个文件的最大大小（字节）
     * @return 生成的文件路径列表
     */
    public static List<String> splitBySize(File inputFile, File outputDir, long maxSizeBytes) {
        List<String> outputFiles = new ArrayList<>();
        
        try {
            if (!inputFile.exists()) {
                throw new IOException("输入文件不存在: " + inputFile.getAbsolutePath());
            }
            
            if (!outputDir.exists()) {
                outputDir.mkdirs();
            }
            
            long fileSize = inputFile.length();
            if (fileSize <= maxSizeBytes) {
                // 文件小于等于最大大小，直接复制
                String outputPath = new File(outputDir, inputFile.getName()).getAbsolutePath();
                Files.copy(inputFile.toPath(), new File(outputPath).toPath(), StandardCopyOption.REPLACE_EXISTING);
                outputFiles.add(outputPath);
                return outputFiles;
            }
            
            // 计算需要分割的文件数量
            String baseName = getBaseName(inputFile.getName());
            String extension = getExtension(inputFile.getName());
            
            // 使用 rdf-file 组件进行切割
            // TODO: 替换为实际的 rdf-file 组件 API 调用
            // 示例：RdfFile.split(inputFile, outputDir, maxSizeBytes);
            
            // 临时实现：实际切割文件
            try (java.io.BufferedReader reader = new java.io.BufferedReader(
                    new java.io.InputStreamReader(
                            new java.io.FileInputStream(inputFile), StandardCharsets.UTF_8))) {
                
                int fileIndex = 1;
                java.io.OutputStreamWriter currentWriter = null;
                long currentSize = 0;
                String line;
                
                while ((line = reader.readLine()) != null) {
                    // 计算当前行的大小（包括换行符）
                    byte[] lineBytes = (line + System.lineSeparator()).getBytes(StandardCharsets.UTF_8);
                    long lineSize = lineBytes.length;
                    
                    // 如果当前文件大小超过限制，创建新文件
                    if (currentWriter == null || currentSize + lineSize > maxSizeBytes) {
                        // 关闭当前文件
                        if (currentWriter != null) {
                            currentWriter.close();
                        }
                        
                        // 创建新文件
                        String partFileName = String.format("%s_part%03d%s", baseName, fileIndex, extension);
                        File partFile = new File(outputDir, partFileName);
                        currentWriter = new java.io.OutputStreamWriter(
                                new java.io.FileOutputStream(partFile), StandardCharsets.UTF_8);
                        outputFiles.add(partFile.getAbsolutePath());
                        currentSize = 0;
                        fileIndex++;
                    }
                    
                    // 写入当前行
                    currentWriter.write(line);
                    currentWriter.write(System.lineSeparator());
                    currentSize += lineSize;
                }
                
                // 关闭最后一个文件
                if (currentWriter != null) {
                    currentWriter.close();
                }
            }
            
            logger.info("文件切割完成: {} -> {} 个文件", inputFile.getName(), outputFiles.size());
            return outputFiles;
            
        } catch (Exception e) {
            logger.error("切割文件失败: {}", inputFile.getAbsolutePath(), e);
            throw new RuntimeException("切割文件失败: " + e.getMessage(), e);
        }
    }
    
    /**
     * 切割文件（按行数）
     * 
     * @param inputFile 输入文件
     * @param outputDir 输出目录
     * @param linesPerFile 每个文件的行数
     * @return 生成的文件路径列表
     */
    public static List<String> splitByLines(File inputFile, File outputDir, int linesPerFile) {
        List<String> outputFiles = new ArrayList<>();
        
        try {
            if (!inputFile.exists()) {
                throw new IOException("输入文件不存在: " + inputFile.getAbsolutePath());
            }
            
            if (!outputDir.exists()) {
                outputDir.mkdirs();
            }
            
            // 使用 rdf-file 组件进行切割
            // TODO: 替换为实际的 rdf-file 组件 API 调用
            // 示例：RdfFile.splitByLines(inputFile, outputDir, linesPerFile);
            
            String baseName = getBaseName(inputFile.getName());
            String extension = getExtension(inputFile.getName());
            
            // 优化实现：使用大缓冲区流式处理，避免一次性加载全部内容
            try (BufferedReader reader = new BufferedReader(
                    new InputStreamReader(
                            new BufferedInputStream(
                                    new FileInputStream(inputFile), 8192 * 1024), // 8MB 缓冲区
                            StandardCharsets.UTF_8), 
                    8192 * 1024)) {
                
                int fileIndex = 1;
                BufferedWriter currentWriter = null;
                int currentLineCount = 0;
                String line;
                
                while ((line = reader.readLine()) != null) {
                    // 如果当前文件行数达到限制，创建新文件
                    if (currentWriter == null || currentLineCount >= linesPerFile) {
                        // 关闭当前文件
                        if (currentWriter != null) {
                            currentWriter.close();
                        }
                        
                        // 创建新文件（使用大缓冲区）
                        String partFileName = String.format("%s_part%03d%s", baseName, fileIndex, extension);
                        File partFile = new File(outputDir, partFileName);
                        currentWriter = new BufferedWriter(
                                new OutputStreamWriter(
                                        new BufferedOutputStream(
                                                new FileOutputStream(partFile), 8192 * 1024),
                                        StandardCharsets.UTF_8),
                                8192 * 1024);
                        outputFiles.add(partFile.getAbsolutePath());
                        currentLineCount = 0;
                        fileIndex++;
                    }
                    
                    // 写入当前行
                    currentWriter.write(line);
                    currentWriter.newLine();
                    currentLineCount++;
                }
                
                // 关闭最后一个文件
                if (currentWriter != null) {
                    currentWriter.close();
                }
            }
            
            logger.info("文件切割完成: {} -> {} 个文件", inputFile.getName(), outputFiles.size());
            return outputFiles;
            
        } catch (Exception e) {
            logger.error("按行数切割文件失败: {}", inputFile.getAbsolutePath(), e);
            throw new RuntimeException("按行数切割文件失败: " + e.getMessage(), e);
        }
    }
    
    /**
     * 合并文件
     * 
     * @param inputFiles 输入文件列表
     * @param outputFile 输出文件
     * @return 是否成功
     */
    public static boolean mergeFiles(List<File> inputFiles, File outputFile) {
        try {
            if (inputFiles == null || inputFiles.isEmpty()) {
                throw new IllegalArgumentException("输入文件列表不能为空");
            }
            
            File outputDir = outputFile.getParentFile();
            if (outputDir != null && !outputDir.exists()) {
                outputDir.mkdirs();
            }
            
            // 使用 rdf-file 组件进行合并
            // TODO: 替换为实际的 rdf-file 组件 API 调用
            // 示例：RdfFile.merge(inputFiles, outputFile);
            
            // 临时实现：合并文件内容
            List<String> allLines = new ArrayList<>();
            for (File inputFile : inputFiles) {
                if (!inputFile.exists()) {
                    logger.warn("文件不存在，跳过: {}", inputFile.getAbsolutePath());
                    continue;
                }
                
                List<String> lines = Files.readAllLines(inputFile.toPath(), StandardCharsets.UTF_8);
                allLines.addAll(lines);
            }
            
            Files.write(outputFile.toPath(), allLines, StandardCharsets.UTF_8);
            
            logger.info("文件合并完成: {} 个文件 -> {}", inputFiles.size(), outputFile.getName());
            return true;
            
        } catch (Exception e) {
            logger.error("合并文件失败", e);
            throw new RuntimeException("合并文件失败: " + e.getMessage(), e);
        }
    }
    
    /**
     * 获取文件名（不含扩展名）
     */
    private static String getBaseName(String fileName) {
        int lastDot = fileName.lastIndexOf('.');
        return lastDot > 0 ? fileName.substring(0, lastDot) : fileName;
    }
    
    /**
     * 获取文件扩展名
     */
    private static String getExtension(String fileName) {
        int lastDot = fileName.lastIndexOf('.');
        return lastDot > 0 ? fileName.substring(lastDot) : "";
    }
}
