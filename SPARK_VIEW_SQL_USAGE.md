# Spark 临时视图和 SQL 处理使用指南

本文档介绍如何使用 `SparkUtils` 进行临时视图管理和 SQL 处理。

## 目录
- [临时视图管理](#临时视图管理)
- [SQL 校验](#sql-校验)
- [SQL 解析](#sql-解析)
- [完整示例](#完整示例)

---

## 临时视图管理

### 1. 创建临时视图

```java
// 读取数据
Dataset<Row> df = SparkUtils.readCsv("data/users.csv", true);

// 创建临时视图
SparkUtils.createTempView(df, "users");
```

### 2. 检查临时视图是否存在

```java
// 检查视图是否存在
boolean exists = SparkUtils.tempViewExists("users");
if (exists) {
    System.out.println("视图 users 存在");
}
```

### 3. 删除临时视图

```java
// 删除临时视图
boolean deleted = SparkUtils.dropTempView("users");
if (deleted) {
    System.out.println("视图删除成功");
} else {
    System.out.println("视图不存在或删除失败");
}
```

**注意**：
- 虽然不连接 Hive，临时视图在 SparkSession 关闭时会自动清理
- 但显式删除是个好习惯，特别是在创建大量临时视图时，可以及时释放资源
- 建议在使用完视图后及时删除，避免内存占用

### 4. 列出所有临时视图

```java
// 获取所有临时视图名称
List<String> views = SparkUtils.listTempViews();
for (String viewName : views) {
    System.out.println("临时视图: " + viewName);
}
```

---

## SQL 校验

### 校验 SQL 语法

```java
// 校验 SQL 语法
String sql = "SELECT name, age FROM users WHERE age > 25";
SqlValidationResult result = SparkUtils.validateSql(sql);

if (result.isValid()) {
    System.out.println("SQL 语法正确: " + result.getMessage());
    // 执行 SQL
    Dataset<Row> df = SparkUtils.executeSql(sql);
} else {
    System.out.println("SQL 语法错误: " + result.getMessage());
}
```

### 校验示例

```java
// 正确的 SQL
String validSql = "SELECT * FROM users";
SqlValidationResult result1 = SparkUtils.validateSql(validSql);
// 输出: SQL 语法正确

// 错误的 SQL
String invalidSql = "SELEC * FROM users";  // 拼写错误
SqlValidationResult result2 = SparkUtils.validateSql(invalidSql);
// 输出: SQL 语法错误: ...
```

---

## SQL 解析

### 解析 SELECT SQL 获取表名和字段信息

```java
// 准备数据
Dataset<Row> users = SparkUtils.readCsv("data/users.csv", true);
SparkUtils.createTempView(users, "users");

// 解析 SQL
String sql = "SELECT name, age, city FROM users WHERE age > 25";
SqlParseResult parseResult = SparkUtils.parseSelectSql(sql);

if (parseResult.isSuccess()) {
    // 获取表名
    List<String> tableNames = parseResult.getTableNames();
    System.out.println("涉及的表: " + tableNames);
    // 输出: [users]
    
    // 获取字段名
    List<String> columns = parseResult.getColumns();
    System.out.println("查询的字段: " + columns);
    // 输出: [name, age, city]
    
    // 获取字段类型（如果表存在）
    Map<String, String> columnTypes = parseResult.getColumnTypes();
    System.out.println("字段类型: " + columnTypes);
    // 输出: {name=StringType, age=IntegerType, city=StringType}
} else {
    System.out.println("解析失败: " + parseResult.getMessage());
}
```

### 解析 JOIN 查询

```java
// 准备数据
Dataset<Row> users = SparkUtils.readCsv("data/users.csv", true);
Dataset<Row> orders = SparkUtils.readCsv("data/orders.csv", true);
SparkUtils.createTempView(users, "users");
SparkUtils.createTempView(orders, "orders");

// 解析 JOIN SQL
String sql = "SELECT u.name, o.order_id FROM users u JOIN orders o ON u.id = o.user_id";
SqlParseResult result = SparkUtils.parseSelectSql(sql);

if (result.isSuccess()) {
    System.out.println("表名: " + result.getTableNames());
    // 输出: [users, orders]
    
    System.out.println("字段: " + result.getColumns());
    // 输出: [name, order_id]
}
```

### 解析聚合查询

```java
String sql = "SELECT COUNT(*) as count, AVG(age) as avg_age FROM users";
SqlParseResult result = SparkUtils.parseSelectSql(sql);

if (result.isSuccess()) {
    System.out.println("字段: " + result.getColumns());
    // 输出: [count, avg_age] 或相关字段
}
```

---

## 完整示例

### 示例 1: 完整的视图生命周期管理

```java
// 1. 读取数据
Dataset<Row> df = SparkUtils.readCsv("data/users.csv", true);

// 2. 创建临时视图
SparkUtils.createTempView(df, "users");
System.out.println("视图创建成功");

// 3. 检查视图是否存在
if (SparkUtils.tempViewExists("users")) {
    System.out.println("视图 users 存在");
}

// 4. 校验 SQL
String sql = "SELECT name, age FROM users WHERE age > 25";
SqlValidationResult validation = SparkUtils.validateSql(sql);
if (validation.isValid()) {
    // 5. 解析 SQL
    SqlParseResult parseResult = SparkUtils.parseSelectSql(sql);
    System.out.println("表名: " + parseResult.getTableNames());
    System.out.println("字段: " + parseResult.getColumns());
    
    // 6. 执行 SQL
    Dataset<Row> result = SparkUtils.executeSql(sql);
    SparkUtils.show(result);
}

// 7. 列出所有视图
List<String> views = SparkUtils.listTempViews();
System.out.println("当前临时视图: " + views);

// 8. 删除视图
SparkUtils.dropTempView("users");
System.out.println("视图已删除");
```

### 示例 2: SQL 校验和解析在 API 中的应用

```java
@RestController
@RequestMapping("/spark")
public class SparkController {

    @PostMapping("/validate-sql")
    public Map<String, Object> validateSql(@RequestParam String sql) {
        SqlValidationResult result = SparkUtils.validateSql(sql);
        Map<String, Object> response = new HashMap<>();
        response.put("valid", result.isValid());
        response.put("message", result.getMessage());
        return response;
    }

    @PostMapping("/parse-sql")
    public SqlParseResult parseSql(@RequestParam String sql) {
        return SparkUtils.parseSelectSql(sql);
    }

    @PostMapping("/execute-sql")
    public List<Map<String, Object>> executeSql(@RequestParam String sql) {
        // 先校验 SQL
        SqlValidationResult validation = SparkUtils.validateSql(sql);
        if (!validation.isValid()) {
            throw new IllegalArgumentException("SQL 语法错误: " + validation.getMessage());
        }
        
        // 执行 SQL
        Dataset<Row> result = SparkUtils.executeSql(sql);
        return SparkUtils.datasetToMapList(result);
    }
}
```

### 示例 3: 批量视图管理

```java
// 创建多个视图
String[] csvFiles = {"users.csv", "orders.csv", "products.csv"};
for (String file : csvFiles) {
    String viewName = file.replace(".csv", "");
    Dataset<Row> df = SparkUtils.readCsv("data/" + file, true);
    SparkUtils.createTempView(df, viewName);
    System.out.println("创建视图: " + viewName);
}

// 列出所有视图
List<String> views = SparkUtils.listTempViews();
System.out.println("所有视图: " + views);

// 清理所有视图
for (String viewName : views) {
    SparkUtils.dropTempView(viewName);
    System.out.println("删除视图: " + viewName);
}
```

---

## 注意事项

### 1. 临时视图的生命周期
- 临时视图只在当前 SparkSession 中有效
- SparkSession 关闭时，所有临时视图自动清理
- 不连接 Hive，所以不会影响 Hive 元数据

### 2. 显式删除的好处
- **内存管理**: 及时释放视图占用的内存
- **资源清理**: 避免视图积累导致的内存问题
- **代码清晰**: 明确的生命周期管理

### 3. SQL 解析限制
- 主要支持简单的 SELECT 语句
- 复杂子查询可能无法完全解析
- 建议先校验 SQL，再执行

### 4. 性能考虑
- SQL 校验会解析 SQL，有一定开销
- 对于频繁执行的 SQL，可以缓存校验结果
- 视图创建和删除操作很快，可以放心使用

---

## API 参考

### 临时视图管理

| 方法 | 说明 | 返回值 |
|------|------|--------|
| `createTempView(Dataset, String)` | 创建临时视图 | void |
| `dropTempView(String)` | 删除临时视图 | boolean |
| `tempViewExists(String)` | 检查视图是否存在 | boolean |
| `listTempViews()` | 列出所有临时视图 | List<String> |

### SQL 处理

| 方法 | 说明 | 返回值 |
|------|------|--------|
| `validateSql(String)` | 校验 SQL 语法 | SqlValidationResult |
| `parseSelectSql(String)` | 解析 SELECT SQL | SqlParseResult |

### 结果类

**SqlValidationResult**
- `isValid()`: 是否有效
- `getMessage()`: 消息

**SqlParseResult**
- `isSuccess()`: 是否成功
- `getMessage()`: 消息
- `getTableNames()`: 表名列表
- `getColumns()`: 字段列表
- `getColumnTypes()`: 字段类型映射

---

更多信息请参考 [README.md](README.md) 和 [SPARK_USAGE_EXAMPLES.md](SPARK_USAGE_EXAMPLES.md)
