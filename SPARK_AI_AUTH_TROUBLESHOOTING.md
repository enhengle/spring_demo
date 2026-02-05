# 讯飞星火AI认证问题排查指南

## 🔴 401 Unauthorized 错误解决方案

### 问题描述

调用讯飞星火API时出现 `401 Unauthorized` 错误，表示认证失败。

### 常见原因及解决方案

#### 1. API凭证配置错误

**检查项：**
- 确认 `application.yml` 中配置了正确的API凭证
- 确认环境变量设置正确
- 确认从讯飞控制台复制的凭证完整无误

**配置示例：**

```yaml
spark:
  ai:
    api-key: your-api-key-here
    api-secret: your-api-secret-here
    app-id: your-app-id-here
```

**环境变量方式：**

```bash
export SPARK_AI_API_KEY=your-api-key-here
export SPARK_AI_API_SECRET=your-api-secret-here
export SPARK_AI_APP_ID=your-app-id-here
```

#### 2. 讯飞星火API使用WebSocket协议

**重要提示：** 讯飞星火API实际上使用 **WebSocket** 协议，而不是HTTP REST API。

当前代码实现的是HTTP方式的示例框架，实际使用时需要：

1. **使用WebSocket客户端**连接讯飞星火API
2. **在WebSocket握手阶段**进行认证
3. **通过WebSocket发送和接收消息**

**WebSocket连接示例：**

```java
// 需要添加WebSocket客户端依赖
// 例如：使用 Java-WebSocket 或 Spring WebSocket

String wsUrl = "wss://spark-api.xf-yun.com/v3.1/chat";
// 在URL中添加认证参数
```

#### 3. 签名生成问题

如果使用HTTP方式，签名生成需要注意：

1. **时间格式**：必须使用 RFC2822 格式，时区为 GMT
2. **签名内容**：`host: {host}\ndate: {date}\nGET {path} HTTP/1.1`
3. **签名算法**：HMAC-SHA256
4. **编码方式**：Base64

**调试签名生成：**

代码中已添加了详细的日志输出，查看日志可以看到：
- 签名原始字符串
- 生成的签名
- Authorization 头
- 完整的认证URL

#### 4. 请求头配置问题

确保请求头包含：
- `Content-Type: application/json`
- `Accept: application/json`
- `X-Appid: {appId}` (如果需要)
- `Authorization: {authorization}` (如果需要)

#### 5. 时间同步问题

服务器时间与讯飞服务器时间差异过大（通常>5分钟）会导致认证失败。

**解决方案：**
```bash
# Linux系统同步时间
sudo ntpdate -s time.nist.gov

# 或使用chrony
sudo chronyd -q
```

### 调试步骤

#### 步骤1：检查配置

```bash
# 查看当前配置
grep -A 10 "spark:" src/main/resources/application.yml

# 检查环境变量
echo $SPARK_AI_API_KEY
echo $SPARK_AI_API_SECRET
echo $SPARK_AI_APP_ID
```

#### 步骤2：查看详细日志

代码中已添加了详细的调试日志，启动应用时设置日志级别：

```yaml
logging:
  level:
    com.practise.demo.util.SparkAiUtil: DEBUG
```

#### 步骤3：验证API凭证

在讯飞控制台：
1. 登录讯飞开放平台
2. 进入控制台
3. 查看API凭证是否有效
4. 检查API调用权限是否开启

#### 步骤4：测试连接

使用Postman或curl测试：

```bash
# 注意：讯飞星火使用WebSocket，HTTP方式可能不适用
# 这里提供一个参考示例

curl -X POST "https://spark-api.xf-yun.com/v3.1/chat" \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer {your-token}" \
  -d '{"messages":[{"role":"user","content":"你好"}]}'
```

### 正确的实现方式

由于讯飞星火API使用WebSocket，建议：

#### 方案1：使用WebSocket客户端

添加依赖：

```xml
<dependency>
    <groupId>org.java-websocket</groupId>
    <artifactId>Java-WebSocket</artifactId>
    <version>1.5.3</version>
</dependency>
```

实现WebSocket客户端：

```java
import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;
import java.net.URI;

// 构建WebSocket URL（包含认证参数）
String wsUrl = buildWebSocketUrl();

WebSocketClient client = new WebSocketClient(new URI(wsUrl)) {
    @Override
    public void onOpen(ServerHandshake handshake) {
        // 连接成功
    }
    
    @Override
    public void onMessage(String message) {
        // 接收消息
    }
    
    @Override
    public void onClose(int code, String reason, boolean remote) {
        // 连接关闭
    }
    
    @Override
    public void onError(Exception ex) {
        // 错误处理
    }
};

client.connect();
```

#### 方案2：使用讯飞官方SDK

讯飞提供了官方的Java SDK，建议使用官方SDK：

1. 访问讯飞开放平台
2. 下载Java SDK
3. 按照官方文档集成

### 临时解决方案

如果暂时无法使用WebSocket，可以：

1. **使用HTTP代理服务**：通过中间服务转发请求
2. **使用讯飞提供的HTTP API**（如果有）
3. **联系讯飞技术支持**：确认是否有HTTP方式的API

### 联系支持

如果以上方法都无法解决问题：

1. **查看讯飞官方文档**：https://www.xfyun.cn/doc/spark/Web.html
2. **联系讯飞技术支持**：通过开放平台提交工单
3. **检查API版本**：确认使用的API版本是否支持当前认证方式

### 相关资源

- [讯飞星火API文档](https://www.xfyun.cn/doc/spark/Web.html)
- [WebSocket协议规范](https://tools.ietf.org/html/rfc6455)
- [HMAC-SHA256签名算法](https://tools.ietf.org/html/rfc2104)

---

**注意**：当前代码实现的是HTTP方式的示例框架。实际使用时，讯飞星火API需要WebSocket连接，请根据官方文档调整实现方式。
