# 讯飞星火AI Postman测试文档

## 📋 概述

本文档提供了使用 Postman 测试讯飞星火AI接口的详细说明，包括所有接口的请求示例和响应示例。

**注意**：当前实现使用HTTP协议调用讯飞星火API，使用Bearer Token认证方式。WebSocket实时流式接口请参考 `SPARK_AI_WEBSOCKET_POSTMAN_GUIDE.md`。

## 🔧 环境配置

### 1. 设置环境变量

在 Postman 中创建环境，设置以下变量：

| 变量名 | 初始值 | 说明 |
|--------|--------|------|
| `base_url` | `http://localhost:1234` | API基础地址 |
| `api_key` | `your-api-key` | 讯飞星火API Key |
| `api_secret` | `your-api-secret` | 讯飞星火API Secret |
| `app_id` | `your-app-id` | 讯飞星火App ID |

### 2. 配置说明

确保 `application.yml` 中配置了正确的讯飞星火API凭证：

```yaml
spark:
  ai:
    api-key: ${SPARK_AI_API_KEY:your-api-key}
    api-secret: ${SPARK_AI_API_SECRET:your-api-secret}
    app-id: ${SPARK_AI_APP_ID:your-app-id}
```

## 📡 API接口测试

### 1. 简单对话接口

**接口地址：** `POST {{base_url}}/api/spark-ai/chat/simple`

**请求方式：** `POST`

**请求参数：**

| 参数名 | 类型 | 必填 | 说明 |
|--------|------|------|------|
| `message` | String | 是 | 用户消息 |

**请求示例（Form-data）：**

```
message: 你好，请介绍一下你自己
```

**请求示例（cURL）：**

```bash
curl --location 'http://localhost:1234/api/spark-ai/chat/simple' \
--form 'message="你好，请介绍一下你自己"'
```

**响应示例：**

```json
{
  "code": 200,
  "message": "success",
  "data": "你好！我是讯飞星火AI助手，由科大讯飞开发。我可以帮助你解答问题、提供信息、协助写作等多种任务。有什么我可以帮助你的吗？"
}
```

---

### 2. 多轮对话接口

**接口地址：** `POST {{base_url}}/api/spark-ai/chat/multi-turn`

**请求方式：** `POST`

**请求参数：**

| 参数名 | 类型 | 必填 | 说明 |
|--------|------|------|------|
| `message` | String | 是 | 当前用户消息 |
| Body | JSON Array | 是 | 对话历史（Message对象数组） |

**请求Body示例（JSON）：**

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

**请求示例（cURL）：**

```bash
curl --location 'http://localhost:1234/api/spark-ai/chat/multi-turn?message=今天天气怎么样？' \
--header 'Content-Type: application/json' \
--data '[
    {
        "role": "user",
        "content": "你好"
    },
    {
        "role": "assistant",
        "content": "你好！我是讯飞星火AI助手。"
    }
]'
```

**响应示例：**

```json
{
  "code": 200,
  "message": "success",
  "data": "抱歉，我无法获取实时天气信息。建议您查看天气预报应用或网站获取准确的天气信息。"
}
```

---

### 3. 完整对话接口

**接口地址：** `POST {{base_url}}/api/spark-ai/chat`

**请求方式：** `POST`

**请求参数：**

| 参数名 | 类型 | 必填 | 说明 |
|--------|------|------|------|
| Body | JSON Array | 是 | 消息列表（Message对象数组） |

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
curl --location 'http://localhost:1234/api/spark-ai/chat' \
--header 'Content-Type: application/json' \
--data '[
    {
        "role": "user",
        "content": "请用Python写一个快速排序算法"
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
        "text": [
          {
            "role": "assistant",
            "content": "以下是Python实现的快速排序算法：\n\n```python\ndef quick_sort(arr):\n    if len(arr) <= 1:\n        return arr\n    pivot = arr[len(arr) // 2]\n    left = [x for x in arr if x < pivot]\n    middle = [x for x in arr if x == pivot]\n    right = [x for x in arr if x > pivot]\n    return quick_sort(left) + middle + quick_sort(right)\n\n# 示例\narr = [3, 6, 8, 10, 1, 2, 1]\nprint(quick_sort(arr))\n```",
            "index": 0
          }
        ]
      },
      "usage": {
        "promptTokens": 15,
        "completionTokens": 120,
        "totalTokens": 135
      }
    }
  }
}
```

---

### 4. 高级对话接口（带参数）

**接口地址：** `POST {{base_url}}/api/spark-ai/chat/advanced`

**请求方式：** `POST`

**请求参数：**

| 参数名 | 类型 | 必填 | 说明 |
|--------|------|------|------|
| `temperature` | Double | 否 | 温度参数（0-1），默认0.5 |
| `maxTokens` | Integer | 否 | 最大token数，默认2048 |
| `model` | String | 否 | 模型版本，默认generalv3 |
| Body | JSON Array | 是 | 消息列表（Message对象数组） |

**请求Body示例（JSON）：**

```json
[
  {
    "role": "user",
    "content": "写一首关于春天的诗"
  }
]
```

**请求示例（cURL）：**

```bash
curl --location 'http://localhost:1234/api/spark-ai/chat/advanced?temperature=0.8&maxTokens=500&model=generalv3' \
--header 'Content-Type: application/json' \
--data '[
    {
        "role": "user",
        "content": "写一首关于春天的诗"
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
        "text": [
          {
            "role": "assistant",
            "content": "春风轻拂绿柳梢，\n花开满树映朝霞。\n鸟语花香处处闻，\n万物复苏展新颜。",
            "index": 0
          }
        ]
      },
      "usage": {
        "promptTokens": 12,
        "completionTokens": 45,
        "totalTokens": 57
      }
    }
  }
}
```

---

### 5. 创建对话历史接口

**接口地址：** `POST {{base_url}}/api/spark-ai/conversation/create`

**请求方式：** `POST`

**请求参数：**

| 参数名 | 类型 | 必填 | 说明 |
|--------|------|------|------|
| `userMessages` | Array | 是 | 用户消息列表 |
| `assistantMessages` | Array | 是 | 助手消息列表 |

**请求示例（Form-data）：**

```
userMessages: ["你好", "今天天气怎么样？"]
assistantMessages: ["你好！", "抱歉，我无法获取实时天气信息。"]
```

**请求示例（cURL）：**

```bash
curl --location 'http://localhost:1234/api/spark-ai/conversation/create' \
--form 'userMessages[]="你好"' \
--form 'userMessages[]="今天天气怎么样？"' \
--form 'assistantMessages[]="你好！"' \
--form 'assistantMessages[]="抱歉，我无法获取实时天气信息。"'
```

**响应示例：**

```json
{
  "code": 200,
  "message": "success",
  "data": [
    {
      "role": "user",
      "content": "你好"
    },
    {
      "role": "assistant",
      "content": "你好！"
    },
    {
      "role": "user",
      "content": "今天天气怎么样？"
    },
    {
      "role": "assistant",
      "content": "抱歉，我无法获取实时天气信息。"
    }
  ]
}
```

---

## 📦 Postman Collection 导入

### 方式一：手动创建Collection

1. 在 Postman 中创建新的 Collection，命名为 "讯飞星火AI"
2. 按照上述接口说明，逐个创建请求
3. 设置环境变量

### 方式二：使用cURL导入

可以将上述cURL命令直接导入到 Postman：

1. 打开 Postman
2. 点击 "Import" 按钮
3. 选择 "Raw text"
4. 粘贴cURL命令
5. Postman会自动解析并创建请求

### 方式三：使用Collection JSON

创建以下JSON文件并导入到Postman：

```json
{
  "info": {
    "name": "讯飞星火AI",
    "schema": "https://schema.getpostman.com/json/collection/v2.1.0/collection.json"
  },
  "item": [
    {
      "name": "简单对话",
      "request": {
        "method": "POST",
        "header": [],
        "body": {
          "mode": "formdata",
          "formdata": [
            {
              "key": "message",
              "value": "你好，请介绍一下你自己",
              "type": "text"
            }
          ]
        },
        "url": {
          "raw": "{{base_url}}/api/spark-ai/chat/simple",
          "host": ["{{base_url}}"],
          "path": ["api", "spark-ai", "chat", "simple"]
        }
      }
    },
    {
      "name": "完整对话",
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
          "raw": "[\n  {\n    \"role\": \"user\",\n    \"content\": \"请用Python写一个快速排序算法\"\n  }\n]"
        },
        "url": {
          "raw": "{{base_url}}/api/spark-ai/chat",
          "host": ["{{base_url}}"],
          "path": ["api", "spark-ai", "chat"]
        }
      }
    }
  ]
}
```

## 🧪 测试场景

### 场景1：单轮对话测试

1. 调用简单对话接口
2. 发送消息："你好"
3. 验证返回的AI回复

### 场景2：多轮对话测试

1. 创建对话历史
2. 调用多轮对话接口
3. 验证AI能够理解上下文

### 场景3：参数调优测试

1. 使用不同的temperature值（0.1, 0.5, 0.9）
2. 观察生成内容的随机性变化
3. 调整maxTokens参数，观察响应长度

### 场景4：错误处理测试

1. 发送空消息，验证错误处理
2. 发送超长消息，验证token限制
3. 使用无效的API凭证，验证认证失败处理

## ⚠️ 注意事项

1. **API凭证配置**：确保在 `application.yml` 中配置了正确的API凭证
2. **网络连接**：确保服务器能够访问讯飞星火API
3. **请求频率**：注意API的调用频率限制
4. **Token限制**：注意maxTokens参数，避免超出模型限制
5. **错误处理**：所有接口都有错误处理，返回的错误信息会包含在响应中

## 📚 参考文档

- [讯飞星火API官方文档](https://www.xfyun.cn/doc/spark/Web.html)
- [Spring Boot官方文档](https://spring.io/projects/spring-boot)
- [Postman使用指南](https://learning.postman.com/docs/)

---

**注意**：本文档中的API地址和参数仅供参考，实际使用时请根据讯飞星火官方API文档进行调整。
