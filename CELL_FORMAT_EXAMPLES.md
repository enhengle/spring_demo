# 单元格格式生成器使用示例

## 📋 概述

`CellFormatBuilder` 提供了便捷的方法来快速创建常用的 Excel 单元格格式，无需手动配置每个属性。

## 🚀 快速开始

```java
import com.practise.demo.util.CellFormatBuilder;
import com.practise.demo.util.ColumnFormat;
import java.util.*;

// 创建格式配置
Map<String, ColumnFormat> formats = new HashMap<>();
formats.put("序号", CellFormatBuilder.serialNumber());
formats.put("姓名", CellFormatBuilder.textCenter());
formats.put("工资", CellFormatBuilder.currencyCNY());
```

## 📝 基础格式

### 文本格式

```java
// 左对齐文本（默认）
CellFormatBuilder.text()

// 居中文本
CellFormatBuilder.textCenter()

// 右对齐文本
CellFormatBuilder.textRight()
```

### 数字格式

```java
// 整数（居中）
CellFormatBuilder.integer()

// 整数（右对齐）
CellFormatBuilder.integerRight()

// 小数（2位，右对齐）
CellFormatBuilder.decimal()

// 小数（自定义小数位数）
CellFormatBuilder.decimal(3)  // 3位小数
CellFormatBuilder.decimal(4)  // 4位小数
```

### 货币格式

```java
// 人民币（¥#,##0.00）
CellFormatBuilder.currencyCNY()

// 美元（$#,##0.00）
CellFormatBuilder.currencyUSD()

// 欧元（€#,##0.00）
CellFormatBuilder.currencyEUR()
```

### 日期格式

```java
// 日期（yyyy-MM-dd）
CellFormatBuilder.date()

// 自定义日期格式
CellFormatBuilder.date("yyyy/MM/dd")
CellFormatBuilder.date("yyyy年MM月dd日")
CellFormatBuilder.date("MM-dd-yyyy")

// 日期时间（yyyy-MM-dd HH:mm:ss）
CellFormatBuilder.dateTime()

// 自定义日期时间格式
CellFormatBuilder.dateTime("yyyy-MM-dd HH:mm")
CellFormatBuilder.dateTime("yyyy/MM/dd HH:mm:ss")
```

### 百分比格式

```java
// 百分比（0.00%）
CellFormatBuilder.percentage()

// 百分比（整数，0%）
CellFormatBuilder.percentageInteger()
```

### 布尔格式

```java
// 布尔值（居中）
CellFormatBuilder.bool()
```

## 🎨 特殊格式

### 标题和表头

```java
// 标题格式（加粗、居中、背景色、14号字体）
CellFormatBuilder.title()

// 表头格式（加粗、居中、浅灰背景、12号字体）
CellFormatBuilder.header()
```

### 状态格式

```java
// 强调格式（加粗、红色字体）
CellFormatBuilder.emphasis()

// 警告格式（黄色背景、深红字体）
CellFormatBuilder.warning()

// 成功格式（绿色背景、深绿字体）
CellFormatBuilder.success()

// 信息格式（蓝色背景、深蓝字体）
CellFormatBuilder.info()
```

### 专用格式

```java
// 金额格式（大号字体、加粗、右对齐）
CellFormatBuilder.amount()

// 序号格式（居中、整数、固定列宽）
CellFormatBuilder.serialNumber()

// 备注格式（自动换行、左对齐、宽列）
CellFormatBuilder.remark()

// 链接格式（蓝色字体）
CellFormatBuilder.link()

// 代码格式（等宽字体）
CellFormatBuilder.code()
```

## 💡 使用示例

### 示例 1：用户列表

```java
List<Map<String, Object>> dataList = new ArrayList<>();
// ... 添加数据 ...

String[] headers = {"序号", "姓名", "年龄", "工资", "入职日期", "邮箱", "状态"};

Map<String, ColumnFormat> formats = new HashMap<>();
formats.put("序号", CellFormatBuilder.serialNumber());
formats.put("姓名", CellFormatBuilder.textCenter());
formats.put("年龄", CellFormatBuilder.integer());
formats.put("工资", CellFormatBuilder.currencyCNY());
formats.put("入职日期", CellFormatBuilder.date());
formats.put("邮箱", CellFormatBuilder.text());
formats.put("状态", CellFormatBuilder.textCenter());

ExcelUtil.writeExcel("users.xlsx", dataList, headers, "用户列表", formats);
```

### 示例 2：财务报表

```java
String[] headers = {"日期", "收入", "支出", "余额", "增长率"};

Map<String, ColumnFormat> formats = new HashMap<>();
formats.put("日期", CellFormatBuilder.date());
formats.put("收入", CellFormatBuilder.currencyCNY());
formats.put("支出", CellFormatBuilder.currencyCNY());
formats.put("余额", CellFormatBuilder.amount());  // 使用金额格式
formats.put("增长率", CellFormatBuilder.percentage());

ExcelUtil.writeExcel("financial.xlsx", dataList, headers, "财务报表", formats);
```

### 示例 3：销售报表（带状态标识）

```java
String[] headers = {"日期", "产品", "数量", "单价", "金额", "状态"};

Map<String, ColumnFormat> formats = new HashMap<>();
formats.put("日期", CellFormatBuilder.date());
formats.put("产品", CellFormatBuilder.text());
formats.put("数量", CellFormatBuilder.integerRight());
formats.put("单价", CellFormatBuilder.currencyCNY());
formats.put("金额", CellFormatBuilder.amount());
// 状态列根据值动态设置格式（需要在写入时处理）

ExcelUtil.writeExcel("sales.xlsx", dataList, headers, "销售报表", formats);
```

### 示例 4：使用预定义模板

```java
// 使用用户列表模板
String[] headers = {"序号", "姓名", "年龄", "工资", "入职日期", "邮箱"};
Map<String, ColumnFormat> formats = CellFormatBuilder.createTemplate("USER_LIST");

ExcelUtil.writeExcel("users.xlsx", dataList, headers, "用户列表", formats);
```

### 示例 5：批量创建格式（使用格式映射）

```java
String[] headers = {"序号", "姓名", "年龄", "工资", "日期"};

// 定义格式映射
Map<String, String> formatMap = new HashMap<>();
formatMap.put("序号", "SERIAL");
formatMap.put("姓名", "TEXT_CENTER");
formatMap.put("年龄", "INTEGER");
formatMap.put("工资", "CURRENCY");
formatMap.put("日期", "DATE");

// 批量创建格式
Map<String, ColumnFormat> formats = CellFormatBuilder.createFormats(headers, formatMap);

ExcelUtil.writeExcel("data.xlsx", dataList, headers, "数据", formats);
```

## 📋 支持的格式类型字符串

使用 `createFormats()` 方法时，支持以下格式类型字符串：

| 格式类型 | 说明 |
|---------|------|
| `TEXT` / `STRING` | 文本（左对齐） |
| `TEXT_CENTER` | 文本（居中） |
| `TEXT_RIGHT` | 文本（右对齐） |
| `INTEGER` / `INT` | 整数（居中） |
| `INTEGER_RIGHT` / `INT_RIGHT` | 整数（右对齐） |
| `DECIMAL` / `DOUBLE` / `FLOAT` | 小数（2位） |
| `PERCENTAGE` / `PERCENT` | 百分比 |
| `PERCENTAGE_INTEGER` | 百分比（整数） |
| `CURRENCY` / `CNY` / `RMB` | 人民币 |
| `USD` | 美元 |
| `EUR` | 欧元 |
| `DATE` | 日期 |
| `DATETIME` / `DATE_TIME` | 日期时间 |
| `BOOL` / `BOOLEAN` | 布尔值 |
| `TITLE` | 标题格式 |
| `HEADER` | 表头格式 |
| `EMPHASIS` | 强调格式 |
| `WARNING` | 警告格式 |
| `SUCCESS` | 成功格式 |
| `INFO` | 信息格式 |
| `AMOUNT` | 金额格式 |
| `SERIAL` / `SERIAL_NUMBER` | 序号格式 |
| `REMARK` / `NOTE` | 备注格式 |
| `LINK` | 链接格式 |
| `CODE` | 代码格式 |

## 🎯 预定义模板

### 用户列表模板（USER_LIST / 用户列表）

```java
Map<String, ColumnFormat> formats = CellFormatBuilder.createTemplate("USER_LIST");
// 包含：序号、姓名、年龄、工资、入职日期、邮箱
```

### 财务报表模板（FINANCIAL / 财务报表）

```java
Map<String, ColumnFormat> formats = CellFormatBuilder.createTemplate("FINANCIAL");
// 包含：日期、收入、支出、余额、增长率
```

### 销售报表模板（SALES / 销售报表）

```java
Map<String, ColumnFormat> formats = CellFormatBuilder.createTemplate("SALES");
// 包含：日期、产品名称、数量、单价、金额、利润率
```

### 库存报表模板（INVENTORY / 库存报表）

```java
Map<String, ColumnFormat> formats = CellFormatBuilder.createTemplate("INVENTORY");
// 包含：产品编号、产品名称、库存数量、单价、总价值、备注
```

### 员工信息模板（EMPLOYEE / 员工信息）

```java
Map<String, ColumnFormat> formats = CellFormatBuilder.createTemplate("EMPLOYEE");
// 包含：工号、姓名、部门、职位、入职日期、工资、状态
```

## 🔧 自定义格式

如果需要更精细的控制，可以基于 `CellFormatBuilder` 创建的方法进行扩展：

```java
// 基于现有格式扩展
ColumnFormat customFormat = CellFormatBuilder.currencyCNY()
    .withFont("Arial", (short) 13)
    .withBold(true)
    .withFontColor(IndexedColors.DARK_BLUE.getIndex())
    .withColumnWidth(15);

// 或直接使用 ColumnFormat
ColumnFormat customFormat2 = ColumnFormat.currency()
    .withNumberFormat("¥#,##0.00")
    .withAlignment(ColumnFormat.Alignment.RIGHT)
    .withBold(true)
    .withFont("微软雅黑", (short) 12)
    .withBackgroundColor(IndexedColors.LIGHT_YELLOW.getIndex());
```

## 📚 完整示例

```java
import com.practise.demo.util.CellFormatBuilder;
import com.practise.demo.util.ExcelUtil;
import org.apache.poi.ss.usermodel.IndexedColors;

public class ExcelExportExample {
    
    public void exportUserReport() {
        // 准备数据
        List<Map<String, Object>> dataList = new ArrayList<>();
        // ... 添加数据 ...
        
        // 定义表头
        String[] headers = {
            "序号", "姓名", "部门", "职位", "入职日期", 
            "基本工资", "绩效奖金", "总工资", "增长率", "状态"
        };
        
        // 创建格式配置
        Map<String, ColumnFormat> formats = new HashMap<>();
        formats.put("序号", CellFormatBuilder.serialNumber());
        formats.put("姓名", CellFormatBuilder.textCenter());
        formats.put("部门", CellFormatBuilder.textCenter());
        formats.put("职位", CellFormatBuilder.textCenter());
        formats.put("入职日期", CellFormatBuilder.date());
        formats.put("基本工资", CellFormatBuilder.currencyCNY());
        formats.put("绩效奖金", CellFormatBuilder.currencyCNY());
        formats.put("总工资", CellFormatBuilder.amount());  // 使用金额格式
        formats.put("增长率", CellFormatBuilder.percentage());
        formats.put("状态", CellFormatBuilder.textCenter());
        
        // 导出 Excel
        ExcelUtil.writeExcel("user_report.xlsx", dataList, headers, "员工报表", formats);
    }
}
```

## ⚠️ 注意事项

1. **格式配置是可选的**：如果不提供格式配置，将使用默认格式
2. **格式 key 必须匹配表头**：格式配置 Map 的 key 必须与表头数组中的值完全匹配
3. **模板使用**：使用预定义模板时，确保表头与模板中定义的列名匹配
4. **格式优先级**：如果同时提供了格式配置和模板，格式配置会覆盖模板中的同名列格式
