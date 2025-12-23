# 优雅停机指南

## 概述

优雅停机（Graceful Shutdown）是指在应用停止时，能够：
1. 停止接收新的请求
2. 等待正在处理的请求完成
3. 关闭线程池和连接
4. 避免数据丢失和任务中断

## 当前实现方案

### 方案一：Spring Boot 2.3+ 内置优雅停机（已实现）

Spring Boot 2.3+ 版本内置了优雅停机功能，通过配置 `server.shutdown=graceful` 即可启用。

**配置方式：**

```yaml
server:
  shutdown: graceful  # 启用优雅停机
spring:
  lifecycle:
    timeout-per-shutdown-phase: 30s  # 优雅停机等待时间，默认30秒
```

**工作原理：**
1. 接收到停机信号（SIGTERM）后，Spring Boot会：
   - 停止接收新的HTTP请求
   - 等待正在处理的请求完成（最多等待30秒）
   - 关闭应用上下文
   - 关闭线程池和连接

2. 如果30秒内请求未完成，会强制关闭

**优点：**
- 配置简单，开箱即用
- 自动处理HTTP请求和线程池
- 与Spring Boot深度集成

**缺点：**
- 等待时间固定，无法针对不同请求设置不同超时时间
- 对自定义线程池需要手动处理

## 测试方式

### 1. 启动应用

```bash
mvn spring-boot:run
# 或
java -jar target/demo-0.0.1-SNAPSHOT.jar
```

### 2. 测试长时间运行的请求

**测试步骤：**

1. **启动一个长时间运行的请求**（在另一个终端）：
```bash
# Windows PowerShell
curl http://localhost:1234/test/shutdown/long-running?seconds=20

# Linux/Mac
curl http://localhost:1234/test/shutdown/long-running?seconds=20
```

2. **在请求处理过程中停止应用**：
```bash
# 方式1：使用Ctrl+C（发送SIGINT信号）
# 方式2：使用kill命令（发送SIGTERM信号）
kill -15 <PID>  # Linux/Mac
# Windows下可以在任务管理器中结束进程
```

3. **观察日志**：
   - 应用会等待请求完成后再关闭
   - 如果超过30秒，会强制关闭

**预期结果：**
- 如果请求在30秒内完成，应用会正常关闭
- 如果请求超过30秒，应用会在30秒后强制关闭

### 3. 测试快速响应请求

```bash
curl http://localhost:1234/test/shutdown/quick
```

快速请求应该能正常响应，然后应用正常关闭。

### 4. 测试异步任务

```bash
curl http://localhost:1234/test/shutdown/async-task?seconds=10
```

### 5. 查看线程池状态

```bash
curl http://localhost:1234/test/shutdown/thread-pool-status
```

## 其他优雅停机方案

### 方案二：自定义Tomcat Connector优雅停机

**实现方式：**

```java
@Configuration
public class CustomGracefulShutdownConfig {
    
    @Bean
    public ServletWebServerFactory servletContainer(GracefulShutdown gracefulShutdown) {
        TomcatServletWebServerFactory factory = new TomcatServletWebServerFactory();
        factory.addConnectorCustomizers(gracefulShutdown);
        return factory;
    }
    
    @Bean
    public GracefulShutdown gracefulShutdown() {
        return new GracefulShutdown();
    }
    
    static class GracefulShutdown implements TomcatConnectorCustomizer, ApplicationListener<ContextClosedEvent> {
        private volatile Connector connector;
        
        @Override
        public void customize(Connector connector) {
            this.connector = connector;
        }
        
        @Override
        public void onApplicationEvent(ContextClosedEvent event) {
            if (connector != null) {
                connector.pause();
                Executor executor = connector.getProtocolHandler().getExecutor();
                if (executor instanceof ThreadPoolExecutor) {
                    ThreadPoolExecutor threadPoolExecutor = (ThreadPoolExecutor) executor;
                    threadPoolExecutor.shutdown();
                    try {
                        if (!threadPoolExecutor.awaitTermination(30, TimeUnit.SECONDS)) {
                            threadPoolExecutor.shutdownNow();
                        }
                    } catch (InterruptedException e) {
                        threadPoolExecutor.shutdownNow();
                    }
                }
            }
        }
    }
}
```

**优点：**
- 可以自定义关闭逻辑
- 可以针对不同连接器设置不同策略

**缺点：**
- 实现复杂
- 需要手动管理线程池

### 方案三：使用Spring Cloud Gateway的优雅停机

如果使用Spring Cloud Gateway，可以配置：

```yaml
spring:
  cloud:
    gateway:
      httpclient:
        connect-timeout: 1000
        response-timeout: 5s
```

### 方案四：使用Actuator的健康检查

结合Kubernetes的readiness和liveness探针：

```yaml
# application.yml
management:
  endpoints:
    web:
      exposure:
        include: health,info
  endpoint:
    health:
      probes:
        enabled: true
```

**K8s配置：**

```yaml
apiVersion: v1
kind: Pod
spec:
  containers:
  - name: app
    livenessProbe:
      httpGet:
        path: /actuator/health/liveness
        port: 8080
      initialDelaySeconds: 60
      periodSeconds: 10
    readinessProbe:
      httpGet:
        path: /actuator/health/readiness
        port: 8080
      initialDelaySeconds: 30
      periodSeconds: 5
    lifecycle:
      preStop:
        exec:
          command: ["/bin/sh", "-c", "sleep 10"]  # 给优雅停机留出时间
```

## K8s部署最佳实践

### 1. 配置preStop钩子

```yaml
apiVersion: apps/v1
kind: Deployment
metadata:
  name: spring-demo
spec:
  template:
    spec:
      containers:
      - name: app
        image: spring-demo:latest
        lifecycle:
          preStop:
            exec:
              command: ["/bin/sh", "-c", "sleep 15"]  # 等待15秒，给优雅停机留出时间
        terminationGracePeriodSeconds: 30  # Pod终止宽限期30秒
```

### 2. 配置readiness探针

```yaml
readinessProbe:
  httpGet:
    path: /actuator/health/readiness
    port: 8080
  initialDelaySeconds: 10
  periodSeconds: 5
  timeoutSeconds: 3
  failureThreshold: 3
```

### 3. 配置liveness探针

```yaml
livenessProbe:
  httpGet:
    path: /actuator/health/liveness
    port: 8080
  initialDelaySeconds: 60
  periodSeconds: 10
  timeoutSeconds: 3
  failureThreshold: 3
```

## 测试脚本

### Windows PowerShell测试脚本

```powershell
# test-graceful-shutdown.ps1

Write-Host "启动长时间运行的请求..."
Start-Job -ScriptBlock {
    curl http://localhost:1234/test/shutdown/long-running?seconds=20
} | Out-Null

Start-Sleep -Seconds 2

Write-Host "停止应用（模拟SIGTERM信号）..."
# 这里需要手动停止应用或使用其他方式发送停止信号
Write-Host "请手动停止应用（Ctrl+C）或使用任务管理器结束进程"
```

### Linux/Mac Bash测试脚本

```bash
#!/bin/bash
# test-graceful-shutdown.sh

echo "启动长时间运行的请求..."
curl http://localhost:1234/test/shutdown/long-running?seconds=20 &
CURL_PID=$!

sleep 2

echo "获取应用PID..."
APP_PID=$(pgrep -f "spring-demo")

if [ -z "$APP_PID" ]; then
    echo "未找到应用进程"
    exit 1
fi

echo "发送SIGTERM信号到应用 (PID: $APP_PID)..."
kill -15 $APP_PID

echo "等待应用关闭..."
wait $APP_PID

echo "测试完成"
```

## 监控和日志

### 查看优雅停机日志

应用关闭时会输出类似以下日志：

```
2024-01-01 10:00:00.000 INFO  [main] GracefulShutdownListener - 接收到应用关闭事件，开始执行优雅停机流程...
2024-01-01 10:00:00.100 INFO  [main] GracefulShutdownListener - 检查并关闭自定义线程池...
2024-01-01 10:00:00.200 INFO  [main] GracefulShutdownListener - 自定义线程池检查完成
2024-01-01 10:00:00.300 INFO  [main] GracefulShutdownListener - 优雅停机流程执行完成
```

### 验证优雅停机是否生效

1. **检查日志**：查看是否有优雅停机的相关日志
2. **检查请求**：长时间运行的请求是否在应用关闭前完成
3. **检查线程池**：线程池是否正常关闭

## 常见问题

### Q1: 优雅停机不生效？

**A:** 检查以下几点：
1. Spring Boot版本是否 >= 2.3.0
2. 是否配置了 `server.shutdown=graceful`
3. 是否通过SIGTERM信号停止（不是SIGKILL）

### Q2: 请求被强制中断？

**A:** 可能原因：
1. 等待时间设置太短，增加 `timeout-per-shutdown-phase` 的值
2. 使用了SIGKILL信号（-9），应该使用SIGTERM（-15）

### Q3: 自定义线程池没有关闭？

**A:** 需要在 `GracefulShutdownListener` 中添加自定义线程池的关闭逻辑。

## 总结

当前项目使用的是**Spring Boot 2.3+内置优雅停机方案**，这是最简单且推荐的方案。对于大多数场景，这个方案已经足够。

如果需要更细粒度的控制，可以考虑方案二（自定义Tomcat Connector优雅停机）。

在K8s环境中部署时，务必配置：
1. `preStop` 钩子
2. `terminationGracePeriodSeconds`
3. `readiness` 和 `liveness` 探针

