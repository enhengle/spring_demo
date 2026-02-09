package com.practise.demo.config;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.openai.OpenAiChatModel;
import org.springframework.ai.openai.OpenAiChatOptions;
import org.springframework.ai.openai.api.OpenAiApi;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

/**
 * Spring AI配置类
 * 配置讯飞星火大模型（使用OpenAI兼容接口）
 * 
 * @author system
 */
@Configuration
public class SpringAiConfig {
    
    @Autowired
    private SparkAiConfig sparkAiConfig;
    
    /**
     * 配置讯飞星火ChatModel（使用OpenAI兼容接口）
     * 讯飞星火的HTTP API是OpenAI兼容的，可以使用OpenAiChatModel
     * 
     * @return ChatModel实例，用于与讯飞星火AI进行对话
     */
    @Bean(name = "xunfeiSparkChatModel")
    @Primary
    public ChatModel xunfeiSparkChatModel() {
        // 讯飞星火使用OpenAI兼容的API，但需要自定义API地址
        OpenAiApi openAiApi = new OpenAiApi(
                sparkAiConfig.getApiUrl(),  // 使用讯飞星火的API地址
                sparkAiConfig.getApiPassword()  // 使用API Password作为Token
        );
        
        OpenAiChatOptions options = OpenAiChatOptions.builder()
                .withModel(sparkAiConfig.getModel())
                .withTemperature(sparkAiConfig.getTemperature() != null ? sparkAiConfig.getTemperature() : 0.5)
                .withMaxTokens(sparkAiConfig.getMaxTokens() != null ? sparkAiConfig.getMaxTokens() : 2048)
                .build();
        
        ChatModel chatModel = new OpenAiChatModel(openAiApi, options);
        return chatModel;
    }
    
    /**
     * 配置ChatClient
     * Spring会自动注入上面创建的ChatModel Bean
     * 
     * @param chatModel ChatModel实例，由Spring自动注入
     * @return ChatClient实例，用于调用AI对话
     */
    @Bean
    public ChatClient chatClient(ChatModel chatModel) {
        return ChatClient.builder(chatModel).build();
    }
}
