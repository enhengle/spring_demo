package com.practise.demo.controller;

import com.practise.demo.common.constant.ErrorCode;
import com.practise.demo.model.dto.SparkWebSocketRequest;
import com.practise.demo.model.dto.SparkWebSocketResponse;
import com.practise.demo.response.Response;
import com.practise.demo.service.SparkAiWebSocketService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.util.List;
import java.util.concurrent.CompletableFuture;

/**
 * 讯飞星火WebSocket控制器
 * 提供实时流式响应的REST API接口（使用SSE）
 * 
 * @author system
 */
@RestController
@RequestMapping("/api/spark-ai/ws")
@Tag(name = "讯飞星火WebSocket", description = "讯飞星火大模型WebSocket实时流式接口")
public class SparkAiWebSocketController {
    
    private static final Logger logger = LoggerFactory.getLogger(SparkAiWebSocketController.class);
    
    @Autowired
    private SparkAiWebSocketService sparkAiWebSocketService;
    
    /**
     * 流式聊天接口（SSE方式，Postman可测试）
     * 使用Server-Sent Events将WebSocket的流式数据转换为HTTP流式响应
     */
    @PostMapping(value = "/chat/stream", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    @Operation(summary = "流式聊天（SSE）", description = "实时返回AI回复，使用SSE协议，Postman可直接测试")
    public SseEmitter streamChat(
            @Parameter(description = "消息列表", required = true)
            @RequestBody List<SparkWebSocketRequest.Text> messages,
            @Parameter(description = "温度参数")
            @RequestParam(value = "temperature", required = false) Double temperature,
            @Parameter(description = "最大token数")
            @RequestParam(value = "maxTokens", required = false) Integer maxTokens,
            @Parameter(description = "思考模式")
            @RequestParam(value = "thinking", required = false) String thinking,
            @Parameter(description = "返回间隔时间（毫秒），用于控制流式返回速度，0表示不延迟")
            @RequestParam(value = "streamDelay", required = false) Long streamDelay) {
        
        if (messages == null || messages.isEmpty()) {
            throw new IllegalArgumentException("消息列表不能为空");
        }
        
        // 创建SSE Emitter（使用配置的超时时间）
        Long sseTimeout = sparkAiWebSocketService.getSseTimeout() != null ? 
                sparkAiWebSocketService.getSseTimeout() : 600000L; // 默认10分钟
        SseEmitter emitter = new SseEmitter(sseTimeout);
        
        // 异步处理
        CompletableFuture.runAsync(() -> {
            try {
                sparkAiWebSocketService.streamChat(
                        messages,
                        temperature,
                        maxTokens,
                        thinking,
                        streamDelay,
                        chunk -> {
                            try {
                                // 发送数据块
                                if (chunk != null && !chunk.isEmpty()) {
                                    logger.debug("SSE发送数据块，长度: {}, 内容: {}", chunk.length(), 
                                            chunk.length() > 50 ? chunk.substring(0, 50) + "..." : chunk);
                                    emitter.send(SseEmitter.event()
                                            .name("message")
                                            .data(chunk));
                                }
                            } catch (Exception e) {
                                // 只记录错误，不中断流式响应
                                logger.error("发送SSE消息失败，但继续处理后续数据", e);
                            }
                        }
                );
                
                // 发送完成事件
                try {
                    emitter.send(SseEmitter.event()
                            .name("complete")
                            .data("流式响应完成"));
                    emitter.complete();
                } catch (Exception e) {
                    logger.error("发送完成事件失败", e);
                    emitter.completeWithError(e);
                }
                
            } catch (Exception e) {
                logger.error("流式聊天失败", e);
                try {
                    emitter.send(SseEmitter.event()
                            .name("error")
                            .data("流式聊天失败: " + e.getMessage()));
                    emitter.complete();
                } catch (Exception ex) {
                    logger.error("发送错误消息失败", ex);
                    emitter.completeWithError(ex);
                }
            }
        });
        
        return emitter;
    }
    
    /**
     * 简单流式聊天接口（SSE方式）
     */
    @PostMapping(value = "/chat/simple/stream", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    @Operation(summary = "简单流式聊天（SSE）", description = "单轮对话，实时返回AI回复")
    public SseEmitter simpleStreamChat(
            @Parameter(description = "用户消息", required = true)
            @RequestParam("message") String message,
            @Parameter(description = "返回间隔时间（毫秒），用于控制流式返回速度，0表示不延迟")
            @RequestParam(value = "streamDelay", required = false) Long streamDelay) {
        
        if (message == null || message.trim().isEmpty()) {
            throw new IllegalArgumentException("消息不能为空");
        }
        
        // 创建SSE Emitter
        SseEmitter emitter = new SseEmitter(60000L);
        
        // 异步处理
        CompletableFuture.runAsync(() -> {
            try {
                sparkAiWebSocketService.simpleStreamChat(
                        message,
                        streamDelay,
                        chunk -> {
                            try {
                                if (chunk != null && !chunk.isEmpty()) {
                                    logger.debug("SSE发送数据块，长度: {}", chunk.length());
                                    emitter.send(SseEmitter.event()
                                            .name("message")
                                            .data(chunk));
                                }
                            } catch (Exception e) {
                                // 只记录错误，不中断流式响应
                                logger.error("发送SSE消息失败，但继续处理后续数据", e);
                            }
                        }
                );
                
                // 发送完成事件
                try {
                    emitter.send(SseEmitter.event()
                            .name("complete")
                            .data("流式响应完成"));
                    emitter.complete();
                } catch (Exception e) {
                    logger.error("发送完成事件失败", e);
                    emitter.completeWithError(e);
                }
                
            } catch (Exception e) {
                logger.error("简单流式聊天失败", e);
                try {
                    emitter.send(SseEmitter.event()
                            .name("error")
                            .data("流式聊天失败: " + e.getMessage()));
                    emitter.complete();
                } catch (Exception ex) {
                    logger.error("发送错误消息失败", ex);
                    emitter.completeWithError(ex);
                }
            }
        });
        
        return emitter;
    }
    
    /**
     * 非流式聊天接口（等待完整响应）
     */
    @PostMapping("/chat")
    @Operation(summary = "WebSocket聊天（非流式）", description = "等待完整响应后返回")
    public Response<SparkWebSocketResponse> chat(
            @Parameter(description = "消息列表", required = true)
            @RequestBody List<SparkWebSocketRequest.Text> messages,
            @Parameter(description = "温度参数")
            @RequestParam(value = "temperature", required = false) Double temperature,
            @Parameter(description = "最大token数")
            @RequestParam(value = "maxTokens", required = false) Integer maxTokens,
            @Parameter(description = "思考模式")
            @RequestParam(value = "thinking", required = false) String thinking,
            @Parameter(description = "返回间隔时间（毫秒），用于控制流式返回速度，非流式模式下无效")
            @RequestParam(value = "streamDelay", required = false) Long streamDelay) {
        
        if (messages == null || messages.isEmpty()) {
            return Response.error(ErrorCode.PARAM_ERROR.getCode(), "消息列表不能为空");
        }
        
        try {
            SparkWebSocketResponse response = sparkAiWebSocketService.streamChat(
                    messages,
                    temperature,
                    maxTokens,
                    thinking,
                    streamDelay,
                    null // 非流式，不需要回调
            );
            return Response.ok(response);
        } catch (Exception e) {
            return Response.error(ErrorCode.OPERATION_FAILED.getCode(), "WebSocket聊天失败: " + e.getMessage());
        }
    }
}
