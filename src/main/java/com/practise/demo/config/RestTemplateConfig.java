package com.practise.demo.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.ClientHttpRequestFactory;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;

/**
 * RestTemplate配置类
 * 
 * @author system
 */
@Configuration
public class RestTemplateConfig {
    
    @Autowired
    private SparkAiConfig sparkAiConfig;
    
    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate(clientHttpRequestFactory());
    }
    
    @Bean
    public ClientHttpRequestFactory clientHttpRequestFactory() {
        SimpleClientHttpRequestFactory factory = new SimpleClientHttpRequestFactory();
        // 使用配置的超时时间
        Integer connectTimeout = sparkAiConfig.getConnectTimeout() != null ? 
                sparkAiConfig.getConnectTimeout() : 30000;
        Integer readTimeout = sparkAiConfig.getReadTimeout() != null ? 
                sparkAiConfig.getReadTimeout() : 60000;
        factory.setConnectTimeout(connectTimeout);
        factory.setReadTimeout(readTimeout);
        return factory;
    }
}
