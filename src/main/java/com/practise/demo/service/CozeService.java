package com.practise.demo.service;

import com.practise.demo.model.dto.CozeWorkflowRequestDTO;
import com.practise.demo.model.dto.CozeWorkflowResponseDTO;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

/**
 * Coze服务接口
 * 
 * @author system
 */
public interface CozeService {
    
    /**
     * 调用Coze SQL识别工作流（阻塞模式）
     * 
     * @param request 工作流请求
     * @return 工作流响应
     */
    CozeWorkflowResponseDTO executeWorkflow(CozeWorkflowRequestDTO request);
    
    /**
     * 调用Coze SQL识别工作流（流式模式）
     * 
     * @param request 工作流请求
     * @param emitter SSE发射器，用于流式推送数据
     */
    void executeWorkflowStream(CozeWorkflowRequestDTO request, SseEmitter emitter);
}
