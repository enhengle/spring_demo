# Excel 操作使用指南

## 📋 概述

本项目提供了完整的 Excel 操作功能，支持 `.xls` 和 `.xlsx` 格式的文件读写操作。

## 🚀 功能特性

- ✅ 读取 Excel 文件（支持 .xls 和 .xlsx）
- ✅ 写入 Excel 文件
- ✅ 支持表头读取/写入
- ✅ 支持多工作表操作
- ✅ 对象列表与 Excel 互转
- ✅ Web 导出功能
- ✅ 自动列宽调整
- ✅ 自定义样式支持
- ✅ **列格式配置（数据类型、日期格式、数字格式、对齐方式、字体样式等）**

## 📦 依赖

项目使用 Apache POI 5.2.3 版本：

```xml
<dependency>
    <groupId>org.apache.poi</groupId>
    <artifactId>poi</artifactId>
    <version>5.2.3</version>
</dependency>
<dependency>
    <groupId>org.apache.poi</groupId>
    <artifactId>poi-ooxml</artifactId>
    <version>5.2.3</version>
</dependency>
```

## 🔧 工具类方法

### ExcelUtil 工具类

#### 1. 读取 Excel 文件

```java
// 读取 Excel 文件（默认第一个工作表，包含表头）
List<Map<String, Object>> dataList = ExcelUtil.readExcel("path/to/file.xlsx", true);

// 读取指定工作表
List<Map<String, Object>> dataList = ExcelUtil.readExcel("path/to/file.xlsx", true, 0);

// 读取为对象列表
List<User> users = ExcelUtil.readExcel("path/to/file.xlsx", User.class, true);
```

#### 2. 写入 Excel 文件

```java
// 准备数据
List<Map<String, Object>> dataList = new ArrayList<>();
Map<String, Object> row1 = new LinkedHashMap<>();
row1.put("姓名", "张三");
row1.put("年龄", 25);
row1.put("工资", 10000.50);
row1.put("入职日期", new Date());
dataList.add(row1);

String[] headers = {"姓名", "年龄", "工资", "入职日期"};

// 写入 Excel（不设置格式）
ExcelUtil.writeExcel("output.xlsx", dataList, headers, "用户数据");

// 使用 Map 的 key 作为表头
ExcelUtil.writeExcel("output.xlsx", dataList, "用户数据");

// 写入 Excel（设置列格式）
Map<String, ColumnFormat> columnFormats = new HashMap<>();
columnFormats.put("姓名", ColumnFormat.text().withAlignment(ColumnFormat.Alignment.CENTER));
columnFormats.put("年龄", ColumnFormat.integer().withAlignment(ColumnFormat.Alignment.CENTER));
columnFormats.put("工资", ColumnFormat.currency().withNumberFormat("¥#,##0.00"));
columnFormats.put("入职日期", ColumnFormat.date().withDateFormat("yyyy-MM-dd"));

ExcelUtil.writeExcel("output.xlsx", dataList, headers, "用户数据", columnFormats);
```

#### 3. 写入对象列表

```java
List<User> users = Arrays.asList(
    new User("张三", 25),
    new User("李四", 30)
);

String[] headers = {"name", "age"};
ExcelUtil.writeExcelFromObjects("users.xlsx", users, headers, "用户列表");
```

#### 4. 获取工作表信息

```java
// 获取所有工作表名称
List<String> sheetNames = ExcelUtil.getSheetNames("file.xlsx");

// 获取指定工作表的行数
int rowCount = ExcelUtil.getRowCount("file.xlsx", 0);
```

#### 5. 导出到输出流（用于 Web 下载）

```java
// 不设置格式
OutputStream outputStream = response.getOutputStream();
String[] headers = {"姓名", "年龄"};
ExcelUtil.exportToStream(outputStream, dataList, headers, "导出数据");

// 设置列格式
Map<String, ColumnFormat> columnFormats = new HashMap<>();
columnFormats.put("姓名", ColumnFormat.text());
columnFormats.put("年龄", ColumnFormat.integer());
ExcelUtil.exportToStream(outputStream, dataList, headers, "导出数据", columnFormats);
```

#### 6. 列格式配置（ColumnFormat）

`ColumnFormat` 类提供了丰富的格式配置选项：

```java
// 创建格式配置
ColumnFormat format = ColumnFormat.number()
    .withNumberFormat("#,##0.00")           // 数字格式
    .withAlignment(ColumnFormat.Alignment.RIGHT)  // 右对齐
    .withFont("微软雅黑", (short) 12)        // 字体和大小
    .withBold(true)                         // 加粗
    .withFontColor(IndexedColors.BLUE.getIndex())  // 字体颜色
    .withBackgroundColor(IndexedColors.YELLOW.getIndex())  // 背景色
    .withWrapText(true)                     // 自动换行
    .withColumnWidth(15);                   // 列宽（字符数）

// 常用格式类型
ColumnFormat.text()              // 文本格式
ColumnFormat.number()            // 数字格式
ColumnFormat.integer()           // 整数格式
ColumnFormat.decimal()           // 小数格式
ColumnFormat.date()              // 日期格式
ColumnFormat.dateTime()          // 日期时间格式
ColumnFormat.percentage()        // 百分比格式
ColumnFormat.currency()          // 货币格式
ColumnFormat.bool()              // 布尔格式

// 日期格式示例
ColumnFormat.date()
    .withDateFormat("yyyy-MM-dd")           // 日期格式
    .withAlignment(ColumnFormat.Alignment.CENTER);

// 货币格式示例
ColumnFormat.currency()
    .withNumberFormat("¥#,##0.00")          // 货币格式
    .withAlignment(ColumnFormat.Alignment.RIGHT);
```

#### 7. 单元格格式生成器（CellFormatBuilder）

`CellFormatBuilder` 提供了便捷的格式生成方法，快速创建常用格式：

```java
import com.practise.demo.util.CellFormatBuilder;

// 基础格式
CellFormatBuilder.text()              // 文本（左对齐）
CellFormatBuilder.textCenter()        // 文本（居中）
CellFormatBuilder.textRight()         // 文本（右对齐）
CellFormatBuilder.integer()           // 整数（居中）
CellFormatBuilder.integerRight()      // 整数（右对齐）
CellFormatBuilder.decimal()           // 小数（2位，右对齐）
CellFormatBuilder.decimal(3)          // 小数（3位，右对齐）

// 货币格式
CellFormatBuilder.currencyCNY()       // 人民币（¥#,##0.00）
CellFormatBuilder.currencyUSD()       // 美元（$#,##0.00）
CellFormatBuilder.currencyEUR()       // 欧元（€#,##0.00）

// 日期格式
CellFormatBuilder.date()              // 日期（yyyy-MM-dd）
CellFormatBuilder.date("yyyy/MM/dd")  // 自定义日期格式
CellFormatBuilder.dateTime()          // 日期时间（yyyy-MM-dd HH:mm:ss）

// 百分比格式
CellFormatBuilder.percentage()        // 百分比（0.00%）
CellFormatBuilder.percentageInteger() // 百分比（0%）

// 特殊格式
CellFormatBuilder.title()             // 标题格式（加粗、居中、背景色）
CellFormatBuilder.header()            // 表头格式（加粗、居中、浅灰背景）
CellFormatBuilder.emphasis()          // 强调格式（加粗、红色）
CellFormatBuilder.warning()           // 警告格式（黄色背景）
CellFormatBuilder.success()           // 成功格式（绿色背景）
CellFormatBuilder.info()              // 信息格式（蓝色背景）
CellFormatBuilder.amount()            // 金额格式（大号、加粗、右对齐）
CellFormatBuilder.serialNumber()      // 序号格式（居中、整数）
CellFormatBuilder.remark()            // 备注格式（自动换行）
CellFormatBuilder.link()              // 链接格式（蓝色字体）
CellFormatBuilder.code()              // 代码格式（等宽字体）

// 使用示例
Map<String, ColumnFormat> formats = new HashMap<>();
formats.put("序号", CellFormatBuilder.serialNumber());
formats.put("姓名", CellFormatBuilder.textCenter());
formats.put("年龄", CellFormatBuilder.integer());
formats.put("工资", CellFormatBuilder.currencyCNY());
formats.put("入职日期", CellFormatBuilder.date());
formats.put("邮箱", CellFormatBuilder.text());

ExcelUtil.writeExcel("output.xlsx", dataList, headers, "用户数据", formats);
```

#### 8. 批量创建格式配置

```java
// 方式1：使用格式映射
String[] headers = {"序号", "姓名", "年龄", "工资", "日期"};
Map<String, String> formatMap = new HashMap<>();
formatMap.put("序号", "SERIAL");
formatMap.put("姓名", "TEXT_CENTER");
formatMap.put("年龄", "INTEGER");
formatMap.put("工资", "CURRENCY");
formatMap.put("日期", "DATE");

Map<String, ColumnFormat> formats = CellFormatBuilder.createFormats(headers, formatMap);

// 方式2：使用预定义模板
Map<String, ColumnFormat> formats = CellFormatBuilder.createTemplate("USER_LIST");
// 支持的模板：
// - "USER_LIST" / "用户列表"
// - "FINANCIAL" / "财务报表"
// - "SALES" / "销售报表"
// - "INVENTORY" / "库存报表"
// - "EMPLOYEE" / "员工信息"
```

## 🌐 REST API 接口

### 1. 读取 Excel 文件

**接口地址：** `POST /api/excel/read`

**请求参数：**
- `file` (MultipartFile): Excel 文件
- `hasHeader` (boolean, 可选, 默认 true): 是否包含表头
- `sheetIndex` (int, 可选, 默认 0): 工作表索引

**示例：**
```bash
curl -X POST "http://localhost:8080/api/excel/read" \
  -F "file=@data.xlsx" \
  -F "hasHeader=true" \
  -F "sheetIndex=0"
```

**响应示例：**
```json
{
  "code": 0,
  "message": "操作成功",
  "data": [
    {
      "姓名": "张三",
      "年龄": 25,
      "邮箱": "zhangsan@example.com"
    },
    {
      "姓名": "李四",
      "年龄": 30,
      "邮箱": "lisi@example.com"
    }
  ]
}
```

### 2. 获取工作表列表

**接口地址：** `POST /api/excel/sheets`

**请求参数：**
- `file` (MultipartFile): Excel 文件

**示例：**
```bash
curl -X POST "http://localhost:8080/api/excel/sheets" \
  -F "file=@data.xlsx"
```

**响应示例：**
```json
{
  "code": 0,
  "message": "操作成功",
  "data": ["Sheet1", "Sheet2", "Sheet3"]
}
```

### 3. 获取行数

**接口地址：** `POST /api/excel/row-count`

**请求参数：**
- `file` (MultipartFile): Excel 文件
- `sheetIndex` (int, 可选, 默认 0): 工作表索引

**示例：**
```bash
curl -X POST "http://localhost:8080/api/excel/row-count" \
  -F "file=@data.xlsx" \
  -F "sheetIndex=0"
```

**响应示例：**
```json
{
  "code": 0,
  "message": "操作成功",
  "data": 100
}
```

### 4. 导出 Excel 文件（支持格式配置）

**接口地址：** `POST /api/excel/export`

**请求体（JSON）：**
```json
{
  "dataList": [
    {"姓名": "张三", "年龄": 25, "工资": 10000.50, "入职日期": "2023-01-01"},
    {"姓名": "李四", "年龄": 30, "工资": 15000.00, "入职日期": "2022-06-15"}
  ],
  "headers": ["姓名", "年龄", "工资", "入职日期"],
  "sheetName": "用户数据",
  "fileName": "users.xlsx",
  "columnFormats": {
    "姓名": {
      "dataType": "TEXT",
      "alignment": "CENTER",
      "fontName": "微软雅黑",
      "fontSize": 12,
      "bold": true
    },
    "年龄": {
      "dataType": "INTEGER",
      "alignment": "CENTER"
    },
    "工资": {
      "dataType": "CURRENCY",
      "numberFormat": "¥#,##0.00",
      "alignment": "RIGHT"
    },
    "入职日期": {
      "dataType": "DATE",
      "dateFormat": "yyyy-MM-dd",
      "alignment": "CENTER"
    }
  }
}
```

**字段说明：**
- `dataList`: 数据列表（必填）
- `headers`: 表头数组（可选，如果不提供则使用 Map 的 key）
- `sheetName`: 工作表名称（可选，默认 "Sheet1"）
- `fileName`: 文件名（可选，默认 "export.xlsx"）
- `columnFormats`: 列格式配置 Map（可选）
  - `dataType`: 数据类型（TEXT, NUMBER, INTEGER, DECIMAL, DATE, DATETIME, PERCENTAGE, CURRENCY, BOOLEAN）
  - `dateFormat`: 日期格式（如 "yyyy-MM-dd"）
  - `numberFormat`: 数字格式（如 "#,##0.00"）
  - `alignment`: 对齐方式（LEFT, CENTER, RIGHT, JUSTIFY）
  - `fontName`: 字体名称
  - `fontSize`: 字体大小
  - `bold`: 是否加粗
  - `fontColor`: 字体颜色索引（参考 IndexedColors）
  - `backgroundColor`: 背景颜色索引
  - `wrapText`: 是否自动换行
  - `columnWidth`: 列宽（字符数）

**示例：**
```bash
curl -X POST "http://localhost:8080/api/excel/export" \
  -H "Content-Type: application/json" \
  -d '{
    "dataList": [
      {"姓名": "张三", "年龄": 25, "工资": 10000.50},
      {"姓名": "李四", "年龄": 30, "工资": 15000.00}
    ],
    "headers": ["姓名", "年龄", "工资"],
    "columnFormats": {
      "工资": {
        "dataType": "CURRENCY",
        "numberFormat": "¥#,##0.00"
      }
    }
  }' \
  --output users.xlsx
```

### 4.1. 导出 Excel 文件（简化版，不支持格式配置）

**接口地址：** `POST /api/excel/export-simple`

**请求参数：**
- `dataList` (List<Map<String, Object>>): 数据列表（JSON 格式）
- `headers` (String[], 可选): 表头数组，如果不提供则使用 Map 的 key
- `sheetName` (String, 可选, 默认 "Sheet1"): 工作表名称
- `fileName` (String, 可选, 默认 "export.xlsx"): 文件名

**示例：**
```bash
curl -X POST "http://localhost:8080/api/excel/export-simple?sheetName=用户数据&fileName=users.xlsx" \
  -H "Content-Type: application/json" \
  -d '[
    {"姓名": "张三", "年龄": 25, "邮箱": "zhangsan@example.com"},
    {"姓名": "李四", "年龄": 30, "邮箱": "lisi@example.com"}
  ]' \
  --output users.xlsx
```

### 5. 导出示例数据

**接口地址：** `GET /api/excel/export-sample`

**示例：**
```bash
curl -X GET "http://localhost:8080/api/excel/export-sample" \
  --output sample.xlsx
```

## 💡 使用示例

### 示例 1：读取用户数据

```java
@Service
public class UserService {
    
    public void importUsersFromExcel(String filePath) {
        List<Map<String, Object>> dataList = ExcelUtil.readExcel(filePath, true);
        
        for (Map<String, Object> row : dataList) {
            String name = (String) row.get("姓名");
            Integer age = ((Number) row.get("年龄")).intValue();
            String email = (String) row.get("邮箱");
            
            // 保存到数据库
            User user = new User();
            user.setName(name);
            user.setAge(age);
            user.setEmail(email);
            // userRepository.save(user);
        }
    }
}
```

### 示例 2：导出用户数据（带格式配置）

```java
@RestController
@RequestMapping("/api/users")
public class UserController {
    
    @GetMapping("/export")
    public void exportUsers(HttpServletResponse response) throws IOException {
        // 查询用户数据
        List<User> users = userService.findAll();
        
        // 转换为 Map 列表
        List<Map<String, Object>> dataList = users.stream()
            .map(user -> {
                Map<String, Object> map = new LinkedHashMap<>();
                map.put("ID", user.getId());
                map.put("姓名", user.getName());
                map.put("年龄", user.getAge());
                map.put("工资", user.getSalary());
                map.put("入职日期", user.getJoinDate());
                map.put("邮箱", user.getEmail());
                return map;
            })
            .collect(Collectors.toList());
        
        // 设置响应头
        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        response.setHeader("Content-Disposition", "attachment; filename=users.xlsx");
        
        // 配置列格式
        Map<String, ColumnFormat> columnFormats = new HashMap<>();
        columnFormats.put("ID", ColumnFormat.integer().withAlignment(ColumnFormat.Alignment.CENTER));
        columnFormats.put("姓名", ColumnFormat.text().withAlignment(ColumnFormat.Alignment.CENTER));
        columnFormats.put("年龄", ColumnFormat.integer().withAlignment(ColumnFormat.Alignment.CENTER));
        columnFormats.put("工资", ColumnFormat.currency()
            .withNumberFormat("¥#,##0.00")
            .withAlignment(ColumnFormat.Alignment.RIGHT));
        columnFormats.put("入职日期", ColumnFormat.date()
            .withDateFormat("yyyy-MM-dd")
            .withAlignment(ColumnFormat.Alignment.CENTER));
        columnFormats.put("邮箱", ColumnFormat.text());
        
        // 导出
        String[] headers = {"ID", "姓名", "年龄", "工资", "入职日期", "邮箱"};
        ExcelUtil.exportToStream(response.getOutputStream(), dataList, headers, "用户列表", columnFormats);
    }
}
```

### 示例 3：处理多工作表

```java
public void processMultiSheetExcel(String filePath) {
    // 获取所有工作表名称
    List<String> sheetNames = ExcelUtil.getSheetNames(filePath);
    
    for (int i = 0; i < sheetNames.size(); i++) {
        String sheetName = sheetNames.get(i);
        System.out.println("处理工作表: " + sheetName);
        
        // 读取每个工作表
        List<Map<String, Object>> dataList = ExcelUtil.readExcel(filePath, true, i);
        
        // 处理数据
        processData(sheetName, dataList);
    }
}
```

## ⚠️ 注意事项

1. **文件格式支持**：支持 `.xls`（Excel 97-2003）和 `.xlsx`（Excel 2007+）格式
2. **内存使用**：大文件（>100MB）建议使用流式处理，避免一次性加载到内存
3. **数据类型**：读取时数字类型会自动识别为 `Double` 或 `Long`，日期类型识别为 `Date`
4. **表头处理**：如果 `hasHeader=true`，第一行会被作为表头，Map 的 key 为表头内容
5. **空值处理**：空单元格读取为 `null`，写入时 `null` 值会写入为空白单元格
6. **列宽调整**：写入 Excel 后会自动调整列宽，但可能不够精确，可以通过 `ColumnFormat.setColumnWidth()` 手动设置
7. **格式配置**：列格式配置是可选的，如果不提供则使用默认格式。格式配置的 key 必须与表头（headers）完全匹配
8. **日期格式**：日期格式字符串遵循 Java SimpleDateFormat 规范，如 "yyyy-MM-dd"、"yyyy-MM-dd HH:mm:ss" 等
9. **数字格式**：数字格式字符串遵循 Excel 格式规范，如 "#,##0.00"、"0.00%" 等
10. **颜色索引**：字体颜色和背景颜色使用 IndexedColors 的索引值，可通过 `IndexedColors.COLOR_NAME.getIndex()` 获取

## 🧪 测试

运行测试用例：

```bash
mvn test -Dtest=ExcelUtilTest
```

测试覆盖：
- ✅ 读写 Excel 文件
- ✅ 表头处理
- ✅ 多工作表操作
- ✅ 不同格式支持（.xls 和 .xlsx）
- ✅ 空数据处理
- ✅ 流式导出

## 📚 相关文档

- [Apache POI 官方文档](https://poi.apache.org/)
- [POI API 文档](https://poi.apache.org/apidocs/index.html)
