package com.practise.demo.utils;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.stereotype.Component;

import java.util.Set;

/**
 * Redis ZSet操作工具类
 * 支持单机、集群、多节点三种配置
 */
@Slf4j
@Component
public class RedisZSetUtils {

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
     * 添加元素到ZSet
     */
    public Boolean zAddSingle(String key, Object value, double score) {
        try {
            Boolean result = singleRedisTemplate.opsForZSet().add(key, value, score);
            log.debug("单机Redis ZSet添加成功 - key: {}, value: {}, score: {}, result: {}", key, value, score, result);
            return result;
        } catch (Exception e) {
            log.error("单机Redis ZSet添加失败 - key: {}, value: {}, score: {}", key, value, score, e);
            throw new RuntimeException("单机Redis ZSet添加失败", e);
        }
    }

    /**
     * 批量添加元素到ZSet
     */
    public Long zAddSingle(String key, Set<ZSetOperations.TypedTuple<Object>> tuples) {
        try {
            Long result = singleRedisTemplate.opsForZSet().add(key, tuples);
            log.debug("单机Redis ZSet批量添加成功 - key: {}, tuples: {}, result: {}", key, tuples, result);
            return result;
        } catch (Exception e) {
            log.error("单机Redis ZSet批量添加失败 - key: {}, tuples: {}", key, tuples, e);
            throw new RuntimeException("单机Redis ZSet批量添加失败", e);
        }
    }

    /**
     * 从ZSet中移除元素
     */
    public Long zRemSingle(String key, Object... values) {
        try {
            Long result = singleRedisTemplate.opsForZSet().remove(key, values);
            log.debug("单机Redis ZSet移除成功 - key: {}, values: {}, result: {}", key, values, result);
            return result;
        } catch (Exception e) {
            log.error("单机Redis ZSet移除失败 - key: {}, values: {}", key, values, e);
            throw new RuntimeException("单机Redis ZSet移除失败", e);
        }
    }

    /**
     * 获取ZSet的大小
     */
    public Long zCardSingle(String key) {
        try {
            Long result = singleRedisTemplate.opsForZSet().size(key);
            log.debug("单机Redis ZSet获取大小成功 - key: {}, result: {}", key, result);
            return result;
        } catch (Exception e) {
            log.error("单机Redis ZSet获取大小失败 - key: {}", key, e);
            throw new RuntimeException("单机Redis ZSet获取大小失败", e);
        }
    }

    /**
     * 获取元素的分数
     */
    public Double zScoreSingle(String key, Object value) {
        try {
            Double result = singleRedisTemplate.opsForZSet().score(key, value);
            log.debug("单机Redis ZSet获取分数成功 - key: {}, value: {}, result: {}", key, value, result);
            return result;
        } catch (Exception e) {
            log.error("单机Redis ZSet获取分数失败 - key: {}, value: {}", key, value, e);
            throw new RuntimeException("单机Redis ZSet获取分数失败", e);
        }
    }

    /**
     * 获取元素的排名（从小到大）
     */
    public Long zRankSingle(String key, Object value) {
        try {
            Long result = singleRedisTemplate.opsForZSet().rank(key, value);
            log.debug("单机Redis ZSet获取排名成功 - key: {}, value: {}, result: {}", key, value, result);
            return result;
        } catch (Exception e) {
            log.error("单机Redis ZSet获取排名失败 - key: {}, value: {}", key, value, e);
            throw new RuntimeException("单机Redis ZSet获取排名失败", e);
        }
    }

    /**
     * 获取元素的排名（从大到小）
     */
    public Long zRevRankSingle(String key, Object value) {
        try {
            Long result = singleRedisTemplate.opsForZSet().reverseRank(key, value);
            log.debug("单机Redis ZSet获取倒序排名成功 - key: {}, value: {}, result: {}", key, value, result);
            return result;
        } catch (Exception e) {
            log.error("单机Redis ZSet获取倒序排名失败 - key: {}, value: {}", key, value, e);
            throw new RuntimeException("单机Redis ZSet获取倒序排名失败", e);
        }
    }

    /**
     * 根据分数范围获取元素（从小到大）
     */
    public Set<Object> zRangeByScoreSingle(String key, double min, double max) {
        try {
            Set<Object> result = singleRedisTemplate.opsForZSet().rangeByScore(key, min, max);
            log.debug("单机Redis ZSet根据分数范围获取元素成功 - key: {}, min: {}, max: {}, result: {}", key, min, max, result);
            return result;
        } catch (Exception e) {
            log.error("单机Redis ZSet根据分数范围获取元素失败 - key: {}, min: {}, max: {}", key, min, max, e);
            throw new RuntimeException("单机Redis ZSet根据分数范围获取元素失败", e);
        }
    }

    /**
     * 根据分数范围获取元素（从大到小）
     */
    public Set<Object> zRevRangeByScoreSingle(String key, double min, double max) {
        try {
            Set<Object> result = singleRedisTemplate.opsForZSet().reverseRangeByScore(key, min, max);
            log.debug("单机Redis ZSet根据分数范围倒序获取元素成功 - key: {}, min: {}, max: {}, result: {}", key, min, max, result);
            return result;
        } catch (Exception e) {
            log.error("单机Redis ZSet根据分数范围倒序获取元素失败 - key: {}, min: {}, max: {}", key, min, max, e);
            throw new RuntimeException("单机Redis ZSet根据分数范围倒序获取元素失败", e);
        }
    }

    /**
     * 根据排名范围获取元素（从小到大）
     */
    public Set<Object> zRangeSingle(String key, long start, long end) {
        try {
            Set<Object> result = singleRedisTemplate.opsForZSet().range(key, start, end);
            log.debug("单机Redis ZSet根据排名范围获取元素成功 - key: {}, start: {}, end: {}, result: {}", key, start, end, result);
            return result;
        } catch (Exception e) {
            log.error("单机Redis ZSet根据排名范围获取元素失败 - key: {}, start: {}, end: {}", key, start, end, e);
            throw new RuntimeException("单机Redis ZSet根据排名范围获取元素失败", e);
        }
    }

    /**
     * 根据排名范围获取元素（从大到小）
     */
    public Set<Object> zRevRangeSingle(String key, long start, long end) {
        try {
            Set<Object> result = singleRedisTemplate.opsForZSet().reverseRange(key, start, end);
            log.debug("单机Redis ZSet根据排名范围倒序获取元素成功 - key: {}, start: {}, end: {}, result: {}", key, start, end, result);
            return result;
        } catch (Exception e) {
            log.error("单机Redis ZSet根据排名范围倒序获取元素失败 - key: {}, start: {}, end: {}", key, start, end, e);
            throw new RuntimeException("单机Redis ZSet根据排名范围倒序获取元素失败", e);
        }
    }

    /**
     * 增加元素的分数
     */
    public Double zIncrBySingle(String key, Object value, double delta) {
        try {
            Double result = singleRedisTemplate.opsForZSet().incrementScore(key, value, delta);
            log.debug("单机Redis ZSet增加分数成功 - key: {}, value: {}, delta: {}, result: {}", key, value, delta, result);
            return result;
        } catch (Exception e) {
            log.error("单机Redis ZSet增加分数失败 - key: {}, value: {}, delta: {}", key, value, delta, e);
            throw new RuntimeException("单机Redis ZSet增加分数失败", e);
        }
    }

    // ==================== 集群Redis操作 ====================

//    /**
//     * 添加元素到ZSet
//     */
//    public Boolean zAddCluster(String key, Object value, double score) {
//        try {
//            Boolean result = clusterRedisTemplate.opsForZSet().add(key, value, score);
//            log.debug("集群Redis ZSet添加成功 - key: {}, value: {}, score: {}, result: {}", key, value, score, result);
//            return result;
//        } catch (Exception e) {
//            log.error("集群Redis ZSet添加失败 - key: {}, value: {}, score: {}", key, value, score, e);
//            throw new RuntimeException("集群Redis ZSet添加失败", e);
//        }
//    }
//
//    /**
//     * 批量添加元素到ZSet
//     */
//    public Long zAddCluster(String key, Set<ZSetOperations.TypedTuple<Object>> tuples) {
//        try {
//            Long result = clusterRedisTemplate.opsForZSet().add(key, tuples);
//            log.debug("集群Redis ZSet批量添加成功 - key: {}, tuples: {}, result: {}", key, tuples, result);
//            return result;
//        } catch (Exception e) {
//            log.error("集群Redis ZSet批量添加失败 - key: {}, tuples: {}", key, tuples, e);
//            throw new RuntimeException("集群Redis ZSet批量添加失败", e);
//        }
//    }
//
//    /**
//     * 从ZSet中移除元素
//     */
//    public Long zRemCluster(String key, Object... values) {
//        try {
//            Long result = clusterRedisTemplate.opsForZSet().remove(key, values);
//            log.debug("集群Redis ZSet移除成功 - key: {}, values: {}, result: {}", key, values, result);
//            return result;
//        } catch (Exception e) {
//            log.error("集群Redis ZSet移除失败 - key: {}, values: {}", key, values, e);
//            throw new RuntimeException("集群Redis ZSet移除失败", e);
//        }
//    }
//
//    /**
//     * 获取ZSet的大小
//     */
//    public Long zCardCluster(String key) {
//        try {
//            Long result = clusterRedisTemplate.opsForZSet().size(key);
//            log.debug("集群Redis ZSet获取大小成功 - key: {}, result: {}", key, result);
//            return result;
//        } catch (Exception e) {
//            log.error("集群Redis ZSet获取大小失败 - key: {}", key, e);
//            throw new RuntimeException("集群Redis ZSet获取大小失败", e);
//        }
//    }
//
//    /**
//     * 获取元素的分数
//     */
//    public Double zScoreCluster(String key, Object value) {
//        try {
//            Double result = clusterRedisTemplate.opsForZSet().score(key, value);
//            log.debug("集群Redis ZSet获取分数成功 - key: {}, value: {}, result: {}", key, value, result);
//            return result;
//        } catch (Exception e) {
//            log.error("集群Redis ZSet获取分数失败 - key: {}, value: {}", key, value, e);
//            throw new RuntimeException("集群Redis ZSet获取分数失败", e);
//        }
//    }
//
//    /**
//     * 获取元素的排名（从小到大）
//     */
//    public Long zRankCluster(String key, Object value) {
//        try {
//            Long result = clusterRedisTemplate.opsForZSet().rank(key, value);
//            log.debug("集群Redis ZSet获取排名成功 - key: {}, value: {}, result: {}", key, value, result);
//            return result;
//        } catch (Exception e) {
//            log.error("集群Redis ZSet获取排名失败 - key: {}, value: {}", key, value, e);
//            throw new RuntimeException("集群Redis ZSet获取排名失败", e);
//        }
//    }
//
//    /**
//     * 获取元素的排名（从大到小）
//     */
//    public Long zRevRankCluster(String key, Object value) {
//        try {
//            Long result = clusterRedisTemplate.opsForZSet().reverseRank(key, value);
//            log.debug("集群Redis ZSet获取倒序排名成功 - key: {}, value: {}, result: {}", key, value, result);
//            return result;
//        } catch (Exception e) {
//            log.error("集群Redis ZSet获取倒序排名失败 - key: {}, value: {}", key, value, e);
//            throw new RuntimeException("集群Redis ZSet获取倒序排名失败", e);
//        }
//    }
//
//    /**
//     * 根据分数范围获取元素（从小到大）
//     */
//    public Set<Object> zRangeByScoreCluster(String key, double min, double max) {
//        try {
//            Set<Object> result = clusterRedisTemplate.opsForZSet().rangeByScore(key, min, max);
//            log.debug("集群Redis ZSet根据分数范围获取元素成功 - key: {}, min: {}, max: {}, result: {}", key, min, max, result);
//            return result;
//        } catch (Exception e) {
//            log.error("集群Redis ZSet根据分数范围获取元素失败 - key: {}, min: {}, max: {}", key, min, max, e);
//            throw new RuntimeException("集群Redis ZSet根据分数范围获取元素失败", e);
//        }
//    }
//
//    /**
//     * 根据分数范围获取元素（从大到小）
//     */
//    public Set<Object> zRevRangeByScoreCluster(String key, double min, double max) {
//        try {
//            Set<Object> result = clusterRedisTemplate.opsForZSet().reverseRangeByScore(key, min, max);
//            log.debug("集群Redis ZSet根据分数范围倒序获取元素成功 - key: {}, min: {}, max: {}, result: {}", key, min, max, result);
//            return result;
//        } catch (Exception e) {
//            log.error("集群Redis ZSet根据分数范围倒序获取元素失败 - key: {}, min: {}, max: {}", key, min, max, e);
//            throw new RuntimeException("集群Redis ZSet根据分数范围倒序获取元素失败", e);
//        }
//    }
//
//    /**
//     * 根据排名范围获取元素（从小到大）
//     */
//    public Set<Object> zRangeCluster(String key, long start, long end) {
//        try {
//            Set<Object> result = clusterRedisTemplate.opsForZSet().range(key, start, end);
//            log.debug("集群Redis ZSet根据排名范围获取元素成功 - key: {}, start: {}, end: {}, result: {}", key, start, end, result);
//            return result;
//        } catch (Exception e) {
//            log.error("集群Redis ZSet根据排名范围获取元素失败 - key: {}, start: {}, end: {}", key, start, end, e);
//            throw new RuntimeException("集群Redis ZSet根据排名范围获取元素失败", e);
//        }
//    }
//
//    /**
//     * 根据排名范围获取元素（从大到小）
//     */
//    public Set<Object> zRevRangeCluster(String key, long start, long end) {
//        try {
//            Set<Object> result = clusterRedisTemplate.opsForZSet().reverseRange(key, start, end);
//            log.debug("集群Redis ZSet根据排名范围倒序获取元素成功 - key: {}, start: {}, end: {}, result: {}", key, start, end, result);
//            return result;
//        } catch (Exception e) {
//            log.error("集群Redis ZSet根据排名范围倒序获取元素失败 - key: {}, start: {}, end: {}", key, start, end, e);
//            throw new RuntimeException("集群Redis ZSet根据排名范围倒序获取元素失败", e);
//        }
//    }
//
//    /**
//     * 增加元素的分数
//     */
//    public Double zIncrByCluster(String key, Object value, double delta) {
//        try {
//            Double result = clusterRedisTemplate.opsForZSet().incrementScore(key, value, delta);
//            log.debug("集群Redis ZSet增加分数成功 - key: {}, value: {}, delta: {}, result: {}", key, value, delta, result);
//            return result;
//        } catch (Exception e) {
//            log.error("集群Redis ZSet增加分数失败 - key: {}, value: {}, delta: {}", key, value, delta, e);
//            throw new RuntimeException("集群Redis ZSet增加分数失败", e);
//        }
//    }
//
//    // ==================== 多节点Redis操作 ====================
//
//    /**
//     * 添加元素到ZSet
//     */
//    public Boolean zAddMultiNode(String key, Object value, double score) {
//        try {
//            Boolean result = multiNodeRedisTemplate.opsForZSet().add(key, value, score);
//            log.debug("多节点Redis ZSet添加成功 - key: {}, value: {}, score: {}, result: {}", key, value, score, result);
//            return result;
//        } catch (Exception e) {
//            log.error("多节点Redis ZSet添加失败 - key: {}, value: {}, score: {}", key, value, score, e);
//            throw new RuntimeException("多节点Redis ZSet添加失败", e);
//        }
//    }
//
//    /**
//     * 批量添加元素到ZSet
//     */
//    public Long zAddMultiNode(String key, Set<ZSetOperations.TypedTuple<Object>> tuples) {
//        try {
//            Long result = multiNodeRedisTemplate.opsForZSet().add(key, tuples);
//            log.debug("多节点Redis ZSet批量添加成功 - key: {}, tuples: {}, result: {}", key, tuples, result);
//            return result;
//        } catch (Exception e) {
//            log.error("多节点Redis ZSet批量添加失败 - key: {}, tuples: {}", key, tuples, e);
//            throw new RuntimeException("多节点Redis ZSet批量添加失败", e);
//        }
//    }
//
//    /**
//     * 从ZSet中移除元素
//     */
//    public Long zRemMultiNode(String key, Object... values) {
//        try {
//            Long result = multiNodeRedisTemplate.opsForZSet().remove(key, values);
//            log.debug("多节点Redis ZSet移除成功 - key: {}, values: {}, result: {}", key, values, result);
//            return result;
//        } catch (Exception e) {
//            log.error("多节点Redis ZSet移除失败 - key: {}, values: {}", key, values, e);
//            throw new RuntimeException("多节点Redis ZSet移除失败", e);
//        }
//    }
//
//    /**
//     * 获取ZSet的大小
//     */
//    public Long zCardMultiNode(String key) {
//        try {
//            Long result = multiNodeRedisTemplate.opsForZSet().size(key);
//            log.debug("多节点Redis ZSet获取大小成功 - key: {}, result: {}", key, result);
//            return result;
//        } catch (Exception e) {
//            log.error("多节点Redis ZSet获取大小失败 - key: {}", key, e);
//            throw new RuntimeException("多节点Redis ZSet获取大小失败", e);
//        }
//    }
//
//    /**
//     * 获取元素的分数
//     */
//    public Double zScoreMultiNode(String key, Object value) {
//        try {
//            Double result = multiNodeRedisTemplate.opsForZSet().score(key, value);
//            log.debug("多节点Redis ZSet获取分数成功 - key: {}, value: {}, result: {}", key, value, result);
//            return result;
//        } catch (Exception e) {
//            log.error("多节点Redis ZSet获取分数失败 - key: {}, value: {}", key, value, e);
//            throw new RuntimeException("多节点Redis ZSet获取分数失败", e);
//        }
//    }
//
//    /**
//     * 获取元素的排名（从小到大）
//     */
//    public Long zRankMultiNode(String key, Object value) {
//        try {
//            Long result = multiNodeRedisTemplate.opsForZSet().rank(key, value);
//            log.debug("多节点Redis ZSet获取排名成功 - key: {}, value: {}, result: {}", key, value, result);
//            return result;
//        } catch (Exception e) {
//            log.error("多节点Redis ZSet获取排名失败 - key: {}, value: {}", key, value, e);
//            throw new RuntimeException("多节点Redis ZSet获取排名失败", e);
//        }
//    }
//
//    /**
//     * 获取元素的排名（从大到小）
//     */
//    public Long zRevRankMultiNode(String key, Object value) {
//        try {
//            Long result = multiNodeRedisTemplate.opsForZSet().reverseRank(key, value);
//            log.debug("多节点Redis ZSet获取倒序排名成功 - key: {}, value: {}, result: {}", key, value, result);
//            return result;
//        } catch (Exception e) {
//            log.error("多节点Redis ZSet获取倒序排名失败 - key: {}, value: {}", key, value, e);
//            throw new RuntimeException("多节点Redis ZSet获取倒序排名失败", e);
//        }
//    }
//
//    /**
//     * 根据分数范围获取元素（从小到大）
//     */
//    public Set<Object> zRangeByScoreMultiNode(String key, double min, double max) {
//        try {
//            Set<Object> result = multiNodeRedisTemplate.opsForZSet().rangeByScore(key, min, max);
//            log.debug("多节点Redis ZSet根据分数范围获取元素成功 - key: {}, min: {}, max: {}, result: {}", key, min, max, result);
//            return result;
//        } catch (Exception e) {
//            log.error("多节点Redis ZSet根据分数范围获取元素失败 - key: {}, min: {}, max: {}", key, min, max, e);
//            throw new RuntimeException("多节点Redis ZSet根据分数范围获取元素失败", e);
//        }
//    }
//
//    /**
//     * 根据分数范围获取元素（从大到小）
//     */
//    public Set<Object> zRevRangeByScoreMultiNode(String key, double min, double max) {
//        try {
//            Set<Object> result = multiNodeRedisTemplate.opsForZSet().reverseRangeByScore(key, min, max);
//            log.debug("多节点Redis ZSet根据分数范围倒序获取元素成功 - key: {}, min: {}, max: {}, result: {}", key, min, max, result);
//            return result;
//        } catch (Exception e) {
//            log.error("多节点Redis ZSet根据分数范围倒序获取元素失败 - key: {}, min: {}, max: {}", key, min, max, e);
//            throw new RuntimeException("多节点Redis ZSet根据分数范围倒序获取元素失败", e);
//        }
//    }
//
//    /**
//     * 根据排名范围获取元素（从小到大）
//     */
//    public Set<Object> zRangeMultiNode(String key, long start, long end) {
//        try {
//            Set<Object> result = multiNodeRedisTemplate.opsForZSet().range(key, start, end);
//            log.debug("多节点Redis ZSet根据排名范围获取元素成功 - key: {}, start: {}, end: {}, result: {}", key, start, end, result);
//            return result;
//        } catch (Exception e) {
//            log.error("多节点Redis ZSet根据排名范围获取元素失败 - key: {}, start: {}, end: {}", key, start, end, e);
//            throw new RuntimeException("多节点Redis ZSet根据排名范围获取元素失败", e);
//        }
//    }
//
//    /**
//     * 根据排名范围获取元素（从大到小）
//     */
//    public Set<Object> zRevRangeMultiNode(String key, long start, long end) {
//        try {
//            Set<Object> result = multiNodeRedisTemplate.opsForZSet().reverseRange(key, start, end);
//            log.debug("多节点Redis ZSet根据排名范围倒序获取元素成功 - key: {}, start: {}, end: {}, result: {}", key, start, end, result);
//            return result;
//        } catch (Exception e) {
//            log.error("多节点Redis ZSet根据排名范围倒序获取元素失败 - key: {}, start: {}, end: {}", key, start, end, e);
//            throw new RuntimeException("多节点Redis ZSet根据排名范围倒序获取元素失败", e);
//        }
//    }
//
//    /**
//     * 增加元素的分数
//     */
//    public Double zIncrByMultiNode(String key, Object value, double delta) {
//        try {
//            Double result = multiNodeRedisTemplate.opsForZSet().incrementScore(key, value, delta);
//            log.debug("多节点Redis ZSet增加分数成功 - key: {}, value: {}, delta: {}, result: {}", key, value, delta, result);
//            return result;
//        } catch (Exception e) {
//            log.error("多节点Redis ZSet增加分数失败 - key: {}, value: {}, delta: {}", key, value, delta, e);
//            throw new RuntimeException("多节点Redis ZSet增加分数失败", e);
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
//     * 通用添加元素方法
//     */
//    public Boolean zAdd(String key, Object value, double score, RedisStringUtils.RedisType redisType) {
//        switch (redisType) {
//            case SINGLE:
//                return zAddSingle(key, value, score);
//            case CLUSTER:
//                return zAddCluster(key, value, score);
//            case MULTI_NODE:
//                return zAddMultiNode(key, value, score);
//            default:
//                throw new IllegalArgumentException("不支持的Redis类型: " + redisType);
//        }
//    }
//
//    /**
//     * 通用移除元素方法
//     */
//    public Long zRem(String key, Object[] values, RedisStringUtils.RedisType redisType) {
//        switch (redisType) {
//            case SINGLE:
//                return zRemSingle(key, values);
//            case CLUSTER:
//                return zRemCluster(key, values);
//            case MULTI_NODE:
//                return zRemMultiNode(key, values);
//            default:
//                throw new IllegalArgumentException("不支持的Redis类型: " + redisType);
//        }
//    }
//
//    /**
//     * 通用获取大小方法
//     */
//    public Long zCard(String key, RedisStringUtils.RedisType redisType) {
//        switch (redisType) {
//            case SINGLE:
//                return zCardSingle(key);
//            case CLUSTER:
//                return zCardCluster(key);
//            case MULTI_NODE:
//                return zCardMultiNode(key);
//            default:
//                throw new IllegalArgumentException("不支持的Redis类型: " + redisType);
//        }
//    }
//
//    /**
//     * 通用获取分数方法
//     */
//    public Double zScore(String key, Object value, RedisStringUtils.RedisType redisType) {
//        switch (redisType) {
//            case SINGLE:
//                return zScoreSingle(key, value);
//            case CLUSTER:
//                return zScoreCluster(key, value);
//            case MULTI_NODE:
//                return zScoreMultiNode(key, value);
//            default:
//                throw new IllegalArgumentException("不支持的Redis类型: " + redisType);
//        }
//    }
}