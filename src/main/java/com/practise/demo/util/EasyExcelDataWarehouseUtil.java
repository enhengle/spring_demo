package com.practise.demo.util;

import com.alibaba.excel.EasyExcel;
import com.alibaba.excel.ExcelWriter;
import com.alibaba.excel.write.metadata.WriteSheet;
import com.alibaba.excel.write.metadata.style.WriteCellStyle;
import com.alibaba.excel.write.metadata.style.WriteFont;
import com.alibaba.excel.write.style.HorizontalCellStyleStrategy;
import com.alibaba.excel.write.style.column.LongestMatchColumnWidthStyleStrategy;
import org.apache.poi.ss.usermodel.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

/**
 * EasyExcel 数仓和业务常用功能工具类
 * 提供数据仓库和业务场景常用的Excel功能
 * 
 * @author system
 */
public class EasyExcelDataWarehouseUtil {
    
    private static final Logger logger = LoggerFactory.getLogger(EasyExcelDataWarehouseUtil.class);
    
    /**
     * 生成数据汇总报表（多Sheet，包含汇总、明细、统计）
     * 
     * @param filePath 输出文件路径
     * @param summaryData 汇总数据
     * @param detailData 明细数据
     * @param statisticsData 统计数据
     */
    public static void generateSummaryReport(String filePath,
                                            EasyExcelAdvancedUtil.MapSheetData summaryData,
                                            EasyExcelAdvancedUtil.MapSheetData detailData,
                                            EasyExcelAdvancedUtil.MapSheetData statisticsData) {
        try (ExcelWriter excelWriter = EasyExcel.write(filePath).build()) {
            // Sheet 1: 汇总
            WriteSheet summarySheet = EasyExcel.writerSheet(0, "汇总")
                    .head(summaryData.getHead())
                    .registerWriteHandler(createSummaryStyle())
                    .registerWriteHandler(new LongestMatchColumnWidthStyleStrategy())
                    .build();
            excelWriter.write(summaryData.getDataList(), summarySheet);
            
            // Sheet 2: 明细
            WriteSheet detailSheet = EasyExcel.writerSheet(1, "明细")
                    .head(detailData.getHead())
                    .registerWriteHandler(createDetailStyle())
                    .registerWriteHandler(new LongestMatchColumnWidthStyleStrategy())
                    .build();
            excelWriter.write(detailData.getDataList(), detailSheet);
            
            // Sheet 3: 统计
            WriteSheet statisticsSheet = EasyExcel.writerSheet(2, "统计")
                    .head(statisticsData.getHead())
                    .registerWriteHandler(createStatisticsStyle())
                    .registerWriteHandler(new LongestMatchColumnWidthStyleStrategy())
                    .build();
            excelWriter.write(statisticsData.getDataList(), statisticsSheet);
            
        } catch (Exception e) {
            logger.error("生成汇总报表失败: {}", filePath, e);
            throw new RuntimeException("生成汇总报表失败: " + e.getMessage(), e);
        }
    }
    
    /**
     * 生成分组汇总报表（按维度分组）
     * 
     * @param filePath 输出文件路径
     * @param groupDataMap 分组数据映射，key为分组名称，value为数据
     */
    public static void generateGroupSummaryReport(String filePath,
                                                 Map<String, EasyExcelAdvancedUtil.MapSheetData> groupDataMap) {
        try (ExcelWriter excelWriter = EasyExcel.write(filePath).build()) {
            int sheetIndex = 0;
            for (Map.Entry<String, EasyExcelAdvancedUtil.MapSheetData> entry : groupDataMap.entrySet()) {
                String groupName = entry.getKey();
                EasyExcelAdvancedUtil.MapSheetData sheetData = entry.getValue();
                
                WriteSheet writeSheet = EasyExcel.writerSheet(sheetIndex++, groupName)
                        .head(sheetData.getHead())
                        .registerWriteHandler(createGroupStyle())
                        .registerWriteHandler(new LongestMatchColumnWidthStyleStrategy())
                        .build();
                
                excelWriter.write(sheetData.getDataList(), writeSheet);
            }
        } catch (Exception e) {
            logger.error("生成分组汇总报表失败: {}", filePath, e);
            throw new RuntimeException("生成分组汇总报表失败: " + e.getMessage(), e);
        }
    }
    
    /**
     * 生成数据对比报表（对比不同时间段或不同维度的数据）
     * 
     * @param filePath 输出文件路径
     * @param comparisonDataMap 对比数据映射
     */
    public static void generateComparisonReport(String filePath,
                                               Map<String, EasyExcelAdvancedUtil.MapSheetData> comparisonDataMap) {
        try (ExcelWriter excelWriter = EasyExcel.write(filePath).build()) {
            int sheetIndex = 0;
            for (Map.Entry<String, EasyExcelAdvancedUtil.MapSheetData> entry : comparisonDataMap.entrySet()) {
                String periodName = entry.getKey();
                EasyExcelAdvancedUtil.MapSheetData sheetData = entry.getValue();
                
                WriteSheet writeSheet = EasyExcel.writerSheet(sheetIndex++, periodName)
                        .head(sheetData.getHead())
                        .registerWriteHandler(createComparisonStyle())
                        .registerWriteHandler(new LongestMatchColumnWidthStyleStrategy())
                        .build();
                
                excelWriter.write(sheetData.getDataList(), writeSheet);
            }
        } catch (Exception e) {
            logger.error("生成对比报表失败: {}", filePath, e);
            throw new RuntimeException("生成对比报表失败: " + e.getMessage(), e);
        }
    }
    
    /**
     * 创建汇总样式（标题行样式）
     */
    private static HorizontalCellStyleStrategy createSummaryStyle() {
        WriteCellStyle headWriteCellStyle = new WriteCellStyle();
        headWriteCellStyle.setFillForegroundColor(IndexedColors.DARK_BLUE.getIndex());
        headWriteCellStyle.setFillPatternType(FillPatternType.SOLID_FOREGROUND);
        headWriteCellStyle.setHorizontalAlignment(HorizontalAlignment.CENTER);
        headWriteCellStyle.setVerticalAlignment(VerticalAlignment.CENTER);
        headWriteCellStyle.setBorderTop(BorderStyle.THIN);
        headWriteCellStyle.setBorderBottom(BorderStyle.THIN);
        headWriteCellStyle.setBorderLeft(BorderStyle.THIN);
        headWriteCellStyle.setBorderRight(BorderStyle.THIN);
        
        WriteFont headWriteFont = new WriteFont();
        headWriteFont.setFontHeightInPoints((short) 12);
        headWriteFont.setBold(true);
        headWriteFont.setColor(IndexedColors.WHITE.getIndex());
        headWriteCellStyle.setWriteFont(headWriteFont);
        
        WriteCellStyle contentWriteCellStyle = new WriteCellStyle();
        contentWriteCellStyle.setHorizontalAlignment(HorizontalAlignment.CENTER);
        contentWriteCellStyle.setBorderTop(BorderStyle.THIN);
        contentWriteCellStyle.setBorderBottom(BorderStyle.THIN);
        contentWriteCellStyle.setBorderLeft(BorderStyle.THIN);
        contentWriteCellStyle.setBorderRight(BorderStyle.THIN);
        
        return new HorizontalCellStyleStrategy(headWriteCellStyle, contentWriteCellStyle);
    }
    
    /**
     * 创建明细样式
     */
    private static HorizontalCellStyleStrategy createDetailStyle() {
        WriteCellStyle headWriteCellStyle = new WriteCellStyle();
        headWriteCellStyle.setFillForegroundColor(IndexedColors.GREY_25_PERCENT.getIndex());
        headWriteCellStyle.setFillPatternType(FillPatternType.SOLID_FOREGROUND);
        headWriteCellStyle.setHorizontalAlignment(HorizontalAlignment.CENTER);
        headWriteCellStyle.setBorderTop(BorderStyle.THIN);
        headWriteCellStyle.setBorderBottom(BorderStyle.THIN);
        headWriteCellStyle.setBorderLeft(BorderStyle.THIN);
        headWriteCellStyle.setBorderRight(BorderStyle.THIN);
        
        WriteFont headWriteFont = new WriteFont();
        headWriteFont.setFontHeightInPoints((short) 11);
        headWriteFont.setBold(true);
        headWriteCellStyle.setWriteFont(headWriteFont);
        
        WriteCellStyle contentWriteCellStyle = new WriteCellStyle();
        contentWriteCellStyle.setHorizontalAlignment(HorizontalAlignment.LEFT);
        contentWriteCellStyle.setBorderTop(BorderStyle.THIN);
        contentWriteCellStyle.setBorderBottom(BorderStyle.THIN);
        contentWriteCellStyle.setBorderLeft(BorderStyle.THIN);
        contentWriteCellStyle.setBorderRight(BorderStyle.THIN);
        
        return new HorizontalCellStyleStrategy(headWriteCellStyle, contentWriteCellStyle);
    }
    
    /**
     * 创建统计样式
     */
    private static HorizontalCellStyleStrategy createStatisticsStyle() {
        WriteCellStyle headWriteCellStyle = new WriteCellStyle();
        headWriteCellStyle.setFillForegroundColor(IndexedColors.LIGHT_GREEN.getIndex());
        headWriteCellStyle.setFillPatternType(FillPatternType.SOLID_FOREGROUND);
        headWriteCellStyle.setHorizontalAlignment(HorizontalAlignment.CENTER);
        headWriteCellStyle.setBorderTop(BorderStyle.MEDIUM);
        headWriteCellStyle.setBorderBottom(BorderStyle.MEDIUM);
        headWriteCellStyle.setBorderLeft(BorderStyle.MEDIUM);
        headWriteCellStyle.setBorderRight(BorderStyle.MEDIUM);
        
        WriteFont headWriteFont = new WriteFont();
        headWriteFont.setFontHeightInPoints((short) 12);
        headWriteFont.setBold(true);
        headWriteFont.setColor(IndexedColors.DARK_GREEN.getIndex());
        headWriteCellStyle.setWriteFont(headWriteFont);
        
        WriteCellStyle contentWriteCellStyle = new WriteCellStyle();
        contentWriteCellStyle.setHorizontalAlignment(HorizontalAlignment.RIGHT);
        // 注意：EasyExcel的数据格式通过注解设置，这里只设置对齐方式
        contentWriteCellStyle.setBorderTop(BorderStyle.THIN);
        contentWriteCellStyle.setBorderBottom(BorderStyle.THIN);
        contentWriteCellStyle.setBorderLeft(BorderStyle.THIN);
        contentWriteCellStyle.setBorderRight(BorderStyle.THIN);
        
        return new HorizontalCellStyleStrategy(headWriteCellStyle, contentWriteCellStyle);
    }
    
    /**
     * 创建分组样式
     */
    private static HorizontalCellStyleStrategy createGroupStyle() {
        WriteCellStyle headWriteCellStyle = new WriteCellStyle();
        headWriteCellStyle.setFillForegroundColor(IndexedColors.LIGHT_BLUE.getIndex());
        headWriteCellStyle.setFillPatternType(FillPatternType.SOLID_FOREGROUND);
        headWriteCellStyle.setHorizontalAlignment(HorizontalAlignment.CENTER);
        
        WriteFont headWriteFont = new WriteFont();
        headWriteFont.setFontHeightInPoints((short) 11);
        headWriteFont.setBold(true);
        headWriteCellStyle.setWriteFont(headWriteFont);
        
        WriteCellStyle contentWriteCellStyle = new WriteCellStyle();
        contentWriteCellStyle.setHorizontalAlignment(HorizontalAlignment.LEFT);
        
        return new HorizontalCellStyleStrategy(headWriteCellStyle, contentWriteCellStyle);
    }
    
    /**
     * 创建对比样式
     */
    private static HorizontalCellStyleStrategy createComparisonStyle() {
        WriteCellStyle headWriteCellStyle = new WriteCellStyle();
        headWriteCellStyle.setFillForegroundColor(IndexedColors.LIGHT_YELLOW.getIndex());
        headWriteCellStyle.setFillPatternType(FillPatternType.SOLID_FOREGROUND);
        headWriteCellStyle.setHorizontalAlignment(HorizontalAlignment.CENTER);
        
        WriteFont headWriteFont = new WriteFont();
        headWriteFont.setFontHeightInPoints((short) 11);
        headWriteFont.setBold(true);
        headWriteCellStyle.setWriteFont(headWriteFont);
        
        WriteCellStyle contentWriteCellStyle = new WriteCellStyle();
        contentWriteCellStyle.setHorizontalAlignment(HorizontalAlignment.CENTER);
        
        return new HorizontalCellStyleStrategy(headWriteCellStyle, contentWriteCellStyle);
    }
    
    /**
     * 生成数据透视表风格的报表（多维度数据展示）
     * 
     * @param filePath 输出文件路径
     * @param pivotDataMap 透视数据映射
     */
    public static void generatePivotStyleReport(String filePath,
                                               Map<String, EasyExcelAdvancedUtil.MapSheetData> pivotDataMap) {
        try (ExcelWriter excelWriter = EasyExcel.write(filePath).build()) {
            int sheetIndex = 0;
            for (Map.Entry<String, EasyExcelAdvancedUtil.MapSheetData> entry : pivotDataMap.entrySet()) {
                String dimensionName = entry.getKey();
                EasyExcelAdvancedUtil.MapSheetData sheetData = entry.getValue();
                
                WriteSheet writeSheet = EasyExcel.writerSheet(sheetIndex++, dimensionName)
                        .head(sheetData.getHead())
                        .registerWriteHandler(createPivotStyle())
                        .registerWriteHandler(new LongestMatchColumnWidthStyleStrategy())
                        .build();
                
                excelWriter.write(sheetData.getDataList(), writeSheet);
            }
        } catch (Exception e) {
            logger.error("生成透视表风格报表失败: {}", filePath, e);
            throw new RuntimeException("生成透视表风格报表失败: " + e.getMessage(), e);
        }
    }
    
    /**
     * 创建透视表样式
     */
    private static HorizontalCellStyleStrategy createPivotStyle() {
        WriteCellStyle headWriteCellStyle = new WriteCellStyle();
        headWriteCellStyle.setFillForegroundColor(IndexedColors.DARK_BLUE.getIndex());
        headWriteCellStyle.setFillPatternType(FillPatternType.SOLID_FOREGROUND);
        headWriteCellStyle.setHorizontalAlignment(HorizontalAlignment.CENTER);
        headWriteCellStyle.setVerticalAlignment(VerticalAlignment.CENTER);
        
        WriteFont headWriteFont = new WriteFont();
        headWriteFont.setFontHeightInPoints((short) 11);
        headWriteFont.setBold(true);
        headWriteFont.setColor(IndexedColors.WHITE.getIndex());
        headWriteCellStyle.setWriteFont(headWriteFont);
        
        WriteCellStyle contentWriteCellStyle = new WriteCellStyle();
        contentWriteCellStyle.setHorizontalAlignment(HorizontalAlignment.RIGHT);
        // 注意：EasyExcel的数据格式通过注解设置
        
        return new HorizontalCellStyleStrategy(headWriteCellStyle, contentWriteCellStyle);
    }
}
