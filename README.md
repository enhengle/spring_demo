# Spring Demo 项目

一个基于 Spring Boot 的演示项目，集成了多种 AI 服务和工作流功能。

## 📋 项目简介

本项目是一个 Spring Boot 演示项目，主要功能包括：

- **Coze SQL识别工作流**：将自然语言转换为 SQL 语句
- **Dify AI助手**：集成 Dify AI 对话功能（支持阻塞和流式模式）
- **用户管理**：基础的 CRUD 功能
- **操作日志**：AOP 切面记录操作日志
- **优雅停机**：支持优雅停机功能

## 🚀 技术栈

- **框架**：Spring Boot 2.6.3
- **Java版本**：1.8
- **数据库**：MySQL
- **ORM**：MyBatis-Plus 3.5.3
- **API文档**：Swagger/OpenAPI 2.2.0
- **配置中心**：Apollo（可选）
- **工具库**：Lombok、Jackson

## 📁 项目结构

```
src/main/java/com/practise/demo/
├── config/              # 配置类
│   ├── CozeConfig.java          # Coze配置
│   ├── RestTemplateConfig.java  # RestTemplate配置
│   ├── SwaggerConfig.java       # Swagger配置
│   └── ...
├── controller/          # 控制器层
│   ├── CozeController.java     # Coze工作流控制器
│   ├── UserController.java      # 用户控制器
│   └── ...
├── service/            # 服务层
│   ├── CozeService.java         # Coze服务接口
│   ├── impl/
│   │   └── CozeServiceImpl.java # Coze服务实现
│   └── ...
├── model/              # 数据模型
│   ├── dto/                    # 数据传输对象
│   │   ├── CozeWorkflowRequestDTO.java
│   │   └── CozeWorkflowResponseDTO.java
│   └── entity/                 # 实体类
├── exception/          # 异常处理
├── common/             # 公共类
└── util/               # 工具类

src/main/resources/
├── application.yml     # 配置文件
└── static/            # 静态资源
    └── coze-test.html # Coze测试页面
```

## ⚙️ 配置说明

### 1. 数据库配置

在 `application.yml` 中配置数据库连接：

```yaml
spring:
  datasource:
    driver-class-name: com.mysql.cj.jdbc.Driver
    url: jdbc:mysql://localhost:3306/demo?useUnicode=true&characterEncoding=utf8&useSSL=false&serverTimezone=Asia/Shanghai
    username: root
    password: root
```

### 2. Coze配置

配置 Coze SQL识别工作流：

```yaml
coze:
  workflow-url: ${COZE_WORKFLOW_URL:https://8xdkxnw4qj.coze.site/run}
  token: ${COZE_TOKEN:your-token-here}
  connect-timeout: ${COZE_CONNECT_TIMEOUT:10000}
  read-timeout: ${COZE_READ_TIMEOUT:60000}
  sse-timeout: ${COZE_SSE_TIMEOUT:300000}
```

**获取Token**：
1. 访问 [Coze平台](https://www.coze.cn)
2. 创建工作流并获取访问Token
3. 将Token配置到 `application.yml` 或环境变量中

### 3. Dify配置（可选）

如果需要使用 Dify AI 功能：

```yaml
dify:
  base-url: ${DIFY_BASE_URL:https://api.dify.ai/v1}
  api-key: ${DIFY_API_KEY:your-api-key}
  app-id: ${DIFY_APP_ID:your-app-id}
  connect-timeout: ${DIFY_CONNECT_TIMEOUT:10000}
  read-timeout: ${DIFY_READ_TIMEOUT:60000}
  sse-timeout: ${DIFY_SSE_TIMEOUT:300000}
```

### 4. 服务器配置

```yaml
server:
  port: ${SERVER_PORT:1234}
  shutdown: graceful  # 优雅停机
```

## 🎯 功能说明

### 1. Coze SQL识别工作流

将自然语言转换为 SQL 语句。

#### API接口

**阻塞模式**：
```http
POST /api/coze/workflow
Content-Type: application/json

{
  "input": "查询所有用户信息",
  "responseMode": "blocking"
}
```

**流式模式**：
```http
POST /api/coze/workflow
Content-Type: application/json

{
  "input": "查询所有用户信息",
  "responseMode": "streaming"
}
```

#### 响应格式

```json
{
  "code": 0,
  "message": "操作成功",
  "data": {
    "result": "SELECT * FROM users",
    "sql": "SELECT * FROM users",
    "taskId": "task-123456",
    "status": "success"
  }
}
```

#### 测试页面

访问：`http://localhost:1234/coze-test.html`

功能特性：
- ✅ 支持阻塞模式和流式模式
- ✅ SQL代码高亮显示
- ✅ 实时显示识别结果
- ✅ 显示任务ID和执行状态

### 2. Dify AI助手（可选）

AI对话功能，支持阻塞和流式两种模式。

#### API接口

**阻塞模式**：
```http
POST /api/dify/chat
Content-Type: application/json

{
  "message": "你好",
  "responseMode": "blocking",
  "conversationId": "可选"
}
```

**流式模式**：
```http
POST /api/dify/chat
Content-Type: application/json

{
  "message": "你好",
  "responseMode": "streaming",
  "conversationId": "可选"
}
```

### 3. 用户管理

基础的用户 CRUD 功能。

#### API接口

- `GET /api/user/list` - 获取用户列表
- `GET /api/user/{id}` - 获取用户详情
- `POST /api/user` - 创建用户
- `PUT /api/user` - 更新用户
- `DELETE /api/user/{id}` - 删除用户

## 🛠️ 快速开始

### 1. 环境要求

- JDK 1.8+
- Maven 3.6+
- MySQL 5.7+

### 2. 克隆项目

```bash
git clone <repository-url>
cd spring-demo
```

### 3. 配置数据库

创建数据库：
```sql
CREATE DATABASE demo CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
```

### 4. 配置应用

编辑 `src/main/resources/application.yml`，配置数据库连接和 Coze Token。

### 5. 编译运行

```bash
# 编译项目
mvn clean package

# 运行项目
mvn spring-boot:run

# 或使用jar包运行
java -jar target/demo-0.0.1-SNAPSHOT.jar
```

### 6. 访问应用

- **应用地址**：http://localhost:1234
- **Swagger文档**：http://localhost:1234/swagger-ui.html
- **Coze测试页面**：http://localhost:1234/coze-test.html

## 📚 API文档

### Swagger UI

启动应用后，访问：`http://localhost:1234/swagger-ui.html`

### 主要接口

#### Coze工作流

| 方法 | 路径 | 说明 |
|------|------|------|
| POST | `/api/coze/workflow` | 执行SQL识别工作流 |

#### Dify AI（可选）

| 方法 | 路径 | 说明 |
|------|------|------|
| POST | `/api/dify/chat` | 发送AI对话消息 |

#### 用户管理

| 方法 | 路径 | 说明 |
|------|------|------|
| GET | `/api/user/list` | 获取用户列表 |
| GET | `/api/user/{id}` | 获取用户详情 |
| POST | `/api/user` | 创建用户 |
| PUT | `/api/user` | 更新用户 |
| DELETE | `/api/user/{id}` | 删除用户 |

## 🔍 代码检查

### 编译状态

- ✅ **编译错误**：0个
- ⚠️ **警告**：9个（不影响功能）

### 警告说明

1. **GlobalExceptionHandler**：Response泛型未指定（5个警告）
   - 建议：使用 `Response<?>` 或具体类型

2. **GracefulShutdownConfig**：未使用的logger字段
   - 影响：无

3. **OperationLogAspect**：未使用的exception变量
   - 影响：无

4. **GracefulShutdownListener**：未使用的shutdownExecutor方法
   - 影响：无（可能是预留方法）

5. **pom.xml**：项目配置需要更新
   - 建议：运行 `mvn clean install` 更新配置

## 🧪 测试

### 单元测试

```bash
mvn test
```

### 集成测试

使用 Postman 或 Swagger UI 测试API接口。

### 前端测试页面

1. **Coze测试页面**：`http://localhost:1234/coze-test.html`
   - 测试SQL识别功能
   - 支持阻塞和流式两种模式

2. **Dify测试页面**（如果已实现）：`http://localhost:1234/dify-test.html`
   - 测试AI对话功能

## 📝 使用示例

### Coze SQL识别示例

**请求**：
```bash
curl -X POST http://localhost:1234/api/coze/workflow \
  -H "Content-Type: application/json" \
  -d '{
    "input": "查询所有用户信息",
    "responseMode": "blocking"
  }'
```

**响应**：
```json
{
  "code": 0,
  "message": "操作成功",
  "data": {
    "result": "SELECT * FROM users",
    "sql": "SELECT * FROM users",
    "taskId": "task-123456",
    "status": "success"
  }
}
```

### 流式模式示例

使用前端测试页面或支持SSE的客户端：

```javascript
fetch('/api/coze/workflow', {
  method: 'POST',
  headers: {
    'Content-Type': 'application/json'
  },
  body: JSON.stringify({
    input: '查询所有用户信息',
    responseMode: 'streaming'
  })
})
.then(response => {
  const reader = response.body.getReader();
  const decoder = new TextDecoder();
  
  function readStream() {
    reader.read().then(({ done, value }) => {
      if (done) return;
      
      const chunk = decoder.decode(value);
      console.log('收到数据:', chunk);
      
      readStream();
    });
  }
  
  readStream();
});
```

## 🐛 故障排查

### 问题1：Coze API调用失败

**错误信息**：`Coze API认证失败（401）`

**解决方案**：
1. 检查 `application.yml` 中的 `coze.token` 配置
2. 确认Token是否有效
3. 检查Token格式是否正确

### 问题2：数据库连接失败

**错误信息**：`Cannot connect to database`

**解决方案**：
1. 检查数据库服务是否启动
2. 检查 `application.yml` 中的数据库配置
3. 确认数据库用户权限

### 问题3：端口被占用

**错误信息**：`Port 1234 is already in use`

**解决方案**：
1. 修改 `application.yml` 中的 `server.port`
2. 或停止占用端口的进程

## 📖 参考文档

- [Spring Boot官方文档](https://spring.io/projects/spring-boot)
- [MyBatis-Plus文档](https://baomidou.com/)
- [Coze API文档](https://docs.coze.cn/developer_guides/coze_api_overview)
- [Dify API文档](https://docs.dify.ai)

## 📄 许可证

本项目仅供学习和演示使用。

## 👥 贡献

欢迎提交 Issue 和 Pull Request！

## 📞 联系方式

如有问题，请提交 Issue 或联系项目维护者。

---

**最后更新**：2024年
