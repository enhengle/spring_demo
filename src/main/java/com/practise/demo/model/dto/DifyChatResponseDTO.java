package com.practise.demo.model.dto;

import lombok.Data;

/**
 * Dify聊天响应DTO
 * 
 * @author system
 */
@Data
public class DifyChatResponseDTO {
    
    /**
     * 助手回复
     */
    private String answer;
    
    /**
     * 对话ID
     */
    private String conversationId;
    
    /**
     * 消息ID
     */
    private String messageId;
}
