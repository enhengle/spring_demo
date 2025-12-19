package com.practise.demo.utils;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.util.Set;
import java.util.List;

/**
 * Redis Set操作工具类
 * 支持单机、集群、多节点三种配置
 */
@Slf4j
@Component
public class RedisSetUtils {

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
     * 添加元素到Set
     */
    public Long sAddSingle(String key, Object... values) {
        try {
            Long result = singleRedisTemplate.opsForSet().add(key, values);
            log.debug("单机Redis Set添加成功 - key: {}, values: {}, result: {}", key, values, result);
            return result;
        } catch (Exception e) {
            log.error("单机Redis Set添加失败 - key: {}, values: {}", key, values, e);
            throw new RuntimeException("单机Redis Set添加失败", e);
        }
    }

    /**
     * 从Set中移除元素
     */
    public Long sRemSingle(String key, Object... values) {
        try {
            Long result = singleRedisTemplate.opsForSet().remove(key, values);
            log.debug("单机Redis Set移除成功 - key: {}, values: {}, result: {}", key, values, result);
            return result;
        } catch (Exception e) {
            log.error("单机Redis Set移除失败 - key: {}, values: {}", key, values, e);
            throw new RuntimeException("单机Redis Set移除失败", e);
        }
    }

    /**
     * 获取Set中的所有元素
     */
    public Set<Object> sMembersSingle(String key) {
        try {
            Set<Object> result = singleRedisTemplate.opsForSet().members(key);
            log.debug("单机Redis Set获取所有元素成功 - key: {}, result: {}", key, result);
            return result;
        } catch (Exception e) {
            log.error("单机Redis Set获取所有元素失败 - key: {}", key, e);
            throw new RuntimeException("单机Redis Set获取所有元素失败", e);
        }
    }

    /**
     * 判断元素是否在Set中
     */
    public Boolean sIsMemberSingle(String key, Object value) {
        try {
            Boolean result = singleRedisTemplate.opsForSet().isMember(key, value);
            log.debug("单机Redis Set成员检查成功 - key: {}, value: {}, result: {}", key, value, result);
            return result;
        } catch (Exception e) {
            log.error("单机Redis Set成员检查失败 - key: {}, value: {}", key, value, e);
            throw new RuntimeException("单机Redis Set成员检查失败", e);
        }
    }

    /**
     * 获取Set的大小
     */
    public Long sCardSingle(String key) {
        try {
            Long result = singleRedisTemplate.opsForSet().size(key);
            log.debug("单机Redis Set获取大小成功 - key: {}, result: {}", key, result);
            return result;
        } catch (Exception e) {
            log.error("单机Redis Set获取大小失败 - key: {}", key, e);
            throw new RuntimeException("单机Redis Set获取大小失败", e);
        }
    }

    /**
     * 随机获取Set中的元素
     */
    public Object sRandMemberSingle(String key) {
        try {
            Object result = singleRedisTemplate.opsForSet().randomMember(key);
            log.debug("单机Redis Set随机获取元素成功 - key: {}, result: {}", key, result);
            return result;
        } catch (Exception e) {
            log.error("单机Redis Set随机获取元素失败 - key: {}", key, e);
            throw new RuntimeException("单机Redis Set随机获取元素失败", e);
        }
    }

    /**
     * 随机获取Set中的多个元素
     */
    public List<Object> sRandMembersSingle(String key, long count) {
        try {
            List<Object> result = singleRedisTemplate.opsForSet().randomMembers(key, count);
            log.debug("单机Redis Set随机获取多个元素成功 - key: {}, count: {}, result: {}", key, count, result);
            return result;
        } catch (Exception e) {
            log.error("单机Redis Set随机获取多个元素失败 - key: {}, count: {}", key, count, e);
            throw new RuntimeException("单机Redis Set随机获取多个元素失败", e);
        }
    }

    /**
     * 弹出Set中的元素
     */
    public Object sPopSingle(String key) {
        try {
            Object result = singleRedisTemplate.opsForSet().pop(key);
            log.debug("单机Redis Set弹出元素成功 - key: {}, result: {}", key, result);
            return result;
        } catch (Exception e) {
            log.error("单机Redis Set弹出元素失败 - key: {}", key, e);
            throw new RuntimeException("单机Redis Set弹出元素失败", e);
        }
    }

    /**
     * 弹出Set中的多个元素
     */
    public List<Object> sPopSingle(String key, long count) {
        try {
            List<Object> result = singleRedisTemplate.opsForSet().pop(key, count);
            log.debug("单机Redis Set弹出多个元素成功 - key: {}, count: {}, result: {}", key, count, result);
            return result;
        } catch (Exception e) {
            log.error("单机Redis Set弹出多个元素失败 - key: {}, count: {}", key, count, e);
            throw new RuntimeException("单机Redis Set弹出多个元素失败", e);
        }
    }

    /**
     * 移动元素到另一个Set
     */
    public Boolean sMoveSingle(String sourceKey, String destinationKey, Object value) {
        try {
            Boolean result = singleRedisTemplate.opsForSet().move(sourceKey, value, destinationKey);
            log.debug("单机Redis Set移动元素成功 - sourceKey: {}, destinationKey: {}, value: {}, result: {}", 
                    sourceKey, destinationKey, value, result);
            return result;
        } catch (Exception e) {
            log.error("单机Redis Set移动元素失败 - sourceKey: {}, destinationKey: {}, value: {}", 
                    sourceKey, destinationKey, value, e);
            throw new RuntimeException("单机Redis Set移动元素失败", e);
        }
    }

    // ==================== 集群Redis操作 ====================

//    /**
//     * 添加元素到Set
//     */
//    public Long sAddCluster(String key, Object... values) {
//        try {
//            Long result = clusterRedisTemplate.opsForSet().add(key, values);
//            log.debug("集群Redis Set添加成功 - key: {}, values: {}, result: {}", key, values, result);
//            return result;
//        } catch (Exception e) {
//            log.error("集群Redis Set添加失败 - key: {}, values: {}", key, values, e);
//            throw new RuntimeException("集群Redis Set添加失败", e);
//        }
//    }
//
//    /**
//     * 从Set中移除元素
//     */
//    public Long sRemCluster(String key, Object... values) {
//        try {
//            Long result = clusterRedisTemplate.opsForSet().remove(key, values);
//            log.debug("集群Redis Set移除成功 - key: {}, values: {}, result: {}", key, values, result);
//            return result;
//        } catch (Exception e) {
//            log.error("集群Redis Set移除失败 - key: {}, values: {}", key, values, e);
//            throw new RuntimeException("集群Redis Set移除失败", e);
//        }
//    }
//
//    /**
//     * 获取Set中的所有元素
//     */
//    public Set<Object> sMembersCluster(String key) {
//        try {
//            Set<Object> result = clusterRedisTemplate.opsForSet().members(key);
//            log.debug("集群Redis Set获取所有元素成功 - key: {}, result: {}", key, result);
//            return result;
//        } catch (Exception e) {
//            log.error("集群Redis Set获取所有元素失败 - key: {}", key, e);
//            throw new RuntimeException("集群Redis Set获取所有元素失败", e);
//        }
//    }
//
//    /**
//     * 判断元素是否在Set中
//     */
//    public Boolean sIsMemberCluster(String key, Object value) {
//        try {
//            Boolean result = clusterRedisTemplate.opsForSet().isMember(key, value);
//            log.debug("集群Redis Set成员检查成功 - key: {}, value: {}, result: {}", key, value, result);
//            return result;
//        } catch (Exception e) {
//            log.error("集群Redis Set成员检查失败 - key: {}, value: {}", key, value, e);
//            throw new RuntimeException("集群Redis Set成员检查失败", e);
//        }
//    }
//
//    /**
//     * 获取Set的大小
//     */
//    public Long sCardCluster(String key) {
//        try {
//            Long result = clusterRedisTemplate.opsForSet().size(key);
//            log.debug("集群Redis Set获取大小成功 - key: {}, result: {}", key, result);
//            return result;
//        } catch (Exception e) {
//            log.error("集群Redis Set获取大小失败 - key: {}", key, e);
//            throw new RuntimeException("集群Redis Set获取大小失败", e);
//        }
//    }
//
//    /**
//     * 随机获取Set中的元素
//     */
//    public Object sRandMemberCluster(String key) {
//        try {
//            Object result = clusterRedisTemplate.opsForSet().randomMember(key);
//            log.debug("集群Redis Set随机获取元素成功 - key: {}, result: {}", key, result);
//            return result;
//        } catch (Exception e) {
//            log.error("集群Redis Set随机获取元素失败 - key: {}", key, e);
//            throw new RuntimeException("集群Redis Set随机获取元素失败", e);
//        }
//    }
//
//    /**
//     * 随机获取Set中的多个元素
//     */
//    public List<Object> sRandMembersCluster(String key, long count) {
//        try {
//            List<Object> result = clusterRedisTemplate.opsForSet().randomMembers(key, count);
//            log.debug("集群Redis Set随机获取多个元素成功 - key: {}, count: {}, result: {}", key, count, result);
//            return result;
//        } catch (Exception e) {
//            log.error("集群Redis Set随机获取多个元素失败 - key: {}, count: {}", key, count, e);
//            throw new RuntimeException("集群Redis Set随机获取多个元素失败", e);
//        }
//    }
//
//    /**
//     * 弹出Set中的元素
//     */
//    public Object sPopCluster(String key) {
//        try {
//            Object result = clusterRedisTemplate.opsForSet().pop(key);
//            log.debug("集群Redis Set弹出元素成功 - key: {}, result: {}", key, result);
//            return result;
//        } catch (Exception e) {
//            log.error("集群Redis Set弹出元素失败 - key: {}", key, e);
//            throw new RuntimeException("集群Redis Set弹出元素失败", e);
//        }
//    }
//
//    /**
//     * 弹出Set中的多个元素
//     */
//    public List<Object> sPopCluster(String key, long count) {
//        try {
//            List<Object> result = clusterRedisTemplate.opsForSet().pop(key, count);
//            log.debug("集群Redis Set弹出多个元素成功 - key: {}, count: {}, result: {}", key, count, result);
//            return result;
//        } catch (Exception e) {
//            log.error("集群Redis Set弹出多个元素失败 - key: {}, count: {}", key, count, e);
//            throw new RuntimeException("集群Redis Set弹出多个元素失败", e);
//        }
//    }
//
//    /**
//     * 移动元素到另一个Set
//     */
//    public Boolean sMoveCluster(String sourceKey, String destinationKey, Object value) {
//        try {
//            Boolean result = clusterRedisTemplate.opsForSet().move(sourceKey, value, destinationKey);
//            log.debug("集群Redis Set移动元素成功 - sourceKey: {}, destinationKey: {}, value: {}, result: {}",
//                    sourceKey, destinationKey, value, result);
//            return result;
//        } catch (Exception e) {
//            log.error("集群Redis Set移动元素失败 - sourceKey: {}, destinationKey: {}, value: {}",
//                    sourceKey, destinationKey, value, e);
//            throw new RuntimeException("集群Redis Set移动元素失败", e);
//        }
//    }
//
//    // ==================== 多节点Redis操作 ====================
//
//    /**
//     * 添加元素到Set
//     */
//    public Long sAddMultiNode(String key, Object... values) {
//        try {
//            Long result = multiNodeRedisTemplate.opsForSet().add(key, values);
//            log.debug("多节点Redis Set添加成功 - key: {}, values: {}, result: {}", key, values, result);
//            return result;
//        } catch (Exception e) {
//            log.error("多节点Redis Set添加失败 - key: {}, values: {}", key, values, e);
//            throw new RuntimeException("多节点Redis Set添加失败", e);
//        }
//    }
//
//    /**
//     * 从Set中移除元素
//     */
//    public Long sRemMultiNode(String key, Object... values) {
//        try {
//            Long result = multiNodeRedisTemplate.opsForSet().remove(key, values);
//            log.debug("多节点Redis Set移除成功 - key: {}, values: {}, result: {}", key, values, result);
//            return result;
//        } catch (Exception e) {
//            log.error("多节点Redis Set移除失败 - key: {}, values: {}", key, values, e);
//            throw new RuntimeException("多节点Redis Set移除失败", e);
//        }
//    }
//
//    /**
//     * 获取Set中的所有元素
//     */
//    public Set<Object> sMembersMultiNode(String key) {
//        try {
//            Set<Object> result = multiNodeRedisTemplate.opsForSet().members(key);
//            log.debug("多节点Redis Set获取所有元素成功 - key: {}, result: {}", key, result);
//            return result;
//        } catch (Exception e) {
//            log.error("多节点Redis Set获取所有元素失败 - key: {}", key, e);
//            throw new RuntimeException("多节点Redis Set获取所有元素失败", e);
//        }
//    }
//
//    /**
//     * 判断元素是否在Set中
//     */
//    public Boolean sIsMemberMultiNode(String key, Object value) {
//        try {
//            Boolean result = multiNodeRedisTemplate.opsForSet().isMember(key, value);
//            log.debug("多节点Redis Set成员检查成功 - key: {}, value: {}, result: {}", key, value, result);
//            return result;
//        } catch (Exception e) {
//            log.error("多节点Redis Set成员检查失败 - key: {}, value: {}", key, value, e);
//            throw new RuntimeException("多节点Redis Set成员检查失败", e);
//        }
//    }
//
//    /**
//     * 获取Set的大小
//     */
//    public Long sCardMultiNode(String key) {
//        try {
//            Long result = multiNodeRedisTemplate.opsForSet().size(key);
//            log.debug("多节点Redis Set获取大小成功 - key: {}, result: {}", key, result);
//            return result;
//        } catch (Exception e) {
//            log.error("多节点Redis Set获取大小失败 - key: {}", key, e);
//            throw new RuntimeException("多节点Redis Set获取大小失败", e);
//        }
//    }
//
//    /**
//     * 随机获取Set中的元素
//     */
//    public Object sRandMemberMultiNode(String key) {
//        try {
//            Object result = multiNodeRedisTemplate.opsForSet().randomMember(key);
//            log.debug("多节点Redis Set随机获取元素成功 - key: {}, result: {}", key, result);
//            return result;
//        } catch (Exception e) {
//            log.error("多节点Redis Set随机获取元素失败 - key: {}", key, e);
//            throw new RuntimeException("多节点Redis Set随机获取元素失败", e);
//        }
//    }
//
//    /**
//     * 随机获取Set中的多个元素
//     */
//    public List<Object> sRandMembersMultiNode(String key, long count) {
//        try {
//            List<Object> result = multiNodeRedisTemplate.opsForSet().randomMembers(key, count);
//            log.debug("多节点Redis Set随机获取多个元素成功 - key: {}, count: {}, result: {}", key, count, result);
//            return result;
//        } catch (Exception e) {
//            log.error("多节点Redis Set随机获取多个元素失败 - key: {}, count: {}", key, count, e);
//            throw new RuntimeException("多节点Redis Set随机获取多个元素失败", e);
//        }
//    }
//
//    /**
//     * 弹出Set中的元素
//     */
//    public Object sPopMultiNode(String key) {
//        try {
//            Object result = multiNodeRedisTemplate.opsForSet().pop(key);
//            log.debug("多节点Redis Set弹出元素成功 - key: {}, result: {}", key, result);
//            return result;
//        } catch (Exception e) {
//            log.error("多节点Redis Set弹出元素失败 - key: {}", key, e);
//            throw new RuntimeException("多节点Redis Set弹出元素失败", e);
//        }
//    }
//
//    /**
//     * 弹出Set中的多个元素
//     */
//    public List<Object> sPopMultiNode(String key, long count) {
//        try {
//            List<Object> result = multiNodeRedisTemplate.opsForSet().pop(key, count);
//            log.debug("多节点Redis Set弹出多个元素成功 - key: {}, count: {}, result: {}", key, count, result);
//            return result;
//        } catch (Exception e) {
//            log.error("多节点Redis Set弹出多个元素失败 - key: {}, count: {}", key, count, e);
//            throw new RuntimeException("多节点Redis Set弹出多个元素失败", e);
//        }
//    }
//
//    /**
//     * 移动元素到另一个Set
//     */
//    public Boolean sMoveMultiNode(String sourceKey, String destinationKey, Object value) {
//        try {
//            Boolean result = multiNodeRedisTemplate.opsForSet().move(sourceKey, value, destinationKey);
//            log.debug("多节点Redis Set移动元素成功 - sourceKey: {}, destinationKey: {}, value: {}, result: {}",
//                    sourceKey, destinationKey, value, result);
//            return result;
//        } catch (Exception e) {
//            log.error("多节点Redis Set移动元素失败 - sourceKey: {}, destinationKey: {}, value: {}",
//                    sourceKey, destinationKey, value, e);
//            throw new RuntimeException("多节点Redis Set移动元素失败", e);
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
//    public Long sAdd(String key, Object[] values, RedisStringUtils.RedisType redisType) {
//        switch (redisType) {
//            case SINGLE:
//                return sAddSingle(key, values);
//            case CLUSTER:
//                return sAddCluster(key, values);
//            case MULTI_NODE:
//                return sAddMultiNode(key, values);
//            default:
//                throw new IllegalArgumentException("不支持的Redis类型: " + redisType);
//        }
//    }
//
//    /**
//     * 通用移除元素方法
//     */
//    public Long sRem(String key, Object[] values, RedisStringUtils.RedisType redisType) {
//        switch (redisType) {
//            case SINGLE:
//                return sRemSingle(key, values);
//            case CLUSTER:
//                return sRemCluster(key, values);
//            case MULTI_NODE:
//                return sRemMultiNode(key, values);
//            default:
//                throw new IllegalArgumentException("不支持的Redis类型: " + redisType);
//        }
//    }
//
//    /**
//     * 通用获取所有元素方法
//     */
//    public Set<Object> sMembers(String key, RedisStringUtils.RedisType redisType) {
//        switch (redisType) {
//            case SINGLE:
//                return sMembersSingle(key);
//            case CLUSTER:
//                return sMembersCluster(key);
//            case MULTI_NODE:
//                return sMembersMultiNode(key);
//            default:
//                throw new IllegalArgumentException("不支持的Redis类型: " + redisType);
//        }
//    }
//
//    /**
//     * 通用成员检查方法
//     */
//    public Boolean sIsMember(String key, Object value, RedisStringUtils.RedisType redisType) {
//        switch (redisType) {
//            case SINGLE:
//                return sIsMemberSingle(key, value);
//            case CLUSTER:
//                return sIsMemberCluster(key, value);
//            case MULTI_NODE:
//                return sIsMemberMultiNode(key, value);
//            default:
//                throw new IllegalArgumentException("不支持的Redis类型: " + redisType);
//        }
//    }
} 