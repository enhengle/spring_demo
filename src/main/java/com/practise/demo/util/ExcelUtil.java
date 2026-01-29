package com.practise.demo.util;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.lang.reflect.Field;
import java.util.*;

/**
 * Excel 操作工具类
 * 支持 .xls 和 .xlsx 格式
 * 
 * @author system
 */
public class ExcelUtil {
    
    private static final Logger logger = LoggerFactory.getLogger(ExcelUtil.class);
    
    private static final String XLS = "xls";
    private static final String XLSX = "xlsx";
    
    /**
     * 读取 Excel 文件为 List<Map<String, Object>>
     * 
     * @param filePath Excel 文件路径
     * @param hasHeader 是否包含表头
     * @return 数据列表，Map 的 key 为列名（如果有表头）或列索引（如果没有表头）
     */
    public static List<Map<String, Object>> readExcel(String filePath, boolean hasHeader) {
        return readExcel(filePath, hasHeader, 0);
    }
    
    /**
     * 读取 Excel 文件指定工作表
     * 
     * @param filePath Excel 文件路径
     * @param hasHeader 是否包含表头
     * @param sheetIndex 工作表索引（从0开始）
     * @return 数据列表
     */
    public static List<Map<String, Object>> readExcel(String filePath, boolean hasHeader, int sheetIndex) {
        List<Map<String, Object>> result = new ArrayList<>();
        
        try (FileInputStream fis = new FileInputStream(filePath)) {
            Workbook workbook = createWorkbook(filePath, fis);
            Sheet sheet = workbook.getSheetAt(sheetIndex);
            
            int startRow = hasHeader ? 1 : 0;
            List<String> headers = null;
            
            if (hasHeader && sheet.getRow(0) != null) {
                headers = new ArrayList<>();
                Row headerRow = sheet.getRow(0);
                for (int i = 0; i < headerRow.getLastCellNum(); i++) {
                    Cell cell = headerRow.getCell(i);
                    headers.add(getCellValueAsString(cell));
                }
            }
            
            for (int i = startRow; i <= sheet.getLastRowNum(); i++) {
                Row row = sheet.getRow(i);
                if (row == null) {
                    continue;
                }
                
                Map<String, Object> rowData = new LinkedHashMap<>();
                int cellCount = row.getLastCellNum();
                
                for (int j = 0; j < cellCount; j++) {
                    Cell cell = row.getCell(j);
                    String key = hasHeader && headers != null && j < headers.size() 
                            ? headers.get(j) 
                            : String.valueOf(j);
                    Object value = getCellValue(cell);
                    rowData.put(key, value);
                }
                
                result.add(rowData);
            }
            
            workbook.close();
        } catch (IOException e) {
            logger.error("读取 Excel 文件失败: {}", filePath, e);
            throw new RuntimeException("读取 Excel 文件失败: " + e.getMessage(), e);
        }
        
        return result;
    }
    
    /**
     * 读取 Excel 文件为 List<T>（使用反射）
     * 
     * @param filePath Excel 文件路径
     * @param clazz 目标类型
     * @param hasHeader 是否包含表头
     * @return 对象列表
     */
    public static <T> List<T> readExcel(String filePath, Class<T> clazz, boolean hasHeader) {
        List<Map<String, Object>> dataList = readExcel(filePath, hasHeader);
        List<T> result = new ArrayList<>();
        
        for (Map<String, Object> rowData : dataList) {
            try {
                T obj = clazz.newInstance();
                Field[] fields = clazz.getDeclaredFields();
                
                for (Field field : fields) {
                    field.setAccessible(true);
                    String fieldName = field.getName();
                    Object value = rowData.get(fieldName);
                    
                    if (value != null) {
                        setFieldValue(obj, field, value);
                    }
                }
                
                result.add(obj);
            } catch (Exception e) {
                logger.error("转换对象失败", e);
            }
        }
        
        return result;
    }
    
    /**
     * 写入数据到 Excel 文件
     * 
     * @param filePath 输出文件路径
     * @param dataList 数据列表
     * @param headers 表头数组
     * @param sheetName 工作表名称
     */
    public static void writeExcel(String filePath, List<Map<String, Object>> dataList, String[] headers, String sheetName) {
        writeExcel(filePath, dataList, headers, sheetName, null);
    }
    
    /**
     * 写入数据到 Excel 文件（支持列格式配置）
     * 
     * @param filePath 输出文件路径
     * @param dataList 数据列表
     * @param headers 表头数组
     * @param sheetName 工作表名称
     * @param columnFormats 列格式配置 Map，key 为列名（表头），value 为格式配置
     */
    public static void writeExcel(String filePath, List<Map<String, Object>> dataList, String[] headers, 
                                 String sheetName, Map<String, ColumnFormat> columnFormats) {
        try (FileOutputStream fos = new FileOutputStream(filePath)) {
            Workbook workbook = createWorkbookByExtension(filePath);
            Sheet sheet = workbook.createSheet(sheetName != null ? sheetName : "Sheet1");
            
            // 创建表头
            Row headerRow = sheet.createRow(0);
            CellStyle headerStyle = createHeaderStyle(workbook);
            
            for (int i = 0; i < headers.length; i++) {
                Cell cell = headerRow.createCell(i);
                cell.setCellValue(headers[i]);
                cell.setCellStyle(headerStyle);
            }
            
            // 创建列格式样式缓存
            Map<String, CellStyle> styleCache = new HashMap<>();
            if (columnFormats != null) {
                for (String header : headers) {
                    ColumnFormat format = columnFormats.get(header);
                    if (format != null) {
                        CellStyle style = createCellStyle(workbook, format);
                        styleCache.put(header, style);
                    }
                }
            }
            
            // 写入数据
            int rowNum = 1;
            for (Map<String, Object> rowData : dataList) {
                Row row = sheet.createRow(rowNum++);
                for (int i = 0; i < headers.length; i++) {
                    Cell cell = row.createCell(i);
                    String header = headers[i];
                    Object value = rowData.get(header);
                    
                    // 应用格式并设置值
                    ColumnFormat format = columnFormats != null ? columnFormats.get(header) : null;
                    setCellValueWithFormat(cell, value, format, styleCache.get(header));
                }
            }
            
            // 设置列宽
            for (int i = 0; i < headers.length; i++) {
                String header = headers[i];
                ColumnFormat format = columnFormats != null ? columnFormats.get(header) : null;
                if (format != null && format.getColumnWidth() != null) {
                    sheet.setColumnWidth(i, format.getColumnWidth() * 256); // POI 中列宽单位是 1/256 字符
                } else {
                    sheet.autoSizeColumn(i);
                }
            }
            
            workbook.write(fos);
            workbook.close();
        } catch (IOException e) {
            logger.error("写入 Excel 文件失败: {}", filePath, e);
            throw new RuntimeException("写入 Excel 文件失败: " + e.getMessage(), e);
        }
    }
    
    /**
     * 写入对象列表到 Excel 文件
     * 
     * @param filePath 输出文件路径
     * @param dataList 对象列表
     * @param headers 表头数组（对应对象的字段名）
     * @param sheetName 工作表名称
     */
    public static <T> void writeExcelFromObjects(String filePath, List<T> dataList, String[] headers, String sheetName) {
        List<Map<String, Object>> mapList = new ArrayList<>();
        
        for (T obj : dataList) {
            Map<String, Object> map = new LinkedHashMap<>();
            Field[] fields = obj.getClass().getDeclaredFields();
            
            for (Field field : fields) {
                field.setAccessible(true);
                try {
                    Object value = field.get(obj);
                    map.put(field.getName(), value);
                } catch (IllegalAccessException e) {
                    logger.error("获取字段值失败: {}", field.getName(), e);
                }
            }
            
            mapList.add(map);
        }
        
        writeExcel(filePath, mapList, headers, sheetName);
    }
    
    /**
     * 写入数据到 Excel（使用 Map 的 key 作为表头）
     * 
     * @param filePath 输出文件路径
     * @param dataList 数据列表
     * @param sheetName 工作表名称
     */
    public static void writeExcel(String filePath, List<Map<String, Object>> dataList, String sheetName) {
        if (dataList == null || dataList.isEmpty()) {
            throw new IllegalArgumentException("数据列表不能为空");
        }
        
        Set<String> headers = new LinkedHashSet<>();
        for (Map<String, Object> rowData : dataList) {
            headers.addAll(rowData.keySet());
        }
        
        writeExcel(filePath, dataList, headers.toArray(new String[0]), sheetName);
    }
    
    /**
     * 合并单元格
     * 
     * @param sheet 工作表
     * @param firstRow 起始行（从0开始）
     * @param lastRow 结束行（从0开始）
     * @param firstCol 起始列（从0开始）
     * @param lastCol 结束列（从0开始）
     */
    public static void mergeCells(Sheet sheet, int firstRow, int lastRow, int firstCol, int lastCol) {
        sheet.addMergedRegion(new CellRangeAddress(firstRow, lastRow, firstCol, lastCol));
    }
    
    /**
     * 设置单元格样式
     * 
     * @param workbook 工作簿
     * @param cell 单元格
     * @param fontName 字体名称
     * @param fontSize 字体大小
     * @param bold 是否加粗
     * @param horizontalAlignment 水平对齐方式
     */
    public static void setCellStyle(Workbook workbook, Cell cell, String fontName, short fontSize, 
                                    boolean bold, HorizontalAlignment horizontalAlignment) {
        CellStyle style = workbook.createCellStyle();
        Font font = workbook.createFont();
        font.setFontName(fontName);
        font.setFontHeightInPoints(fontSize);
        font.setBold(bold);
        style.setFont(font);
        style.setAlignment(horizontalAlignment);
        cell.setCellStyle(style);
    }
    
    /**
     * 读取 Excel 文件的所有工作表名称
     * 
     * @param filePath Excel 文件路径
     * @return 工作表名称列表
     */
    public static List<String> getSheetNames(String filePath) {
        List<String> sheetNames = new ArrayList<>();
        
        try (FileInputStream fis = new FileInputStream(filePath)) {
            Workbook workbook = createWorkbook(filePath, fis);
            int sheetCount = workbook.getNumberOfSheets();
            
            for (int i = 0; i < sheetCount; i++) {
                sheetNames.add(workbook.getSheetName(i));
            }
            
            workbook.close();
        } catch (IOException e) {
            logger.error("读取工作表名称失败: {}", filePath, e);
            throw new RuntimeException("读取工作表名称失败: " + e.getMessage(), e);
        }
        
        return sheetNames;
    }
    
    /**
     * 获取工作表行数
     * 
     * @param filePath Excel 文件路径
     * @param sheetIndex 工作表索引
     * @return 行数
     */
    public static int getRowCount(String filePath, int sheetIndex) {
        try (FileInputStream fis = new FileInputStream(filePath)) {
            Workbook workbook = createWorkbook(filePath, fis);
            Sheet sheet = workbook.getSheetAt(sheetIndex);
            int rowCount = sheet.getLastRowNum() + 1;
            workbook.close();
            return rowCount;
        } catch (IOException e) {
            logger.error("获取行数失败: {}", filePath, e);
            throw new RuntimeException("获取行数失败: " + e.getMessage(), e);
        }
    }
    
    /**
     * 导出 Excel 到输出流（用于 Web 下载）
     * 
     * @param outputStream 输出流
     * @param dataList 数据列表
     * @param headers 表头数组
     * @param sheetName 工作表名称
     */
    public static void exportToStream(OutputStream outputStream, List<Map<String, Object>> dataList, 
                                     String[] headers, String sheetName) {
        exportToStream(outputStream, dataList, headers, sheetName, null);
    }
    
    /**
     * 导出 Excel 到输出流（用于 Web 下载，支持列格式配置）
     * 
     * @param outputStream 输出流
     * @param dataList 数据列表
     * @param headers 表头数组
     * @param sheetName 工作表名称
     * @param columnFormats 列格式配置 Map，key 为列名（表头），value 为格式配置
     */
    public static void exportToStream(OutputStream outputStream, List<Map<String, Object>> dataList, 
                                     String[] headers, String sheetName, Map<String, ColumnFormat> columnFormats) {
        try {
            Workbook workbook = new XSSFWorkbook();
            Sheet sheet = workbook.createSheet(sheetName != null ? sheetName : "Sheet1");
            
            // 创建表头
            Row headerRow = sheet.createRow(0);
            CellStyle headerStyle = createHeaderStyle(workbook);
            
            for (int i = 0; i < headers.length; i++) {
                Cell cell = headerRow.createCell(i);
                cell.setCellValue(headers[i]);
                cell.setCellStyle(headerStyle);
            }
            
            // 创建列格式样式缓存
            Map<String, CellStyle> styleCache = new HashMap<>();
            if (columnFormats != null) {
                for (String header : headers) {
                    ColumnFormat format = columnFormats.get(header);
                    if (format != null) {
                        CellStyle style = createCellStyle(workbook, format);
                        styleCache.put(header, style);
                    }
                }
            }
            
            // 写入数据
            int rowNum = 1;
            for (Map<String, Object> rowData : dataList) {
                Row row = sheet.createRow(rowNum++);
                for (int i = 0; i < headers.length; i++) {
                    Cell cell = row.createCell(i);
                    String header = headers[i];
                    Object value = rowData.get(header);
                    
                    // 应用格式并设置值
                    ColumnFormat format = columnFormats != null ? columnFormats.get(header) : null;
                    setCellValueWithFormat(cell, value, format, styleCache.get(header));
                }
            }
            
            // 设置列宽
            for (int i = 0; i < headers.length; i++) {
                String header = headers[i];
                ColumnFormat format = columnFormats != null ? columnFormats.get(header) : null;
                if (format != null && format.getColumnWidth() != null) {
                    sheet.setColumnWidth(i, format.getColumnWidth() * 256);
                } else {
                    sheet.autoSizeColumn(i);
                }
            }
            
            workbook.write(outputStream);
            workbook.close();
        } catch (IOException e) {
            logger.error("导出 Excel 到流失败", e);
            throw new RuntimeException("导出 Excel 到流失败: " + e.getMessage(), e);
        }
    }
    
    // ==================== 私有辅助方法 ====================
    
    /**
     * 根据文件扩展名创建 Workbook
     */
    private static Workbook createWorkbookByExtension(String filePath) throws IOException {
        String extension = getFileExtension(filePath);
        if (XLSX.equalsIgnoreCase(extension)) {
            return new XSSFWorkbook();
        } else if (XLS.equalsIgnoreCase(extension)) {
            return new HSSFWorkbook();
        } else {
            throw new IllegalArgumentException("不支持的文件格式: " + extension);
        }
    }
    
    /**
     * 根据文件创建 Workbook
     */
    private static Workbook createWorkbook(String filePath, InputStream inputStream) throws IOException {
        String extension = getFileExtension(filePath);
        if (XLSX.equalsIgnoreCase(extension)) {
            return new XSSFWorkbook(inputStream);
        } else if (XLS.equalsIgnoreCase(extension)) {
            return new HSSFWorkbook(inputStream);
        } else {
            throw new IllegalArgumentException("不支持的文件格式: " + extension);
        }
    }
    
    /**
     * 获取文件扩展名
     */
    private static String getFileExtension(String filePath) {
        int lastDotIndex = filePath.lastIndexOf('.');
        if (lastDotIndex == -1) {
            return XLSX; // 默认使用 xlsx
        }
        return filePath.substring(lastDotIndex + 1).toLowerCase();
    }
    
    /**
     * 获取单元格值
     */
    private static Object getCellValue(Cell cell) {
        if (cell == null) {
            return null;
        }
        
        switch (cell.getCellType()) {
            case STRING:
                return cell.getStringCellValue();
            case NUMERIC:
                if (org.apache.poi.ss.usermodel.DateUtil.isCellDateFormatted(cell)) {
                    return cell.getDateCellValue();
                } else {
                    double numericValue = cell.getNumericCellValue();
                    // 判断是否为整数
                    if (numericValue == (long) numericValue) {
                        return (long) numericValue;
                    } else {
                        return numericValue;
                    }
                }
            case BOOLEAN:
                return cell.getBooleanCellValue();
            case FORMULA:
                return cell.getCellFormula();
            case BLANK:
                return null;
            default:
                return null;
        }
    }
    
    /**
     * 获取单元格值为字符串
     */
    private static String getCellValueAsString(Cell cell) {
        Object value = getCellValue(cell);
        return value != null ? value.toString() : "";
    }
    
    /**
     * 设置单元格值
     */
    private static void setCellValue(Cell cell, Object value) {
        if (value == null) {
            cell.setBlank();
            return;
        }
        
        if (value instanceof String) {
            cell.setCellValue((String) value);
        } else if (value instanceof Number) {
            if (value instanceof Integer || value instanceof Long || value instanceof Short || value instanceof Byte) {
                cell.setCellValue(((Number) value).doubleValue());
            } else {
                cell.setCellValue(((Number) value).doubleValue());
            }
        } else if (value instanceof Boolean) {
            cell.setCellValue((Boolean) value);
        } else if (value instanceof Date) {
            cell.setCellValue((Date) value);
        } else {
            cell.setCellValue(value.toString());
        }
    }
    
    /**
     * 设置单元格值并应用格式
     */
    private static void setCellValueWithFormat(Cell cell, Object value, ColumnFormat format, CellStyle style) {
        // 设置值
        if (format != null) {
            // 根据数据类型设置值
            switch (format.getDataType()) {
                case TEXT:
                    cell.setCellValue(value != null ? value.toString() : "");
                    break;
                case NUMBER:
                case DECIMAL:
                    if (value instanceof Number) {
                        cell.setCellValue(((Number) value).doubleValue());
                    } else if (value != null) {
                        try {
                            cell.setCellValue(Double.parseDouble(value.toString()));
                        } catch (NumberFormatException e) {
                            cell.setCellValue(value.toString());
                        }
                    }
                    break;
                case INTEGER:
                    if (value instanceof Number) {
                        cell.setCellValue(((Number) value).doubleValue());
                    } else if (value != null) {
                        try {
                            cell.setCellValue(Long.parseLong(value.toString()));
                        } catch (NumberFormatException e) {
                            cell.setCellValue(value.toString());
                        }
                    }
                    break;
                case DATE:
                case DATETIME:
                    if (value instanceof Date) {
                        cell.setCellValue((Date) value);
                    } else if (value != null) {
                        // 尝试解析日期字符串
                        try {
                            java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat(format.getDateFormat());
                            Date date = sdf.parse(value.toString());
                            cell.setCellValue(date);
                        } catch (Exception e) {
                            cell.setCellValue(value.toString());
                        }
                    }
                    break;
                case PERCENTAGE:
                    if (value instanceof Number) {
                        cell.setCellValue(((Number) value).doubleValue() / 100.0);
                    } else if (value != null) {
                        try {
                            double num = Double.parseDouble(value.toString());
                            cell.setCellValue(num / 100.0);
                        } catch (NumberFormatException e) {
                            cell.setCellValue(value.toString());
                        }
                    }
                    break;
                case CURRENCY:
                    if (value instanceof Number) {
                        cell.setCellValue(((Number) value).doubleValue());
                    } else if (value != null) {
                        try {
                            cell.setCellValue(Double.parseDouble(value.toString()));
                        } catch (NumberFormatException e) {
                            cell.setCellValue(value.toString());
                        }
                    }
                    break;
                case BOOLEAN:
                    if (value instanceof Boolean) {
                        cell.setCellValue((Boolean) value);
                    } else if (value != null) {
                        cell.setCellValue(Boolean.parseBoolean(value.toString()));
                    }
                    break;
                default:
                    setCellValue(cell, value);
                    break;
            }
        } else {
            setCellValue(cell, value);
        }
        
        // 应用样式
        if (style != null) {
            cell.setCellStyle(style);
        }
    }
    
    /**
     * 根据格式配置创建单元格样式
     */
    private static CellStyle createCellStyle(Workbook workbook, ColumnFormat format) {
        CellStyle style = workbook.createCellStyle();
        
        // 设置数据格式
        DataFormat dataFormat = workbook.createDataFormat();
        String formatPattern = null;
        
        switch (format.getDataType()) {
            case DATE:
                formatPattern = format.getDateFormat();
                break;
            case DATETIME:
                formatPattern = format.getDateFormat();
                break;
            case PERCENTAGE:
                formatPattern = "0.00%";
                break;
            case CURRENCY:
                formatPattern = "¥#,##0.00";
                break;
            case INTEGER:
                formatPattern = "#,##0";
                break;
            case DECIMAL:
            case NUMBER:
                formatPattern = format.getNumberFormat();
                break;
            default:
                break;
        }
        
        if (formatPattern != null) {
            style.setDataFormat(dataFormat.getFormat(formatPattern));
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
        
        return style;
    }
    
    /**
     * 创建表头样式
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
        style.setBorderBottom(BorderStyle.THIN);
        style.setBorderTop(BorderStyle.THIN);
        style.setBorderLeft(BorderStyle.THIN);
        style.setBorderRight(BorderStyle.THIN);
        return style;
    }
    
    /**
     * 设置字段值（支持类型转换）
     */
    private static void setFieldValue(Object obj, Field field, Object value) throws Exception {
        Class<?> fieldType = field.getType();
        
        if (value == null) {
            return;
        }
        
        if (fieldType.isAssignableFrom(value.getClass())) {
            field.set(obj, value);
        } else if (fieldType == String.class) {
            field.set(obj, value.toString());
        } else if (fieldType == Integer.class || fieldType == int.class) {
            if (value instanceof Number) {
                field.set(obj, ((Number) value).intValue());
            } else {
                field.set(obj, Integer.parseInt(value.toString()));
            }
        } else if (fieldType == Long.class || fieldType == long.class) {
            if (value instanceof Number) {
                field.set(obj, ((Number) value).longValue());
            } else {
                field.set(obj, Long.parseLong(value.toString()));
            }
        } else if (fieldType == Double.class || fieldType == double.class) {
            if (value instanceof Number) {
                field.set(obj, ((Number) value).doubleValue());
            } else {
                field.set(obj, Double.parseDouble(value.toString()));
            }
        } else if (fieldType == Boolean.class || fieldType == boolean.class) {
            if (value instanceof Boolean) {
                field.set(obj, value);
            } else {
                field.set(obj, Boolean.parseBoolean(value.toString()));
            }
        } else if (fieldType == Date.class && value instanceof Date) {
            field.set(obj, value);
        } else {
            logger.warn("无法转换字段类型: {} = {}", field.getName(), value.getClass());
        }
    }
}
