package com.practise.demo.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.web.embedded.tomcat.TomcatServletWebServerFactory;
import org.springframework.boot.web.servlet.server.ServletWebServerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * 优雅停机配置
 * Spring Boot 2.3+ 已内置graceful shutdown支持，通过server.shutdown=graceful配置即可
 * 此配置类主要用于自定义线程池的优雅关闭
 * 
 * @author system
 * @date 2024
 */
@Configuration
public class GracefulShutdownConfig {
    
    private static final Logger logger = LoggerFactory.getLogger(GracefulShutdownConfig.class);
    
    @Bean
    public ServletWebServerFactory servletContainer() {
        TomcatServletWebServerFactory factory = new TomcatServletWebServerFactory();
        factory.addConnectorCustomizers(connector -> {
            connector.setProperty("connectionTimeout", "20000");
            connector.setProperty("keepAliveTimeout", "30000");
        });
        return factory;
    }
}

