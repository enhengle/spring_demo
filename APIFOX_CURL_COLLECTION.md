# Apifox 接口测试 - cURL 命令集合

本文档包含所有 Spark API 接口的 cURL 命令，可以直接复制到 Apifox 中导入使用。

## 导入方式

1. 打开 Apifox
2. 选择"导入" -> "cURL"
3. 复制下面的 cURL 命令，逐个导入
4. 或者使用 Apifox 的批量导入功能

---

## 1. 健康检查

```bash
curl -X GET 'http://localhost:1234/api/spark/health' \
  -H 'Accept: application/json'
```

---

## 2. 数据读取

### 2.1 读取 CSV 文件

```bash
curl -X GET 'http://localhost:1234/api/spark/read/csv?filePath=test-data/test.csv&hasHeader=true' \
  -H 'Accept: application/json'
```

### 2.2 读取 JSON 文件

```bash
curl -X GET 'http://localhost:1234/api/spark/read/json?filePath=test-data/test.json' \
  -H 'Accept: application/json'
```

---

## 3. 临时视图管理

### 3.1 通过 SQL 创建临时视图（从 CSV，推荐）

```bash
curl -X POST 'http://localhost:1234/api/spark/view/create-by-sql' \
  -H 'Content-Type: application/json' \
  -H 'Accept: application/json' \
  -d '{
    "sql": "CREATE TEMPORARY VIEW users AS SELECT * FROM csv.`test-data/test.csv` OPTIONS (header \"true\", inferSchema \"true\")"
  }'
```

### 3.2 通过 SQL 创建临时视图（从 JSON）

```bash
curl -X POST 'http://localhost:1234/api/spark/view/create-by-sql' \
  -H 'Content-Type: application/json' \
  -H 'Accept: application/json' \
  -d '{
    "sql": "CREATE TEMPORARY VIEW users AS SELECT * FROM json.`test-data/test.json`"
  }'
```

### 3.3 通过 SQL 创建临时视图（从现有视图）

```bash
curl -X POST 'http://localhost:1234/api/spark/view/create-by-sql' \
  -H 'Content-Type: application/json' \
  -H 'Accept: application/json' \
  -d '{
    "sql": "CREATE TEMPORARY VIEW young_users AS SELECT name, age FROM users WHERE age < 30"
  }'
```

### 3.4 通过 SQL 创建临时视图（聚合查询）

```bash
curl -X POST 'http://localhost:1234/api/spark/view/create-by-sql' \
  -H 'Content-Type: application/json' \
  -H 'Accept: application/json' \
  -d '{
    "sql": "CREATE TEMPORARY VIEW user_stats AS SELECT COUNT(*) as count, AVG(age) as avg_age FROM users"
  }'
```

### 3.5 创建临时视图（从文件，已废弃）

```bash
curl -X POST 'http://localhost:1234/api/spark/view/create' \
  -H 'Content-Type: application/json' \
  -H 'Accept: application/json' \
  -d '{
    "filePath": "test-data/test.csv",
    "viewName": "users",
    "fileType": "csv",
    "hasHeader": true
  }'
```

### 3.6 删除临时视图

```bash
curl -X DELETE 'http://localhost:1234/api/spark/view/users' \
  -H 'Accept: application/json'
```

### 3.7 检查临时视图是否存在

```bash
curl -X GET 'http://localhost:1234/api/spark/view/users/exists' \
  -H 'Accept: application/json'
```

### 3.8 列出所有临时视图

```bash
curl -X GET 'http://localhost:1234/api/spark/view/list' \
  -H 'Accept: application/json'
```

### 3.9 获取视图 Schema

```bash
curl -X GET 'http://localhost:1234/api/spark/view/users/schema' \
  -H 'Accept: application/json'
```

### 3.10 预览视图数据

```bash
curl -X GET 'http://localhost:1234/api/spark/view/users/preview?limit=10' \
  -H 'Accept: application/json'
```

---

## 4. SQL 操作

### 4.1 校验 SQL 语法

```bash
curl -X POST 'http://localhost:1234/api/spark/sql/validate' \
  -H 'Content-Type: application/json' \
  -H 'Accept: application/json' \
  -d '{
    "sql": "SELECT * FROM users WHERE age > 25"
  }'
```

### 4.2 解析 SELECT SQL

```bash
curl -X POST 'http://localhost:1234/api/spark/sql/parse' \
  -H 'Content-Type: application/json' \
  -H 'Accept: application/json' \
  -d '{
    "sql": "SELECT name, age FROM users WHERE age > 25"
  }'
```

### 4.3 执行 SQL 查询（POST）

```bash
curl -X POST 'http://localhost:1234/api/spark/sql/execute' \
  -H 'Content-Type: application/json' \
  -H 'Accept: application/json' \
  -d '{
    "sql": "SELECT * FROM users WHERE age > 25 ORDER BY age DESC",
    "limit": 10
  }'
```

### 4.4 执行 SQL 查询（GET）

```bash
curl -X GET 'http://localhost:1234/api/spark/sql/execute?sql=SELECT * FROM users&limit=10' \
  -H 'Accept: application/json'
```

---

## 完整测试流程示例

### 流程 1: 从 CSV 创建视图并查询

```bash
# 步骤 1: 从 CSV 创建视图
curl -X POST 'http://localhost:1234/api/spark/view/create-by-sql' \
  -H 'Content-Type: application/json' \
  -H 'Accept: application/json' \
  -d '{
    "sql": "CREATE TEMPORARY VIEW users AS SELECT * FROM csv.`test-data/test.csv` OPTIONS (header \"true\", inferSchema \"true\")"
  }'

# 步骤 2: 预览数据
curl -X GET 'http://localhost:1234/api/spark/view/users/preview?limit=5' \
  -H 'Accept: application/json'

# 步骤 3: 执行查询
curl -X POST 'http://localhost:1234/api/spark/sql/execute' \
  -H 'Content-Type: application/json' \
  -H 'Accept: application/json' \
  -d '{
    "sql": "SELECT name, age FROM users WHERE age > 20 ORDER BY age DESC",
    "limit": 10
  }'

# 步骤 4: 删除视图
curl -X DELETE 'http://localhost:1234/api/spark/view/users' \
  -H 'Accept: application/json'
```

### 流程 2: 多视图操作

```bash
# 步骤 1: 创建基础视图
curl -X POST 'http://localhost:1234/api/spark/view/create-by-sql' \
  -H 'Content-Type: application/json' \
  -H 'Accept: application/json' \
  -d '{
    "sql": "CREATE TEMPORARY VIEW users AS SELECT * FROM csv.`test-data/test.csv` OPTIONS (header \"true\", inferSchema \"true\")"
  }'

# 步骤 2: 创建过滤视图
curl -X POST 'http://localhost:1234/api/spark/view/create-by-sql' \
  -H 'Content-Type: application/json' \
  -H 'Accept: application/json' \
  -d '{
    "sql": "CREATE TEMPORARY VIEW young_users AS SELECT * FROM users WHERE age < 30"
  }'

# 步骤 3: 创建聚合视图
curl -X POST 'http://localhost:1234/api/spark/view/create-by-sql' \
  -H 'Content-Type: application/json' \
  -H 'Accept: application/json' \
  -d '{
    "sql": "CREATE TEMPORARY VIEW user_stats AS SELECT COUNT(*) as count, AVG(age) as avg_age FROM users"
  }'

# 步骤 4: 列出所有视图
curl -X GET 'http://localhost:1234/api/spark/view/list' \
  -H 'Accept: application/json'

# 步骤 5: 查询聚合视图
curl -X POST 'http://localhost:1234/api/spark/sql/execute' \
  -H 'Content-Type: application/json' \
  -H 'Accept: application/json' \
  -d '{
    "sql": "SELECT * FROM user_stats",
    "limit": 10
  }'
```

---

## 注意事项

1. **文件路径**: 确保 `test-data/test.csv` 和 `test-data/test.json` 文件存在
2. **视图名称**: 如果视图已存在，使用 `CREATE OR REPLACE TEMPORARY VIEW` 可以替换
3. **SQL 转义**: JSON 中的 SQL 语句需要转义双引号
4. **端口**: 默认端口是 1234，根据实际情况修改

---

## 快速复制区域

### 最常用的接口

```bash
# 1. 健康检查
curl -X GET 'http://localhost:1234/api/spark/health'

# 2. 从 CSV 创建视图
curl -X POST 'http://localhost:1234/api/spark/view/create-by-sql' \
  -H 'Content-Type: application/json' \
  -d '{"sql": "CREATE TEMPORARY VIEW users AS SELECT * FROM csv.`test-data/test.csv` OPTIONS (header \"true\", inferSchema \"true\")"}'

# 3. 执行 SQL 查询
curl -X POST 'http://localhost:1234/api/spark/sql/execute' \
  -H 'Content-Type: application/json' \
  -d '{"sql": "SELECT * FROM users", "limit": 10}'

# 4. 列出所有视图
curl -X GET 'http://localhost:1234/api/spark/view/list'

# 5. 删除视图
curl -X DELETE 'http://localhost:1234/api/spark/view/users'
```
