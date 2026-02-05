package com.practise.demo.service;

import com.practise.demo.model.dto.SparkChatRequest;
import com.practise.demo.model.dto.SparkChatResponse;
import com.practise.demo.util.SparkAiUtil;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/**
 * 讯飞星火AI服务类测试用例
 * 
 * @author system
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("讯飞星火AI服务类测试")
class SparkAiServiceTest {
    
    @Mock
    private SparkAiUtil sparkAiUtil;
    
    @InjectMocks
    private SparkAiService sparkAiService;
    
    @Test
    @DisplayName("测试简单对话")
    void testSimpleChat() {
        // 准备测试数据
        String userMessage = "你好";
        String expectedResponse = "你好！我是讯飞星火AI助手。";
        
        // Mock行为
        when(sparkAiUtil.simpleChat(userMessage)).thenReturn(expectedResponse);
        
        // 执行测试
        String result = sparkAiService.simpleChat(userMessage);
        
        // 验证结果
        assertNotNull(result);
        assertEquals(expectedResponse, result);
        verify(sparkAiUtil, times(1)).simpleChat(userMessage);
    }
    
    @Test
    @DisplayName("测试多轮对话")
    void testMultiTurnChat() {
        // 准备测试数据
        List<SparkChatRequest.Message> history = new ArrayList<>();
        SparkChatRequest.Message msg1 = new SparkChatRequest.Message();
        msg1.setRole("user");
        msg1.setContent("你好");
        history.add(msg1);
        
        String userMessage = "今天天气怎么样？";
        String expectedResponse = "抱歉，我无法获取实时天气信息。";
        
        // Mock行为
        when(sparkAiUtil.multiTurnChat(anyList(), eq(userMessage))).thenReturn(expectedResponse);
        
        // 执行测试
        String result = sparkAiService.multiTurnChat(history, userMessage);
        
        // 验证结果
        assertNotNull(result);
        assertEquals(expectedResponse, result);
        verify(sparkAiUtil, times(1)).multiTurnChat(anyList(), eq(userMessage));
    }
    
    @Test
    @DisplayName("测试完整对话")
    void testChat() {
        // 准备测试数据
        List<SparkChatRequest.Message> messages = new ArrayList<>();
        SparkChatRequest.Message message = new SparkChatRequest.Message();
        message.setRole("user");
        message.setContent("测试消息");
        messages.add(message);
        
        SparkChatResponse mockResponse = createMockResponse("测试回复");
        
        // Mock行为
        when(sparkAiUtil.chat(anyList())).thenReturn(mockResponse);
        
        // 执行测试
        SparkChatResponse result = sparkAiService.chat(messages);
        
        // 验证结果
        assertNotNull(result);
        assertNotNull(result.getChoices());
        verify(sparkAiUtil, times(1)).chat(anyList());
    }
    
    @Test
    @DisplayName("测试带参数的对话")
    void testChatWithParams() {
        // 准备测试数据
        List<SparkChatRequest.Message> messages = new ArrayList<>();
        SparkChatRequest.Message message = new SparkChatRequest.Message();
        message.setRole("user");
        message.setContent("测试消息");
        messages.add(message);
        
        Double temperature = 0.7;
        Integer maxTokens = 1024;
        String model = "generalv2";
        
        SparkChatResponse mockResponse = createMockResponse("测试回复");
        
        // Mock行为
        when(sparkAiUtil.chat(anyList(), anyDouble(), anyInt(), anyString())).thenReturn(mockResponse);
        
        // 执行测试
        SparkChatResponse result = sparkAiService.chatWithParams(messages, temperature, maxTokens, model);
        
        // 验证结果
        assertNotNull(result);
        verify(sparkAiUtil, times(1)).chat(anyList(), eq(temperature), eq(maxTokens), eq(model));
    }
    
    @Test
    @DisplayName("测试创建对话历史")
    void testCreateConversationHistory() {
        // 准备测试数据
        List<String> userMessages = new ArrayList<>();
        userMessages.add("你好");
        userMessages.add("今天天气怎么样？");
        
        List<String> assistantMessages = new ArrayList<>();
        assistantMessages.add("你好！");
        assistantMessages.add("抱歉，我无法获取实时天气信息。");
        
        // 执行测试
        List<SparkChatRequest.Message> result = 
                sparkAiService.createConversationHistory(userMessages, assistantMessages);
        
        // 验证结果
        assertNotNull(result);
        assertEquals(4, result.size());
        assertEquals("user", result.get(0).getRole());
        assertEquals("assistant", result.get(1).getRole());
        assertEquals("user", result.get(2).getRole());
        assertEquals("assistant", result.get(3).getRole());
    }
    
    @Test
    @DisplayName("测试异常处理")
    void testExceptionHandling() {
        // 准备测试数据
        String userMessage = "测试消息";
        
        // Mock异常
        when(sparkAiUtil.simpleChat(userMessage)).thenThrow(new RuntimeException("API调用失败"));
        
        // 执行测试并验证异常
        assertThrows(RuntimeException.class, () -> {
            sparkAiService.simpleChat(userMessage);
        });
    }
    
    /**
     * 创建模拟响应对象
     */
    private SparkChatResponse createMockResponse(String content) {
        SparkChatResponse response = new SparkChatResponse();
        response.setId("test-id");
        response.setObject("chat.completion");
        response.setCreated(System.currentTimeMillis() / 1000);
        response.setModel("spark-x");
        
        SparkChatResponse.Choice choice = new SparkChatResponse.Choice();
        choice.setIndex(0);
        choice.setFinishReason("stop");
        
        SparkChatResponse.Message message = new SparkChatResponse.Message();
        message.setRole("assistant");
        message.setContent(content);
        choice.setMessage(message);
        
        List<SparkChatResponse.Choice> choices = new ArrayList<>();
        choices.add(choice);
        response.setChoices(choices);
        
        SparkChatResponse.Usage usage = new SparkChatResponse.Usage();
        usage.setPromptTokens(10);
        usage.setCompletionTokens(20);
        usage.setTotalTokens(30);
        response.setUsage(usage);
        
        return response;
    }
}
