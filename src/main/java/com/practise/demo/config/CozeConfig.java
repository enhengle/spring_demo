package com.practise.demo.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * Coze配置类
 * 
 * @author system
 */
@Data
@Configuration
@ConfigurationProperties(prefix = "coze")
public class CozeConfig {
    
    /**
     * Coze工作流API地址
     * 默认：https://8xdkxnw4qj.coze.site/run
     */
    private String workflowUrl = "https://8xdkxnw4qj.coze.site/run";
    
    /**
     * Coze API Token
     */
    private String token;
    
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
