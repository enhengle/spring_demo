package com.practise.demo.service;

import com.practise.demo.model.dto.SparkChatRequest;
import com.practise.demo.model.dto.SparkChatResponse;
import com.practise.demo.util.SparkAiUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * 讯飞星火AI服务类
 * 提供AI相关的业务方法
 * 
 * @author system
 */
@Service
public class SparkAiService {
    
    private static final Logger logger = LoggerFactory.getLogger(SparkAiService.class);
    
    @Autowired
    private SparkAiUtil sparkAiUtil;
    
    /**
     * 简单对话
     * 
     * @param userMessage 用户消息
     * @return AI回复
     */
    public String simpleChat(String userMessage) {
        logger.info("收到用户消息: {}", userMessage);
        try {
            String response = sparkAiUtil.simpleChat(userMessage);
            logger.info("AI回复: {}", response);
            return response;
        } catch (Exception e) {
            logger.error("AI对话失败", e);
            throw new RuntimeException("AI对话失败: " + e.getMessage(), e);
        }
    }
    
    /**
     * 多轮对话
     * 
     * @param conversationHistory 对话历史
     * @param userMessage 当前用户消息
     * @return AI回复
     */
    public String multiTurnChat(List<SparkChatRequest.Message> conversationHistory, String userMessage) {
        logger.info("收到用户消息: {}", userMessage);
        try {
            String response = sparkAiUtil.multiTurnChat(conversationHistory, userMessage);
            logger.info("AI回复: {}", response);
            return response;
        } catch (Exception e) {
            logger.error("AI多轮对话失败", e);
            throw new RuntimeException("AI多轮对话失败: " + e.getMessage(), e);
        }
    }
    
    /**
     * 完整对话（返回完整响应对象）
     * 
     * @param messages 消息列表
     * @return 完整响应
     */
    public SparkChatResponse chat(List<SparkChatRequest.Message> messages) {
        logger.info("发送消息数量: {}", messages.size());
        try {
            SparkChatResponse response = sparkAiUtil.chat(messages);
            logger.info("AI响应成功");
            return response;
        } catch (Exception e) {
            logger.error("AI对话失败", e);
            throw new RuntimeException("AI对话失败: " + e.getMessage(), e);
        }
    }
    
    /**
     * 带参数的对话
     * 
     * @param messages 消息列表
     * @param temperature 温度参数
     * @param maxTokens 最大token数
     * @param model 模型版本
     * @return 完整响应
     */
    public SparkChatResponse chatWithParams(List<SparkChatRequest.Message> messages,
                                            Double temperature,
                                            Integer maxTokens,
                                            String model) {
        logger.info("发送消息数量: {}, temperature: {}, maxTokens: {}, model: {}", 
                messages.size(), temperature, maxTokens, model);
        try {
            SparkChatResponse response = sparkAiUtil.chat(messages, temperature, maxTokens, model);
            logger.info("AI响应成功");
            return response;
        } catch (Exception e) {
            logger.error("AI对话失败", e);
            throw new RuntimeException("AI对话失败: " + e.getMessage(), e);
        }
    }
    
    /**
     * 创建对话历史
     * 
     * @param userMessages 用户消息列表
     * @param assistantMessages 助手消息列表
     * @return 对话历史
     */
    public List<SparkChatRequest.Message> createConversationHistory(List<String> userMessages,
                                                                     List<String> assistantMessages) {
        List<SparkChatRequest.Message> history = new ArrayList<>();
        
        int maxSize = Math.max(userMessages.size(), assistantMessages.size());
        for (int i = 0; i < maxSize; i++) {
            if (i < userMessages.size()) {
                SparkChatRequest.Message userMsg = new SparkChatRequest.Message();
                userMsg.setRole("user");
                userMsg.setContent(userMessages.get(i));
                history.add(userMsg);
            }
            
            if (i < assistantMessages.size()) {
                SparkChatRequest.Message assistantMsg = new SparkChatRequest.Message();
                assistantMsg.setRole("assistant");
                assistantMsg.setContent(assistantMessages.get(i));
                history.add(assistantMsg);
            }
        }
        
        return history;
    }
}
