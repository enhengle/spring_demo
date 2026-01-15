# Spark 额外实用功能说明

## 已实现的核心功能

### ✅ 1. 通过 SQL 创建临时视图
**方法**: `createTempViewBySql(String sql)`
**接口**: `POST /api/spark/view/create-by-sql`

```java
// 示例
String sql = "CREATE TEMPORARY VIEW users AS SELECT * FROM csv.`data/users.csv` OPTIONS (header 'true', inferSchema 'true')";
SparkUtils.createTempViewBySql(sql);
```

### ✅ 2. SQL 语法校验
**方法**: `validateSql(String sql)`
**接口**: `POST /api/spark/sql/validate`

```java
// 示例
SqlValidationResult result = SparkUtils.validateSql("SELECT * FROM users");
if (result.isValid()) {
    // SQL 语法正确
}
```

---

## 🚀 新增的实用功能

### 1. 数据统计分析

#### 1.1 基本统计信息（describe）
```java
Dataset<Row> stats = SparkUtils.describe(dataset);
// 返回：count, mean, stddev, min, max
```

#### 1.2 详细统计信息（summary）
```java
Dataset<Row> summary = SparkUtils.summary(dataset);
// 返回：count, mean, stddev, min, 25%, 50%, 75%, max
```

### 2. 数据去重

#### 2.1 完全去重
```java
Dataset<Row> distinct = SparkUtils.distinct(dataset);
```

#### 2.2 按列去重
```java
Dataset<Row> distinct = SparkUtils.dropDuplicates(dataset, "id", "name");
```

### 3. 数据采样

#### 3.1 随机采样
```java
// 采样 10% 的数据
Dataset<Row> sample = SparkUtils.sample(dataset, 0.1, null);

// 带随机种子的采样（可重复）
Dataset<Row> sample = SparkUtils.sample(dataset, 0.1, 12345L);
```

#### 3.2 获取前 N 行
```java
Dataset<Row> topN = SparkUtils.takeSample(dataset, 100);
```

### 4. 数据合并

#### 4.1 UNION 合并
```java
Dataset<Row> merged = SparkUtils.union(dataset1, dataset2);
```

### 5. 数据分区管理

#### 5.1 获取分区数
```java
int partitions = SparkUtils.getPartitionCount(dataset);
```

#### 5.2 重新分区
```java
Dataset<Row> repartitioned = SparkUtils.repartition(dataset, 4);
```

#### 5.3 合并分区（减少分区数）
```java
Dataset<Row> coalesced = SparkUtils.coalesce(dataset, 2);
```

### 6. 缓存管理

#### 6.1 缓存数据
```java
Dataset<Row> cached = SparkUtils.cache(dataset);
```

#### 6.2 释放缓存
```java
SparkUtils.unpersist(dataset);
```

#### 6.3 检查是否已缓存
```java
boolean isCached = SparkUtils.isCached(dataset);
```

### 7. 空值处理

#### 7.1 删除包含 null 的行
```java
Dataset<Row> cleaned = SparkUtils.dropNulls(dataset);
```

#### 7.2 删除指定列包含 null 的行
```java
Dataset<Row> cleaned = SparkUtils.dropNulls(dataset, "name", "age");
```

#### 7.3 填充空值
```java
// 填充所有 null 为 "Unknown"
Dataset<Row> filled = SparkUtils.fillNulls(dataset, "Unknown");

// 按列填充
Map<String, Object> valueMap = new HashMap<>();
valueMap.put("name", "Unknown");
valueMap.put("age", 0);
Dataset<Row> filled = SparkUtils.fillNulls(dataset, valueMap);
```

### 8. 执行计划分析

#### 8.1 获取执行计划
```java
String plan = SparkUtils.explain(dataset);
```

#### 8.2 获取详细执行计划
```java
String detailedPlan = SparkUtils.explain(dataset, true);
```

#### 8.3 打印执行计划
```java
SparkUtils.explainPlan(dataset);
SparkUtils.explainPlan(dataset, true); // 详细计划
```

#### 8.4 SQL 执行计划
```java
String plan = SparkUtils.explainSql("SELECT * FROM users WHERE age > 25");
```

### 9. 数据排序

#### 9.1 单列排序
```java
// 升序
Dataset<Row> sorted = SparkUtils.sort(dataset, "age", true);

// 降序
Dataset<Row> sorted = SparkUtils.sort(dataset, "age", false);
```

#### 9.2 多列排序
```java
Dataset<Row> sorted = SparkUtils.sort(dataset, "age", "name");
```

### 10. 数据类型操作

#### 10.1 获取列类型
```java
String type = SparkUtils.getColumnType(dataset, "age");
// 返回: IntegerType
```

#### 10.2 获取所有列类型
```java
Map<String, String> types = SparkUtils.getColumnTypes(dataset);
// 返回: {id=IntegerType, name=StringType, age=IntegerType}
```

### 11. 数据转换

#### 11.1 重命名列
```java
Dataset<Row> renamed = SparkUtils.renameColumn(dataset, "oldName", "newName");
```

#### 11.2 添加新列（常量值）
```java
Dataset<Row> withNewCol = SparkUtils.addColumn(dataset, "status", "active");
```

#### 11.3 删除列
```java
Dataset<Row> dropped = SparkUtils.dropColumns(dataset, "col1", "col2");
```

#### 11.4 选择列
```java
Dataset<Row> selected = SparkUtils.selectColumns(dataset, "name", "age");
```

### 12. 数据分组和聚合

#### 12.1 分组
```java
RelationalGroupedDataset grouped = SparkUtils.groupBy(dataset, "city", "age");
// 需要配合聚合函数使用，例如：
// grouped.agg(functions.count("*").alias("count"))
```

### 13. 数据连接（JOIN）

#### 13.1 内连接
```java
Dataset<Row> joined = SparkUtils.innerJoin(left, right, 
    functions.col("left.id").equalTo(functions.col("right.id")));
```

#### 13.2 其他 JOIN 类型
```java
// joinType: inner, left, right, outer, left_outer, right_outer, left_semi, left_anti
Dataset<Row> joined = SparkUtils.join(left, right, 
    functions.col("left.id").equalTo(functions.col("right.id")), 
    "left");
```

### 14. Parquet 格式支持

#### 14.1 读取 Parquet
```java
Dataset<Row> df = SparkUtils.readParquet("data/users.parquet");
```

#### 14.2 写入 Parquet
```java
SparkUtils.writeParquet(dataset, "output/users.parquet");
```

---

## 📊 功能分类总结

### 数据分析类
- ✅ 数据统计（describe, summary）
- ✅ 数据采样（sample, takeSample）
- ✅ 数据排序（sort）

### 数据清洗类
- ✅ 数据去重（distinct, dropDuplicates）
- ✅ 空值处理（dropNulls, fillNulls）

### 数据转换类
- ✅ 列操作（重命名、添加、删除、选择）
- ✅ 数据类型操作（获取类型、类型转换）

### 数据合并类
- ✅ UNION 合并
- ✅ JOIN 连接

### 性能优化类
- ✅ 分区管理（repartition, coalesce）
- ✅ 缓存管理（cache, unpersist）

### 性能分析类
- ✅ 执行计划分析（explain, explainPlan）

### 数据格式类
- ✅ Parquet 格式支持（readParquet, writeParquet）

---

## 💡 使用建议

### 1. 大数据处理流程
```java
// 1. 读取数据
Dataset<Row> df = SparkUtils.readCsv("data.csv", true);

// 2. 数据清洗
df = SparkUtils.dropNulls(df, "id", "name");
df = SparkUtils.dropDuplicates(df, "id");

// 3. 数据转换
df = SparkUtils.renameColumn(df, "old_name", "new_name");

// 4. 缓存（如果多次使用）
df = SparkUtils.cache(df);

// 5. 统计分析
Dataset<Row> stats = SparkUtils.describe(df);

// 6. 执行查询
Dataset<Row> result = SparkUtils.executeSql("SELECT * FROM ...");

// 7. 释放缓存
SparkUtils.unpersist(df);
```

### 2. 性能优化建议
- 对于需要多次使用的 Dataset，使用 `cache()` 缓存
- 合理设置分区数，避免过多或过少
- 使用 `coalesce()` 而不是 `repartition()` 来减少分区数
- 使用 `explain()` 查看执行计划，优化查询

### 3. 数据质量检查
```java
// 检查数据质量
long totalCount = SparkUtils.count(dataset);
long distinctCount = SparkUtils.count(SparkUtils.distinct(dataset));
long nullCount = totalCount - SparkUtils.count(SparkUtils.dropNulls(dataset));

System.out.println("总行数: " + totalCount);
System.out.println("去重后行数: " + distinctCount);
System.out.println("包含 null 的行数: " + (totalCount - nullCount));
```

---

## 🔧 后续可扩展功能

### 建议添加的功能：

1. **数据导出**
   - 导出到 Excel
   - 导出到数据库（JDBC）
   - 导出到其他格式

2. **数据验证**
   - 数据格式验证
   - 数据范围验证
   - 数据完整性检查

3. **数据转换**
   - 数据类型转换
   - 数据格式转换（日期、时间等）
   - 数据编码转换

4. **数据聚合**
   - 预定义的聚合函数封装
   - 窗口函数支持

5. **数据监控**
   - 数据质量报告
   - 处理性能监控
   - 资源使用监控

---

更多详细信息请参考：
- [README.md](README.md)
- [SPARK_USAGE_EXAMPLES.md](SPARK_USAGE_EXAMPLES.md)
- [SPARK_VIEW_SQL_USAGE.md](SPARK_VIEW_SQL_USAGE.md)
