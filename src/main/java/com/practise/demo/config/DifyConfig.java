package com.practise.demo.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * Dify配置类
 * 
 * @author system
 */
@Data
@Configuration
@ConfigurationProperties(prefix = "dify")
public class DifyConfig {
    
    /**
     * Dify API基础URL
     * 默认：https://api.dify.ai/v1
     */
    private String baseUrl = "https://api.dify.ai/v1";
    
    /**
     * Dify API Key（从Dify平台获取，格式：app-xxx）
     */
    private String apiKey;
    
    /**
     * Dify应用ID（可选）
     */
    private String appId;
    
    /**
     * 连接超时时间（毫秒）
     * 默认：10000（10秒）
     */
    private Integer connectTimeout = 10000;
    
    /**
     * 读取超时时间（毫秒）
     * 默认：60000（60秒）
     */
    private Integer readTimeout = 60000;
    
    /**
     * SSE超时时间（毫秒）
     * 默认：300000（5分钟）
     */
    private Long sseTimeout = 300000L;
}
