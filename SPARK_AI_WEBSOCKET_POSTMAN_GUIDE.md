# 讯飞星火WebSocket实时流式接口 Postman测试文档

## 📋 概述

本文档提供了使用 Postman 测试讯飞星火WebSocket实时流式接口的详细说明。接口使用 **SSE (Server-Sent Events)** 协议，可以将WebSocket的流式数据转换为HTTP流式响应，Postman可以直接测试。

参考文档：
- [WebSocket协议文档](https://www.xfyun.cn/doc/spark/X1ws.html)
- [HTTP协议文档](https://www.xfyun.cn/doc/spark/X1http.html)

## 🔧 环境配置

### 1. 设置环境变量

在 Postman 中创建环境，设置以下变量：

| 变量名 | 初始值 | 说明 |
|--------|--------|------|
| `base_url` | `http://localhost:1234` | API基础地址 |

### 2. 配置说明

确保 `application.yml` 中配置了正确的WebSocket凭证：

```yaml
spark:
  ai:
    # WebSocket协议使用（必填）
    api-key: ${SPARK_AI_API_KEY:your-api-key}
    api-secret: ${SPARK_AI_API_SECRET:your-api-secret}
    app-id: ${SPARK_AI_APP_ID:your-app-id}
    # WebSocket API地址
    ws-url: wss://spark-api.xf-yun.com/v1/x1
```

**获取凭证地址：** https://console.xfyun.cn/services/bmx1

## 📡 API接口测试

### 1. 流式聊天接口（SSE方式）

**接口地址：** `POST {{base_url}}/api/spark-ai/ws/chat/stream`

**请求方式：** `POST`

**Content-Type：** `application/json`

**Accept：** `text/event-stream`（重要！）

**请求Body示例（JSON）：**

```json
[
  {
    "role": "user",
    "content": "请用Python写一个快速排序算法"
  }
]
```

**请求示例（cURL）：**

```bash
curl -X POST "http://localhost:1234/api/spark-ai/ws/chat/stream" \
  -H "Content-Type: application/json" \
  -H "Accept: text/event-stream" \
  -d '[
    {
      "role": "user",
      "content": "请用Python写一个快速排序算法"
    }
  ]'
```

**响应格式（SSE）：**

```
event: message
data: 以下是

event: message
data: Python实现的

event: message
data: 快速排序算法：

event: message
data: 

```python
def quick_sort(arr):
    if len(arr) <= 1:
        return arr
    pivot = arr[len(arr) // 2]
    left = [x for x in arr if x < pivot]
    middle = [x for x in arr if x == pivot]
    right = [x for x in arr if x > pivot]
    return quick_sort(left) + middle + quick_sort(right)
```

event: complete
data: 流式响应完成
```

**Postman设置步骤：**

1. 创建新的 POST 请求
2. URL: `{{base_url}}/api/spark-ai/ws/chat/stream`
3. Headers:
   - `Content-Type: application/json`
   - `Accept: text/event-stream`
4. Body (raw JSON):
   ```json
   [
     {
       "role": "user",
       "content": "你好，请介绍一下你自己"
     }
   ]
   ```
5. 点击 Send，Postman会实时显示流式响应

---

### 2. 简单流式聊天接口（SSE方式）

**接口地址：** `POST {{base_url}}/api/spark-ai/ws/chat/simple/stream`

**请求方式：** `POST`

**请求参数：**

| 参数名 | 类型 | 必填 | 说明 |
|--------|------|------|------|
| `message` | String | 是 | 用户消息 |
| `streamDelay` | Long | 否 | 返回间隔时间（毫秒），用于控制流式返回速度，0表示不延迟 |

**请求示例（cURL）：**

```bash
curl -X POST "http://localhost:1234/api/spark-ai/ws/chat/simple/stream?message=你好" \
  -H "Accept: text/event-stream"
```

**Postman设置步骤：**

1. 创建新的 POST 请求
2. URL: `{{base_url}}/api/spark-ai/ws/chat/simple/stream?message=你好`
3. Headers:
   - `Accept: text/event-stream`
4. 点击 Send

---

### 3. 非流式聊天接口（等待完整响应）

**接口地址：** `POST {{base_url}}/api/spark-ai/ws/chat`

**请求方式：** `POST`

**请求Body示例（JSON）：**

```json
[
  {
    "role": "user",
    "content": "推荐两个国内适合自驾的景点"
  }
]
```

**请求参数（Query Parameters）：**

| 参数名 | 类型 | 必填 | 说明 |
|--------|------|------|------|
| `temperature` | Double | 否 | 温度参数（0-1） |
| `maxTokens` | Integer | 否 | 最大token数 |
| `thinking` | String | 否 | 思考模式（X1.5新增） |

**请求示例（cURL）：**

```bash
curl -X POST "http://localhost:1234/api/spark-ai/ws/chat?temperature=0.7&maxTokens=2048" \
  -H "Content-Type: application/json" \
  -d '[
    {
      "role": "user",
      "content": "推荐两个国内适合自驾的景点"
    }
  ]'
```

**响应示例：**

```json
{
  "code": 200,
  "message": "success",
  "data": {
    "header": {
      "code": 0,
      "message": "success",
      "sid": "chatcmpl-xxx",
      "status": 2
    },
    "payload": {
      "choices": {
        "text": {
          "role": "assistant",
          "content": "推荐两个国内适合自驾的景点：\n\n1. **川藏线**：...",
          "index": 0
        },
        "status": 2
      },
      "usage": {
        "text": {
          "prompt_tokens": 15,
          "completion_tokens": 120,
          "total_tokens": 135
        }
      }
    }
  }
}
```

---

## 🧪 Postman测试步骤详解

### 测试流式接口（SSE）

1. **打开Postman**
2. **创建新请求**
   - 方法：`POST`
   - URL：`http://localhost:1234/api/spark-ai/ws/chat/stream`
3. **设置Headers**
   ```
   Content-Type: application/json
   Accept: text/event-stream
   ```
4. **设置Body**
   - 选择 `raw`
   - 选择 `JSON`
   - 输入：
   ```json
   [
     {
       "role": "user",
       "content": "写一首关于春天的诗"
     }
   ]
   ```
5. **点击Send**
   - Postman会实时显示流式响应
   - 每个数据块会以 `event: message` 和 `data: ...` 格式显示
   - 最后会收到 `event: complete` 表示完成

### 注意事项

1. **Accept头很重要**：必须设置为 `text/event-stream`，否则无法接收流式响应
2. **等待时间**：流式响应可能需要一些时间，请耐心等待
3. **连接超时**：如果60秒内没有响应，连接会自动关闭
4. **错误处理**：如果出现错误，会通过SSE发送错误信息

---

## 📦 Postman Collection 导入

### Collection JSON

```json
{
  "info": {
    "name": "讯飞星火WebSocket实时流式接口",
    "schema": "https://schema.getpostman.com/json/collection/v2.1.0/collection.json"
  },
  "item": [
    {
      "name": "流式聊天（SSE）",
      "request": {
        "method": "POST",
        "header": [
          {
            "key": "Content-Type",
            "value": "application/json"
          },
          {
            "key": "Accept",
            "value": "text/event-stream"
          }
        ],
        "body": {
          "mode": "raw",
          "raw": "[\n  {\n    \"role\": \"user\",\n    \"content\": \"你好，请介绍一下你自己\"\n  }\n]"
        },
        "url": {
          "raw": "{{base_url}}/api/spark-ai/ws/chat/stream",
          "host": ["{{base_url}}"],
          "path": ["api", "spark-ai", "ws", "chat", "stream"]
        }
      }
    },
    {
      "name": "简单流式聊天（SSE）",
      "request": {
        "method": "POST",
        "header": [
          {
            "key": "Accept",
            "value": "text/event-stream"
          }
        ],
        "url": {
          "raw": "{{base_url}}/api/spark-ai/ws/chat/simple/stream?message=你好",
          "host": ["{{base_url}}"],
          "path": ["api", "spark-ai", "ws", "chat", "simple", "stream"],
          "query": [
            {
              "key": "message",
              "value": "你好"
            }
          ]
        }
      }
    },
    {
      "name": "非流式聊天",
      "request": {
        "method": "POST",
        "header": [
          {
            "key": "Content-Type",
            "value": "application/json"
          }
        ],
        "body": {
          "mode": "raw",
          "raw": "[\n  {\n    \"role\": \"user\",\n    \"content\": \"推荐两个国内适合自驾的景点\"\n  }\n]"
        },
        "url": {
          "raw": "{{base_url}}/api/spark-ai/ws/chat",
          "host": ["{{base_url}}"],
          "path": ["api", "spark-ai", "ws", "chat"]
        }
      }
    }
  ]
}
```

**导入步骤：**

1. 打开 Postman
2. 点击 "Import" 按钮
3. 选择 "Raw text"
4. 粘贴上面的 JSON
5. 点击 "Import"

---

## 🧪 测试场景

### 场景1：实时流式响应测试

1. 调用流式聊天接口
2. 观察Postman中实时显示的数据块
3. 验证数据是否实时返回

### 场景2：多轮对话测试

```json
[
  {
    "role": "user",
    "content": "你好"
  },
  {
    "role": "assistant",
    "content": "你好！我是讯飞星火AI助手。"
  },
  {
    "role": "user",
    "content": "今天天气怎么样？"
  }
]
```

### 场景3：参数调优测试

使用不同的参数组合：
- `temperature=0.1`（更确定性）
- `temperature=0.9`（更随机性）
- `maxTokens=1024`（限制长度）
- `thinking=deep`（深度思考模式）

### 场景4：错误处理测试

1. 发送空消息，验证错误处理
2. 使用无效的API凭证，验证认证失败处理
3. 网络断开，验证连接恢复

---

## ⚠️ 注意事项

1. **API凭证配置**：确保在 `application.yml` 中配置了正确的WebSocket凭证（api-key, api-secret, app-id）
2. **网络连接**：确保服务器能够访问讯飞星火WebSocket API
3. **超时设置**：SSE连接默认60秒超时，可根据需要调整
4. **流式响应**：Postman会实时显示数据块，请耐心等待完整响应
5. **错误处理**：所有接口都有错误处理，错误信息会通过SSE发送

---

## 📚 参考资源

- [讯飞星火WebSocket文档](https://www.xfyun.cn/doc/spark/X1ws.html)
- [讯飞星火HTTP文档](https://www.xfyun.cn/doc/spark/X1http.html)
- [Server-Sent Events规范](https://developer.mozilla.org/zh-CN/docs/Web/API/Server-sent_events)
- [Postman使用指南](https://learning.postman.com/docs/)

---

**提示**：SSE接口可以直接在Postman中测试，无需额外的WebSocket客户端工具。数据会实时流式返回，非常适合测试大模型的实时响应功能。
