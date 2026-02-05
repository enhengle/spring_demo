package com.practise.demo.util;

import com.practise.demo.config.SparkAiConfig;
import com.practise.demo.model.dto.SparkChatRequest;
import com.practise.demo.model.dto.SparkChatResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.*;

/**
 * 讯飞星火AI工具类
 * 提供与讯飞星火大模型交互的通用方法
 * 
 * @author system
 */
@Component
public class SparkAiUtil {
    
    private static final Logger logger = LoggerFactory.getLogger(SparkAiUtil.class);
    
    @Autowired
    private SparkAiConfig sparkAiConfig;
    
    @Autowired
    private RestTemplate restTemplate;
    
    
    /**
     * 发送聊天消息（非流式）
     * 
     * @param messages 消息列表
     * @return 响应结果
     */
    public SparkChatResponse chat(List<SparkChatRequest.Message> messages) {
        return chat(messages, null, null, null);
    }
    
    /**
     * 发送聊天消息（非流式）
     * 
     * @param messages 消息列表
     * @param temperature 温度参数
     * @param maxTokens 最大token数
     * @return 响应结果
     */
    public SparkChatResponse chat(List<SparkChatRequest.Message> messages, 
                                  Double temperature, 
                                  Integer maxTokens) {
        return chat(messages, temperature, maxTokens, null);
    }
    
    /**
     * 发送聊天消息（非流式）
     * 参考文档：https://www.xfyun.cn/doc/spark/X1http.html
     * 
     * @param messages 消息列表
     * @param temperature 温度参数
     * @param maxTokens 最大token数
     * @param model 模型版本
     * @return 响应结果
     */
    public SparkChatResponse chat(List<SparkChatRequest.Message> messages,
                                  Double temperature,
                                  Integer maxTokens,
                                  String model) {
        try {
            // 检查API Password配置
            if (sparkAiConfig.getApiPassword() == null || sparkAiConfig.getApiPassword().isEmpty()) {
                throw new IllegalArgumentException("API Password未配置，请在控制台获取：https://console.xfyun.cn/services/bmx1");
            }
            
            SparkChatRequest request = new SparkChatRequest();
            request.setMessages(messages);
            request.setModel(model != null ? model : sparkAiConfig.getModel());
            request.setTemperature(temperature != null ? temperature : sparkAiConfig.getTemperature());
            request.setMaxTokens(maxTokens != null ? maxTokens : sparkAiConfig.getMaxTokens());
            request.setStream(sparkAiConfig.getStream() != null ? sparkAiConfig.getStream() : false);
            
            // 使用官方HTTP API地址
            String url = sparkAiConfig.getApiUrl();
            logger.debug("请求URL: {}", url);
            
            // 构建请求头（使用Bearer Token认证）
            HttpHeaders headers = buildHeaders();
            
            // 发送请求
            HttpEntity<SparkChatRequest> entity = new HttpEntity<>(request, headers);
            logger.debug("请求体: {}", request);
            
            ResponseEntity<SparkChatResponse> response = restTemplate.exchange(
                    url,
                    HttpMethod.POST,
                    entity,
                    SparkChatResponse.class
            );
            
            if (response.getStatusCode() == HttpStatus.OK && response.getBody() != null) {
                SparkChatResponse body = response.getBody();
                logger.info("讯飞星火API调用成功");
                return body;
            } else {
                logger.error("讯飞星火API调用失败，状态码: {}", response.getStatusCode());
                throw new RuntimeException("讯飞星火API调用失败，状态码: " + response.getStatusCode());
            }
            
        } catch (org.springframework.web.client.HttpClientErrorException.Unauthorized e) {
            logger.error("讯飞星火API认证失败 (401 Unauthorized)");
            logger.error("请检查以下配置:");
            logger.error("1. API Password: {}", sparkAiConfig.getApiPassword() != null ? 
                    (sparkAiConfig.getApiPassword().length() > 10 ? 
                            sparkAiConfig.getApiPassword().substring(0, 10) + "..." : "已配置") : "未配置");
            logger.error("2. 获取地址: https://console.xfyun.cn/services/bmx1");
            logger.error("3. 请求URL: {}", sparkAiConfig.getApiUrl());
            logger.error("4. 响应内容: {}", e.getResponseBodyAsString());
            throw new RuntimeException("讯飞星火API认证失败，请检查API Password配置是否正确。获取地址：https://console.xfyun.cn/services/bmx1", e);
        } catch (org.springframework.web.client.HttpClientErrorException e) {
            logger.error("讯飞星火API调用失败，HTTP状态码: {}", e.getStatusCode());
            logger.error("响应内容: {}", e.getResponseBodyAsString());
            throw new RuntimeException("讯飞星火API调用失败: " + e.getStatusCode() + " - " + 
                    e.getResponseBodyAsString(), e);
        } catch (Exception e) {
            logger.error("调用讯飞星火API异常", e);
            throw new RuntimeException("调用讯飞星火API异常: " + e.getMessage(), e);
        }
    }
    
    /**
     * 简单对话（单轮）
     * 
     * @param userMessage 用户消息
     * @return AI回复
     */
    public String simpleChat(String userMessage) {
        List<SparkChatRequest.Message> messages = new ArrayList<>();
        SparkChatRequest.Message message = new SparkChatRequest.Message();
        message.setRole("user");
        message.setContent(userMessage);
        messages.add(message);
        
        SparkChatResponse response = chat(messages);
        
        if (response.getChoices() != null 
                && !response.getChoices().isEmpty()
                && response.getChoices().get(0).getMessage() != null) {
            return response.getChoices().get(0).getMessage().getContent();
        }
        
        return "抱歉，无法获取回复";
    }
    
    /**
     * 多轮对话
     * 
     * @param conversationHistory 对话历史
     * @param userMessage 当前用户消息
     * @return AI回复
     */
    public String multiTurnChat(List<SparkChatRequest.Message> conversationHistory, String userMessage) {
        List<SparkChatRequest.Message> messages = new ArrayList<>(conversationHistory);
        SparkChatRequest.Message message = new SparkChatRequest.Message();
        message.setRole("user");
        message.setContent(userMessage);
        messages.add(message);
        
        SparkChatResponse response = chat(messages);
        
        if (response.getChoices() != null 
                && !response.getChoices().isEmpty()
                && response.getChoices().get(0).getMessage() != null) {
            return response.getChoices().get(0).getMessage().getContent();
        }
        
        return "抱歉，无法获取回复";
    }
    
    /**
     * 构建请求头
     * 参考文档：https://www.xfyun.cn/doc/spark/X1http.html
     * 使用Bearer Token认证方式
     */
    private HttpHeaders buildHeaders() {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        
        // 使用Bearer Token认证
        String apiPassword = sparkAiConfig.getApiPassword();
        if (apiPassword == null || apiPassword.isEmpty()) {
            throw new IllegalArgumentException("API Password未配置，请在控制台获取：https://console.xfyun.cn/services/bmx1");
        }
        
        headers.set("Authorization", "Bearer " + apiPassword);
        
        logger.debug("请求头已设置，Authorization: Bearer {}", 
                apiPassword.length() > 10 ? apiPassword.substring(0, 10) + "..." : apiPassword);
        
        return headers;
    }
}
