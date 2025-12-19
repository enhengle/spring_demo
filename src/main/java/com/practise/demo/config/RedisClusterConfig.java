package com.practise.demo.config;//package com.qihoo.finance..config;
//
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.beans.factory.annotation.Value;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.data.redis.connection.RedisClusterConfiguration;
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
// * 集群Redis配置
// */
//@Slf4j
//@Configuration
//public class RedisClusterConfig {
//
//    @Value("${redis.cluster.nodes:localhost:7000,localhost:7001,localhost:7002}")
//    private String clusterNodes;
//
//    @Value("${redis.cluster.password:}")
//    private String password;
//
//    @Value("${redis.cluster.timeout:3000}")
//    private int timeout;
//
//    @Value("${redis.cluster.max-redirects:5}")
//    private int maxRedirects;
//
//    @Value("${redis.cluster.refresh-period:60}")
//    private long refreshPeriod;
//
//    @Value("${redis.cluster.max-active:8}")
//    private int maxActive;
//
//    @Value("${redis.cluster.max-idle:8}")
//    private int maxIdle;
//
//    @Value("${redis.cluster.min-idle:0}")
//    private int minIdle;
//
//    @Value("${redis.cluster.max-wait:3000}")
//    private long maxWait;
//
//    /**
//     * 集群Redis连接工厂
//     */
//    @Bean("redisClusterConnectionFactory")
//    public RedisConnectionFactory redisClusterConnectionFactory() {
//        log.info("初始化集群Redis连接工厂 - nodes: {}", clusterNodes);
//
//        // 解析集群节点
//        List<String> nodes = Arrays.asList(clusterNodes.split(","));
//        log.info("集群节点列表: {}", nodes);
//
//        RedisClusterConfiguration config = new RedisClusterConfiguration(nodes);
//
//        if (password != null && !password.trim().isEmpty()) {
//            config.setPassword(password);
//        }
//
//        config.setMaxRedirects(maxRedirects);
////        config.setRefreshPeriod(refreshPeriod);
//
//        LettuceConnectionFactory factory = new LettuceConnectionFactory(config);
//        factory.setTimeout(timeout);
//
//        log.info("集群Redis连接工厂初始化完成");
//        return factory;
//    }
//
//    /**
//     * 集群Redis模板
//     */
//    @Bean("redisClusterTemplate")
//    public RedisTemplate<String, Object> redisClusterTemplate() {
//        log.info("初始化集群Redis模板");
//
//        RedisTemplate<String, Object> template = new RedisTemplate<>();
//        template.setConnectionFactory(redisClusterConnectionFactory());
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
//        log.info("集群Redis模板初始化完成");
//        return template;
//    }
//
//    /**
//     * 获取集群节点列表
//     */
//    public List<String> getClusterNodes() {
//        return Arrays.asList(clusterNodes.split(","));
//    }
//
//    /**
//     * 获取Redis集群信息
//     */
//    public String getRedisClusterInfo() {
//        return String.format("集群Redis - nodes: %s, maxRedirects: %d", clusterNodes, maxRedirects);
//    }
//}