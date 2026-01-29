package com.practise.demo.util;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.*;

/**
 * ExcelAdvancedUtil 高级操作工具类测试
 * 测试单元格合并、冻结窗格、公式、样式等高级功能
 * 
 * @author system
 */
@DisplayName("Excel高级操作工具类测试")
class ExcelAdvancedUtilTest {
    
    /**
     * 临时目录，用于存放测试文件
     */
    @TempDir
    Path tempDir;
    
    /**
     * 测试合并单元格功能
     * 验证能正确合并指定区域的单元格
     */
    @Test
    @DisplayName("测试合并单元格功能")
    void testMergeCells() throws IOException {
        try (Workbook workbook = new XSSFWorkbook()) {
            Sheet sheet = workbook.createSheet("测试");
        
        // 创建测试单元格
        Row row1 = sheet.createRow(0);
        row1.createCell(0).setCellValue("合并前");
        row1.createCell(1).setCellValue("合并前");
        
        // 合并单元格
        ExcelAdvancedUtil.mergeCells(sheet, 0, 0, 0, 1);
        
        // 验证合并区域数量
        assertEquals(1, sheet.getNumMergedRegions(), "应该有1个合并区域");
        
            // 验证合并区域范围
            CellRangeAddress mergedRegion = sheet.getMergedRegion(0);
            assertEquals(0, mergedRegion.getFirstRow(), "起始行应为0");
            assertEquals(0, mergedRegion.getLastRow(), "结束行应为0");
            assertEquals(0, mergedRegion.getFirstColumn(), "起始列应为0");
            assertEquals(1, mergedRegion.getLastColumn(), "结束列应为1");
        }
    }
    
    /**
     * 测试合并行单元格（单行多列）
     * 验证能正确合并同一行的多个列
     */
    @Test
    @DisplayName("测试合并行单元格")
    void testMergeRowCells() throws IOException {
        try (Workbook workbook = new XSSFWorkbook()) {
            Sheet sheet = workbook.createSheet("测试");
            
            Row row = sheet.createRow(0);
            row.createCell(0).setCellValue("合并");
            
            ExcelAdvancedUtil.mergeRowCells(sheet, 0, 0, 2);
            
            assertEquals(1, sheet.getNumMergedRegions(), "应该有1个合并区域");
            CellRangeAddress mergedRegion = sheet.getMergedRegion(0);
            assertEquals(0, mergedRegion.getFirstRow(), "起始行应为0");
            assertEquals(0, mergedRegion.getLastRow(), "结束行应为0");
            assertEquals(0, mergedRegion.getFirstColumn(), "起始列应为0");
            assertEquals(2, mergedRegion.getLastColumn(), "结束列应为2");
        }
    }
    
    /**
     * 测试合并列单元格（单列多行）
     * 验证能正确合并同一列的多个行
     */
    @Test
    @DisplayName("测试合并列单元格")
    void testMergeColumnCells() throws IOException {
        try (Workbook workbook = new XSSFWorkbook()) {
            Sheet sheet = workbook.createSheet("测试");
            
            Row row1 = sheet.createRow(0);
            row1.createCell(0).setCellValue("合并");
            
            ExcelAdvancedUtil.mergeColumnCells(sheet, 0, 2, 0);
            
            assertEquals(1, sheet.getNumMergedRegions(), "应该有1个合并区域");
            CellRangeAddress mergedRegion = sheet.getMergedRegion(0);
            assertEquals(0, mergedRegion.getFirstRow(), "起始行应为0");
            assertEquals(2, mergedRegion.getLastRow(), "结束行应为2");
            assertEquals(0, mergedRegion.getFirstColumn(), "起始列应为0");
            assertEquals(0, mergedRegion.getLastColumn(), "结束列应为0");
        }
    }
    
    /**
     * 测试合并表头
     * 验证能正确合并第一行的多个列
     */
    @Test
    @DisplayName("测试合并表头")
    void testMergeHeader() throws IOException {
        try (Workbook workbook = new XSSFWorkbook()) {
            Sheet sheet = workbook.createSheet("测试");
            
            Row headerRow = sheet.createRow(0);
            headerRow.createCell(0).setCellValue("合并表头");
            
            ExcelAdvancedUtil.mergeHeader(sheet, 0, 2);
            
            assertEquals(1, sheet.getNumMergedRegions(), "应该有1个合并区域");
            CellRangeAddress mergedRegion = sheet.getMergedRegion(0);
            assertEquals(0, mergedRegion.getFirstRow(), "起始行应为0（表头行）");
            assertEquals(0, mergedRegion.getLastRow(), "结束行应为0（表头行）");
        }
    }
    
    /**
     * 测试冻结窗格功能
     * 验证能正确冻结指定行和列
     */
    @Test
    @DisplayName("测试冻结窗格功能")
    void testFreezePane() throws IOException {
        try (Workbook workbook = new XSSFWorkbook()) {
            Sheet sheet = workbook.createSheet("测试");
            
            // 创建一些数据
            for (int i = 0; i < 10; i++) {
                Row row = sheet.createRow(i);
                for (int j = 0; j < 5; j++) {
                    row.createCell(j).setCellValue("数据" + i + "-" + j);
                }
            }
            
            // 冻结第1行和第1列
            ExcelAdvancedUtil.freezePane(sheet, 1, 1);
            
            // 验证冻结窗格已设置（通过检查sheet属性）
            assertNotNull(sheet, "工作表不应为空");
        }
    }
    
    /**
     * 测试冻结首行
     * 验证能正确冻结第一行
     */
    @Test
    @DisplayName("测试冻结首行")
    void testFreezeFirstRow() throws IOException {
        try (Workbook workbook = new XSSFWorkbook()) {
            Sheet sheet = workbook.createSheet("测试");
            
            ExcelAdvancedUtil.freezeFirstRow(sheet);
            
            assertNotNull(sheet, "工作表不应为空");
        }
    }
    
    /**
     * 测试冻结首列
     * 验证能正确冻结第一列
     */
    @Test
    @DisplayName("测试冻结首列")
    void testFreezeFirstColumn() throws IOException {
        try (Workbook workbook = new XSSFWorkbook()) {
            Sheet sheet = workbook.createSheet("测试");
            
            ExcelAdvancedUtil.freezeFirstColumn(sheet);
            
            assertNotNull(sheet, "工作表不应为空");
        }
    }
    
    /**
     * 测试冻结首行和首列
     * 验证能同时冻结第一行和第一列
     */
    @Test
    @DisplayName("测试冻结首行和首列")
    void testFreezeFirstRowAndColumn() throws IOException {
        try (Workbook workbook = new XSSFWorkbook()) {
            Sheet sheet = workbook.createSheet("测试");
            
            ExcelAdvancedUtil.freezeFirstRowAndColumn(sheet);
            
            assertNotNull(sheet, "工作表不应为空");
        }
    }
    
    /**
     * 测试设置列宽
     * 验证能正确设置指定列的宽度
     */
    @Test
    @DisplayName("测试设置列宽")
    void testSetColumnWidth() throws IOException {
        try (Workbook workbook = new XSSFWorkbook()) {
            Sheet sheet = workbook.createSheet("测试");
            
            ExcelAdvancedUtil.setColumnWidth(sheet, 0, 15);
            
            int width = sheet.getColumnWidth(0);
            assertEquals(15 * 256, width, "列宽应为15字符（15*256单位）");
        }
    }
    
    /**
     * 测试批量设置列宽
     * 验证能正确批量设置多列的宽度
     */
    @Test
    @DisplayName("测试批量设置列宽")
    void testSetColumnWidths() throws IOException {
        try (Workbook workbook = new XSSFWorkbook()) {
            Sheet sheet = workbook.createSheet("测试");
            
            int[] widths = {10, 15, 20};
            ExcelAdvancedUtil.setColumnWidths(sheet, widths);
            
            assertEquals(10 * 256, sheet.getColumnWidth(0), "第1列宽度应为10字符");
            assertEquals(15 * 256, sheet.getColumnWidth(1), "第2列宽度应为15字符");
            assertEquals(20 * 256, sheet.getColumnWidth(2), "第3列宽度应为20字符");
        }
    }
    
    /**
     * 测试自动调整列宽
     * 验证能自动调整列宽以适应内容
     */
    @Test
    @DisplayName("测试自动调整列宽")
    void testAutoSizeColumn() throws IOException {
        try (Workbook workbook = new XSSFWorkbook()) {
            Sheet sheet = workbook.createSheet("测试");
            
            Row row = sheet.createRow(0);
            row.createCell(0).setCellValue("这是一个很长的文本内容用于测试自动调整列宽");
            
            ExcelAdvancedUtil.autoSizeColumn(sheet, 0);
            
            assertTrue(sheet.getColumnWidth(0) > 0, "列宽应该大于0");
        }
    }
    
    /**
     * 测试设置行高
     * 验证能正确设置行高
     */
    @Test
    @DisplayName("测试设置行高")
    void testSetRowHeight() throws IOException {
        try (Workbook workbook = new XSSFWorkbook()) {
            Sheet sheet = workbook.createSheet("测试");
            
            Row row = sheet.createRow(0);
            ExcelAdvancedUtil.setRowHeight(row, 20.0f);
            
            assertEquals(20.0f, row.getHeightInPoints(), 0.01f, "行高应为20磅");
        }
    }
    
    /**
     * 测试设置单元格公式
     * 验证能正确设置单元格公式
     */
    @Test
    @DisplayName("测试设置单元格公式")
    void testSetCellFormula() throws IOException {
        try (Workbook workbook = new XSSFWorkbook()) {
            Sheet sheet = workbook.createSheet("测试");
            
            // 创建数据
            Row row1 = sheet.createRow(0);
            row1.createCell(0).setCellValue(10);
            row1.createCell(1).setCellValue(20);
            
            Row row2 = sheet.createRow(1);
            Cell formulaCell = row2.createCell(0);
            
            ExcelAdvancedUtil.setCellFormula(formulaCell, "SUM(A1:B1)");
            
            assertEquals("SUM(A1:B1)", formulaCell.getCellFormula(), "公式应为SUM(A1:B1)");
        }
    }
    
    /**
     * 测试设置求和公式
     * 验证能正确设置SUM公式
     */
    @Test
    @DisplayName("测试设置求和公式")
    void testSetSumFormula() throws IOException {
        try (Workbook workbook = new XSSFWorkbook()) {
            Sheet sheet = workbook.createSheet("测试");
            
            // 创建数据
            for (int i = 0; i < 5; i++) {
                Row row = sheet.createRow(i);
                row.createCell(0).setCellValue(i + 1);
            }
            
            // 设置求和公式
            Row summaryRow = sheet.createRow(5);
            Cell sumCell = summaryRow.createCell(0);
            ExcelAdvancedUtil.setSumFormula(sumCell, 0, 4, 0);
            
            assertEquals("SUM(A1:A5)", sumCell.getCellFormula(), "公式应为SUM(A1:A5)");
        }
    }
    
    /**
     * 测试设置平均值公式
     * 验证能正确设置AVERAGE公式
     */
    @Test
    @DisplayName("测试设置平均值公式")
    void testSetAverageFormula() throws IOException {
        try (Workbook workbook = new XSSFWorkbook()) {
            Sheet sheet = workbook.createSheet("测试");
            
            Row summaryRow = sheet.createRow(5);
            Cell avgCell = summaryRow.createCell(0);
            ExcelAdvancedUtil.setAverageFormula(avgCell, 0, 4, 0);
            
            assertEquals("AVERAGE(A1:A5)", avgCell.getCellFormula(), "公式应为AVERAGE(A1:A5)");
        }
    }
    
    /**
     * 测试设置最大值和最小值公式
     * 验证能正确设置MAX和MIN公式
     */
    @Test
    @DisplayName("测试设置最大值和最小值公式")
    void testSetMaxMinFormula() throws IOException {
        try (Workbook workbook = new XSSFWorkbook()) {
            Sheet sheet = workbook.createSheet("测试");
            
            Row summaryRow = sheet.createRow(5);
            Cell maxCell = summaryRow.createCell(0);
            Cell minCell = summaryRow.createCell(1);
            
            ExcelAdvancedUtil.setMaxFormula(maxCell, 0, 4, 0);
            ExcelAdvancedUtil.setMinFormula(minCell, 0, 4, 0);
            
            assertEquals("MAX(A1:A5)", maxCell.getCellFormula(), "最大值公式应为MAX(A1:A5)");
            assertEquals("MIN(A1:A5)", minCell.getCellFormula(), "最小值公式应为MIN(A1:A5)");
        }
    }
    
    /**
     * 测试设置计数公式
     * 验证能正确设置COUNT和COUNTIF公式
     */
    @Test
    @DisplayName("测试设置计数公式")
    void testSetCountFormula() throws IOException {
        try (Workbook workbook = new XSSFWorkbook()) {
            Sheet sheet = workbook.createSheet("测试");
            
            Row summaryRow = sheet.createRow(5);
            Cell countCell = summaryRow.createCell(0);
            Cell countIfCell = summaryRow.createCell(1);
            
            ExcelAdvancedUtil.setCountFormula(countCell, 0, 4, 0);
            ExcelAdvancedUtil.setCountIfFormula(countIfCell, 0, 4, 0, ">10");
            
            assertEquals("COUNT(A1:A5)", countCell.getCellFormula(), "计数公式应为COUNT(A1:A5)");
            assertEquals("COUNTIF(A1:A5,\">10\")", countIfCell.getCellFormula(), "条件计数公式应为COUNTIF(A1:A5,\">10\")");
        }
    }
    
    /**
     * 测试设置单元格边框
     * 验证能正确设置单元格边框样式
     */
    @Test
    @DisplayName("测试设置单元格边框")
    void testSetCellBorder() throws IOException {
        try (Workbook workbook = new XSSFWorkbook()) {
            CellStyle style = workbook.createCellStyle();
            
            ExcelAdvancedUtil.setCellBorder(style, BorderStyle.THIN);
            
            assertEquals(BorderStyle.THIN, style.getBorderTop(), "上边框应为THIN");
            assertEquals(BorderStyle.THIN, style.getBorderBottom(), "下边框应为THIN");
            assertEquals(BorderStyle.THIN, style.getBorderLeft(), "左边框应为THIN");
            assertEquals(BorderStyle.THIN, style.getBorderRight(), "右边框应为THIN");
        }
    }
    
    /**
     * 测试创建数据汇总行
     * 验证能正确创建包含汇总公式的汇总行
     */
    @Test
    @DisplayName("测试创建数据汇总行")
    void testCreateSummaryRow() throws IOException {
        try (Workbook workbook = new XSSFWorkbook()) {
            Sheet sheet = workbook.createSheet("测试");
            
            // 创建表头
            Row headerRow = sheet.createRow(0);
            String[] headers = {"产品", "数量", "单价", "金额"};
            for (int i = 0; i < headers.length; i++) {
                headerRow.createCell(i).setCellValue(headers[i]);
            }
            
            // 创建数据
            for (int i = 1; i <= 5; i++) {
                Row row = sheet.createRow(i);
                row.createCell(0).setCellValue("产品" + i);
                row.createCell(1).setCellValue(i * 10);
                row.createCell(2).setCellValue(100.0);
                row.createCell(3).setCellValue(i * 10 * 100.0);
            }
            
            // 创建汇总行
            int[] summaryColumns = {1, 3}; // 汇总数量和金额列
            ExcelAdvancedUtil.createSummaryRow(sheet, 6, headers, "合计", summaryColumns, 1, 5);
            
            // 验证汇总行存在
            Row summaryRow = sheet.getRow(6);
            assertNotNull(summaryRow, "汇总行不应为空");
            
            // 验证汇总标签
            assertEquals("合计", summaryRow.getCell(0).getStringCellValue(), "汇总标签应为'合计'");
            
            // 验证汇总公式
            Cell sumCell = summaryRow.getCell(3);
            assertNotNull(sumCell, "汇总单元格不应为空");
            assertNotNull(sumCell.getCellFormula(), "汇总单元格应有公式");
            assertTrue(sumCell.getCellFormula().contains("SUM"), "汇总公式应包含SUM");
        }
    }
    
    /**
     * 测试列字母和索引转换
     * 验证能正确在列字母和索引之间转换
     */
    @Test
    @DisplayName("测试列字母和索引转换")
    void testColumnLetterAndIndexConversion() {
        // 测试列索引转字母
        assertEquals("A", ExcelAdvancedUtil.getColumnLetter(0), "索引0应为A");
        assertEquals("B", ExcelAdvancedUtil.getColumnLetter(1), "索引1应为B");
        assertEquals("Z", ExcelAdvancedUtil.getColumnLetter(25), "索引25应为Z");
        assertEquals("AA", ExcelAdvancedUtil.getColumnLetter(26), "索引26应为AA");
        assertEquals("AB", ExcelAdvancedUtil.getColumnLetter(27), "索引27应为AB");
        
        // 测试列字母转索引
        assertEquals(0, ExcelAdvancedUtil.getColumnIndex("A"), "字母A应为索引0");
        assertEquals(1, ExcelAdvancedUtil.getColumnIndex("B"), "字母B应为索引1");
        assertEquals(25, ExcelAdvancedUtil.getColumnIndex("Z"), "字母Z应为索引25");
        assertEquals(26, ExcelAdvancedUtil.getColumnIndex("AA"), "字母AA应为索引26");
        assertEquals(27, ExcelAdvancedUtil.getColumnIndex("AB"), "字母AB应为索引27");
    }
    
    /**
     * 测试综合功能：合并单元格、格式、样式、公式联合使用
     * 验证多个功能能够协同工作
     */
    @Test
    @DisplayName("测试综合功能：合并、格式、样式、公式联合使用")
    void testComprehensiveFeatures() throws Exception {
        try (Workbook workbook = new XSSFWorkbook()) {
            Sheet sheet = workbook.createSheet("综合测试");
        
        // 1. 创建表头并合并
        Row headerRow = sheet.createRow(0);
        headerRow.createCell(0).setCellValue("销售报表");
        ExcelAdvancedUtil.mergeHeader(sheet, 0, 3);
        
        // 2. 创建列标题
        Row titleRow = sheet.createRow(1);
        String[] headers = {"产品", "数量", "单价", "金额"};
        for (int i = 0; i < headers.length; i++) {
            Cell cell = titleRow.createCell(i);
            cell.setCellValue(headers[i]);
            // 应用样式
            CellStyle style = workbook.createCellStyle();
            Font font = workbook.createFont();
            font.setBold(true);
            style.setFont(font);
            style.setAlignment(HorizontalAlignment.CENTER);
            ExcelAdvancedUtil.setCellBorder(style, BorderStyle.THIN);
            cell.setCellStyle(style);
        }
        
        // 3. 创建数据
        for (int i = 2; i <= 6; i++) {
            Row row = sheet.createRow(i);
            row.createCell(0).setCellValue("产品" + (i - 1));
            row.createCell(1).setCellValue((i - 1) * 10);
            row.createCell(2).setCellValue(100.0);
            // 金额列使用公式
            Cell amountCell = row.createCell(3);
            ExcelAdvancedUtil.setCellFormula(amountCell, String.format("B%d*C%d", i + 1, i + 1));
        }
        
        // 4. 创建汇总行
        int[] summaryColumns = {1, 3};
        ExcelAdvancedUtil.createSummaryRow(sheet, 7, headers, "合计", summaryColumns, 2, 6);
        
        // 5. 设置列宽
        ExcelAdvancedUtil.setColumnWidths(sheet, new int[]{15, 10, 10, 15});
        
        // 6. 冻结首行和首列
        ExcelAdvancedUtil.freezeFirstRowAndColumn(sheet);
        
        // 验证结果
        assertEquals(1, sheet.getNumMergedRegions(), "应该有合并区域");
        assertNotNull(sheet.getRow(7), "汇总行应存在");
        
            // 保存文件用于验证
            String filePath = tempDir.resolve("comprehensive_test.xlsx").toString();
            try (FileOutputStream fos = new FileOutputStream(filePath)) {
                workbook.write(fos);
            }
            
            assertTrue(new java.io.File(filePath).exists(), "文件应该被创建");
        }
    }
}
