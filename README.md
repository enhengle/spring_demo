# Spring Boot Demo 项目

## 项目简介

这是一个规范的Spring Boot项目，包含了企业级应用开发所需的基础功能和最佳实践。

## 项目结构

```
src/main/java/com/practise/demo/
├── common/                    # 公共层
│   ├── annotation/           # 自定义注解
│   │   └── OperationLog.java  # 操作日志注解
│   ├── aspect/               # AOP切面
│   │   └── OperationLogAspect.java  # 操作日志切面
│   ├── config/               # 配置类
│   │   ├── ApolloConfig.java         # Apollo配置（可选）
│   │   ├── GracefulShutdownConfig.java  # 优雅停机配置
│   │   ├── GracefulShutdownListener.java # 优雅停机监听器
│   │   ├── MyBatisPlusConfig.java    # MyBatis-Plus配置
│   │   └── SwaggerConfig.java        # Swagger配置
│   └── constant/             # 常量类
│       └── ErrorCode.java    # 错误码统一管理
├── controller/               # 接口层（Controller）
│   └── TestController.java
├── service/                  # 业务层（Service）
│   └── TestService.java
├── mapper/                   # 数据访问层（Mapper）
│   └── UserMapper.java
├── model/                    # 数据模型层
│   └── entity/               # 实体类
│       ├── BaseEntity.java   # 基础实体类
│       └── User.java         # 用户实体（示例）
├── myExceptionHandler/       # 异常处理
│   ├── ServerException.java
│   └── ServerExceptionHandler.java
├── response/                 # 响应类
│   ├── EnumCode.java
│   └── Response.java
└── util/                     # 工具类
    └── DateUtil.java
```

## 功能特性

### 1. 规范的工程结构
- ✅ **接口层（Controller）**：处理HTTP请求
- ✅ **业务层（Service）**：业务逻辑处理
- ✅ **数据访问层（Mapper）**：数据库操作
- ✅ **数据模型层（Model/Entity）**：数据实体
- ✅ **公共层（Common）**：公共组件、工具类、常量等

### 2. 多环境配置
项目支持多环境配置，通过`spring.profiles.active`指定环境：
- `dev`：开发环境（application-dev.yml）
- `test`：测试环境（application-test.yml）
- `prod`：生产环境（application-prod.yml）

**使用方式：**
```bash
# 通过环境变量
export SPRING_PROFILES_ACTIVE=prod

# 通过启动参数
java -jar demo.jar --spring.profiles.active=prod

# 在K8s中通过环境变量配置
env:
  - name: SPRING_PROFILES_ACTIVE
    value: "prod"
```

### 3. 配置中心兼容
项目兼容多种配置方式：
- **Apollo配置中心**：当`apollo.bootstrap.enabled=true`时启用
- **Application配置文件**：默认使用application.yml
- **环境变量**：支持通过环境变量覆盖配置
- **默认配置**：提供默认值，确保项目可正常运行

**配置优先级：** Apollo配置 > 环境变量 > application.yml > 默认值

### 4. 接口文档（Swagger/OpenAPI）
项目集成了Swagger 3（OpenAPI 3），自动生成接口文档。

**访问地址：**
- Swagger UI: http://localhost:1234/swagger-ui.html
- API Docs: http://localhost:1234/v3/api-docs

**使用示例：**
```java
@Tag(name = "用户接口", description = "用户相关接口")
@RestController
@RequestMapping("/user")
public class UserController {
    
    @Operation(summary = "获取用户", description = "根据ID获取用户信息")
    @GetMapping("/{id}")
    public Response<User> getUser(@PathVariable Long id) {
        // ...
    }
}
```

### 5. MyBatis-Plus数据访问层
项目使用MyBatis-Plus作为ORM框架，提供了：
- 自动填充（创建时间、更新时间）
- 逻辑删除
- 分页插件
- 基础CRUD操作

**使用示例：**
```java
@Mapper
public interface UserMapper extends BaseMapper<User> {
    // 继承BaseMapper即可使用基础CRUD方法
}

@Service
public class UserService {
    @Autowired
    private UserMapper userMapper;
    
    public User getUserById(Long id) {
        return userMapper.selectById(id);
    }
}
```

### 6. 操作审计（AOP实现）
通过AOP实现操作日志记录，自动记录接口调用信息。

**使用方式：**
```java
@OperationLog(value = "创建用户", type = "新增", recordParams = true, recordResult = false)
@PostMapping("/user")
public Response<User> createUser(@RequestBody User user) {
    // ...
}
```

**记录内容：**
- 操作类型、操作描述
- 请求IP、URL、请求方式
- 请求参数、返回结果（可选）
- 执行耗时
- 异常信息（如有）

### 7. 错误码统一管理
所有错误码统一在`ErrorCode`类中管理，便于维护和扩展。

**使用示例：**
```java
// 成功响应
return Response.ok(data);

// 错误响应
return Response.error(ErrorCode.PARAM_ERROR);
return Response.error(ErrorCode.DATA_NOT_FOUND, "用户不存在");
```

**错误码分类：**
- `1000-1999`：系统错误码
- `2000-2999`：业务错误码
- `3000-3999`：权限错误码

### 8. 优雅停机
项目实现了优雅停机功能，确保：
- 停止接收新请求
- 等待正在处理的请求完成
- 关闭线程池和连接
- 避免数据丢失和任务中断

**配置说明：**
```yaml
server:
  shutdown: graceful  # 启用优雅停机
spring:
  lifecycle:
    timeout-per-shutdown-phase: 30s  # 等待时间
```

**工作原理：**
1. 接收到停机信号（SIGTERM）后，停止接收新请求
2. 等待正在处理的请求完成（最多30秒）
3. 关闭自定义线程池
4. 关闭应用上下文

## 打包部署

### 打包成JAR
```bash
mvn clean package
```

生成的JAR文件位于：`target/demo-0.0.1-SNAPSHOT.jar`

### K8s部署
项目已配置为可打包成JAR包，适合在K8s容器中运行。

**Dockerfile示例：**
```dockerfile
FROM openjdk:8-jre-alpine
COPY target/demo-0.0.1-SNAPSHOT.jar app.jar
ENTRYPOINT ["java", "-jar", "/app.jar"]
```

**K8s Deployment示例：**
```yaml
apiVersion: apps/v1
kind: Deployment
metadata:
  name: spring-demo
spec:
  replicas: 2
  template:
    spec:
      containers:
      - name: app
        image: spring-demo:latest
        env:
        - name: SPRING_PROFILES_ACTIVE
          value: "prod"
        - name: DB_URL
          valueFrom:
            secretKeyRef:
              name: db-secret
              key: url
        - name: DB_USERNAME
          valueFrom:
            secretKeyRef:
              name: db-secret
              key: username
        - name: DB_PASSWORD
          valueFrom:
            secretKeyRef:
              name: db-secret
              key: password
        lifecycle:
          preStop:
            exec:
              command: ["/bin/sh", "-c", "sleep 10"]  # 给优雅停机留出时间
```

## 环境要求
- JDK 1.8+
- Maven 3.6+
- MySQL 5.7+（或MySQL 8.0+）

## 快速开始

1. **克隆项目**
```bash
git clone <repository-url>
cd spring-demo
```

2. **配置数据库**
修改`application-dev.yml`中的数据库连接信息

3. **运行项目**
```bash
mvn spring-boot:run
```

4. **访问接口**
- 应用地址：http://localhost:1234
- Swagger文档：http://localhost:1234/swagger-ui.html
- 测试接口：http://localhost:1234/test/get_port

## 注意事项

1. **Apollo配置**：如果使用Apollo配置中心，需要：
   - 在`application.yml`中启用Apollo配置
   - 配置Apollo Meta地址
   - 确保网络可达Apollo服务器

2. **优雅停机**：在K8s中部署时，建议配置`preStop`钩子，给应用留出优雅停机的时间。

3. **日志配置**：日志文件默认保存在`./logs`目录，可通过`common.logDir`配置修改。

4. **生产环境**：生产环境建议关闭Swagger UI，已在`application-prod.yml`中配置。

## 许可证

Apache 2.0
