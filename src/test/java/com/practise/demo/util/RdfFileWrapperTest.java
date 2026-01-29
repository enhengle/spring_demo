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
 * RDF-File 组件包装类测试用例
 * 
 * @author system
 */
@DisplayName("RDF-File组件包装类测试")
class RdfFileWrapperTest {
    
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
            for (int i = 1; i <= 100; i++) {
                writer.write("Line " + i + "\n");
            }
        }
        
        outputDir = tempDir.resolve("output").toFile();
    }
    
    @Test
    @DisplayName("按大小切割文件")
    void testSplitBySize() {
        // 每个文件最大 500 字节
        List<String> outputFiles = RdfFileWrapper.splitBySize(testFile, outputDir, 500);
        
        assertNotNull(outputFiles);
        assertFalse(outputFiles.isEmpty());
        assertTrue(outputFiles.size() > 1);
        
        // 验证每个文件都存在
        for (String filePath : outputFiles) {
            File file = new File(filePath);
            assertTrue(file.exists(), "文件应该存在: " + filePath);
        }
    }
    
    @Test
    @DisplayName("按行数切割文件")
    void testSplitByLines() {
        // 每个文件 20 行
        List<String> outputFiles = RdfFileWrapper.splitByLines(testFile, outputDir, 20);
        
        assertNotNull(outputFiles);
        assertEquals(5, outputFiles.size()); // 100行 / 20行 = 5个文件
        
        // 验证每个文件都存在
        for (String filePath : outputFiles) {
            File file = new File(filePath);
            assertTrue(file.exists(), "文件应该存在: " + filePath);
            
            // 验证文件内容行数
            try {
                List<String> lines = Files.readAllLines(file.toPath(), StandardCharsets.UTF_8);
                assertTrue(lines.size() <= 20, "每个文件应该不超过20行");
            } catch (IOException e) {
                fail("读取文件失败: " + e.getMessage());
            }
        }
    }
    
    @Test
    @DisplayName("合并文件")
    void testMergeFiles() throws IOException {
        // 先切割文件
        List<String> splitFiles = RdfFileWrapper.splitByLines(testFile, outputDir, 25);
        assertEquals(4, splitFiles.size());
        
        // 准备文件列表
        List<File> filesToMerge = new ArrayList<>();
        for (String filePath : splitFiles) {
            filesToMerge.add(new File(filePath));
        }
        
        // 合并文件
        File mergedFile = tempDir.resolve("merged.txt").toFile();
        boolean success = RdfFileWrapper.mergeFiles(filesToMerge, mergedFile);
        
        assertTrue(success);
        assertTrue(mergedFile.exists());
        
        // 验证合并后的行数
        List<String> mergedLines = Files.readAllLines(mergedFile.toPath(), StandardCharsets.UTF_8);
        assertEquals(100, mergedLines.size());
    }
    
    @Test
    @DisplayName("切割小文件（小于最大大小）")
    void testSplitSmallFile() {
        // 创建一个很小的文件
        File smallFile = tempDir.resolve("small.txt").toFile();
        try (java.io.OutputStreamWriter writer = new java.io.OutputStreamWriter(
                new java.io.FileOutputStream(smallFile), StandardCharsets.UTF_8)) {
            writer.write("Small file content\n");
        } catch (IOException e) {
            fail("创建小文件失败: " + e.getMessage());
        }
        
        // 按大尺寸切割，应该只生成一个文件
        List<String> outputFiles = RdfFileWrapper.splitBySize(smallFile, outputDir, 1024 * 1024);
        
        assertNotNull(outputFiles);
        assertEquals(1, outputFiles.size());
    }
    
    @Test
    @DisplayName("合并空文件列表")
    void testMergeEmptyFiles() {
        List<File> emptyList = new ArrayList<>();
        File outputFile = tempDir.resolve("output.txt").toFile();
        
        assertThrows(RuntimeException.class, () -> {
            RdfFileWrapper.mergeFiles(emptyList, outputFile);
        });
    }
    
    @Test
    @DisplayName("切割不存在的文件")
    void testSplitNonExistentFile() {
        File nonExistentFile = tempDir.resolve("nonexistent.txt").toFile();
        
        assertThrows(RuntimeException.class, () -> {
            RdfFileWrapper.splitBySize(nonExistentFile, outputDir, 100);
        });
    }
}
