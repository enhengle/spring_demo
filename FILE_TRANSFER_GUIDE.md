# 文件传输功能使用指南

## 📋 概述

本项目提供了完整的文件传输功能，支持多种传输方式：
- **FTP** (File Transfer Protocol)
- **SFTP** (SSH File Transfer Protocol)
- **本地文件系统** (用于测试)

## 🏗️ 架构设计

### 核心组件

1. **FileTransferService 接口** (`util.filetransfer.FileTransferService`)
   - 定义统一的文件传输接口
   - 包含连接、上传、下载、删除、列表等通用方法

2. **实现类**
   - `FtpTransferService`: FTP传输实现
   - `SftpTransferService`: SFTP传输实现
   - `LocalFileTransferService`: 本地文件系统实现

3. **FileTransferFactory 工厂类**
   - 根据传输类型创建相应的服务实例

4. **FileTransferConfig 配置类**
   - 统一的配置管理

## 📦 依赖说明

已在 `pom.xml` 中添加以下依赖：

```xml
<!-- Apache Commons Net - FTP 文件传输 -->
<dependency>
    <groupId>commons-net</groupId>
    <artifactId>commons-net</artifactId>
    <version>3.9.0</version>
</dependency>

<!-- JSch - SFTP/SSH 文件传输 -->
<dependency>
    <groupId>com.jcraft</groupId>
    <artifactId>jsch</artifactId>
    <version>0.1.55</version>
</dependency>
```

## 🚀 使用方法

### 1. FTP 文件传输

#### 上传文件

```java
// 创建配置
FileTransferConfig config = new FileTransferConfig();
config.setHost("ftp.example.com");
config.setPort(21);
config.setUsername("username");
config.setPassword("password");
config.setPassiveMode(true); // 使用被动模式

// 创建服务
FileTransferService ftpService = FileTransferFactory.createService(FileTransferFactory.TransferType.FTP);

// 连接
if (ftpService.connect(config)) {
    // 上传文件
    boolean success = ftpService.uploadFile("/local/path/file.txt", "/remote/path/file.txt");
    
    // 断开连接
    ftpService.disconnect();
}
```

#### 下载文件

```java
FileTransferService ftpService = FileTransferFactory.createService(FileTransferFactory.TransferType.FTP);
if (ftpService.connect(config)) {
    boolean success = ftpService.downloadFile("/remote/path/file.txt", "/local/path/file.txt");
    ftpService.disconnect();
}
```

#### 列出文件

```java
FileTransferService ftpService = FileTransferFactory.createService(FileTransferFactory.TransferType.FTP);
if (ftpService.connect(config)) {
    List<String> files = ftpService.listFiles("/remote/path");
    ftpService.disconnect();
}
```

### 2. SFTP 文件传输

#### 上传文件

```java
// 创建配置
FileTransferConfig config = new FileTransferConfig();
config.setHost("sftp.example.com");
config.setPort(22);
config.setUsername("username");
config.setPassword("password");

// 如果使用密钥认证
// config.setPrivateKeyPath("/path/to/private/key");
// config.setPrivateKeyPassphrase("passphrase");

// 创建服务
FileTransferService sftpService = FileTransferFactory.createService(FileTransferFactory.TransferType.SFTP);

// 连接并上传
if (sftpService.connect(config)) {
    boolean success = sftpService.uploadFile("/local/path/file.txt", "/remote/path/file.txt");
    sftpService.disconnect();
}
```

#### 下载文件

```java
FileTransferService sftpService = FileTransferFactory.createService(FileTransferFactory.TransferType.SFTP);
if (sftpService.connect(config)) {
    boolean success = sftpService.downloadFile("/remote/path/file.txt", "/local/path/file.txt");
    sftpService.disconnect();
}
```

### 3. 使用 Service 层（推荐）

```java
@Autowired
private FileTransferService fileTransferService;

// 上传到FTP
FileTransferConfig config = new FileTransferConfig("ftp.example.com", 21, "user", "pass");
boolean success = fileTransferService.uploadToFtp(multipartFile, config, "/remote/path/file.txt");

// 从SFTP下载
boolean success = fileTransferService.downloadFromSftp(config, "/remote/path/file.txt", "/local/path/file.txt");
```

## 🌐 REST API 接口

### FTP 接口

#### 上传文件到FTP服务器

```bash
POST /api/file-transfer/ftp/upload
Content-Type: multipart/form-data

file: <文件>
host: ftp.example.com
port: 21
username: username
password: password
remotePath: /remote/path/file.txt
```

#### 从FTP服务器下载文件

```bash
POST /api/file-transfer/ftp/download

host: ftp.example.com
port: 21
username: username
password: password
remotePath: /remote/path/file.txt
localPath: /local/path/file.txt
```

#### 列出FTP服务器文件

```bash
GET /api/file-transfer/ftp/list

host: ftp.example.com
port: 21
username: username
password: password
remotePath: /remote/path
```

### SFTP 接口

#### 上传文件到SFTP服务器

```bash
POST /api/file-transfer/sftp/upload
Content-Type: multipart/form-data

file: <文件>
host: sftp.example.com
port: 22
username: username
password: password
remotePath: /remote/path/file.txt
```

#### 从SFTP服务器下载文件

```bash
POST /api/file-transfer/sftp/download

host: sftp.example.com
port: 22
username: username
password: password
remotePath: /remote/path/file.txt
localPath: /local/path/file.txt
```

#### 列出SFTP服务器文件

```bash
GET /api/file-transfer/sftp/list

host: sftp.example.com
port: 22
username: username
password: password
remotePath: /remote/path
```

## 🔧 配置说明

### FileTransferConfig 配置项

| 配置项 | 类型 | 说明 | 默认值 |
|--------|------|------|--------|
| `host` | String | 服务器地址 | - |
| `port` | int | 服务器端口 | - |
| `username` | String | 用户名 | - |
| `password` | String | 密码 | - |
| `remotePath` | String | 远程路径 | - |
| `timeout` | int | 超时时间（毫秒） | 30000 |
| `passiveMode` | boolean | FTP被动模式 | true |
| `encoding` | String | 编码 | UTF-8 |
| `privateKeyPath` | String | SFTP私钥路径 | - |
| `privateKeyPassphrase` | String | SFTP私钥密码 | - |
| `connectTimeout` | int | 连接超时（毫秒） | 10000 |

## 📝 通用方法说明

### FileTransferService 接口方法

| 方法 | 说明 |
|------|------|
| `connect(FileTransferConfig)` | 连接服务器 |
| `disconnect()` | 断开连接 |
| `uploadFile(String, String)` | 上传文件（本地路径 -> 远程路径） |
| `uploadFile(InputStream, String)` | 上传文件（输入流 -> 远程路径） |
| `downloadFile(String, String)` | 下载文件（远程路径 -> 本地路径） |
| `downloadFile(String)` | 下载文件（返回输入流） |
| `deleteFile(String)` | 删除文件 |
| `listFiles(String)` | 列出目录文件 |
| `createDirectory(String)` | 创建目录 |
| `deleteDirectory(String)` | 删除目录 |
| `exists(String)` | 检查文件是否存在 |
| `getFileSize(String)` | 获取文件大小 |
| `rename(String, String)` | 重命名文件 |

## 🧪 单元测试

### 测试用例

1. **LocalFileTransferServiceTest**
   - 测试本地文件传输功能
   - 包括上传、下载、删除、列表等操作

2. **FileTransferFactoryTest**
   - 测试工厂类创建服务实例

### 运行测试

```bash
# 运行所有文件传输测试
mvn test -Dtest=*FileTransfer*Test

# 运行本地文件传输测试
mvn test -Dtest=LocalFileTransferServiceTest

# 运行工厂类测试
mvn test -Dtest=FileTransferFactoryTest
```

## ⚠️ 注意事项

### FTP

1. **被动模式 vs 主动模式**
   - 默认使用被动模式（`passiveMode = true`）
   - 如果遇到连接问题，可以尝试切换模式

2. **防火墙**
   - 被动模式需要开放数据端口范围
   - 主动模式需要客户端开放端口

3. **编码问题**
   - 默认使用 UTF-8 编码
   - 如果遇到中文文件名问题，可以调整编码

### SFTP

1. **密钥认证**
   - 支持密码认证和密钥认证
   - 密钥认证更安全，推荐使用

2. **主机密钥检查**
   - 当前实现跳过主机密钥检查（`StrictHostKeyChecking = no`）
   - 生产环境应设置为 `yes` 并配置已知主机

3. **权限问题**
   - 确保用户有相应的读写权限

## 🔒 安全建议

1. **密码管理**
   - 不要在代码中硬编码密码
   - 使用配置文件或环境变量
   - 考虑使用密钥认证（SFTP）

2. **连接加密**
   - SFTP 默认加密传输
   - FTP 建议使用 FTPS（FTP over SSL/TLS）

3. **权限控制**
   - 使用最小权限原则
   - 限制文件访问范围

## 📚 参考资源

- [Apache Commons Net 文档](https://commons.apache.org/proper/commons-net/)
- [JSch 文档](http://www.jcraft.com/jsch/)
- [FTP 协议规范](https://tools.ietf.org/html/rfc959)
- [SFTP 协议规范](https://tools.ietf.org/html/draft-ietf-secsh-filexfer-02)

---

**注意**：FTP 和 SFTP 的实际连接测试需要真实的服务器环境。单元测试主要使用本地文件系统进行功能验证。
