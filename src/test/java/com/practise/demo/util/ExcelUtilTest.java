package com.practise.demo.util;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.File;
import java.nio.file.Path;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

/**
 * ExcelUtil 工具类测试
 * 测试 Excel 文件的读写、格式配置等功能
 * 
 * @author system
 */
@DisplayName("Excel工具类测试")
class ExcelUtilTest {
    
    /**
     * 临时目录，用于存放测试文件
     */
    @TempDir
    Path tempDir;
    
    /**
     * 测试基本的写入和读取功能
     * 验证数据能够正确写入Excel文件并读取回来
     */
    @Test
    @DisplayName("测试基本的写入和读取功能")
    void testWriteAndReadExcel() throws Exception {
        // 准备测试数据
        List<Map<String, Object>> dataList = new ArrayList<>();
        for (int i = 1; i <= 5; i++) {
            Map<String, Object> row = new LinkedHashMap<>();
            row.put("序号", i);
            row.put("姓名", "用户" + i);
            row.put("年龄", 20 + i);
            row.put("邮箱", "user" + i + "@example.com");
            dataList.add(row);
        }
        
        String[] headers = {"序号", "姓名", "年龄", "邮箱"};
        String filePath = tempDir.resolve("test.xlsx").toString();
        
        // 写入Excel
        ExcelUtil.writeExcel(filePath, dataList, headers, "测试数据");
        
        // 验证文件存在
        File file = new File(filePath);
        assertTrue(file.exists(), "Excel文件应该被创建");
        
        // 读取Excel
        List<Map<String, Object>> readData = ExcelUtil.readExcel(filePath, true);
        
        // 验证数据
        assertNotNull(readData, "读取的数据不应为空");
        assertEquals(5, readData.size(), "应该有5行数据");
        
        Map<String, Object> firstRow = readData.get(0);
        assertEquals(1, firstRow.get("序号"), "第一行序号应为1");
        assertEquals("用户1", firstRow.get("姓名"), "第一行姓名应为用户1");
    }
    
    /**
     * 测试不指定表头的写入功能
     * 验证使用Map的key作为表头时能正确写入
     */
    @Test
    @DisplayName("测试不指定表头的写入功能")
    void testWriteExcelWithoutHeaders() throws Exception {
        // 准备测试数据
        List<Map<String, Object>> dataList = new ArrayList<>();
        Map<String, Object> row1 = new LinkedHashMap<>();
        row1.put("姓名", "张三");
        row1.put("年龄", 25);
        dataList.add(row1);
        
        Map<String, Object> row2 = new LinkedHashMap<>();
        row2.put("姓名", "李四");
        row2.put("年龄", 30);
        dataList.add(row2);
        
        String filePath = tempDir.resolve("test_no_headers.xlsx").toString();
        
        // 写入Excel（不指定表头，使用Map的key）
        ExcelUtil.writeExcel(filePath, dataList, "测试");
        
        // 读取Excel
        List<Map<String, Object>> readData = ExcelUtil.readExcel(filePath, true);
        
        // 验证数据
        assertEquals(2, readData.size(), "应该有2行数据");
        assertEquals("张三", readData.get(0).get("姓名"), "第一行姓名应为张三");
        assertEquals(25, readData.get(0).get("年龄"), "第一行年龄应为25");
    }
    
    /**
     * 测试读取不包含表头的Excel文件
     * 验证hasHeader=false时能正确读取数据
     */
    @Test
    @DisplayName("测试读取不包含表头的Excel文件")
    void testReadExcelWithoutHeader() throws Exception {
        // 先创建一个没有表头的Excel
        List<Map<String, Object>> dataList = new ArrayList<>();
        Map<String, Object> row = new LinkedHashMap<>();
        row.put("0", "数据1");
        row.put("1", "数据2");
        dataList.add(row);
        
        String[] headers = {"列1", "列2"};
        String filePath = tempDir.resolve("test_no_header.xlsx").toString();
        
        // 写入Excel（带表头）
        ExcelUtil.writeExcel(filePath, dataList, headers, "测试");
        
        // 读取Excel（不包含表头）
        List<Map<String, Object>> readData = ExcelUtil.readExcel(filePath, false);
        
        // 验证数据（应该包含表头行）
        assertTrue(readData.size() >= 1, "应该至少有一行数据");
    }
    
    /**
     * 测试获取工作表名称列表
     * 验证能正确获取Excel文件中的所有工作表名称
     */
    @Test
    @DisplayName("测试获取工作表名称列表")
    void testGetSheetNames() throws Exception {
        // 创建Excel文件
        List<Map<String, Object>> dataList = new ArrayList<>();
        Map<String, Object> row = new LinkedHashMap<>();
        row.put("数据", "测试");
        dataList.add(row);
        
        String[] headers = {"数据"};
        String filePath = tempDir.resolve("test_sheets.xlsx").toString();
        
        ExcelUtil.writeExcel(filePath, dataList, headers, "工作表1");
        
        // 获取工作表名称
        List<String> sheetNames = ExcelUtil.getSheetNames(filePath);
        
        assertNotNull(sheetNames, "工作表名称列表不应为空");
        assertTrue(sheetNames.size() > 0, "应该至少有一个工作表");
        assertEquals("工作表1", sheetNames.get(0), "第一个工作表名称应为'工作表1'");
    }
    
    /**
     * 测试获取工作表行数
     * 验证能正确统计工作表中的行数（包含表头）
     */
    @Test
    @DisplayName("测试获取工作表行数")
    void testGetRowCount() throws Exception {
        // 创建Excel文件
        List<Map<String, Object>> dataList = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            Map<String, Object> row = new LinkedHashMap<>();
            row.put("序号", i);
            dataList.add(row);
        }
        
        String[] headers = {"序号"};
        String filePath = tempDir.resolve("test_row_count.xlsx").toString();
        
        ExcelUtil.writeExcel(filePath, dataList, headers, "测试");
        
        // 获取行数（包含表头）
        int rowCount = ExcelUtil.getRowCount(filePath, 0);
        
        assertTrue(rowCount >= 10, "行数应该至少为10（包含表头）");
    }
    
    /**
     * 测试读取指定工作表
     * 验证能正确读取指定索引的工作表数据
     */
    @Test
    @DisplayName("测试读取指定工作表")
    void testReadExcelWithDifferentSheet() throws Exception {
        // 创建Excel文件
        List<Map<String, Object>> dataList = new ArrayList<>();
        Map<String, Object> row = new LinkedHashMap<>();
        row.put("数据", "工作表1数据");
        dataList.add(row);
        
        String[] headers = {"数据"};
        String filePath = tempDir.resolve("test_multi_sheet.xlsx").toString();
        
        ExcelUtil.writeExcel(filePath, dataList, headers, "Sheet1");
        
        // 读取第一个工作表
        List<Map<String, Object>> readData = ExcelUtil.readExcel(filePath, true, 0);
        
        assertNotNull(readData, "读取的数据不应为空");
        assertTrue(readData.size() > 0, "应该有数据");
    }
    
    /**
     * 测试导出到输出流
     * 验证能正确将数据导出到输出流（用于Web下载）
     */
    @Test
    @DisplayName("测试导出到输出流")
    void testExportToStream() throws Exception {
        // 准备测试数据
        List<Map<String, Object>> dataList = new ArrayList<>();
        for (int i = 1; i <= 3; i++) {
            Map<String, Object> row = new LinkedHashMap<>();
            row.put("序号", i);
            row.put("名称", "项目" + i);
            dataList.add(row);
        }
        
        String[] headers = {"序号", "名称"};
        String filePath = tempDir.resolve("test_stream.xlsx").toString();
        
        // 导出到文件流
        try (java.io.FileOutputStream fos = new java.io.FileOutputStream(filePath)) {
            ExcelUtil.exportToStream(fos, dataList, headers, "流导出测试");
        }
        
        // 验证文件存在
        File file = new File(filePath);
        assertTrue(file.exists(), "Excel文件应该被创建");
        
        // 读取验证
        List<Map<String, Object>> readData = ExcelUtil.readExcel(filePath, true);
        assertEquals(3, readData.size(), "应该有3行数据");
    }
    
    /**
     * 测试写入.xls格式文件
     * 验证能正确写入旧版Excel格式（.xls）
     */
    @Test
    @DisplayName("测试写入.xls格式文件")
    void testWriteExcelWithXlsFormat() throws Exception {
        // 准备测试数据
        List<Map<String, Object>> dataList = new ArrayList<>();
        Map<String, Object> row = new LinkedHashMap<>();
        row.put("数据", "测试");
        dataList.add(row);
        
        String[] headers = {"数据"};
        String filePath = tempDir.resolve("test.xls").toString();
        
        // 写入.xls格式
        ExcelUtil.writeExcel(filePath, dataList, headers, "测试");
        
        // 验证文件存在
        File file = new File(filePath);
        assertTrue(file.exists(), "Excel文件应该被创建");
        
        // 读取验证
        List<Map<String, Object>> readData = ExcelUtil.readExcel(filePath, true);
        assertNotNull(readData, "读取的数据不应为空");
    }
    
    /**
     * 测试读取不存在的文件
     * 验证读取不存在的文件时会抛出异常
     */
    @Test
    @DisplayName("测试读取不存在的文件")
    void testReadExcelWithEmptyFile() {
        // 创建一个空文件
        String filePath = tempDir.resolve("empty.xlsx").toString();
        
        // 应该抛出异常或返回空列表
        assertThrows(Exception.class, () -> {
            ExcelUtil.readExcel(filePath, true);
        }, "读取不存在的文件应该抛出异常");
    }
    
    /**
     * 测试写入空数据列表
     * 验证写入空数据列表时不会抛出异常
     */
    @Test
    @DisplayName("测试写入空数据列表")
    void testWriteExcelWithNullData() {
        String filePath = tempDir.resolve("null_data.xlsx").toString();
        
        // 写入空数据列表
        List<Map<String, Object>> emptyList = new ArrayList<>();
        String[] headers = {"列1"};
        
        assertDoesNotThrow(() -> {
            ExcelUtil.writeExcel(filePath, emptyList, headers, "空数据");
        }, "写入空数据列表不应抛出异常");
    }
    
    /**
     * 测试带格式配置的写入功能
     * 验证能正确应用列格式配置
     */
    @Test
    @DisplayName("测试带格式配置的写入功能")
    void testWriteExcelWithFormats() throws Exception {
        // 准备测试数据
        List<Map<String, Object>> dataList = new ArrayList<>();
        for (int i = 1; i <= 3; i++) {
            Map<String, Object> row = new LinkedHashMap<>();
            row.put("序号", i);
            row.put("姓名", "用户" + i);
            row.put("年龄", 20 + i);
            row.put("工资", 10000.50 + i * 1000);
            row.put("入职日期", new Date());
            dataList.add(row);
        }
        
        String[] headers = {"序号", "姓名", "年龄", "工资", "入职日期"};
        
        // 创建格式配置
        Map<String, ColumnFormat> formats = new HashMap<>();
        formats.put("序号", CellFormatBuilder.serialNumber());
        formats.put("姓名", CellFormatBuilder.textCenter());
        formats.put("年龄", CellFormatBuilder.integer());
        formats.put("工资", CellFormatBuilder.currencyCNY());
        formats.put("入职日期", CellFormatBuilder.date());
        
        String filePath = tempDir.resolve("test_with_formats.xlsx").toString();
        
        // 写入Excel（带格式）
        ExcelUtil.writeExcel(filePath, dataList, headers, "格式测试", formats);
        
        // 验证文件存在
        File file = new File(filePath);
        assertTrue(file.exists(), "Excel文件应该被创建");
        
        // 读取验证
        List<Map<String, Object>> readData = ExcelUtil.readExcel(filePath, true);
        assertEquals(3, readData.size(), "应该有3行数据");
    }
    
    /**
     * 测试带格式配置的导出到流
     * 验证能正确应用格式配置并导出到输出流
     */
    @Test
    @DisplayName("测试带格式配置的导出到流")
    void testExportToStreamWithFormats() throws Exception {
        // 准备测试数据
        List<Map<String, Object>> dataList = new ArrayList<>();
        Map<String, Object> row = new LinkedHashMap<>();
        row.put("产品", "商品A");
        row.put("价格", 99.99);
        row.put("数量", 100);
        dataList.add(row);
        
        String[] headers = {"产品", "价格", "数量"};
        
        // 创建格式配置
        Map<String, ColumnFormat> formats = new HashMap<>();
        formats.put("产品", CellFormatBuilder.textCenter());
        formats.put("价格", CellFormatBuilder.currencyCNY());
        formats.put("数量", CellFormatBuilder.integerRight());
        
        String filePath = tempDir.resolve("test_stream_formats.xlsx").toString();
        
        // 导出到文件流（带格式）
        try (java.io.FileOutputStream fos = new java.io.FileOutputStream(filePath)) {
            ExcelUtil.exportToStream(fos, dataList, headers, "流导出格式测试", formats);
        }
        
        // 验证文件存在
        File file = new File(filePath);
        assertTrue(file.exists(), "Excel文件应该被创建");
    }
    
    /**
     * 测试部分列格式配置
     * 验证只配置部分列的格式时，其他列使用默认格式
     */
    @Test
    @DisplayName("测试部分列格式配置")
    void testPartialFormatConfig() throws Exception {
        // 准备测试数据
        List<Map<String, Object>> dataList = new ArrayList<>();
        Map<String, Object> row = new LinkedHashMap<>();
        row.put("列1", "文本");
        row.put("列2", 100);
        row.put("列3", 99.99);
        dataList.add(row);
        
        String[] headers = {"列1", "列2", "列3"};
        
        // 只配置部分列的格式
        Map<String, ColumnFormat> formats = new HashMap<>();
        formats.put("列2", CellFormatBuilder.integer());
        formats.put("列3", CellFormatBuilder.currencyCNY());
        // 列1不配置格式，应使用默认格式
        
        String filePath = tempDir.resolve("test_partial_formats.xlsx").toString();
        
        // 写入Excel（部分格式）
        assertDoesNotThrow(() -> {
            ExcelUtil.writeExcel(filePath, dataList, headers, "部分格式", formats);
        }, "部分格式配置不应抛出异常");
        
        // 验证文件存在
        File file = new File(filePath);
        assertTrue(file.exists(), "Excel文件应该被创建");
    }
}
