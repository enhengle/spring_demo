# Kafka 实时流 → Spark Streaming → 实时指标计算 → Redis/ClickHouse Demo

## 📋 项目概述

这是一个完整的实时数据处理 demo，展示了如何使用 Kafka、Spark Streaming、Redis 和 ClickHouse 构建实时指标计算系统。

### 数据流程

```
Kafka 实时流 
  ↓
Spark Streaming (消费 Kafka 消息)
  ↓
实时指标计算 (总计数、总金额、平均金额、按维度分组统计)
  ↓
写入 Redis (用于实时查询)
  ↓
写入 ClickHouse (用于历史数据存储和分析)
```

## 🏗️ 架构组件

### 1. **Kafka**
- 消息队列，接收实时事件数据
- Topic: `realtime-metrics`

### 2. **Spark Streaming**
- 流处理引擎，消费 Kafka 消息
- 批次间隔：5秒
- 本地模式运行

### 3. **实时指标计算**
计算以下指标：
- **总计数** (`total_count`): 事件总数
- **总金额** (`total_amount`): 金额总和
- **平均金额** (`avg_amount`): 平均金额
- **按事件类型统计** (`event_type_count:*`): 按事件类型分组计数
- **按地区统计** (`region_count:*`): 按地区分组计数
- **按产品统计** (`product_amount:*`): 按产品ID分组金额总和

### 4. **Redis**
- 存储实时指标，用于快速查询
- Key 格式: `metric:{type}:{dimension}:{value}:{windowStart}`
- TTL: 1小时

### 5. **ClickHouse**
- 存储历史指标数据，用于分析和报表
- 表名: `realtime_metrics`
- 数据保留: 30天

## 🚀 快速开始

### 1. 前置条件

#### 安装 Kafka
```bash
# 下载 Kafka
wget https://downloads.apache.org/kafka/2.8.0/kafka_2.13-2.8.0.tgz
tar -xzf kafka_2.13-2.8.0.tgz
cd kafka_2.13-2.8.0

# 启动 Zookeeper
bin/zookeeper-server-start.sh config/zookeeper.properties

# 启动 Kafka
bin/kafka-server-start.sh config/server.properties

# 创建 Topic
bin/kafka-topics.sh --create --topic realtime-metrics --bootstrap-server localhost:9092 --partitions 1 --replication-factor 1
```

#### 安装 Redis
```bash
# 使用 Docker
docker run -d -p 6379:6379 redis:latest

# 或使用本地安装
redis-server
```

#### 安装 ClickHouse
```bash
# 使用 Docker
docker run -d -p 8123:8123 -p 9000:9000 --name clickhouse-server yandex/clickhouse-server

# 或使用本地安装
# 参考: https://clickhouse.tech/docs/en/getting-started/install/
```

### 2. 配置应用

编辑 `src/main/resources/application.yml`:

```yaml
# Kafka 配置
kafka:
  bootstrap-servers: localhost:9092
  topic: realtime-metrics

# Redis 配置
spring:
  redis:
    host: localhost
    port: 6379

# ClickHouse 配置
clickhouse:
  url: jdbc:clickhouse://localhost:8123/default
  username: default
  password: 
```

### 3. 启动应用

```bash
mvn clean install
mvn spring-boot:run
```

### 4. 使用 API

#### 启动流处理
```bash
curl http://localhost:1234/api/realtime/start
```

#### 发送测试数据
```bash
curl -X POST http://localhost:1234/api/realtime/send-test-data \
  -H "Content-Type: application/json" \
  -d '{"count": 100}'
```

#### 查看流处理状态
```bash
curl http://localhost:1234/api/realtime/status
```

#### 从 Redis 获取指标
```bash
# 获取所有指标
curl http://localhost:1234/api/realtime/metrics/redis?pattern=metric:*

# 获取特定指标
curl http://localhost:1234/api/realtime/metrics/redis/metric:count:total_count:1234567890
```

#### 停止流处理
```bash
curl http://localhost:1234/api/realtime/stop
```

## 📊 数据模型

### MetricEvent (Kafka 消息格式)
```json
{
  "userId": "U0001",
  "eventType": "purchase",
  "productId": "P001",
  "amount": 99.99,
  "timestamp": 1633024800000,
  "region": "beijing"
}
```

### RealtimeMetric (指标结果)
```json
{
  "metricKey": "total_count",
  "metricType": "count",
  "metricValue": 100.0,
  "windowStart": 1633024800000,
  "windowEnd": 1633024860000,
  "dimension": null,
  "dimensionValue": null
}
```

## 🔧 API 接口说明

### 1. 启动流处理
- **URL**: `GET /api/realtime/start`
- **说明**: 启动 Spark Streaming 流处理

### 2. 停止流处理
- **URL**: `GET /api/realtime/stop`
- **说明**: 停止 Spark Streaming 流处理

### 3. 获取流处理状态
- **URL**: `GET /api/realtime/status`
- **说明**: 查看流处理是否运行中

### 4. 发送测试数据
- **URL**: `POST /api/realtime/send-test-data`
- **Body**: `{"count": 10}`
- **说明**: 向 Kafka 发送指定数量的测试数据

### 5. 从 Redis 获取指标
- **URL**: `GET /api/realtime/metrics/redis?pattern=metric:*`
- **说明**: 获取所有匹配模式的指标

### 6. 获取特定指标
- **URL**: `GET /api/realtime/metrics/redis/{key}`
- **说明**: 获取指定键的指标值

## 📈 指标说明

### 指标类型

1. **count**: 计数类型
   - `total_count`: 总事件数
   - `event_type_count:{eventType}`: 按事件类型计数
   - `region_count:{region}`: 按地区计数

2. **sum**: 求和类型
   - `total_amount`: 总金额
   - `product_amount:{productId}`: 按产品金额总和

3. **avg**: 平均值类型
   - `avg_amount`: 平均金额

### Redis Key 格式

```
metric:{metricType}:{dimension}:{dimensionValue}:{windowStart}
```

示例:
- `metric:count::1633024800000` (总计数)
- `metric:count:region:beijing:1633024800000` (北京地区计数)
- `metric:sum:productId:P001:1633024800000` (产品P001金额总和)

## 🗄️ ClickHouse 表结构

```sql
CREATE TABLE realtime_metrics (
    metric_key String,
    metric_type String,
    metric_value Float64,
    window_start DateTime64(3),
    window_end DateTime64(3),
    dimension String,
    dimension_value String,
    create_time DateTime DEFAULT now()
) ENGINE = MergeTree()
ORDER BY (metric_key, window_start)
TTL create_time + INTERVAL 30 DAY
```

### 查询示例

```sql
-- 查询最近1小时的总计数
SELECT 
    metric_value,
    window_start
FROM realtime_metrics
WHERE metric_key = 'total_count'
  AND window_start >= now() - INTERVAL 1 HOUR
ORDER BY window_start DESC;

-- 按地区统计最近1小时的数据
SELECT 
    dimension_value as region,
    SUM(metric_value) as total_count
FROM realtime_metrics
WHERE metric_key LIKE 'region_count:%'
  AND window_start >= now() - INTERVAL 1 HOUR
GROUP BY dimension_value;
```

## ⚙️ 配置说明

### application.yml 配置项

```yaml
# Kafka 配置
kafka:
  bootstrap-servers: localhost:9092        # Kafka 服务器地址
  topic: realtime-metrics                  # Topic 名称
  consumer:
    group-id: spark-streaming-group        # 消费者组ID
    auto-offset-reset: earliest            # 偏移量重置策略

# Redis 配置
spring:
  redis:
    host: localhost                        # Redis 主机
    port: 6379                             # Redis 端口
    password:                              # Redis 密码（可选）
    database: 0                            # 数据库索引

# ClickHouse 配置
clickhouse:
  url: jdbc:clickhouse://localhost:8123/default
  username: default
  password: 
  database: default
  table: realtime_metrics

# Spark Streaming 配置
spark:
  streaming:
    batch-interval: 5s                     # 批次间隔
    checkpoint-dir: ./checkpoint            # 检查点目录
```

## 🔍 监控和调试

### 查看 Kafka 消息
```bash
# 消费消息
bin/kafka-console-consumer.sh --topic realtime-metrics --from-beginning --bootstrap-server localhost:9092
```

### 查看 Redis 数据
```bash
redis-cli
> KEYS metric:*
> GET metric:count::1633024800000
```

### 查看 ClickHouse 数据
```bash
clickhouse-client
> SELECT * FROM realtime_metrics LIMIT 10;
```

### 查看应用日志
应用日志会输出：
- 流处理启动/停止状态
- 每个批次处理的数据量
- 指标计算完成信息
- Redis/ClickHouse 写入状态

## ⚠️ 注意事项

1. **Kafka 连接**: 确保 Kafka 服务已启动并可访问
2. **Redis 连接**: 确保 Redis 服务已启动
3. **ClickHouse 连接**: 确保 ClickHouse 服务已启动
4. **资源管理**: Spark Streaming 会占用一定内存，注意监控
5. **检查点目录**: 确保 `checkpoint-dir` 目录有写权限
6. **批次间隔**: 根据数据量调整 `batch-interval`，太小会增加开销
7. **数据量**: 本地模式适合开发和测试，生产环境建议使用集群模式

## 🐛 常见问题

### 1. Kafka 连接失败
- 检查 Kafka 服务是否启动
- 检查 `bootstrap-servers` 配置是否正确
- 检查防火墙设置

### 2. Redis 连接失败
- 检查 Redis 服务是否启动
- 检查 Redis 配置是否正确
- 检查 Redis 密码（如果有）

### 3. ClickHouse 连接失败
- 检查 ClickHouse 服务是否启动
- 检查 JDBC URL 是否正确
- 检查网络连接

### 4. 流处理启动失败
- 检查 Kafka Topic 是否存在
- 检查检查点目录权限
- 查看应用日志获取详细错误信息

## 📚 扩展功能

### 1. 添加更多指标
在 `RealtimeStreamingService.calculateMetrics()` 方法中添加新的指标计算逻辑。

### 2. 使用窗口函数
可以使用 Spark Streaming 的窗口函数进行滑动窗口统计：
```java
JavaDStream<MetricEvent> windowedStream = events.window(
    Durations.minutes(5),  // 窗口长度
    Durations.minutes(1)   // 滑动间隔
);
```

### 3. 状态管理
使用 `updateStateByKey` 或 `mapWithState` 进行状态管理，实现累计统计。

### 4. 多数据源
可以同时消费多个 Kafka Topic，进行数据合并和关联。

## 📄 许可证

本项目仅供学习和参考使用。
