package com.practise.demo.controller;

import com.practise.demo.common.constant.ErrorCode;
import com.practise.demo.model.dto.SparkChatRequest;
import com.practise.demo.model.dto.SparkChatResponse;
import com.practise.demo.response.Response;
import com.practise.demo.service.SparkAiService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 讯飞星火AI控制器
 * 提供AI对话相关的REST API接口
 * 
 * @author system
 */
@RestController
@RequestMapping("/api/spark-ai")
@Tag(name = "讯飞星火AI", description = "讯飞星火大模型对话接口")
public class SparkAiController {
    
    @Autowired
    private SparkAiService sparkAiService;
    
    /**
     * 简单对话接口
     */
    @PostMapping("/chat/simple")
    @Operation(summary = "简单对话", description = "单轮对话，用户发送一条消息，AI回复一条消息")
    public Response<String> simpleChat(
            @Parameter(description = "用户消息", required = true)
            @RequestParam("message") String message) {
        
        if (message == null || message.trim().isEmpty()) {
            return Response.error(ErrorCode.PARAM_ERROR.getCode(), "消息不能为空");
        }
        
        try {
            String response = sparkAiService.simpleChat(message);
            return Response.ok(response);
        } catch (Exception e) {
            return Response.error(ErrorCode.OPERATION_FAILED.getCode(), "AI对话失败: " + e.getMessage());
        }
    }
    
    /**
     * 多轮对话接口
     */
    @PostMapping("/chat/multi-turn")
    @Operation(summary = "多轮对话", description = "支持多轮对话，需要传入对话历史")
    public Response<String> multiTurnChat(
            @Parameter(description = "对话历史", required = true)
            @RequestBody List<SparkChatRequest.Message> conversationHistory,
            @Parameter(description = "当前用户消息", required = true)
            @RequestParam("message") String message) {
        
        if (message == null || message.trim().isEmpty()) {
            return Response.error(ErrorCode.PARAM_ERROR.getCode(), "消息不能为空");
        }
        
        if (conversationHistory == null || conversationHistory.isEmpty()) {
            return Response.error(ErrorCode.PARAM_ERROR.getCode(), "对话历史不能为空");
        }
        
        try {
            String response = sparkAiService.multiTurnChat(conversationHistory, message);
            return Response.ok(response);
        } catch (Exception e) {
            return Response.error(ErrorCode.OPERATION_FAILED.getCode(), "AI多轮对话失败: " + e.getMessage());
        }
    }
    
    /**
     * 完整对话接口（返回完整响应对象）
     */
    @PostMapping("/chat")
    @Operation(summary = "完整对话", description = "发送消息列表，返回完整的AI响应对象")
    public Response<SparkChatResponse> chat(
            @Parameter(description = "消息列表", required = true)
            @RequestBody List<SparkChatRequest.Message> messages) {
        
        if (messages == null || messages.isEmpty()) {
            return Response.error(ErrorCode.PARAM_ERROR.getCode(), "消息列表不能为空");
        }
        
        try {
            SparkChatResponse response = sparkAiService.chat(messages);
            return Response.ok(response);
        } catch (Exception e) {
            return Response.error(ErrorCode.OPERATION_FAILED.getCode(), "AI对话失败: " + e.getMessage());
        }
    }
    
    /**
     * 带参数的对话接口
     */
    @PostMapping("/chat/advanced")
    @Operation(summary = "高级对话", description = "支持自定义温度、最大token数、模型版本等参数")
    public Response<SparkChatResponse> chatWithParams(
            @Parameter(description = "消息列表", required = true)
            @RequestBody List<SparkChatRequest.Message> messages,
            @Parameter(description = "温度参数（0-1）")
            @RequestParam(value = "temperature", required = false) Double temperature,
            @Parameter(description = "最大token数")
            @RequestParam(value = "maxTokens", required = false) Integer maxTokens,
            @Parameter(description = "模型版本")
            @RequestParam(value = "model", required = false) String model) {
        
        if (messages == null || messages.isEmpty()) {
            return Response.error(ErrorCode.PARAM_ERROR.getCode(), "消息列表不能为空");
        }
        
        try {
            SparkChatResponse response = sparkAiService.chatWithParams(messages, temperature, maxTokens, model);
            return Response.ok(response);
        } catch (Exception e) {
            return Response.error(ErrorCode.OPERATION_FAILED.getCode(), "AI对话失败: " + e.getMessage());
        }
    }
    
    /**
     * 创建对话历史
     */
    @PostMapping("/conversation/create")
    @Operation(summary = "创建对话历史", description = "根据用户消息和助手消息列表创建对话历史")
    public Response<List<SparkChatRequest.Message>> createConversationHistory(
            @Parameter(description = "用户消息列表", required = true)
            @RequestParam("userMessages") List<String> userMessages,
            @Parameter(description = "助手消息列表", required = true)
            @RequestParam("assistantMessages") List<String> assistantMessages) {
        
        if (userMessages == null || userMessages.isEmpty()) {
            return Response.error(ErrorCode.PARAM_ERROR.getCode(), "用户消息列表不能为空");
        }
        
        try {
            List<SparkChatRequest.Message> history = 
                    sparkAiService.createConversationHistory(userMessages, assistantMessages);
            return Response.ok(history);
        } catch (Exception e) {
            return Response.error(ErrorCode.OPERATION_FAILED.getCode(), "创建对话历史失败: " + e.getMessage());
        }
    }
}
