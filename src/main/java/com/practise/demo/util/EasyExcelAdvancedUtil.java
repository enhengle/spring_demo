package com.practise.demo.util;

import com.alibaba.excel.EasyExcel;
import com.alibaba.excel.ExcelWriter;
import com.alibaba.excel.write.metadata.WriteSheet;
import com.alibaba.excel.write.metadata.fill.FillConfig;
import com.alibaba.excel.write.metadata.style.WriteCellStyle;
import com.alibaba.excel.write.metadata.style.WriteFont;
import com.alibaba.excel.write.style.HorizontalCellStyleStrategy;
import com.alibaba.excel.write.style.column.LongestMatchColumnWidthStyleStrategy;
import org.apache.poi.ss.usermodel.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Map;

/**
 * EasyExcel 高级功能工具类
 * 提供多Sheet、单元格格式、合并、样式等数仓和业务常用功能
 * 
 * @author system
 */
public class EasyExcelAdvancedUtil {
    
    private static final Logger logger = LoggerFactory.getLogger(EasyExcelAdvancedUtil.class);
    
    /**
     * 生成多Sheet的Excel文件
     * 
     * @param filePath 输出文件路径
     * @param sheetDataMap Sheet数据映射，key为Sheet名称，value为数据列表和表头类
     */
    public static <T> void writeMultiSheetExcel(String filePath, 
                                               Map<String, SheetData<T>> sheetDataMap) {
        try (ExcelWriter excelWriter = EasyExcel.write(filePath).build()) {
            int sheetIndex = 0;
            for (Map.Entry<String, SheetData<T>> entry : sheetDataMap.entrySet()) {
                String sheetName = entry.getKey();
                SheetData<T> sheetData = entry.getValue();
                
                WriteSheet writeSheet = EasyExcel.writerSheet(sheetIndex++, sheetName)
                        .head(sheetData.getHeadClass())
                        .build();
                
                excelWriter.write(sheetData.getDataList(), writeSheet);
            }
        } catch (Exception e) {
            logger.error("生成多Sheet Excel文件失败: {}", filePath, e);
            throw new RuntimeException("生成多Sheet Excel文件失败: " + e.getMessage(), e);
        }
    }
    
    /**
     * 生成多Sheet的Excel文件（使用Map数据）
     * 
     * @param filePath 输出文件路径
     * @param sheetDataMap Sheet数据映射，key为Sheet名称，value为数据（表头和数据列表）
     */
    public static void writeMultiSheetExcelWithMap(String filePath,
                                                   Map<String, MapSheetData> sheetDataMap) {
        try (ExcelWriter excelWriter = EasyExcel.write(filePath).build()) {
            int sheetIndex = 0;
            for (Map.Entry<String, MapSheetData> entry : sheetDataMap.entrySet()) {
                String sheetName = entry.getKey();
                MapSheetData sheetData = entry.getValue();
                
                WriteSheet writeSheet = EasyExcel.writerSheet(sheetIndex++, sheetName)
                        .head(sheetData.getHead())
                        .build();
                
                excelWriter.write(sheetData.getDataList(), writeSheet);
            }
        } catch (Exception e) {
            logger.error("生成多Sheet Excel文件失败: {}", filePath, e);
            throw new RuntimeException("生成多Sheet Excel文件失败: " + e.getMessage(), e);
        }
    }
    
    /**
     * 写入Excel并设置单元格样式
     * 
     * @param filePath 输出文件路径
     * @param headClass 表头类
     * @param dataList 数据列表
     * @param styleStrategy 样式策略
     */
    public static <T> void writeExcelWithStyle(String filePath, Class<T> headClass,
                                              List<T> dataList, HorizontalCellStyleStrategy styleStrategy) {
        try {
            EasyExcel.write(filePath, headClass)
                    .registerWriteHandler(styleStrategy)
                    .registerWriteHandler(new LongestMatchColumnWidthStyleStrategy()) // 自动列宽
                    .sheet("Sheet1")
                    .doWrite(dataList);
        } catch (Exception e) {
            logger.error("写入Excel文件失败: {}", filePath, e);
            throw new RuntimeException("写入Excel文件失败: " + e.getMessage(), e);
        }
    }
    
    /**
     * 创建表头样式（标题样式）
     * 
     * @return 样式策略
     */
    public static HorizontalCellStyleStrategy createHeaderStyle() {
        // 表头样式
        WriteCellStyle headWriteCellStyle = new WriteCellStyle();
        headWriteCellStyle.setFillForegroundColor(IndexedColors.GREY_25_PERCENT.getIndex());
        headWriteCellStyle.setFillPatternType(FillPatternType.SOLID_FOREGROUND);
        headWriteCellStyle.setHorizontalAlignment(HorizontalAlignment.CENTER);
        headWriteCellStyle.setVerticalAlignment(VerticalAlignment.CENTER);
        
        // 表头字体
        WriteFont headWriteFont = new WriteFont();
        headWriteFont.setFontHeightInPoints((short) 12);
        headWriteFont.setBold(true);
        headWriteFont.setColor(IndexedColors.BLACK.getIndex());
        headWriteCellStyle.setWriteFont(headWriteFont);
        
        // 数据样式（默认）
        WriteCellStyle contentWriteCellStyle = new WriteCellStyle();
        contentWriteCellStyle.setHorizontalAlignment(HorizontalAlignment.LEFT);
        contentWriteCellStyle.setVerticalAlignment(VerticalAlignment.CENTER);
        
        return new HorizontalCellStyleStrategy(headWriteCellStyle, contentWriteCellStyle);
    }
    
    /**
     * 创建强调样式（用于重要数据）
     * 
     * @return 样式策略
     */
    public static HorizontalCellStyleStrategy createEmphasisStyle() {
        WriteCellStyle headWriteCellStyle = new WriteCellStyle();
        headWriteCellStyle.setFillForegroundColor(IndexedColors.LIGHT_BLUE.getIndex());
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
     * 创建数字格式样式
     * 
     * @param numberFormat 数字格式（如 "#,##0.00"）
     * @return 样式策略
     * 
     * 注意：EasyExcel 的数据格式主要通过注解设置，这里提供基础样式
     */
    public static HorizontalCellStyleStrategy createNumberFormatStyle(String numberFormat) {
        WriteCellStyle contentWriteCellStyle = new WriteCellStyle();
        contentWriteCellStyle.setHorizontalAlignment(HorizontalAlignment.RIGHT);
        contentWriteCellStyle.setVerticalAlignment(VerticalAlignment.CENTER);
        
        // 注意：EasyExcel 的数据格式设置需要通过 @ColumnWidth 和自定义转换器
        // 或使用 @ExcelProperty 注解的 converter 属性
        return new HorizontalCellStyleStrategy(null, contentWriteCellStyle);
    }
    
    /**
     * 写入Excel并设置列宽
     * 
     * @param filePath 输出文件路径
     * @param headClass 表头类
     * @param dataList 数据列表
     * @param columnWidths 列宽数组（单位：字符数）
     * 
     * 注意：EasyExcel的列宽设置主要通过注解 @ColumnWidth 实现
     * 此方法使用自动列宽策略，如需精确控制请使用注解
     */
    public static <T> void writeExcelWithColumnWidth(String filePath, Class<T> headClass,
                                                    List<T> dataList, int[] columnWidths) {
        try {
            // 使用自动列宽策略
            EasyExcel.write(filePath, headClass)
                    .registerWriteHandler(new LongestMatchColumnWidthStyleStrategy())
                    .sheet("Sheet1")
                    .doWrite(dataList);
            
            // 注意：如需精确控制列宽，请在实体类字段上使用 @ColumnWidth 注解
            // 例如：@ColumnWidth(20)
        } catch (Exception e) {
            logger.error("写入Excel文件失败: {}", filePath, e);
            throw new RuntimeException("写入Excel文件失败: " + e.getMessage(), e);
        }
    }
    
    // 注意：EasyExcel的列宽设置主要通过注解 @ColumnWidth 实现
    // 例如：@ColumnWidth(20) 在实体类字段上使用
    
    /**
     * Sheet数据封装类
     */
    public static class SheetData<T> {
        private Class<T> headClass;
        private List<T> dataList;
        
        public SheetData(Class<T> headClass, List<T> dataList) {
            this.headClass = headClass;
            this.dataList = dataList;
        }
        
        public Class<T> getHeadClass() {
            return headClass;
        }
        
        public List<T> getDataList() {
            return dataList;
        }
    }
    
    /**
     * Map格式的Sheet数据封装类
     */
    public static class MapSheetData {
        private List<List<String>> head;
        private List<List<Object>> dataList;
        
        public MapSheetData(List<List<String>> head, List<List<Object>> dataList) {
            this.head = head;
            this.dataList = dataList;
        }
        
        public List<List<String>> getHead() {
            return head;
        }
        
        public List<List<Object>> getDataList() {
            return dataList;
        }
    }
    
    /**
     * 使用模板填充数据（适合报表生成）
     * 
     * @param templatePath 模板文件路径
     * @param outputPath 输出文件路径
     * @param fillData 填充数据
     */
    public static void fillTemplate(String templatePath, String outputPath, Map<String, Object> fillData) {
        try {
            EasyExcel.write(outputPath)
                    .withTemplate(templatePath)
                    .sheet()
                    .doFill(fillData);
        } catch (Exception e) {
            logger.error("填充模板失败", e);
            throw new RuntimeException("填充模板失败: " + e.getMessage(), e);
        }
    }
    
    /**
     * 使用模板填充列表数据
     * 
     * @param templatePath 模板文件路径
     * @param outputPath 输出文件路径
     * @param fillData 填充数据
     * @param listData 列表数据
     */
    public static void fillTemplateWithList(String templatePath, String outputPath,
                                           Map<String, Object> fillData, List<?> listData) {
        try {
            EasyExcel.write(outputPath)
                    .withTemplate(templatePath)
                    .sheet()
                    .doFill(fillData);
            
            // 填充列表数据
            FillConfig fillConfig = FillConfig.builder().forceNewRow(Boolean.TRUE).build();
            EasyExcel.write(outputPath)
                    .withTemplate(templatePath)
                    .sheet()
                    .doFill(listData, fillConfig);
        } catch (Exception e) {
            logger.error("填充模板列表数据失败", e);
            throw new RuntimeException("填充模板列表数据失败: " + e.getMessage(), e);
        }
    }
}
