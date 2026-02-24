package com.practise.demo.util;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;

import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * ID生成器工厂测试类
 * 测试多部署场景下的ID生成策略
 * 
 * @author system
 */
@Slf4j
public class IdGeneratorFactoryTest {
    
    /**
     * 测试默认ID生成器
     */
    @Test
    public void testDefaultGenerator() {
        System.out.println("=== 默认ID生成器测试 ===");
        
        IdGeneratorFactory.IdGenerator generator = IdGeneratorFactory.getDefaultGenerator();
        
        for (int i = 0; i < 10; i++) {
            String id = generator.nextId();
            System.out.println("生成的ID: " + id);
        }
        
        System.out.println("✓ 默认ID生成器测试通过");
    }
    
    /**
     * 测试雪花算法生成器（手动指定ID）
     */
    @Test
    public void testSnowflakeGeneratorWithManualId() {
        System.out.println("\n=== 雪花算法生成器测试（手动指定ID） ===");
        
        // 模拟多部署场景：不同机器使用不同的机器ID
        IdGeneratorFactory.IdGenerator generator1 = IdGeneratorFactory.createSnowflakeGenerator(1, 1);
        IdGeneratorFactory.IdGenerator generator2 = IdGeneratorFactory.createSnowflakeGenerator(1, 2);
        IdGeneratorFactory.IdGenerator generator3 = IdGeneratorFactory.createSnowflakeGenerator(2, 1);
        
        System.out.println("机器1 (数据中心1, 机器1):");
        for (int i = 0; i < 5; i++) {
            System.out.println("  ID: " + generator1.nextId());
        }
        
        System.out.println("机器2 (数据中心1, 机器2):");
        for (int i = 0; i < 5; i++) {
            System.out.println("  ID: " + generator2.nextId());
        }
        
        System.out.println("机器3 (数据中心2, 机器1):");
        for (int i = 0; i < 5; i++) {
            System.out.println("  ID: " + generator3.nextId());
        }
        
        System.out.println("✓ 雪花算法生成器测试通过");
    }
    
    /**
     * 测试UUID生成器
     */
    @Test
    public void testUuidGenerator() {
        System.out.println("\n=== UUID生成器测试 ===");
        
        IdGeneratorFactory.IdGenerator generator = IdGeneratorFactory.createUuidGenerator();
        
        for (int i = 0; i < 10; i++) {
            String id = generator.nextId();
            System.out.println("生成的UUID: " + id);
        }
        
        System.out.println("✓ UUID生成器测试通过");
    }
    
    /**
     * 测试多部署场景下的ID唯一性（雪花算法）
     */
    @Test
    public void testMultiDeploymentSnowflake() throws InterruptedException {
        System.out.println("\n=== 多部署场景测试（雪花算法） ===");
        
        // 模拟5个不同的部署实例，每个使用不同的机器ID
        List<IdGeneratorFactory.IdGenerator> generators = new ArrayList<>();
        for (int i = 0; i < 5; i++) {
            generators.add(IdGeneratorFactory.createSnowflakeGenerator(1, i + 1));
        }
        
        Map<String, Integer> idMap = new ConcurrentHashMap<>();
        ExecutorService executor = Executors.newFixedThreadPool(50);
        CountDownLatch latch = new CountDownLatch(50);
        AtomicInteger duplicateCount = new AtomicInteger(0);
        
        // 每个线程使用不同的生成器（模拟不同机器）
        for (int i = 0; i < 50; i++) {
            final int threadIndex = i;
            final IdGeneratorFactory.IdGenerator generator = generators.get(i % generators.size());
            
            executor.submit(() -> {
                try {
                    for (int j = 0; j < 100; j++) {
                        String id = generator.nextId();
                        Integer existing = idMap.put(id, threadIndex);
                        if (existing != null) {
                            duplicateCount.incrementAndGet();
                            log.error("发现重复ID: {} - 原有线程: {}, 新增线程: {}", id, existing, threadIndex);
                        }
                    }
                } finally {
                    latch.countDown();
                }
            });
        }
        
        latch.await(60, TimeUnit.SECONDS);
        executor.shutdown();
        
        int totalIds = 50 * 100;
        int uniqueIds = idMap.size();
        int duplicates = duplicateCount.get();
        
        System.out.println(String.format("总生成ID数: %d", totalIds));
        System.out.println(String.format("唯一ID数: %d", uniqueIds));
        System.out.println(String.format("检测到重复数: %d", duplicates));
        System.out.println(String.format("重复率: %.4f%%", (duplicates * 100.0 / totalIds)));
        
        assert uniqueIds == totalIds : String.format("期望唯一ID数: %d, 实际: %d", totalIds, uniqueIds);
        assert duplicates == 0 : String.format("发现重复ID: %d", duplicates);
        
        System.out.println("✓ 多部署场景测试通过：所有ID都是唯一的");
    }
    
    /**
     * 测试多部署场景下的ID唯一性（UUID）
     */
    @Test
    public void testMultiDeploymentUuid() throws InterruptedException {
        System.out.println("\n=== 多部署场景测试（UUID） ===");
        
        // UUID不需要配置，所有实例直接使用
        IdGeneratorFactory.IdGenerator generator = IdGeneratorFactory.createUuidGenerator();
        
        Map<String, Integer> idMap = new ConcurrentHashMap<>();
        ExecutorService executor = Executors.newFixedThreadPool(50);
        CountDownLatch latch = new CountDownLatch(50);
        AtomicInteger duplicateCount = new AtomicInteger(0);
        
        // 所有线程使用同一个生成器（模拟多部署）
        for (int i = 0; i < 50; i++) {
            final int threadIndex = i;
            executor.submit(() -> {
                try {
                    for (int j = 0; j < 100; j++) {
                        String id = generator.nextId();
                        Integer existing = idMap.put(id, threadIndex);
                        if (existing != null) {
                            duplicateCount.incrementAndGet();
                            log.error("发现重复UUID: {} - 原有线程: {}, 新增线程: {}", id, existing, threadIndex);
                        }
                    }
                } finally {
                    latch.countDown();
                }
            });
        }
        
        latch.await(60, TimeUnit.SECONDS);
        executor.shutdown();
        
        int totalIds = 50 * 100;
        int uniqueIds = idMap.size();
        int duplicates = duplicateCount.get();
        
        System.out.println(String.format("总生成UUID数: %d", totalIds));
        System.out.println(String.format("唯一UUID数: %d", uniqueIds));
        System.out.println(String.format("检测到重复数: %d", duplicates));
        System.out.println(String.format("重复率: %.10f%%", (duplicates * 100.0 / totalIds)));
        
        assert uniqueIds == totalIds : String.format("期望唯一UUID数: %d, 实际: %d", totalIds, uniqueIds);
        assert duplicates == 0 : String.format("发现重复UUID: %d", duplicates);
        
        System.out.println("✓ 多部署场景测试通过：所有UUID都是唯一的");
    }
    
    /**
     * 测试配置信息
     */
    @Test
    public void testConfigInfo() {
        System.out.println("\n=== 配置信息测试 ===");
        System.out.println(IdGeneratorFactory.getConfigInfo());
        System.out.println("✓ 配置信息测试通过");
    }
    
    /**
     * 测试自动选择生成器
     */
    @Test
    public void testAutoGenerator() {
        System.out.println("\n=== 自动选择生成器测试 ===");
        
        // 测试AUTO模式（优先雪花算法）
        IdGeneratorFactory.IdGenerator generator = IdGeneratorFactory.createGenerator(
                IdGeneratorFactory.IdGeneratorType.AUTO);
        
        for (int i = 0; i < 10; i++) {
            String id = generator.nextId();
            System.out.println("自动生成的ID: " + id);
        }
        
        System.out.println("✓ 自动选择生成器测试通过");
    }
    
    /**
     * 对比测试：雪花算法 vs UUID
     */
    @Test
    public void testComparison() {
        System.out.println("\n=== 雪花算法 vs UUID 对比测试 ===");
        
        IdGeneratorFactory.IdGenerator snowflakeGenerator = 
                IdGeneratorFactory.createSnowflakeGenerator(1, 1);
        IdGeneratorFactory.IdGenerator uuidGenerator = 
                IdGeneratorFactory.createUuidGenerator();
        
        int count = 10000;
        
        // 雪花算法性能测试
        long startTime = System.currentTimeMillis();
        for (int i = 0; i < count; i++) {
            snowflakeGenerator.nextId();
        }
        long snowflakeTime = System.currentTimeMillis() - startTime;
        
        // UUID性能测试
        startTime = System.currentTimeMillis();
        for (int i = 0; i < count; i++) {
            uuidGenerator.nextId();
        }
        long uuidTime = System.currentTimeMillis() - startTime;
        
        System.out.println(String.format("生成 %d 个ID的性能对比:", count));
        System.out.println(String.format("雪花算法: %d 毫秒 (%.2f 微秒/个)", 
                snowflakeTime, snowflakeTime * 1000.0 / count));
        System.out.println(String.format("UUID: %d 毫秒 (%.2f 微秒/个)", 
                uuidTime, uuidTime * 1000.0 / count));
        System.out.println(String.format("性能比: %.2fx", (double) uuidTime / snowflakeTime));
        
        // ID长度对比
        String snowflakeId = snowflakeGenerator.nextId();
        String uuidId = uuidGenerator.nextId();
        System.out.println(String.format("\nID长度对比:"));
        System.out.println(String.format("雪花算法ID: %s (长度: %d)", snowflakeId, snowflakeId.length()));
        System.out.println(String.format("UUID: %s (长度: %d)", uuidId, uuidId.length()));
        
        System.out.println("\n✓ 对比测试完成");
    }
    
    /**
     * 测试错误场景：相同机器ID的冲突
     */
    @Test
    public void testSameMachineIdConflict() throws InterruptedException {
        System.out.println("\n=== 相同机器ID冲突测试 ===");
        System.out.println("警告：如果多个实例使用相同的机器ID，会生成重复ID！");
        
        // 模拟错误配置：两个实例使用相同的机器ID
        IdGeneratorFactory.IdGenerator generator1 = IdGeneratorFactory.createSnowflakeGenerator(1, 1);
        IdGeneratorFactory.IdGenerator generator2 = IdGeneratorFactory.createSnowflakeGenerator(1, 1);
        
        Set<String> idSet1 = ConcurrentHashMap.newKeySet();
        Set<String> idSet2 = ConcurrentHashMap.newKeySet();
        Set<String> allIds = ConcurrentHashMap.newKeySet();
        
        ExecutorService executor = Executors.newFixedThreadPool(2);
        CountDownLatch latch = new CountDownLatch(2);
        
        executor.submit(() -> {
            for (int i = 0; i < 1000; i++) {
                String id = generator1.nextId();
                idSet1.add(id);
                allIds.add(id);
            }
            latch.countDown();
        });
        
        executor.submit(() -> {
            for (int i = 0; i < 1000; i++) {
                String id = generator2.nextId();
                idSet2.add(id);
                allIds.add(id);
            }
            latch.countDown();
        });
        
        latch.await(10, TimeUnit.SECONDS);
        executor.shutdown();
        
        int totalGenerated = idSet1.size() + idSet2.size();
        int uniqueIds = allIds.size();
        int conflicts = totalGenerated - uniqueIds;
        
        System.out.println(String.format("实例1生成ID数: %d", idSet1.size()));
        System.out.println(String.format("实例2生成ID数: %d", idSet2.size()));
        System.out.println(String.format("总生成ID数: %d", totalGenerated));
        System.out.println(String.format("唯一ID数: %d", uniqueIds));
        System.out.println(String.format("冲突ID数: %d", conflicts));
        
        if (conflicts > 0) {
            System.out.println("⚠️ 检测到冲突！相同机器ID会导致ID重复！");
        } else {
            System.out.println("✓ 未检测到冲突（可能因为时间戳不同）");
        }
        
        System.out.println("\n建议：每个部署实例必须使用唯一的机器ID！");
    }
}
