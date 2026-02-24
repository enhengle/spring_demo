package com.practise.demo.util;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;

import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * UUID工具类测试
 * 
 * @author system
 */
@Slf4j
public class UuidUtilTest {
    
    /**
     * 测试基本UUID生成
     */
    @Test
    public void testBasicUuidGeneration() {
        System.out.println("=== 基本UUID生成测试 ===");
        
        // 测试标准UUID生成
        String uuid1 = UuidUtil.generateUuid();
        System.out.println("标准UUID: " + uuid1);
        assert uuid1.length() == 36 : "标准UUID长度应为36";
        assert uuid1.contains("-") : "标准UUID应包含连字符";
        
        // 测试不带连字符的UUID
        String uuid2 = UuidUtil.generateUuidWithoutHyphens();
        System.out.println("不带连字符UUID: " + uuid2);
        assert uuid2.length() == 32 : "不带连字符UUID长度应为32";
        assert !uuid2.contains("-") : "不带连字符UUID不应包含连字符";
        
        // 测试大写UUID
        String uuid3 = UuidUtil.generateUuidUpperCase();
        System.out.println("大写UUID: " + uuid3);
        assert uuid3.equals(uuid3.toUpperCase()) : "应为大写UUID";
        
        // 测试UUID对象生成
        UUID uuidObj = UuidUtil.generateUuidObject();
        System.out.println("UUID对象: " + uuidObj);
        assert uuidObj != null : "UUID对象不应为null";
        
        System.out.println("✓ 基本UUID生成测试通过");
    }
    
    /**
     * 测试UUID格式验证
     */
    @Test
    public void testUuidValidation() {
        System.out.println("\n=== UUID格式验证测试 ===");
        
        // 有效UUID
        String validUuid1 = UuidUtil.generateUuid();
        assert UuidUtil.isValidUuid(validUuid1) : "有效UUID应通过验证";
        System.out.println("有效UUID（带连字符）: " + validUuid1 + " - 验证通过");
        
        String validUuid2 = UuidUtil.generateUuidWithoutHyphens();
        assert UuidUtil.isValidUuid(validUuid2) : "有效UUID（不带连字符）应通过验证";
        System.out.println("有效UUID（不带连字符）: " + validUuid2 + " - 验证通过");
        
        // 无效UUID
        assert !UuidUtil.isValidUuid(null) : "null应验证失败";
        assert !UuidUtil.isValidUuid("") : "空字符串应验证失败";
        assert !UuidUtil.isValidUuid("invalid-uuid") : "无效UUID应验证失败";
        assert !UuidUtil.isValidUuid("12345") : "长度不足应验证失败";
        
        System.out.println("✓ UUID格式验证测试通过");
    }
    
    /**
     * 测试UUID格式化
     */
    @Test
    public void testUuidFormatting() {
        System.out.println("\n=== UUID格式化测试 ===");
        
        String uuidWithoutHyphens = UuidUtil.generateUuidWithoutHyphens();
        String formatted = UuidUtil.formatUuid(uuidWithoutHyphens);
        System.out.println("原始UUID: " + uuidWithoutHyphens);
        System.out.println("格式化后: " + formatted);
        assert formatted.length() == 36 : "格式化后长度应为36";
        assert formatted.contains("-") : "格式化后应包含连字符";
        
        String removed = UuidUtil.removeHyphens(formatted);
        System.out.println("移除连字符后: " + removed);
        assert removed.length() == 32 : "移除连字符后长度应为32";
        assert removed.equals(uuidWithoutHyphens) : "应等于原始UUID";
        
        System.out.println("✓ UUID格式化测试通过");
    }
    
    /**
     * 测试UUID v3（确定性UUID）
     */
    @Test
    public void testUuidV3() {
        System.out.println("\n=== UUID v3测试 ===");
        
        String name = "test-name";
        
        // 相同输入应生成相同UUID
        String uuid1 = UuidUtil.generateUuidV3FromDns(name);
        String uuid2 = UuidUtil.generateUuidV3FromDns(name);
        System.out.println("UUID v3 (DNS, name='" + name + "'): " + uuid1);
        assert uuid1.equals(uuid2) : "相同输入应生成相同UUID v3";
        
        // 不同输入应生成不同UUID
        String uuid3 = UuidUtil.generateUuidV3FromDns("different-name");
        assert !uuid1.equals(uuid3) : "不同输入应生成不同UUID v3";
        
        // 不同命名空间应生成不同UUID
        String uuid4 = UuidUtil.generateUuidV3FromUrl(name);
        assert !uuid1.equals(uuid4) : "不同命名空间应生成不同UUID v3";
        
        System.out.println("✓ UUID v3测试通过");
    }
    
    /**
     * 测试UUID v5（确定性UUID）
     */
    @Test
    public void testUuidV5() {
        System.out.println("\n=== UUID v5测试 ===");
        
        String name = "test-name";
        
        // 相同输入应生成相同UUID
        String uuid1 = UuidUtil.generateUuidV5FromDns(name);
        String uuid2 = UuidUtil.generateUuidV5FromDns(name);
        System.out.println("UUID v5 (DNS, name='" + name + "'): " + uuid1);
        assert uuid1.equals(uuid2) : "相同输入应生成相同UUID v5";
        
        // 不同输入应生成不同UUID
        String uuid3 = UuidUtil.generateUuidV5FromDns("different-name");
        assert !uuid1.equals(uuid3) : "不同输入应生成不同UUID v5";
        
        // v3和v5应生成不同UUID（即使输入相同）
        String uuid4 = UuidUtil.generateUuidV3FromDns(name);
        assert !uuid1.equals(uuid4) : "v3和v5应生成不同UUID";
        
        System.out.println("✓ UUID v5测试通过");
    }
    
    /**
     * 测试高并发场景下的UUID唯一性
     */
    @Test
    public void testConcurrentUuidGeneration() throws InterruptedException {
        System.out.println("\n=== 高并发UUID生成测试 ===");
        
        // 使用ConcurrentHashMap存储生成的UUID，自动去重
        Map<String, String> uuidMap = new ConcurrentHashMap<>();
        
        // 线程池配置
        int threadCount = 50;
        int uuidsPerThread = 1000;
        ExecutorService executor = Executors.newFixedThreadPool(threadCount);
        CountDownLatch latch = new CountDownLatch(threadCount);
        AtomicInteger duplicateCount = new AtomicInteger(0);
        
        // 提交任务
        for (int i = 0; i < threadCount; i++) {
            final int threadIndex = i;
            executor.submit(() -> {
                try {
                    for (int j = 0; j < uuidsPerThread; j++) {
                        String uuid = UuidUtil.generateUuid();
                        String key = String.format("Thread-%d-Index-%d", threadIndex, j);
                        
                        // 检查重复
                        String existing = uuidMap.put(uuid, key);
                        if (existing != null) {
                            duplicateCount.incrementAndGet();
                            log.error("发现重复UUID: {} - 原有: {}, 新增: {}", uuid, existing, key);
                        }
                    }
                } catch (Exception e) {
                    log.error("生成UUID时发生异常", e);
                } finally {
                    latch.countDown();
                }
            });
        }
        
        // 等待所有任务完成
        latch.await(60, TimeUnit.SECONDS);
        executor.shutdown();
        
        // 统计结果
        int totalUuids = threadCount * uuidsPerThread;
        int uniqueUuids = uuidMap.size();
        int duplicates = duplicateCount.get();
        
        System.out.println(String.format("总生成UUID数: %d", totalUuids));
        System.out.println(String.format("唯一UUID数: %d", uniqueUuids));
        System.out.println(String.format("检测到重复数: %d", duplicates));
        System.out.println(String.format("重复率: %.10f%%", (duplicates * 100.0 / totalUuids)));
        
        // 打印部分UUID信息
        System.out.println("\n=== 部分生成的UUID信息 ===");
        int count = 0;
        for (Map.Entry<String, String> entry : uuidMap.entrySet()) {
            if (count++ < 10) {
                System.out.println(String.format("UUID: %s, 来源: %s", entry.getKey(), entry.getValue()));
            }
        }
        
        // 验证结果（UUID碰撞概率极低，理论上不应有重复）
        assert uniqueUuids == totalUuids : String.format("期望唯一UUID数: %d, 实际: %d", totalUuids, uniqueUuids);
        assert duplicates == 0 : String.format("发现重复UUID: %d", duplicates);
        
        System.out.println("\n✓ 高并发测试通过：所有UUID都是唯一的");
    }
    
    /**
     * 测试批量生成UUID
     */
    @Test
    public void testBatchUuidGeneration() {
        System.out.println("\n=== 批量UUID生成测试 ===");
        
        int count = 10000;
        long startTime = System.currentTimeMillis();
        String[] uuids = UuidUtil.generateUuids(count);
        long endTime = System.currentTimeMillis();
        
        System.out.println(String.format("生成 %d 个UUID耗时: %d 毫秒", count, (endTime - startTime)));
        System.out.println(String.format("平均每个UUID耗时: %.4f 微秒", ((endTime - startTime) * 1000.0 / count)));
        
        // 验证唯一性
        Set<String> uuidSet = new HashSet<>(Arrays.asList(uuids));
        System.out.println(String.format("唯一UUID数: %d", uuidSet.size()));
        assert uuidSet.size() == count : "所有UUID应该是唯一的";
        
        System.out.println("✓ 批量UUID生成测试通过");
    }
    
    /**
     * 测试UUID字节转换
     */
    @Test
    public void testUuidByteConversion() {
        System.out.println("\n=== UUID字节转换测试 ===");
        
        String originalUuid = UuidUtil.generateUuid();
        System.out.println("原始UUID: " + originalUuid);
        
        // UUID转字节数组
        byte[] bytes = UuidUtil.uuidToBytes(originalUuid);
        System.out.println("字节数组长度: " + bytes.length);
        assert bytes.length == 16 : "UUID字节数组长度应为16";
        
        // 字节数组转UUID
        String convertedUuid = UuidUtil.uuidFromBytes(bytes);
        System.out.println("转换后UUID: " + convertedUuid);
        assert originalUuid.equals(convertedUuid) : "转换后UUID应与原始UUID相同";
        
        System.out.println("✓ UUID字节转换测试通过");
    }
    
    /**
     * 测试UUID比较（字典序）
     */
    @Test
    public void testUuidComparison() {
        System.out.println("\n=== UUID比较测试 ===");
        
        String uuid1 = UuidUtil.generateUuid();
        String uuid2 = UuidUtil.generateUuid();
        
        int result = UuidUtil.compareUuid(uuid1, uuid2);
        System.out.println(String.format("UUID1: %s", uuid1));
        System.out.println(String.format("UUID2: %s", uuid2));
        System.out.println(String.format("比较结果: %d (负数表示uuid1<uuid2, 正数表示uuid1>uuid2)", result));
        
        // 相同UUID应返回0
        int sameResult = UuidUtil.compareUuid(uuid1, uuid1);
        assert sameResult == 0 : "相同UUID比较应返回0";
        
        System.out.println("✓ UUID比较测试通过");
    }
    
    /**
     * 测试UUID的坑：不能保证时间顺序
     */
    @Test
    public void testUuidTimeOrderIssue() {
        System.out.println("\n=== UUID时间顺序问题测试 ===");
        
        // UUID v4是随机生成的，不能保证按生成时间排序
        List<String> uuids = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            uuids.add(UuidUtil.generateUuid());
            try {
                Thread.sleep(1); // 确保时间戳不同
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
        
        System.out.println("生成的UUID列表（按生成顺序）:");
        for (int i = 0; i < uuids.size(); i++) {
            System.out.println(String.format("%d. %s", i + 1, uuids.get(i)));
        }
        
        // 按字典序排序
        List<String> sortedUuids = new ArrayList<>(uuids);
        Collections.sort(sortedUuids);
        
        System.out.println("\n按字典序排序后:");
        for (int i = 0; i < sortedUuids.size(); i++) {
            System.out.println(String.format("%d. %s", i + 1, sortedUuids.get(i)));
        }
        
        // 验证排序后顺序与生成顺序不同（大概率）
        boolean orderChanged = !uuids.equals(sortedUuids);
        System.out.println("\n排序后顺序是否改变: " + orderChanged);
        System.out.println("说明: UUID v4是随机生成的，不能保证按时间排序");
        
        System.out.println("✓ UUID时间顺序问题测试通过");
    }
    
    /**
     * 综合测试：打印Map信息
     */
    @Test
    public void testComprehensive() throws InterruptedException {
        System.out.println("\n=== 综合测试 ===");
        
        // 使用Map存储UUID和生成信息
        Map<String, Map<String, Object>> uuidInfoMap = new ConcurrentHashMap<>();
        
        ExecutorService executor = Executors.newFixedThreadPool(30);
        CountDownLatch latch = new CountDownLatch(30);
        
        for (int i = 0; i < 30; i++) {
            final int threadIndex = i;
            executor.submit(() -> {
                try {
                    for (int j = 0; j < 100; j++) {
                        String uuid = UuidUtil.generateUuid();
                        
                        Map<String, Object> infoMap = new HashMap<>();
                        infoMap.put("threadIndex", threadIndex);
                        infoMap.put("uuidIndex", j);
                        infoMap.put("timestamp", System.currentTimeMillis());
                        infoMap.put("format", "standard");
                        infoMap.put("length", uuid.length());
                        
                        // 检查重复
                        Map<String, Object> existing = uuidInfoMap.put(uuid, infoMap);
                        if (existing != null) {
                            System.err.println(String.format("发现重复UUID: %s", uuid));
                            System.err.println(String.format("  原有: Thread-%d, Index-%d",
                                    existing.get("threadIndex"), existing.get("uuidIndex")));
                            System.err.println(String.format("  新增: Thread-%d, Index-%d",
                                    infoMap.get("threadIndex"), infoMap.get("uuidIndex")));
                        }
                    }
                } finally {
                    latch.countDown();
                }
            });
        }
        
        latch.await(60, TimeUnit.SECONDS);
        executor.shutdown();
        
        // 打印Map统计信息
        System.out.println("\n=== Map统计信息 ===");
        System.out.println(String.format("总UUID数: %d", uuidInfoMap.size()));
        System.out.println(String.format("唯一UUID数: %d", uuidInfoMap.size()));
        
        // 按格式分组统计
        Map<String, Integer> formatCount = new HashMap<>();
        for (Map<String, Object> info : uuidInfoMap.values()) {
            String format = (String) info.get("format");
            formatCount.put(format, formatCount.getOrDefault(format, 0) + 1);
        }
        
        System.out.println("\n按格式统计:");
        formatCount.forEach((format, count) -> 
                System.out.println(String.format("  %s: %d 个UUID", format, count)));
        
        // 打印前20个UUID的详细信息
        System.out.println("\n=== 前20个UUID详细信息 ===");
        int count = 0;
        for (Map.Entry<String, Map<String, Object>> entry : uuidInfoMap.entrySet()) {
            if (count++ >= 20) break;
            
            String uuid = entry.getKey();
            Map<String, Object> info = entry.getValue();
            System.out.println(String.format(
                    "UUID: %s | Thread: %d | Index: %d | 长度: %d | 时间戳: %d",
                    uuid,
                    info.get("threadIndex"),
                    info.get("uuidIndex"),
                    info.get("length"),
                    info.get("timestamp")
            ));
        }
        
        // 验证无重复
        int expectedCount = 30 * 100;
        assert uuidInfoMap.size() == expectedCount : 
                String.format("期望UUID数: %d, 实际: %d", expectedCount, uuidInfoMap.size());
        
        System.out.println("\n✓ 综合测试通过：所有UUID都是唯一的，无重复");
    }
    
    /**
     * 测试UUID性能（与雪花算法对比）
     */
    @Test
    public void testUuidPerformance() {
        System.out.println("\n=== UUID性能测试 ===");
        
        int count = 100000;
        
        // UUID生成性能
        long startTime = System.currentTimeMillis();
        for (int i = 0; i < count; i++) {
            UuidUtil.generateUuid();
        }
        long uuidTime = System.currentTimeMillis() - startTime;
        
        // 雪花算法生成性能（对比）
        SnowflakeIdGenerator snowflake = new SnowflakeIdGenerator(1, 1);
        startTime = System.currentTimeMillis();
        for (int i = 0; i < count; i++) {
            snowflake.nextId();
        }
        long snowflakeTime = System.currentTimeMillis() - startTime;
        
        System.out.println(String.format("生成 %d 个ID的性能对比:", count));
        System.out.println(String.format("UUID: %d 毫秒 (%.2f 微秒/个)", uuidTime, uuidTime * 1000.0 / count));
        System.out.println(String.format("雪花算法: %d 毫秒 (%.2f 微秒/个)", snowflakeTime, snowflakeTime * 1000.0 / count));
        System.out.println(String.format("性能比: %.2fx", (double) uuidTime / snowflakeTime));
        
        System.out.println("✓ UUID性能测试完成");
    }
}
