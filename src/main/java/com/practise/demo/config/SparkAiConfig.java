package com.practise.demo.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * 讯飞星火AI配置类
 * 参考文档：https://www.xfyun.cn/doc/spark/X1http.html
 * 
 * @author system
 */
@Data
@Configuration
@ConfigurationProperties(prefix = "spark.ai")
public class SparkAiConfig {
    
    /**
     * API Password（HTTP协议使用）
     * 在控制台获取：https://console.xfyun.cn/services/bmx1
     */
    private String apiPassword;
    
    /**
     * API Key（WebSocket协议使用，可选）
     */
    private String apiKey;
    
    /**
     * API Secret（WebSocket协议使用，可选）
     */
    private String apiSecret;
    
    /**
     * App ID（WebSocket协议使用，可选）
     */
    private String appId;
    
    /**
     * HTTP API地址
     * X1.5版本：https://spark-api-open.xf-yun.com/v2/chat/completions
     * X1.5-0106版本：https://spark-api-open-preview.xf-yun.com/v2/chat/completions
     */
    private String apiUrl = "https://spark-api-open.xf-yun.com/v2/chat/completions";
    
    /**
     * WebSocket API地址
     * X1.5版本：wss://spark-api.xf-yun.com/v1/x1
     * X1.5-0106版本：wss://spark-openapi.cn-huabei-1.xf-yun.com/v1/reasoner-x1-preview
     */
    private String wsUrl = "wss://spark-api.xf-yun.com/v1/x1";
    
    /**
     * 模型版本
     * X1.5版本：spark-x
     */
    private String model = "spark-x";
    
    /**
     * 默认温度参数
     */
    private Double temperature = 0.5;
    
    /**
     * 默认最大token数
     */
    private Integer maxTokens = 2048;
    
    /**
     * 是否流式返回
     */
    private Boolean stream = false;
    
    /**
     * 连接超时时间（毫秒）
     */
    private Integer connectTimeout = 30000;
    
    /**
     * 读取超时时间（毫秒）
     */
    private Integer readTimeout = 60000;
    
    /**
     * WebSocket返回间隔时间（毫秒）
     * 用于控制流式返回的速度，0表示不延迟
     */
    private Long streamDelay = 0L;
    
    /**
     * WebSocket响应超时时间（秒）
     * 用于等待WebSocket完整响应的最大时间
     */
    private Long wsResponseTimeout = 300L; // 默认5分钟
    
    /**
     * SSE流式响应超时时间（毫秒）
     * 用于SSE连接的最大保持时间
     */
    private Long sseTimeout = 600000L; // 默认10分钟
}
