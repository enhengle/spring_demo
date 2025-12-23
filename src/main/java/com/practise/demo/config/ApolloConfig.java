package com.practise.demo.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Configuration;

/**
 * Apollo配置中心配置类（可选）
 * 当apollo.bootstrap.enabled=true时才会启用
 * 
 * @author system
 * @date 2024
 */
@Configuration
@ConditionalOnProperty(prefix = "apollo.bootstrap", name = "enabled", havingValue = "true", matchIfMissing = false)
public class ApolloConfig {
    
    private static final Logger logger = LoggerFactory.getLogger(ApolloConfig.class);
    
    public ApolloConfig() {
        logger.info("Apollo配置中心已启用");
    }
}

