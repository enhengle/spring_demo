package com.practise.demo.controller;

import com.practise.demo.common.constant.ErrorCode;
import com.practise.demo.model.dto.DifyChatRequestDTO;
import com.practise.demo.model.dto.DifyChatResponseDTO;
import com.practise.demo.response.Response;
import com.practise.demo.service.DifyService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

/**
 * Dify控制器
 * 
 * @author system
 */
@RestController
@RequestMapping("/api/dify")
@Tag(name = "Dify AI助手", description = "Dify AI对话接口")
public class DifyController {
    
    private static final Logger logger = LoggerFactory.getLogger(DifyController.class);
    
    @Autowired
    private DifyService difyService;
    
    /**
     * 发送聊天消息（阻塞模式或流式模式）
     */
    @PostMapping("/chat")
    @Operation(summary = "发送聊天消息", description = "支持阻塞模式和流式模式")
    public Object sendChatMessage(@Validated @RequestBody DifyChatRequestDTO request) {
        logger.info("接收Dify聊天请求：message={}, responseMode={}", 
                request.getMessage(), request.getResponseMode());
        
        // 根据响应模式选择处理方式
        String responseMode = request.getResponseMode();
        if (responseMode == null || responseMode.isEmpty()) {
            responseMode = "blocking";
        }
        
        if ("streaming".equalsIgnoreCase(responseMode)) {
            // 流式模式：返回SSE
            SseEmitter emitter = new SseEmitter(300000L); // 5分钟超时
            emitter.onTimeout(() -> {
                logger.warn("SSE连接超时");
                emitter.complete();
            });
            emitter.onError((ex) -> {
                logger.error("SSE连接错误", ex);
                emitter.completeWithError(ex);
            });
            difyService.sendChatMessageStream(request, emitter);
            return emitter;
        } else {
            // 阻塞模式：等待完整响应
            try {
                DifyChatResponseDTO response = difyService.sendChatMessage(request);
                return Response.ok(response);
            } catch (Exception e) {
                logger.error("Dify聊天失败", e);
                return Response.error(ErrorCode.OPERATION_FAILED.getCode(), 
                        "Dify聊天失败: " + e.getMessage());
            }
        }
    }
}
