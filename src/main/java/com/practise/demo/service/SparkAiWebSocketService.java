package com.practise.demo.service;

import com.practise.demo.config.SparkAiConfig;
import com.practise.demo.model.dto.SparkWebSocketRequest;
import com.practise.demo.model.dto.SparkWebSocketResponse;
import com.practise.demo.util.SparkAiWebSocketUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

/**
 * 讯飞星火WebSocket服务类
 * 
 * @author system
 */
@Service
public class SparkAiWebSocketService {
    
    private static final Logger logger = LoggerFactory.getLogger(SparkAiWebSocketService.class);
    
    @Autowired
    private SparkAiWebSocketUtil sparkAiWebSocketUtil;
    
    @Autowired
    private SparkAiConfig sparkAiConfig;
    
    /**
     * 获取SSE超时时间（毫秒）
     */
    public Long getSseTimeout() {
        return sparkAiConfig.getSseTimeout() != null ? sparkAiConfig.getSseTimeout() : 600000L;
    }
    
    /**
     * 流式聊天（实时返回结果）
     * 
     * @param messages 消息列表
     * @param chunkConsumer 实时数据消费者
     * @return 完整响应
     */
    public SparkWebSocketResponse streamChat(List<SparkWebSocketRequest.Text> messages,
                                             Consumer<String> chunkConsumer) {
        logger.info("开始WebSocket流式聊天，消息数量: {}", messages.size());
        try {
            SparkWebSocketResponse response = sparkAiWebSocketUtil.streamChat(
                    messages,
                    null,
                    null,
                    null,
                    chunkConsumer != null ? chunkConsumer::accept : null
            );
            logger.info("WebSocket流式聊天完成");
            return response;
        } catch (Exception e) {
            logger.error("WebSocket流式聊天失败", e);
            throw new RuntimeException("WebSocket流式聊天失败: " + e.getMessage(), e);
        }
    }
    
    /**
     * 流式聊天（带参数）
     * 
     * @param messages 消息列表
     * @param temperature 温度参数
     * @param maxTokens 最大token数
     * @param thinking 思考模式
     * @param chunkConsumer 实时数据消费者
     * @return 完整响应
     */
    public SparkWebSocketResponse streamChat(List<SparkWebSocketRequest.Text> messages,
                                             Double temperature,
                                             Integer maxTokens,
                                             String thinking,
                                             Consumer<String> chunkConsumer) {
        return streamChat(messages, temperature, maxTokens, thinking, null, chunkConsumer);
    }
    
    /**
     * 流式聊天（带参数和返回间隔）
     * 
     * @param messages 消息列表
     * @param temperature 温度参数
     * @param maxTokens 最大token数
     * @param thinking 思考模式
     * @param streamDelay 返回间隔时间（毫秒）
     * @param chunkConsumer 实时数据消费者
     * @return 完整响应
     */
    public SparkWebSocketResponse streamChat(List<SparkWebSocketRequest.Text> messages,
                                             Double temperature,
                                             Integer maxTokens,
                                             String thinking,
                                             Long streamDelay,
                                             Consumer<String> chunkConsumer) {
        return streamChat(messages, temperature, maxTokens, thinking, streamDelay, null, chunkConsumer);
    }
    
    /**
     * 流式聊天（带参数、返回间隔和超时时间）
     * 
     * @param messages 消息列表
     * @param temperature 温度参数
     * @param maxTokens 最大token数
     * @param thinking 思考模式
     * @param streamDelay 返回间隔时间（毫秒）
     * @param timeoutSeconds 超时时间（秒）
     * @param chunkConsumer 实时数据消费者
     * @return 完整响应
     */
    public SparkWebSocketResponse streamChat(List<SparkWebSocketRequest.Text> messages,
                                             Double temperature,
                                             Integer maxTokens,
                                             String thinking,
                                             Long streamDelay,
                                             Long timeoutSeconds,
                                             Consumer<String> chunkConsumer) {
        logger.info("开始WebSocket流式聊天，消息数量: {}, temperature: {}, maxTokens: {}, thinking: {}, streamDelay: {}ms, timeout: {}s", 
                messages.size(), temperature, maxTokens, thinking, streamDelay, timeoutSeconds);
        try {
            SparkWebSocketResponse response = sparkAiWebSocketUtil.streamChat(
                    messages,
                    temperature,
                    maxTokens,
                    thinking,
                    streamDelay,
                    timeoutSeconds,
                    chunkConsumer != null ? chunkConsumer::accept : null
            );
            logger.info("WebSocket流式聊天完成");
            return response;
        } catch (Exception e) {
            logger.error("WebSocket流式聊天失败", e);
            throw new RuntimeException("WebSocket流式聊天失败: " + e.getMessage(), e);
        }
    }
    
    /**
     * 简单对话（转换为WebSocket格式）
     * 
     * @param userMessage 用户消息
     * @param chunkConsumer 实时数据消费者
     * @return 完整响应
     */
    public SparkWebSocketResponse simpleStreamChat(String userMessage, Consumer<String> chunkConsumer) {
        return simpleStreamChat(userMessage, null, chunkConsumer);
    }
    
    /**
     * 简单对话（转换为WebSocket格式，支持返回间隔）
     * 
     * @param userMessage 用户消息
     * @param streamDelay 返回间隔时间（毫秒）
     * @param chunkConsumer 实时数据消费者
     * @return 完整响应
     */
    public SparkWebSocketResponse simpleStreamChat(String userMessage, Long streamDelay, Consumer<String> chunkConsumer) {
        List<SparkWebSocketRequest.Text> messages = new ArrayList<>();
        SparkWebSocketRequest.Text text = new SparkWebSocketRequest.Text();
        text.setRole("user");
        text.setContent(userMessage);
        messages.add(text);
        
        return streamChat(messages, null, null, null, streamDelay, chunkConsumer);
    }
}
