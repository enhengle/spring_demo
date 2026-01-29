package com.practise.demo.model.dto;

import com.practise.demo.util.ColumnFormat;

import java.util.List;
import java.util.Map;

/**
 * Excel 导出请求 DTO
 * 
 * @author system
 */
public class ExportExcelRequest {
    
    /**
     * 数据列表
     */
    private List<Map<String, Object>> dataList;
    
    /**
     * 表头数组
     */
    private String[] headers;
    
    /**
     * 工作表名称
     */
    private String sheetName = "Sheet1";
    
    /**
     * 文件名
     */
    private String fileName = "export.xlsx";
    
    /**
     * 列格式配置 Map
     * key 为列名（表头），value 为格式配置 Map
     */
    private Map<String, ColumnFormatConfig> columnFormats;
    
    // Getter 和 Setter
    public List<Map<String, Object>> getDataList() {
        return dataList;
    }
    
    public void setDataList(List<Map<String, Object>> dataList) {
        this.dataList = dataList;
    }
    
    public String[] getHeaders() {
        return headers;
    }
    
    public void setHeaders(String[] headers) {
        this.headers = headers;
    }
    
    public String getSheetName() {
        return sheetName;
    }
    
    public void setSheetName(String sheetName) {
        this.sheetName = sheetName;
    }
    
    public String getFileName() {
        return fileName;
    }
    
    public void setFileName(String fileName) {
        this.fileName = fileName;
    }
    
    public Map<String, ColumnFormatConfig> getColumnFormats() {
        return columnFormats;
    }
    
    public void setColumnFormats(Map<String, ColumnFormatConfig> columnFormats) {
        this.columnFormats = columnFormats;
    }
    
    /**
     * 将 ColumnFormatConfig Map 转换为 ColumnFormat Map
     */
    public Map<String, ColumnFormat> toColumnFormatMap() {
        if (columnFormats == null || columnFormats.isEmpty()) {
            return null;
        }
        
        Map<String, ColumnFormat> result = new java.util.HashMap<>();
        for (Map.Entry<String, ColumnFormatConfig> entry : columnFormats.entrySet()) {
            result.put(entry.getKey(), entry.getValue().toColumnFormat());
        }
        return result;
    }
    
    /**
     * 列格式配置（简化版，用于 JSON 反序列化）
     */
    public static class ColumnFormatConfig {
        private String dataType;              // TEXT, NUMBER, INTEGER, DECIMAL, DATE, DATETIME, PERCENTAGE, CURRENCY, BOOLEAN
        private String dateFormat;            // 日期格式，如 yyyy-MM-dd
        private String numberFormat;          // 数字格式，如 #,##0.00
        private String alignment;             // LEFT, CENTER, RIGHT, JUSTIFY
        private String fontName;              // 字体名称
        private Short fontSize;                // 字体大小
        private Boolean bold;                  // 是否加粗
        private Short fontColor;              // 字体颜色索引
        private Short backgroundColor;        // 背景颜色索引
        private Boolean wrapText;             // 是否自动换行
        private Integer columnWidth;          // 列宽（字符数）
        
        /**
         * 转换为 ColumnFormat 对象
         */
        public ColumnFormat toColumnFormat() {
            ColumnFormat format = new ColumnFormat();
            
            // 设置数据类型
            if (dataType != null) {
                try {
                    format.setDataType(ColumnFormat.DataType.valueOf(dataType.toUpperCase()));
                } catch (IllegalArgumentException e) {
                    format.setDataType(ColumnFormat.DataType.TEXT);
                }
            }
            
            // 设置其他属性
            if (dateFormat != null) {
                format.setDateFormat(dateFormat);
            }
            if (numberFormat != null) {
                format.setNumberFormat(numberFormat);
            }
            if (alignment != null) {
                try {
                    format.setAlignment(ColumnFormat.Alignment.valueOf(alignment.toUpperCase()));
                } catch (IllegalArgumentException e) {
                    format.setAlignment(ColumnFormat.Alignment.LEFT);
                }
            }
            if (fontName != null) {
                format.setFontName(fontName);
            }
            if (fontSize != null) {
                format.setFontSize(fontSize);
            }
            if (bold != null) {
                format.setBold(bold);
            }
            if (fontColor != null) {
                format.setFontColor(fontColor);
            }
            if (backgroundColor != null) {
                format.setBackgroundColor(backgroundColor);
            }
            if (wrapText != null) {
                format.setWrapText(wrapText);
            }
            if (columnWidth != null) {
                format.setColumnWidth(columnWidth);
            }
            
            return format;
        }
        
        // Getter 和 Setter
        public String getDataType() {
            return dataType;
        }
        
        public void setDataType(String dataType) {
            this.dataType = dataType;
        }
        
        public String getDateFormat() {
            return dateFormat;
        }
        
        public void setDateFormat(String dateFormat) {
            this.dateFormat = dateFormat;
        }
        
        public String getNumberFormat() {
            return numberFormat;
        }
        
        public void setNumberFormat(String numberFormat) {
            this.numberFormat = numberFormat;
        }
        
        public String getAlignment() {
            return alignment;
        }
        
        public void setAlignment(String alignment) {
            this.alignment = alignment;
        }
        
        public String getFontName() {
            return fontName;
        }
        
        public void setFontName(String fontName) {
            this.fontName = fontName;
        }
        
        public Short getFontSize() {
            return fontSize;
        }
        
        public void setFontSize(Short fontSize) {
            this.fontSize = fontSize;
        }
        
        public Boolean getBold() {
            return bold;
        }
        
        public void setBold(Boolean bold) {
            this.bold = bold;
        }
        
        public Short getFontColor() {
            return fontColor;
        }
        
        public void setFontColor(Short fontColor) {
            this.fontColor = fontColor;
        }
        
        public Short getBackgroundColor() {
            return backgroundColor;
        }
        
        public void setBackgroundColor(Short backgroundColor) {
            this.backgroundColor = backgroundColor;
        }
        
        public Boolean getWrapText() {
            return wrapText;
        }
        
        public void setWrapText(Boolean wrapText) {
            this.wrapText = wrapText;
        }
        
        public Integer getColumnWidth() {
            return columnWidth;
        }
        
        public void setColumnWidth(Integer columnWidth) {
            this.columnWidth = columnWidth;
        }
    }
}
