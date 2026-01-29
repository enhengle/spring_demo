package com.practise.demo.util;

import org.apache.poi.ss.usermodel.*;

/**
 * Excel 列格式配置类
 * 用于定义 Excel 列的格式信息
 * 
 * @author system
 */
public class ColumnFormat {
    
    /**
     * 数据类型枚举
     */
    public enum DataType {
        TEXT,           // 文本
        NUMBER,         // 数字
        INTEGER,        // 整数
        DECIMAL,        // 小数
        DATE,           // 日期
        DATETIME,       // 日期时间
        PERCENTAGE,     // 百分比
        CURRENCY,       // 货币
        BOOLEAN         // 布尔值
    }
    
    /**
     * 对齐方式
     */
    public enum Alignment {
        LEFT(HorizontalAlignment.LEFT),
        CENTER(HorizontalAlignment.CENTER),
        RIGHT(HorizontalAlignment.RIGHT),
        JUSTIFY(HorizontalAlignment.JUSTIFY);
        
        private final HorizontalAlignment poiAlignment;
        
        Alignment(HorizontalAlignment poiAlignment) {
            this.poiAlignment = poiAlignment;
        }
        
        public HorizontalAlignment getPoiAlignment() {
            return poiAlignment;
        }
    }
    
    private DataType dataType = DataType.TEXT;
    private String dateFormat = "yyyy-MM-dd";           // 日期格式
    private String numberFormat = "#,##0.00";            // 数字格式
    private Alignment alignment = Alignment.LEFT;         // 对齐方式
    private String fontName = "微软雅黑";                 // 字体名称
    private Short fontSize = 11;                        // 字体大小
    private Boolean bold = false;                        // 是否加粗
    private Short fontColor = IndexedColors.BLACK.getIndex();  // 字体颜色
    private Short backgroundColor = null;               // 背景颜色
    private Boolean wrapText = false;                    // 是否自动换行
    private Integer columnWidth = null;                  // 列宽（字符数）
    
    // 构造函数
    public ColumnFormat() {
    }
    
    public ColumnFormat(DataType dataType) {
        this.dataType = dataType;
    }
    
    // 静态工厂方法
    public static ColumnFormat text() {
        return new ColumnFormat(DataType.TEXT);
    }
    
    public static ColumnFormat number() {
        return new ColumnFormat(DataType.NUMBER);
    }
    
    public static ColumnFormat integer() {
        return new ColumnFormat(DataType.INTEGER);
    }
    
    public static ColumnFormat decimal() {
        return new ColumnFormat(DataType.DECIMAL);
    }
    
    public static ColumnFormat date() {
        return new ColumnFormat(DataType.DATE);
    }
    
    public static ColumnFormat dateTime() {
        return new ColumnFormat(DataType.DATETIME);
    }
    
    public static ColumnFormat percentage() {
        return new ColumnFormat(DataType.PERCENTAGE);
    }
    
    public static ColumnFormat currency() {
        return new ColumnFormat(DataType.CURRENCY);
    }
    
    public static ColumnFormat bool() {
        return new ColumnFormat(DataType.BOOLEAN);
    }
    
    // 链式设置方法
    public ColumnFormat withDateFormat(String dateFormat) {
        this.dateFormat = dateFormat;
        return this;
    }
    
    public ColumnFormat withNumberFormat(String numberFormat) {
        this.numberFormat = numberFormat;
        return this;
    }
    
    public ColumnFormat withAlignment(Alignment alignment) {
        this.alignment = alignment;
        return this;
    }
    
    public ColumnFormat withFont(String fontName, Short fontSize) {
        this.fontName = fontName;
        this.fontSize = fontSize;
        return this;
    }
    
    public ColumnFormat withBold(Boolean bold) {
        this.bold = bold;
        return this;
    }
    
    public ColumnFormat withFontColor(Short fontColor) {
        this.fontColor = fontColor;
        return this;
    }
    
    public ColumnFormat withBackgroundColor(Short backgroundColor) {
        this.backgroundColor = backgroundColor;
        return this;
    }
    
    public ColumnFormat withWrapText(Boolean wrapText) {
        this.wrapText = wrapText;
        return this;
    }
    
    public ColumnFormat withColumnWidth(Integer columnWidth) {
        this.columnWidth = columnWidth;
        return this;
    }
    
    // Getter 和 Setter
    public DataType getDataType() {
        return dataType;
    }
    
    public void setDataType(DataType dataType) {
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
    
    public Alignment getAlignment() {
        return alignment;
    }
    
    public void setAlignment(Alignment alignment) {
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
