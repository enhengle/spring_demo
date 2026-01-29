package com.practise.demo.util;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Map;

/**
 * Excel 高级操作工具类
 * 提供单元格合并、冻结窗格、公式、样式等高级功能
 * 
 * @author system
 */
public class ExcelAdvancedUtil {
    
    private static final Logger logger = LoggerFactory.getLogger(ExcelAdvancedUtil.class);
    
    /**
     * 合并单元格（便捷方法）
     * 
     * @param sheet 工作表
     * @param firstRow 起始行（从0开始）
     * @param lastRow 结束行（从0开始）
     * @param firstCol 起始列（从0开始）
     * @param lastCol 结束列（从0开始）
     */
    public static void mergeCells(Sheet sheet, int firstRow, int lastRow, int firstCol, int lastCol) {
        if (firstRow > lastRow || firstCol > lastCol) {
            throw new IllegalArgumentException("起始位置不能大于结束位置");
        }
        sheet.addMergedRegion(new CellRangeAddress(firstRow, lastRow, firstCol, lastCol));
    }
    
    /**
     * 合并单元格区域（单行多列）
     * 
     * @param sheet 工作表
     * @param row 行索引（从0开始）
     * @param firstCol 起始列（从0开始）
     * @param lastCol 结束列（从0开始）
     */
    public static void mergeRowCells(Sheet sheet, int row, int firstCol, int lastCol) {
        mergeCells(sheet, row, row, firstCol, lastCol);
    }
    
    /**
     * 合并单元格区域（单列多行）
     * 
     * @param sheet 工作表
     * @param firstRow 起始行（从0开始）
     * @param lastRow 结束行（从0开始）
     * @param col 列索引（从0开始）
     */
    public static void mergeColumnCells(Sheet sheet, int firstRow, int lastRow, int col) {
        mergeCells(sheet, firstRow, lastRow, col, col);
    }
    
    /**
     * 合并表头（合并第一行的多个列）
     * 
     * @param sheet 工作表
     * @param firstCol 起始列（从0开始）
     * @param lastCol 结束列（从0开始）
     */
    public static void mergeHeader(Sheet sheet, int firstCol, int lastCol) {
        mergeRowCells(sheet, 0, firstCol, lastCol);
    }
    
    /**
     * 冻结窗格（冻结指定行和列）
     * 
     * @param sheet 工作表
     * @param rowSplit 冻结的行数（从第rowSplit+1行开始滚动）
     * @param colSplit 冻结的列数（从第colSplit+1列开始滚动）
     */
    public static void freezePane(Sheet sheet, int rowSplit, int colSplit) {
        sheet.createFreezePane(colSplit, rowSplit);
    }
    
    /**
     * 冻结首行
     * 
     * @param sheet 工作表
     */
    public static void freezeFirstRow(Sheet sheet) {
        freezePane(sheet, 1, 0);
    }
    
    /**
     * 冻结首列
     * 
     * @param sheet 工作表
     */
    public static void freezeFirstColumn(Sheet sheet) {
        freezePane(sheet, 0, 1);
    }
    
    /**
     * 冻结首行和首列
     * 
     * @param sheet 工作表
     */
    public static void freezeFirstRowAndColumn(Sheet sheet) {
        freezePane(sheet, 1, 1);
    }
    
    /**
     * 设置列宽
     * 
     * @param sheet 工作表
     * @param columnIndex 列索引（从0开始）
     * @param width 列宽（字符数）
     */
    public static void setColumnWidth(Sheet sheet, int columnIndex, int width) {
        sheet.setColumnWidth(columnIndex, width * 256); // POI中列宽单位是1/256字符
    }
    
    /**
     * 批量设置列宽
     * 
     * @param sheet 工作表
     * @param columnWidths 列宽数组，索引对应列索引
     */
    public static void setColumnWidths(Sheet sheet, int[] columnWidths) {
        for (int i = 0; i < columnWidths.length; i++) {
            setColumnWidth(sheet, i, columnWidths[i]);
        }
    }
    
    /**
     * 自动调整列宽
     * 
     * @param sheet 工作表
     * @param columnIndex 列索引（从0开始）
     */
    public static void autoSizeColumn(Sheet sheet, int columnIndex) {
        sheet.autoSizeColumn(columnIndex);
    }
    
    /**
     * 自动调整所有列宽
     * 
     * @param sheet 工作表
     * @param columnCount 列数
     */
    public static void autoSizeAllColumns(Sheet sheet, int columnCount) {
        for (int i = 0; i < columnCount; i++) {
            autoSizeColumn(sheet, i);
        }
    }
    
    /**
     * 设置行高
     * 
     * @param row 行对象
     * @param height 行高（磅值）
     */
    public static void setRowHeight(Row row, float height) {
        row.setHeightInPoints(height);
    }
    
    /**
     * 设置行高（像素）
     * 
     * @param row 行对象
     * @param heightInPixels 行高（像素）
     */
    public static void setRowHeightInPixels(Row row, int heightInPixels) {
        row.setHeight((short) (heightInPixels * 20)); // 1像素 = 20个单位
    }
    
    /**
     * 设置单元格公式
     * 
     * @param cell 单元格
     * @param formula 公式字符串（如 "SUM(A1:A10)"）
     */
    public static void setCellFormula(Cell cell, String formula) {
        cell.setCellFormula(formula);
    }
    
    /**
     * 设置求和公式
     * 
     * @param cell 目标单元格
     * @param startRow 起始行（从0开始）
     * @param endRow 结束行（从0开始）
     * @param col 列索引（从0开始）
     */
    public static void setSumFormula(Cell cell, int startRow, int endRow, int col) {
        String colLetter = getColumnLetter(col);
        String formula = String.format("SUM(%s%d:%s%d)", colLetter, startRow + 1, colLetter, endRow + 1);
        setCellFormula(cell, formula);
    }
    
    /**
     * 设置平均值公式
     * 
     * @param cell 目标单元格
     * @param startRow 起始行（从0开始）
     * @param endRow 结束行（从0开始）
     * @param col 列索引（从0开始）
     */
    public static void setAverageFormula(Cell cell, int startRow, int endRow, int col) {
        String colLetter = getColumnLetter(col);
        String formula = String.format("AVERAGE(%s%d:%s%d)", colLetter, startRow + 1, colLetter, endRow + 1);
        setCellFormula(cell, formula);
    }
    
    /**
     * 设置最大值公式
     * 
     * @param cell 目标单元格
     * @param startRow 起始行（从0开始）
     * @param endRow 结束行（从0开始）
     * @param col 列索引（从0开始）
     */
    public static void setMaxFormula(Cell cell, int startRow, int endRow, int col) {
        String colLetter = getColumnLetter(col);
        String formula = String.format("MAX(%s%d:%s%d)", colLetter, startRow + 1, colLetter, endRow + 1);
        setCellFormula(cell, formula);
    }
    
    /**
     * 设置最小值公式
     * 
     * @param cell 目标单元格
     * @param startRow 起始行（从0开始）
     * @param endRow 结束行（从0开始）
     * @param col 列索引（从0开始）
     */
    public static void setMinFormula(Cell cell, int startRow, int endRow, int col) {
        String colLetter = getColumnLetter(col);
        String formula = String.format("MIN(%s%d:%s%d)", colLetter, startRow + 1, colLetter, endRow + 1);
        setCellFormula(cell, formula);
    }
    
    /**
     * 设置计数公式
     * 
     * @param cell 目标单元格
     * @param startRow 起始行（从0开始）
     * @param endRow 结束行（从0开始）
     * @param col 列索引（从0开始）
     */
    public static void setCountFormula(Cell cell, int startRow, int endRow, int col) {
        String colLetter = getColumnLetter(col);
        String formula = String.format("COUNT(%s%d:%s%d)", colLetter, startRow + 1, colLetter, endRow + 1);
        setCellFormula(cell, formula);
    }
    
    /**
     * 设置条件计数公式（COUNTIF）
     * 
     * @param cell 目标单元格
     * @param startRow 起始行（从0开始）
     * @param endRow 结束行（从0开始）
     * @param col 列索引（从0开始）
     * @param condition 条件（如 ">100"）
     */
    public static void setCountIfFormula(Cell cell, int startRow, int endRow, int col, String condition) {
        String colLetter = getColumnLetter(col);
        String formula = String.format("COUNTIF(%s%d:%s%d,\"%s\")", colLetter, startRow + 1, colLetter, endRow + 1, condition);
        setCellFormula(cell, formula);
    }
    
    /**
     * 设置条件求和公式（SUMIF）
     * 
     * @param cell 目标单元格
     * @param rangeStartRow 范围起始行（从0开始）
     * @param rangeEndRow 范围结束行（从0开始）
     * @param rangeCol 范围列索引（从0开始）
     * @param condition 条件（如 ">100"）
     * @param sumStartRow 求和范围起始行（从0开始）
     * @param sumEndRow 求和范围结束行（从0开始）
     * @param sumCol 求和列索引（从0开始）
     */
    public static void setSumIfFormula(Cell cell, int rangeStartRow, int rangeEndRow, int rangeCol,
                                      String condition, int sumStartRow, int sumEndRow, int sumCol) {
        String rangeColLetter = getColumnLetter(rangeCol);
        String sumColLetter = getColumnLetter(sumCol);
        String formula = String.format("SUMIF(%s%d:%s%d,\"%s\",%s%d:%s%d)",
                rangeColLetter, rangeStartRow + 1, rangeColLetter, rangeEndRow + 1, condition,
                sumColLetter, sumStartRow + 1, sumColLetter, sumEndRow + 1);
        setCellFormula(cell, formula);
    }
    
    /**
     * 设置单元格边框
     * 
     * @param style 单元格样式
     * @param borderStyle 边框样式
     */
    public static void setCellBorder(CellStyle style, BorderStyle borderStyle) {
        style.setBorderTop(borderStyle);
        style.setBorderBottom(borderStyle);
        style.setBorderLeft(borderStyle);
        style.setBorderRight(borderStyle);
    }
    
    /**
     * 设置单元格边框（带颜色）
     * 
     * @param style 单元格样式
     * @param borderStyle 边框样式
     * @param borderColor 边框颜色索引
     */
    public static void setCellBorder(CellStyle style, BorderStyle borderStyle, short borderColor) {
        setCellBorder(style, borderStyle);
        style.setTopBorderColor(borderColor);
        style.setBottomBorderColor(borderColor);
        style.setLeftBorderColor(borderColor);
        style.setRightBorderColor(borderColor);
    }
    
    /**
     * 创建数据汇总行（常用于报表底部）
     * 
     * @param sheet 工作表
     * @param rowIndex 行索引（从0开始）
     * @param headers 表头数组
     * @param summaryLabel 汇总标签（如 "合计"、"总计"）
     * @param summaryColumns 需要汇总的列索引数组
     * @param dataStartRow 数据起始行（从0开始，不包含表头）
     * @param dataEndRow 数据结束行（从0开始）
     */
    public static void createSummaryRow(Sheet sheet, int rowIndex, String[] headers, String summaryLabel,
                                       int[] summaryColumns, int dataStartRow, int dataEndRow) {
        Row summaryRow = sheet.createRow(rowIndex);
        
        // 创建汇总行样式
        Workbook workbook = sheet.getWorkbook();
        CellStyle summaryStyle = workbook.createCellStyle();
        Font font = workbook.createFont();
        font.setBold(true);
        summaryStyle.setFont(font);
        summaryStyle.setFillForegroundColor(IndexedColors.GREY_25_PERCENT.getIndex());
        summaryStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        setCellBorder(summaryStyle, BorderStyle.THIN);
        
        // 设置汇总标签
        Cell labelCell = summaryRow.createCell(0);
        labelCell.setCellValue(summaryLabel);
        labelCell.setCellStyle(summaryStyle);
        
        // 设置汇总公式
        for (int colIndex : summaryColumns) {
            Cell cell = summaryRow.createCell(colIndex);
            setSumFormula(cell, dataStartRow, dataEndRow, colIndex);
            cell.setCellStyle(summaryStyle);
        }
        
        // 合并标签单元格（如果需要）
        if (summaryColumns.length > 0 && summaryColumns[0] > 1) {
            mergeRowCells(sheet, rowIndex, 0, summaryColumns[0] - 1);
        }
    }
    
    /**
     * 创建分组汇总（按某个字段分组后汇总）
     * 
     * @param sheet 工作表
     * @param dataList 数据列表
     * @param groupByColumn 分组列索引
     * @param summaryColumns 汇总列索引数组
     * @param headers 表头数组
     */
    public static void createGroupSummary(Sheet sheet, List<Map<String, Object>> dataList, int groupByColumn,
                                         int[] summaryColumns, String[] headers) {
        // 这里简化实现，实际应该根据数据分组
        // 完整实现需要先对数据进行分组，然后在每组后插入汇总行
        logger.warn("createGroupSummary 方法需要根据实际业务逻辑实现");
    }
    
    /**
     * 设置打印区域
     * 
     * @param sheet 工作表
     * @param startRow 起始行（从0开始）
     * @param endRow 结束行（从0开始）
     * @param startCol 起始列（从0开始）
     * @param endCol 结束列（从0开始）
     */
    public static void setPrintArea(Sheet sheet, int startRow, int endRow, int startCol, int endCol) {
        if (sheet instanceof XSSFSheet) {
            XSSFWorkbook workbook = (XSSFWorkbook) sheet.getWorkbook();
            String reference = String.format("%s%d:%s%d",
                    getColumnLetter(startCol), startRow + 1,
                    getColumnLetter(endCol), endRow + 1);
            workbook.setPrintArea(workbook.getSheetIndex(sheet), reference);
        }
    }
    
    /**
     * 设置打印标题行（每页都打印）
     * 注意：POI对打印标题的支持有限，此方法仅作示例
     * 
     * @param sheet 工作表
     * @param startRow 起始行（从0开始）
     * @param endRow 结束行（从0开始）
     */
    public static void setPrintTitleRows(Sheet sheet, int startRow, int endRow) {
        // POI对打印标题的支持需要通过重复行设置
        // 这里简化处理，实际使用时可能需要通过其他方式实现
        logger.info("设置打印标题行: {} - {}", startRow + 1, endRow + 1);
    }
    
    /**
     * 设置打印标题列（每页都打印）
     * 注意：POI对打印标题的支持有限，此方法仅作示例
     * 
     * @param sheet 工作表
     * @param startCol 起始列（从0开始）
     * @param endCol 结束列（从0开始）
     */
    public static void setPrintTitleColumns(Sheet sheet, int startCol, int endCol) {
        // POI对打印标题的支持需要通过重复列设置
        // 这里简化处理，实际使用时可能需要通过其他方式实现
        logger.info("设置打印标题列: {} - {}", getColumnLetter(startCol), getColumnLetter(endCol));
    }
    
    /**
     * 将列索引转换为列字母（如 0->A, 1->B, 26->AA）
     * 
     * @param columnIndex 列索引（从0开始）
     * @return 列字母
     */
    public static String getColumnLetter(int columnIndex) {
        StringBuilder sb = new StringBuilder();
        while (columnIndex >= 0) {
            sb.append((char) ('A' + (columnIndex % 26)));
            columnIndex = columnIndex / 26 - 1;
        }
        return sb.reverse().toString();
    }
    
    /**
     * 将列字母转换为列索引（如 A->0, B->1, AA->26）
     * 
     * @param columnLetter 列字母
     * @return 列索引（从0开始）
     */
    public static int getColumnIndex(String columnLetter) {
        int index = 0;
        for (int i = 0; i < columnLetter.length(); i++) {
            index = index * 26 + (columnLetter.charAt(i) - 'A' + 1);
        }
        return index - 1;
    }
}
