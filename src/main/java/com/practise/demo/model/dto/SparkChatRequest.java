package com.practise.demo.model.dto;

import lombok.Data;
import java.util.List;

/**
 * 讯飞星火聊天请求DTO
 * 
 * @author system
 */
@Data
public class SparkChatRequest {
    
    /**
     * 消息列表
     */
    private List<Message> messages;
    
    /**
     * 模型版本（如：general, generalv2, generalv3）
     */
    private String model = "generalv3";
    
    /**
     * 温度参数（0-1），控制随机性
     */
    private Double temperature = 0.5;
    
    /**
     * 最大生成token数
     */
    private Integer maxTokens = 2048;
    
    /**
     * 是否流式返回
     */
    private Boolean stream = false;
    
    /**
     * 消息对象
     */
    @Data
    public static class Message {
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
