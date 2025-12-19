package com.practise.demo.utils;
//
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.data.redis.core.RedisTemplate;
//import org.springframework.stereotype.Component;
//
//import java.util.Collection;
//import java.util.List;
//import java.util.Map;
//import java.util.Set;
//import java.util.concurrent.TimeUnit;
//
///**
// * Redis综合工具类
// * 整合所有数据结构的操作，提供统一的接口
// */
//@Slf4j
//@Component
//public class RedisUtils {
//
//    @Autowired
//    private RedisStringUtils stringUtils;
//
//    @Autowired
//    private RedisListUtils listUtils;
//
//    @Autowired
//    private RedisSetUtils setUtils;
//
//    @Autowired
//    private RedisZSetUtils zSetUtils;
//
//    @Autowired
//    private RedisHashUtils hashUtils;
//
//    // ==================== String操作 ====================
//
//    /**
//     * 设置键值对
//     */
//    public void set(String key, Object value, RedisStringUtils.RedisType redisType) {
//        stringUtils.set(key, value, redisType);
//    }
//
//    /**
//     * 设置键值对（带过期时间）
//     */
//    public void set(String key, Object value, long timeout, TimeUnit unit, RedisStringUtils.RedisType redisType) {
//        switch (redisType) {
//            case SINGLE:
//                stringUtils.setSingle(key, value, timeout, unit);
//                break;
//            case CLUSTER:
//                stringUtils.setCluster(key, value, timeout, unit);
//                break;
//            case MULTI_NODE:
//                stringUtils.setMultiNode(key, value, timeout, unit);
//                break;
//            default:
//                throw new IllegalArgumentException("不支持的Redis类型: " + redisType);
//        }
//    }
//
//    /**
//     * 获取值
//     */
//    public Object get(String key, RedisStringUtils.RedisType redisType) {
//        return stringUtils.get(key, redisType);
//    }
//
//    /**
//     * 删除键
//     */
//    public Boolean delete(String key, RedisStringUtils.RedisType redisType) {
//        switch (redisType) {
//            case SINGLE:
//                return stringUtils.deleteSingle(key);
//            case CLUSTER:
//                return stringUtils.deleteCluster(key);
//            case MULTI_NODE:
//                return stringUtils.deleteMultiNode(key);
//            default:
//                throw new IllegalArgumentException("不支持的Redis类型: " + redisType);
//        }
//    }
//
//    /**
//     * 批量设置
//     */
//    public void multiSet(Map<String, Object> map, RedisStringUtils.RedisType redisType) {
//        switch (redisType) {
//            case SINGLE:
//                stringUtils.multiSetSingle(map);
//                break;
//            case CLUSTER:
//                stringUtils.multiSetCluster(map);
//                break;
//            case MULTI_NODE:
//                stringUtils.multiSetMultiNode(map);
//                break;
//            default:
//                throw new IllegalArgumentException("不支持的Redis类型: " + redisType);
//        }
//    }
//
//    /**
//     * 批量获取
//     */
//    public List<Object> multiGet(Collection<String> keys, RedisStringUtils.RedisType redisType) {
//        switch (redisType) {
//            case SINGLE:
//                return stringUtils.multiGetSingle(keys);
//            case CLUSTER:
//                return stringUtils.multiGetCluster(keys);
//            case MULTI_NODE:
//                return stringUtils.multiGetMultiNode(keys);
//            default:
//                throw new IllegalArgumentException("不支持的Redis类型: " + redisType);
//        }
//    }
//
//    /**
//     * 设置过期时间
//     */
//    public Boolean expire(String key, long timeout, TimeUnit unit, RedisStringUtils.RedisType redisType) {
//        switch (redisType) {
//            case SINGLE:
//                return stringUtils.expireSingle(key, timeout, unit);
//            case CLUSTER:
//                return stringUtils.expireCluster(key, timeout, unit);
//            case MULTI_NODE:
//                return stringUtils.expireMultiNode(key, timeout, unit);
//            default:
//                throw new IllegalArgumentException("不支持的Redis类型: " + redisType);
//        }
//    }
//
//    /**
//     * 获取过期时间
//     */
//    public Long getExpire(String key, TimeUnit unit, RedisStringUtils.RedisType redisType) {
//        switch (redisType) {
//            case SINGLE:
//                return stringUtils.getExpireSingle(key, unit);
//            case CLUSTER:
//                return stringUtils.getExpireCluster(key, unit);
//            case MULTI_NODE:
//                return stringUtils.getExpireMultiNode(key, unit);
//            default:
//                throw new IllegalArgumentException("不支持的Redis类型: " + redisType);
//        }
//    }
//
//    /**
//     * 判断键是否存在
//     */
//    public Boolean hasKey(String key, RedisStringUtils.RedisType redisType) {
//        switch (redisType) {
//            case SINGLE:
//                return stringUtils.hasKeySingle(key);
//            case CLUSTER:
//                return stringUtils.hasKeyCluster(key);
//            case MULTI_NODE:
//                return stringUtils.hasKeyMultiNode(key);
//            default:
//                throw new IllegalArgumentException("不支持的Redis类型: " + redisType);
//        }
//    }
//
//    // ==================== List操作 ====================
//
//    /**
//     * 左推入元素
//     */
//    public Long lPush(String key, Object[] values, RedisStringUtils.RedisType redisType) {
//        return listUtils.lPush(key, values, redisType);
//    }
//
//    /**
//     * 右推入元素
//     */
//    public Long rPush(String key, Object[] values, RedisStringUtils.RedisType redisType) {
//        return listUtils.rPush(key, values, redisType);
//    }
//
//    /**
//     * 左弹出元素
//     */
//    public Object lPop(String key, RedisStringUtils.RedisType redisType) {
//        return listUtils.lPop(key, redisType);
//    }
//
//    /**
//     * 右弹出元素
//     */
//    public Object rPop(String key, RedisStringUtils.RedisType redisType) {
//        return listUtils.rPop(key, redisType);
//    }
//
//    /**
//     * 获取列表长度
//     */
//    public Long lLen(String key, RedisStringUtils.RedisType redisType) {
//        switch (redisType) {
//            case SINGLE:
//                return listUtils.lLenSingle(key);
//            case CLUSTER:
//                return listUtils.lLenCluster(key);
//            case MULTI_NODE:
//                return listUtils.lLenMultiNode(key);
//            default:
//                throw new IllegalArgumentException("不支持的Redis类型: " + redisType);
//        }
//    }
//
//    /**
//     * 获取指定范围的元素
//     */
//    public List<Object> lRange(String key, long start, long end, RedisStringUtils.RedisType redisType) {
//        switch (redisType) {
//            case SINGLE:
//                return listUtils.lRangeSingle(key, start, end);
//            case CLUSTER:
//                return listUtils.lRangeCluster(key, start, end);
//            case MULTI_NODE:
//                return listUtils.lRangeMultiNode(key, start, end);
//            default:
//                throw new IllegalArgumentException("不支持的Redis类型: " + redisType);
//        }
//    }
//
//    /**
//     * 根据索引获取元素
//     */
//    public Object lIndex(String key, long index, RedisStringUtils.RedisType redisType) {
//        switch (redisType) {
//            case SINGLE:
//                return listUtils.lIndexSingle(key, index);
//            case CLUSTER:
//                return listUtils.lIndexCluster(key, index);
//            case MULTI_NODE:
//                return listUtils.lIndexMultiNode(key, index);
//            default:
//                throw new IllegalArgumentException("不支持的Redis类型: " + redisType);
//        }
//    }
//
//    /**
//     * 设置指定索引的元素
//     */
//    public void lSet(String key, long index, Object value, RedisStringUtils.RedisType redisType) {
//        switch (redisType) {
//            case SINGLE:
//                listUtils.lSetSingle(key, index, value);
//                break;
//            case CLUSTER:
//                listUtils.lSetCluster(key, index, value);
//                break;
//            case MULTI_NODE:
//                listUtils.lSetMultiNode(key, index, value);
//                break;
//            default:
//                throw new IllegalArgumentException("不支持的Redis类型: " + redisType);
//        }
//    }
//
//    /**
//     * 删除指定值的元素
//     */
//    public Long lRem(String key, long count, Object value, RedisStringUtils.RedisType redisType) {
//        switch (redisType) {
//            case SINGLE:
//                return listUtils.lRemSingle(key, count, value);
//            case CLUSTER:
//                return listUtils.lRemCluster(key, count, value);
//            case MULTI_NODE:
//                return listUtils.lRemMultiNode(key, count, value);
//            default:
//                throw new IllegalArgumentException("不支持的Redis类型: " + redisType);
//        }
//    }
//
//    /**
//     * 修剪列表
//     */
//    public void lTrim(String key, long start, long end, RedisStringUtils.RedisType redisType) {
//        switch (redisType) {
//            case SINGLE:
//                listUtils.lTrimSingle(key, start, end);
//                break;
//            case CLUSTER:
//                listUtils.lTrimCluster(key, start, end);
//                break;
//            case MULTI_NODE:
//                listUtils.lTrimMultiNode(key, start, end);
//                break;
//            default:
//                throw new IllegalArgumentException("不支持的Redis类型: " + redisType);
//        }
//    }
//
//    // ==================== Set操作 ====================
//
//    /**
//     * 添加元素到Set
//     */
//    public Long sAdd(String key, Object[] values, RedisStringUtils.RedisType redisType) {
//        return setUtils.sAdd(key, values, redisType);
//    }
//
//    /**
//     * 从Set中移除元素
//     */
//    public Long sRem(String key, Object[] values, RedisStringUtils.RedisType redisType) {
//        return setUtils.sRem(key, values, redisType);
//    }
//
//    /**
//     * 获取Set中的所有元素
//     */
//    public Set<Object> sMembers(String key, RedisStringUtils.RedisType redisType) {
//        return setUtils.sMembers(key, redisType);
//    }
//
//    /**
//     * 判断元素是否在Set中
//     */
//    public Boolean sIsMember(String key, Object value, RedisStringUtils.RedisType redisType) {
//        return setUtils.sIsMember(key, value, redisType);
//    }
//
//    /**
//     * 获取Set的大小
//     */
//    public Long sCard(String key, RedisStringUtils.RedisType redisType) {
//        switch (redisType) {
//            case SINGLE:
//                return setUtils.sCardSingle(key);
//            case CLUSTER:
//                return setUtils.sCardCluster(key);
//            case MULTI_NODE:
//                return setUtils.sCardMultiNode(key);
//            default:
//                throw new IllegalArgumentException("不支持的Redis类型: " + redisType);
//        }
//    }
//
//    /**
//     * 随机获取Set中的元素
//     */
//    public Object sRandMember(String key, RedisStringUtils.RedisType redisType) {
//        switch (redisType) {
//            case SINGLE:
//                return setUtils.sRandMemberSingle(key);
//            case CLUSTER:
//                return setUtils.sRandMemberCluster(key);
//            case MULTI_NODE:
//                return setUtils.sRandMemberMultiNode(key);
//            default:
//                throw new IllegalArgumentException("不支持的Redis类型: " + redisType);
//        }
//    }
//
//    /**
//     * 随机获取Set中的多个元素
//     */
//    public List<Object> sRandMembers(String key, long count, RedisStringUtils.RedisType redisType) {
//        switch (redisType) {
//            case SINGLE:
//                return setUtils.sRandMembersSingle(key, count);
//            case CLUSTER:
//                return setUtils.sRandMembersCluster(key, count);
//            case MULTI_NODE:
//                return setUtils.sRandMembersMultiNode(key, count);
//            default:
//                throw new IllegalArgumentException("不支持的Redis类型: " + redisType);
//        }
//    }
//
//    /**
//     * 弹出Set中的元素
//     */
//    public Object sPop(String key, RedisStringUtils.RedisType redisType) {
//        switch (redisType) {
//            case SINGLE:
//                return setUtils.sPopSingle(key);
//            case CLUSTER:
//                return setUtils.sPopCluster(key);
//            case MULTI_NODE:
//                return setUtils.sPopMultiNode(key);
//            default:
//                throw new IllegalArgumentException("不支持的Redis类型: " + redisType);
//        }
//    }
//
//    /**
//     * 弹出Set中的多个元素
//     */
//    public List<Object> sPop(String key, long count, RedisStringUtils.RedisType redisType) {
//        switch (redisType) {
//            case SINGLE:
//                return setUtils.sPopSingle(key, count);
//            case CLUSTER:
//                return setUtils.sPopCluster(key, count);
//            case MULTI_NODE:
//                return setUtils.sPopMultiNode(key, count);
//            default:
//                throw new IllegalArgumentException("不支持的Redis类型: " + redisType);
//        }
//    }
//
//    /**
//     * 移动元素到另一个Set
//     */
//    public Boolean sMove(String sourceKey, String destinationKey, Object value, RedisStringUtils.RedisType redisType) {
//        switch (redisType) {
//            case SINGLE:
//                return setUtils.sMoveSingle(sourceKey, destinationKey, value);
//            case CLUSTER:
//                return setUtils.sMoveCluster(sourceKey, destinationKey, value);
//            case MULTI_NODE:
//                return setUtils.sMoveMultiNode(sourceKey, destinationKey, value);
//            default:
//                throw new IllegalArgumentException("不支持的Redis类型: " + redisType);
//        }
//    }
//
//    // ==================== ZSet操作 ====================
//
//    /**
//     * 添加元素到ZSet
//     */
//    public Boolean zAdd(String key, Object value, double score, RedisStringUtils.RedisType redisType) {
//        return zSetUtils.zAdd(key, value, score, redisType);
//    }
//
//    /**
//     * 从ZSet中移除元素
//     */
//    public Long zRem(String key, Object[] values, RedisStringUtils.RedisType redisType) {
//        return zSetUtils.zRem(key, values, redisType);
//    }
//
//    /**
//     * 获取ZSet的大小
//     */
//    public Long zCard(String key, RedisStringUtils.RedisType redisType) {
//        return zSetUtils.zCard(key, redisType);
//    }
//
//    /**
//     * 获取元素的分数
//     */
//    public Double zScore(String key, Object value, RedisStringUtils.RedisType redisType) {
//        return zSetUtils.zScore(key, value, redisType);
//    }
//
//    /**
//     * 获取元素的排名（从小到大）
//     */
//    public Long zRank(String key, Object value, RedisStringUtils.RedisType redisType) {
//        switch (redisType) {
//            case SINGLE:
//                return zSetUtils.zRankSingle(key, value);
//            case CLUSTER:
//                return zSetUtils.zRankCluster(key, value);
//            case MULTI_NODE:
//                return zSetUtils.zRankMultiNode(key, value);
//            default:
//                throw new IllegalArgumentException("不支持的Redis类型: " + redisType);
//        }
//    }
//
//    /**
//     * 获取元素的排名（从大到小）
//     */
//    public Long zRevRank(String key, Object value, RedisStringUtils.RedisType redisType) {
//        switch (redisType) {
//            case SINGLE:
//                return zSetUtils.zRevRankSingle(key, value);
//            case CLUSTER:
//                return zSetUtils.zRevRankCluster(key, value);
//            case MULTI_NODE:
//                return zSetUtils.zRevRankMultiNode(key, value);
//            default:
//                throw new IllegalArgumentException("不支持的Redis类型: " + redisType);
//        }
//    }
//
//    /**
//     * 根据分数范围获取元素（从小到大）
//     */
//    public Set<Object> zRangeByScore(String key, double min, double max, RedisStringUtils.RedisType redisType) {
//        switch (redisType) {
//            case SINGLE:
//                return zSetUtils.zRangeByScoreSingle(key, min, max);
//            case CLUSTER:
//                return zSetUtils.zRangeByScoreCluster(key, min, max);
//            case MULTI_NODE:
//                return zSetUtils.zRangeByScoreMultiNode(key, min, max);
//            default:
//                throw new IllegalArgumentException("不支持的Redis类型: " + redisType);
//        }
//    }
//
//    /**
//     * 根据分数范围获取元素（从大到小）
//     */
//    public Set<Object> zRevRangeByScore(String key, double min, double max, RedisStringUtils.RedisType redisType) {
//        switch (redisType) {
//            case SINGLE:
//                return zSetUtils.zRevRangeByScoreSingle(key, min, max);
//            case CLUSTER:
//                return zSetUtils.zRevRangeByScoreCluster(key, min, max);
//            case MULTI_NODE:
//                return zSetUtils.zRevRangeByScoreMultiNode(key, min, max);
//            default:
//                throw new IllegalArgumentException("不支持的Redis类型: " + redisType);
//        }
//    }
//
//    /**
//     * 根据排名范围获取元素（从小到大）
//     */
//    public Set<Object> zRange(String key, long start, long end, RedisStringUtils.RedisType redisType) {
//        switch (redisType) {
//            case SINGLE:
//                return zSetUtils.zRangeSingle(key, start, end);
//            case CLUSTER:
//                return zSetUtils.zRangeCluster(key, start, end);
//            case MULTI_NODE:
//                return zSetUtils.zRangeMultiNode(key, start, end);
//            default:
//                throw new IllegalArgumentException("不支持的Redis类型: " + redisType);
//        }
//    }
//
//    /**
//     * 根据排名范围获取元素（从大到小）
//     */
//    public Set<Object> zRevRange(String key, long start, long end, RedisStringUtils.RedisType redisType) {
//        switch (redisType) {
//            case SINGLE:
//                return zSetUtils.zRevRangeSingle(key, start, end);
//            case CLUSTER:
//                return zSetUtils.zRevRangeCluster(key, start, end);
//            case MULTI_NODE:
//                return zSetUtils.zRevRangeMultiNode(key, start, end);
//            default:
//                throw new IllegalArgumentException("不支持的Redis类型: " + redisType);
//        }
//    }
//
//    /**
//     * 增加元素的分数
//     */
//    public Double zIncrBy(String key, Object value, double delta, RedisStringUtils.RedisType redisType) {
//        switch (redisType) {
//            case SINGLE:
//                return zSetUtils.zIncrBySingle(key, value, delta);
//            case CLUSTER:
//                return zSetUtils.zIncrByCluster(key, value, delta);
//            case MULTI_NODE:
//                return zSetUtils.zIncrByMultiNode(key, value, delta);
//            default:
//                throw new IllegalArgumentException("不支持的Redis类型: " + redisType);
//        }
//    }
//
//    // ==================== Hash操作 ====================
//
//    /**
//     * 设置Hash字段值
//     */
//    public void hSet(String key, String field, Object value, RedisStringUtils.RedisType redisType) {
//        hashUtils.hSet(key, field, value, redisType);
//    }
//
//    /**
//     * 批量设置Hash字段值
//     */
//    public void hMSet(String key, Map<String, Object> map, RedisStringUtils.RedisType redisType) {
//        hashUtils.hMSet(key, map, redisType);
//    }
//
//    /**
//     * 获取Hash字段值
//     */
//    public Object hGet(String key, String field, RedisStringUtils.RedisType redisType) {
//        return hashUtils.hGet(key, field, redisType);
//    }
//
//    /**
//     * 批量获取Hash字段值
//     */
//    public List<Object> hMGet(String key, Collection<String> fields, RedisStringUtils.RedisType redisType) {
//        return hashUtils.hMGet(key, fields, redisType);
//    }
//
//    /**
//     * 获取Hash的所有字段和值
//     */
//    public Map<Object, Object> hGetAll(String key, RedisStringUtils.RedisType redisType) {
//        return hashUtils.hGetAll(key, redisType);
//    }
//
//    /**
//     * 获取Hash的所有字段名
//     */
//    public Set<Object> hKeys(String key, RedisStringUtils.RedisType redisType) {
//        switch (redisType) {
//            case SINGLE:
//                return hashUtils.hKeysSingle(key);
//            case CLUSTER:
//                return hashUtils.hKeysCluster(key);
//            case MULTI_NODE:
//                return hashUtils.hKeysMultiNode(key);
//            default:
//                throw new IllegalArgumentException("不支持的Redis类型: " + redisType);
//        }
//    }
//
//    /**
//     * 获取Hash的所有值
//     */
//    public List<Object> hVals(String key, RedisStringUtils.RedisType redisType) {
//        switch (redisType) {
//            case SINGLE:
//                return hashUtils.hValsSingle(key);
//            case CLUSTER:
//                return hashUtils.hValsCluster(key);
//            case MULTI_NODE:
//                return hashUtils.hValsMultiNode(key);
//            default:
//                throw new IllegalArgumentException("不支持的Redis类型: " + redisType);
//        }
//    }
//
//    /**
//     * 获取Hash的大小
//     */
//    public Long hLen(String key, RedisStringUtils.RedisType redisType) {
//        switch (redisType) {
//            case SINGLE:
//                return hashUtils.hLenSingle(key);
//            case CLUSTER:
//                return hashUtils.hLenCluster(key);
//            case MULTI_NODE:
//                return hashUtils.hLenMultiNode(key);
//            default:
//                throw new IllegalArgumentException("不支持的Redis类型: " + redisType);
//        }
//    }
//
//    /**
//     * 判断Hash字段是否存在
//     */
//    public Boolean hExists(String key, String field, RedisStringUtils.RedisType redisType) {
//        switch (redisType) {
//            case SINGLE:
//                return hashUtils.hExistsSingle(key, field);
//            case CLUSTER:
//                return hashUtils.hExistsCluster(key, field);
//            case MULTI_NODE:
//                return hashUtils.hExistsMultiNode(key, field);
//            default:
//                throw new IllegalArgumentException("不支持的Redis类型: " + redisType);
//        }
//    }
//
//    /**
//     * 删除Hash字段
//     */
//    public Long hDel(String key, Object[] fields, RedisStringUtils.RedisType redisType) {
//        switch (redisType) {
//            case SINGLE:
//                return hashUtils.hDelSingle(key, fields);
//            case CLUSTER:
//                return hashUtils.hDelCluster(key, fields);
//            case MULTI_NODE:
//                return hashUtils.hDelMultiNode(key, fields);
//            default:
//                throw new IllegalArgumentException("不支持的Redis类型: " + redisType);
//        }
//    }
//
//    /**
//     * 增加Hash字段的数值
//     */
//    public Long hIncrBy(String key, String field, long delta, RedisStringUtils.RedisType redisType) {
//        switch (redisType) {
//            case SINGLE:
//                return hashUtils.hIncrBySingle(key, field, delta);
//            case CLUSTER:
//                return hashUtils.hIncrByCluster(key, field, delta);
//            case MULTI_NODE:
//                return hashUtils.hIncrByMultiNode(key, field, delta);
//            default:
//                throw new IllegalArgumentException("不支持的Redis类型: " + redisType);
//        }
//    }
//
//    /**
//     * 增加Hash字段的浮点数值
//     */
//    public Double hIncrByFloat(String key, String field, double delta, RedisStringUtils.RedisType redisType) {
//        switch (redisType) {
//            case SINGLE:
//                return hashUtils.hIncrByFloatSingle(key, field, delta);
//            case CLUSTER:
//                return hashUtils.hIncrByFloatCluster(key, field, delta);
//            case MULTI_NODE:
//                return hashUtils.hIncrByFloatMultiNode(key, field, delta);
//            default:
//                throw new IllegalArgumentException("不支持的Redis类型: " + redisType);
//        }
//    }
//
//    // ==================== 工具方法 ====================
//
//    /**
//     * 获取Redis类型枚举
//     */
//    public RedisStringUtils.RedisType getRedisType(String type) {
//        if ("single".equalsIgnoreCase(type)) {
//            return RedisStringUtils.RedisType.SINGLE;
//        } else if ("cluster".equalsIgnoreCase(type)) {
//            return RedisStringUtils.RedisType.CLUSTER;
//        } else if ("multi_node".equalsIgnoreCase(type) || "sentinel".equalsIgnoreCase(type)) {
//            return RedisStringUtils.RedisType.MULTI_NODE;
//        } else {
//            throw new IllegalArgumentException("不支持的Redis类型: " + type);
//        }
//    }
//
//    /**
//     * 获取Redis模板
//     */
//    public RedisTemplate<String, Object> getRedisTemplate(RedisStringUtils.RedisType redisType) {
//        return stringUtils.getRedisTemplate(redisType);
//    }
//} 