### 一个springboot项目的新手demo

### 环境
```$xslt
环境：window11 + jdk8
开发软件：idea
```
### 功能说明
```$xslt
redis简单操作：string+list
```
# Redis工具类使用说明

## 概述

本项目提供了完整的Redis操作工具类，支持五种数据结构（String、List、Set、ZSet、Hash）和三种Redis配置（单机、集群、多节点/哨兵）。

## 工具类结构

### 1. 基础工具类
- `RedisStringUtils` - String操作工具类
- `RedisListUtils` - List操作工具类
- `RedisSetUtils` - Set操作工具类
- `RedisZSetUtils` - ZSet操作工具类
- `RedisHashUtils` - Hash操作工具类

### 2. 综合工具类
- `RedisUtils` - 统一接口工具类，整合所有操作

### 3. 配置类
- `RedisSingleConfig` - 单机Redis配置
- `RedisClusterConfig` - 集群Redis配置
- `RedisMultiNodeConfig` - 多节点/哨兵Redis配置
- `RedisConnectionConfig` - 连接配置增强类

### 4. 监控服务
- `RedisConnectionMonitorService` - 连接监控和重连服务

## Redis类型枚举

```java
public enum RedisType {
    SINGLE,     // 单机Redis
    CLUSTER,    // 集群Redis
    MULTI_NODE  // 多节点Redis（哨兵模式）
}
```

## 使用方法

### 1. 注入工具类

```java
@Autowired
private RedisUtils redisUtils;

@Autowired
private RedisStringUtils stringUtils;

@Autowired
private RedisListUtils listUtils;
```

### 2. String操作示例

```java
// 设置值
redisUtils.set("key", "value", RedisStringUtils.RedisType.SINGLE);

// 设置值并指定过期时间
redisUtils.set("key", "value", 60, TimeUnit.SECONDS, RedisStringUtils.RedisType.SINGLE);

// 获取值
Object value = redisUtils.get("key", RedisStringUtils.RedisType.SINGLE);

// 批量设置
Map<String, Object> batchMap = new HashMap<>();
batchMap.put("key1", "value1");
batchMap.put("key2", "value2");
redisUtils.multiSet(batchMap, RedisStringUtils.RedisType.SINGLE);

// 批量获取
List<Object> values = redisUtils.multiGet(Arrays.asList("key1", "key2"), RedisStringUtils.RedisType.SINGLE);

// 设置过期时间
redisUtils.expire("key", 300, TimeUnit.SECONDS, RedisStringUtils.RedisType.SINGLE);

// 检查键是否存在
Boolean exists = redisUtils.hasKey("key", RedisStringUtils.RedisType.SINGLE);
```

### 3. List操作示例

```java
// 左推入元素
redisUtils.lPush("listKey", new Object[]{"value1", "value2"}, RedisStringUtils.RedisType.SINGLE);

// 右推入元素
redisUtils.rPush("listKey", new Object[]{"value3", "value4"}, RedisStringUtils.RedisType.SINGLE);

// 左弹出元素
Object leftValue = redisUtils.lPop("listKey", RedisStringUtils.RedisType.SINGLE);

// 右弹出元素
Object rightValue = redisUtils.rPop("listKey", RedisStringUtils.RedisType.SINGLE);

// 获取列表长度
Long length = redisUtils.lLen("listKey", RedisStringUtils.RedisType.SINGLE);

// 获取指定范围的元素
List<Object> range = redisUtils.lRange("listKey", 0, -1, RedisStringUtils.RedisType.SINGLE);

// 根据索引获取元素
Object indexValue = redisUtils.lIndex("listKey", 0, RedisStringUtils.RedisType.SINGLE);

// 设置指定索引的元素
redisUtils.lSet("listKey", 0, "newValue", RedisStringUtils.RedisType.SINGLE);

// 删除指定值的元素
Long removed = redisUtils.lRem("listKey", 1, "value1", RedisStringUtils.RedisType.SINGLE);

// 修剪列表
redisUtils.lTrim("listKey", 0, 2, RedisStringUtils.RedisType.SINGLE);
```

### 4. Set操作示例

```java
// 添加元素
redisUtils.sAdd("setKey", new Object[]{"value1", "value2", "value3"}, RedisStringUtils.RedisType.SINGLE);

// 移除元素
Long removed = redisUtils.sRem("setKey", new Object[]{"value1"}, RedisStringUtils.RedisType.SINGLE);

// 获取所有元素
Set<Object> members = redisUtils.sMembers("setKey", RedisStringUtils.RedisType.SINGLE);

// 检查元素是否存在
Boolean exists = redisUtils.sIsMember("setKey", "value2", RedisStringUtils.RedisType.SINGLE);

// 获取集合大小
Long size = redisUtils.sCard("setKey", RedisStringUtils.RedisType.SINGLE);

// 随机获取元素
Object random = redisUtils.sRandMember("setKey", RedisStringUtils.RedisType.SINGLE);

// 随机获取多个元素
List<Object> randoms = redisUtils.sRandMembers("setKey", 2, RedisStringUtils.RedisType.SINGLE);

// 弹出元素
Object popped = redisUtils.sPop("setKey", RedisStringUtils.RedisType.SINGLE);

// 移动元素到另一个Set
Boolean moved = redisUtils.sMove("sourceKey", "destKey", "value", RedisStringUtils.RedisType.SINGLE);
```

### 5. ZSet操作示例

```java
// 添加元素
redisUtils.zAdd("zsetKey", "value1", 1.0, RedisStringUtils.RedisType.SINGLE);

// 批量添加元素
Set<ZSetOperations.TypedTuple<Object>> tuples = new HashSet<>();
tuples.add(new DefaultTypedTuple<>("value2", 2.0));
tuples.add(new DefaultTypedTuple<>("value3", 3.0));
// 注意：这里需要根据具体的ZSet工具类方法来实现

// 移除元素
Long removed = redisUtils.zRem("zsetKey", new Object[]{"value1"}, RedisStringUtils.RedisType.SINGLE);

// 获取集合大小
Long size = redisUtils.zCard("zsetKey", RedisStringUtils.RedisType.SINGLE);

// 获取元素分数
Double score = redisUtils.zScore("zsetKey", "value2", RedisStringUtils.RedisType.SINGLE);

// 获取元素排名（从小到大）
Long rank = redisUtils.zRank("zsetKey", "value2", RedisStringUtils.RedisType.SINGLE);

// 获取元素排名（从大到小）
Long revRank = redisUtils.zRevRank("zsetKey", "value2", RedisStringUtils.RedisType.SINGLE);

// 根据分数范围获取元素
Set<Object> scoreRange = redisUtils.zRangeByScore("zsetKey", 1.0, 3.0, RedisStringUtils.RedisType.SINGLE);

// 根据排名范围获取元素
Set<Object> rankRange = redisUtils.zRange("zsetKey", 0, -1, RedisStringUtils.RedisType.SINGLE);

// 增加元素分数
Double newScore = redisUtils.zIncrBy("zsetKey", "value1", 5.0, RedisStringUtils.RedisType.SINGLE);
```

### 6. Hash操作示例

```java
// 设置字段值
redisUtils.hSet("hashKey", "field1", "value1", RedisStringUtils.RedisType.SINGLE);

// 批量设置字段值
Map<String, Object> fieldMap = new HashMap<>();
fieldMap.put("field2", "value2");
fieldMap.put("field3", "value3");
redisUtils.hMSet("hashKey", fieldMap, RedisStringUtils.RedisType.SINGLE);

// 获取字段值
Object value = redisUtils.hGet("hashKey", "field1", RedisStringUtils.RedisType.SINGLE);

// 批量获取字段值
List<Object> values = redisUtils.hMGet("hashKey", Arrays.asList("field1", "field2"), RedisStringUtils.RedisType.SINGLE);

// 获取所有字段和值
Map<Object, Object> allFields = redisUtils.hGetAll("hashKey", RedisStringUtils.RedisType.SINGLE);

// 获取所有字段名
Set<Object> keys = redisUtils.hKeys("hashKey", RedisStringUtils.RedisType.SINGLE);

// 获取所有值
List<Object> allValues = redisUtils.hVals("hashKey", RedisStringUtils.RedisType.SINGLE);

// 获取Hash大小
Long size = redisUtils.hLen("hashKey", RedisStringUtils.RedisType.SINGLE);

// 检查字段是否存在
Boolean exists = redisUtils.hExists("hashKey", "field1", RedisStringUtils.RedisType.SINGLE);

// 删除字段
Long deleted = redisUtils.hDel("hashKey", new Object[]{"field1"}, RedisStringUtils.RedisType.SINGLE);

// 增加字段数值
Long newValue = redisUtils.hIncrBy("hashKey", "counter", 1, RedisStringUtils.RedisType.SINGLE);

// 增加字段浮点数值
Double newFloatValue = redisUtils.hIncrByFloat("hashKey", "floatCounter", 0.5, RedisStringUtils.RedisType.SINGLE);
```

## 配置说明

### 1. 配置文件

在 `application-redis.yml` 中配置Redis连接参数：

```yaml
redis:
  connection:
    auto-reconnect: true
    heartbeat-interval: 30
    max-retries: 3
    retry-delay: 1000
  
  single:
    host: localhost
    port: 6379
    password: 
    database: 0
  
  cluster:
    cluster-nodes: localhost:7001,localhost:7002,localhost:7003
    password: 
  
  sentinel:
    master-name: mymaster
    sentinel-nodes: localhost:26379,localhost:26380,localhost:26381
    master-password: 
    sentinel-password: 
```

### 2. 连接监控

```java
@Autowired
private RedisConnectionMonitorService monitorService;

// 获取连接状态报告
String report = monitorService.getRedisConnectionStatusReport();
log.info(report);

// 手动重连
boolean success = monitorService.reconnectSingleRedis();

// 检查连接健康状态
boolean isHealthy = monitorService.isAllRedisHealthy();
```

## 错误处理

所有工具类都包含完整的异常处理：

1. **异常捕获**: 捕获所有Redis操作异常
2. **日志记录**: 记录详细的操作日志和错误信息
3. **异常转换**: 将Redis异常转换为RuntimeException
4. **操作结果**: 返回操作结果或抛出异常

## 性能优化

1. **批量操作**: 支持批量设置、获取、删除等操作
2. **连接池**: 配置合适的连接池参数
3. **序列化**: 使用高效的序列化器
4. **异常处理**: 避免异常影响性能

## 注意事项

1. **键命名**: 使用有意义的键名前缀，避免键名冲突
2. **过期时间**: 合理设置过期时间，避免内存泄漏
3. **批量操作**: 大量数据操作时使用批量方法
4. **异常处理**: 在生产环境中妥善处理异常
5. **监控告警**: 配置连接监控和告警机制

## 总结

这套Redis工具类提供了完整的Redis操作功能，支持多种数据结构和部署模式，具有良好的错误处理、日志记录和性能优化特性，可以满足各种Redis使用场景的需求。 