package com.practise.demo.utils;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Redis List操作工具类
 * 支持单机、集群、多节点三种配置
 */
@Slf4j
@Component
public class RedisListUtils {

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
     * 左推入元素
     */
    public Long lPushSingle(String key, Object... values) {
        try {
            Long result = singleRedisTemplate.opsForList().leftPushAll(key, values);
            log.debug("单机Redis左推入成功 - key: {}, values: {}, result: {}", key, values, result);
            return result;
        } catch (Exception e) {
            log.error("单机Redis左推入失败 - key: {}, values: {}", key, values, e);
            throw new RuntimeException("单机Redis左推入失败", e);
        }
    }

    /**
     * 右推入元素
     */
    public Long rPushSingle(String key, Object... values) {
        try {
            Long result = singleRedisTemplate.opsForList().rightPushAll(key, values);
            log.debug("单机Redis右推入成功 - key: {}, values: {}, result: {}", key, values, result);
            return result;
        } catch (Exception e) {
            log.error("单机Redis右推入失败 - key: {}, values: {}", key, values, e);
            throw new RuntimeException("单机Redis右推入失败", e);
        }
    }

    /**
     * 左弹出元素
     */
    public Object lPopSingle(String key) {
        try {
            Object result = singleRedisTemplate.opsForList().leftPop(key);
            log.debug("单机Redis左弹出成功 - key: {}, result: {}", key, result);
            return result;
        } catch (Exception e) {
            log.error("单机Redis左弹出失败 - key: {}", key, e);
            throw new RuntimeException("单机Redis左弹出失败", e);
        }
    }

    /**
     * 右弹出元素
     */
    public Object rPopSingle(String key) {
        try {
            Object result = singleRedisTemplate.opsForList().rightPop(key);
            log.debug("单机Redis右弹出成功 - key: {}, result: {}", key, result);
            return result;
        } catch (Exception e) {
            log.error("单机Redis右弹出失败 - key: {}", key, e);
            throw new RuntimeException("单机Redis右弹出失败", e);
        }
    }

    /**
     * 获取列表长度
     */
    public Long lLenSingle(String key) {
        try {
            Long result = singleRedisTemplate.opsForList().size(key);
            log.debug("单机Redis获取列表长度成功 - key: {}, result: {}", key, result);
            return result;
        } catch (Exception e) {
            log.error("单机Redis获取列表长度失败 - key: {}", key, e);
            throw new RuntimeException("单机Redis获取列表长度失败", e);
        }
    }

    /**
     * 获取指定范围的元素
     */
    public List<Object> lRangeSingle(String key, long start, long end) {
        try {
            List<Object> result = singleRedisTemplate.opsForList().range(key, start, end);
            log.debug("单机Redis获取范围元素成功 - key: {}, start: {}, end: {}, result: {}", key, start, end, result);
            return result;
        } catch (Exception e) {
            log.error("单机Redis获取范围元素失败 - key: {}, start: {}, end: {}", key, start, end, e);
            throw new RuntimeException("单机Redis获取范围元素失败", e);
        }
    }

    /**
     * 根据索引获取元素
     */
    public Object lIndexSingle(String key, long index) {
        try {
            Object result = singleRedisTemplate.opsForList().index(key, index);
            log.debug("单机Redis根据索引获取元素成功 - key: {}, index: {}, result: {}", key, index, result);
            return result;
        } catch (Exception e) {
            log.error("单机Redis根据索引获取元素失败 - key: {}, index: {}", key, index, e);
            throw new RuntimeException("单机Redis根据索引获取元素失败", e);
        }
    }

    /**
     * 设置指定索引的元素
     */
    public void lSetSingle(String key, long index, Object value) {
        try {
            singleRedisTemplate.opsForList().set(key, index, value);
            log.debug("单机Redis设置索引元素成功 - key: {}, index: {}, value: {}", key, index, value);
        } catch (Exception e) {
            log.error("单机Redis设置索引元素失败 - key: {}, index: {}, value: {}", key, index, value, e);
            throw new RuntimeException("单机Redis设置索引元素失败", e);
        }
    }

    /**
     * 删除指定值的元素
     */
    public Long lRemSingle(String key, long count, Object value) {
        try {
            Long result = singleRedisTemplate.opsForList().remove(key, count, value);
            log.debug("单机Redis删除元素成功 - key: {}, count: {}, value: {}, result: {}", key, count, value, result);
            return result;
        } catch (Exception e) {
            log.error("单机Redis删除元素失败 - key: {}, count: {}, value: {}", key, count, value, e);
            throw new RuntimeException("单机Redis删除元素失败", e);
        }
    }

    /**
     * 修剪列表
     */
    public void lTrimSingle(String key, long start, long end) {
        try {
            singleRedisTemplate.opsForList().trim(key, start, end);
            log.debug("单机Redis修剪列表成功 - key: {}, start: {}, end: {}", key, start, end);
        } catch (Exception e) {
            log.error("单机Redis修剪列表失败 - key: {}, start: {}, end: {}", key, start, end, e);
            throw new RuntimeException("单机Redis修剪列表失败", e);
        }
    }

    // ==================== 集群Redis操作 ====================
//
//    /**
//     * 左推入元素
//     */
//    public Long lPushCluster(String key, Object... values) {
//        try {
//            Long result = clusterRedisTemplate.opsForList().leftPushAll(key, values);
//            log.debug("集群Redis左推入成功 - key: {}, values: {}, result: {}", key, values, result);
//            return result;
//        } catch (Exception e) {
//            log.error("集群Redis左推入失败 - key: {}, values: {}", key, values, e);
//            throw new RuntimeException("集群Redis左推入失败", e);
//        }
//    }
//
//    /**
//     * 右推入元素
//     */
//    public Long rPushCluster(String key, Object... values) {
//        try {
//            Long result = clusterRedisTemplate.opsForList().rightPushAll(key, values);
//            log.debug("集群Redis右推入成功 - key: {}, values: {}, result: {}", key, values, result);
//            return result;
//        } catch (Exception e) {
//            log.error("集群Redis右推入失败 - key: {}, values: {}", key, values, e);
//            throw new RuntimeException("集群Redis右推入失败", e);
//        }
//    }
//
//    /**
//     * 左弹出元素
//     */
//    public Object lPopCluster(String key) {
//        try {
//            Object result = clusterRedisTemplate.opsForList().leftPop(key);
//            log.debug("集群Redis左弹出成功 - key: {}, result: {}", key, result);
//            return result;
//        } catch (Exception e) {
//            log.error("集群Redis左弹出失败 - key: {}", key, e);
//            throw new RuntimeException("集群Redis左弹出失败", e);
//        }
//    }
//
//    /**
//     * 右弹出元素
//     */
//    public Object rPopCluster(String key) {
//        try {
//            Object result = clusterRedisTemplate.opsForList().rightPop(key);
//            log.debug("集群Redis右弹出成功 - key: {}, result: {}", key, result);
//            return result;
//        } catch (Exception e) {
//            log.error("集群Redis右弹出失败 - key: {}", key, e);
//            throw new RuntimeException("集群Redis右弹出失败", e);
//        }
//    }
//
//    /**
//     * 获取列表长度
//     */
//    public Long lLenCluster(String key) {
//        try {
//            Long result = clusterRedisTemplate.opsForList().size(key);
//            log.debug("集群Redis获取列表长度成功 - key: {}, result: {}", key, result);
//            return result;
//        } catch (Exception e) {
//            log.error("集群Redis获取列表长度失败 - key: {}", key, e);
//            throw new RuntimeException("集群Redis获取列表长度失败", e);
//        }
//    }
//
//    /**
//     * 获取指定范围的元素
//     */
//    public List<Object> lRangeCluster(String key, long start, long end) {
//        try {
//            List<Object> result = clusterRedisTemplate.opsForList().range(key, start, end);
//            log.debug("集群Redis获取范围元素成功 - key: {}, start: {}, end: {}, result: {}", key, start, end, result);
//            return result;
//        } catch (Exception e) {
//            log.error("集群Redis获取范围元素失败 - key: {}, start: {}, end: {}", key, start, end, e);
//            throw new RuntimeException("集群Redis获取范围元素失败", e);
//        }
//    }
//
//    /**
//     * 根据索引获取元素
//     */
//    public Object lIndexCluster(String key, long index) {
//        try {
//            Object result = clusterRedisTemplate.opsForList().index(key, index);
//            log.debug("集群Redis根据索引获取元素成功 - key: {}, index: {}, result: {}", key, index, result);
//            return result;
//        } catch (Exception e) {
//            log.error("集群Redis根据索引获取元素失败 - key: {}, index: {}", key, index, e);
//            throw new RuntimeException("集群Redis根据索引获取元素失败", e);
//        }
//    }
//
//    /**
//     * 设置指定索引的元素
//     */
//    public void lSetCluster(String key, long index, Object value) {
//        try {
//            clusterRedisTemplate.opsForList().set(key, index, value);
//            log.debug("集群Redis设置索引元素成功 - key: {}, index: {}, value: {}", key, index, value);
//        } catch (Exception e) {
//            log.error("集群Redis设置索引元素失败 - key: {}, index: {}, value: {}", key, index, value, e);
//            throw new RuntimeException("集群Redis设置索引元素失败", e);
//        }
//    }
//
//    /**
//     * 删除指定值的元素
//     */
//    public Long lRemCluster(String key, long count, Object value) {
//        try {
//            Long result = clusterRedisTemplate.opsForList().remove(key, count, value);
//            log.debug("集群Redis删除元素成功 - key: {}, count: {}, value: {}, result: {}", key, count, value, result);
//            return result;
//        } catch (Exception e) {
//            log.error("集群Redis删除元素失败 - key: {}, count: {}, value: {}", key, count, value, e);
//            throw new RuntimeException("集群Redis删除元素失败", e);
//        }
//    }
//
//    /**
//     * 修剪列表
//     */
//    public void lTrimCluster(String key, long start, long end) {
//        try {
//            clusterRedisTemplate.opsForList().trim(key, start, end);
//            log.debug("集群Redis修剪列表成功 - key: {}, start: {}, end: {}", key, start, end);
//        } catch (Exception e) {
//            log.error("集群Redis修剪列表失败 - key: {}, start: {}, end: {}", key, start, end, e);
//            throw new RuntimeException("集群Redis修剪列表失败", e);
//        }
//    }
//
//    // ==================== 多节点Redis操作 ====================
//
//    /**
//     * 左推入元素
//     */
//    public Long lPushMultiNode(String key, Object... values) {
//        try {
//            Long result = multiNodeRedisTemplate.opsForList().leftPushAll(key, values);
//            log.debug("多节点Redis左推入成功 - key: {}, values: {}, result: {}", key, values, result);
//            return result;
//        } catch (Exception e) {
//            log.error("多节点Redis左推入失败 - key: {}, values: {}", key, values, e);
//            throw new RuntimeException("多节点Redis左推入失败", e);
//        }
//    }
//
//    /**
//     * 右推入元素
//     */
//    public Long rPushMultiNode(String key, Object... values) {
//        try {
//            Long result = multiNodeRedisTemplate.opsForList().rightPushAll(key, values);
//            log.debug("多节点Redis右推入成功 - key: {}, values: {}, result: {}", key, values, result);
//            return result;
//        } catch (Exception e) {
//            log.error("多节点Redis右推入失败 - key: {}, values: {}", key, values, e);
//            throw new RuntimeException("多节点Redis右推入失败", e);
//        }
//    }
//
//    /**
//     * 左弹出元素
//     */
//    public Object lPopMultiNode(String key) {
//        try {
//            Object result = multiNodeRedisTemplate.opsForList().leftPop(key);
//            log.debug("多节点Redis左弹出成功 - key: {}, result: {}", key, result);
//            return result;
//        } catch (Exception e) {
//            log.error("多节点Redis左弹出失败 - key: {}", key, e);
//            throw new RuntimeException("多节点Redis左弹出失败", e);
//        }
//    }
//
//    /**
//     * 右弹出元素
//     */
//    public Object rPopMultiNode(String key) {
//        try {
//            Object result = multiNodeRedisTemplate.opsForList().rightPop(key);
//            log.debug("多节点Redis右弹出成功 - key: {}, result: {}", key, result);
//            return result;
//        } catch (Exception e) {
//            log.error("多节点Redis右弹出失败 - key: {}", key, e);
//            throw new RuntimeException("多节点Redis右弹出失败", e);
//        }
//    }
//
//    /**
//     * 获取列表长度
//     */
//    public Long lLenMultiNode(String key) {
//        try {
//            Long result = multiNodeRedisTemplate.opsForList().size(key);
//            log.debug("多节点Redis获取列表长度成功 - key: {}, result: {}", key, result);
//            return result;
//        } catch (Exception e) {
//            log.error("多节点Redis获取列表长度失败 - key: {}", key, e);
//            throw new RuntimeException("多节点Redis获取列表长度失败", e);
//        }
//    }
//
//    /**
//     * 获取指定范围的元素
//     */
//    public List<Object> lRangeMultiNode(String key, long start, long end) {
//        try {
//            List<Object> result = multiNodeRedisTemplate.opsForList().range(key, start, end);
//            log.debug("多节点Redis获取范围元素成功 - key: {}, start: {}, end: {}, result: {}", key, start, end, result);
//            return result;
//        } catch (Exception e) {
//            log.error("多节点Redis获取范围元素失败 - key: {}, start: {}, end: {}", key, start, end, e);
//            throw new RuntimeException("多节点Redis获取范围元素失败", e);
//        }
//    }
//
//    /**
//     * 根据索引获取元素
//     */
//    public Object lIndexMultiNode(String key, long index) {
//        try {
//            Object result = multiNodeRedisTemplate.opsForList().index(key, index);
//            log.debug("多节点Redis根据索引获取元素成功 - key: {}, index: {}, result: {}", key, index, result);
//            return result;
//        } catch (Exception e) {
//            log.error("多节点Redis根据索引获取元素失败 - key: {}, index: {}", key, index, e);
//            throw new RuntimeException("多节点Redis根据索引获取元素失败", e);
//        }
//    }
//
//    /**
//     * 设置指定索引的元素
//     */
//    public void lSetMultiNode(String key, long index, Object value) {
//        try {
//            multiNodeRedisTemplate.opsForList().set(key, index, value);
//            log.debug("多节点Redis设置索引元素成功 - key: {}, index: {}, value: {}", key, index, value);
//        } catch (Exception e) {
//            log.error("多节点Redis设置索引元素失败 - key: {}, index: {}, value: {}", key, index, value, e);
//            throw new RuntimeException("多节点Redis设置索引元素失败", e);
//        }
//    }
//
//    /**
//     * 删除指定值的元素
//     */
//    public Long lRemMultiNode(String key, long count, Object value) {
//        try {
//            Long result = multiNodeRedisTemplate.opsForList().remove(key, count, value);
//            log.debug("多节点Redis删除元素成功 - key: {}, count: {}, value: {}, result: {}", key, count, value, result);
//            return result;
//        } catch (Exception e) {
//            log.error("多节点Redis删除元素失败 - key: {}, count: {}, value: {}", key, count, value, e);
//            throw new RuntimeException("多节点Redis删除元素失败", e);
//        }
//    }
//
//    /**
//     * 修剪列表
//     */
//    public void lTrimMultiNode(String key, long start, long end) {
//        try {
//            multiNodeRedisTemplate.opsForList().trim(key, start, end);
//            log.debug("多节点Redis修剪列表成功 - key: {}, start: {}, end: {}", key, start, end);
//        } catch (Exception e) {
//            log.error("多节点Redis修剪列表失败 - key: {}, start: {}, end: {}", key, start, end, e);
//            throw new RuntimeException("多节点Redis修剪列表失败", e);
//        }
//    }
//
//    // ==================== 通用方法 ====================
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
//     * 通用左推入方法
//     */
//    public Long lPush(String key, Object[] values, RedisStringUtils.RedisType redisType) {
//        switch (redisType) {
//            case SINGLE:
//                return lPushSingle(key, values);
//            case CLUSTER:
//                return lPushCluster(key, values);
//            case MULTI_NODE:
//                return lPushMultiNode(key, values);
//            default:
//                throw new IllegalArgumentException("不支持的Redis类型: " + redisType);
//        }
//    }
//
//    /**
//     * 通用右推入方法
//     */
//    public Long rPush(String key, Object[] values, RedisStringUtils.RedisType redisType) {
//        switch (redisType) {
//            case SINGLE:
//                return rPushSingle(key, values);
//            case CLUSTER:
//                return rPushCluster(key, values);
//            case MULTI_NODE:
//                return rPushMultiNode(key, values);
//            default:
//                throw new IllegalArgumentException("不支持的Redis类型: " + redisType);
//        }
//    }
//
//    /**
//     * 通用左弹出方法
//     */
//    public Object lPop(String key, RedisStringUtils.RedisType redisType) {
//        switch (redisType) {
//            case SINGLE:
//                return lPopSingle(key);
//            case CLUSTER:
//                return lPopCluster(key);
//            case MULTI_NODE:
//                return lPopMultiNode(key);
//            default:
//                throw new IllegalArgumentException("不支持的Redis类型: " + redisType);
//        }
//    }
//
//    /**
//     * 通用右弹出方法
//     */
//    public Object rPop(String key, RedisStringUtils.RedisType redisType) {
//        switch (redisType) {
//            case SINGLE:
//                return rPopSingle(key);
//            case CLUSTER:
//                return rPopCluster(key);
//            case MULTI_NODE:
//                return rPopMultiNode(key);
//            default:
//                throw new IllegalArgumentException("不支持的Redis类型: " + redisType);
//        }
//    }
} 