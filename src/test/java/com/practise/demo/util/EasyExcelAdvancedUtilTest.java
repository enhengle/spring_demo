package com.practise.demo.util;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.File;
import java.nio.file.Path;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

/**
 * EasyExcel 高级功能测试用例
 * 
 * @author system
 */
@DisplayName("EasyExcel 高级功能测试")
class EasyExcelAdvancedUtilTest {
    
    @TempDir
    Path tempDir;
    
    /**
     * 测试数据类
     */
    public static class TestData {
        @com.alibaba.excel.annotation.ExcelProperty("姓名")
        private String name;
        
        @com.alibaba.excel.annotation.ExcelProperty("年龄")
        private Integer age;
        
        @com.alibaba.excel.annotation.ExcelProperty("金额")
        private Double amount;
        
        public TestData() {}
        
        public TestData(String name, Integer age, Double amount) {
            this.name = name;
            this.age = age;
            this.amount = amount;
        }
        
        // Getters and Setters
        public String getName() { return name; }
        public void setName(String name) { this.name = name; }
        public Integer getAge() { return age; }
        public void setAge(Integer age) { this.age = age; }
        public Double getAmount() { return amount; }
        public void setAmount(Double amount) { this.amount = amount; }
    }
    
    @Test
    @DisplayName("测试生成多Sheet Excel文件")
    void testWriteMultiSheetExcel() {
        File outputFile = tempDir.resolve("multi_sheet.xlsx").toFile();
        
        // 准备数据
        List<TestData> sheet1Data = Arrays.asList(
            new TestData("张三", 25, 1000.50),
            new TestData("李四", 30, 2000.75)
        );
        
        List<TestData> sheet2Data = Arrays.asList(
            new TestData("王五", 28, 1500.25),
            new TestData("赵六", 35, 3000.00)
        );
        
        Map<String, EasyExcelAdvancedUtil.SheetData<TestData>> sheetDataMap = new LinkedHashMap<>();
        sheetDataMap.put("Sheet1", new EasyExcelAdvancedUtil.SheetData<>(TestData.class, sheet1Data));
        sheetDataMap.put("Sheet2", new EasyExcelAdvancedUtil.SheetData<>(TestData.class, sheet2Data));
        
        // 生成多Sheet文件
        EasyExcelAdvancedUtil.writeMultiSheetExcel(outputFile.getAbsolutePath(), sheetDataMap);
        
        // 验证文件存在
        assertTrue(outputFile.exists(), "文件应该存在");
        assertTrue(outputFile.length() > 0, "文件应该有内容");
    }
    
    @Test
    @DisplayName("测试生成多Sheet Excel文件（使用Map）")
    void testWriteMultiSheetExcelWithMap() {
        File outputFile = tempDir.resolve("multi_sheet_map.xlsx").toFile();
        
        // 准备表头
        List<List<String>> head1 = Arrays.asList(
            Arrays.asList("产品名称"),
            Arrays.asList("销量"),
            Arrays.asList("金额")
        );
        
        List<List<String>> head2 = Arrays.asList(
            Arrays.asList("地区"),
            Arrays.asList("销售额"),
            Arrays.asList("增长率")
        );
        
        // 准备数据
        List<List<Object>> data1 = Arrays.asList(
            Arrays.asList("产品A", 100, 10000),
            Arrays.asList("产品B", 200, 20000)
        );
        
        List<List<Object>> data2 = Arrays.asList(
            Arrays.asList("北京", 50000, "10%"),
            Arrays.asList("上海", 60000, "15%")
        );
        
        Map<String, EasyExcelAdvancedUtil.MapSheetData> sheetDataMap = new LinkedHashMap<>();
        sheetDataMap.put("产品统计", new EasyExcelAdvancedUtil.MapSheetData(head1, data1));
        sheetDataMap.put("地区统计", new EasyExcelAdvancedUtil.MapSheetData(head2, data2));
        
        // 生成多Sheet文件
        EasyExcelAdvancedUtil.writeMultiSheetExcelWithMap(outputFile.getAbsolutePath(), sheetDataMap);
        
        // 验证文件存在
        assertTrue(outputFile.exists(), "文件应该存在");
    }
    
    @Test
    @DisplayName("测试写入Excel并设置样式")
    void testWriteExcelWithStyle() {
        File outputFile = tempDir.resolve("styled.xlsx").toFile();
        
        List<TestData> dataList = Arrays.asList(
            new TestData("张三", 25, 1000.50),
            new TestData("李四", 30, 2000.75)
        );
        
        // 使用表头样式
        com.alibaba.excel.write.style.HorizontalCellStyleStrategy styleStrategy = 
            EasyExcelAdvancedUtil.createHeaderStyle();
        
        EasyExcelAdvancedUtil.writeExcelWithStyle(
            outputFile.getAbsolutePath(), 
            TestData.class, 
            dataList, 
            styleStrategy
        );
        
        // 验证文件存在
        assertTrue(outputFile.exists(), "文件应该存在");
    }
    
    @Test
    @DisplayName("测试写入Excel并设置列宽")
    void testWriteExcelWithColumnWidth() {
        File outputFile = tempDir.resolve("column_width.xlsx").toFile();
        
        List<TestData> dataList = Arrays.asList(
            new TestData("张三", 25, 1000.50),
            new TestData("李四", 30, 2000.75)
        );
        
        // 设置列宽：姓名15，年龄10，金额20
        int[] columnWidths = {15, 10, 20};
        
        EasyExcelAdvancedUtil.writeExcelWithColumnWidth(
            outputFile.getAbsolutePath(),
            TestData.class,
            dataList,
            columnWidths
        );
        
        // 验证文件存在
        assertTrue(outputFile.exists(), "文件应该存在");
    }
    
    @Test
    @DisplayName("测试创建强调样式")
    void testCreateEmphasisStyle() {
        com.alibaba.excel.write.style.HorizontalCellStyleStrategy styleStrategy = 
            EasyExcelAdvancedUtil.createEmphasisStyle();
        
        assertNotNull(styleStrategy, "样式策略应该不为空");
    }
    
    @Test
    @DisplayName("测试创建数字格式样式")
    void testCreateNumberFormatStyle() {
        com.alibaba.excel.write.style.HorizontalCellStyleStrategy styleStrategy = 
            EasyExcelAdvancedUtil.createNumberFormatStyle("#,##0.00");
        
        assertNotNull(styleStrategy, "样式策略应该不为空");
    }
}
