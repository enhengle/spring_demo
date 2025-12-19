package com.practise.demo.utils;
//
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.stereotype.Component;
//
//import java.util.Arrays;
//import java.util.HashMap;
//import java.util.List;
//import java.util.Map;
//import java.util.concurrent.TimeUnit;
//import java.util.Set;
//
///**
// * Redis工具类测试类
// * 演示各种操作的使用方法
// */
//@Slf4j
//@Component
//public class RedisUtilsTest {
//
//    @Autowired
//    private RedisUtils redisUtils;
//
//    /**
//     * 测试String操作
//     */
//    public void testStringOperations() {
//        log.info("=== 测试String操作 ===");
//
//        // 单机Redis
//        redisUtils.set("test:string:single", "单机Redis测试值", RedisStringUtils.RedisType.SINGLE);
//        Object singleValue = redisUtils.get("test:string:single", RedisStringUtils.RedisType.SINGLE);
//        log.info("单机Redis获取值: {}", singleValue);
//
//        // 集群Redis
//        redisUtils.set("test:string:cluster", "集群Redis测试值", RedisStringUtils.RedisType.CLUSTER);
//        Object clusterValue = redisUtils.get("test:string:cluster", RedisStringUtils.RedisType.CLUSTER);
//        log.info("集群Redis获取值: {}", clusterValue);
//
//        // 多节点Redis
//        redisUtils.set("test:string:multinode", "多节点Redis测试值", RedisStringUtils.RedisType.MULTI_NODE);
//        Object multiNodeValue = redisUtils.get("test:string:multinode", RedisStringUtils.RedisType.MULTI_NODE);
//        log.info("多节点Redis获取值: {}", multiNodeValue);
//
//        // 设置过期时间
//        redisUtils.set("test:string:expire", "过期测试值", 10, TimeUnit.SECONDS, RedisStringUtils.RedisType.SINGLE);
//        log.info("设置过期时间成功");
//
//        // 批量操作
//        Map<String, Object> batchMap = new HashMap<>();
//        batchMap.put("test:batch:1", "批量值1");
//        batchMap.put("test:batch:2", "批量值2");
//        batchMap.put("test:batch:3", "批量值3");
//        redisUtils.multiSet(batchMap, RedisStringUtils.RedisType.SINGLE);
//
//        List<Object> batchValues = redisUtils.multiGet(Arrays.asList("test:batch:1", "test:batch:2", "test:batch:3"),
//                RedisStringUtils.RedisType.SINGLE);
//        log.info("批量获取值: {}", batchValues);
//    }
//
//    /**
//     * 测试List操作
//     */
//    public void testListOperations() {
//        log.info("=== 测试List操作 ===");
//
//        // 单机Redis
//        redisUtils.lPush("test:list:single", new Object[]{"列表值1", "列表值2", "列表值3"}, RedisStringUtils.RedisType.SINGLE);
//        Long singleLen = redisUtils.lLen("test:list:single", RedisStringUtils.RedisType.SINGLE);
//        log.info("单机Redis列表长度: {}", singleLen);
//
//        List<Object> singleRange = redisUtils.lRange("test:list:single", 0, -1, RedisStringUtils.RedisType.SINGLE);
//        log.info("单机Redis列表范围: {}", singleRange);
//
//        Object singlePop = redisUtils.lPop("test:list:single", RedisStringUtils.RedisType.SINGLE);
//        log.info("单机Redis左弹出: {}", singlePop);
//
//        // 集群Redis
//        redisUtils.rPush("test:list:cluster", new Object[]{"集群列表值1", "集群列表值2"}, RedisStringUtils.RedisType.CLUSTER);
//        Long clusterLen = redisUtils.lLen("test:list:cluster", RedisStringUtils.RedisType.CLUSTER);
//        log.info("集群Redis列表长度: {}", clusterLen);
//
//        // 多节点Redis
//        redisUtils.lPush("test:list:multinode", new Object[]{"多节点列表值1", "多节点列表值2"}, RedisStringUtils.RedisType.MULTI_NODE);
//        Object multiNodeIndex = redisUtils.lIndex("test:list:multinode", 0, RedisStringUtils.RedisType.MULTI_NODE);
//        log.info("多节点Redis索引0的值: {}", multiNodeIndex);
//    }
//
//    /**
//     * 测试Set操作
//     */
//    public void testSetOperations() {
//        log.info("=== 测试Set操作 ===");
//
//        // 单机Redis
//        redisUtils.sAdd("test:set:single", new Object[]{"集合值1", "集合值2", "集合值3"}, RedisStringUtils.RedisType.SINGLE);
//        Long singleSize = redisUtils.sCard("test:set:single", RedisStringUtils.RedisType.SINGLE);
//        log.info("单机Redis集合大小: {}", singleSize);
//
//        Set<Object> singleMembers = redisUtils.sMembers("test:set:single", RedisStringUtils.RedisType.SINGLE);
//        log.info("单机Redis集合成员: {}", singleMembers);
//
//        Boolean singleExists = redisUtils.sIsMember("test:set:single", "集合值1", RedisStringUtils.RedisType.SINGLE);
//        log.info("单机Redis成员存在检查: {}", singleExists);
//
//        // 集群Redis
//        redisUtils.sAdd("test:set:cluster", new Object[]{"集群集合值1", "集群集合值2"}, RedisStringUtils.RedisType.CLUSTER);
//        Object clusterRand = redisUtils.sRandMember("test:set:cluster", RedisStringUtils.RedisType.CLUSTER);
//        log.info("集群Redis随机成员: {}", clusterRand);
//
//        // 多节点Redis
//        redisUtils.sAdd("test:set:multinode", new Object[]{"多节点集合值1", "多节点集合值2"}, RedisStringUtils.RedisType.MULTI_NODE);
//        List<Object> multiNodeRand = redisUtils.sRandMembers("test:set:multinode", 2, RedisStringUtils.RedisType.MULTI_NODE);
//        log.info("多节点Redis随机多个成员: {}", multiNodeRand);
//    }
//
//    /**
//     * 测试ZSet操作
//     */
//    public void testZSetOperations() {
//        log.info("=== 测试ZSet操作 ===");
//
//        // 单机Redis
//        redisUtils.zAdd("test:zset:single", "有序集合值1", 1.0, RedisStringUtils.RedisType.SINGLE);
//        redisUtils.zAdd("test:zset:single", "有序集合值2", 2.0, RedisStringUtils.RedisType.SINGLE);
//        redisUtils.zAdd("test:zset:single", "有序集合值3", 3.0, RedisStringUtils.RedisType.SINGLE);
//
//        Long singleSize = redisUtils.zCard("test:zset:single", RedisStringUtils.RedisType.SINGLE);
//        log.info("单机Redis有序集合大小: {}", singleSize);
//
//        Double singleScore = redisUtils.zScore("test:zset:single", "有序集合值1", RedisStringUtils.RedisType.SINGLE);
//        log.info("单机Redis值1的分数: {}", singleScore);
//
//        Long singleRank = redisUtils.zRank("test:zset:single", "有序集合值2", RedisStringUtils.RedisType.SINGLE);
//        log.info("单机Redis值2的排名: {}", singleRank);
//
//        Set<Object> singleRange = redisUtils.zRange("test:zset:single", 0, -1, RedisStringUtils.RedisType.SINGLE);
//        log.info("单机Redis有序集合范围: {}", singleRange);
//
//        // 集群Redis
//        redisUtils.zAdd("test:zset:cluster", "集群有序集合值1", 10.0, RedisStringUtils.RedisType.CLUSTER);
//        Double clusterScore = redisUtils.zScore("test:zset:cluster", "集群有序集合值1", RedisStringUtils.RedisType.CLUSTER);
//        log.info("集群Redis值1的分数: {}", clusterScore);
//
//        // 多节点Redis
//        redisUtils.zAdd("test:zset:multinode", "多节点有序集合值1", 100.0, RedisStringUtils.RedisType.MULTI_NODE);
//        Long multiNodeRank = redisUtils.zRevRank("test:zset:multinode", "多节点有序集合值1", RedisStringUtils.RedisType.MULTI_NODE);
//        log.info("多节点Redis值1的倒序排名: {}", multiNodeRank);
//    }
//
//    /**
//     * 测试Hash操作
//     */
//    public void testHashOperations() {
//        log.info("=== 测试Hash操作 ===");
//
//        // 单机Redis
//        redisUtils.hSet("test:hash:single", "字段1", "哈希值1", RedisStringUtils.RedisType.SINGLE);
//        redisUtils.hSet("test:hash:single", "字段2", "哈希值2", RedisStringUtils.RedisType.SINGLE);
//
//        Object singleValue = redisUtils.hGet("test:hash:single", "字段1", RedisStringUtils.RedisType.SINGLE);
//        log.info("单机Redis哈希字段1的值: {}", singleValue);
//
//        Long singleSize = redisUtils.hLen("test:hash:single", RedisStringUtils.RedisType.SINGLE);
//        log.info("单机Redis哈希大小: {}", singleSize);
//
//        Boolean singleExists = redisUtils.hExists("test:hash:single", "字段1", RedisStringUtils.RedisType.SINGLE);
//        log.info("单机Redis字段1存在检查: {}", singleExists);
//
//        // 批量操作
//        Map<String, Object> batchMap = new HashMap<>();
//        batchMap.put("批量字段1", "批量哈希值1");
//        batchMap.put("批量字段2", "批量哈希值2");
//        redisUtils.hMSet("test:hash:single", batchMap, RedisStringUtils.RedisType.SINGLE);
//
//        List<Object> batchValues = redisUtils.hMGet("test:hash:single", Arrays.asList("批量字段1", "批量字段2"),
//                RedisStringUtils.RedisType.SINGLE);
//        log.info("单机Redis批量获取哈希值: {}", batchValues);
//
//        // 集群Redis
//        redisUtils.hSet("test:hash:cluster", "集群字段1", "集群哈希值1", RedisStringUtils.RedisType.CLUSTER);
//        Object clusterValue = redisUtils.hGet("test:hash:cluster", "集群字段1", RedisStringUtils.RedisType.CLUSTER);
//        log.info("集群Redis哈希字段1的值: {}", clusterValue);
//
//        // 多节点Redis
//        redisUtils.hSet("test:hash:multinode", "多节点字段1", "多节点哈希值1", RedisStringUtils.RedisType.MULTI_NODE);
//        Set<Object> multiNodeKeys = redisUtils.hKeys("test:hash:multinode", RedisStringUtils.RedisType.MULTI_NODE);
//        log.info("多节点Redis哈希字段名: {}", multiNodeKeys);
//
//        // 数值操作
//        redisUtils.hIncrBy("test:hash:single", "计数器", 1, RedisStringUtils.RedisType.SINGLE);
//        Long counter = redisUtils.hIncrBy("test:hash:single", "计数器", 5, RedisStringUtils.RedisType.SINGLE);
//        log.info("单机Redis计数器值: {}", counter);
//    }
//
//    /**
//     * 运行所有测试
//     */
//    public void runAllTests() {
//        try {
//            log.info("开始运行Redis工具类测试...");
//
//            testStringOperations();
//            testListOperations();
//            testSetOperations();
//            testZSetOperations();
//            testHashOperations();
//
//            log.info("所有Redis工具类测试完成！");
//        } catch (Exception e) {
//            log.error("Redis工具类测试过程中发生异常", e);
//        }
//    }
//
//    /**
//     * 清理测试数据
//     */
//    public void cleanupTestData() {
//        log.info("开始清理测试数据...");
//
//        try {
//            // 清理String测试数据
//            redisUtils.delete("test:string:single", RedisStringUtils.RedisType.SINGLE);
//            redisUtils.delete("test:string:cluster", RedisStringUtils.RedisType.CLUSTER);
//            redisUtils.delete("test:string:multinode", RedisStringUtils.RedisType.MULTI_NODE);
//            redisUtils.delete("test:string:expire", RedisStringUtils.RedisType.SINGLE);
//            redisUtils.delete("test:batch:1", RedisStringUtils.RedisType.SINGLE);
//            redisUtils.delete("test:batch:2", RedisStringUtils.RedisType.SINGLE);
//            redisUtils.delete("test:batch:3", RedisStringUtils.RedisType.SINGLE);
//
//            // 清理List测试数据
//            redisUtils.delete("test:list:single", RedisStringUtils.RedisType.SINGLE);
//            redisUtils.delete("test:list:cluster", RedisStringUtils.RedisType.CLUSTER);
//            redisUtils.delete("test:list:multinode", RedisStringUtils.RedisType.MULTI_NODE);
//
//            // 清理Set测试数据
//            redisUtils.delete("test:set:single", RedisStringUtils.RedisType.SINGLE);
//            redisUtils.delete("test:set:cluster", RedisStringUtils.RedisType.CLUSTER);
//            redisUtils.delete("test:set:multinode", RedisStringUtils.RedisType.MULTI_NODE);
//
//            // 清理ZSet测试数据
//            redisUtils.delete("test:zset:single", RedisStringUtils.RedisType.SINGLE);
//            redisUtils.delete("test:zset:cluster", RedisStringUtils.RedisType.CLUSTER);
//            redisUtils.delete("test:zset:multinode", RedisStringUtils.RedisType.MULTI_NODE);
//
//            // 清理Hash测试数据
//            redisUtils.delete("test:hash:single", RedisStringUtils.RedisType.SINGLE);
//            redisUtils.delete("test:hash:cluster", RedisStringUtils.RedisType.CLUSTER);
//            redisUtils.delete("test:hash:multinode", RedisStringUtils.RedisType.MULTI_NODE);
//
//            log.info("测试数据清理完成！");
//        } catch (Exception e) {
//            log.error("清理测试数据过程中发生异常", e);
//        }
//    }
//}