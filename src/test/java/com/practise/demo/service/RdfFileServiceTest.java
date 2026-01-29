package com.practise.demo.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * RDF-File 服务类测试用例
 * 
 * @author system
 */
@SpringBootTest
@DisplayName("RDF-File服务类测试")
class RdfFileServiceTest {
    
    @Autowired
    private RdfFileService rdfFileService;
    
    @TempDir
    Path tempDir;
    
    private MultipartFile testFile;
    
    @BeforeEach
    void setUp() throws IOException {
        // 创建测试文件
        File file = tempDir.resolve("test.txt").toFile();
        try (java.io.OutputStreamWriter writer = new java.io.OutputStreamWriter(
                new java.io.FileOutputStream(file), StandardCharsets.UTF_8)) {
            for (int i = 1; i <= 100; i++) {
                writer.write("Line " + i + "\n");
            }
        }
        
        byte[] content = Files.readAllBytes(file.toPath());
        testFile = new MockMultipartFile(
            "file", 
            "test.txt", 
            "text/plain", 
            content
        );
    }
    
    @Test
    @DisplayName("按大小切割文件")
    void testSplitBySize() {
        List<String> outputFiles = rdfFileService.splitBySize(testFile, 0.01); // 10KB
        
        assertNotNull(outputFiles);
        assertFalse(outputFiles.isEmpty());
        
        // 验证文件存在
        for (String filePath : outputFiles) {
            File file = new File(filePath);
            assertTrue(file.exists(), "文件应该存在: " + filePath);
        }
    }
    
    @Test
    @DisplayName("按行数切割文件")
    void testSplitByLines() {
        List<String> outputFiles = rdfFileService.splitByLines(testFile, 20);
        
        assertNotNull(outputFiles);
        assertEquals(5, outputFiles.size()); // 100行 / 20行 = 5个文件
        
        // 验证文件存在
        for (String filePath : outputFiles) {
            File file = new File(filePath);
            assertTrue(file.exists(), "文件应该存在: " + filePath);
        }
    }
    
    @Test
    @DisplayName("合并文件")
    void testMergeFiles() throws IOException {
        // 创建多个测试文件
        MultipartFile[] files = new MultipartFile[3];
        for (int i = 0; i < 3; i++) {
            File file = tempDir.resolve("part" + i + ".txt").toFile();
            try (java.io.OutputStreamWriter writer = new java.io.OutputStreamWriter(
                    new java.io.FileOutputStream(file), StandardCharsets.UTF_8)) {
                writer.write("Part " + i + " content\n");
            }
            
            byte[] content = Files.readAllBytes(file.toPath());
            files[i] = new MockMultipartFile(
                "files",
                "part" + i + ".txt",
                "text/plain",
                content
            );
        }
        
        String outputPath = rdfFileService.mergeFiles(files, "merged.txt");
        
        assertNotNull(outputPath);
        File mergedFile = new File(outputPath);
        assertTrue(mergedFile.exists());
        
        // 验证合并后的内容
        List<String> lines = Files.readAllLines(mergedFile.toPath(), StandardCharsets.UTF_8);
        assertEquals(3, lines.size());
    }
    
    @Test
    @DisplayName("切割Excel文件（按工作表）")
    void testSplitExcelBySheets() throws IOException {
        // 创建简单的Excel文件内容（这里需要实际的Excel文件）
        // 由于需要真实的Excel文件，这个测试可能需要实际的Excel文件数据
        // 暂时跳过或使用模拟数据
        
        // TODO: 添加Excel文件切割测试
        // 需要创建包含多个工作表的Excel文件
    }
    
    @Test
    @DisplayName("合并Excel文件")
    void testMergeExcelFiles() throws IOException {
        // TODO: 添加Excel文件合并测试
        // 需要创建多个Excel文件进行合并测试
    }
    
    @Test
    @DisplayName("切割空文件")
    void testSplitEmptyFile() throws IOException {
        File emptyFile = tempDir.resolve("empty.txt").toFile();
        emptyFile.createNewFile();
        
        byte[] content = Files.readAllBytes(emptyFile.toPath());
        MultipartFile emptyMultipartFile = new MockMultipartFile(
            "file",
            "empty.txt",
            "text/plain",
            content
        );
        
        // 应该返回一个文件（原文件）
        List<String> outputFiles = rdfFileService.splitBySize(emptyMultipartFile, 1.0);
        assertNotNull(outputFiles);
    }
}
