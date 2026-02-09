package com.practise.demo.model.dto;

import lombok.Data;
import javax.validation.constraints.NotBlank;

/**
 * Dify聊天请求DTO
 * 
 * @author system
 */
@Data
public class DifyChatRequestDTO {
    
    /**
     * 用户消息
     */
    @NotBlank(message = "消息不能为空")
    private String message;
    
    /**
     * 对话ID（可选，用于保持上下文）
     */
    private String conversationId;
    
    /**
     * 响应模式：blocking（阻塞模式，等待完整响应）或 streaming（流式模式，实时返回）
     * 默认为 blocking
     */
    private String responseMode = "blocking";
}
