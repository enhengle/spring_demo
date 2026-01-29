package com.practise.demo.util;

import org.apache.poi.ss.usermodel.*;

import java.util.*;

/**
 * Excel 数据仓库/业务常用工具类
 * 提供数据仓库和业务场景中常用的Excel操作方法
 * 
 * @author system
 */
public class ExcelDataWarehouseUtil {
    
    /**
     * 创建数据字典表
     * 用于展示字段说明、数据类型、示例值等
     * 
     * @param sheet 工作表
     * @param dictionaryData 字典数据，Map的key为字段名，value为字段信息Map（包含：说明、类型、示例等）
     * @param startRow 起始行（从0开始）
     */
    public static void createDataDictionary(Sheet sheet, Map<String, Map<String, String>> dictionaryData, int startRow) {
        Workbook workbook = sheet.getWorkbook();
        
        // 创建表头
        Row headerRow = sheet.createRow(startRow);
        String[] headers = {"字段名", "字段说明", "数据类型", "是否必填", "示例值", "备注"};
        CellStyle headerStyle = createHeaderStyle(workbook);
        
        for (int i = 0; i < headers.length; i++) {
            Cell cell = headerRow.createCell(i);
            cell.setCellValue(headers[i]);
            cell.setCellStyle(headerStyle);
        }
        
        // 创建数据行
        int rowIndex = startRow + 1;
        for (Map.Entry<String, Map<String, String>> entry : dictionaryData.entrySet()) {
            Row row = sheet.createRow(rowIndex++);
            String fieldName = entry.getKey();
            Map<String, String> fieldInfo = entry.getValue();
            
            row.createCell(0).setCellValue(fieldName);
            row.createCell(1).setCellValue(fieldInfo.getOrDefault("说明", ""));
            row.createCell(2).setCellValue(fieldInfo.getOrDefault("类型", ""));
            row.createCell(3).setCellValue(fieldInfo.getOrDefault("必填", ""));
            row.createCell(4).setCellValue(fieldInfo.getOrDefault("示例", ""));
            row.createCell(5).setCellValue(fieldInfo.getOrDefault("备注", ""));
        }
        
        // 自动调整列宽
        for (int i = 0; i < headers.length; i++) {
            sheet.autoSizeColumn(i);
        }
    }
    
    /**
     * 创建数据质量报告
     * 包含数据量、空值统计、重复值统计等
     * 
     * @param sheet 工作表
     * @param qualityData 质量数据，包含：总行数、空值数、重复数等
     * @param startRow 起始行（从0开始）
     */
    public static void createDataQualityReport(Sheet sheet, Map<String, Object> qualityData, int startRow) {
        Workbook workbook = sheet.getWorkbook();
        
        // 创建标题
        Row titleRow = sheet.createRow(startRow);
        Cell titleCell = titleRow.createCell(0);
        titleCell.setCellValue("数据质量报告");
        CellStyle titleStyle = workbook.createCellStyle();
        Font titleFont = workbook.createFont();
        titleFont.setBold(true);
        titleFont.setFontHeightInPoints((short) 14);
        titleStyle.setFont(titleFont);
        titleCell.setCellStyle(titleStyle);
        ExcelAdvancedUtil.mergeRowCells(sheet, startRow, 0, 3);
        
        // 创建统计信息
        int rowIndex = startRow + 2;
        String[] labels = {"总记录数", "有效记录数", "空值记录数", "重复记录数", "数据完整率", "数据准确率"};
        String[] keys = {"totalCount", "validCount", "nullCount", "duplicateCount", "completeness", "accuracy"};
        
        for (int i = 0; i < labels.length; i++) {
            Row row = sheet.createRow(rowIndex++);
            row.createCell(0).setCellValue(labels[i]);
            Object value = qualityData.get(keys[i]);
            if (value != null) {
                row.createCell(1).setCellValue(value.toString());
            }
        }
    }
    
    /**
     * 创建对比报表（对比两个时间段的数据）
     * 
     * @param sheet 工作表
     * @param period1Data 第一期数据
     * @param period2Data 第二期数据
     * @param period1Label 第一期标签
     * @param period2Label 第二期标签
     * @param headers 表头数组
     * @param startRow 起始行（从0开始）
     */
    public static void createComparisonReport(Sheet sheet, List<Map<String, Object>> period1Data,
                                             List<Map<String, Object>> period2Data, String period1Label,
                                             String period2Label, String[] headers, int startRow) {
        Workbook workbook = sheet.getWorkbook();
        
        // 创建表头
        Row headerRow = sheet.createRow(startRow);
        CellStyle headerStyle = createHeaderStyle(workbook);
        
        // 第一列：指标名称
        Cell indicatorCell = headerRow.createCell(0);
        indicatorCell.setCellValue("指标");
        indicatorCell.setCellStyle(headerStyle);
        
        // 合并表头：第一期
        int period1StartCol = 1;
        int period1EndCol = headers.length;
        Cell period1Cell = headerRow.createCell(period1StartCol);
        period1Cell.setCellValue(period1Label);
        period1Cell.setCellStyle(headerStyle);
        ExcelAdvancedUtil.mergeRowCells(sheet, startRow, period1StartCol, period1EndCol);
        
        // 合并表头：第二期
        int period2StartCol = period1EndCol + 1;
        int period2EndCol = period2StartCol + headers.length - 1;
        Cell period2Cell = headerRow.createCell(period2StartCol);
        period2Cell.setCellValue(period2Label);
        period2Cell.setCellStyle(headerStyle);
        ExcelAdvancedUtil.mergeRowCells(sheet, startRow, period2StartCol, period2EndCol);
        
        // 创建子表头
        Row subHeaderRow = sheet.createRow(startRow + 1);
        for (int i = 0; i < headers.length; i++) {
            Cell cell = subHeaderRow.createCell(i + 1);
            cell.setCellValue(headers[i]);
            cell.setCellStyle(headerStyle);
        }
        for (int i = 0; i < headers.length; i++) {
            Cell cell = subHeaderRow.createCell(i + period2StartCol);
            cell.setCellValue(headers[i]);
            cell.setCellStyle(headerStyle);
        }
        
        // 创建数据行（简化实现，假设两期数据行数相同）
        int dataStartRow = startRow + 2;
        int maxRows = Math.max(period1Data.size(), period2Data.size());
        
        for (int i = 0; i < maxRows; i++) {
            Row row = sheet.createRow(dataStartRow + i);
            
            // 指标名称（使用第一个数据的key）
            if (i < period1Data.size() && !period1Data.get(i).isEmpty()) {
                String indicator = period1Data.get(i).keySet().iterator().next();
                row.createCell(0).setCellValue(indicator);
            }
            
            // 第一期数据
            if (i < period1Data.size()) {
                Map<String, Object> rowData = period1Data.get(i);
                int colIndex = 1;
                for (String header : headers) {
                    Object value = rowData.get(header);
                    if (value != null) {
                        row.createCell(colIndex++).setCellValue(value.toString());
                    } else {
                        colIndex++;
                    }
                }
            }
            
            // 第二期数据
            if (i < period2Data.size()) {
                Map<String, Object> rowData = period2Data.get(i);
                int colIndex = period2StartCol;
                for (String header : headers) {
                    Object value = rowData.get(header);
                    if (value != null) {
                        row.createCell(colIndex++).setCellValue(value.toString());
                    } else {
                        colIndex++;
                    }
                }
            }
        }
    }
    
    /**
     * 创建趋势分析报表（时间序列数据）
     * 
     * @param sheet 工作表
     * @param timeSeriesData 时间序列数据，key为时间，value为数据Map
     * @param headers 表头数组
     * @param startRow 起始行（从0开始）
     */
    public static void createTrendAnalysisReport(Sheet sheet, Map<String, Map<String, Object>> timeSeriesData,
                                                 String[] headers, int startRow) {
        Workbook workbook = sheet.getWorkbook();
        
        // 创建表头
        Row headerRow = sheet.createRow(startRow);
        CellStyle headerStyle = createHeaderStyle(workbook);
        
        headerRow.createCell(0).setCellValue("时间");
        headerRow.getCell(0).setCellStyle(headerStyle);
        
        for (int i = 0; i < headers.length; i++) {
            Cell cell = headerRow.createCell(i + 1);
            cell.setCellValue(headers[i]);
            cell.setCellStyle(headerStyle);
        }
        
        // 创建数据行（按时间排序）
        List<String> sortedTimes = new ArrayList<>(timeSeriesData.keySet());
        Collections.sort(sortedTimes);
        
        int rowIndex = startRow + 1;
        for (String time : sortedTimes) {
            Row row = sheet.createRow(rowIndex++);
            row.createCell(0).setCellValue(time);
            
            Map<String, Object> data = timeSeriesData.get(time);
            for (int i = 0; i < headers.length; i++) {
                Object value = data.get(headers[i]);
                if (value != null) {
                    row.createCell(i + 1).setCellValue(value.toString());
                }
            }
        }
    }
    
    /**
     * 创建数据导出模板
     * 包含表头、示例数据、数据验证规则说明
     * 
     * @param sheet 工作表
     * @param headers 表头数组
     * @param sampleData 示例数据
     * @param validationRules 验证规则说明（可选）
     * @param startRow 起始行（从0开始）
     */
    public static void createExportTemplate(Sheet sheet, String[] headers, Map<String, Object> sampleData,
                                           Map<String, String> validationRules, int startRow) {
        Workbook workbook = sheet.getWorkbook();
        
        // 创建说明行
        Row instructionRow = sheet.createRow(startRow);
        Cell instructionCell = instructionRow.createCell(0);
        instructionCell.setCellValue("请按照以下格式填写数据，示例数据仅供参考");
        CellStyle instructionStyle = workbook.createCellStyle();
        Font instructionFont = workbook.createFont();
        instructionFont.setColor(IndexedColors.RED.getIndex());
        instructionStyle.setFont(instructionFont);
        instructionCell.setCellStyle(instructionStyle);
        ExcelAdvancedUtil.mergeRowCells(sheet, startRow, 0, headers.length - 1);
        
        // 创建表头
        Row headerRow = sheet.createRow(startRow + 2);
        CellStyle headerStyle = createHeaderStyle(workbook);
        
        for (int i = 0; i < headers.length; i++) {
            Cell cell = headerRow.createCell(i);
            cell.setCellValue(headers[i]);
            cell.setCellStyle(headerStyle);
        }
        
        // 创建示例数据行
        if (sampleData != null && !sampleData.isEmpty()) {
            Row sampleRow = sheet.createRow(startRow + 3);
            CellStyle sampleStyle = workbook.createCellStyle();
            Font sampleFont = workbook.createFont();
            sampleFont.setItalic(true);
            sampleFont.setColor(IndexedColors.GREY_50_PERCENT.getIndex());
            sampleStyle.setFont(sampleFont);
            
            for (int i = 0; i < headers.length; i++) {
                Cell cell = sampleRow.createCell(i);
                Object value = sampleData.get(headers[i]);
                if (value != null) {
                    cell.setCellValue(value.toString());
                }
                cell.setCellStyle(sampleStyle);
            }
        }
        
        // 创建验证规则说明（如果有）
        if (validationRules != null && !validationRules.isEmpty()) {
            int ruleStartRow = startRow + 5;
            Row ruleTitleRow = sheet.createRow(ruleStartRow);
            ruleTitleRow.createCell(0).setCellValue("数据验证规则：");
            
            int ruleRowIndex = ruleStartRow + 1;
            for (Map.Entry<String, String> entry : validationRules.entrySet()) {
                Row ruleRow = sheet.createRow(ruleRowIndex++);
                ruleRow.createCell(0).setCellValue(entry.getKey() + ":");
                ruleRow.createCell(1).setCellValue(entry.getValue());
            }
        }
    }
    
    /**
     * 创建数据统计报表
     * 包含总计、平均值、最大值、最小值等统计信息
     * 
     * @param sheet 工作表
     * @param dataList 数据列表
     * @param headers 表头数组
     * @param numericColumns 数值列索引数组
     * @param startRow 起始行（从0开始）
     */
    public static void createStatisticsReport(Sheet sheet, List<Map<String, Object>> dataList, String[] headers,
                                             int[] numericColumns, int startRow) {
        Workbook workbook = sheet.getWorkbook();
        
        // 创建表头
        Row headerRow = sheet.createRow(startRow);
        CellStyle headerStyle = createHeaderStyle(workbook);
        
        for (int i = 0; i < headers.length; i++) {
            Cell cell = headerRow.createCell(i);
            cell.setCellValue(headers[i]);
            cell.setCellStyle(headerStyle);
        }
        
        // 创建数据行
        int dataStartRow = startRow + 1;
        for (int i = 0; i < dataList.size(); i++) {
            Row row = sheet.createRow(dataStartRow + i);
            Map<String, Object> rowData = dataList.get(i);
            
            for (int j = 0; j < headers.length; j++) {
                Object value = rowData.get(headers[j]);
                if (value != null) {
                    row.createCell(j).setCellValue(value.toString());
                }
            }
        }
        
        // 创建统计行
        int statsStartRow = dataStartRow + dataList.size() + 1;
        String[] statLabels = {"总计", "平均值", "最大值", "最小值"};
        
        for (int i = 0; i < statLabels.length; i++) {
            Row statsRow = sheet.createRow(statsStartRow + i);
            Cell labelCell = statsRow.createCell(0);
            labelCell.setCellValue(statLabels[i]);
            
            CellStyle labelStyle = workbook.createCellStyle();
            Font labelFont = workbook.createFont();
            labelFont.setBold(true);
            labelStyle.setFont(labelFont);
            labelCell.setCellStyle(labelStyle);
            
            // 为每个数值列创建统计公式
            for (int colIndex : numericColumns) {
                Cell statCell = statsRow.createCell(colIndex);
                int dataEndRow = dataStartRow + dataList.size() - 1;
                
                switch (i) {
                    case 0: // 总计
                        ExcelAdvancedUtil.setSumFormula(statCell, dataStartRow, dataEndRow, colIndex);
                        break;
                    case 1: // 平均值
                        ExcelAdvancedUtil.setAverageFormula(statCell, dataStartRow, dataEndRow, colIndex);
                        break;
                    case 2: // 最大值
                        ExcelAdvancedUtil.setMaxFormula(statCell, dataStartRow, dataEndRow, colIndex);
                        break;
                    case 3: // 最小值
                        ExcelAdvancedUtil.setMinFormula(statCell, dataStartRow, dataEndRow, colIndex);
                        break;
                }
                
                CellStyle statStyle = workbook.createCellStyle();
                Font statFont = workbook.createFont();
                statFont.setBold(true);
                statStyle.setFont(statFont);
                statCell.setCellStyle(statStyle);
            }
        }
    }
    
    /**
     * 创建表头样式（辅助方法）
     */
    private static CellStyle createHeaderStyle(Workbook workbook) {
        CellStyle style = workbook.createCellStyle();
        Font font = workbook.createFont();
        font.setBold(true);
        font.setFontHeightInPoints((short) 12);
        style.setFont(font);
        style.setAlignment(HorizontalAlignment.CENTER);
        style.setFillForegroundColor(IndexedColors.GREY_25_PERCENT.getIndex());
        style.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        ExcelAdvancedUtil.setCellBorder(style, BorderStyle.THIN);
        return style;
    }
}
