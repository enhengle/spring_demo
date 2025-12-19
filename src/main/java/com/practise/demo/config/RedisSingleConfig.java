package com.practise.demo.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

/**
 * 单机Redis配置
 */
@Slf4j
@Configuration
public class RedisSingleConfig {

    @Value("${redis.single.host:localhost}")
    private String host;

    @Value("${redis.single.port:6379}")
    private int port;

    @Value("${redis.single.password:}")
    private String password;

    @Value("${redis.single.database:0}")
    private int database;

    @Value("${redis.single.timeout:3000}")
    private int timeout;

    @Value("${redis.single.max-active:8}")
    private int maxActive;

    @Value("${redis.single.max-idle:8}")
    private int maxIdle;

    @Value("${redis.single.min-idle:0}")
    private int minIdle;

    @Value("${redis.single.max-wait:3000}")
    private long maxWait;

    /**
     * 单机Redis连接工厂
     * 使用默认的LettuceClientConfiguration避免类型不匹配问题
     */
    @Bean
    @Primary
    public RedisConnectionFactory redisSingleConnectionFactory() {
        log.info("初始化单机Redis连接工厂 - host: {}, port: {}, database: {}", host, port, database);
        
        RedisStandaloneConfiguration config = new RedisStandaloneConfiguration();
        config.setHostName(host);
        config.setPort(port);
        config.setDatabase(database);
        
        if (password != null && !password.trim().isEmpty()) {
            config.setPassword(password);
        }
        
        // 使用默认的LettuceClientConfiguration，避免类型不匹配问题
        LettuceConnectionFactory factory = new LettuceConnectionFactory(config);
        factory.setTimeout(timeout);
        
        log.info("单机Redis连接工厂初始化完成");
        return factory;
    }

    /**
     * 单机Redis模板
     */
    @Bean
    @Primary
    public RedisTemplate<String, Object> redisSingleTemplate() {
        log.info("初始化单机Redis模板");
        
        RedisTemplate<String, Object> template = new RedisTemplate<>();
        template.setConnectionFactory(redisSingleConnectionFactory());
        
        // 设置序列化器
        StringRedisSerializer stringSerializer = new StringRedisSerializer();
        GenericJackson2JsonRedisSerializer jsonSerializer = new GenericJackson2JsonRedisSerializer();
        
        // key使用String序列化器
        template.setKeySerializer(stringSerializer);
        template.setHashKeySerializer(stringSerializer);
        
        // value使用JSON序列化器
        template.setValueSerializer(jsonSerializer);
        template.setHashValueSerializer(jsonSerializer);
        
        // 设置默认序列化器
        template.setDefaultSerializer(jsonSerializer);
        
        template.afterPropertiesSet();
        
        log.info("单机Redis模板初始化完成");
        return template;
    }

    /**
     * 获取Redis连接信息
     */
    public String getRedisInfo() {
        return String.format("单机Redis - host: %s, port: %d, database: %d", host, port, database);
    }
} 