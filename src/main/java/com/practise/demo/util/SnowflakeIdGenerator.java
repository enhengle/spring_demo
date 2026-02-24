package com.practise.demo.util;

import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.atomic.AtomicLong;

/**
 * 雪花算法ID生成器
 * 
 * 64位ID结构：
 * 0 - 41位时间戳（毫秒）- 10位机器标识（5位数据中心ID + 5位机器ID）- 12位序列号
 * 
 * 特性：
 * 1. 处理时间戳回拨问题
 * 2. 支持多机器部署（通过机器ID区分）
 * 3. 高并发下序列号耗尽自动等待下一毫秒
 * 4. 处理NTP调整引发的时间异常
 * 5. 跨语言兼容（使用标准Unix时间戳）
 * 
 * @author system
 */
@Slf4j
public class SnowflakeIdGenerator {
    
    /**
     * 起始时间戳（2020-01-01 00:00:00），可自定义以减少时间戳位数占用
     * 使用标准Unix时间戳（1970-01-01）以确保跨语言兼容
     */
    private static final long START_TIMESTAMP = 1577836800000L; // 2020-01-01 00:00:00
    
    /**
     * 机器ID占用的位数（5位）
     */
    private static final long MACHINE_ID_BITS = 5L;
    
    /**
     * 数据中心ID占用的位数（5位）
     */
    private static final long DATACENTER_ID_BITS = 5L;
    
    /**
     * 序列号占用的位数（12位）
     */
    private static final long SEQUENCE_BITS = 12L;
    
    /**
     * 机器ID最大值（31）
     */
    private static final long MAX_MACHINE_ID = ~(-1L << MACHINE_ID_BITS);
    
    /**
     * 数据中心ID最大值（31）
     */
    private static final long MAX_DATACENTER_ID = ~(-1L << DATACENTER_ID_BITS);
    
    /**
     * 序列号最大值（4095）
     */
    private static final long MAX_SEQUENCE = ~(-1L << SEQUENCE_BITS);
    
    /**
     * 机器ID向左移12位
     */
    private static final long MACHINE_ID_SHIFT = SEQUENCE_BITS;
    
    /**
     * 数据中心ID向左移17位（12+5）
     */
    private static final long DATACENTER_ID_SHIFT = SEQUENCE_BITS + MACHINE_ID_BITS;
    
    /**
     * 时间戳向左移22位（12+5+5）
     */
    private static final long TIMESTAMP_SHIFT = SEQUENCE_BITS + MACHINE_ID_BITS + DATACENTER_ID_BITS;
    
    /**
     * 数据中心ID（0-31）
     */
    private final long datacenterId;
    
    /**
     * 机器ID（0-31）
     */
    private final long machineId;
    
    /**
     * 序列号（0-4095）
     */
    private final AtomicLong sequence = new AtomicLong(0L);
    
    /**
     * 上次生成ID的时间戳
     */
    private volatile long lastTimestamp = -1L;
    
    /**
     * 时间回拨容忍度（毫秒），默认5毫秒
     * 如果时间回拨在容忍范围内，等待时间前进
     */
    private static final long TIME_BACKWARD_TOLERANCE = 5L;
    
    /**
     * 构造函数
     * 
     * @param datacenterId 数据中心ID（0-31）
     * @param machineId 机器ID（0-31）
     */
    public SnowflakeIdGenerator(long datacenterId, long machineId) {
        if (datacenterId > MAX_DATACENTER_ID || datacenterId < 0) {
            throw new IllegalArgumentException(
                    String.format("数据中心ID必须在0到%d之间", MAX_DATACENTER_ID));
        }
        if (machineId > MAX_MACHINE_ID || machineId < 0) {
            throw new IllegalArgumentException(
                    String.format("机器ID必须在0到%d之间", MAX_MACHINE_ID));
        }
        this.datacenterId = datacenterId;
        this.machineId = machineId;
        log.info("雪花算法ID生成器初始化成功 - 数据中心ID: {}, 机器ID: {}", datacenterId, machineId);
    }
    
    /**
     * 生成下一个ID
     * 
     * @return 64位长整型ID
     */
    public synchronized long nextId() {
        long currentTimestamp = getCurrentTimestamp();
        
        // 处理时间回拨问题
        if (currentTimestamp < lastTimestamp) {
            long offset = lastTimestamp - currentTimestamp;
            // 如果回拨时间在容忍范围内，等待时间前进
            if (offset <= TIME_BACKWARD_TOLERANCE) {
                log.warn("检测到时间回拨 {} 毫秒，在容忍范围内，等待时间前进", offset);
                currentTimestamp = waitUntilNextMillis(lastTimestamp);
            } else {
                // 时间回拨超过容忍度，抛出异常
                throw new RuntimeException(
                        String.format("时间回拨异常！拒绝生成ID。回拨时间: %d 毫秒，上次时间戳: %d，当前时间戳: %d",
                                offset, lastTimestamp, currentTimestamp));
            }
        }
        
        // 同一毫秒内
        if (currentTimestamp == lastTimestamp) {
            // 序列号自增
            long sequenceValue = sequence.incrementAndGet();
            
            // 序列号溢出，等待下一毫秒（处理高并发下序列号耗尽问题）
            if (sequenceValue > MAX_SEQUENCE) {
                log.warn("序列号耗尽，等待下一毫秒");
                currentTimestamp = waitUntilNextMillis(lastTimestamp);
                sequence.set(0L);
                sequenceValue = 0L;
            }
            
            // 更新上次时间戳
            lastTimestamp = currentTimestamp;
            
            // 生成ID
            return generateId(currentTimestamp, sequenceValue);
        } else {
            // 新的毫秒，序列号重置为0
            sequence.set(0L);
            lastTimestamp = currentTimestamp;
            
            // 生成ID
            return generateId(currentTimestamp, 0L);
        }
    }
    
    /**
     * 生成ID的核心方法
     * 
     * @param timestamp 时间戳
     * @param sequence 序列号
     * @return 生成的ID
     */
    private long generateId(long timestamp, long sequence) {
        return ((timestamp - START_TIMESTAMP) << TIMESTAMP_SHIFT)
                | (datacenterId << DATACENTER_ID_SHIFT)
                | (machineId << MACHINE_ID_SHIFT)
                | sequence;
    }
    
    /**
     * 获取当前时间戳（毫秒）
     * 
     * @return 当前时间戳
     */
    private long getCurrentTimestamp() {
        return System.currentTimeMillis();
    }
    
    /**
     * 等待直到下一毫秒
     * 
     * @param lastTimestamp 上次时间戳
     * @return 新的时间戳
     */
    private long waitUntilNextMillis(long lastTimestamp) {
        long currentTimestamp = getCurrentTimestamp();
        while (currentTimestamp <= lastTimestamp) {
            currentTimestamp = getCurrentTimestamp();
        }
        return currentTimestamp;
    }
    
    /**
     * 解析ID，获取时间戳、数据中心ID、机器ID、序列号
     * 
     * @param id 生成的ID
     * @return ID信息对象
     */
    public IdInfo parseId(long id) {
        long timestamp = (id >> TIMESTAMP_SHIFT) + START_TIMESTAMP;
        long datacenterId = (id >> DATACENTER_ID_SHIFT) & MAX_DATACENTER_ID;
        long machineId = (id >> MACHINE_ID_SHIFT) & MAX_MACHINE_ID;
        long sequence = id & MAX_SEQUENCE;
        
        return new IdInfo(id, timestamp, datacenterId, machineId, sequence);
    }
    
    /**
     * ID信息类
     */
    public static class IdInfo {
        private final long id;
        private final long timestamp;
        private final long datacenterId;
        private final long machineId;
        private final long sequence;
        
        public IdInfo(long id, long timestamp, long datacenterId, long machineId, long sequence) {
            this.id = id;
            this.timestamp = timestamp;
            this.datacenterId = datacenterId;
            this.machineId = machineId;
            this.sequence = sequence;
        }
        
        public long getId() {
            return id;
        }
        
        public long getTimestamp() {
            return timestamp;
        }
        
        public long getDatacenterId() {
            return datacenterId;
        }
        
        public long getMachineId() {
            return machineId;
        }
        
        public long getSequence() {
            return sequence;
        }
        
        @Override
        public String toString() {
            return String.format("IdInfo{id=%d, timestamp=%d, datacenterId=%d, machineId=%d, sequence=%d}",
                    id, timestamp, datacenterId, machineId, sequence);
        }
    }
}
