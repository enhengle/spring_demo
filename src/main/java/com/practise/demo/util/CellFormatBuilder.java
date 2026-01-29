package com.practise.demo.util;

import org.apache.poi.ss.usermodel.IndexedColors;

import java.util.HashMap;
import java.util.Map;

/**
 * 单元格格式生成器
 * 提供常用的单元格格式模板
 * 
 * @author system
 */
public class CellFormatBuilder {
    
    /**
     * 创建文本格式（左对齐）
     */
    public static ColumnFormat text() {
        return ColumnFormat.text()
            .withAlignment(ColumnFormat.Alignment.LEFT);
    }
    
    /**
     * 创建文本格式（居中）
     */
    public static ColumnFormat textCenter() {
        return ColumnFormat.text()
            .withAlignment(ColumnFormat.Alignment.CENTER);
    }
    
    /**
     * 创建文本格式（右对齐）
     */
    public static ColumnFormat textRight() {
        return ColumnFormat.text()
            .withAlignment(ColumnFormat.Alignment.RIGHT);
    }
    
    /**
     * 创建整数格式（居中）
     */
    public static ColumnFormat integer() {
        return ColumnFormat.integer()
            .withAlignment(ColumnFormat.Alignment.CENTER)
            .withNumberFormat("#,##0");
    }
    
    /**
     * 创建整数格式（右对齐）
     */
    public static ColumnFormat integerRight() {
        return ColumnFormat.integer()
            .withAlignment(ColumnFormat.Alignment.RIGHT)
            .withNumberFormat("#,##0");
    }
    
    /**
     * 创建小数格式（保留2位小数）
     */
    public static ColumnFormat decimal() {
        return ColumnFormat.decimal()
            .withAlignment(ColumnFormat.Alignment.RIGHT)
            .withNumberFormat("#,##0.00");
    }
    
    /**
     * 创建小数格式（自定义小数位数）
     */
    public static ColumnFormat decimal(int decimalPlaces) {
        StringBuilder format = new StringBuilder("#,##0");
        if (decimalPlaces > 0) {
            format.append(".");
            for (int i = 0; i < decimalPlaces; i++) {
                format.append("0");
            }
        }
        return ColumnFormat.decimal()
            .withAlignment(ColumnFormat.Alignment.RIGHT)
            .withNumberFormat(format.toString());
    }
    
    /**
     * 创建百分比格式
     */
    public static ColumnFormat percentage() {
        return ColumnFormat.percentage()
            .withAlignment(ColumnFormat.Alignment.RIGHT)
            .withNumberFormat("0.00%");
    }
    
    /**
     * 创建百分比格式（保留整数）
     */
    public static ColumnFormat percentageInteger() {
        return ColumnFormat.percentage()
            .withAlignment(ColumnFormat.Alignment.RIGHT)
            .withNumberFormat("0%");
    }
    
    /**
     * 创建人民币货币格式
     */
    public static ColumnFormat currencyCNY() {
        return ColumnFormat.currency()
            .withAlignment(ColumnFormat.Alignment.RIGHT)
            .withNumberFormat("¥#,##0.00");
    }
    
    /**
     * 创建美元货币格式
     */
    public static ColumnFormat currencyUSD() {
        return ColumnFormat.currency()
            .withAlignment(ColumnFormat.Alignment.RIGHT)
            .withNumberFormat("$#,##0.00");
    }
    
    /**
     * 创建欧元货币格式
     */
    public static ColumnFormat currencyEUR() {
        return ColumnFormat.currency()
            .withAlignment(ColumnFormat.Alignment.RIGHT)
            .withNumberFormat("€#,##0.00");
    }
    
    /**
     * 创建日期格式（yyyy-MM-dd）
     */
    public static ColumnFormat date() {
        return ColumnFormat.date()
            .withDateFormat("yyyy-MM-dd")
            .withAlignment(ColumnFormat.Alignment.CENTER);
    }
    
    /**
     * 创建日期格式（自定义格式）
     */
    public static ColumnFormat date(String format) {
        return ColumnFormat.date()
            .withDateFormat(format)
            .withAlignment(ColumnFormat.Alignment.CENTER);
    }
    
    /**
     * 创建日期时间格式（yyyy-MM-dd HH:mm:ss）
     */
    public static ColumnFormat dateTime() {
        return ColumnFormat.dateTime()
            .withDateFormat("yyyy-MM-dd HH:mm:ss")
            .withAlignment(ColumnFormat.Alignment.CENTER);
    }
    
    /**
     * 创建日期时间格式（自定义格式）
     */
    public static ColumnFormat dateTime(String format) {
        return ColumnFormat.dateTime()
            .withDateFormat(format)
            .withAlignment(ColumnFormat.Alignment.CENTER);
    }
    
    /**
     * 创建布尔格式（居中）
     */
    public static ColumnFormat bool() {
        return ColumnFormat.bool()
            .withAlignment(ColumnFormat.Alignment.CENTER);
    }
    
    /**
     * 创建标题格式（加粗、居中、背景色）
     */
    public static ColumnFormat title() {
        return ColumnFormat.text()
            .withAlignment(ColumnFormat.Alignment.CENTER)
            .withBold(true)
            .withFont("微软雅黑", (short) 14)
            .withBackgroundColor(IndexedColors.GREY_25_PERCENT.getIndex())
            .withFontColor(IndexedColors.BLACK.getIndex());
    }
    
    /**
     * 创建表头格式（加粗、居中、浅灰背景）
     */
    public static ColumnFormat header() {
        return ColumnFormat.text()
            .withAlignment(ColumnFormat.Alignment.CENTER)
            .withBold(true)
            .withFont("微软雅黑", (short) 12)
            .withBackgroundColor(IndexedColors.GREY_25_PERCENT.getIndex())
            .withFontColor(IndexedColors.BLACK.getIndex());
    }
    
    /**
     * 创建强调格式（加粗、红色字体）
     */
    public static ColumnFormat emphasis() {
        return ColumnFormat.text()
            .withBold(true)
            .withFontColor(IndexedColors.RED.getIndex());
    }
    
    /**
     * 创建警告格式（黄色背景）
     */
    public static ColumnFormat warning() {
        return ColumnFormat.text()
            .withBackgroundColor(IndexedColors.YELLOW.getIndex())
            .withFontColor(IndexedColors.DARK_RED.getIndex());
    }
    
    /**
     * 创建成功格式（绿色背景）
     */
    public static ColumnFormat success() {
        return ColumnFormat.text()
            .withBackgroundColor(IndexedColors.LIGHT_GREEN.getIndex())
            .withFontColor(IndexedColors.DARK_GREEN.getIndex());
    }
    
    /**
     * 创建信息格式（蓝色背景）
     */
    public static ColumnFormat info() {
        return ColumnFormat.text()
            .withBackgroundColor(IndexedColors.LIGHT_BLUE.getIndex())
            .withFontColor(IndexedColors.DARK_BLUE.getIndex());
    }
    
    /**
     * 创建金额格式（大号字体、加粗、右对齐）
     */
    public static ColumnFormat amount() {
        return ColumnFormat.currency()
            .withNumberFormat("¥#,##0.00")
            .withAlignment(ColumnFormat.Alignment.RIGHT)
            .withBold(true)
            .withFont("微软雅黑", (short) 13);
    }
    
    /**
     * 创建序号格式（居中、整数）
     */
    public static ColumnFormat serialNumber() {
        return ColumnFormat.integer()
            .withAlignment(ColumnFormat.Alignment.CENTER)
            .withNumberFormat("0")
            .withColumnWidth(8);
    }
    
    /**
     * 创建备注格式（自动换行、左对齐）
     */
    public static ColumnFormat remark() {
        return ColumnFormat.text()
            .withAlignment(ColumnFormat.Alignment.LEFT)
            .withWrapText(true)
            .withColumnWidth(30);
    }
    
    /**
     * 创建链接格式（蓝色字体、下划线）
     */
    public static ColumnFormat link() {
        return ColumnFormat.text()
            .withFontColor(IndexedColors.BLUE.getIndex())
            .withAlignment(ColumnFormat.Alignment.LEFT);
    }
    
    /**
     * 创建代码格式（等宽字体）
     */
    public static ColumnFormat code() {
        return ColumnFormat.text()
            .withFont("Consolas", (short) 10)
            .withAlignment(ColumnFormat.Alignment.LEFT);
    }
    
    /**
     * 批量创建列格式配置
     * 
     * @param headers 表头数组
     * @param formatMap 格式映射，key 为表头，value 为格式类型字符串
     * @return 格式配置 Map
     */
    public static Map<String, ColumnFormat> createFormats(String[] headers, Map<String, String> formatMap) {
        Map<String, ColumnFormat> formats = new HashMap<>();
        
        for (String header : headers) {
            String formatType = formatMap.get(header);
            if (formatType != null) {
                ColumnFormat format = createFormatByType(formatType);
                if (format != null) {
                    formats.put(header, format);
                }
            }
        }
        
        return formats;
    }
    
    /**
     * 根据类型字符串创建格式
     */
    private static ColumnFormat createFormatByType(String type) {
        if (type == null) {
            return null;
        }
        
        switch (type.toUpperCase()) {
            case "TEXT":
            case "STRING":
                return text();
            case "TEXT_CENTER":
                return textCenter();
            case "TEXT_RIGHT":
                return textRight();
            case "INTEGER":
            case "INT":
                return integer();
            case "INTEGER_RIGHT":
            case "INT_RIGHT":
                return integerRight();
            case "DECIMAL":
            case "DOUBLE":
            case "FLOAT":
                return decimal();
            case "PERCENTAGE":
            case "PERCENT":
                return percentage();
            case "PERCENTAGE_INTEGER":
                return percentageInteger();
            case "CURRENCY":
            case "CNY":
            case "RMB":
                return currencyCNY();
            case "USD":
                return currencyUSD();
            case "EUR":
                return currencyEUR();
            case "DATE":
                return date();
            case "DATETIME":
            case "DATE_TIME":
                return dateTime();
            case "BOOL":
            case "BOOLEAN":
                return bool();
            case "TITLE":
                return title();
            case "HEADER":
                return header();
            case "EMPHASIS":
                return emphasis();
            case "WARNING":
                return warning();
            case "SUCCESS":
                return success();
            case "INFO":
                return info();
            case "AMOUNT":
                return amount();
            case "SERIAL":
            case "SERIAL_NUMBER":
                return serialNumber();
            case "REMARK":
            case "NOTE":
                return remark();
            case "LINK":
                return link();
            case "CODE":
                return code();
            default:
                return text(); // 默认文本格式
        }
    }
    
    /**
     * 创建常用格式配置模板
     * 
     * @param templateName 模板名称
     * @return 格式配置 Map
     */
    public static Map<String, ColumnFormat> createTemplate(String templateName) {
        Map<String, ColumnFormat> formats = new HashMap<>();
        
        switch (templateName.toUpperCase()) {
            case "USER_LIST":
            case "用户列表":
                formats.put("序号", serialNumber());
                formats.put("姓名", textCenter());
                formats.put("年龄", integer());
                formats.put("工资", currencyCNY());
                formats.put("入职日期", date());
                formats.put("邮箱", text());
                break;
                
            case "FINANCIAL":
            case "财务报表":
                formats.put("日期", date());
                formats.put("收入", currencyCNY());
                formats.put("支出", currencyCNY());
                formats.put("余额", amount());
                formats.put("增长率", percentage());
                break;
                
            case "SALES":
            case "销售报表":
                formats.put("日期", date());
                formats.put("产品名称", text());
                formats.put("数量", integerRight());
                formats.put("单价", currencyCNY());
                formats.put("金额", amount());
                formats.put("利润率", percentage());
                break;
                
            case "INVENTORY":
            case "库存报表":
                formats.put("产品编号", text());
                formats.put("产品名称", text());
                formats.put("库存数量", integerRight());
                formats.put("单价", currencyCNY());
                formats.put("总价值", amount());
                formats.put("备注", remark());
                break;
                
            case "EMPLOYEE":
            case "员工信息":
                formats.put("工号", textCenter());
                formats.put("姓名", textCenter());
                formats.put("部门", textCenter());
                formats.put("职位", textCenter());
                formats.put("入职日期", date());
                formats.put("工资", currencyCNY());
                formats.put("状态", textCenter());
                break;
                
            default:
                // 默认模板：所有列使用文本格式
                break;
        }
        
        return formats;
    }
}
