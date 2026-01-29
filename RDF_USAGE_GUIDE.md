# RDF 操作使用指南

## 📋 概述

本项目提供了完整的 RDF（Resource Description Framework）操作功能，基于 Apache Jena 4.7.0 实现。支持 RDF 文件的读写、查询、转换等常见功能。

## 🚀 功能特性

- ✅ RDF 文件读写（支持 RDF/XML, Turtle, N-Triples, JSON-LD 等格式）
- ✅ 三元组（Triple）的创建、查询、删除
- ✅ SPARQL 查询（SELECT, ASK, CONSTRUCT）
- ✅ 命名空间和前缀管理
- ✅ RDF 模型操作（合并、交集、差集）
- ✅ 资源类型和属性查询
- ✅ 格式转换（RDF/XML ↔ Turtle ↔ N-Triples ↔ JSON-LD）
- ✅ RDF 验证

## 📦 依赖

项目使用 Apache Jena 4.7.0：

```xml
<dependency>
    <groupId>org.apache.jena</groupId>
    <artifactId>jena-core</artifactId>
    <version>4.7.0</version>
</dependency>
<dependency>
    <groupId>org.apache.jena</groupId>
    <artifactId>jena-arq</artifactId>
    <version>4.7.0</version>
</dependency>
<dependency>
    <groupId>org.apache.jena</groupId>
    <artifactId>jena-iri</artifactId>
    <version>4.7.0</version>
</dependency>
```

## 🔧 工具类方法

### RdfUtil 工具类

#### 1. 创建 RDF 模型

```java
// 创建空的 RDF 模型
Model model = RdfUtil.createModel();

// 创建带命名空间的 RDF 模型
Model model = RdfUtil.createModel("http://example.org/", "ex");
```

#### 2. 读取 RDF 文件

```java
// 从文件读取（自动识别格式）
Model model = RdfUtil.readModel("data.rdf");

// 指定格式读取
Model model = RdfUtil.readModel("data.ttl", "TURTLE");
Model model = RdfUtil.readModel("data.xml", "RDF/XML");
Model model = RdfUtil.readModel("data.nt", "N-TRIPLES");
Model model = RdfUtil.readModel("data.jsonld", "JSON-LD");
```

#### 3. 写入 RDF 文件

```java
// 写入文件（默认 Turtle 格式）
RdfUtil.writeModel(model, "output.ttl");

// 指定格式写入
RdfUtil.writeModel(model, "output.xml", "RDF/XML");
RdfUtil.writeModel(model, "output.nt", "N-TRIPLES");
RdfUtil.writeModel(model, "output.jsonld", "JSON-LD");
```

#### 4. 添加三元组

```java
String ns = "http://example.org/";
String personUri = ns + "alice";
String nameProperty = ns + "name";

// 添加单个三元组
RdfUtil.addStatement(model, personUri, nameProperty, "Alice");

// 批量添加三元组
List<String[]> triples = Arrays.asList(
    new String[]{personUri, nameProperty, "Alice"},
    new String[]{personUri, ns + "age", "30"}
);
RdfUtil.addStatements(model, triples);
```

#### 5. 查询三元组

```java
// 查询所有三元组
List<Map<String, String>> allStatements = RdfUtil.listStatements(model);

// 按主体查询
List<Map<String, String>> statements = RdfUtil.listStatements(
    model, personUri, null, null);

// 按谓词查询
List<Map<String, String>> statements = RdfUtil.listStatements(
    model, null, nameProperty, null);

// 按客体查询
List<Map<String, String>> statements = RdfUtil.listStatements(
    model, null, null, "Alice");
```

#### 6. 执行 SPARQL 查询

```java
// SELECT 查询
String query = "PREFIX ex: <http://example.org/> " +
              "SELECT ?name ?age WHERE { " +
              "  ?person ex:name ?name . " +
              "  ?person ex:age ?age . " +
              "}";
List<Map<String, String>> results = RdfUtil.executeQuery(model, query);

// ASK 查询（返回布尔值）
String askQuery = "PREFIX ex: <http://example.org/> " +
                  "ASK { ?person ex:name \"Alice\" }";
boolean exists = RdfUtil.executeAskQuery(model, askQuery);

// CONSTRUCT 查询（返回 RDF 模型）
String constructQuery = "PREFIX ex: <http://example.org/> " +
                       "CONSTRUCT { ?person ex:hasName ?name } " +
                       "WHERE { ?person ex:name ?name }";
Model constructed = RdfUtil.executeConstructQuery(model, constructQuery);
```

#### 7. 资源操作

```java
String ns = "http://example.org/";
String personUri = ns + "alice";
String personType = ns + "Person";

// 添加资源类型
RdfUtil.addResourceType(model, personUri, personType);

// 查询指定类型的所有资源
List<String> persons = RdfUtil.getResourcesByType(model, personType);

// 获取资源的所有属性
Map<String, List<String>> properties = RdfUtil.getResourceProperties(
    model, personUri);

// 添加资源标签（支持多语言）
RdfUtil.addResourceLabel(model, personUri, "Alice", "en");
RdfUtil.addResourceLabel(model, personUri, "爱丽丝", "zh");

// 添加资源注释
RdfUtil.addResourceComment(model, personUri, "This is Alice", "en");
```

#### 8. 模型操作

```java
Model model1 = RdfUtil.createModel();
Model model2 = RdfUtil.createModel();

// 合并模型
Model merged = RdfUtil.union(model1, model2);

// 获取交集
Model intersection = RdfUtil.intersection(model1, model2);

// 获取差集（model1 - model2）
Model difference = RdfUtil.difference(model1, model2);

// 获取模型大小
long size = RdfUtil.getSize(model);

// 检查是否为空
boolean isEmpty = RdfUtil.isEmpty(model);

// 清空模型
RdfUtil.clear(model);
```

#### 9. 格式转换

```java
// 转换为不同格式的字符串
String turtle = RdfUtil.toTurtle(model);
String rdfXml = RdfUtil.toRdfXml(model);
String nTriples = RdfUtil.toNTriples(model);
String jsonLd = RdfUtil.toJsonLd(model);

// 从 JSON-LD 创建模型
String jsonLdString = "{\"@context\":{\"ex\":\"http://example.org/\"}," +
                     "\"@id\":\"http://example.org/alice\"," +
                     "\"ex:name\":\"Alice\"}";
Model model = RdfUtil.fromJsonLd(jsonLdString);

// 转换文件格式
RdfUtil.convertFormat("input.ttl", "output.xml", "RDF/XML");
```

#### 10. 命名空间管理

```java
// 设置命名空间前缀
RdfUtil.setNamespace(model, "ex", "http://example.org/");
RdfUtil.setNamespace(model, "foaf", "http://xmlns.com/foaf/0.1/");

// 获取所有命名空间
Map<String, String> namespaces = RdfUtil.getNamespaces(model);
```

#### 11. 删除三元组

```java
// 删除指定三元组
RdfUtil.removeStatement(model, personUri, nameProperty, "Alice");

// 删除资源的所有三元组
RdfUtil.removeStatement(model, personUri, null, null);

// 删除指定谓词的所有三元组
RdfUtil.removeStatement(model, personUri, nameProperty, null);
```

## 🌐 REST API 接口

### 1. 验证 RDF 文件

```bash
POST /api/rdf/validate
Content-Type: multipart/form-data

file: <RDF文件>
lang: TURTLE (可选)
```

### 2. 执行 SPARQL 查询

```bash
POST /api/rdf/query
Content-Type: multipart/form-data

file: <RDF文件>
query: PREFIX ex: <http://example.org/> SELECT ?name WHERE { ?person ex:name ?name }
lang: TURTLE (可选)
```

### 3. 执行 SPARQL ASK 查询

```bash
POST /api/rdf/ask
Content-Type: multipart/form-data

file: <RDF文件>
query: PREFIX ex: <http://example.org/> ASK { ?person ex:name "Alice" }
lang: TURTLE (可选)
```

### 4. 查询三元组

```bash
POST /api/rdf/statements
Content-Type: multipart/form-data

file: <RDF文件>
subject: http://example.org/alice (可选)
predicate: http://example.org/name (可选)
object: Alice (可选)
lang: TURTLE (可选)
```

### 5. 获取资源属性

```bash
POST /api/rdf/resource/properties
Content-Type: multipart/form-data

file: <RDF文件>
resourceUri: http://example.org/alice
lang: TURTLE (可选)
```

### 6. 按类型查询资源

```bash
POST /api/rdf/resources/by-type
Content-Type: multipart/form-data

file: <RDF文件>
typeUri: http://example.org/Person
lang: TURTLE (可选)
```

### 7. 转换 RDF 格式

```bash
POST /api/rdf/convert
Content-Type: multipart/form-data

file: <RDF文件>
outputLang: JSON-LD (TURTLE, RDF/XML, N-TRIPLES, JSON-LD)
inputLang: TURTLE (可选)
```

### 8. 合并 RDF 文件

```bash
POST /api/rdf/merge
Content-Type: multipart/form-data

file1: <第一个RDF文件>
file2: <第二个RDF文件>
outputLang: TURTLE
lang1: TURTLE (可选)
lang2: TURTLE (可选)
```

## 📝 使用示例

### 示例 1：创建简单的 RDF 模型

```java
// 创建模型并设置命名空间
String ns = "http://example.org/";
Model model = RdfUtil.createModel(ns, "ex");

// 添加三元组
RdfUtil.addStatement(model, ns + "alice", ns + "name", "Alice");
RdfUtil.addStatement(model, ns + "alice", ns + "age", "30");
RdfUtil.addResourceType(model, ns + "alice", ns + "Person");

// 保存为 Turtle 格式
RdfUtil.writeModel(model, "alice.ttl", "TURTLE");
```

### 示例 2：查询和转换

```java
// 读取 RDF 文件
Model model = RdfUtil.readModel("data.ttl", "TURTLE");

// 执行 SPARQL 查询
String query = "PREFIX ex: <http://example.org/> " +
              "SELECT ?name WHERE { " +
              "  ?person ex:name ?name . " +
              "}";
List<Map<String, String>> results = RdfUtil.executeQuery(model, query);

// 转换为 JSON-LD
String jsonLd = RdfUtil.toJsonLd(model);
System.out.println(jsonLd);
```

### 示例 3：合并多个 RDF 文件

```java
Model model1 = RdfUtil.readModel("file1.ttl", "TURTLE");
Model model2 = RdfUtil.readModel("file2.ttl", "TURTLE");

Model merged = RdfUtil.union(model1, model2);
RdfUtil.writeModel(merged, "merged.ttl", "TURTLE");
```

### 示例 4：使用 REST API

```bash
# 验证 RDF 文件
curl -X POST "http://localhost:1234/api/rdf/validate" \
  -F "file=@data.ttl" \
  -F "lang=TURTLE"

# 执行查询
curl -X POST "http://localhost:1234/api/rdf/query" \
  -F "file=@data.ttl" \
  -F "query=PREFIX ex: <http://example.org/> SELECT ?name WHERE { ?person ex:name ?name }"

# 转换格式
curl -X POST "http://localhost:1234/api/rdf/convert" \
  -F "file=@data.ttl" \
  -F "outputLang=JSON-LD"
```

## 🔍 支持的 RDF 格式

- **RDF/XML**: XML 格式的 RDF
- **Turtle (TTL)**: 人类可读的 RDF 格式
- **N-Triples (NT)**: 每行一个三元组的简单格式
- **JSON-LD**: JSON 格式的 RDF

## 📚 相关资源

- [Apache Jena 官方文档](https://jena.apache.org/)
- [SPARQL 查询语言规范](https://www.w3.org/TR/sparql11-query/)
- [RDF 1.1 规范](https://www.w3.org/TR/rdf11-concepts/)

## ⚠️ 注意事项

1. **文件格式识别**: 如果不指定格式，系统会尝试自动识别，但建议明确指定格式以确保正确解析。

2. **命名空间**: 使用命名空间前缀可以简化 RDF 表示，提高可读性。

3. **SPARQL 查询**: 确保查询字符串中的命名空间前缀与模型中的命名空间一致。

4. **资源管理**: 使用完 Model 对象后，建议调用 `model.close()` 释放资源（如果使用 InfModel 等特殊模型）。

5. **大文件处理**: 对于大型 RDF 文件，考虑使用流式处理或分块读取。

## 🧪 测试

运行测试用例：

```bash
# 运行所有 RDF 相关测试
mvn test -Dtest=RdfUtilTest

# 运行特定测试方法
mvn test -Dtest=RdfUtilTest#testExecuteQuery
```
