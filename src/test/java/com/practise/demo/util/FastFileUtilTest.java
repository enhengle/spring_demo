package com.practise.demo.util;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * 快速文件处理工具类测试用例
 * 
 * @author system
 */
@DisplayName("快速文件处理工具类测试")
class FastFileUtilTest {
    
    @TempDir
    Path tempDir;
    
    private File testFile;
    private File outputDir;
    
    @BeforeEach
    void setUp() throws IOException {
        // 创建测试文件
        testFile = tempDir.resolve("test.txt").toFile();
        try (java.io.OutputStreamWriter writer = new java.io.OutputStreamWriter(
                new java.io.FileOutputStream(testFile), StandardCharsets.UTF_8)) {
            for (int i = 1; i <= 1000; i++) {
                writer.write("Line " + i + " - This is a test line with some content\n");
            }
        }
        
        outputDir = tempDir.resolve("output").toFile();
    }
    
    @Test
    @DisplayName("快速按大小切割文件")
    void testFastSplitBySize() {
        // 每个文件最大 10KB
        List<String> outputFiles = FastFileUtil.fastSplitBySize(testFile, outputDir, 10 * 1024);
        
        assertNotNull(outputFiles);
        assertFalse(outputFiles.isEmpty());
        assertTrue(outputFiles.size() > 1);
        
        // 验证每个文件都存在
        for (String filePath : outputFiles) {
            File file = new File(filePath);
            assertTrue(file.exists(), "文件应该存在: " + filePath);
            assertTrue(file.length() > 0, "文件应该有内容: " + filePath);
        }
    }
    
    @Test
    @DisplayName("快速按行数切割文件")
    void testFastSplitByLines() {
        // 每个文件 100 行
        List<String> outputFiles = FastFileUtil.fastSplitByLines(testFile, outputDir, 100);
        
        assertNotNull(outputFiles);
        assertEquals(10, outputFiles.size()); // 1000行 / 100行 = 10个文件
        
        // 验证每个文件都存在
        for (String filePath : outputFiles) {
            File file = new File(filePath);
            assertTrue(file.exists(), "文件应该存在: " + filePath);
        }
    }
    
    @Test
    @DisplayName("快速合并文件")
    void testFastMergeFiles() throws IOException {
        // 先切割文件
        List<String> splitFiles = FastFileUtil.fastSplitByLines(testFile, outputDir, 200);
        assertEquals(5, splitFiles.size());
        
        // 准备文件列表
        List<File> filesToMerge = new ArrayList<>();
        for (String filePath : splitFiles) {
            filesToMerge.add(new File(filePath));
        }
        
        // 快速合并文件
        File mergedFile = tempDir.resolve("merged.txt").toFile();
        boolean success = FastFileUtil.fastMergeFiles(filesToMerge, mergedFile);
        
        assertTrue(success);
        assertTrue(mergedFile.exists());
        
        // 验证合并后的行数
        List<String> mergedLines = Files.readAllLines(mergedFile.toPath(), StandardCharsets.UTF_8);
        assertEquals(1000, mergedLines.size());
    }
    
    @Test
    @DisplayName("快速复制文件")
    void testFastCopyFile() {
        File targetFile = tempDir.resolve("copied.txt").toFile();
        boolean success = FastFileUtil.fastCopyFile(testFile, targetFile);
        
        assertTrue(success);
        assertTrue(targetFile.exists());
        assertEquals(testFile.length(), targetFile.length());
    }
    
    @Test
    @DisplayName("快速读取文件")
    void testFastReadFile() throws IOException {
        byte[] content = FastFileUtil.fastReadFile(testFile.getAbsolutePath());
        
        assertNotNull(content);
        assertTrue(content.length > 0);
        assertEquals(testFile.length(), content.length);
    }
    
    @Test
    @DisplayName("快速写入文件")
    void testFastWriteFile() {
        String testContent = "This is test content\nLine 2\nLine 3";
        byte[] content = testContent.getBytes(StandardCharsets.UTF_8);
        
        File targetFile = tempDir.resolve("written.txt").toFile();
        boolean success = FastFileUtil.fastWriteFile(targetFile.getAbsolutePath(), content);
        
        assertTrue(success);
        assertTrue(targetFile.exists());
        
        // 验证内容
        try {
            byte[] readContent = Files.readAllBytes(targetFile.toPath());
            assertArrayEquals(content, readContent);
        } catch (IOException e) {
            fail("读取文件失败: " + e.getMessage());
        }
    }
    
    @Test
    @DisplayName("性能对比测试 - 切割大文件")
    void testPerformanceComparison() throws IOException {
        // 创建较大的测试文件（1MB）
        File largeFile = tempDir.resolve("large_test.txt").toFile();
        try (java.io.OutputStreamWriter writer = new java.io.OutputStreamWriter(
                new java.io.FileOutputStream(largeFile), StandardCharsets.UTF_8)) {
            StringBuilder sb = new StringBuilder();
            for (int j = 0; j < 100; j++) {
                sb.append("x");
            }
            String padding = sb.toString();
            for (int i = 1; i <= 10000; i++) {
                writer.write("Line " + i + " - " + padding + "\n");
            }
        }
        
        // 测试快速切割
        long startTime = System.currentTimeMillis();
        List<String> files = FastFileUtil.fastSplitBySize(largeFile, outputDir, 100 * 1024); // 100KB
        long endTime = System.currentTimeMillis();
        
        long duration = endTime - startTime;
        logger.info("快速切割 {} 文件耗时: {} ms", largeFile.length(), duration);
        
        assertNotNull(files);
        assertFalse(files.isEmpty());
        assertTrue(duration < 5000, "切割应该在5秒内完成");
    }
    
    private static final org.slf4j.Logger logger = org.slf4j.LoggerFactory.getLogger(FastFileUtilTest.class);
}
