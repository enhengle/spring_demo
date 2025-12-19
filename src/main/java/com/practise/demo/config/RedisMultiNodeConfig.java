package com.practise.demo.config;
//
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.beans.factory.annotation.Value;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.data.redis.connection.RedisSentinelConfiguration;
//import org.springframework.data.redis.connection.RedisConnectionFactory;
//import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
//import org.springframework.data.redis.core.RedisTemplate;
//import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
//import org.springframework.data.redis.serializer.StringRedisSerializer;
//
//import java.util.Arrays;
//import java.util.List;
//
///**
// * 多节点Redis配置（哨兵模式）
// */
//@Slf4j
//@Configuration
//public class RedisMultiNodeConfig {
//
//    @Value("${redis.sentinel.master:mymaster}")
//    private String masterName;
//
//    @Value("${redis.sentinel.nodes:localhost:26379,localhost:26380,localhost:26381}")
//    private String sentinelNodes;
//
//    @Value("${redis.sentinel.password:}")
//    private String sentinelPassword;
//
//    @Value("${redis.master.password:}")
//    private String masterPassword;
//
//    @Value("${redis.sentinel.database:0}")
//    private int database;
//
//    @Value("${redis.sentinel.timeout:3000}")
//    private int timeout;
//
//    @Value("${redis.sentinel.max-active:8}")
//    private int maxActive;
//
//    @Value("${redis.sentinel.max-idle:8}")
//    private int maxIdle;
//
//    @Value("${redis.sentinel.min-idle:0}")
//    private int minIdle;
//
//    @Value("${redis.sentinel.max-wait:3000}")
//    private long maxWait;
//
//    /**
//     * 多节点Redis连接工厂（哨兵模式）
//     */
//    @Bean("redisMultiNodeConnectionFactory")
//    public RedisConnectionFactory redisMultiNodeConnectionFactory() {
//        log.info("初始化多节点Redis连接工厂 - master: {}, sentinels: {}", masterName, sentinelNodes);
//
//        // 解析哨兵节点
//        List<String> sentinels = Arrays.asList(sentinelNodes.split(","));
//        log.info("哨兵节点列表: {}", sentinels);
//
//        RedisSentinelConfiguration config = new RedisSentinelConfiguration()
//                .master(masterName)
//                .sentinel(sentinels.get(0).split(":")[0],
//                         Integer.parseInt(sentinels.get(0).split(":")[1]));
//
//        // 添加其他哨兵节点
//        for (int i = 1; i < sentinels.size(); i++) {
//            String[] parts = sentinels.get(i).split(":");
//            config.sentinel(parts[0], Integer.parseInt(parts[1]));
//        }
//
//        // 设置数据库
//        config.setDatabase(database);
//
//        // 设置密码
//        if (masterPassword != null && !masterPassword.trim().isEmpty()) {
//            config.setPassword(masterPassword);
//        }
//
//        if (sentinelPassword != null && !sentinelPassword.trim().isEmpty()) {
//            config.setSentinelPassword(sentinelPassword);
//        }
//
//        LettuceConnectionFactory factory = new LettuceConnectionFactory(config);
//        factory.setTimeout(timeout);
//
//        log.info("多节点Redis连接工厂初始化完成");
//        return factory;
//    }
//
//    /**
//     * 多节点Redis模板
//     */
//    @Bean("redisMultiNodeTemplate")
//    public RedisTemplate<String, Object> redisMultiNodeTemplate() {
//        log.info("初始化多节点Redis模板");
//
//        RedisTemplate<String, Object> template = new RedisTemplate<>();
//        template.setConnectionFactory(redisMultiNodeConnectionFactory());
//
//        // 设置序列化器
//        StringRedisSerializer stringSerializer = new StringRedisSerializer();
//        GenericJackson2JsonRedisSerializer jsonSerializer = new GenericJackson2JsonRedisSerializer();
//
//        // key使用String序列化器
//        template.setKeySerializer(stringSerializer);
//        template.setHashKeySerializer(stringSerializer);
//
//        // value使用JSON序列化器
//        template.setValueSerializer(jsonSerializer);
//        template.setHashValueSerializer(jsonSerializer);
//
//        // 设置默认序列化器
//        template.setDefaultSerializer(jsonSerializer);
//
//        template.afterPropertiesSet();
//
//        log.info("多节点Redis模板初始化完成");
//        return template;
//    }
//
//    /**
//     * 获取哨兵节点列表
//     */
//    public List<String> getSentinelNodes() {
//        return Arrays.asList(sentinelNodes.split(","));
//    }
//
//    /**
//     * 获取主节点名称
//     */
//    public String getMasterName() {
//        return masterName;
//    }
//
//    /**
//     * 获取Redis多节点信息
//     */
//    public String getRedisMultiNodeInfo() {
//        return String.format("多节点Redis - master: %s, sentinels: %s, database: %d",
//                masterName, sentinelNodes, database);
//    }
//}