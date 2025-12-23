# 日志配置说明

## 日志文件结构

项目配置了日志切割功能，日志文件结构如下：

```
logs/
├── application.log              # 当前普通日志文件
├── application-2024-01-01-0.log # 按日期和大小切割的普通日志文件
├── application-2024-01-01-1.log
├── error.log                    # 当前错误日志文件
└── error-2024-01-01-0.log      # 按日期和大小切割的错误日志文件
```

## 日志分类

### 1. 普通日志文件（application.log）
- **包含级别**：DEBUG、INFO
- **排除级别**：WARN、ERROR
- **文件命名**：`application-{日期}-{序号}.log`
- **切割规则**：
  - 按日期切割：每天一个文件
  - 按大小切割：单个文件超过100MB时创建新文件
  - 保留天数：30天
  - 总大小限制：10GB

### 2. 错误日志文件（error.log）
- **包含级别**：WARN、ERROR
- **文件命名**：`error-{日期}-{序号}.log`
- **切割规则**：
  - 按日期切割：每天一个文件
  - 按大小切割：单个文件超过100MB时创建新文件
  - 保留天数：90天（错误日志保留更长时间）
  - 总大小限制：5GB

## 日志切割说明

### 二次切割机制

日志文件采用**按日期和大小双重切割**机制：

1. **第一次切割（按日期）**：
   - 每天凌晨自动创建新的日志文件
   - 文件名格式：`application-2024-01-01-0.log`

2. **第二次切割（按大小）**：
   - 当单个日志文件超过100MB时，自动创建新文件
   - 文件名格式：`application-2024-01-01-1.log`（序号递增）

### 切割示例

假设2024年1月1日的日志：

```
application-2024-01-01-0.log  (100MB)
application-2024-01-01-1.log  (100MB)
application-2024-01-01-2.log  (50MB)   # 当前正在写入的文件
```

当文件大小达到100MB时，会自动创建下一个序号的文件。

## 日志级别说明

| 级别 | 说明 | 输出位置 |
|------|------|----------|
| DEBUG | 调试信息 | application.log |
| INFO | 一般信息 | application.log |
| WARN | 警告信息 | error.log |
| ERROR | 错误信息 | error.log |

## 环境配置

### 开发环境（dev）
- 控制台输出：是
- 日志级别：DEBUG
- 项目包日志级别：DEBUG

### 测试环境（test）
- 控制台输出：是
- 日志级别：INFO
- 项目包日志级别：INFO

### 生产环境（prod）
- 控制台输出：否（只输出到文件）
- 日志级别：INFO
- 项目包日志级别：INFO
- 第三方框架日志级别：WARN

## 异步输出

为了提高性能，日志采用异步输出方式：

- **普通日志异步队列大小**：512
- **错误日志异步队列大小**：256
- **不丢失日志**：队列满时不会丢弃日志

## 配置参数

在 `application.yml` 中可以配置日志目录：

```yaml
common:
  logDir: ${LOG_DIR:./logs}  # 日志目录，可通过环境变量LOG_DIR覆盖
```

## 日志格式

```
2024-01-01 10:00:00.123 [main] INFO  com.practise.demo.service.UserService - 用户创建成功
```

格式说明：
- `2024-01-01 10:00:00.123`：时间戳（精确到毫秒）
- `[main]`：线程名
- `INFO`：日志级别
- `com.practise.demo.service.UserService`：类名（最多36个字符）
- `用户创建成功`：日志消息

## 日志清理

### 自动清理
- 普通日志：保留30天后自动删除
- 错误日志：保留90天后自动删除
- 启动时自动清理过期日志

### 手动清理
可以手动删除 `logs` 目录下的过期日志文件。

## 使用示例

### 在代码中使用日志

```java
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Service
public class UserService {
    private static final Logger logger = LoggerFactory.getLogger(UserService.class);
    
    public void createUser(User user) {
        logger.debug("开始创建用户: {}", user.getUsername());
        logger.info("用户创建成功: {}", user.getId());
        logger.warn("用户名已存在: {}", user.getUsername());  // 会输出到error.log
        logger.error("创建用户失败", e);  // 会输出到error.log
    }
}
```

## 注意事项

1. **日志目录权限**：确保应用有权限在日志目录下创建和写入文件
2. **磁盘空间**：定期检查日志目录大小，避免磁盘空间不足
3. **日志级别**：生产环境建议使用INFO级别，避免产生过多DEBUG日志
4. **错误日志**：错误日志单独存储，便于快速定位问题
5. **日志格式**：统一使用UTF-8编码，避免中文乱码

## 故障排查

### 问题1：日志文件没有生成
- 检查日志目录权限
- 检查 `common.logDir` 配置是否正确
- 检查磁盘空间是否充足

### 问题2：日志文件过大
- 检查 `maxFileSize` 配置（当前100MB）
- 检查日志级别是否设置过低（如DEBUG）
- 检查是否有大量异常日志

### 问题3：日志文件没有切割
- 检查日期是否变化（按日期切割）
- 检查文件大小是否超过100MB（按大小切割）
- 检查应用是否正常重启（启动时会清理过期日志）

### 问题4：错误日志没有单独输出
- 检查日志级别是否为WARN或ERROR
- 检查 `FILE_ERROR` appender配置是否正确
- 检查过滤器配置是否正确

