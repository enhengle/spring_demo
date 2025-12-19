package com.practise.demo.utils;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * Redis String操作工具类
 * 支持单机、集群、多节点三种配置
 */
@Slf4j
@Component
public class RedisStringUtils {

    @Autowired
    @Qualifier("redisSingleTemplate")
    private RedisTemplate<String, Object> singleRedisTemplate;

//    @Autowired
//    @Qualifier("redisClusterTemplate")
//    private RedisTemplate<String, Object> clusterRedisTemplate;
//
//    @Autowired
//    @Qualifier("redisMultiNodeTemplate")
//    private RedisTemplate<String, Object> multiNodeRedisTemplate;

    // ==================== 单机Redis操作 ====================

    /**
     * 设置键值对
     */
    public void setSingle(String key, Object value) {
        try {
            singleRedisTemplate.opsForValue().set(key, value);
            log.debug("单机Redis设置成功 - key: {}, value: {}", key, value);
        } catch (Exception e) {
            log.error("单机Redis设置失败 - key: {}, value: {}", key, value, e);
            throw new RuntimeException("单机Redis设置失败", e);
        }
    }

    /**
     * 设置键值对（带过期时间）
     */
    public void setSingle(String key, Object value, long timeout, TimeUnit unit) {
        try {
            singleRedisTemplate.opsForValue().set(key, value, timeout, unit);
            log.debug("单机Redis设置成功 - key: {}, value: {}, timeout: {} {}", key, value, timeout, unit);
        } catch (Exception e) {
            log.error("单机Redis设置失败 - key: {}, value: {}, timeout: {} {}", key, value, timeout, unit, e);
            throw new RuntimeException("单机Redis设置失败", e);
        }
    }

    /**
     * 获取值
     */
    public Object getSingle(String key) {
        try {
            Object value = singleRedisTemplate.opsForValue().get(key);
            log.debug("单机Redis获取成功 - key: {}, value: {}", key, value);
            return value;
        } catch (Exception e) {
            log.error("单机Redis获取失败 - key: {}", key, e);
            throw new RuntimeException("单机Redis获取失败", e);
        }
    }

    /**
     * 删除键
     */
    public Boolean deleteSingle(String key) {
        try {
            Boolean result = singleRedisTemplate.delete(key);
            log.debug("单机Redis删除成功 - key: {}, result: {}", key, result);
            return result;
        } catch (Exception e) {
            log.error("单机Redis删除失败 - key: {}", key, e);
            throw new RuntimeException("单机Redis删除失败", e);
        }
    }

    /**
     * 批量设置
     */
    public void multiSetSingle(Map<String, Object> map) {
        try {
            singleRedisTemplate.opsForValue().multiSet(map);
            log.debug("单机Redis批量设置成功 - size: {}", map.size());
        } catch (Exception e) {
            log.error("单机Redis批量设置失败 - size: {}", map.size(), e);
            throw new RuntimeException("单机Redis批量设置失败", e);
        }
    }

    /**
     * 批量获取
     */
    public List<Object> multiGetSingle(Collection<String> keys) {
        try {
            List<Object> result = singleRedisTemplate.opsForValue().multiGet(keys);
            log.debug("单机Redis批量获取成功 - keys: {}, result: {}", keys, result);
            return result;
        } catch (Exception e) {
            log.error("单机Redis批量获取失败 - keys: {}", keys, e);
            throw new RuntimeException("单机Redis批量获取失败", e);
        }
    }

    /**
     * 设置过期时间
     */
    public Boolean expireSingle(String key, long timeout, TimeUnit unit) {
        try {
            Boolean result = singleRedisTemplate.expire(key, timeout, unit);
            log.debug("单机Redis设置过期时间成功 - key: {}, timeout: {} {}", key, timeout, unit);
            return result;
        } catch (Exception e) {
            log.error("单机Redis设置过期时间失败 - key: {}, timeout: {} {}", key, timeout, unit, e);
            throw new RuntimeException("单机Redis设置过期时间失败", e);
        }
    }

    /**
     * 获取过期时间
     */
    public Long getExpireSingle(String key, TimeUnit unit) {
        try {
            Long result = singleRedisTemplate.getExpire(key, unit);
            log.debug("单机Redis获取过期时间成功 - key: {}, expire: {}", key, result);
            return result;
        } catch (Exception e) {
            log.error("单机Redis获取过期时间失败 - key: {}", key, e);
            throw new RuntimeException("单机Redis获取过期时间失败", e);
        }
    }

    /**
     * 判断键是否存在
     */
    public Boolean hasKeySingle(String key) {
        try {
            Boolean result = singleRedisTemplate.hasKey(key);
            log.debug("单机Redis键存在检查成功 - key: {}, result: {}", key, result);
            return result;
        } catch (Exception e) {
            log.error("单机Redis键存在检查失败 - key: {}", key, e);
            throw new RuntimeException("单机Redis键存在检查失败", e);
        }
    }

    // ==================== 集群Redis操作 ====================

//    /**
//     * 设置键值对
//     */
//    public void setCluster(String key, Object value) {
//        try {
//            clusterRedisTemplate.opsForValue().set(key, value);
//            log.debug("集群Redis设置成功 - key: {}, value: {}", key, value);
//        } catch (Exception e) {
//            log.error("集群Redis设置失败 - key: {}, value: {}", key, value, e);
//            throw new RuntimeException("集群Redis设置失败", e);
//        }
//    }
//
//    /**
//     * 设置键值对（带过期时间）
//     */
//    public void setCluster(String key, Object value, long timeout, TimeUnit unit) {
//        try {
//            clusterRedisTemplate.opsForValue().set(key, value, timeout, unit);
//            log.debug("集群Redis设置成功 - key: {}, value: {}, timeout: {} {}", key, value, timeout, unit);
//        } catch (Exception e) {
//            log.error("集群Redis设置失败 - key: {}, value: {}, timeout: {} {}", key, value, timeout, unit, e);
//            throw new RuntimeException("集群Redis设置失败", e);
//        }
//    }
//
//    /**
//     * 获取值
//     */
//    public Object getCluster(String key) {
//        try {
//            Object value = clusterRedisTemplate.opsForValue().get(key);
//            log.debug("集群Redis获取成功 - key: {}, value: {}", key, value);
//            return value;
//        } catch (Exception e) {
//            log.error("集群Redis获取失败 - key: {}", key, e);
//            throw new RuntimeException("集群Redis获取失败", e);
//        }
//    }
//
//    /**
//     * 删除键
//     */
//    public Boolean deleteCluster(String key) {
//        try {
//            Boolean result = clusterRedisTemplate.delete(key);
//            log.debug("集群Redis删除成功 - key: {}, result: {}", key, result);
//            return result;
//        } catch (Exception e) {
//            log.error("集群Redis删除失败 - key: {}", key, e);
//            throw new RuntimeException("集群Redis删除失败", e);
//        }
//    }
//
//    /**
//     * 批量设置
//     */
//    public void multiSetCluster(Map<String, Object> map) {
//        try {
//            clusterRedisTemplate.opsForValue().multiSet(map);
//            log.debug("集群Redis批量设置成功 - size: {}", map.size());
//        } catch (Exception e) {
//            log.error("集群Redis批量设置失败 - size: {}", map.size(), e);
//            throw new RuntimeException("集群Redis批量设置失败", e);
//        }
//    }
//
//    /**
//     * 批量获取
//     */
//    public List<Object> multiGetCluster(Collection<String> keys) {
//        try {
//            List<Object> result = clusterRedisTemplate.opsForValue().multiGet(keys);
//            log.debug("集群Redis批量获取成功 - keys: {}, result: {}", keys, result);
//            return result;
//        } catch (Exception e) {
//            log.error("集群Redis批量获取失败 - keys: {}", keys, e);
//            throw new RuntimeException("集群Redis批量获取失败", e);
//        }
//    }
//
//    /**
//     * 设置过期时间
//     */
//    public Boolean expireCluster(String key, long timeout, TimeUnit unit) {
//        try {
//            Boolean result = clusterRedisTemplate.expire(key, timeout, unit);
//            log.debug("集群Redis设置过期时间成功 - key: {}, timeout: {} {}", key, timeout, unit);
//            return result;
//        } catch (Exception e) {
//            log.error("集群Redis设置过期时间失败 - key: {}, timeout: {} {}", key, timeout, unit, e);
//            throw new RuntimeException("集群Redis设置过期时间失败", e);
//        }
//    }
//
//    /**
//     * 获取过期时间
//     */
//    public Long getExpireCluster(String key, TimeUnit unit) {
//        try {
//            Long result = clusterRedisTemplate.getExpire(key, unit);
//            log.debug("集群Redis获取过期时间成功 - key: {}, expire: {}", key, result);
//            return result;
//        } catch (Exception e) {
//            log.error("集群Redis获取过期时间失败 - key: {}", key, e);
//            throw new RuntimeException("集群Redis获取过期时间失败", e);
//        }
//    }
//
//    /**
//     * 判断键是否存在
//     */
//    public Boolean hasKeyCluster(String key) {
//        try {
//            Boolean result = clusterRedisTemplate.hasKey(key);
//            log.debug("集群Redis键存在检查成功 - key: {}, result: {}", key, result);
//            return result;
//        } catch (Exception e) {
//            log.error("集群Redis键存在检查失败 - key: {}", key, e);
//            throw new RuntimeException("集群Redis键存在检查失败", e);
//        }
//    }
//
//    // ==================== 多节点Redis操作 ====================
//
//    /**
//     * 设置键值对
//     */
//    public void setMultiNode(String key, Object value) {
//        try {
//            multiNodeRedisTemplate.opsForValue().set(key, value);
//            log.debug("多节点Redis设置成功 - key: {}, value: {}", key, value);
//        } catch (Exception e) {
//            log.error("多节点Redis设置失败 - key: {}, value: {}", key, value, e);
//            throw new RuntimeException("多节点Redis设置失败", e);
//        }
//    }
//
//    /**
//     * 设置键值对（带过期时间）
//     */
//    public void setMultiNode(String key, Object value, long timeout, TimeUnit unit) {
//        try {
//            multiNodeRedisTemplate.opsForValue().set(key, value, timeout, unit);
//            log.debug("多节点Redis设置成功 - key: {}, value: {}, timeout: {} {}", key, value, timeout, unit);
//        } catch (Exception e) {
//            log.error("多节点Redis设置失败 - key: {}, value: {}, timeout: {} {}", key, value, timeout, unit, e);
//            throw new RuntimeException("多节点Redis设置失败", e);
//        }
//    }
//
//    /**
//     * 获取值
//     */
//    public Object getMultiNode(String key) {
//        try {
//            Object value = multiNodeRedisTemplate.opsForValue().get(key);
//            log.debug("多节点Redis获取成功 - key: {}, value: {}", key, value);
//            return value;
//        } catch (Exception e) {
//            log.error("多节点Redis获取失败 - key: {}", key, e);
//            throw new RuntimeException("多节点Redis获取失败", e);
//        }
//    }
//
//    /**
//     * 删除键
//     */
//    public Boolean deleteMultiNode(String key) {
//        try {
//            Boolean result = multiNodeRedisTemplate.delete(key);
//            log.debug("多节点Redis删除成功 - key: {}, result: {}", key, result);
//            return result;
//        } catch (Exception e) {
//            log.error("多节点Redis删除失败 - key: {}", key, e);
//            throw new RuntimeException("多节点Redis删除失败", e);
//        }
//    }
//
//    /**
//     * 批量设置
//     */
//    public void multiSetMultiNode(Map<String, Object> map) {
//        try {
//            multiNodeRedisTemplate.opsForValue().multiSet(map);
//            log.debug("多节点Redis批量设置成功 - size: {}", map.size());
//        } catch (Exception e) {
//            log.error("多节点Redis批量设置失败 - size: {}", map.size(), e);
//            throw new RuntimeException("多节点Redis批量设置失败", e);
//        }
//    }
//
//    /**
//     * 批量获取
//     */
//    public List<Object> multiGetMultiNode(Collection<String> keys) {
//        try {
//            List<Object> result = multiNodeRedisTemplate.opsForValue().multiGet(keys);
//            log.debug("多节点Redis批量获取成功 - keys: {}, result: {}", keys, result);
//            return result;
//        } catch (Exception e) {
//            log.error("多节点Redis批量获取失败 - keys: {}", keys, e);
//            throw new RuntimeException("多节点Redis批量获取失败", e);
//        }
//    }
//
//    /**
//     * 设置过期时间
//     */
//    public Boolean expireMultiNode(String key, long timeout, TimeUnit unit) {
//        try {
//            Boolean result = multiNodeRedisTemplate.expire(key, timeout, unit);
//            log.debug("多节点Redis设置过期时间成功 - key: {}, timeout: {} {}", key, timeout, unit);
//            return result;
//        } catch (Exception e) {
//            log.error("多节点Redis设置过期时间失败 - key: {}, timeout: {} {}", key, timeout, unit, e);
//            throw new RuntimeException("多节点Redis设置过期时间失败", e);
//        }
//    }
//
//    /**
//     * 获取过期时间
//     */
//    public Long getExpireMultiNode(String key, TimeUnit unit) {
//        try {
//            Long result = multiNodeRedisTemplate.getExpire(key, unit);
//            log.debug("多节点Redis获取过期时间成功 - key: {}, expire: {}", key, result);
//            return result;
//        } catch (Exception e) {
//            log.error("多节点Redis获取过期时间失败 - key: {}", key, e);
//            throw new RuntimeException("多节点Redis获取过期时间失败", e);
//        }
//    }
//
//    /**
//     * 判断键是否存在
//     */
//    public Boolean hasKeyMultiNode(String key) {
//        try {
//            Boolean result = multiNodeRedisTemplate.hasKey(key);
//            log.debug("多节点Redis键存在检查成功 - key: {}, result: {}", key, result);
//            return result;
//        } catch (Exception e) {
//            log.error("多节点Redis键存在检查失败 - key: {}", key, e);
//            throw new RuntimeException("多节点Redis键存在检查失败", e);
//        }
//    }
//
//    // ==================== 通用方法 ====================
//
//    /**
//     * 根据Redis类型获取对应的模板
//     */
//    public RedisTemplate<String, Object> getRedisTemplate(RedisType redisType) {
//        switch (redisType) {
//            case SINGLE:
//                return singleRedisTemplate;
//            case CLUSTER:
//                return clusterRedisTemplate;
//            case MULTI_NODE:
//                return multiNodeRedisTemplate;
//            default:
//                throw new IllegalArgumentException("不支持的Redis类型: " + redisType);
//        }
//    }
//
//    /**
//     * 通用设置方法
//     */
//    public void set(String key, Object value, RedisType redisType) {
//        switch (redisType) {
//            case SINGLE:
//                setSingle(key, value);
//                break;
//            case CLUSTER:
//                setCluster(key, value);
//                break;
//            case MULTI_NODE:
//                setMultiNode(key, value);
//                break;
//            default:
//                throw new IllegalArgumentException("不支持的Redis类型: " + redisType);
//        }
//    }
//
//    /**
//     * 通用获取方法
//     */
//    public Object get(String key, RedisType redisType) {
//        switch (redisType) {
//            case SINGLE:
//                return getSingle(key);
//            case CLUSTER:
//                return getCluster(key);
//            case MULTI_NODE:
//                return getMultiNode(key);
//            default:
//                throw new IllegalArgumentException("不支持的Redis类型: " + redisType);
//        }
//    }
//
//    /**
//     * Redis类型枚举
//     */
//    public enum RedisType {
//        SINGLE,     // 单机Redis
//        CLUSTER,    // 集群Redis
//        MULTI_NODE  // 多节点Redis
//    }
} 