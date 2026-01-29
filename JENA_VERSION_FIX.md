# Jena 版本兼容性修复说明

## 问题描述

编译错误：`无法访问org.apache.jena.rdf.model.Model`

**原因：**
- Apache Jena 4.7.0 需要 Java 11+，而项目使用 Java 8
- 本地 Maven 仓库中的 JAR 文件可能损坏或不完整

## 解决方案

### 1. 已完成的修改

已将 `pom.xml` 中的 Jena 版本从 4.7.0 降级到 3.17.0（兼容 Java 8）：

```xml
<!-- Apache Jena - RDF 操作 (使用 3.x 版本以兼容 Java 8) -->
<dependency>
    <groupId>org.apache.jena</groupId>
    <artifactId>jena-core</artifactId>
    <version>3.17.0</version>
</dependency>
<dependency>
    <groupId>org.apache.jena</groupId>
    <artifactId>jena-arq</artifactId>
    <version>3.17.0</version>
</dependency>
<dependency>
    <groupId>org.apache.jena</groupId>
    <artifactId>jena-iri</artifactId>
    <version>3.17.0</version>
</dependency>
```

### 2. 需要执行的步骤

#### 步骤 1：清理本地 Maven 仓库中的 Jena 文件

**Windows PowerShell:**
```powershell
# 删除本地仓库中的 Jena 4.7.0 文件
Remove-Item -Recurse -Force "$env:USERPROFILE\.m2\repository\org\apache\jena\jena-core\4.7.0"
Remove-Item -Recurse -Force "$env:USERPROFILE\.m2\repository\org\apache\jena\jena-arq\4.7.0"
Remove-Item -Recurse -Force "$env:USERPROFILE\.m2\repository\org\apache\jena\jena-iri\4.7.0"
```

**或者手动删除：**
```
删除目录：D:\maven_repo\org\apache\jena\jena-core\4.7.0
删除目录：D:\maven_repo\org\apache\jena\jena-arq\4.7.0
删除目录：D:\maven_repo\org\apache\jena\jena-iri\4.7.0
```

#### 步骤 2：清理项目并重新编译

```bash
# 清理项目
mvn clean

# 强制更新依赖并编译
mvn clean compile -U

# 或者完整构建
mvn clean install -U
```

#### 步骤 3：验证依赖下载

检查是否成功下载了 Jena 3.17.0：

```
D:\maven_repo\org\apache\jena\jena-core\3.17.0\jena-core-3.17.0.jar
D:\maven_repo\org\apache\jena\jena-arq\3.17.0\jena-arq-3.17.0.jar
D:\maven_repo\org\apache\jena\jena-iri\3.17.0\jena-iri-3.17.0.jar
```

### 3. 版本兼容性说明

| Jena 版本 | Java 版本要求 | 说明 |
|----------|--------------|------|
| 3.x      | Java 8+      | 稳定版本，推荐用于 Java 8 项目 |
| 4.x      | Java 11+     | 新版本，需要 Java 11 或更高版本 |

### 4. API 兼容性

Jena 3.17.0 与 4.x 的 API 基本兼容，代码无需修改。主要 API 如 `RDFDataMgr`、`ModelFactory`、`Lang` 等在两个版本中都可用。

### 5. 如果问题仍然存在

如果清理后仍然有问题，可以尝试：

1. **检查 Maven 配置**
   - 确认 Maven 设置正确
   - 检查网络连接，确保可以访问 Maven Central

2. **手动下载依赖**
   ```bash
   mvn dependency:resolve -U
   ```

3. **检查 IDE 设置**
   - 在 IDE（如 IntelliJ IDEA）中刷新 Maven 项目
   - 重新导入 Maven 项目

4. **验证 Java 版本**
   ```bash
   java -version
   ```
   确保使用的是 Java 8 或更高版本

### 6. 验证修复

编译成功后，运行测试验证：

```bash
mvn test -Dtest=RdfUtilTest
```

## 参考

- [Apache Jena 官方文档](https://jena.apache.org/)
- [Jena 3.17.0 发布说明](https://jena.apache.org/download/index.cgi)
