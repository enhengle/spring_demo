package com.practise.demo.util;

import org.apache.poi.ss.usermodel.*;
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
 * Excel 综合功能测试类
 * 测试单元格格式、样式、合并、公式等功能的联合使用
 * 
 * @author system
 */
@DisplayName("Excel综合功能测试")
class ExcelComprehensiveTest {
    
    /**
     * 临时目录，用于存放测试文件
     */
    @TempDir
    Path tempDir;
    
    /**
     * 测试综合报表生成：格式、样式、合并、公式、冻结窗格
     * 模拟实际业务场景中的报表生成
     */
    @Test
    @DisplayName("测试综合报表生成：格式、样式、合并、公式、冻结窗格")
    void testComprehensiveReport() throws IOException {
        try (Workbook workbook = new XSSFWorkbook()) {
            Sheet sheet = workbook.createSheet("销售报表");
            
            // 1. 创建标题行（合并单元格 + 样式）
            Row titleRow = sheet.createRow(0);
            Cell titleCell = titleRow.createCell(0);
            titleCell.setCellValue("2024年度销售报表");
            CellStyle titleStyle = workbook.createCellStyle();
            Font titleFont = workbook.createFont();
            titleFont.setBold(true);
            titleFont.setFontHeightInPoints((short) 16);
            titleStyle.setFont(titleFont);
            titleStyle.setAlignment(HorizontalAlignment.CENTER);
            titleStyle.setVerticalAlignment(VerticalAlignment.CENTER);
            titleCell.setCellStyle(titleStyle);
            ExcelAdvancedUtil.mergeCells(sheet, 0, 0, 0, 4); // 合并标题行
            
            // 2. 创建表头（格式 + 样式）
            Row headerRow = sheet.createRow(1);
            String[] headers = {"产品名称", "销售数量", "单价", "金额", "日期"};
            Map<String, ColumnFormat> formats = new HashMap<>();
            formats.put("产品名称", CellFormatBuilder.textCenter());
            formats.put("销售数量", CellFormatBuilder.integer());
            formats.put("单价", CellFormatBuilder.currencyCNY());
            formats.put("金额", CellFormatBuilder.amount());
            formats.put("日期", CellFormatBuilder.date());
            
            CellStyle headerStyle = workbook.createCellStyle();
            Font headerFont = workbook.createFont();
            headerFont.setBold(true);
            headerStyle.setFont(headerFont);
            headerStyle.setAlignment(HorizontalAlignment.CENTER);
            headerStyle.setFillForegroundColor(IndexedColors.GREY_25_PERCENT.getIndex());
            headerStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
            ExcelAdvancedUtil.setCellBorder(headerStyle, BorderStyle.THIN);
            
            for (int i = 0; i < headers.length; i++) {
                Cell cell = headerRow.createCell(i);
                cell.setCellValue(headers[i]);
                cell.setCellStyle(headerStyle);
            }
            
            // 3. 创建数据行（格式 + 公式）
            int dataStartRow = 2;
            for (int i = 0; i < 10; i++) {
                Row row = sheet.createRow(dataStartRow + i);
                
                // 产品名称
                Cell nameCell = row.createCell(0);
                nameCell.setCellValue("产品" + (i + 1));
                applyFormat(workbook, nameCell, formats.get("产品名称"));
                
                // 销售数量
                Cell qtyCell = row.createCell(1);
                qtyCell.setCellValue((i + 1) * 10);
                applyFormat(workbook, qtyCell, formats.get("销售数量"));
                
                // 单价
                Cell priceCell = row.createCell(2);
                priceCell.setCellValue(100.0 + i * 10);
                applyFormat(workbook, priceCell, formats.get("单价"));
                
                // 金额（使用公式）
                Cell amountCell = row.createCell(3);
                int rowNum = dataStartRow + i + 1;
                ExcelAdvancedUtil.setCellFormula(amountCell, String.format("B%d*C%d", rowNum, rowNum));
                applyFormat(workbook, amountCell, formats.get("金额"));
                
                // 日期
                Cell dateCell = row.createCell(4);
                dateCell.setCellValue(new java.util.Date());
                applyFormat(workbook, dateCell, formats.get("日期"));
            }
            
            // 4. 创建汇总行（合并 + 公式 + 样式）
            int summaryRowIndex = dataStartRow + 10;
            int[] summaryColumns = {1, 3}; // 汇总数量和金额
            ExcelAdvancedUtil.createSummaryRow(sheet, summaryRowIndex, headers, "合计", 
                    summaryColumns, dataStartRow, dataStartRow + 9);
            
            // 5. 设置列宽
            ExcelAdvancedUtil.setColumnWidths(sheet, new int[]{20, 12, 12, 15, 15});
            
            // 6. 冻结首行和首列
            ExcelAdvancedUtil.freezeFirstRowAndColumn(sheet);
            
            // 7. 设置行高
            ExcelAdvancedUtil.setRowHeight(titleRow, 30.0f);
            ExcelAdvancedUtil.setRowHeight(headerRow, 20.0f);
            
            // 验证结果
            assertEquals(1, sheet.getNumMergedRegions(), "应该有合并区域");
            assertNotNull(sheet.getRow(summaryRowIndex), "汇总行应存在");
            
            // 保存文件
            String filePath = tempDir.resolve("comprehensive_report.xlsx").toString();
            try (FileOutputStream fos = new FileOutputStream(filePath)) {
                workbook.write(fos);
            }
            
            assertTrue(new java.io.File(filePath).exists(), "文件应该被创建");
        }
    }
    
    /**
     * 测试带分组汇总的报表
     * 模拟按部门分组并汇总的场景
     */
    @Test
    @DisplayName("测试带分组汇总的报表")
    void testGroupedSummaryReport() throws IOException {
        try (Workbook workbook = new XSSFWorkbook()) {
            Sheet sheet = workbook.createSheet("部门报表");
            
            // 创建表头
            Row headerRow = sheet.createRow(0);
            String[] headers = {"部门", "员工", "工资", "奖金", "总计"};
            for (int i = 0; i < headers.length; i++) {
                headerRow.createCell(i).setCellValue(headers[i]);
            }
            
            // 创建数据（按部门分组）
            int currentRow = 1;
            String[] departments = {"技术部", "销售部", "人事部"};
            
            for (String dept : departments) {
                int deptStartRow = currentRow;
                
                // 部门数据
                for (int i = 0; i < 3; i++) {
                    Row row = sheet.createRow(currentRow++);
                    row.createCell(0).setCellValue(dept);
                    row.createCell(1).setCellValue("员工" + (i + 1));
                    row.createCell(2).setCellValue(10000 + i * 1000);
                    row.createCell(3).setCellValue(2000 + i * 200);
                }
                
                // 部门小计行
                Row deptSummaryRow = sheet.createRow(currentRow++);
                Cell deptLabelCell = deptSummaryRow.createCell(0);
                deptLabelCell.setCellValue(dept + "小计");
                
                // 设置汇总公式
                ExcelAdvancedUtil.setSumFormula(deptSummaryRow.createCell(2), deptStartRow, currentRow - 2, 2);
                ExcelAdvancedUtil.setSumFormula(deptSummaryRow.createCell(3), deptStartRow, currentRow - 2, 3);
                ExcelAdvancedUtil.setSumFormula(deptSummaryRow.createCell(4), deptStartRow, currentRow - 2, 2);
                
                // 合并部门标签
                ExcelAdvancedUtil.mergeColumnCells(sheet, deptStartRow, currentRow - 2, 0);
            }
            
            // 创建总计行
            int totalRowIndex = currentRow;
            Row totalRow = sheet.createRow(totalRowIndex);
            totalRow.createCell(0).setCellValue("总计");
            ExcelAdvancedUtil.setSumFormula(totalRow.createCell(2), 1, totalRowIndex - 1, 2);
            ExcelAdvancedUtil.setSumFormula(totalRow.createCell(3), 1, totalRowIndex - 1, 3);
            ExcelAdvancedUtil.setSumFormula(totalRow.createCell(4), 1, totalRowIndex - 1, 2);
            
            // 设置样式
            CellStyle summaryStyle = workbook.createCellStyle();
            Font summaryFont = workbook.createFont();
            summaryFont.setBold(true);
            summaryStyle.setFont(summaryFont);
            summaryStyle.setFillForegroundColor(IndexedColors.LIGHT_YELLOW.getIndex());
            summaryStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
            
            // 应用样式到汇总行
            for (int i = 0; i < headers.length; i++) {
                Row summaryRow = sheet.getRow(totalRowIndex);
                if (summaryRow != null) {
                    Cell cell = summaryRow.getCell(i);
                    if (cell != null) {
                        cell.setCellStyle(summaryStyle);
                    }
                }
            }
            
            // 验证
            assertTrue(sheet.getNumMergedRegions() > 0, "应该有合并区域");
            
            // 保存文件
            String filePath = tempDir.resolve("grouped_summary_report.xlsx").toString();
            try (FileOutputStream fos = new FileOutputStream(filePath)) {
                workbook.write(fos);
            }
            
            assertTrue(new java.io.File(filePath).exists(), "文件应该被创建");
        }
    }
    
    /**
     * 测试数据透视表风格的报表
     * 模拟数据透视表的布局和汇总
     */
    @Test
    @DisplayName("测试数据透视表风格的报表")
    void testPivotTableStyleReport() throws IOException {
        try (Workbook workbook = new XSSFWorkbook()) {
            Sheet sheet = workbook.createSheet("数据透视");
            
            // 创建多级表头
            Row headerRow1 = sheet.createRow(0);
            headerRow1.createCell(0).setCellValue("产品");
            ExcelAdvancedUtil.mergeCells(sheet, 0, 0, 0, 0);
            headerRow1.createCell(1).setCellValue("Q1");
            ExcelAdvancedUtil.mergeCells(sheet, 0, 0, 1, 3);
            headerRow1.createCell(4).setCellValue("Q2");
            ExcelAdvancedUtil.mergeCells(sheet, 0, 0, 4, 6);
            
            Row headerRow2 = sheet.createRow(1);
            String[] subHeaders = {"产品名称", "1月", "2月", "3月", "4月", "5月", "6月"};
            for (int i = 0; i < subHeaders.length; i++) {
                headerRow2.createCell(i).setCellValue(subHeaders[i]);
            }
            
            // 创建数据
            for (int i = 0; i < 5; i++) {
                Row row = sheet.createRow(i + 2);
                row.createCell(0).setCellValue("产品" + (i + 1));
                for (int j = 1; j <= 6; j++) {
                    row.createCell(j).setCellValue((i + 1) * 100 + j * 10);
                }
            }
            
            // 创建季度汇总列
            Row q1SummaryRow = sheet.createRow(7);
            q1SummaryRow.createCell(0).setCellValue("Q1合计");
            ExcelAdvancedUtil.setSumFormula(q1SummaryRow.createCell(1), 2, 6, 1);
            ExcelAdvancedUtil.setSumFormula(q1SummaryRow.createCell(2), 2, 6, 2);
            ExcelAdvancedUtil.setSumFormula(q1SummaryRow.createCell(3), 2, 6, 3);
            
            Row q2SummaryRow = sheet.createRow(8);
            q2SummaryRow.createCell(0).setCellValue("Q2合计");
            ExcelAdvancedUtil.setSumFormula(q2SummaryRow.createCell(4), 2, 6, 4);
            ExcelAdvancedUtil.setSumFormula(q2SummaryRow.createCell(5), 2, 6, 5);
            ExcelAdvancedUtil.setSumFormula(q2SummaryRow.createCell(6), 2, 6, 6);
            
            // 冻结前两行
            ExcelAdvancedUtil.freezePane(sheet, 2, 0);
            
            // 设置列宽
            ExcelAdvancedUtil.setColumnWidths(sheet, new int[]{15, 12, 12, 12, 12, 12, 12});
            
            // 验证
            assertTrue(sheet.getNumMergedRegions() >= 2, "应该有至少2个合并区域");
            
            // 保存文件
            String filePath = tempDir.resolve("pivot_table_style_report.xlsx").toString();
            try (FileOutputStream fos = new FileOutputStream(filePath)) {
                workbook.write(fos);
            }
            
            assertTrue(new java.io.File(filePath).exists(), "文件应该被创建");
        }
    }
    
    /**
     * 测试条件格式和样式组合
     * 测试不同数据类型的格式应用
     */
    @Test
    @DisplayName("测试条件格式和样式组合")
    void testConditionalFormattingAndStyles() throws IOException {
        try (Workbook workbook = new XSSFWorkbook()) {
            Sheet sheet = workbook.createSheet("条件格式测试");
            
            // 创建表头
            Row headerRow = sheet.createRow(0);
            String[] headers = {"项目", "数值", "状态", "百分比", "日期"};
            for (int i = 0; i < headers.length; i++) {
                headerRow.createCell(i).setCellValue(headers[i]);
            }
            
            // 创建格式配置
            Map<String, ColumnFormat> formats = new HashMap<>();
            formats.put("项目", CellFormatBuilder.textCenter());
            formats.put("数值", CellFormatBuilder.integerRight());
            formats.put("状态", CellFormatBuilder.textCenter());
            formats.put("百分比", CellFormatBuilder.percentage());
            formats.put("日期", CellFormatBuilder.date());
            
            // 创建数据并应用格式
            for (int i = 1; i <= 10; i++) {
                Row row = sheet.createRow(i);
                
                // 项目
                Cell projectCell = row.createCell(0);
                projectCell.setCellValue("项目" + i);
                applyFormat(workbook, projectCell, formats.get("项目"));
                
                // 数值
                Cell valueCell = row.createCell(1);
                int value = i * 100;
                valueCell.setCellValue(value);
                applyFormat(workbook, valueCell, formats.get("数值"));
                
                // 状态（根据数值应用不同样式）
                Cell statusCell = row.createCell(2);
                if (value > 500) {
                    statusCell.setCellValue("优秀");
                    CellStyle style = workbook.createCellStyle();
                    Font font = workbook.createFont();
                    font.setColor(IndexedColors.GREEN.getIndex());
                    style.setFont(font);
                    statusCell.setCellStyle(style);
                } else {
                    statusCell.setCellValue("一般");
                }
                
                // 百分比
                Cell percentCell = row.createCell(3);
                percentCell.setCellValue(value / 1000.0);
                applyFormat(workbook, percentCell, formats.get("百分比"));
                
                // 日期
                Cell dateCell = row.createCell(4);
                dateCell.setCellValue(new java.util.Date());
                applyFormat(workbook, dateCell, formats.get("日期"));
            }
            
            // 设置列宽
            ExcelAdvancedUtil.setColumnWidths(sheet, new int[]{15, 12, 12, 12, 15});
            
            // 保存文件
            String filePath = tempDir.resolve("conditional_formatting_test.xlsx").toString();
            try (FileOutputStream fos = new FileOutputStream(filePath)) {
                workbook.write(fos);
            }
            
            assertTrue(new java.io.File(filePath).exists(), "文件应该被创建");
        }
    }
    
    /**
     * 应用格式到单元格（辅助方法）
     */
    private void applyFormat(Workbook workbook, Cell cell, ColumnFormat format) {
        if (format != null) {
            // 创建样式
            CellStyle style = workbook.createCellStyle();
            DataFormat dataFormat = workbook.createDataFormat();
            
            // 设置数据格式
            switch (format.getDataType()) {
                case DATE:
                case DATETIME:
                    style.setDataFormat(dataFormat.getFormat(format.getDateFormat()));
                    break;
                case PERCENTAGE:
                    style.setDataFormat(dataFormat.getFormat("0.00%"));
                    break;
                case CURRENCY:
                    style.setDataFormat(dataFormat.getFormat(format.getNumberFormat()));
                    break;
                case INTEGER:
                case DECIMAL:
                case NUMBER:
                    style.setDataFormat(dataFormat.getFormat(format.getNumberFormat()));
                    break;
                default:
                    break;
            }
            
            // 设置对齐方式
            style.setAlignment(format.getAlignment().getPoiAlignment());
            
            // 设置字体
            Font font = workbook.createFont();
            font.setFontName(format.getFontName());
            font.setFontHeightInPoints(format.getFontSize());
            font.setBold(format.getBold());
            if (format.getFontColor() != null) {
                font.setColor(format.getFontColor());
            }
            style.setFont(font);
            
            // 设置背景色
            if (format.getBackgroundColor() != null) {
                style.setFillForegroundColor(format.getBackgroundColor());
                style.setFillPattern(FillPatternType.SOLID_FOREGROUND);
            }
            
            // 设置自动换行
            style.setWrapText(format.getWrapText());
            
            // 设置边框
            style.setBorderBottom(BorderStyle.THIN);
            style.setBorderTop(BorderStyle.THIN);
            style.setBorderLeft(BorderStyle.THIN);
            style.setBorderRight(BorderStyle.THIN);
            
            cell.setCellStyle(style);
        }
    }
}
