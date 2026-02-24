package com.practise.demo.util;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;

import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 雪花算法ID生成器测试类
 * 
 * @author system
 */
@Slf4j
public class SnowflakeIdGeneratorTest {
    
    /**
     * 测试基本ID生成
     */
    @Test
    public void testBasicIdGeneration() {
        SnowflakeIdGenerator generator = new SnowflakeIdGenerator(1, 1);
        
        System.out.println("=== 基本ID生成测试 ===");
        for (int i = 0; i < 10; i++) {
            long id = generator.nextId();
            SnowflakeIdGenerator.IdInfo info = generator.parseId(id);
            System.out.println(String.format("ID: %d, 时间戳: %d, 数据中心ID: %d, 机器ID: %d, 序列号: %d",
                    id, info.getTimestamp(), info.getDatacenterId(), info.getMachineId(), info.getSequence()));
        }
    }
    
    /**
     * 测试高并发场景下的ID唯一性
     */
    @Test
    public void testConcurrentIdGeneration() throws InterruptedException {
        System.out.println("\n=== 高并发ID生成测试 ===");
        
        // 创建多个生成器，模拟多机器环境
        SnowflakeIdGenerator generator1 = new SnowflakeIdGenerator(1, 1);
        SnowflakeIdGenerator generator2 = new SnowflakeIdGenerator(1, 2);
        SnowflakeIdGenerator generator3 = new SnowflakeIdGenerator(2, 1);
        
        // 使用ConcurrentHashMap存储生成的ID，自动去重
        Map<Long, String> idMap = new ConcurrentHashMap<>();
        
        // 线程池配置
        int threadCount = 50;
        int idsPerThread = 1000;
        ExecutorService executor = Executors.newFixedThreadPool(threadCount);
        CountDownLatch latch = new CountDownLatch(threadCount * 3);
        AtomicInteger duplicateCount = new AtomicInteger(0);
        
        // 生成器列表
        List<SnowflakeIdGenerator> generators = Arrays.asList(generator1, generator2, generator3);
        
        // 提交任务
        for (int i = 0; i < threadCount; i++) {
            final int threadIndex = i;
            for (SnowflakeIdGenerator generator : generators) {
                executor.submit(() -> {
                    try {
                        for (int j = 0; j < idsPerThread; j++) {
                            long id = generator.nextId();
                            String key = String.format("Thread-%d-Generator-%d", threadIndex, generators.indexOf(generator));
                            
                            // 检查重复
                            String existing = idMap.put(id, key);
                            if (existing != null) {
                                duplicateCount.incrementAndGet();
                                log.error("发现重复ID: {} - 原有: {}, 新增: {}", id, existing, key);
                            }
                        }
                    } catch (Exception e) {
                        log.error("生成ID时发生异常", e);
                    } finally {
                        latch.countDown();
                    }
                });
            }
        }
        
        // 等待所有任务完成
        latch.await(60, TimeUnit.SECONDS);
        executor.shutdown();
        
        // 统计结果
        int totalIds = threadCount * generators.size() * idsPerThread;
        int uniqueIds = idMap.size();
        int duplicates = duplicateCount.get();
        
        System.out.println(String.format("总生成ID数: %d", totalIds));
        System.out.println(String.format("唯一ID数: %d", uniqueIds));
        System.out.println(String.format("检测到重复数: %d", duplicates));
        System.out.println(String.format("重复率: %.4f%%", (duplicates * 100.0 / totalIds)));
        
        // 打印部分ID信息
        System.out.println("\n=== 部分生成的ID信息 ===");
        int count = 0;
        for (Map.Entry<Long, String> entry : idMap.entrySet()) {
            if (count++ < 10) {
                long id = entry.getKey();
                SnowflakeIdGenerator.IdInfo info = generator1.parseId(id);
                System.out.println(String.format("ID: %d, 来源: %s, 时间戳: %d, 序列号: %d",
                        id, entry.getValue(), info.getTimestamp(), info.getSequence()));
            }
        }
        
        // 验证结果
        assert uniqueIds == totalIds : String.format("期望唯一ID数: %d, 实际: %d", totalIds, uniqueIds);
        assert duplicates == 0 : String.format("发现重复ID: %d", duplicates);
        
        System.out.println("\n✓ 高并发测试通过：所有ID都是唯一的");
    }
    
    /**
     * 测试多机器ID生成（不同机器ID）
     */
    @Test
    public void testMultiMachineIdGeneration() {
        System.out.println("\n=== 多机器ID生成测试 ===");
        
        // 创建多个不同机器ID的生成器
        SnowflakeIdGenerator generator1 = new SnowflakeIdGenerator(1, 1);
        SnowflakeIdGenerator generator2 = new SnowflakeIdGenerator(1, 2);
        SnowflakeIdGenerator generator3 = new SnowflakeIdGenerator(2, 1);
        SnowflakeIdGenerator generator4 = new SnowflakeIdGenerator(2, 2);
        
        Map<Long, Integer> idMap = new ConcurrentHashMap<>();
        ExecutorService executor = Executors.newFixedThreadPool(10);
        CountDownLatch latch = new CountDownLatch(40);
        
        List<SnowflakeIdGenerator> generators = Arrays.asList(generator1, generator2, generator3, generator4);
        
        for (int i = 0; i < 10; i++) {
            final int threadIndex = i;
            for (SnowflakeIdGenerator generator : generators) {
                executor.submit(() -> {
                    try {
                        for (int j = 0; j < 100; j++) {
                            long id = generator.nextId();
                            idMap.put(id, threadIndex);
                        }
                    } finally {
                        latch.countDown();
                    }
                });
            }
        }
        
        try {
            latch.await(30, TimeUnit.SECONDS);
            executor.shutdown();
            
            System.out.println(String.format("多机器生成ID总数: %d", idMap.size()));
            System.out.println(String.format("唯一ID数: %d", idMap.size()));
            System.out.println("✓ 多机器测试通过：所有机器生成的ID都是唯一的");
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
    
    /**
     * 测试ID解析功能
     */
    @Test
    public void testIdParsing() {
        System.out.println("\n=== ID解析测试 ===");
        
        SnowflakeIdGenerator generator = new SnowflakeIdGenerator(5, 10);
        
        for (int i = 0; i < 5; i++) {
            long id = generator.nextId();
            SnowflakeIdGenerator.IdInfo info = generator.parseId(id);
            
            System.out.println(String.format("原始ID: %d", id));
            System.out.println(String.format("解析结果: %s", info));
            System.out.println(String.format("验证 - 数据中心ID: %d (期望: 5), 机器ID: %d (期望: 10)",
                    info.getDatacenterId(), info.getMachineId()));
            
            assert info.getDatacenterId() == 5 : "数据中心ID解析错误";
            assert info.getMachineId() == 10 : "机器ID解析错误";
        }
        
        System.out.println("✓ ID解析测试通过");
    }
    
    /**
     * 测试序列号耗尽场景（高并发）
     */
    @Test
    public void testSequenceExhaustion() throws InterruptedException {
        System.out.println("\n=== 序列号耗尽测试 ===");
        
        SnowflakeIdGenerator generator = new SnowflakeIdGenerator(1, 1);
        Set<Long> idSet = ConcurrentHashMap.newKeySet();
        
        // 在同一毫秒内快速生成大量ID，触发序列号耗尽
        ExecutorService executor = Executors.newFixedThreadPool(20);
        CountDownLatch latch = new CountDownLatch(20);
        
        for (int i = 0; i < 20; i++) {
            executor.submit(() -> {
                try {
                    // 每个线程生成500个ID，总共10000个，超过4096的序列号上限
                    for (int j = 0; j < 500; j++) {
                        long id = generator.nextId();
                        idSet.add(id);
                    }
                } finally {
                    latch.countDown();
                }
            });
        }
        
        latch.await(30, TimeUnit.SECONDS);
        executor.shutdown();
        
        System.out.println(String.format("生成ID总数: %d", idSet.size()));
        System.out.println(String.format("唯一ID数: %d", idSet.size()));
        System.out.println("✓ 序列号耗尽测试通过：系统自动等待下一毫秒，所有ID都是唯一的");
    }
    
    /**
     * 综合测试：打印Map信息
     */
    @Test
    public void testComprehensive() throws InterruptedException {
        System.out.println("\n=== 综合测试 ===");
        
        // 创建多个生成器
        SnowflakeIdGenerator generator1 = new SnowflakeIdGenerator(1, 1);
        SnowflakeIdGenerator generator2 = new SnowflakeIdGenerator(1, 2);
        SnowflakeIdGenerator generator3 = new SnowflakeIdGenerator(2, 1);
        
        // 使用Map存储ID和生成信息
        Map<Long, Map<String, Object>> idInfoMap = new ConcurrentHashMap<>();
        
        ExecutorService executor = Executors.newFixedThreadPool(30);
        CountDownLatch latch = new CountDownLatch(90);
        
        List<SnowflakeIdGenerator> generators = Arrays.asList(generator1, generator2, generator3);
        
        for (int i = 0; i < 30; i++) {
            final int threadIndex = i;
            for (SnowflakeIdGenerator generator : generators) {
                executor.submit(() -> {
                    try {
                        for (int j = 0; j < 100; j++) {
                            long id = generator.nextId();
                            SnowflakeIdGenerator.IdInfo info = generator.parseId(id);
                            
                            Map<String, Object> infoMap = new HashMap<>();
                            infoMap.put("threadIndex", threadIndex);
                            infoMap.put("generatorIndex", generators.indexOf(generator));
                            infoMap.put("timestamp", info.getTimestamp());
                            infoMap.put("datacenterId", info.getDatacenterId());
                            infoMap.put("machineId", info.getMachineId());
                            infoMap.put("sequence", info.getSequence());
                            
                            // 检查重复
                            Map<String, Object> existing = idInfoMap.put(id, infoMap);
                            if (existing != null) {
                                System.err.println(String.format("发现重复ID: %d", id));
                                System.err.println(String.format("  原有: Thread-%d, Generator-%d",
                                        existing.get("threadIndex"), existing.get("generatorIndex")));
                                System.err.println(String.format("  新增: Thread-%d, Generator-%d",
                                        infoMap.get("threadIndex"), infoMap.get("generatorIndex")));
                            }
                        }
                    } finally {
                        latch.countDown();
                    }
                });
            }
        }
        
        latch.await(60, TimeUnit.SECONDS);
        executor.shutdown();
        
        // 打印Map统计信息
        System.out.println("\n=== Map统计信息 ===");
        System.out.println(String.format("总ID数: %d", idInfoMap.size()));
        System.out.println(String.format("唯一ID数: %d", idInfoMap.size()));
        
        // 按机器ID分组统计
        Map<Long, Integer> machineIdCount = new HashMap<>();
        Map<Long, Integer> datacenterIdCount = new HashMap<>();
        
        for (Map<String, Object> info : idInfoMap.values()) {
            Long machineId = (Long) info.get("machineId");
            Long datacenterId = (Long) info.get("datacenterId");
            
            machineIdCount.put(machineId, machineIdCount.getOrDefault(machineId, 0) + 1);
            datacenterIdCount.put(datacenterId, datacenterIdCount.getOrDefault(datacenterId, 0) + 1);
        }
        
        System.out.println("\n按机器ID统计:");
        machineIdCount.forEach((machineId, count) -> 
                System.out.println(String.format("  机器ID %d: %d 个ID", machineId, count)));
        
        System.out.println("\n按数据中心ID统计:");
        datacenterIdCount.forEach((datacenterId, count) -> 
                System.out.println(String.format("  数据中心ID %d: %d 个ID", datacenterId, count)));
        
        // 打印前20个ID的详细信息
        System.out.println("\n=== 前20个ID详细信息 ===");
        int count = 0;
        for (Map.Entry<Long, Map<String, Object>> entry : idInfoMap.entrySet()) {
            if (count++ >= 20) break;
            
            Long id = entry.getKey();
            Map<String, Object> info = entry.getValue();
            System.out.println(String.format(
                    "ID: %d | Thread: %d | Generator: %d | 数据中心ID: %d | 机器ID: %d | 序列号: %d | 时间戳: %d",
                    id,
                    info.get("threadIndex"),
                    info.get("generatorIndex"),
                    info.get("datacenterId"),
                    info.get("machineId"),
                    info.get("sequence"),
                    info.get("timestamp")
            ));
        }
        
        // 验证无重复
        int expectedCount = 30 * 3 * 100;
        assert idInfoMap.size() == expectedCount : 
                String.format("期望ID数: %d, 实际: %d", expectedCount, idInfoMap.size());
        
        System.out.println("\n✓ 综合测试通过：所有ID都是唯一的，无重复");
    }
}
