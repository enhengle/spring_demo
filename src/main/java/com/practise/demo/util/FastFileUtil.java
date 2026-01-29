package com.practise.demo.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.nio.ByteBuffer;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.List;

/**
 * 快速文件处理工具类
 * 使用 NIO、内存映射等技术提升文件处理性能
 * 
 * @author system
 */
public class FastFileUtil {
    
    private static final Logger logger = LoggerFactory.getLogger(FastFileUtil.class);
    
    // 默认缓冲区大小：8MB
    private static final int DEFAULT_BUFFER_SIZE = 8 * 1024 * 1024;
    // 内存映射文件大小限制：100MB（超过此大小使用流式处理）
    private static final long MAPPED_FILE_SIZE_LIMIT = 100 * 1024 * 1024;
    
    /**
     * 快速按大小切割文件（使用 NIO 和内存映射）
     * 
     * @param inputFile 输入文件
     * @param outputDir 输出目录
     * @param maxSizeBytes 每个文件的最大大小（字节）
     * @return 生成的文件路径列表
     */
    public static List<String> fastSplitBySize(File inputFile, File outputDir, long maxSizeBytes) {
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
                Files.copy(inputFile.toPath(), new File(outputPath).toPath(), 
                          java.nio.file.StandardCopyOption.REPLACE_EXISTING);
                outputFiles.add(outputPath);
                return outputFiles;
            }
            
            // 对于大文件，使用内存映射文件
            if (fileSize <= MAPPED_FILE_SIZE_LIMIT) {
                return splitBySizeWithMappedBuffer(inputFile, outputDir, maxSizeBytes);
            } else {
                // 超大文件使用流式处理
                return splitBySizeWithStream(inputFile, outputDir, maxSizeBytes);
            }
            
        } catch (Exception e) {
            logger.error("快速切割文件失败: {}", inputFile.getAbsolutePath(), e);
            throw new RuntimeException("快速切割文件失败: " + e.getMessage(), e);
        }
    }
    
    /**
     * 使用内存映射文件切割（适用于中等大小文件）
     */
    private static List<String> splitBySizeWithMappedBuffer(File inputFile, File outputDir, long maxSizeBytes) 
            throws IOException {
        List<String> outputFiles = new ArrayList<>();
        String baseName = getBaseName(inputFile.getName());
        String extension = getExtension(inputFile.getName());
        
        try (FileChannel inputChannel = FileChannel.open(inputFile.toPath(), StandardOpenOption.READ)) {
            long fileSize = inputFile.length();
            long position = 0;
            int fileIndex = 1;
            
            while (position < fileSize) {
                long remaining = fileSize - position;
                long chunkSize = Math.min(maxSizeBytes, remaining);
                
                // 使用内存映射读取
                MappedByteBuffer buffer = inputChannel.map(
                    FileChannel.MapMode.READ_ONLY, position, chunkSize);
                
                // 创建输出文件
                String partFileName = String.format("%s_part%03d%s", baseName, fileIndex, extension);
                File partFile = new File(outputDir, partFileName);
                
                // 使用 NIO 快速写入
                try (FileChannel outputChannel = FileChannel.open(
                        partFile.toPath(), 
                        StandardOpenOption.CREATE, 
                        StandardOpenOption.WRITE,
                        StandardOpenOption.TRUNCATE_EXISTING)) {
                    outputChannel.write(buffer);
                }
                
                outputFiles.add(partFile.getAbsolutePath());
                position += chunkSize;
                fileIndex++;
            }
        }
        
        logger.info("内存映射切割完成: {} -> {} 个文件", inputFile.getName(), outputFiles.size());
        return outputFiles;
    }
    
    /**
     * 使用流式处理切割（适用于超大文件）
     */
    private static List<String> splitBySizeWithStream(File inputFile, File outputDir, long maxSizeBytes) 
            throws IOException {
        List<String> outputFiles = new ArrayList<>();
        String baseName = getBaseName(inputFile.getName());
        String extension = getExtension(inputFile.getName());
        
        // 使用大缓冲区
        byte[] buffer = new byte[DEFAULT_BUFFER_SIZE];
        
        try (FileInputStream fis = new FileInputStream(inputFile);
             BufferedInputStream bis = new BufferedInputStream(fis, DEFAULT_BUFFER_SIZE)) {
            
            int fileIndex = 1;
            FileOutputStream currentFos = null;
            FileChannel currentOutputChannel = null;
            long currentSize = 0;
            int bytesRead;
            
            try {
                while ((bytesRead = bis.read(buffer)) != -1) {
                    // 如果当前文件大小超过限制，创建新文件
                    if (currentOutputChannel == null || currentSize + bytesRead > maxSizeBytes) {
                        // 关闭当前文件
                        if (currentOutputChannel != null) {
                            currentOutputChannel.close();
                        }
                        if (currentFos != null) {
                            currentFos.close();
                        }
                        
                        // 创建新文件
                        String partFileName = String.format("%s_part%03d%s", baseName, fileIndex, extension);
                        File partFile = new File(outputDir, partFileName);
                        currentFos = new FileOutputStream(partFile);
                        currentOutputChannel = currentFos.getChannel();
                        outputFiles.add(partFile.getAbsolutePath());
                        currentSize = 0;
                        fileIndex++;
                    }
                    
                    // 写入数据
                    ByteBuffer byteBuffer = ByteBuffer.wrap(buffer, 0, bytesRead);
                    currentOutputChannel.write(byteBuffer);
                    currentSize += bytesRead;
                }
            } finally {
                // 关闭最后一个文件
                if (currentOutputChannel != null) {
                    currentOutputChannel.close();
                }
                if (currentFos != null) {
                    currentFos.close();
                }
            }
        }
        
        logger.info("流式切割完成: {} -> {} 个文件", inputFile.getName(), outputFiles.size());
        return outputFiles;
    }
    
    /**
     * 快速按行数切割文件（使用大缓冲区和 NIO）
     * 
     * @param inputFile 输入文件
     * @param outputDir 输出目录
     * @param linesPerFile 每个文件的行数
     * @return 生成的文件路径列表
     */
    public static List<String> fastSplitByLines(File inputFile, File outputDir, int linesPerFile) {
        List<String> outputFiles = new ArrayList<>();
        
        try {
            if (!inputFile.exists()) {
                throw new IOException("输入文件不存在: " + inputFile.getAbsolutePath());
            }
            
            if (!outputDir.exists()) {
                outputDir.mkdirs();
            }
            
            String baseName = getBaseName(inputFile.getName());
            String extension = getExtension(inputFile.getName());
            
            // 使用大缓冲区读取
            try (BufferedReader reader = new BufferedReader(
                    new InputStreamReader(
                            new BufferedInputStream(
                                    new FileInputStream(inputFile), DEFAULT_BUFFER_SIZE), 
                            StandardCharsets.UTF_8), 
                    DEFAULT_BUFFER_SIZE)) {
                
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
                                                new FileOutputStream(partFile), DEFAULT_BUFFER_SIZE),
                                        StandardCharsets.UTF_8),
                                DEFAULT_BUFFER_SIZE);
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
            
            logger.info("快速按行切割完成: {} -> {} 个文件", inputFile.getName(), outputFiles.size());
            return outputFiles;
            
        } catch (Exception e) {
            logger.error("快速按行切割文件失败: {}", inputFile.getAbsolutePath(), e);
            throw new RuntimeException("快速按行切割文件失败: " + e.getMessage(), e);
        }
    }
    
    /**
     * 快速合并文件（使用 NIO）
     * 
     * @param inputFiles 输入文件列表
     * @param outputFile 输出文件
     * @return 是否成功
     */
    public static boolean fastMergeFiles(List<File> inputFiles, File outputFile) {
        try {
            if (inputFiles == null || inputFiles.isEmpty()) {
                throw new IllegalArgumentException("输入文件列表不能为空");
            }
            
            File outputDir = outputFile.getParentFile();
            if (outputDir != null && !outputDir.exists()) {
                outputDir.mkdirs();
            }
            
            // 使用 NIO 快速合并
            try (FileChannel outputChannel = FileChannel.open(
                    outputFile.toPath(),
                    StandardOpenOption.CREATE,
                    StandardOpenOption.WRITE,
                    StandardOpenOption.TRUNCATE_EXISTING)) {
                
                long totalTransferred = 0;
                for (File inputFile : inputFiles) {
                    if (!inputFile.exists()) {
                        logger.warn("文件不存在，跳过: {}", inputFile.getAbsolutePath());
                        continue;
                    }
                    
                    try (FileChannel inputChannel = FileChannel.open(
                            inputFile.toPath(), StandardOpenOption.READ)) {
                        // 使用 transferTo 进行零拷贝传输
                        long transferred = inputChannel.transferTo(0, inputChannel.size(), outputChannel);
                        totalTransferred += transferred;
                    }
                }
                
                logger.info("快速合并完成: {} 个文件 -> {} (总大小: {} bytes)", 
                        inputFiles.size(), outputFile.getName(), totalTransferred);
            }
            
            return true;
            
        } catch (Exception e) {
            logger.error("快速合并文件失败", e);
            throw new RuntimeException("快速合并文件失败: " + e.getMessage(), e);
        }
    }
    
    /**
     * 快速复制文件（使用 NIO transferTo）
     * 
     * @param source 源文件
     * @param target 目标文件
     * @return 是否成功
     */
    public static boolean fastCopyFile(File source, File target) {
        try {
            File targetDir = target.getParentFile();
            if (targetDir != null && !targetDir.exists()) {
                targetDir.mkdirs();
            }
            
            try (FileChannel sourceChannel = FileChannel.open(source.toPath(), StandardOpenOption.READ);
                 FileChannel targetChannel = FileChannel.open(
                         target.toPath(),
                         StandardOpenOption.CREATE,
                         StandardOpenOption.WRITE,
                         StandardOpenOption.TRUNCATE_EXISTING)) {
                
                // 使用 transferTo 进行零拷贝
                sourceChannel.transferTo(0, sourceChannel.size(), targetChannel);
            }
            
            return true;
            
        } catch (Exception e) {
            logger.error("快速复制文件失败", e);
            throw new RuntimeException("快速复制文件失败: " + e.getMessage(), e);
        }
    }
    
    /**
     * 快速读取文件内容（使用内存映射，适用于大文件）
     * 
     * @param filePath 文件路径
     * @return 文件内容字节数组
     */
    public static byte[] fastReadFile(String filePath) throws IOException {
        File file = new File(filePath);
        if (!file.exists()) {
            throw new FileNotFoundException("文件不存在: " + filePath);
        }
        
        long fileSize = file.length();
        if (fileSize > Integer.MAX_VALUE) {
            throw new IOException("文件太大，无法一次性读取: " + fileSize);
        }
        
        // 使用内存映射文件读取
        try (FileChannel channel = FileChannel.open(Paths.get(filePath), StandardOpenOption.READ)) {
            MappedByteBuffer buffer = channel.map(FileChannel.MapMode.READ_ONLY, 0, fileSize);
            byte[] bytes = new byte[(int) fileSize];
            buffer.get(bytes);
            return bytes;
        }
    }
    
    /**
     * 快速写入文件（使用 NIO）
     * 
     * @param filePath 文件路径
     * @param content 内容
     * @return 是否成功
     */
    public static boolean fastWriteFile(String filePath, byte[] content) {
        try {
            File file = new File(filePath);
            File parentDir = file.getParentFile();
            if (parentDir != null && !parentDir.exists()) {
                parentDir.mkdirs();
            }
            
            try (FileChannel channel = FileChannel.open(
                    Paths.get(filePath),
                    StandardOpenOption.CREATE,
                    StandardOpenOption.WRITE,
                    StandardOpenOption.TRUNCATE_EXISTING)) {
                
                ByteBuffer buffer = ByteBuffer.wrap(content);
                channel.write(buffer);
            }
            
            return true;
            
        } catch (Exception e) {
            logger.error("快速写入文件失败", e);
            throw new RuntimeException("快速写入文件失败: " + e.getMessage(), e);
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
