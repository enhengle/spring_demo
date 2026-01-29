package com.practise.demo.util;

import org.apache.poi.ss.usermodel.IndexedColors;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

/**
 * CellFormatBuilder 测试类
 * 测试单元格格式生成器的各种功能
 * 
 * @author system
 */
@DisplayName("单元格格式生成器测试")
class CellFormatBuilderTest {
    
    /**
     * 测试文本格式生成
     */
    @Test
    @DisplayName("测试文本格式生成")
    void testTextFormats() {
        // 测试左对齐文本
        ColumnFormat textLeft = CellFormatBuilder.text();
        assertNotNull(textLeft, "文本格式不应为空");
        assertEquals(ColumnFormat.DataType.TEXT, textLeft.getDataType(), "数据类型应为TEXT");
        assertEquals(ColumnFormat.Alignment.LEFT, textLeft.getAlignment(), "对齐方式应为LEFT");
        
        // 测试居中文本
        ColumnFormat textCenter = CellFormatBuilder.textCenter();
        assertEquals(ColumnFormat.Alignment.CENTER, textCenter.getAlignment(), "对齐方式应为CENTER");
        
        // 测试右对齐文本
        ColumnFormat textRight = CellFormatBuilder.textRight();
        assertEquals(ColumnFormat.Alignment.RIGHT, textRight.getAlignment(), "对齐方式应为RIGHT");
    }
    
    /**
     * 测试整数格式生成
     */
    @Test
    @DisplayName("测试整数格式生成")
    void testIntegerFormats() {
        // 测试居中整数
        ColumnFormat integer = CellFormatBuilder.integer();
        assertNotNull(integer, "整数格式不应为空");
        assertEquals(ColumnFormat.DataType.INTEGER, integer.getDataType(), "数据类型应为INTEGER");
        assertEquals(ColumnFormat.Alignment.CENTER, integer.getAlignment(), "对齐方式应为CENTER");
        assertEquals("#,##0", integer.getNumberFormat(), "数字格式应为#,##0");
        
        // 测试右对齐整数
        ColumnFormat integerRight = CellFormatBuilder.integerRight();
        assertEquals(ColumnFormat.Alignment.RIGHT, integerRight.getAlignment(), "对齐方式应为RIGHT");
    }
    
    /**
     * 测试小数格式生成
     */
    @Test
    @DisplayName("测试小数格式生成")
    void testDecimalFormats() {
        // 测试默认小数格式（2位）
        ColumnFormat decimal = CellFormatBuilder.decimal();
        assertNotNull(decimal, "小数格式不应为空");
        assertEquals(ColumnFormat.DataType.DECIMAL, decimal.getDataType(), "数据类型应为DECIMAL");
        assertEquals("#,##0.00", decimal.getNumberFormat(), "数字格式应为#,##0.00");
        
        // 测试自定义小数位数
        ColumnFormat decimal3 = CellFormatBuilder.decimal(3);
        assertEquals("#,##0.000", decimal3.getNumberFormat(), "数字格式应为#,##0.000");
        
        ColumnFormat decimal4 = CellFormatBuilder.decimal(4);
        assertEquals("#,##0.0000", decimal4.getNumberFormat(), "数字格式应为#,##0.0000");
        
        // 测试0位小数（整数）
        ColumnFormat decimal0 = CellFormatBuilder.decimal(0);
        assertEquals("#,##0", decimal0.getNumberFormat(), "数字格式应为#,##0");
    }
    
    /**
     * 测试货币格式生成
     */
    @Test
    @DisplayName("测试货币格式生成")
    void testCurrencyFormats() {
        // 测试人民币格式
        ColumnFormat cny = CellFormatBuilder.currencyCNY();
        assertNotNull(cny, "人民币格式不应为空");
        assertEquals(ColumnFormat.DataType.CURRENCY, cny.getDataType(), "数据类型应为CURRENCY");
        assertEquals("¥#,##0.00", cny.getNumberFormat(), "数字格式应为¥#,##0.00");
        assertEquals(ColumnFormat.Alignment.RIGHT, cny.getAlignment(), "对齐方式应为RIGHT");
        
        // 测试美元格式
        ColumnFormat usd = CellFormatBuilder.currencyUSD();
        assertEquals("$#,##0.00", usd.getNumberFormat(), "数字格式应为$#,##0.00");
        
        // 测试欧元格式
        ColumnFormat eur = CellFormatBuilder.currencyEUR();
        assertEquals("€#,##0.00", eur.getNumberFormat(), "数字格式应为€#,##0.00");
    }
    
    /**
     * 测试日期格式生成
     */
    @Test
    @DisplayName("测试日期格式生成")
    void testDateFormats() {
        // 测试默认日期格式
        ColumnFormat date = CellFormatBuilder.date();
        assertNotNull(date, "日期格式不应为空");
        assertEquals(ColumnFormat.DataType.DATE, date.getDataType(), "数据类型应为DATE");
        assertEquals("yyyy-MM-dd", date.getDateFormat(), "日期格式应为yyyy-MM-dd");
        assertEquals(ColumnFormat.Alignment.CENTER, date.getAlignment(), "对齐方式应为CENTER");
        
        // 测试自定义日期格式
        ColumnFormat dateCustom = CellFormatBuilder.date("yyyy/MM/dd");
        assertEquals("yyyy/MM/dd", dateCustom.getDateFormat(), "日期格式应为yyyy/MM/dd");
        
        // 测试日期时间格式
        ColumnFormat dateTime = CellFormatBuilder.dateTime();
        assertEquals(ColumnFormat.DataType.DATETIME, dateTime.getDataType(), "数据类型应为DATETIME");
        assertEquals("yyyy-MM-dd HH:mm:ss", dateTime.getDateFormat(), "日期格式应为yyyy-MM-dd HH:mm:ss");
        
        // 测试自定义日期时间格式
        ColumnFormat dateTimeCustom = CellFormatBuilder.dateTime("yyyy-MM-dd HH:mm");
        assertEquals("yyyy-MM-dd HH:mm", dateTimeCustom.getDateFormat(), "日期格式应为yyyy-MM-dd HH:mm");
    }
    
    /**
     * 测试百分比格式生成
     */
    @Test
    @DisplayName("测试百分比格式生成")
    void testPercentageFormats() {
        // 测试默认百分比格式
        ColumnFormat percentage = CellFormatBuilder.percentage();
        assertNotNull(percentage, "百分比格式不应为空");
        assertEquals(ColumnFormat.DataType.PERCENTAGE, percentage.getDataType(), "数据类型应为PERCENTAGE");
        assertEquals("0.00%", percentage.getNumberFormat(), "数字格式应为0.00%");
        
        // 测试整数百分比格式
        ColumnFormat percentageInt = CellFormatBuilder.percentageInteger();
        assertEquals("0%", percentageInt.getNumberFormat(), "数字格式应为0%");
    }
    
    /**
     * 测试布尔格式生成
     */
    @Test
    @DisplayName("测试布尔格式生成")
    void testBooleanFormat() {
        ColumnFormat bool = CellFormatBuilder.bool();
        assertNotNull(bool, "布尔格式不应为空");
        assertEquals(ColumnFormat.DataType.BOOLEAN, bool.getDataType(), "数据类型应为BOOLEAN");
        assertEquals(ColumnFormat.Alignment.CENTER, bool.getAlignment(), "对齐方式应为CENTER");
    }
    
    /**
     * 测试标题和表头格式生成
     */
    @Test
    @DisplayName("测试标题和表头格式生成")
    void testTitleAndHeaderFormats() {
        // 测试标题格式
        ColumnFormat title = CellFormatBuilder.title();
        assertNotNull(title, "标题格式不应为空");
        assertTrue(title.getBold(), "标题应加粗");
        assertEquals(ColumnFormat.Alignment.CENTER, title.getAlignment(), "标题应对齐居中");
        assertNotNull(title.getBackgroundColor(), "标题应有背景色");
        
        // 测试表头格式
        ColumnFormat header = CellFormatBuilder.header();
        assertNotNull(header, "表头格式不应为空");
        assertTrue(header.getBold(), "表头应加粗");
        assertEquals(ColumnFormat.Alignment.CENTER, header.getAlignment(), "表头应对齐居中");
        assertNotNull(header.getBackgroundColor(), "表头应有背景色");
    }
    
    /**
     * 测试状态格式生成
     */
    @Test
    @DisplayName("测试状态格式生成")
    void testStatusFormats() {
        // 测试强调格式
        ColumnFormat emphasis = CellFormatBuilder.emphasis();
        assertNotNull(emphasis, "强调格式不应为空");
        assertTrue(emphasis.getBold(), "强调格式应加粗");
        assertEquals(IndexedColors.RED.getIndex(), emphasis.getFontColor(), "强调格式字体应为红色");
        
        // 测试警告格式
        ColumnFormat warning = CellFormatBuilder.warning();
        assertNotNull(warning.getBackgroundColor(), "警告格式应有背景色");
        
        // 测试成功格式
        ColumnFormat success = CellFormatBuilder.success();
        assertNotNull(success.getBackgroundColor(), "成功格式应有背景色");
        
        // 测试信息格式
        ColumnFormat info = CellFormatBuilder.info();
        assertNotNull(info.getBackgroundColor(), "信息格式应有背景色");
    }
    
    /**
     * 测试专用格式生成
     */
    @Test
    @DisplayName("测试专用格式生成")
    void testSpecialFormats() {
        // 测试金额格式
        ColumnFormat amount = CellFormatBuilder.amount();
        assertNotNull(amount, "金额格式不应为空");
        assertTrue(amount.getBold(), "金额格式应加粗");
        assertEquals(ColumnFormat.Alignment.RIGHT, amount.getAlignment(), "金额格式应右对齐");
        
        // 测试序号格式
        ColumnFormat serial = CellFormatBuilder.serialNumber();
        assertNotNull(serial, "序号格式不应为空");
        assertEquals(ColumnFormat.Alignment.CENTER, serial.getAlignment(), "序号格式应对齐居中");
        assertNotNull(serial.getColumnWidth(), "序号格式应有列宽");
        
        // 测试备注格式
        ColumnFormat remark = CellFormatBuilder.remark();
        assertNotNull(remark, "备注格式不应为空");
        assertTrue(remark.getWrapText(), "备注格式应自动换行");
        assertNotNull(remark.getColumnWidth(), "备注格式应有列宽");
        
        // 测试链接格式
        ColumnFormat link = CellFormatBuilder.link();
        assertNotNull(link, "链接格式不应为空");
        assertEquals(IndexedColors.BLUE.getIndex(), link.getFontColor(), "链接格式字体应为蓝色");
        
        // 测试代码格式
        ColumnFormat code = CellFormatBuilder.code();
        assertNotNull(code, "代码格式不应为空");
        assertEquals("Consolas", code.getFontName(), "代码格式字体应为Consolas");
    }
    
    /**
     * 测试批量创建格式配置
     */
    @Test
    @DisplayName("测试批量创建格式配置")
    void testCreateFormats() {
        String[] headers = {"序号", "姓名", "年龄", "工资", "日期"};
        
        // 创建格式映射
        Map<String, String> formatMap = new HashMap<>();
        formatMap.put("序号", "SERIAL");
        formatMap.put("姓名", "TEXT_CENTER");
        formatMap.put("年龄", "INTEGER");
        formatMap.put("工资", "CURRENCY");
        formatMap.put("日期", "DATE");
        
        // 批量创建格式
        Map<String, ColumnFormat> formats = CellFormatBuilder.createFormats(headers, formatMap);
        
        assertNotNull(formats, "格式Map不应为空");
        assertEquals(5, formats.size(), "应该有5个格式配置");
        assertNotNull(formats.get("序号"), "序号格式不应为空");
        assertNotNull(formats.get("姓名"), "姓名格式不应为空");
        assertNotNull(formats.get("年龄"), "年龄格式不应为空");
        assertNotNull(formats.get("工资"), "工资格式不应为空");
        assertNotNull(formats.get("日期"), "日期格式不应为空");
        
        // 验证格式类型
        assertEquals(ColumnFormat.DataType.INTEGER, formats.get("年龄").getDataType(), "年龄应为整数格式");
        assertEquals(ColumnFormat.DataType.CURRENCY, formats.get("工资").getDataType(), "工资应为货币格式");
        assertEquals(ColumnFormat.DataType.DATE, formats.get("日期").getDataType(), "日期应为日期格式");
    }
    
    /**
     * 测试使用不存在的格式类型
     */
    @Test
    @DisplayName("测试使用不存在的格式类型")
    void testInvalidFormatType() {
        String[] headers = {"列1", "列2"};
        
        Map<String, String> formatMap = new HashMap<>();
        formatMap.put("列1", "INVALID_TYPE");
        formatMap.put("列2", "TEXT");
        
        Map<String, ColumnFormat> formats = CellFormatBuilder.createFormats(headers, formatMap);
        
        // 不存在的类型应使用默认文本格式
        assertNotNull(formats.get("列1"), "无效类型应返回默认格式");
        assertEquals(ColumnFormat.DataType.TEXT, formats.get("列1").getDataType(), "无效类型应使用文本格式");
        assertNotNull(formats.get("列2"), "有效类型应返回对应格式");
    }
    
    /**
     * 测试预定义模板
     */
    @Test
    @DisplayName("测试预定义模板")
    void testTemplates() {
        // 测试用户列表模板
        Map<String, ColumnFormat> userListTemplate = CellFormatBuilder.createTemplate("USER_LIST");
        assertNotNull(userListTemplate, "用户列表模板不应为空");
        assertTrue(userListTemplate.size() > 0, "模板应包含格式配置");
        
        // 验证模板中的格式
        assertNotNull(userListTemplate.get("序号"), "模板应包含序号格式");
        assertNotNull(userListTemplate.get("姓名"), "模板应包含姓名格式");
        assertNotNull(userListTemplate.get("工资"), "模板应包含工资格式");
        
        // 测试财务报表模板
        Map<String, ColumnFormat> financialTemplate = CellFormatBuilder.createTemplate("FINANCIAL");
        assertNotNull(financialTemplate, "财务报表模板不应为空");
        
        // 测试销售报表模板
        Map<String, ColumnFormat> salesTemplate = CellFormatBuilder.createTemplate("SALES");
        assertNotNull(salesTemplate, "销售报表模板不应为空");
        
        // 测试库存报表模板
        Map<String, ColumnFormat> inventoryTemplate = CellFormatBuilder.createTemplate("INVENTORY");
        assertNotNull(inventoryTemplate, "库存报表模板不应为空");
        
        // 测试员工信息模板
        Map<String, ColumnFormat> employeeTemplate = CellFormatBuilder.createTemplate("EMPLOYEE");
        assertNotNull(employeeTemplate, "员工信息模板不应为空");
        
        // 测试不存在的模板（应返回空Map）
        Map<String, ColumnFormat> unknownTemplate = CellFormatBuilder.createTemplate("UNKNOWN");
        assertNotNull(unknownTemplate, "未知模板不应返回null");
        assertTrue(unknownTemplate.isEmpty(), "未知模板应返回空Map");
    }
    
    /**
     * 测试模板大小写不敏感
     */
    @Test
    @DisplayName("测试模板大小写不敏感")
    void testTemplateCaseInsensitive() {
        // 测试小写
        Map<String, ColumnFormat> template1 = CellFormatBuilder.createTemplate("user_list");
        assertNotNull(template1, "小写模板名称应有效");
        
        // 测试混合大小写
        Map<String, ColumnFormat> template2 = CellFormatBuilder.createTemplate("User_List");
        assertNotNull(template2, "混合大小写模板名称应有效");
        
        // 测试中文模板名称
        Map<String, ColumnFormat> template3 = CellFormatBuilder.createTemplate("用户列表");
        assertNotNull(template3, "中文模板名称应有效");
    }
    
    /**
     * 测试空格式映射
     */
    @Test
    @DisplayName("测试空格式映射")
    void testEmptyFormatMap() {
        String[] headers = {"列1", "列2"};
        Map<String, String> emptyMap = new HashMap<>();
        
        Map<String, ColumnFormat> formats = CellFormatBuilder.createFormats(headers, emptyMap);
        
        assertNotNull(formats, "格式Map不应为null");
        assertTrue(formats.isEmpty(), "空映射应返回空格式Map");
    }
    
    /**
     * 测试部分格式映射
     */
    @Test
    @DisplayName("测试部分格式映射")
    void testPartialFormatMap() {
        String[] headers = {"列1", "列2", "列3"};
        
        Map<String, String> formatMap = new HashMap<>();
        formatMap.put("列1", "TEXT_CENTER");
        formatMap.put("列3", "INTEGER");
        // 列2没有配置格式
        
        Map<String, ColumnFormat> formats = CellFormatBuilder.createFormats(headers, formatMap);
        
        assertEquals(2, formats.size(), "应该只有2个格式配置");
        assertNotNull(formats.get("列1"), "列1应有格式配置");
        assertNotNull(formats.get("列3"), "列3应有格式配置");
        assertNull(formats.get("列2"), "列2不应有格式配置");
    }
}
