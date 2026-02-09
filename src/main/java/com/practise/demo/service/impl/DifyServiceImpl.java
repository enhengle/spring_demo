package com.practise.demo.service.impl;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.practise.demo.common.constant.ErrorCode;
import com.practise.demo.config.DifyConfig;
import com.practise.demo.exception.ServerException;
import com.practise.demo.model.dto.DifyChatRequestDTO;
import com.practise.demo.model.dto.DifyChatResponseDTO;
import com.practise.demo.service.DifyService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

/**
 * Dify服务实现类
 * 
 * @author system
 */
@Service
public class DifyServiceImpl implements DifyService {
    
    private static final Logger logger = LoggerFactory.getLogger(DifyServiceImpl.class);
    
    @Autowired
    private RestTemplate restTemplate;
    
    @Autowired
    private ObjectMapper objectMapper;
    
    @Autowired
    private DifyConfig difyConfig;
    
    @Override
    public DifyChatResponseDTO sendChatMessage(DifyChatRequestDTO request) {
        logger.info("发送Dify聊天消息：message={}, responseMode={}", 
                request.getMessage(), request.getResponseMode());
        
        // 检查配置
        if (difyConfig.getApiKey() == null || difyConfig.getApiKey().isEmpty()) {
            throw new ServerException(ErrorCode.SYSTEM_ERROR, "Dify API Key未配置");
        }
        
        try {
            // 构建请求体
            Map<String, Object> requestBody = new HashMap<>();
            requestBody.put("inputs", new HashMap<>());
            requestBody.put("query", request.getMessage());
            
            String responseMode = request.getResponseMode();
            if (responseMode == null || responseMode.isEmpty()) {
                responseMode = "blocking";
            }
            requestBody.put("response_mode", responseMode);
            requestBody.put("user", "default-user");
            
            // 如果有对话ID，添加到请求中
            if (request.getConversationId() != null && !request.getConversationId().isEmpty()) {
                requestBody.put("conversation_id", request.getConversationId());
            }
            
            // 设置请求头
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            
            // 处理API Key
            String authHeader = difyConfig.getApiKey().trim();
            if (authHeader.startsWith("Bearer ")) {
                authHeader = authHeader.substring(7).trim();
            }
            if (!authHeader.startsWith("app-")) {
                logger.warn("API Key格式可能不正确，应该以 'app-' 开头");
            }
            authHeader = "Bearer " + authHeader;
            headers.set("Authorization", authHeader);
            
            HttpEntity<Map<String, Object>> httpEntity = new HttpEntity<>(requestBody, headers);
            
            // 构建API URL
            String apiUrl = difyConfig.getBaseUrl() + "/chat-messages";
            
            logger.info("调用Dify聊天助手API：url={}", apiUrl);
            
            // 发送请求
            ResponseEntity<String> response;
            try {
                response = restTemplate.exchange(
                        apiUrl,
                        HttpMethod.POST,
                        httpEntity,
                        String.class
                );
            } catch (org.springframework.web.client.HttpClientErrorException e) {
                String errorBody = e.getResponseBodyAsString();
                logger.error("Dify API调用失败：状态码={}, 响应体={}", e.getStatusCode(), errorBody);
                
                if (e.getStatusCode() == HttpStatus.UNAUTHORIZED) {
                    throw new ServerException(ErrorCode.SYSTEM_ERROR, 
                            "Dify API认证失败（401），请检查API Key是否正确");
                } else if (e.getStatusCode() == HttpStatus.NOT_FOUND) {
                    throw new ServerException(ErrorCode.SYSTEM_ERROR, 
                            "Dify API端点不存在（404），请检查API URL是否正确：" + apiUrl);
                } else {
                    throw new ServerException(ErrorCode.SYSTEM_ERROR, 
                            "Dify API调用失败：" + e.getStatusCode() + " - " + 
                            (errorBody != null && !errorBody.isEmpty() ? errorBody : e.getMessage()));
                }
            } catch (org.springframework.web.client.ResourceAccessException e) {
                logger.error("Dify API连接失败", e);
                throw new ServerException(ErrorCode.SYSTEM_ERROR, 
                        "无法连接到Dify服务，请检查网络连接和API URL：" + apiUrl);
            } catch (Exception e) {
                logger.error("Dify API调用异常", e);
                throw new ServerException(ErrorCode.SYSTEM_ERROR, 
                        "Dify API调用失败：" + e.getMessage());
            }
            
            if (response.getStatusCode() != HttpStatus.OK) {
                throw new ServerException(ErrorCode.SYSTEM_ERROR, 
                        "Dify API调用失败，状态码：" + response.getStatusCode());
            }
            
            // 解析响应
            String responseBody = response.getBody();
            logger.debug("Dify API响应：{}", responseBody);
            
            DifyChatResponseDTO responseDTO = parseDifyResponse(responseBody);
            
            logger.info("Dify聊天消息发送成功：conversationId={}", responseDTO.getConversationId());
            
            return responseDTO;
            
        } catch (ServerException e) {
            throw e;
        } catch (Exception e) {
            logger.error("Dify API调用失败", e);
            throw new ServerException(ErrorCode.SYSTEM_ERROR, 
                    "Dify API调用失败：" + e.getMessage());
        }
    }
    
    /**
     * 解析Dify聊天助手响应
     */
    private DifyChatResponseDTO parseDifyResponse(String responseBody) {
        try {
            JsonNode rootNode = objectMapper.readTree(responseBody);
            DifyChatResponseDTO responseDTO = new DifyChatResponseDTO();
            
            // 提取answer
            if (rootNode.has("answer")) {
                responseDTO.setAnswer(rootNode.get("answer").asText());
            }
            
            // 提取conversation_id
            if (rootNode.has("conversation_id")) {
                responseDTO.setConversationId(rootNode.get("conversation_id").asText());
            }
            
            // 提取message_id或id
            if (rootNode.has("message_id")) {
                responseDTO.setMessageId(rootNode.get("message_id").asText());
            } else if (rootNode.has("id")) {
                responseDTO.setMessageId(rootNode.get("id").asText());
            }
            
            // 兼容工作流格式
            JsonNode dataNode = rootNode.get("data");
            if (dataNode != null) {
                if (responseDTO.getAnswer() == null && dataNode.has("answer")) {
                    responseDTO.setAnswer(dataNode.get("answer").asText());
                }
                if (responseDTO.getConversationId() == null && dataNode.has("conversation_id")) {
                    responseDTO.setConversationId(dataNode.get("conversation_id").asText());
                }
                if (responseDTO.getMessageId() == null && dataNode.has("id")) {
                    responseDTO.setMessageId(dataNode.get("id").asText());
                }
                
                // 尝试从outputs中提取
                JsonNode outputsNode = dataNode.get("outputs");
                if (outputsNode != null && responseDTO.getAnswer() == null) {
                    if (outputsNode.has("answer")) {
                        responseDTO.setAnswer(outputsNode.get("answer").asText());
                    } else if (outputsNode.has("text")) {
                        responseDTO.setAnswer(outputsNode.get("text").asText());
                    } else if (outputsNode.has("result")) {
                        responseDTO.setAnswer(outputsNode.get("result").asText());
                    }
                }
            }
            
            // 如果仍然没有答案，使用默认值
            if (responseDTO.getAnswer() == null || responseDTO.getAnswer().isEmpty()) {
                logger.warn("Dify响应格式异常，无法提取答案：{}", responseBody);
                responseDTO.setAnswer("抱歉，我暂时无法回答这个问题。");
            }
            
            return responseDTO;
            
        } catch (Exception e) {
            logger.error("解析Dify响应失败", e);
            throw new ServerException(ErrorCode.SYSTEM_ERROR, 
                    "解析Dify响应失败：" + e.getMessage());
        }
    }
    
    @Override
    public void sendChatMessageStream(DifyChatRequestDTO request, SseEmitter emitter) {
        logger.info("发送Dify流式聊天消息：message={}", request.getMessage());
        
        // 检查配置
        if (difyConfig.getApiKey() == null || difyConfig.getApiKey().isEmpty()) {
            try {
                emitter.send(SseEmitter.event()
                        .name("error")
                        .data("Dify API Key未配置"));
                emitter.complete();
            } catch (Exception e) {
                logger.error("发送错误消息失败", e);
            }
            return;
        }
        
        try {
            // 构建请求体（流式模式）
            Map<String, Object> requestBody = new HashMap<>();
            requestBody.put("inputs", new HashMap<>());
            requestBody.put("query", request.getMessage());
            requestBody.put("response_mode", "streaming");
            requestBody.put("user", "default-user");
            
            // 如果有对话ID，添加到请求中
            if (request.getConversationId() != null && !request.getConversationId().isEmpty()) {
                requestBody.put("conversation_id", request.getConversationId());
            }
            
            // 处理API Key
            String authHeader = difyConfig.getApiKey().trim();
            if (authHeader.startsWith("Bearer ")) {
                authHeader = authHeader.substring(7).trim();
            }
            authHeader = "Bearer " + authHeader;
            
            // 构建API URL
            String apiUrl = difyConfig.getBaseUrl() + "/chat-messages";
            
            logger.info("调用Dify流式API：url={}", apiUrl);
            
            // 使用HttpURLConnection进行流式请求
            URL url = new URL(apiUrl);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("POST");
            connection.setRequestProperty("Content-Type", "application/json");
            connection.setRequestProperty("Authorization", authHeader);
            connection.setDoOutput(true);
            connection.setConnectTimeout(difyConfig.getConnectTimeout());
            connection.setReadTimeout(difyConfig.getReadTimeout());
            
            // 发送请求体
            String requestBodyJson = objectMapper.writeValueAsString(requestBody);
            connection.getOutputStream().write(requestBodyJson.getBytes(StandardCharsets.UTF_8));
            
            String conversationId = null;
            String messageId = null;
            StringBuilder fullAnswer = new StringBuilder();
            
            // 读取流式响应
            try (BufferedReader reader = new BufferedReader(
                    new InputStreamReader(connection.getInputStream(), StandardCharsets.UTF_8))) {
                
                String line;
                while ((line = reader.readLine()) != null) {
                    if (line.startsWith("data: ")) {
                        String data = line.substring(6);
                        
                        if ("[DONE]".equals(data)) {
                            emitter.send(SseEmitter.event()
                                    .name("done")
                                    .data(""));
                            break;
                        }
                        
                        try {
                            JsonNode eventNode = objectMapper.readTree(data);
                            String event = eventNode.has("event") ? eventNode.get("event").asText() : "";
                            
                            if ("message".equals(event) || "message_end".equals(event)) {
                                // 提取对话ID和消息ID
                                if (eventNode.has("conversation_id")) {
                                    conversationId = eventNode.get("conversation_id").asText();
                                }
                                if (eventNode.has("id")) {
                                    messageId = eventNode.get("id").asText();
                                }
                                
                                // 提取答案片段
                                if (eventNode.has("answer")) {
                                    String answerChunk = eventNode.get("answer").asText();
                                    fullAnswer.append(answerChunk);
                                    
                                    // 发送答案片段到前端
                                    Map<String, Object> chunkData = new HashMap<>();
                                    chunkData.put("chunk", answerChunk);
                                    chunkData.put("conversationId", conversationId);
                                    chunkData.put("messageId", messageId);
                                    
                                    emitter.send(SseEmitter.event()
                                            .name("message")
                                            .data(objectMapper.writeValueAsString(chunkData)));
                                }
                            } else if ("error".equals(event)) {
                                String errorMsg = eventNode.has("message") ? 
                                        eventNode.get("message").asText() : "未知错误";
                                emitter.send(SseEmitter.event()
                                        .name("error")
                                        .data(errorMsg));
                                break;
                            }
                        } catch (Exception e) {
                            logger.warn("解析流式数据失败：{}", data, e);
                        }
                    }
                }
                
                // 发送完整响应
                DifyChatResponseDTO finalResponse = new DifyChatResponseDTO();
                finalResponse.setAnswer(fullAnswer.toString());
                finalResponse.setConversationId(conversationId);
                finalResponse.setMessageId(messageId);
                
                emitter.send(SseEmitter.event()
                        .name("complete")
                        .data(objectMapper.writeValueAsString(finalResponse)));
                
            } finally {
                connection.disconnect();
            }
            
            emitter.complete();
            logger.info("Dify流式聊天消息发送成功：conversationId={}", conversationId);
            
        } catch (Exception e) {
            logger.error("Dify流式API调用失败", e);
            try {
                emitter.send(SseEmitter.event()
                        .name("error")
                        .data("Dify API调用失败：" + e.getMessage()));
                emitter.completeWithError(e);
            } catch (Exception ex) {
                logger.error("发送错误消息失败", ex);
            }
        }
    }
}
