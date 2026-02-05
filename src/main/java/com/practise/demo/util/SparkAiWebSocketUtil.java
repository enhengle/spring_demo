package com.practise.demo.util;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.practise.demo.config.SparkAiConfig;
import org.springframework.beans.factory.annotation.Autowired;
import com.practise.demo.model.dto.SparkWebSocketRequest;
import com.practise.demo.model.dto.SparkWebSocketResponse;
import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.net.URI;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

/**
 * 讯飞星火WebSocket工具类
 * 参考文档：https://www.xfyun.cn/doc/spark/X1ws.html
 * 
 * @author system
 */
@Component
public class SparkAiWebSocketUtil {
    
    private static final Logger logger = LoggerFactory.getLogger(SparkAiWebSocketUtil.class);
    
    @Autowired
    private SparkAiConfig sparkAiConfig;
    
    @Autowired
    private ObjectMapper objectMapper;
    
    /**
     * 流式聊天（实时返回结果）
     * 
     * @param messages 消息列表
     * @param callback 实时回调函数
     * @return 完整响应
     */
    public SparkWebSocketResponse streamChat(List<SparkWebSocketRequest.Text> messages,
                                             StreamCallback callback) {
        return streamChat(messages, null, null, null, callback);
    }
    
    /**
     * 流式聊天（实时返回结果）
     * 
     * @param messages 消息列表
     * @param temperature 温度参数
     * @param maxTokens 最大token数
     * @param callback 实时回调函数
     * @return 完整响应
     */
    public SparkWebSocketResponse streamChat(List<SparkWebSocketRequest.Text> messages,
                                             Double temperature,
                                             Integer maxTokens,
                                             StreamCallback callback) {
        return streamChat(messages, temperature, maxTokens, null, callback);
    }
    
    /**
     * 流式聊天（实时返回结果）
     * 
     * @param messages 消息列表
     * @param temperature 温度参数
     * @param maxTokens 最大token数
     * @param thinking 思考模式（X1.5新增）
     * @param callback 实时回调函数
     * @return 完整响应
     */
    public SparkWebSocketResponse streamChat(List<SparkWebSocketRequest.Text> messages,
                                             Double temperature,
                                             Integer maxTokens,
                                             String thinking,
                                             StreamCallback callback) {
        return streamChat(messages, temperature, maxTokens, thinking, null, callback);
    }
    
    /**
     * 流式聊天（实时返回结果，支持自定义返回间隔）
     * 
     * @param messages 消息列表
     * @param temperature 温度参数
     * @param maxTokens 最大token数
     * @param thinking 思考模式（X1.5新增）
     * @param streamDelay 返回间隔时间（毫秒），null则使用配置中的默认值
     * @param callback 实时回调函数
     * @return 完整响应
     */
    public SparkWebSocketResponse streamChat(List<SparkWebSocketRequest.Text> messages,
                                             Double temperature,
                                             Integer maxTokens,
                                             String thinking,
                                             Long streamDelay,
                                             StreamCallback callback) {
        return streamChat(messages, temperature, maxTokens, thinking, streamDelay, null, callback);
    }
    
    /**
     * 流式聊天（实时返回结果，支持自定义返回间隔和超时时间）
     * 
     * @param messages 消息列表
     * @param temperature 温度参数
     * @param maxTokens 最大token数
     * @param thinking 思考模式（X1.5新增）
     * @param streamDelay 返回间隔时间（毫秒），null则使用配置中的默认值
     * @param timeoutSeconds 超时时间（秒），null则使用配置中的默认值
     * @param callback 实时回调函数
     * @return 完整响应
     */
    public SparkWebSocketResponse streamChat(List<SparkWebSocketRequest.Text> messages,
                                             Double temperature,
                                             Integer maxTokens,
                                             String thinking,
                                             Long streamDelay,
                                             Long timeoutSeconds,
                                             StreamCallback callback) {
        final Long finalTimeoutSeconds = timeoutSeconds; // 用于内部类访问
        CompletableFuture<SparkWebSocketResponse> future = new CompletableFuture<>();
        StringBuilder fullContent = new StringBuilder();
        SparkWebSocketResponse finalResponse = new SparkWebSocketResponse();
        
        try {
            // 检查配置
            if (sparkAiConfig.getAppId() == null || sparkAiConfig.getAppId().isEmpty()) {
                throw new IllegalArgumentException("App ID未配置");
            }
            if (sparkAiConfig.getApiKey() == null || sparkAiConfig.getApiKey().isEmpty()) {
                throw new IllegalArgumentException("API Key未配置");
            }
            if (sparkAiConfig.getApiSecret() == null || sparkAiConfig.getApiSecret().isEmpty()) {
                throw new IllegalArgumentException("API Secret未配置");
            }
            
            // 生成WebSocket URL（包含鉴权参数）
            String wsUrl = generateWebSocketUrl();
            logger.debug("WebSocket URL: {}", wsUrl);
            
            // 创建WebSocket客户端
            WebSocketClient client = new WebSocketClient(new URI(wsUrl)) {
                @Override
                public void onOpen(ServerHandshake handshake) {
                    logger.info("WebSocket连接已建立");
                    
                    try {
                        // 构建请求
                        SparkWebSocketRequest request = buildRequest(messages, temperature, maxTokens, thinking);
                        String requestJson = objectMapper.writeValueAsString(request);
                        logger.debug("发送请求: {}", requestJson);
                        
                        // 发送请求
                        send(requestJson);
                    } catch (Exception e) {
                        logger.error("发送WebSocket消息失败", e);
                        future.completeExceptionally(e);
                    }
                }
                
                @Override
                public void onMessage(String message) {
                    try {
                        logger.debug("收到消息: {}", message);
                        SparkWebSocketResponse response = objectMapper.readValue(message, SparkWebSocketResponse.class);
                        
                        // 处理响应
                        if (response.getHeader() != null) {
                            if (response.getHeader().getCode() != 0) {
                                logger.error("WebSocket返回错误: {}", response.getHeader().getMessage());
                                future.completeExceptionally(
                                        new RuntimeException("WebSocket错误: " + response.getHeader().getMessage()));
                                close();
                                return;
                            }
                            
                            // 提取内容
                            if (response.getPayload() != null 
                                    && response.getPayload().getChoices() != null
                                    && response.getPayload().getChoices().getText() != null
                                    && !response.getPayload().getChoices().getText().isEmpty()) {
                                // text是数组，取第一个元素
                                SparkWebSocketResponse.Text text = response.getPayload().getChoices().getText().get(0);
                                if (text != null && text.getContent() != null) {
                                    String content = text.getContent();
                                    fullContent.append(content);
                                    
                                    // 实时回调
                                    if (callback != null) {
                                        try {
                                            logger.debug("发送数据块，长度: {}, 内容: {}", content.length(), 
                                                    content.length() > 50 ? content.substring(0, 50) + "..." : content);
                                            callback.onChunk(content);
                                            
                                            // 控制返回间隔时间
                                            Long delay = streamDelay != null ? streamDelay : 
                                                    (sparkAiConfig.getStreamDelay() != null ? sparkAiConfig.getStreamDelay() : 0L);
                                            if (delay > 0) {
                                                try {
                                                    logger.debug("延迟 {} 毫秒", delay);
                                                    Thread.sleep(delay);
                                                } catch (InterruptedException e) {
                                                    Thread.currentThread().interrupt();
                                                    logger.warn("延迟被中断", e);
                                                }
                                            }
                                        } catch (Exception e) {
                                            // 回调失败不影响后续数据处理
                                            logger.error("回调处理失败，但继续处理后续数据", e);
                                        }
                                    }
                                }
                            }
                            
                            // 判断是否结束
                            if (response.getHeader().getStatus() == 2) {
                                // 最后一个结果
                                finalResponse.setHeader(response.getHeader());
                                finalResponse.setPayload(response.getPayload());
                                
                                // 设置完整内容
                                if (finalResponse.getPayload().getChoices() != null
                                        && finalResponse.getPayload().getChoices().getText() != null
                                        && !finalResponse.getPayload().getChoices().getText().isEmpty()) {
                                    // 更新第一个text元素的内容
                                    SparkWebSocketResponse.Text text = finalResponse.getPayload().getChoices().getText().get(0);
                                    if (text != null) {
                                        text.setContent(fullContent.toString());
                                    }
                                }
                                
                                logger.info("WebSocket响应完成");
                                future.complete(finalResponse);
                                close();
                            }
                        }
                    } catch (Exception e) {
                        logger.error("解析WebSocket消息失败", e);
                        future.completeExceptionally(e);
                    }
                }
                
                @Override
                public void onClose(int code, String reason, boolean remote) {
                    logger.info("WebSocket连接已关闭: code={}, reason={}", code, reason);
                    if (!future.isDone()) {
                        future.completeExceptionally(new RuntimeException("WebSocket连接关闭: " + reason));
                    }
                }
                
                @Override
                public void onError(Exception ex) {
                    logger.error("WebSocket错误", ex);
                    if (!future.isDone()) {
                        future.completeExceptionally(ex);
                    }
                }
            };
            
            // 连接WebSocket
            client.connect();
            
            // 等待响应（使用配置的超时时间）
            Long timeout = (finalTimeoutSeconds != null ? finalTimeoutSeconds : 
                    (sparkAiConfig.getWsResponseTimeout() != null ? sparkAiConfig.getWsResponseTimeout() : 300L)); // 默认5分钟
            logger.debug("WebSocket等待响应，超时时间: {}秒", timeout);
            SparkWebSocketResponse response = future.get(timeout, TimeUnit.SECONDS);
            return response;
            
        } catch (java.util.concurrent.TimeoutException e) {
            Long timeoutValue = finalTimeoutSeconds != null ? finalTimeoutSeconds : 
                    (sparkAiConfig.getWsResponseTimeout() != null ? sparkAiConfig.getWsResponseTimeout() : 300L);
            logger.error("WebSocket聊天超时，超时时间: {}秒", timeoutValue);
            throw new RuntimeException("WebSocket聊天超时: " + timeoutValue + "秒内未收到完整响应", e);
        } catch (java.util.concurrent.ExecutionException e) {
            Throwable cause = e.getCause();
            if (cause instanceof RuntimeException) {
                throw (RuntimeException) cause;
            }
            logger.error("WebSocket聊天失败", e);
            String errorMsg = cause != null ? cause.getMessage() : e.getMessage();
            throw new RuntimeException("WebSocket聊天失败: " + (errorMsg != null ? errorMsg : "未知错误"), cause != null ? cause : e);
        } catch (Exception e) {
            logger.error("WebSocket聊天失败", e);
            throw new RuntimeException("WebSocket聊天失败: " + (e.getMessage() != null ? e.getMessage() : "未知错误"), e);
        }
    }
    
    /**
     * 生成WebSocket URL（包含鉴权参数）
     */
    private String generateWebSocketUrl() throws Exception {
        String host = "spark-api.xf-yun.com";
        String path = "/v1/x1";
        
        SimpleDateFormat sdf = new SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss z", Locale.US);
        sdf.setTimeZone(TimeZone.getTimeZone("GMT"));
        String date = sdf.format(new Date());
        
        // 构建签名字符串
        String signatureOrigin = String.format("host: %s\ndate: %s\nGET %s HTTP/1.1", host, date, path);
        
        // 使用HMAC-SHA256签名
        Mac mac = Mac.getInstance("HmacSHA256");
        SecretKeySpec secretKeySpec = new SecretKeySpec(
                sparkAiConfig.getApiSecret().getBytes(StandardCharsets.UTF_8), 
                "HmacSHA256");
        mac.init(secretKeySpec);
        byte[] signatureBytes = mac.doFinal(signatureOrigin.getBytes(StandardCharsets.UTF_8));
        String signature = Base64.getEncoder().encodeToString(signatureBytes);
        
        // 构建authorization字符串
        String authorizationOrigin = String.format(
                "api_key=\"%s\", algorithm=\"hmac-sha256\", headers=\"host date request-line\", signature=\"%s\"",
                sparkAiConfig.getApiKey(), signature);
        String authorization = Base64.getEncoder().encodeToString(
                authorizationOrigin.getBytes(StandardCharsets.UTF_8));
        
        // 构建完整URL
        String wsUrl = String.format("wss://%s%s?authorization=%s&date=%s&host=%s",
                host, path,
                URLEncoder.encode(authorization, StandardCharsets.UTF_8.name()),
                URLEncoder.encode(date, StandardCharsets.UTF_8.name()),
                URLEncoder.encode(host, StandardCharsets.UTF_8.name()));
        
        return wsUrl;
    }
    
    /**
     * 构建请求对象
     */
    private SparkWebSocketRequest buildRequest(List<SparkWebSocketRequest.Text> messages,
                                               Double temperature,
                                               Integer maxTokens,
                                               String thinking) {
        SparkWebSocketRequest request = new SparkWebSocketRequest();
        
        // 设置header
        SparkWebSocketRequest.Header header = new SparkWebSocketRequest.Header();
        header.setAppId(sparkAiConfig.getAppId());
        header.setUid("user_" + System.currentTimeMillis());
        request.setHeader(header);
        
        // 设置parameter
        SparkWebSocketRequest.Parameter parameter = new SparkWebSocketRequest.Parameter();
        SparkWebSocketRequest.Chat chat = new SparkWebSocketRequest.Chat();
        chat.setDomain("spark-x");
        chat.setTemperature(temperature != null ? temperature : sparkAiConfig.getTemperature());
        chat.setMaxTokens(maxTokens != null ? maxTokens : sparkAiConfig.getMaxTokens());
        if (thinking != null) {
            chat.setThinking(thinking);
        }
        parameter.setChat(chat);
        request.setParameter(parameter);
        
        // 设置payload
        SparkWebSocketRequest.Payload payload = new SparkWebSocketRequest.Payload();
        SparkWebSocketRequest.Message message = new SparkWebSocketRequest.Message();
        message.setText(messages);
        payload.setMessage(message);
        request.setPayload(payload);
        
        return request;
    }
    
    /**
     * 流式回调接口
     */
    @FunctionalInterface
    public interface StreamCallback {
        /**
         * 接收到数据块时的回调
         * 
         * @param chunk 数据块
         */
        void onChunk(String chunk);
    }
}
