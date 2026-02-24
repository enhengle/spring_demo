package com.practise.demo.util;

import lombok.extern.slf4j.Slf4j;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * ID生成器工厂类
 * 
 * 多部署场景下的ID生成策略：
 * 
 * 1. 雪花算法（Snowflake）：
 *    - 优点：性能好、可排序、数据库索引友好、占用空间小
 *    - 缺点：需要配置机器ID，多部署时必须确保机器ID唯一
 *    - 适用场景：需要时间顺序、数据库主键、高并发场景
 *    - 机器ID配置方案：
 *      a) 环境变量：SNOWFLAKE_DATACENTER_ID, SNOWFLAKE_MACHINE_ID
 *      b) 配置文件：application.yml
 *      c) 自动发现：基于IP地址、容器ID、K8s Pod名称等
 *      d) 注册中心：从Zookeeper/Consul等获取唯一ID
 * 
 * 2. UUID：
 *    - 优点：无需配置、跨语言、多部署直接使用、碰撞概率极低
 *    - 缺点：不能排序、数据库索引性能差、占用空间大（36字符）
 *    - 适用场景：分布式系统、微服务、不需要排序的场景
 * 
 * @author system
 */
@Slf4j
public class IdGeneratorFactory {
    
    /**
     * ID生成器类型
     */
    public enum IdGeneratorType {
        SNOWFLAKE,  // 雪花算法
        UUID,       // UUID v4
        AUTO        // 自动选择（优先雪花算法）
    }
    
    /**
     * 雪花算法生成器缓存（按datacenterId和machineId组合）
     */
    private static final Map<String, SnowflakeIdGenerator> SNOWFLAKE_CACHE = new ConcurrentHashMap<>();
    
    /**
     * 默认ID生成器
     */
    private static volatile IdGenerator defaultGenerator;
    
    /**
     * ID生成器接口
     */
    public interface IdGenerator {
        /**
         * 生成下一个ID
         * @return ID字符串
         */
        String nextId();
        
        /**
         * 生成下一个ID（长整型）
         * @return ID长整型
         */
        long nextLongId();
    }
    
    /**
     * 雪花算法ID生成器包装类
     */
    private static class SnowflakeIdGeneratorWrapper implements IdGenerator {
        private final SnowflakeIdGenerator generator;
        
        public SnowflakeIdGeneratorWrapper(SnowflakeIdGenerator generator) {
            this.generator = generator;
        }
        
        @Override
        public String nextId() {
            return String.valueOf(generator.nextId());
        }
        
        @Override
        public long nextLongId() {
            return generator.nextId();
        }
    }
    
    /**
     * UUID ID生成器包装类
     */
    private static class UuidIdGeneratorWrapper implements IdGenerator {
        @Override
        public String nextId() {
            return UuidUtil.generateUuidWithoutHyphens(); // 不带连字符，更紧凑
        }
        
        @Override
        public long nextLongId() {
            // UUID不能直接转换为long，返回hashCode的绝对值
            return Math.abs(UuidUtil.generateUuid().hashCode());
        }
    }
    
    /**
     * 获取默认ID生成器
     * 优先使用雪花算法，如果配置失败则使用UUID
     * 
     * @return ID生成器
     */
    public static IdGenerator getDefaultGenerator() {
        if (defaultGenerator == null) {
            synchronized (IdGeneratorFactory.class) {
                if (defaultGenerator == null) {
                    defaultGenerator = createGenerator(IdGeneratorType.AUTO);
                }
            }
        }
        return defaultGenerator;
    }
    
    /**
     * 创建ID生成器
     * 
     * @param type 生成器类型
     * @return ID生成器
     */
    public static IdGenerator createGenerator(IdGeneratorType type) {
        switch (type) {
            case SNOWFLAKE:
                return createSnowflakeGenerator();
            case UUID:
                return new UuidIdGeneratorWrapper();
            case AUTO:
            default:
                // 自动选择：优先雪花算法，失败则使用UUID
                try {
                    return createSnowflakeGenerator();
                } catch (Exception e) {
                    log.warn("创建雪花算法生成器失败，降级使用UUID: {}", e.getMessage());
                    return new UuidIdGeneratorWrapper();
                }
        }
    }
    
    /**
     * 创建雪花算法生成器
     * 从环境变量或系统属性读取配置
     * 
     * @return 雪花算法生成器
     */
    private static SnowflakeIdGeneratorWrapper createSnowflakeGenerator() {
        // 1. 优先从环境变量读取
        String datacenterIdStr = System.getenv("SNOWFLAKE_DATACENTER_ID");
        String machineIdStr = System.getenv("SNOWFLAKE_MACHINE_ID");
        
        // 2. 从系统属性读取
        if (datacenterIdStr == null || datacenterIdStr.isEmpty()) {
            datacenterIdStr = System.getProperty("snowflake.datacenter.id");
        }
        if (machineIdStr == null || machineIdStr.isEmpty()) {
            machineIdStr = System.getProperty("snowflake.machine.id");
        }
        
        // 3. 如果都未配置，尝试自动生成
        long datacenterId;
        long machineId;
        
        if (datacenterIdStr != null && !datacenterIdStr.isEmpty()) {
            try {
                datacenterId = Long.parseLong(datacenterIdStr);
            } catch (NumberFormatException e) {
                log.warn("解析数据中心ID失败: {}, 使用默认值0", datacenterIdStr);
                datacenterId = 0;
            }
        } else {
            // 自动生成：基于IP地址的最后一段
            datacenterId = generateDatacenterIdFromIp();
        }
        
        if (machineIdStr != null && !machineIdStr.isEmpty()) {
            try {
                machineId = Long.parseLong(machineIdStr);
            } catch (NumberFormatException e) {
                log.warn("解析机器ID失败: {}, 使用默认值0", machineIdStr);
                machineId = 0;
            }
        } else {
            // 自动生成：基于主机名或进程ID
            machineId = generateMachineId();
        }
        
        // 验证ID范围
        if (datacenterId < 0 || datacenterId > 31) {
            log.warn("数据中心ID超出范围: {}, 使用默认值0", datacenterId);
            datacenterId = 0;
        }
        if (machineId < 0 || machineId > 31) {
            log.warn("机器ID超出范围: {}, 使用默认值0", machineId);
            machineId = 0;
        }
        
        // 使用缓存避免重复创建
        final long finalDatacenterId = datacenterId;
        final long finalMachineId = machineId;
        String cacheKey = finalDatacenterId + "-" + finalMachineId;
        SnowflakeIdGenerator generator = SNOWFLAKE_CACHE.computeIfAbsent(cacheKey, 
                k -> new SnowflakeIdGenerator(finalDatacenterId, finalMachineId));
        
        log.info("创建雪花算法ID生成器 - 数据中心ID: {}, 机器ID: {}", finalDatacenterId, finalMachineId);
        return new SnowflakeIdGeneratorWrapper(generator);
    }
    
    /**
     * 基于IP地址生成数据中心ID（0-31）
     * 
     * @return 数据中心ID
     */
    private static long generateDatacenterIdFromIp() {
        try {
            java.net.InetAddress localHost = java.net.InetAddress.getLocalHost();
            String ip = localHost.getHostAddress();
            String[] parts = ip.split("\\.");
            if (parts.length == 4) {
                // 使用IP最后一段的模32
                int lastOctet = Integer.parseInt(parts[3]);
                return lastOctet % 32;
            }
        } catch (Exception e) {
            log.debug("无法获取IP地址生成数据中心ID", e);
        }
        return 0;
    }
    
    /**
     * 生成机器ID（0-31）
     * 优先使用主机名，其次使用进程ID
     * 
     * @return 机器ID
     */
    private static long generateMachineId() {
        try {
            // 方法1：基于主机名
            String hostname = java.net.InetAddress.getLocalHost().getHostName();
            if (hostname != null && !hostname.isEmpty()) {
                // 使用主机名的hashCode模32
                return Math.abs(hostname.hashCode()) % 32;
            }
        } catch (Exception e) {
            log.debug("无法获取主机名生成机器ID", e);
        }
        
        try {
            // 方法2：基于进程ID
            String processName = java.lang.management.ManagementFactory
                    .getRuntimeMXBean().getName();
            if (processName != null && !processName.isEmpty()) {
                // 提取进程ID部分
                String pid = processName.split("@")[0];
                return Long.parseLong(pid) % 32;
            }
        } catch (Exception e) {
            log.debug("无法获取进程ID生成机器ID", e);
        }
        
        // 默认返回0
        return 0;
    }
    
    /**
     * 手动创建雪花算法生成器（指定ID）
     * 
     * @param datacenterId 数据中心ID（0-31）
     * @param machineId 机器ID（0-31）
     * @return ID生成器
     */
    public static IdGenerator createSnowflakeGenerator(long datacenterId, long machineId) {
        String cacheKey = datacenterId + "-" + machineId;
        SnowflakeIdGenerator generator = SNOWFLAKE_CACHE.computeIfAbsent(cacheKey, 
                k -> new SnowflakeIdGenerator(datacenterId, machineId));
        return new SnowflakeIdGeneratorWrapper(generator);
    }
    
    /**
     * 创建UUID生成器
     * 
     * @return UUID生成器
     */
    public static IdGenerator createUuidGenerator() {
        return new UuidIdGeneratorWrapper();
    }
    
    /**
     * 重置默认生成器（用于测试或重新配置）
     */
    public static void resetDefaultGenerator() {
        synchronized (IdGeneratorFactory.class) {
            defaultGenerator = null;
        }
    }
    
    /**
     * 获取当前配置信息（用于调试）
     * 
     * @return 配置信息字符串
     */
    public static String getConfigInfo() {
        StringBuilder info = new StringBuilder();
        info.append("=== ID生成器配置信息 ===\n");
        
        // 环境变量
        String envDatacenterId = System.getenv("SNOWFLAKE_DATACENTER_ID");
        String envMachineId = System.getenv("SNOWFLAKE_MACHINE_ID");
        info.append(String.format("环境变量 - SNOWFLAKE_DATACENTER_ID: %s\n", 
                envDatacenterId != null ? envDatacenterId : "未设置"));
        info.append(String.format("环境变量 - SNOWFLAKE_MACHINE_ID: %s\n", 
                envMachineId != null ? envMachineId : "未设置"));
        
        // 系统属性
        String propDatacenterId = System.getProperty("snowflake.datacenter.id");
        String propMachineId = System.getProperty("snowflake.machine.id");
        info.append(String.format("系统属性 - snowflake.datacenter.id: %s\n", 
                propDatacenterId != null ? propDatacenterId : "未设置"));
        info.append(String.format("系统属性 - snowflake.machine.id: %s\n", 
                propMachineId != null ? propMachineId : "未设置"));
        
        // 自动生成的值
        info.append(String.format("自动生成 - 数据中心ID: %d\n", generateDatacenterIdFromIp()));
        info.append(String.format("自动生成 - 机器ID: %d\n", generateMachineId()));
        
        // 主机信息
        try {
            java.net.InetAddress localHost = java.net.InetAddress.getLocalHost();
            info.append(String.format("主机名: %s\n", localHost.getHostName()));
            info.append(String.format("IP地址: %s\n", localHost.getHostAddress()));
        } catch (Exception e) {
            info.append("无法获取主机信息\n");
        }
        
        return info.toString();
    }
}
