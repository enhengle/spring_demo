package com.practise.demo.utils;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.util.*;

/**
 * Redis Hash操作工具类
 * 支持单机、集群、多节点三种配置
 */
@Slf4j
@Component
public class RedisHashUtils {

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
     * 设置Hash字段值
     */
    public void hSetSingle(String key, String field, Object value) {
        try {
            singleRedisTemplate.opsForHash().put(key, field, value);
            log.debug("单机Redis Hash设置成功 - key: {}, field: {}, value: {}", key, field, value);
        } catch (Exception e) {
            log.error("单机Redis Hash设置失败 - key: {}, field: {}, value: {}", key, field, value, e);
            throw new RuntimeException("单机Redis Hash设置失败", e);
        }
    }

    /**
     * 批量设置Hash字段值
     */
    public void hMSetSingle(String key, Map<String, Object> map) {
        try {
            singleRedisTemplate.opsForHash().putAll(key, map);
            log.debug("单机Redis Hash批量设置成功 - key: {}, map: {}", key, map);
        } catch (Exception e) {
            log.error("单机Redis Hash批量设置失败 - key: {}, map: {}", key, map, e);
            throw new RuntimeException("单机Redis Hash批量设置失败", e);
        }
    }

    /**
     * 获取Hash字段值
     */
    public Object hGetSingle(String key, String field) {
        try {
            Object result = singleRedisTemplate.opsForHash().get(key, field);
            log.debug("单机Redis Hash获取成功 - key: {}, field: {}, result: {}", key, field, result);
            return result;
        } catch (Exception e) {
            log.error("单机Redis Hash获取失败 - key: {}, field: {}", key, field, e);
            throw new RuntimeException("单机Redis Hash获取失败", e);
        }
    }

    /**
     * 批量获取Hash字段值
     */
    public List<Object> hMGetSingle(String key, Collection<String> fields) {
        try {
            List<Object> result = singleRedisTemplate.opsForHash().multiGet(key, Collections.singleton(fields));
            log.debug("单机Redis Hash批量获取成功 - key: {}, fields: {}, result: {}", key, fields, result);
            return result;
        } catch (Exception e) {
            log.error("单机Redis Hash批量获取失败 - key: {}, fields: {}", key, fields, e);
            throw new RuntimeException("单机Redis Hash批量获取失败", e);
        }
    }

    /**
     * 获取Hash的所有字段和值
     */
    public Map<Object, Object> hGetAllSingle(String key) {
        try {
            Map<Object, Object> result = singleRedisTemplate.opsForHash().entries(key);
            log.debug("单机Redis Hash获取所有字段成功 - key: {}, result: {}", key, result);
            return result;
        } catch (Exception e) {
            log.error("单机Redis Hash获取所有字段失败 - key: {}", key, e);
            throw new RuntimeException("单机Redis Hash获取所有字段失败", e);
        }
    }

    /**
     * 获取Hash的所有字段名
     */
    public Set<Object> hKeysSingle(String key) {
        try {
            Set<Object> result = singleRedisTemplate.opsForHash().keys(key);
            log.debug("单机Redis Hash获取所有字段名成功 - key: {}, result: {}", key, result);
            return result;
        } catch (Exception e) {
            log.error("单机Redis Hash获取所有字段名失败 - key: {}", key, e);
            throw new RuntimeException("单机Redis Hash获取所有字段名失败", e);
        }
    }

    /**
     * 获取Hash的所有值
     */
    public List<Object> hValsSingle(String key) {
        try {
            List<Object> result = singleRedisTemplate.opsForHash().values(key);
            log.debug("单机Redis Hash获取所有值成功 - key: {}, result: {}", key, result);
            return result;
        } catch (Exception e) {
            log.error("单机Redis Hash获取所有值失败 - key: {}", key, e);
            throw new RuntimeException("单机Redis Hash获取所有值失败", e);
        }
    }

    /**
     * 获取Hash的大小
     */
    public Long hLenSingle(String key) {
        try {
            Long result = singleRedisTemplate.opsForHash().size(key);
            log.debug("单机Redis Hash获取大小成功 - key: {}, result: {}", key, result);
            return result;
        } catch (Exception e) {
            log.error("单机Redis Hash获取大小失败 - key: {}", key, e);
            throw new RuntimeException("单机Redis Hash获取大小失败", e);
        }
    }

    /**
     * 判断Hash字段是否存在
     */
    public Boolean hExistsSingle(String key, String field) {
        try {
            Boolean result = singleRedisTemplate.opsForHash().hasKey(key, field);
            log.debug("单机Redis Hash字段存在检查成功 - key: {}, field: {}, result: {}", key, field, result);
            return result;
        } catch (Exception e) {
            log.error("单机Redis Hash字段存在检查失败 - key: {}, field: {}", key, field, e);
            throw new RuntimeException("单机Redis Hash字段存在检查失败", e);
        }
    }

    /**
     * 删除Hash字段
     */
    public Long hDelSingle(String key, Object... fields) {
        try {
            Long result = singleRedisTemplate.opsForHash().delete(key, fields);
            log.debug("单机Redis Hash删除字段成功 - key: {}, fields: {}, result: {}", key, fields, result);
            return result;
        } catch (Exception e) {
            log.error("单机Redis Hash删除字段失败 - key: {}, fields: {}", key, fields, e);
            throw new RuntimeException("单机Redis Hash删除字段失败", e);
        }
    }

    /**
     * 增加Hash字段的数值
     */
    public Long hIncrBySingle(String key, String field, long delta) {
        try {
            Long result = singleRedisTemplate.opsForHash().increment(key, field, delta);
            log.debug("单机Redis Hash增加数值成功 - key: {}, field: {}, delta: {}, result: {}", key, field, delta, result);
            return result;
        } catch (Exception e) {
            log.error("单机Redis Hash增加数值失败 - key: {}, field: {}, delta: {}", key, field, delta, e);
            throw new RuntimeException("单机Redis Hash增加数值失败", e);
        }
    }

    /**
     * 增加Hash字段的浮点数值
     */
    public Double hIncrByFloatSingle(String key, String field, double delta) {
        try {
            Double result = singleRedisTemplate.opsForHash().increment(key, field, delta);
            log.debug("单机Redis Hash增加浮点数值成功 - key: {}, field: {}, delta: {}, result: {}", key, field, delta, result);
            return result;
        } catch (Exception e) {
            log.error("单机Redis Hash增加浮点数值失败 - key: {}, field: {}, delta: {}", key, field, delta, e);
            throw new RuntimeException("单机Redis Hash增加浮点数值失败", e);
        }
    }

    // ==================== 集群Redis操作 ====================
//
//    /**
//     * 设置Hash字段值
//     */
//    public void hSetCluster(String key, String field, Object value) {
//        try {
//            clusterRedisTemplate.opsForHash().put(key, field, value);
//            log.debug("集群Redis Hash设置成功 - key: {}, field: {}, value: {}", key, field, value);
//        } catch (Exception e) {
//            log.error("集群Redis Hash设置失败 - key: {}, field: {}, value: {}", key, field, value, e);
//            throw new RuntimeException("集群Redis Hash设置失败", e);
//        }
//    }
//
//    /**
//     * 批量设置Hash字段值
//     */
//    public void hMSetCluster(String key, Map<String, Object> map) {
//        try {
//            clusterRedisTemplate.opsForHash().putAll(key, map);
//            log.debug("集群Redis Hash批量设置成功 - key: {}, map: {}", key, map);
//        } catch (Exception e) {
//            log.error("集群Redis Hash批量设置失败 - key: {}, map: {}", key, map, e);
//            throw new RuntimeException("集群Redis Hash批量设置失败", e);
//        }
//    }
//
//    /**
//     * 获取Hash字段值
//     */
//    public Object hGetCluster(String key, String field) {
//        try {
//            Object result = clusterRedisTemplate.opsForHash().get(key, field);
//            log.debug("集群Redis Hash获取成功 - key: {}, field: {}, result: {}", key, field, result);
//            return result;
//        } catch (Exception e) {
//            log.error("集群Redis Hash获取失败 - key: {}, field: {}", key, field, e);
//            throw new RuntimeException("集群Redis Hash获取失败", e);
//        }
//    }
//
//    /**
//     * 批量获取Hash字段值
//     */
//    public List<Object> hMGetCluster(String key, Collection<String> fields) {
//        try {
//            List<Object> result = clusterRedisTemplate.opsForHash().multiGet(key, fields);
//            log.debug("集群Redis Hash批量获取成功 - key: {}, fields: {}, result: {}", key, fields, result);
//            return result;
//        } catch (Exception e) {
//            log.error("集群Redis Hash批量获取失败 - key: {}, fields: {}", key, fields, e);
//            throw new RuntimeException("集群Redis Hash批量获取失败", e);
//        }
//    }
//
//    /**
//     * 获取Hash的所有字段和值
//     */
//    public Map<Object, Object> hGetAllCluster(String key) {
//        try {
//            Map<Object, Object> result = clusterRedisTemplate.opsForHash().entries(key);
//            log.debug("集群Redis Hash获取所有字段成功 - key: {}, result: {}", key, result);
//            return result;
//        } catch (Exception e) {
//            log.error("集群Redis Hash获取所有字段失败 - key: {}", key, e);
//            throw new RuntimeException("集群Redis Hash获取所有字段失败", e);
//        }
//    }
//
//    /**
//     * 获取Hash的所有字段名
//     */
//    public Set<Object> hKeysCluster(String key) {
//        try {
//            Set<Object> result = clusterRedisTemplate.opsForHash().keys(key);
//            log.debug("集群Redis Hash获取所有字段名成功 - key: {}, result: {}", key, result);
//            return result;
//        } catch (Exception e) {
//            log.error("集群Redis Hash获取所有字段名失败 - key: {}", key, e);
//            throw new RuntimeException("集群Redis Hash获取所有字段名失败", e);
//        }
//    }
//
//    /**
//     * 获取Hash的所有值
//     */
//    public List<Object> hValsCluster(String key) {
//        try {
//            List<Object> result = clusterRedisTemplate.opsForHash().values(key);
//            log.debug("集群Redis Hash获取所有值成功 - key: {}, result: {}", key, result);
//            return result;
//        } catch (Exception e) {
//            log.error("集群Redis Hash获取所有值失败 - key: {}", key, e);
//            throw new RuntimeException("集群Redis Hash获取所有值失败", e);
//        }
//    }
//
//    /**
//     * 获取Hash的大小
//     */
//    public Long hLenCluster(String key) {
//        try {
//            Long result = clusterRedisTemplate.opsForHash().size(key);
//            log.debug("集群Redis Hash获取大小成功 - key: {}, result: {}", key, result);
//            return result;
//        } catch (Exception e) {
//            log.error("集群Redis Hash获取大小失败 - key: {}", key, e);
//            throw new RuntimeException("集群Redis Hash获取大小失败", e);
//        }
//    }
//
//    /**
//     * 判断Hash字段是否存在
//     */
//    public Boolean hExistsCluster(String key, String field) {
//        try {
//            Boolean result = clusterRedisTemplate.opsForHash().hasKey(key, field);
//            log.debug("集群Redis Hash字段存在检查成功 - key: {}, field: {}, result: {}", key, field, result);
//            return result;
//        } catch (Exception e) {
//            log.error("集群Redis Hash字段存在检查失败 - key: {}, field: {}", key, field, e);
//            throw new RuntimeException("集群Redis Hash字段存在检查失败", e);
//        }
//    }
//
//    /**
//     * 删除Hash字段
//     */
//    public Long hDelCluster(String key, Object... fields) {
//        try {
//            Long result = clusterRedisTemplate.opsForHash().delete(key, fields);
//            log.debug("集群Redis Hash删除字段成功 - key: {}, fields: {}, result: {}", key, fields, result);
//            return result;
//        } catch (Exception e) {
//            log.error("集群Redis Hash删除字段失败 - key: {}, fields: {}", key, fields, e);
//            throw new RuntimeException("集群Redis Hash删除字段失败", e);
//        }
//    }
//
//    /**
//     * 增加Hash字段的数值
//     */
//    public Long hIncrByCluster(String key, String field, long delta) {
//        try {
//            Long result = clusterRedisTemplate.opsForHash().increment(key, field, delta);
//            log.debug("集群Redis Hash增加数值成功 - key: {}, field: {}, delta: {}, result: {}", key, field, delta, result);
//            return result;
//        } catch (Exception e) {
//            log.error("集群Redis Hash增加数值失败 - key: {}, field: {}, delta: {}", key, field, delta, e);
//            throw new RuntimeException("集群Redis Hash增加数值失败", e);
//        }
//    }
//
//    /**
//     * 增加Hash字段的浮点数值
//     */
//    public Double hIncrByFloatCluster(String key, String field, double delta) {
//        try {
//            Double result = clusterRedisTemplate.opsForHash().increment(key, field, delta);
//            log.debug("集群Redis Hash增加浮点数值成功 - key: {}, field: {}, delta: {}, result: {}", key, field, delta, result);
//            return result;
//        } catch (Exception e) {
//            log.error("集群Redis Hash增加浮点数值失败 - key: {}, field: {}, delta: {}", key, field, delta, e);
//            throw new RuntimeException("集群Redis Hash增加浮点数值失败", e);
//        }
//    }
//
//    // ==================== 多节点Redis操作 ====================
//
//    /**
//     * 设置Hash字段值
//     */
//    public void hSetMultiNode(String key, String field, Object value) {
//        try {
//            multiNodeRedisTemplate.opsForHash().put(key, field, value);
//            log.debug("多节点Redis Hash设置成功 - key: {}, field: {}, value: {}", key, field, value);
//        } catch (Exception e) {
//            log.error("多节点Redis Hash设置失败 - key: {}, field: {}, value: {}", key, field, value, e);
//            throw new RuntimeException("多节点Redis Hash设置失败", e);
//        }
//    }
//
//    /**
//     * 批量设置Hash字段值
//     */
//    public void hMSetMultiNode(String key, Map<String, Object> map) {
//        try {
//            multiNodeRedisTemplate.opsForHash().putAll(key, map);
//            log.debug("多节点Redis Hash批量设置成功 - key: {}, map: {}", key, map);
//        } catch (Exception e) {
//            log.error("多节点Redis Hash批量设置失败 - key: {}, map: {}", key, map, e);
//            throw new RuntimeException("多节点Redis Hash批量设置失败", e);
//        }
//    }
//
//    /**
//     * 获取Hash字段值
//     */
//    public Object hGetMultiNode(String key, String field) {
//        try {
//            Object result = multiNodeRedisTemplate.opsForHash().get(key, field);
//            log.debug("多节点Redis Hash获取成功 - key: {}, field: {}, result: {}", key, field, result);
//            return result;
//        } catch (Exception e) {
//            log.error("多节点Redis Hash获取失败 - key: {}, field: {}", key, field, e);
//            throw new RuntimeException("多节点Redis Hash获取失败", e);
//        }
//    }
//
//    /**
//     * 批量获取Hash字段值
//     */
//    public List<Object> hMGetMultiNode(String key, Collection<String> fields) {
//        try {
//            List<Object> result = multiNodeRedisTemplate.opsForHash().multiGet(key, fields);
//            log.debug("多节点Redis Hash批量获取成功 - key: {}, fields: {}, result: {}", key, fields, result);
//            return result;
//        } catch (Exception e) {
//            log.error("多节点Redis Hash批量获取失败 - key: {}, fields: {}", key, fields, e);
//            throw new RuntimeException("多节点Redis Hash批量获取失败", e);
//        }
//    }
//
//    /**
//     * 获取Hash的所有字段和值
//     */
//    public Map<Object, Object> hGetAllMultiNode(String key) {
//        try {
//            Map<Object, Object> result = multiNodeRedisTemplate.opsForHash().entries(key);
//            log.debug("多节点Redis Hash获取所有字段成功 - key: {}, result: {}", key, result);
//            return result;
//        } catch (Exception e) {
//            log.error("多节点Redis Hash获取所有字段失败 - key: {}", key, e);
//            throw new RuntimeException("多节点Redis Hash获取所有字段失败", e);
//        }
//    }
//
//    /**
//     * 获取Hash的所有字段名
//     */
//    public Set<Object> hKeysMultiNode(String key) {
//        try {
//            Set<Object> result = multiNodeRedisTemplate.opsForHash().keys(key);
//            log.debug("多节点Redis Hash获取所有字段名成功 - key: {}, result: {}", key, result);
//            return result;
//        } catch (Exception e) {
//            log.error("多节点Redis Hash获取所有字段名失败 - key: {}", key, e);
//            throw new RuntimeException("多节点Redis Hash获取所有字段名失败", e);
//        }
//    }
//
//    /**
//     * 获取Hash的所有值
//     */
//    public List<Object> hValsMultiNode(String key) {
//        try {
//            List<Object> result = multiNodeRedisTemplate.opsForHash().values(key);
//            log.debug("多节点Redis Hash获取所有值成功 - key: {}, result: {}", key, result);
//            return result;
//        } catch (Exception e) {
//            log.error("多节点Redis Hash获取所有值失败 - key: {}", key, e);
//            throw new RuntimeException("多节点Redis Hash获取所有值失败", e);
//        }
//    }
//
//    /**
//     * 获取Hash的大小
//     */
//    public Long hLenMultiNode(String key) {
//        try {
//            Long result = multiNodeRedisTemplate.opsForHash().size(key);
//            log.debug("多节点Redis Hash获取大小成功 - key: {}, result: {}", key, result);
//            return result;
//        } catch (Exception e) {
//            log.error("多节点Redis Hash获取大小失败 - key: {}", key, e);
//            throw new RuntimeException("多节点Redis Hash获取大小失败", e);
//        }
//    }
//
//    /**
//     * 判断Hash字段是否存在
//     */
//    public Boolean hExistsMultiNode(String key, String field) {
//        try {
//            Boolean result = multiNodeRedisTemplate.opsForHash().hasKey(key, field);
//            log.debug("多节点Redis Hash字段存在检查成功 - key: {}, field: {}, result: {}", key, field, result);
//            return result;
//        } catch (Exception e) {
//            log.error("多节点Redis Hash字段存在检查失败 - key: {}, field: {}", key, field, e);
//            throw new RuntimeException("多节点Redis Hash字段存在检查失败", e);
//        }
//    }
//
//    /**
//     * 删除Hash字段
//     */
//    public Long hDelMultiNode(String key, Object... fields) {
//        try {
//            Long result = multiNodeRedisTemplate.opsForHash().delete(key, fields);
//            log.debug("多节点Redis Hash删除字段成功 - key: {}, fields: {}, result: {}", key, fields, result);
//            return result;
//        } catch (Exception e) {
//            log.error("多节点Redis Hash删除字段失败 - key: {}, fields: {}", key, fields, e);
//            throw new RuntimeException("多节点Redis Hash删除字段失败", e);
//        }
//    }
//
//    /**
//     * 增加Hash字段的数值
//     */
//    public Long hIncrByMultiNode(String key, String field, long delta) {
//        try {
//            Long result = multiNodeRedisTemplate.opsForHash().increment(key, field, delta);
//            log.debug("多节点Redis Hash增加数值成功 - key: {}, field: {}, delta: {}, result: {}", key, field, delta, result);
//            return result;
//        } catch (Exception e) {
//            log.error("多节点Redis Hash增加数值失败 - key: {}, field: {}, delta: {}", key, field, delta, e);
//            throw new RuntimeException("多节点Redis Hash增加数值失败", e);
//        }
//    }
//
//    /**
//     * 增加Hash字段的浮点数值
//     */
//    public Double hIncrByFloatMultiNode(String key, String field, double delta) {
//        try {
//            Double result = multiNodeRedisTemplate.opsForHash().increment(key, field, delta);
//            log.debug("多节点Redis Hash增加浮点数值成功 - key: {}, field: {}, delta: {}, result: {}", key, field, delta, result);
//            return result;
//        } catch (Exception e) {
//            log.error("多节点Redis Hash增加浮点数值失败 - key: {}, field: {}, delta: {}", key, field, delta, e);
//            throw new RuntimeException("多节点Redis Hash增加浮点数值失败", e);
//        }
//    }

    // ==================== 通用方法 ====================
//
//    /**
//     * 根据Redis类型获取对应的模板
//     */
//    public RedisTemplate<String, Object> getRedisTemplate(RedisStringUtils.RedisType redisType) {
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
//     * 通用设置字段值方法
//     */
//    public void hSet(String key, String field, Object value, RedisStringUtils.RedisType redisType) {
//        switch (redisType) {
//            case SINGLE:
//                hSetSingle(key, field, value);
//                break;
//            case CLUSTER:
//                hSetCluster(key, field, value);
//                break;
//            case MULTI_NODE:
//                hSetMultiNode(key, field, value);
//                break;
//            default:
//                throw new IllegalArgumentException("不支持的Redis类型: " + redisType);
//        }
//    }
//
//    /**
//     * 通用获取字段值方法
//     */
//    public Object hGet(String key, String field, RedisStringUtils.RedisType redisType) {
//        switch (redisType) {
//            case SINGLE:
//                return hGetSingle(key, field);
//            case CLUSTER:
//                return hGetCluster(key, field);
//            case MULTI_NODE:
//                return hGetMultiNode(key, field);
//            default:
//                throw new IllegalArgumentException("不支持的Redis类型: " + redisType);
//        }
//    }
//
//    /**
//     * 通用批量设置方法
//     */
//    public void hMSet(String key, Map<String, Object> map, RedisStringUtils.RedisType redisType) {
//        switch (redisType) {
//            case SINGLE:
//                hMSetSingle(key, map);
//                break;
//            case CLUSTER:
//                hMSetCluster(key, map);
//                break;
//            case MULTI_NODE:
//                hMSetMultiNode(key, map);
//                break;
//            default:
//                throw new IllegalArgumentException("不支持的Redis类型: " + redisType);
//        }
//    }
//
//    /**
//     * 通用批量获取方法
//     */
//    public List<Object> hMGet(String key, Collection<String> fields, RedisStringUtils.RedisType redisType) {
//        switch (redisType) {
//            case SINGLE:
//                return hMGetSingle(key, fields);
//            case CLUSTER:
//                return hMGetCluster(key, fields);
//            case MULTI_NODE:
//                return hMGetMultiNode(key, fields);
//            default:
//                throw new IllegalArgumentException("不支持的Redis类型: " + redisType);
//        }
//    }
//
//    /**
//     * 通用获取所有字段方法
//     */
//    public Map<Object, Object> hGetAll(String key, RedisStringUtils.RedisType redisType) {
//        switch (redisType) {
//            case SINGLE:
//                return hGetAllSingle(key);
//            case CLUSTER:
//                return hGetAllCluster(key);
//            case MULTI_NODE:
//                return hGetAllMultiNode(key);
//            default:
//                throw new IllegalArgumentException("不支持的Redis类型: " + redisType);
//        }
//    }
} 