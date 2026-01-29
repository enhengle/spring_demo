package com.practise.demo.util;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Path;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

/**
 * ExcelDataWarehouseUtil 数据仓库工具类测试
 * 测试数据仓库和业务场景中常用的Excel操作方法
 * 
 * @author system
 */
@DisplayName("Excel数据仓库工具类测试")
class ExcelDataWarehouseUtilTest {
    
    /**
     * 临时目录，用于存放测试文件
     */
    @TempDir
    Path tempDir;
    
    /**
     * 测试创建数据字典表
     * 验证能正确创建包含字段说明、类型、示例等信息的字典表
     */
    @Test
    @DisplayName("测试创建数据字典表")
    void testCreateDataDictionary() throws IOException {
        try (XSSFWorkbook workbook = new XSSFWorkbook()) {
            Sheet sheet = workbook.createSheet("数据字典");
            
            // 准备字典数据
            Map<String, Map<String, String>> dictionaryData = new LinkedHashMap<>();
            
            Map<String, String> field1 = new HashMap<>();
            field1.put("说明", "用户ID");
            field1.put("类型", "Long");
            field1.put("必填", "是");
            field1.put("示例", "1001");
            field1.put("备注", "主键");
            dictionaryData.put("userId", field1);
            
            Map<String, String> field2 = new HashMap<>();
            field2.put("说明", "用户名");
            field2.put("类型", "String");
            field2.put("必填", "是");
            field2.put("示例", "zhangsan");
            field2.put("备注", "唯一");
            dictionaryData.put("userName", field2);
            
            // 创建数据字典
            ExcelDataWarehouseUtil.createDataDictionary(sheet, dictionaryData, 0);
            
            // 验证表头存在
            assertNotNull(sheet.getRow(0), "表头行应存在");
            assertEquals("字段名", sheet.getRow(0).getCell(0).getStringCellValue(), "第一个表头应为'字段名'");
            
            // 验证数据行存在
            assertNotNull(sheet.getRow(1), "第一行数据应存在");
            assertEquals("userId", sheet.getRow(1).getCell(0).getStringCellValue(), "第一行字段名应为userId");
            
            // 保存文件
            String filePath = tempDir.resolve("data_dictionary.xlsx").toString();
            try (FileOutputStream fos = new FileOutputStream(filePath)) {
                workbook.write(fos);
            }
            
            assertTrue(new java.io.File(filePath).exists(), "文件应该被创建");
        }
    }
    
    /**
     * 测试创建数据质量报告
     * 验证能正确创建包含数据量、空值统计等信息的质量报告
     */
    @Test
    @DisplayName("测试创建数据质量报告")
    void testCreateDataQualityReport() throws IOException {
        try (XSSFWorkbook workbook = new XSSFWorkbook()) {
            Sheet sheet = workbook.createSheet("数据质量报告");
            
            // 准备质量数据
            Map<String, Object> qualityData = new HashMap<>();
            qualityData.put("totalCount", 1000);
            qualityData.put("validCount", 950);
            qualityData.put("nullCount", 50);
            qualityData.put("duplicateCount", 10);
            qualityData.put("completeness", "95%");
            qualityData.put("accuracy", "98%");
            
            // 创建质量报告
            ExcelDataWarehouseUtil.createDataQualityReport(sheet, qualityData, 0);
            
            // 验证标题存在
            assertNotNull(sheet.getRow(0), "标题行应存在");
            assertEquals("数据质量报告", sheet.getRow(0).getCell(0).getStringCellValue(), "标题应为'数据质量报告'");
            
            // 保存文件
            String filePath = tempDir.resolve("data_quality_report.xlsx").toString();
            try (FileOutputStream fos = new FileOutputStream(filePath)) {
                workbook.write(fos);
            }
            
            assertTrue(new java.io.File(filePath).exists(), "文件应该被创建");
        }
    }
    
    /**
     * 测试创建对比报表
     * 验证能正确创建两个时间段的数据对比报表
     */
    @Test
    @DisplayName("测试创建对比报表")
    void testCreateComparisonReport() throws IOException {
        try (XSSFWorkbook workbook = new XSSFWorkbook()) {
            Sheet sheet = workbook.createSheet("对比报表");
            
            // 准备第一期数据
            List<Map<String, Object>> period1Data = new ArrayList<>();
            for (int i = 0; i < 3; i++) {
                Map<String, Object> row = new LinkedHashMap<>();
                row.put("销售额", 10000 + i * 1000);
                row.put("利润", 2000 + i * 200);
                period1Data.add(row);
            }
            
            // 准备第二期数据
            List<Map<String, Object>> period2Data = new ArrayList<>();
            for (int i = 0; i < 3; i++) {
                Map<String, Object> row = new LinkedHashMap<>();
                row.put("销售额", 12000 + i * 1000);
                row.put("利润", 2500 + i * 200);
                period2Data.add(row);
            }
            
            String[] headers = {"销售额", "利润"};
            
            // 创建对比报表
            ExcelDataWarehouseUtil.createComparisonReport(sheet, period1Data, period2Data,
                    "2024Q1", "2024Q2", headers, 0);
            
            // 验证表头存在
            assertNotNull(sheet.getRow(0), "表头行应存在");
            assertNotNull(sheet.getRow(1), "子表头行应存在");
            
            // 保存文件
            String filePath = tempDir.resolve("comparison_report.xlsx").toString();
            try (FileOutputStream fos = new FileOutputStream(filePath)) {
                workbook.write(fos);
            }
            
            assertTrue(new java.io.File(filePath).exists(), "文件应该被创建");
        }
    }
    
    /**
     * 测试创建趋势分析报表
     * 验证能正确创建时间序列数据的趋势分析报表
     */
    @Test
    @DisplayName("测试创建趋势分析报表")
    void testCreateTrendAnalysisReport() throws IOException {
        try (XSSFWorkbook workbook = new XSSFWorkbook()) {
            Sheet sheet = workbook.createSheet("趋势分析");
            
            // 准备时间序列数据
            Map<String, Map<String, Object>> timeSeriesData = new LinkedHashMap<>();
            
            Map<String, Object> data1 = new HashMap<>();
            data1.put("销售额", 10000);
            data1.put("订单数", 100);
            timeSeriesData.put("2024-01", data1);
            
            Map<String, Object> data2 = new HashMap<>();
            data2.put("销售额", 12000);
            data2.put("订单数", 120);
            timeSeriesData.put("2024-02", data2);
            
            Map<String, Object> data3 = new HashMap<>();
            data3.put("销售额", 15000);
            data3.put("订单数", 150);
            timeSeriesData.put("2024-03", data3);
            
            String[] headers = {"销售额", "订单数"};
            
            // 创建趋势分析报表
            ExcelDataWarehouseUtil.createTrendAnalysisReport(sheet, timeSeriesData, headers, 0);
            
            // 验证表头存在
            assertNotNull(sheet.getRow(0), "表头行应存在");
            assertEquals("时间", sheet.getRow(0).getCell(0).getStringCellValue(), "第一列应为'时间'");
            
            // 验证数据按时间排序
            assertNotNull(sheet.getRow(1), "第一行数据应存在");
            assertEquals("2024-01", sheet.getRow(1).getCell(0).getStringCellValue(), "第一行时间应为2024-01");
            
            // 保存文件
            String filePath = tempDir.resolve("trend_analysis_report.xlsx").toString();
            try (FileOutputStream fos = new FileOutputStream(filePath)) {
                workbook.write(fos);
            }
            
            assertTrue(new java.io.File(filePath).exists(), "文件应该被创建");
        }
    }
    
    /**
     * 测试创建数据导出模板
     * 验证能正确创建包含表头、示例数据、验证规则的导出模板
     */
    @Test
    @DisplayName("测试创建数据导出模板")
    void testCreateExportTemplate() throws IOException {
        try (XSSFWorkbook workbook = new XSSFWorkbook()) {
            Sheet sheet = workbook.createSheet("导出模板");
            
            // 准备数据
            String[] headers = {"姓名", "年龄", "邮箱"};
            Map<String, Object> sampleData = new HashMap<>();
            sampleData.put("姓名", "张三");
            sampleData.put("年龄", 25);
            sampleData.put("邮箱", "zhangsan@example.com");
            
            Map<String, String> validationRules = new HashMap<>();
            validationRules.put("姓名", "必填，长度2-20字符");
            validationRules.put("年龄", "必填，18-65之间的整数");
            validationRules.put("邮箱", "必填，有效的邮箱格式");
            
            // 创建导出模板
            ExcelDataWarehouseUtil.createExportTemplate(sheet, headers, sampleData, validationRules, 0);
            
            // 验证说明行存在
            assertNotNull(sheet.getRow(0), "说明行应存在");
            
            // 验证表头存在
            assertNotNull(sheet.getRow(2), "表头行应存在");
            assertEquals("姓名", sheet.getRow(2).getCell(0).getStringCellValue(), "第一个表头应为'姓名'");
            
            // 验证示例数据存在
            assertNotNull(sheet.getRow(3), "示例数据行应存在");
            
            // 保存文件
            String filePath = tempDir.resolve("export_template.xlsx").toString();
            try (FileOutputStream fos = new FileOutputStream(filePath)) {
                workbook.write(fos);
            }
            
            assertTrue(new java.io.File(filePath).exists(), "文件应该被创建");
        }
    }
    
    /**
     * 测试创建数据统计报表
     * 验证能正确创建包含总计、平均值、最大值、最小值等统计信息的报表
     */
    @Test
    @DisplayName("测试创建数据统计报表")
    void testCreateStatisticsReport() throws IOException {
        try (XSSFWorkbook workbook = new XSSFWorkbook()) {
            Sheet sheet = workbook.createSheet("统计报表");
            
            // 准备数据
            List<Map<String, Object>> dataList = new ArrayList<>();
            for (int i = 0; i < 10; i++) {
                Map<String, Object> row = new LinkedHashMap<>();
                row.put("产品", "产品" + (i + 1));
                row.put("销量", (i + 1) * 10);
                row.put("金额", (i + 1) * 1000);
                dataList.add(row);
            }
            
            String[] headers = {"产品", "销量", "金额"};
            int[] numericColumns = {1, 2}; // 销量和金额列
            
            // 创建统计报表
            ExcelDataWarehouseUtil.createStatisticsReport(sheet, dataList, headers, numericColumns, 0);
            
            // 验证表头存在
            assertNotNull(sheet.getRow(0), "表头行应存在");
            
            // 验证数据行存在
            assertNotNull(sheet.getRow(1), "第一行数据应存在");
            
            // 验证统计行存在
            int statsStartRow = 1 + dataList.size() + 1;
            assertNotNull(sheet.getRow(statsStartRow), "统计行应存在");
            assertEquals("总计", sheet.getRow(statsStartRow).getCell(0).getStringCellValue(), "第一个统计标签应为'总计'");
            
            // 验证统计公式存在
            Cell sumCell = sheet.getRow(statsStartRow).getCell(1);
            assertNotNull(sumCell.getCellFormula(), "统计单元格应有公式");
            assertTrue(sumCell.getCellFormula().contains("SUM"), "统计公式应包含SUM");
            
            // 保存文件
            String filePath = tempDir.resolve("statistics_report.xlsx").toString();
            try (FileOutputStream fos = new FileOutputStream(filePath)) {
                workbook.write(fos);
            }
            
            assertTrue(new java.io.File(filePath).exists(), "文件应该被创建");
        }
    }
}
