package com.practise.demo.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;

/**
 * Redis 工具类
 */
@Component
public class RedisUtil {

    private static final Logger logger = LoggerFactory.getLogger(RedisUtil.class);

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    /**
     * 设置键值对
     *
     * @param key   键
     * @param value 值
     */
    public void set(String key, Object value) {
        try {
            redisTemplate.opsForValue().set(key, value);
        } catch (Exception e) {
            logger.error("Redis 设置值失败: key={}", key, e);
            throw new RuntimeException("Redis 设置值失败", e);
        }
    }

    /**
     * 设置键值对（带过期时间）
     *
     * @param key      键
     * @param value    值
     * @param timeout  过期时间
     * @param timeUnit 时间单位
     */
    public void set(String key, Object value, long timeout, TimeUnit timeUnit) {
        try {
            redisTemplate.opsForValue().set(key, value, timeout, timeUnit);
        } catch (Exception e) {
            logger.error("Redis 设置值失败: key={}", key, e);
            throw new RuntimeException("Redis 设置值失败", e);
        }
    }

    /**
     * 获取值
     *
     * @param key 键
     * @return 值
     */
    public Object get(String key) {
        try {
            return redisTemplate.opsForValue().get(key);
        } catch (Exception e) {
            logger.error("Redis 获取值失败: key={}", key, e);
            return null;
        }
    }

    /**
     * 删除键
     *
     * @param key 键
     */
    public void delete(String key) {
        try {
            redisTemplate.delete(key);
        } catch (Exception e) {
            logger.error("Redis 删除键失败: key={}", key, e);
        }
    }

    /**
     * 批量设置
     *
     * @param metrics 指标列表
     */
    public void batchSet(List<com.practise.demo.model.RealtimeMetric> metrics) {
        try {
            for (com.practise.demo.model.RealtimeMetric metric : metrics) {
                String key = buildRedisKey(metric);
                String value = String.valueOf(metric.getMetricValue());
                redisTemplate.opsForValue().set(key, value, 1, TimeUnit.HOURS);
            }
            logger.debug("批量写入 Redis 成功: count={}", metrics.size());
        } catch (Exception e) {
            logger.error("批量写入 Redis 失败", e);
            throw new RuntimeException("批量写入 Redis 失败", e);
        }
    }

    /**
     * 构建 Redis 键
     *
     * @param metric 指标
     * @return Redis 键
     */
    private String buildRedisKey(com.practise.demo.model.RealtimeMetric metric) {
        StringBuilder key = new StringBuilder("metric:");
        key.append(metric.getMetricType()).append(":");
        if (metric.getDimension() != null && metric.getDimensionValue() != null) {
            key.append(metric.getDimension()).append(":").append(metric.getDimensionValue()).append(":");
        }
        key.append(metric.getWindowStart());
        return key.toString();
    }

    /**
     * 获取所有匹配的键
     *
     * @param pattern 模式
     * @return 键集合
     */
    public Set<String> keys(String pattern) {
        try {
            return redisTemplate.keys(pattern);
        } catch (Exception e) {
            logger.error("Redis 获取键失败: pattern={}", pattern, e);
            return null;
        }
    }
}
