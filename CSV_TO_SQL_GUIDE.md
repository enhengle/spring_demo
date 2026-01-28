# CSV 转 SQL 语句功能指南

## 功能说明

将 CSV 文件或视图数据转换为 INSERT 或 REPLACE SQL 语句，支持批量生成，方便数据迁移和导入。

---

## 主要功能

### 1. CSV 文件转 INSERT 语句
将 CSV 文件转换为 INSERT INTO 语句

### 2. CSV 文件转 REPLACE 语句
将 CSV 文件转换为 REPLACE INTO 语句

### 3. 视图数据转 SQL 语句
将临时视图中的数据转换为 INSERT/REPLACE 语句

### 4. 保存到文件
将生成的 SQL 语句保存到文件

---

## API 接口

### 1. CSV 转 INSERT 语句

**POST** `/api/spark/csv/to-insert`

**请求体**:
```json
{
  "csvFilePath": "test-data/test.csv",
  "tableName": "users",
  "hasHeader": true,
  "batchSize": 100
}
```

**参数说明**:
- `csvFilePath` (required): CSV 文件路径
- `tableName` (required): 目标表名
- `hasHeader` (optional, default: true): 是否包含表头
- `batchSize` (optional): 批量生成大小，null 或 0 表示每条数据一个语句

**响应示例**:
```json
{
  "success": true,
  "message": "转换成功",
  "tableName": "users",
  "statementCount": 3,
  "statements": [
    "INSERT INTO users (id, name, age) VALUES (1, 'Alice', 25)",
    "INSERT INTO users (id, name, age) VALUES (2, 'Bob', 30)",
    "INSERT INTO users (id, name, age) VALUES (3, 'Charlie', 28)"
  ]
}
```

**批量模式响应示例** (batchSize=2):
```json
{
  "success": true,
  "message": "转换成功",
  "tableName": "users",
  "statementCount": 2,
  "statements": [
    "INSERT INTO users (id, name, age) VALUES (1, 'Alice', 25), (2, 'Bob', 30)",
    "INSERT INTO users (id, name, age) VALUES (3, 'Charlie', 28)"
  ]
}
```

### 2. CSV 转 REPLACE 语句

**POST** `/api/spark/csv/to-replace`

**请求体**:
```json
{
  "csvFilePath": "test-data/test.csv",
  "tableName": "users",
  "hasHeader": true,
  "batchSize": 100
}
```

**响应示例**:
```json
{
  "success": true,
  "message": "转换成功",
  "tableName": "users",
  "statementCount": 3,
  "statements": [
    "REPLACE INTO users (id, name, age) VALUES (1, 'Alice', 25)",
    "REPLACE INTO users (id, name, age) VALUES (2, 'Bob', 30)",
    "REPLACE INTO users (id, name, age) VALUES (3, 'Charlie', 28)"
  ]
}
```

### 3. CSV 转 SQL 并保存到文件

**POST** `/api/spark/csv/to-sql-file`

**请求体**:
```json
{
  "csvFilePath": "test-data/test.csv",
  "tableName": "users",
  "hasHeader": true,
  "outputFilePath": "output/insert_users.sql",
  "batchSize": 100,
  "sqlType": "INSERT"
}
```

**参数说明**:
- `csvFilePath` (required): CSV 文件路径
- `tableName` (required): 目标表名
- `hasHeader` (optional, default: true): 是否包含表头
- `outputFilePath` (required): 输出 SQL 文件路径
- `batchSize` (optional): 批量生成大小
- `sqlType` (optional, default: "INSERT"): SQL 类型，INSERT 或 REPLACE

**响应示例**:
```json
{
  "success": true,
  "message": "转换并保存成功",
  "tableName": "users",
  "statementCount": 3,
  "outputFile": "output/insert_users.sql"
}
```

### 4. 视图数据转 INSERT 语句

**POST** `/api/spark/view/{viewName}/to-insert`

**路径参数**:
- `viewName`: 视图名称

**请求体**:
```json
{
  "tableName": "users",
  "batchSize": 100
}
```

**响应示例**:
```json
{
  "success": true,
  "message": "转换成功",
  "tableName": "users",
  "statementCount": 3,
  "statements": [
    "INSERT INTO users (id, name, age) VALUES (1, 'Alice', 25)",
    "INSERT INTO users (id, name, age) VALUES (2, 'Bob', 30)",
    "INSERT INTO users (id, name, age) VALUES (3, 'Charlie', 28)"
  ]
}
```

### 5. 视图数据转 REPLACE 语句

**POST** `/api/spark/view/{viewName}/to-replace`

**路径参数**:
- `viewName`: 视图名称

**请求体**:
```json
{
  "tableName": "users",
  "batchSize": 100
}
```

---

## 使用示例

### 示例 1: CSV 转 INSERT（单条模式）

```bash
curl -X POST http://localhost:1234/api/spark/csv/to-insert \
  -H "Content-Type: application/json" \
  -d '{
    "csvFilePath": "test-data/test.csv",
    "tableName": "users",
    "hasHeader": true,
    "batchSize": null
  }'
```

**生成的 SQL**:
```sql
INSERT INTO users (id, name, age) VALUES (1, 'Alice', 25);
INSERT INTO users (id, name, age) VALUES (2, 'Bob', 30);
INSERT INTO users (id, name, age) VALUES (3, 'Charlie', 28);
```

### 示例 2: CSV 转 INSERT（批量模式）

```bash
curl -X POST http://localhost:1234/api/spark/csv/to-insert \
  -H "Content-Type: application/json" \
  -d '{
    "csvFilePath": "test-data/test.csv",
    "tableName": "users",
    "hasHeader": true,
    "batchSize": 100
  }'
```

**生成的 SQL** (假设有 250 条数据):
```sql
INSERT INTO users (id, name, age) VALUES (1, 'Alice', 25), (2, 'Bob', 30), ..., (100, 'User100', 30);
INSERT INTO users (id, name, age) VALUES (101, 'User101', 25), ..., (200, 'User200', 28);
INSERT INTO users (id, name, age) VALUES (201, 'User201', 30), ..., (250, 'User250', 25);
```

### 示例 3: CSV 转 REPLACE

```bash
curl -X POST http://localhost:1234/api/spark/csv/to-replace \
  -H "Content-Type: application/json" \
  -d '{
    "csvFilePath": "test-data/test.csv",
    "tableName": "users",
    "hasHeader": true,
    "batchSize": 50
  }'
```

**生成的 SQL**:
```sql
REPLACE INTO users (id, name, age) VALUES (1, 'Alice', 25), (2, 'Bob', 30), ...;
REPLACE INTO users (id, name, age) VALUES (51, 'User51', 28), ...;
```

### 示例 4: CSV 转 SQL 并保存到文件

```bash
curl -X POST http://localhost:1234/api/spark/csv/to-sql-file \
  -H "Content-Type: application/json" \
  -d '{
    "csvFilePath": "test-data/test.csv",
    "tableName": "users",
    "hasHeader": true,
    "outputFilePath": "output/insert_users.sql",
    "batchSize": 100,
    "sqlType": "INSERT"
  }'
```

### 示例 5: 视图数据转 INSERT

```bash
# 1. 先创建视图
curl -X POST http://localhost:1234/api/spark/view/create-by-sql \
  -H "Content-Type: application/json" \
  -d '{
    "sql": "CREATE TEMPORARY VIEW users AS SELECT * FROM csv.`test-data/test.csv` OPTIONS (header \"true\", inferSchema \"true\")"
  }'

# 2. 将视图数据转换为 INSERT 语句
curl -X POST http://localhost:1234/api/spark/view/users/to-insert \
  -H "Content-Type: application/json" \
  -d '{
    "tableName": "users",
    "batchSize": 100
  }'
```

---

## 代码使用示例

### Java 代码示例

```java
import com.practise.demo.utils.SparkUtils;
import java.util.List;

// 1. CSV 转 INSERT（单条模式）
List<String> insertStatements = SparkUtils.csvToInsertStatements(
    "data/users.csv", "users", true, null);

// 2. CSV 转 INSERT（批量模式，每批100条）
List<String> batchInsertStatements = SparkUtils.csvToInsertStatements(
    "data/users.csv", "users", true, 100);

// 3. CSV 转 REPLACE
List<String> replaceStatements = SparkUtils.csvToReplaceStatements(
    "data/users.csv", "users", true, 100);

// 4. CSV 转 SQL 并保存到文件
int count = SparkUtils.csvToSqlFile(
    "data/users.csv", "users", true, 
    "output/insert_users.sql", 100, "INSERT");

// 5. 视图数据转 INSERT
Dataset<Row> df = SparkUtils.getSparkSession().table("users");
List<String> statements = SparkUtils.datasetToInsertStatements(df, "users", 100);
```

---

## 特性说明

### 1. 数据类型处理
- **字符串**: 自动添加单引号并转义特殊字符
- **数字**: 直接使用，不加引号
- **NULL**: 转换为 SQL 的 NULL
- **日期时间**: 转换为字符串格式

### 2. SQL 转义
- 单引号 (`'`) 自动转义为 (`''`)
- 反斜杠 (`\`) 自动转义为 (`\\`)
- 确保生成的 SQL 语句安全

### 3. 批量模式
- **单条模式** (batchSize=null 或 0): 每条数据生成一个 SQL 语句
- **批量模式** (batchSize>0): 多条数据合并到一个 SQL 语句中，提高执行效率

### 4. 批量模式优势
- 减少 SQL 语句数量
- 提高数据库执行效率
- 减少网络传输开销

---

## 注意事项

### 1. 文件路径
- 使用相对路径时，相对于应用运行目录
- 确保 CSV 文件存在且可读

### 2. 表名和列名
- 表名和列名会直接使用，建议使用合法的 SQL 标识符
- 如果列名包含特殊字符，可能需要手动调整

### 3. 数据量
- 大数据量建议使用批量模式
- 单条模式适合小数据量或需要逐条控制的情况

### 4. 内存使用
- 批量模式会一次性加载所有数据到内存
- 超大文件建议分批处理

### 5. SQL 兼容性
- 生成的 SQL 语句符合标准 SQL 语法
- 某些数据库可能需要调整（如 MySQL 的 REPLACE INTO）

---

## 完整流程示例

### 流程: CSV → 视图 → INSERT 语句

```bash
# 1. 从 CSV 创建视图
curl -X POST http://localhost:1234/api/spark/view/create-by-sql \
  -H "Content-Type: application/json" \
  -d '{
    "sql": "CREATE TEMPORARY VIEW users AS SELECT * FROM csv.`test-data/test.csv` OPTIONS (header \"true\", inferSchema \"true\")"
  }'

# 2. 预览数据
curl http://localhost:1234/api/spark/view/users/preview?limit=5

# 3. 将视图数据转换为 INSERT 语句
curl -X POST http://localhost:1234/api/spark/view/users/to-insert \
  -H "Content-Type: application/json" \
  -d '{
    "tableName": "users",
    "batchSize": 100
  }'

# 4. 或者直接转换 CSV 文件
curl -X POST http://localhost:1234/api/spark/csv/to-insert \
  -H "Content-Type: application/json" \
  -d '{
    "csvFilePath": "test-data/test.csv",
    "tableName": "users",
    "hasHeader": true,
    "batchSize": 100
  }'
```

---

## 生成的 SQL 示例

### INSERT 语句示例

```sql
-- 单条模式
INSERT INTO users (id, name, age, city) VALUES (1, 'Alice', 25, 'Beijing');
INSERT INTO users (id, name, age, city) VALUES (2, 'Bob', 30, 'Shanghai');

-- 批量模式（batchSize=2）
INSERT INTO users (id, name, age, city) VALUES (1, 'Alice', 25, 'Beijing'), (2, 'Bob', 30, 'Shanghai');
INSERT INTO users (id, name, age, city) VALUES (3, 'Charlie', 28, 'Guangzhou');
```

### REPLACE 语句示例

```sql
-- 单条模式
REPLACE INTO users (id, name, age, city) VALUES (1, 'Alice', 25, 'Beijing');
REPLACE INTO users (id, name, age, city) VALUES (2, 'Bob', 30, 'Shanghai');

-- 批量模式
REPLACE INTO users (id, name, age, city) VALUES (1, 'Alice', 25, 'Beijing'), (2, 'Bob', 30, 'Shanghai');
```

---

## 错误处理

### 常见错误

1. **文件不存在**
```json
{
  "success": false,
  "message": "转换失败: 文件不存在: data/users.csv"
}
```

2. **表名为空**
```json
{
  "success": false,
  "message": "转换失败: 表名不能为空"
}
```

3. **视图不存在**（视图转 SQL 时）
```json
{
  "success": false,
  "message": "视图不存在: users"
}
```

---

## 性能建议

1. **小数据量** (< 1000 行): 使用单条模式 (batchSize=null)
2. **中等数据量** (1000-10000 行): 使用批量模式 (batchSize=100-500)
3. **大数据量** (> 10000 行): 使用批量模式 (batchSize=500-1000)，或分批处理

---

更多信息请参考：
- [SPARK_API_DOCUMENTATION.md](SPARK_API_DOCUMENTATION.md)
- [README.md](README.md)
