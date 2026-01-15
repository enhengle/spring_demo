# Spark API 接口文档

本文档描述了 Spark Controller 提供的所有 REST API 接口。

## 基础信息

- **Base URL**: `http://localhost:1234/api/spark`
- **Content-Type**: `application/json` (POST/PUT/DELETE)
- **Response Format**: JSON

---

## API 接口列表

### 1. 健康检查

**GET** `/api/spark/health`

检查服务是否正常运行。

**响应示例**:
```json
{
  "status": "UP",
  "message": "Spark Service is running",
  "timestamp": 1234567890
}
```

---

### 2. 数据读取

#### 2.1 读取 CSV 文件

**GET** `/api/spark/read/csv`

读取 CSV 文件并返回基本信息。

**请求参数**:
- `filePath` (required): 文件路径
- `hasHeader` (optional, default: true): 是否包含表头

**示例请求**:
```
GET /api/spark/read/csv?filePath=data/users.csv&hasHeader=true
```

**响应示例**:
```json
{
  "success": true,
  "message": "CSV 文件读取成功",
  "rowCount": 100,
  "columns": ["id", "name", "age"],
  "columnCount": 3
}
```

#### 2.2 读取 JSON 文件

**GET** `/api/spark/read/json`

读取 JSON 文件并返回基本信息。

**请求参数**:
- `filePath` (required): 文件路径

**示例请求**:
```
GET /api/spark/read/json?filePath=data/users.json
```

**响应示例**:
```json
{
  "success": true,
  "message": "JSON 文件读取成功",
  "rowCount": 100,
  "columns": ["id", "name", "age"],
  "columnCount": 3
}
```

---

### 3. 临时视图管理

#### 3.1 创建临时视图（从文件）

**POST** `/api/spark/view/create`

从文件创建临时视图。

**请求体**:
```json
{
  "filePath": "data/users.csv",
  "viewName": "users",
  "fileType": "csv",
  "hasHeader": true
}
```

**参数说明**:
- `filePath` (required): 文件路径
- `viewName` (required): 视图名称
- `fileType` (optional, default: "csv"): 文件类型 (csv/json)
- `hasHeader` (optional, default: true): CSV 文件是否包含表头

**响应示例**:
```json
{
  "success": true,
  "message": "临时视图创建成功",
  "viewName": "users",
  "rowCount": 100,
  "columns": ["id", "name", "age"]
}
```

#### 3.2 通过 SQL 语句创建临时视图（推荐）

**POST** `/api/spark/view/create-by-sql`

通过 SQL 语句创建临时视图，支持 CREATE TEMPORARY VIEW 语法。
**可以直接从 CSV/JSON 文件读取并创建视图，无需先创建基础视图。**

**请求体**:
```json
{
  "sql": "CREATE TEMPORARY VIEW users AS SELECT * FROM csv.`data/users.csv` OPTIONS (header 'true', inferSchema 'true')"
}
```

**SQL 语法示例**:

**1. 从 CSV 文件直接创建视图**:
```sql
-- 基本语法（带表头）
CREATE TEMPORARY VIEW users AS 
SELECT * FROM csv.`data/users.csv` 
OPTIONS (header 'true', inferSchema 'true')

-- 不带表头
CREATE TEMPORARY VIEW users AS 
SELECT * FROM csv.`data/users.csv` 
OPTIONS (header 'false', inferSchema 'true')

-- 指定分隔符
CREATE TEMPORARY VIEW users AS 
SELECT * FROM csv.`data/users.csv` 
OPTIONS (header 'true', inferSchema 'true', delimiter ',')

-- 选择特定列
CREATE TEMPORARY VIEW users_filtered AS 
SELECT id, name, age FROM csv.`data/users.csv` 
OPTIONS (header 'true', inferSchema 'true')
```

**2. 从 JSON 文件直接创建视图**:
```sql
-- 从 JSON 文件创建视图
CREATE TEMPORARY VIEW users AS 
SELECT * FROM json.`data/users.json`

-- 选择特定字段
CREATE TEMPORARY VIEW users_simple AS 
SELECT id, name, age FROM json.`data/users.json`
```

**3. 从现有视图创建新视图**:
```sql
-- 过滤数据
CREATE TEMPORARY VIEW young_users AS 
SELECT name, age FROM users WHERE age < 30

-- 聚合查询
CREATE TEMPORARY VIEW user_stats AS 
SELECT city, COUNT(*) as count, AVG(age) as avg_age 
FROM users 
GROUP BY city

-- JOIN 查询
CREATE TEMPORARY VIEW user_orders AS 
SELECT u.name, o.order_id, o.amount 
FROM users u 
JOIN orders o ON u.id = o.user_id
```

**4. 支持 CREATE OR REPLACE**:
```sql
CREATE OR REPLACE TEMPORARY VIEW users AS 
SELECT * FROM csv.`data/users.csv` 
OPTIONS (header 'true', inferSchema 'true')
```

**5. 复杂示例：从 CSV 读取并处理**:
```sql
CREATE TEMPORARY VIEW processed_users AS 
SELECT 
  id,
  UPPER(name) as name_upper,
  age,
  CASE 
    WHEN age < 30 THEN 'Young'
    WHEN age < 50 THEN 'Middle'
    ELSE 'Old'
  END as age_group
FROM csv.`data/users.csv` 
OPTIONS (header 'true', inferSchema 'true')
WHERE age > 18
```

**参数说明**:
- `sql` (required): CREATE TEMPORARY VIEW 语句

**响应示例**:
```json
{
  "success": true,
  "message": "通过 SQL 创建临时视图成功",
  "viewName": "filtered_users",
  "rowCount": 50,
  "columns": ["name", "age"],
  "columnCount": 2
}
```

**错误响应示例**:
```json
{
  "success": false,
  "message": "SQL 语法错误: ..."
}
```

#### 3.3 删除临时视图

**DELETE** `/api/spark/view/{viewName}`

删除指定的临时视图。

**路径参数**:
- `viewName`: 视图名称

**示例请求**:
```
DELETE /api/spark/view/users
```

**响应示例**:
```json
{
  "success": true,
  "message": "临时视图删除成功",
  "viewName": "users"
}
```

#### 3.4 检查临时视图是否存在

**GET** `/api/spark/view/{viewName}/exists`

检查临时视图是否存在。

**路径参数**:
- `viewName`: 视图名称

**示例请求**:
```
GET /api/spark/view/users/exists
```

**响应示例**:
```json
{
  "success": true,
  "exists": true,
  "viewName": "users",
  "message": "视图存在"
}
```

#### 3.5 列出所有临时视图

**GET** `/api/spark/view/list`

获取所有临时视图的列表。

**示例请求**:
```
GET /api/spark/view/list
```

**响应示例**:
```json
{
  "success": true,
  "views": ["users", "orders", "products"],
  "count": 3,
  "message": "获取临时视图列表成功"
}
```

#### 3.6 获取视图 Schema

**GET** `/api/spark/view/{viewName}/schema`

获取视图的 Schema 信息（字段名和类型）。

**路径参数**:
- `viewName`: 视图名称

**示例请求**:
```
GET /api/spark/view/users/schema
```

**响应示例**:
```json
{
  "success": true,
  "message": "获取 Schema 成功",
  "viewName": "users",
  "schema": [
    {
      "name": "id",
      "type": "IntegerType"
    },
    {
      "name": "name",
      "type": "StringType"
    },
    {
      "name": "age",
      "type": "IntegerType"
    }
  ],
  "columnCount": 3
}
```

#### 3.7 预览视图数据

**GET** `/api/spark/view/{viewName}/preview`

预览视图的数据（前 N 行）。

**路径参数**:
- `viewName`: 视图名称

**查询参数**:
- `limit` (optional, default: 20): 预览行数

**示例请求**:
```
GET /api/spark/view/users/preview?limit=10
```

**响应示例**:
```json
{
  "success": true,
  "message": "获取预览数据成功",
  "viewName": "users",
  "totalCount": 100,
  "previewCount": 10,
  "columns": ["id", "name", "age"],
  "data": [
    {
      "id": 1,
      "name": "Alice",
      "age": 25
    },
    {
      "id": 2,
      "name": "Bob",
      "age": 30
    }
  ]
}
```

---

### 4. SQL 操作

#### 4.1 校验 SQL 语法

**POST** `/api/spark/sql/validate`

校验 SQL 语句的语法是否正确。

**请求体**:
```json
{
  "sql": "SELECT * FROM users WHERE age > 25"
}
```

**响应示例**:
```json
{
  "success": true,
  "valid": true,
  "message": "SQL 语法正确"
}
```

**错误响应示例**:
```json
{
  "success": true,
  "valid": false,
  "message": "SQL 语法错误: ..."
}
```

#### 4.2 解析 SELECT SQL

**POST** `/api/spark/sql/parse`

解析 SELECT SQL 语句，提取表名、字段等信息。

**请求体**:
```json
{
  "sql": "SELECT name, age FROM users WHERE age > 25"
}
```

**响应示例**:
```json
{
  "success": true,
  "message": "解析成功",
  "tableNames": ["users"],
  "columns": ["name", "age"],
  "columnTypes": {
    "name": "StringType",
    "age": "IntegerType"
  }
}
```

#### 4.3 执行 SQL 查询 (POST)

**POST** `/api/spark/sql/execute`

执行 SQL 查询并返回结果。

**请求体**:
```json
{
  "sql": "SELECT * FROM users WHERE age > 25",
  "limit": 100
}
```

**参数说明**:
- `sql` (required): SQL 查询语句
- `limit` (optional): 限制返回行数，不设置则返回所有结果

**响应示例**:
```json
{
  "success": true,
  "message": "SQL 执行成功",
  "totalCount": 50,
  "returnCount": 50,
  "columns": ["id", "name", "age"],
  "data": [
    {
      "id": 1,
      "name": "Alice",
      "age": 26
    }
  ]
}
```

#### 4.4 执行 SQL 查询 (GET)

**GET** `/api/spark/sql/execute`

快速执行 SQL 查询（GET 方式，用于简单查询）。

**查询参数**:
- `sql` (required): SQL 查询语句
- `limit` (optional): 限制返回行数

**示例请求**:
```
GET /api/spark/sql/execute?sql=SELECT * FROM users&limit=10
```

**响应格式**: 同 POST 方式

---

## 完整使用流程示例

### 示例 1: 从 CSV 创建视图并查询（推荐方式）

```bash
# 1. 通过 SQL 直接从 CSV 文件创建视图
curl -X POST http://localhost:1234/api/spark/view/create-by-sql \
  -H "Content-Type: application/json" \
  -d '{
    "sql": "CREATE TEMPORARY VIEW users AS SELECT * FROM csv.`data/users.csv` OPTIONS (header \"true\", inferSchema \"true\")"
  }'

# 2. 预览数据
curl http://localhost:1234/api/spark/view/users/preview?limit=5

# 3. 执行查询
curl -X POST http://localhost:1234/api/spark/sql/execute \
  -H "Content-Type: application/json" \
  -d '{
    "sql": "SELECT name, age FROM users WHERE age > 25 ORDER BY age DESC",
    "limit": 10
  }'

# 4. 删除视图
curl -X DELETE http://localhost:1234/api/spark/view/users
```

### 示例 1-1: 传统方式（从文件创建，已废弃）

```bash
# 1. 创建临时视图（传统方式，不推荐）
curl -X POST http://localhost:1234/api/spark/view/create \
  -H "Content-Type: application/json" \
  -d '{
    "filePath": "data/users.csv",
    "viewName": "users",
    "fileType": "csv",
    "hasHeader": true
  }'
```

### 示例 2: SQL 校验和解析

```bash
# 1. 校验 SQL
curl -X POST http://localhost:1234/api/spark/sql/validate \
  -H "Content-Type: application/json" \
  -d '{
    "sql": "SELECT * FROM users WHERE age > 25"
  }'

# 2. 解析 SQL
curl -X POST http://localhost:1234/api/spark/sql/parse \
  -H "Content-Type: application/json" \
  -d '{
    "sql": "SELECT name, age FROM users WHERE age > 25"
  }'
```

### 示例 3: 多表 JOIN 查询

```bash
# 1. 创建多个视图
curl -X POST http://localhost:1234/api/spark/view/create \
  -H "Content-Type: application/json" \
  -d '{
    "filePath": "data/users.csv",
    "viewName": "users",
    "fileType": "csv",
    "hasHeader": true
  }'

curl -X POST http://localhost:1234/api/spark/view/create \
  -H "Content-Type: application/json" \
  -d '{
    "filePath": "data/orders.csv",
    "viewName": "orders",
    "fileType": "csv",
    "hasHeader": true
  }'

# 2. 执行 JOIN 查询
curl -X POST http://localhost:1234/api/spark/sql/execute \
  -H "Content-Type: application/json" \
  -d '{
    "sql": "SELECT u.name, o.order_id, o.amount FROM users u JOIN orders o ON u.id = o.user_id",
    "limit": 20
  }'

# 3. 列出所有视图
curl http://localhost:1234/api/spark/view/list
```

### 示例 4: 通过 SQL 创建临时视图（完整流程）

```bash
# 方式一：直接从 CSV 文件创建视图（推荐）
curl -X POST http://localhost:1234/api/spark/view/create-by-sql \
  -H "Content-Type: application/json" \
  -d '{
    "sql": "CREATE TEMPORARY VIEW users AS SELECT * FROM csv.`data/users.csv` OPTIONS (header \"true\", inferSchema \"true\")"
  }'

# 方式二：从 JSON 文件创建视图
curl -X POST http://localhost:1234/api/spark/view/create-by-sql \
  -H "Content-Type: application/json" \
  -d '{
    "sql": "CREATE TEMPORARY VIEW users AS SELECT * FROM json.`data/users.json`"
  }'

# 2. 通过 SQL 从现有视图创建过滤后的视图
curl -X POST http://localhost:1234/api/spark/view/create-by-sql \
  -H "Content-Type: application/json" \
  -d '{
    "sql": "CREATE TEMPORARY VIEW young_users AS SELECT name, age FROM users WHERE age < 30"
  }'

# 3. 通过 SQL 创建聚合视图
curl -X POST http://localhost:1234/api/spark/view/create-by-sql \
  -H "Content-Type: application/json" \
  -d '{
    "sql": "CREATE TEMPORARY VIEW user_stats AS SELECT city, COUNT(*) as count, AVG(age) as avg_age FROM users GROUP BY city"
  }'

# 4. 直接从 CSV 创建处理后的视图（一步到位）
curl -X POST http://localhost:1234/api/spark/view/create-by-sql \
  -H "Content-Type: application/json" \
  -d '{
    "sql": "CREATE TEMPORARY VIEW processed_users AS SELECT id, UPPER(name) as name_upper, age FROM csv.`data/users.csv` OPTIONS (header \"true\", inferSchema \"true\") WHERE age > 18"
  }'

# 5. 查询新创建的视图
curl -X POST http://localhost:1234/api/spark/sql/execute \
  -H "Content-Type: application/json" \
  -d '{
    "sql": "SELECT * FROM young_users ORDER BY age",
    "limit": 10
  }'

# 6. 查看所有视图
curl http://localhost:1234/api/spark/view/list
```

---

## 错误处理

所有接口在出错时都会返回包含 `success: false` 的响应：

```json
{
  "success": false,
  "message": "错误描述信息"
}
```

常见错误：
- 文件不存在
- 视图不存在
- SQL 语法错误
- SQL 执行失败

---

## 注意事项

1. **临时视图生命周期**: 临时视图只在当前 SparkSession 中有效，应用重启后会丢失
2. **文件路径**: 使用相对路径时，相对于应用运行目录
3. **SQL 限制**: 主要支持 SELECT 查询，不支持 DDL/DML 操作
4. **性能考虑**: 大数据量查询建议使用 `limit` 参数限制返回行数
5. **资源管理**: 使用完毕后建议删除临时视图以释放内存

---

## 测试建议

1. 使用 Postman 或 curl 测试各个接口
2. 先创建测试数据文件（CSV/JSON）
3. 按照流程：创建视图 → 查询 → 删除视图
4. 测试错误场景（不存在的文件、视图等）

---

更多信息请参考：
- [README.md](README.md)
- [SPARK_USAGE_EXAMPLES.md](SPARK_USAGE_EXAMPLES.md)
- [SPARK_VIEW_SQL_USAGE.md](SPARK_VIEW_SQL_USAGE.md)
