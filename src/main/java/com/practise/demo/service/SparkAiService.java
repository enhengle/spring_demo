package com.practise.demo.service;

import com.practise.demo.model.dto.SparkChatRequest;
import com.practise.demo.model.dto.SparkChatResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.messages.Message;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.ai.chat.messages.AssistantMessage;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 讯飞星火AI服务类
 * 使用Spring AI框架提供AI相关的业务方法
 * 
 * @author system
 */
@Service
public class SparkAiService {
    
    private static final Logger logger = LoggerFactory.getLogger(SparkAiService.class);
    
    @Autowired
    private ChatClient chatClient;
    
    /**
     * 简单对话
     * 
     * @param userMessage 用户消息
     * @return AI回复
     */
    public String simpleChat(String userMessage) {
        logger.info("收到用户消息: {}", userMessage);
        try {
            String response = chatClient.prompt(userMessage).call().content();
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
            // 将对话历史转换为Spring AI的Message格式
            List<Message> messages = conversationHistory.stream()
                    .map(msg -> {
                        if ("user".equals(msg.getRole())) {
                            return new UserMessage(msg.getContent());
                        } else if ("assistant".equals(msg.getRole())) {
                            return new AssistantMessage(msg.getContent());
                        }
                        return null;
                    })
                    .filter(msg -> msg != null)
                    .collect(Collectors.toList());
            
            // 添加当前用户消息
            messages.add(new UserMessage(userMessage));
            
            String response = chatClient.prompt().messages(messages).call().content();
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
            // 将消息转换为Spring AI的Message格式
            List<Message> springAiMessages = messages.stream()
                    .map(msg -> {
                        if ("user".equals(msg.getRole())) {
                            return new UserMessage(msg.getContent());
                        } else if ("assistant".equals(msg.getRole())) {
                            return new AssistantMessage(msg.getContent());
                        }
                        return null;
                    })
                    .filter(msg -> msg != null)
                    .collect(Collectors.toList());
            
            ChatResponse chatResponse = chatClient.prompt().messages(springAiMessages).call().chatResponse();
            
            // 转换为SparkChatResponse格式
            SparkChatResponse response = new SparkChatResponse();
            if (chatResponse.getResult() != null && chatResponse.getResult().getOutput() != null) {
                String content = chatResponse.getResult().getOutput().getContent();
                SparkChatResponse.Choice choice = new SparkChatResponse.Choice();
                SparkChatResponse.Message message = new SparkChatResponse.Message();
                message.setContent(content);
                message.setRole("assistant");
                choice.setMessage(message);
                response.setChoices(List.of(choice));
            } else {
                // 如果getResult()为null，尝试直接从chatResponse获取内容
                logger.warn("ChatResponse.getResult()为null，尝试其他方式获取内容");
                // Spring AI的ChatResponse可能直接包含内容，需要根据实际API调整
            }
            
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
            // 将消息转换为Spring AI的Message格式
            List<Message> springAiMessages = messages.stream()
                    .map(msg -> {
                        if ("user".equals(msg.getRole())) {
                            return new UserMessage(msg.getContent());
                        } else if ("assistant".equals(msg.getRole())) {
                            return new AssistantMessage(msg.getContent());
                        }
                        return null;
                    })
                    .filter(msg -> msg != null)
                    .collect(Collectors.toList());
            
            // 使用ChatClient调用，参数已在配置中设置
            // 注意：如果需要动态参数，需要在配置中重新创建ChatModel
            ChatResponse chatResponse = chatClient.prompt().messages(springAiMessages).call().chatResponse();
            
            // 转换为SparkChatResponse格式
            SparkChatResponse response = new SparkChatResponse();
            if (chatResponse.getResult() != null && chatResponse.getResult().getOutput() != null) {
                String content = chatResponse.getResult().getOutput().getContent();
                SparkChatResponse.Choice choice = new SparkChatResponse.Choice();
                SparkChatResponse.Message message = new SparkChatResponse.Message();
                message.setContent(content);
                message.setRole("assistant");
                choice.setMessage(message);
                response.setChoices(List.of(choice));
            } else {
                // 如果getResult()为null，尝试直接从chatResponse获取内容
                logger.warn("ChatResponse.getResult()为null，尝试其他方式获取内容");
                // Spring AI的ChatResponse可能直接包含内容，需要根据实际API调整
            }
            
            logger.info("AI响应成功");
            return response;
        } catch (Exception e) {
            logger.error("AI对话失败", e);
            throw new RuntimeException("AI对话失败: " + e.getMessage(), e);
        }
    }
    
    /**
     * 流式对话
     * 
     * @param messages 消息列表
     * @return 流式响应
     */
    public Flux<String> streamChat(List<SparkChatRequest.Message> messages) {
        logger.info("发送流式消息数量: {}", messages.size());
        try {
            // 将消息转换为Spring AI的Message格式
            List<Message> springAiMessages = messages.stream()
                    .map(msg -> {
                        if ("user".equals(msg.getRole())) {
                            return new UserMessage(msg.getContent());
                        } else if ("assistant".equals(msg.getRole())) {
                            return new AssistantMessage(msg.getContent());
                        }
                        return null;
                    })
                    .filter(msg -> msg != null)
                    .collect(Collectors.toList());
            
            return chatClient.prompt().messages(springAiMessages).stream().content();
        } catch (Exception e) {
            logger.error("AI流式对话失败", e);
            return Flux.error(new RuntimeException("AI流式对话失败: " + e.getMessage(), e));
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
