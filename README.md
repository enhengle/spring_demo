# Spring Boot Demo 项目

### 项目简介
一个 Spring Boot 项目的新手 demo，集成了 Spark 大数据处理工具，支持本地模式的数据处理。

### 环境
```bash
环境：Windows 11 + JDK 8
开发软件：IntelliJ IDEA
技术栈：Spring Boot、Apache Spark
```

---

## 📚 Spark 功能详解

### 什么是 Apache Spark？

Apache Spark 是一个快速、通用的大数据处理引擎，专为大规模数据处理而设计。它提供了比 Hadoop MapReduce 快 100 倍的内存计算能力。

### Spark 核心功能

#### 1. **分布式数据处理**
- **RDD (Resilient Distributed Dataset)**: 弹性分布式数据集，是 Spark 的基础数据结构
- **容错性**: 自动处理节点故障，数据自动恢复
- **并行处理**: 将数据分布到多个节点并行计算

#### 2. **Spark SQL**
- **结构化数据处理**: 使用 SQL 或 DataFrame API 处理结构化数据
- **数据源支持**: 支持 CSV、JSON、Parquet、JDBC 等多种数据源
- **Catalyst 优化器**: 自动优化查询计划，提升性能

#### 3. **流式处理 (Spark Streaming)**
- **实时数据处理**: 处理实时数据流
- **微批处理**: 将流数据分成小批次处理
- **窗口操作**: 支持滑动窗口、滚动窗口等时间窗口操作

#### 4. **机器学习 (MLlib)**
- **分布式机器学习**: 支持大规模机器学习算法
- **算法库**: 分类、回归、聚类、推荐等算法
- **特征工程**: 特征提取、转换、选择

#### 5. **图计算 (GraphX)**
- **图数据处理**: 处理图结构数据
- **图算法**: PageRank、连通分量、最短路径等

### Spark 运行模式

#### 1. **本地模式 (Local Mode)**
```java
setMaster("local[*]")  // 使用所有 CPU 核心
setMaster("local[4]")  // 使用 4 个核心
setMaster("local")     // 使用 1 个核心
```
- **优点**: 开发测试方便，无需集群
- **缺点**: 性能有限，适合小规模数据

#### 2. **Standalone 模式**
- 使用 Spark 自带的集群管理器
- 适合中小规模集群

#### 3. **YARN 模式**
- 运行在 Hadoop YARN 上
- 利用 Hadoop 集群资源

#### 4. **Mesos 模式**
- 运行在 Apache Mesos 上
- 资源共享和隔离

#### 5. **Kubernetes 模式**
- 运行在 Kubernetes 上
- 容器化部署

### Spark 核心概念

#### 1. **SparkSession**
- Spark 2.0+ 的统一入口点
- 替代了 SQLContext 和 HiveContext
- 用于创建 DataFrame、执行 SQL 等

#### 2. **DataFrame**
- 以列形式组织的分布式数据集合
- 类似关系型数据库的表
- 支持 SQL 查询和 DataFrame API

#### 3. **Dataset**
- 类型安全的 DataFrame
- 结合了 RDD 和 DataFrame 的优点
- 编译时类型检查

#### 4. **RDD (Resilient Distributed Dataset)**
- 不可变的分布式数据集合
- 支持两种操作：
  - **Transformations**: 转换操作（lazy，延迟执行）
  - **Actions**: 行动操作（立即执行）

### Spark 操作类型

#### Transformations (转换操作)
- `map()`: 对每个元素应用函数
- `filter()`: 过滤数据
- `flatMap()`: 扁平化映射
- `groupBy()`: 分组
- `join()`: 连接
- `union()`: 合并
- `distinct()`: 去重

#### Actions (行动操作)
- `collect()`: 收集所有数据到驱动节点
- `count()`: 统计元素数量
- `first()`: 获取第一个元素
- `take(n)`: 获取前 n 个元素
- `saveAsTextFile()`: 保存为文本文件
- `foreach()`: 对每个元素执行操作

### Spark 性能优化

#### 1. **缓存和持久化**
```java
df.cache()        // 缓存到内存
df.persist()      // 持久化
df.unpersist()    // 释放缓存
```

#### 2. **分区优化**
- 合理设置分区数
- 避免数据倾斜
- 使用 `coalesce()` 和 `repartition()`

#### 3. **广播变量**
- 将小数据集广播到所有节点
- 减少数据传输

#### 4. **累加器**
- 分布式计数器
- 用于聚合统计

---

## 🛠️ SparkUtils 工具类使用指南

### 功能概览

`SparkUtils` 是一个本地 Spark 工具类，提供了常用的 Spark 操作封装，方便在 Spring Boot 项目中使用。

### 主要功能

#### 1. **初始化 Spark**
```java
// 获取默认 SparkSession
SparkSession spark = SparkUtils.getSparkSession();

// 获取指定名称的 SparkSession
SparkSession spark = SparkUtils.getSparkSession("MyApp");
```

#### 2. **读取数据**
```java
// 读取 CSV 文件
Dataset<Row> df = SparkUtils.readCsv("data.csv", true);

// 读取 JSON 文件
Dataset<Row> df = SparkUtils.readJson("data.json");

// 读取文本文件
JavaRDD<String> rdd = SparkUtils.readTextFile("data.txt");
```

#### 3. **数据处理**
```java
// 执行 SQL 查询
Dataset<Row> result = SparkUtils.executeSql("SELECT * FROM table WHERE age > 18");

// 创建临时视图
SparkUtils.createTempView(df, "myTable");

// 转换为 List<Map>
List<Map<String, Object>> list = SparkUtils.datasetToMapList(df);
```

#### 4. **数据写入**
```java
// 写入 CSV
SparkUtils.writeCsv(df, "output.csv");

// 写入 JSON
SparkUtils.writeJson(df, "output.json");
```

#### 5. **数据查看**
```java
// 显示前 20 行
SparkUtils.show(df);

// 显示前 n 行
SparkUtils.show(df, 10);

// 打印 Schema
SparkUtils.printSchema(df);

// 统计行数
long count = SparkUtils.count(df);
```

#### 6. **资源管理**
```java
// 关闭所有资源
SparkUtils.closeAll();
```

### 完整示例

```java
// 1. 读取 CSV 文件
Dataset<Row> df = SparkUtils.readCsv("data/users.csv", true);

// 2. 查看数据
SparkUtils.show(df, 5);
SparkUtils.printSchema(df);

// 3. 创建临时视图
SparkUtils.createTempView(df, "users");

// 4. 执行 SQL 查询
Dataset<Row> result = SparkUtils.executeSql(
    "SELECT name, age FROM users WHERE age > 25 ORDER BY age DESC"
);

// 5. 显示结果
SparkUtils.show(result);

// 6. 保存结果
SparkUtils.writeCsv(result, "output/filtered_users.csv");

// 7. 关闭资源
SparkUtils.closeAll();
```

---

## 🧪 测试用例

项目包含完整的测试用例，位于 `src/test/java/com/practise/demo/utils/SparkUtilsTest.java`

### 运行测试
```bash
mvn test
```

### 测试覆盖
- ✅ SparkSession 初始化
- ✅ CSV 文件读写
- ✅ JSON 文件读写
- ✅ SQL 查询执行
- ✅ 数据转换操作
- ✅ 资源管理

---

## 📝 application.yml 配置

```yaml
# 服务器配置
server:
  port: 1234

# Spark 配置通过代码设置，无需在此配置
# 所有 Spark 相关配置都在 SparkUtils 类中管理
```

---

## 🚀 快速开始

### 1. 安装依赖
```bash
mvn clean install
```

### 2. 运行应用
```bash
mvn spring-boot:run
```

### 3. 使用 SparkUtils
```java
import com.practise.demo.utils.SparkUtils;

// 在 Controller 或 Service 中使用
Dataset<Row> df = SparkUtils.readCsv("data.csv", true);
```

---

## 📖 更多资源

- [Apache Spark 官方文档](https://spark.apache.org/docs/latest/)
- [Spark SQL 编程指南](https://spark.apache.org/docs/latest/sql-programming-guide.html)
- [Spark RDD 编程指南](https://spark.apache.org/docs/latest/rdd-programming-guide.html)

---

## ⚠️ 注意事项

1. **本地模式限制**: 本项目使用本地模式，适合开发和测试，不适合生产环境
2. **资源管理**: 使用完毕后记得调用 `closeAll()` 关闭资源
3. **数据量**: 本地模式处理数据量有限，建议不超过几 GB
4. **内存**: 确保有足够的内存运行 Spark

---

## 📄 许可证

本项目仅供学习和参考使用。
