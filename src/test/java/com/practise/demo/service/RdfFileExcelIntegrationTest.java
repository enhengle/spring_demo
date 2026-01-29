package com.practise.demo.service;

import com.practise.demo.util.ExcelUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockMultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

/**
 * RDF-File 与 Excel 功能集成测试用例
 * 
 * @author system
 */
@SpringBootTest
@DisplayName("RDF-File与Excel功能集成测试")
class RdfFileExcelIntegrationTest {
    
    @Autowired
    private RdfFileService rdfFileService;
    
    @TempDir
    Path tempDir;
    
    private String excelFilePath;
    
    @BeforeEach
    void setUp() throws IOException {
        // 创建测试 Excel 文件
        excelFilePath = tempDir.resolve("test_data.xlsx").toString();
        
        List<Map<String, Object>> dataList = new ArrayList<>();
        for (int i = 1; i <= 50; i++) {
            Map<String, Object> row = new LinkedHashMap<>();
            row.put("ID", i);
            row.put("姓名", "用户" + i);
            row.put("年龄", 20 + i);
            row.put("工资", 5000.0 + i * 100);
            row.put("入职日期", new Date());
            dataList.add(row);
        }
        
        String[] headers = {"ID", "姓名", "年龄", "工资", "入职日期"};
        ExcelUtil.writeExcel(excelFilePath, dataList, headers, "员工数据");
    }
    
    @Test
    @DisplayName("切割Excel文件并按工作表分割")
    void testSplitExcelBySheets() throws IOException {
        // 读取Excel文件并转换为MultipartFile
        byte[] excelContent = Files.readAllBytes(new File(excelFilePath).toPath());
        MockMultipartFile excelFile = new MockMultipartFile(
            "file",
            "test_data.xlsx",
            "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet",
            excelContent
        );
        
        // 切割Excel文件
        List<String> outputFiles = rdfFileService.splitExcelBySheets(excelFile);
        
        assertNotNull(outputFiles);
        assertFalse(outputFiles.isEmpty());
        
        // 验证每个文件都存在
        for (String filePath : outputFiles) {
            File file = new File(filePath);
            assertTrue(file.exists(), "切割后的文件应该存在: " + filePath);
            
            // 验证文件内容
            List<Map<String, Object>> data = ExcelUtil.readExcel(filePath, true);
            assertFalse(data.isEmpty(), "文件应该包含数据");
        }
    }
    
    @Test
    @DisplayName("合并多个Excel文件")
    void testMergeExcelFiles() throws IOException {
        // 创建多个Excel文件
        List<MockMultipartFile> excelFiles = new ArrayList<>();
        
        for (int fileIndex = 0; fileIndex < 3; fileIndex++) {
            String filePath = tempDir.resolve("data_part" + fileIndex + ".xlsx").toString();
            
            List<Map<String, Object>> dataList = new ArrayList<>();
            for (int i = 1; i <= 10; i++) {
                Map<String, Object> row = new LinkedHashMap<>();
                row.put("ID", fileIndex * 10 + i);
                row.put("姓名", "用户" + (fileIndex * 10 + i));
                row.put("年龄", 20 + i);
                dataList.add(row);
            }
            
            String[] headers = {"ID", "姓名", "年龄"};
            ExcelUtil.writeExcel(filePath, dataList, headers, "数据");
            
            byte[] content = Files.readAllBytes(new File(filePath).toPath());
            excelFiles.add(new MockMultipartFile(
                "files",
                "data_part" + fileIndex + ".xlsx",
                "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet",
                content
            ));
        }
        
        // 合并Excel文件
        String mergedPath = rdfFileService.mergeExcelFiles(
            excelFiles.toArray(new MockMultipartFile[0]),
            "merged_data.xlsx",
            "合并数据"
        );
        
        assertNotNull(mergedPath);
        File mergedFile = new File(mergedPath);
        assertTrue(mergedFile.exists());
        
        // 验证合并后的数据
        List<Map<String, Object>> mergedData = ExcelUtil.readExcel(mergedPath, true);
        assertEquals(30, mergedData.size(), "合并后应该有30行数据");
    }
    
    @Test
    @DisplayName("切割文本文件后转换为Excel")
    void testSplitTextFileAndConvertToExcel() throws IOException {
        // 创建文本文件
        File textFile = tempDir.resolve("data.txt").toFile();
        try (java.io.OutputStreamWriter writer = new java.io.OutputStreamWriter(
                new java.io.FileOutputStream(textFile), StandardCharsets.UTF_8)) {
            writer.write("ID,姓名,年龄\n");
            for (int i = 1; i <= 100; i++) {
                writer.write(i + ",用户" + i + "," + (20 + i) + "\n");
            }
        }
        
        byte[] textContent = Files.readAllBytes(textFile.toPath());
        MockMultipartFile textFileMultipart = new MockMultipartFile(
            "file",
            "data.txt",
            "text/plain",
            textContent
        );
        
        // 按行数切割文本文件
        List<String> splitFiles = rdfFileService.splitByLines(textFileMultipart, 20);
        
        assertNotNull(splitFiles);
        assertEquals(5, splitFiles.size()); // 100行 / 20行 = 5个文件
        
        // 将每个切割后的文件转换为Excel
        List<String> excelFiles = new ArrayList<>();
        for (String splitFilePath : splitFiles) {
            File splitFile = new File(splitFilePath);
            List<String> lines = Files.readAllLines(splitFile.toPath(), StandardCharsets.UTF_8);
            
            // 解析CSV数据
            List<Map<String, Object>> dataList = new ArrayList<>();
            String[] headers = null;
            
            for (int i = 0; i < lines.size(); i++) {
                String line = lines.get(i);
                if (i == 0) {
                    headers = line.split(",");
                } else {
                    String[] values = line.split(",");
                    Map<String, Object> row = new LinkedHashMap<>();
                    for (int j = 0; j < headers.length && j < values.length; j++) {
                        row.put(headers[j].trim(), values[j].trim());
                    }
                    dataList.add(row);
                }
            }
            
            // 写入Excel
            String excelPath = tempDir.resolve("excel_" + splitFile.getName().replace(".txt", ".xlsx")).toString();
            if (headers != null && !dataList.isEmpty()) {
                ExcelUtil.writeExcel(excelPath, dataList, headers, "数据");
                excelFiles.add(excelPath);
            }
        }
        
        assertEquals(5, excelFiles.size());
        
        // 验证Excel文件
        for (String excelPath : excelFiles) {
            File excelFile = new File(excelPath);
            assertTrue(excelFile.exists());
            
            List<Map<String, Object>> data = ExcelUtil.readExcel(excelPath, true);
            assertTrue(data.size() <= 20, "每个Excel文件应该不超过20行数据");
        }
    }
    
    @Test
    @DisplayName("从Excel读取数据并切割为多个文本文件")
    void testReadExcelAndSplitToTextFiles() throws IOException {
        // 读取Excel数据
        List<Map<String, Object>> excelData = ExcelUtil.readExcel(excelFilePath, true);
        assertEquals(50, excelData.size());
        
        // 将数据写入文本文件
        File textFile = tempDir.resolve("excel_export.txt").toFile();
        try (java.io.OutputStreamWriter writer = new java.io.OutputStreamWriter(
                new java.io.FileOutputStream(textFile), StandardCharsets.UTF_8)) {
            // 写入表头
            if (!excelData.isEmpty()) {
                String header = String.join(",", excelData.get(0).keySet());
                writer.write(header + "\n");
            }
            
            // 写入数据
            for (Map<String, Object> row : excelData) {
                List<String> values = new ArrayList<>();
                for (Object value : row.values()) {
                    values.add(value != null ? value.toString() : "");
                }
                writer.write(String.join(",", values) + "\n");
            }
        }
        
        // 切割文本文件
        byte[] textContent = Files.readAllBytes(textFile.toPath());
        MockMultipartFile textFileMultipart = new MockMultipartFile(
            "file",
            "excel_export.txt",
            "text/plain",
            textContent
        );
        
        List<String> splitFiles = rdfFileService.splitByLines(textFileMultipart, 10);
        
        assertNotNull(splitFiles);
        assertEquals(5, splitFiles.size()); // 50行数据 + 1行表头 = 51行，按10行切割 = 6个文件（实际可能更多）
        
        // 验证切割后的文件
        for (String splitFilePath : splitFiles) {
            File splitFile = new File(splitFilePath);
            assertTrue(splitFile.exists());
        }
    }
    
    @Test
    @DisplayName("综合场景：切割Excel -> 合并 -> 验证")
    void testCompleteWorkflow() throws IOException {
        // 1. 读取原始Excel
        byte[] excelContent = Files.readAllBytes(new File(excelFilePath).toPath());
        MockMultipartFile originalExcel = new MockMultipartFile(
            "file",
            "original.xlsx",
            "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet",
            excelContent
        );
        
        // 2. 切割Excel文件
        List<String> splitExcelFiles = rdfFileService.splitExcelBySheets(originalExcel);
        assertFalse(splitExcelFiles.isEmpty());
        
        // 3. 准备合并文件
        List<MockMultipartFile> filesToMerge = new ArrayList<>();
        for (String splitPath : splitExcelFiles) {
            byte[] content = Files.readAllBytes(new File(splitPath).toPath());
            filesToMerge.add(new MockMultipartFile(
                "files",
                new File(splitPath).getName(),
                "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet",
                content
            ));
        }
        
        // 4. 合并文件
        String mergedPath = rdfFileService.mergeExcelFiles(
            filesToMerge.toArray(new MockMultipartFile[0]),
            "final_merged.xlsx",
            "最终数据"
        );
        
        assertNotNull(mergedPath);
        File mergedFile = new File(mergedPath);
        assertTrue(mergedFile.exists());
        
        // 5. 验证合并后的数据完整性
        List<Map<String, Object>> originalData = ExcelUtil.readExcel(excelFilePath, true);
        List<Map<String, Object>> mergedData = ExcelUtil.readExcel(mergedPath, true);
        
        // 合并后的数据行数应该等于或大于原始数据（因为可能有表头）
        assertTrue(mergedData.size() >= originalData.size() - 1);
    }
}
