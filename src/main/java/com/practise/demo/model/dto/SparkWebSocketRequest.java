package com.practise.demo.model.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import java.util.List;
import java.util.Map;

/**
 * 讯飞星火WebSocket请求DTO
 * 参考文档：https://www.xfyun.cn/doc/spark/X1ws.html
 * 
 * @author system
 */
@Data
public class SparkWebSocketRequest {
    
    /**
     * 请求头
     */
    private Header header;
    
    /**
     * 请求参数
     */
    private Parameter parameter;
    
    /**
     * 请求负载
     */
    private Payload payload;
    
    @Data
    public static class Header {
        /**
         * 应用ID
         */
        @JsonProperty("app_id")
        private String appId;
        
        /**
         * 用户ID（可选）
         */
        private String uid;
    }
    
    @Data
    public static class Parameter {
        /**
         * 聊天参数
         */
        private Chat chat;
    }
    
    @Data
    public static class Chat {
        /**
         * 模型domain
         */
        private String domain = "spark-x";
        
        /**
         * 最大token数
         */
        private Integer maxTokens = 4096;
        
        /**
         * 温度参数
         */
        private Double temperature = 0.5;
        
        /**
         * 存在惩罚
         */
        private Double presencePenalty = 1.0;
        
        /**
         * 频率惩罚
         */
        private Double frequencyPenalty = 0.02;
        
        /**
         * Top K
         */
        private Integer topK = 5;
        
        /**
         * 工具列表
         */
        private List<Map<String, Object>> tools;
        
        /**
         * 思考模式（X1.5新增）
         */
        private String thinking;
    }
    
    @Data
    public static class Payload {
        /**
         * 消息内容
         */
        private Message message;
    }
    
    @Data
    public static class Message {
        /**
         * 文本消息列表
         */
        private List<Text> text;
    }
    
    @Data
    public static class Text {
        /**
         * 角色：user, assistant, system
         */
        private String role;
        
        /**
         * 消息内容
         */
        private String content;
    }
}
