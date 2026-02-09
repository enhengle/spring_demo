package com.practise.demo.service;

import com.practise.demo.model.dto.DifyChatRequestDTO;
import com.practise.demo.model.dto.DifyChatResponseDTO;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

/**
 * Dify服务接口
 * 
 * @author system
 */
public interface DifyService {
    
    /**
     * 发送聊天消息到Dify（阻塞模式）
     * 
     * @param request 聊天请求
     * @return 聊天响应
     */
    DifyChatResponseDTO sendChatMessage(DifyChatRequestDTO request);
    
    /**
     * 发送聊天消息到Dify（流式模式）
     * 
     * @param request 聊天请求
     * @param emitter SSE发射器，用于流式推送数据
     */
    void sendChatMessageStream(DifyChatRequestDTO request, SseEmitter emitter);
}
