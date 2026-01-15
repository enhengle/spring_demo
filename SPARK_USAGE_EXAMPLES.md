# SparkUtils 使用示例

本文档提供了 `SparkUtils` 工具类的详细使用示例。

## 目录
- [基础示例](#基础示例)
- [数据读取示例](#数据读取示例)
- [数据处理示例](#数据处理示例)
- [数据写入示例](#数据写入示例)
- [高级示例](#高级示例)

---

## 基础示例

### 1. 初始化 SparkSession

```java
import com.practise.demo.utils.SparkUtils;
import org.apache.spark.sql.SparkSession;

// 获取默认 SparkSession
SparkSession spark = SparkUtils.getSparkSession();

// 获取指定名称的 SparkSession
SparkSession spark = SparkUtils.getSparkSession("MyApplication");
```

---

## 数据读取示例

### 2. 读取 CSV 文件

```java
// 读取带表头的 CSV 文件
Dataset<Row> df = SparkUtils.readCsv("data/users.csv", true);

// 读取不带表头的 CSV 文件
Dataset<Row> df = SparkUtils.readCsv("data/data.csv", false);

// 查看数据
SparkUtils.show(df, 10);
SparkUtils.printSchema(df);
```

**CSV 文件示例 (users.csv):**
```csv
id,name,age,city
1,Alice,25,Beijing
2,Bob,30,Shanghai
3,Charlie,28,Guangzhou
```

### 3. 读取 JSON 文件

```java
// 读取 JSON 文件
Dataset<Row> df = SparkUtils.readJson("data/users.json");

// 查看数据
SparkUtils.show(df);
```

**JSON 文件示例 (users.json):**
```json
{"id":1,"name":"Alice","age":25,"city":"Beijing"}
{"id":2,"name":"Bob","age":30,"city":"Shanghai"}
{"id":3,"name":"Charlie","age":28,"city":"Guangzhou"}
```

### 4. 读取文本文件

```java
// 读取文本文件
JavaRDD<String> rdd = SparkUtils.readTextFile("data/logs.txt");

// 统计行数
long count = rdd.count();

// 过滤包含特定关键词的行
JavaRDD<String> filtered = rdd.filter(line -> line.contains("ERROR"));
```

---

## 数据处理示例

### 5. 执行 SQL 查询

```java
// 读取数据
Dataset<Row> df = SparkUtils.readCsv("data/users.csv", true);

// 创建临时视图
SparkUtils.createTempView(df, "users");

// 执行 SQL 查询
Dataset<Row> result = SparkUtils.executeSql(
    "SELECT name, age FROM users WHERE age > 25 ORDER BY age DESC"
);

// 显示结果
SparkUtils.show(result);
```

### 6. 数据过滤和转换

```java
Dataset<Row> df = SparkUtils.readCsv("data/users.csv", true);

// 使用 DataFrame API 过滤
Dataset<Row> filtered = df.filter("age > 25");

// 选择特定列
Dataset<Row> selected = df.select("name", "age");

// 添加新列
Dataset<Row> withNewColumn = df.withColumn("age_group", 
    functions.when(df.col("age").$less(30), "Young")
              .otherwise("Old"));
```

### 7. 数据聚合

```java
Dataset<Row> df = SparkUtils.readCsv("data/sales.csv", true);
SparkUtils.createTempView(df, "sales");

// 按城市统计销售额
Dataset<Row> result = SparkUtils.executeSql(
    "SELECT city, SUM(amount) as total_sales, COUNT(*) as order_count " +
    "FROM sales GROUP BY city ORDER BY total_sales DESC"
);

SparkUtils.show(result);
```

### 8. 数据连接 (JOIN)

```java
// 读取两个 CSV 文件
Dataset<Row> users = SparkUtils.readCsv("data/users.csv", true);
Dataset<Row> orders = SparkUtils.readCsv("data/orders.csv", true);

// 创建临时视图
SparkUtils.createTempView(users, "users");
SparkUtils.createTempView(orders, "orders");

// 执行 JOIN 查询
Dataset<Row> result = SparkUtils.executeSql(
    "SELECT u.name, u.city, o.order_id, o.amount " +
    "FROM users u JOIN orders o ON u.id = o.user_id"
);

SparkUtils.show(result);
```

---

## 数据写入示例

### 9. 写入 CSV 文件

```java
Dataset<Row> df = SparkUtils.readCsv("data/users.csv", true);

// 处理数据
Dataset<Row> result = SparkUtils.executeSql(
    "SELECT * FROM users WHERE age > 25"
);

// 写入 CSV 文件
SparkUtils.writeCsv(result, "output/filtered_users.csv");
```

### 10. 写入 JSON 文件

```java
Dataset<Row> df = SparkUtils.readCsv("data/users.csv", true);

// 写入 JSON 文件
SparkUtils.writeJson(df, "output/users.json");
```

---

## 高级示例

### 11. 复杂数据处理流程

```java
// 1. 读取多个数据源
Dataset<Row> users = SparkUtils.readCsv("data/users.csv", true);
Dataset<Row> orders = SparkUtils.readCsv("data/orders.csv", true);
Dataset<Row> products = SparkUtils.readCsv("data/products.csv", true);

// 2. 创建临时视图
SparkUtils.createTempView(users, "users");
SparkUtils.createTempView(orders, "orders");
SparkUtils.createTempView(products, "products");

// 3. 执行复杂查询
Dataset<Row> result = SparkUtils.executeSql(
    "SELECT " +
    "  u.name, " +
    "  u.city, " +
    "  p.product_name, " +
    "  SUM(o.amount) as total_amount, " +
    "  COUNT(o.order_id) as order_count " +
    "FROM users u " +
    "JOIN orders o ON u.id = o.user_id " +
    "JOIN products p ON o.product_id = p.id " +
    "WHERE o.order_date >= '2024-01-01' " +
    "GROUP BY u.name, u.city, p.product_name " +
    "ORDER BY total_amount DESC"
);

// 4. 查看结果
SparkUtils.show(result, 20);

// 5. 保存结果
SparkUtils.writeCsv(result, "output/user_product_summary.csv");

// 6. 转换为 List 用于进一步处理
List<Map<String, Object>> dataList = SparkUtils.datasetToMapList(result);

// 7. 关闭资源
SparkUtils.closeAll();
```

### 12. 使用自定义 Schema

```java
// 定义 Schema
StructType schema = SparkUtils.createSchema(
    new String[]{"id", "Integer"},
    new String[]{"name", "String"},
    new String[]{"score", "Double"},
    new String[]{"is_active", "Boolean"}
);

// 创建数据
SparkSession spark = SparkUtils.getSparkSession();
List<Row> data = new ArrayList<>();
data.add(org.apache.spark.sql.RowFactory.create(1, "Alice", 95.5, true));
data.add(org.apache.spark.sql.RowFactory.create(2, "Bob", 88.0, false));
data.add(org.apache.spark.sql.RowFactory.create(3, "Charlie", 92.5, true));

// 创建 Dataset
Dataset<Row> df = spark.createDataFrame(data, schema);

// 使用 Dataset
SparkUtils.show(df);
```

### 13. 数据清洗示例

```java
Dataset<Row> df = SparkUtils.readCsv("data/dirty_data.csv", true);

// 数据清洗
Dataset<Row> cleaned = df
    .filter("age IS NOT NULL AND age > 0")  // 过滤无效年龄
    .filter("name IS NOT NULL AND name != ''")  // 过滤空名称
    .dropDuplicates("id")  // 去重
    .na().drop();  // 删除包含 null 的行

// 保存清洗后的数据
SparkUtils.writeCsv(cleaned, "output/cleaned_data.csv");
```

### 14. 在 Spring Boot Controller 中使用

```java
package com.practise.demo.controller;

import com.practise.demo.utils.SparkUtils;
import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Row;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/spark")
public class SparkController {

    @PostMapping("/query")
    public List<Map<String, Object>> executeQuery(@RequestParam String sql) {
        Dataset<Row> result = SparkUtils.executeSql(sql);
        return SparkUtils.datasetToMapList(result);
    }

    @GetMapping("/csv/{filename}")
    public List<Map<String, Object>> readCsv(@PathVariable String filename) {
        Dataset<Row> df = SparkUtils.readCsv("data/" + filename, true);
        return SparkUtils.datasetToMapList(df);
    }
}
```

---

## 最佳实践

### 1. 资源管理
```java
try {
    Dataset<Row> df = SparkUtils.readCsv("data.csv", true);
    // 处理数据...
} finally {
    // 在应用关闭时调用
    SparkUtils.closeAll();
}
```

### 2. 性能优化
```java
// 对于需要多次使用的 Dataset，考虑缓存
Dataset<Row> df = SparkUtils.readCsv("data.csv", true);
df.cache();  // 缓存到内存

// 多次使用后
df.unpersist();  // 释放缓存
```

### 3. 错误处理
```java
try {
    Dataset<Row> df = SparkUtils.readCsv("data.csv", true);
    // 处理数据...
} catch (Exception e) {
    e.printStackTrace();
    // 错误处理...
} finally {
    SparkUtils.closeAll();
}
```

---

## 注意事项

1. **本地模式限制**: 本项目使用本地模式，适合开发和测试
2. **数据量**: 本地模式处理数据量有限，建议不超过几 GB
3. **内存**: 确保有足够的内存运行 Spark
4. **资源关闭**: 使用完毕后记得关闭资源
5. **文件路径**: 使用相对路径或绝对路径，确保文件存在

---

## 常见问题

### Q: 如何查看 Spark 执行计划？
```java
Dataset<Row> df = SparkUtils.readCsv("data.csv", true);
df.explain(true);  // 显示详细执行计划
```

### Q: 如何设置分区数？
```java
SparkSession spark = SparkUtils.getSparkSession();
spark.conf().set("spark.sql.shuffle.partitions", "4");
```

### Q: 如何处理大数据文件？
对于大文件，建议：
1. 使用过滤条件减少数据量
2. 分批处理
3. 使用分区和缓存优化

---

更多信息请参考 [README.md](README.md)
