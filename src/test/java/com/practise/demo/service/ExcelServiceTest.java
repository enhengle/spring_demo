package com.practise.demo.service;

import com.practise.demo.util.CellFormatBuilder;
import com.practise.demo.util.ColumnFormat;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileInputStream;
import java.nio.file.Path;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

/**
 * ExcelService 服务层测试类
 * 测试 Excel 服务的各种功能
 * 
 * @author system
 */
@DisplayName("Excel服务层测试")
class ExcelServiceTest {
    
    /**
     * Excel服务实例
     */
    private ExcelService excelService;
    
    /**
     * 临时目录，用于存放测试文件
     */
    @TempDir
    Path tempDir;
    
    /**
     * 测试前初始化
     */
    @BeforeEach
    void setUp() {
        excelService = new ExcelService();
    }
    
    /**
     * 测试读取Excel文件（包含表头）
     * 验证能正确读取上传的Excel文件并返回数据列表
     */
    @Test
    @DisplayName("测试读取Excel文件（包含表头）")
    void testReadExcelWithHeader() throws Exception {
        // 创建测试Excel文件
        String filePath = createTestExcelFile();
        
        // 创建MultipartFile
        MultipartFile file = createMultipartFile(filePath);
        
        // 读取Excel
        List<Map<String, Object>> result = excelService.readExcel(file, true);
        
        // 验证结果
        assertNotNull(result, "读取结果不应为空");
        assertTrue(result.size() > 0, "应该有数据");
        
        Map<String, Object> firstRow = result.get(0);
        assertTrue(firstRow.containsKey("序号"), "应包含序号列");
        assertTrue(firstRow.containsKey("姓名"), "应包含姓名列");
    }
    
    /**
     * 测试读取Excel文件（不包含表头）
     * 验证hasHeader=false时能正确读取数据
     */
    @Test
    @DisplayName("测试读取Excel文件（不包含表头）")
    void testReadExcelWithoutHeader() throws Exception {
        String filePath = createTestExcelFile();
        MultipartFile file = createMultipartFile(filePath);
        
        List<Map<String, Object>> result = excelService.readExcel(file, false);
        
        assertNotNull(result, "读取结果不应为空");
    }
    
    /**
     * 测试读取指定工作表
     * 验证能正确读取指定索引的工作表
     */
    @Test
    @DisplayName("测试读取指定工作表")
    void testReadExcelWithSheetIndex() throws Exception {
        String filePath = createTestExcelFile();
        MultipartFile file = createMultipartFile(filePath);
        
        List<Map<String, Object>> result = excelService.readExcel(file, true, 0);
        
        assertNotNull(result, "读取结果不应为空");
    }
    
    /**
     * 测试获取工作表名称列表
     * 验证能正确获取Excel文件中的所有工作表名称
     */
    @Test
    @DisplayName("测试获取工作表名称列表")
    void testGetSheetNames() throws Exception {
        String filePath = createTestExcelFile();
        MultipartFile file = createMultipartFile(filePath);
        
        List<String> sheetNames = excelService.getSheetNames(file);
        
        assertNotNull(sheetNames, "工作表名称列表不应为空");
        assertTrue(sheetNames.size() > 0, "应该至少有一个工作表");
    }
    
    /**
     * 测试获取工作表行数
     * 验证能正确统计工作表中的行数
     */
    @Test
    @DisplayName("测试获取工作表行数")
    void testGetRowCount() throws Exception {
        String filePath = createTestExcelFile();
        MultipartFile file = createMultipartFile(filePath);
        
        int rowCount = excelService.getRowCount(file, 0);
        
        assertTrue(rowCount > 0, "行数应该大于0");
    }
    
    /**
     * 测试导出Excel到输出流（不带格式）
     * 验证能正确导出数据到输出流
     */
    @Test
    @DisplayName("测试导出Excel到输出流（不带格式）")
    void testExportExcelWithoutFormats() throws Exception {
        // 准备数据
        List<Map<String, Object>> dataList = new ArrayList<>();
        for (int i = 1; i <= 3; i++) {
            Map<String, Object> row = new LinkedHashMap<>();
            row.put("序号", i);
            row.put("姓名", "用户" + i);
            dataList.add(row);
        }
        
        String[] headers = {"序号", "姓名"};
        String outputPath = tempDir.resolve("export_test.xlsx").toString();
        
        // 导出到文件流
        try (java.io.FileOutputStream fos = new java.io.FileOutputStream(outputPath)) {
            excelService.exportExcel(fos, dataList, headers, "测试数据");
        }
        
        // 验证文件存在
        File file = new File(outputPath);
        assertTrue(file.exists(), "导出文件应该被创建");
    }
    
    /**
     * 测试导出Excel到输出流（带格式配置）
     * 验证能正确应用格式配置并导出
     */
    @Test
    @DisplayName("测试导出Excel到输出流（带格式配置）")
    void testExportExcelWithFormats() throws Exception {
        // 准备数据
        List<Map<String, Object>> dataList = new ArrayList<>();
        Map<String, Object> row = new LinkedHashMap<>();
        row.put("姓名", "张三");
        row.put("年龄", 25);
        row.put("工资", 10000.50);
        dataList.add(row);
        
        String[] headers = {"姓名", "年龄", "工资"};
        
        // 创建格式配置
        Map<String, ColumnFormat> formats = new HashMap<>();
        formats.put("姓名", CellFormatBuilder.textCenter());
        formats.put("年龄", CellFormatBuilder.integer());
        formats.put("工资", CellFormatBuilder.currencyCNY());
        
        String outputPath = tempDir.resolve("export_with_formats.xlsx").toString();
        
        // 导出到文件流（带格式）
        try (java.io.FileOutputStream fos = new java.io.FileOutputStream(outputPath)) {
            excelService.exportExcel(fos, dataList, headers, "格式测试", formats);
        }
        
        // 验证文件存在
        File file = new File(outputPath);
        assertTrue(file.exists(), "导出文件应该被创建");
    }
    
    /**
     * 测试导出Excel（使用Map的key作为表头）
     * 验证不提供表头时能使用Map的key作为表头
     */
    @Test
    @DisplayName("测试导出Excel（使用Map的key作为表头）")
    void testExportExcelWithMapKeys() throws Exception {
        // 准备数据
        List<Map<String, Object>> dataList = new ArrayList<>();
        Map<String, Object> row = new LinkedHashMap<>();
        row.put("列1", "值1");
        row.put("列2", "值2");
        dataList.add(row);
        
        String outputPath = tempDir.resolve("export_map_keys.xlsx").toString();
        
        // 导出（不提供表头）
        try (java.io.FileOutputStream fos = new java.io.FileOutputStream(outputPath)) {
            excelService.exportExcel(fos, dataList, "测试");
        }
        
        // 验证文件存在
        File file = new File(outputPath);
        assertTrue(file.exists(), "导出文件应该被创建");
    }
    
    /**
     * 测试导出空数据列表
     * 验证导出空数据列表时会抛出异常
     */
    @Test
    @DisplayName("测试导出空数据列表")
    void testExportEmptyData() {
        List<Map<String, Object>> emptyList = new ArrayList<>();
        String outputPath = tempDir.resolve("export_empty.xlsx").toString();
        
        assertThrows(IllegalArgumentException.class, () -> {
            try (java.io.FileOutputStream fos = new java.io.FileOutputStream(outputPath)) {
                excelService.exportExcel(fos, emptyList, "空数据");
            }
        }, "导出空数据列表应抛出异常");
    }
    
    /**
     * 测试读取空文件
     * 验证读取空文件时会抛出异常
     */
    @Test
    @DisplayName("测试读取空文件")
    void testReadEmptyFile() {
        // 创建空文件
        String emptyFilePath = tempDir.resolve("empty.xlsx").toString();
        File emptyFile = new File(emptyFilePath);
        try {
            emptyFile.createNewFile();
        } catch (Exception e) {
            // ignore
        }
        
        MultipartFile file = createMultipartFile(emptyFilePath);
        
        assertThrows(Exception.class, () -> {
            excelService.readExcel(file, true);
        }, "读取空文件应抛出异常");
    }
    
    /**
     * 创建测试用的Excel文件
     * 
     * @return 文件路径
     */
    private String createTestExcelFile() throws Exception {
        List<Map<String, Object>> dataList = new ArrayList<>();
        for (int i = 1; i <= 3; i++) {
            Map<String, Object> row = new LinkedHashMap<>();
            row.put("序号", i);
            row.put("姓名", "用户" + i);
            row.put("年龄", 20 + i);
            dataList.add(row);
        }
        
        String[] headers = {"序号", "姓名", "年龄"};
        String filePath = tempDir.resolve("test_data.xlsx").toString();
        
        com.practise.demo.util.ExcelUtil.writeExcel(filePath, dataList, headers, "测试数据");
        
        return filePath;
    }
    
    /**
     * 创建MultipartFile对象
     * 
     * @param filePath 文件路径
     * @return MultipartFile对象
     */
    private MultipartFile createMultipartFile(String filePath) {
        try {
            File file = new File(filePath);
            FileInputStream fis = new FileInputStream(file);
            byte[] content = new byte[(int) file.length()];
            fis.read(content);
            fis.close();
            
            return new MockMultipartFile(
                "file",
                file.getName(),
                "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet",
                content
            );
        } catch (Exception e) {
            throw new RuntimeException("创建MultipartFile失败", e);
        }
    }
}
