package com.practise.demo.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextClosedEvent;
import org.springframework.stereotype.Component;

import javax.annotation.PreDestroy;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * 优雅停机监听器
 * Spring Boot 2.3+ 已内置graceful shutdown，此监听器主要用于关闭自定义线程池
 * 
 * @author system
 * @date 2024
 */
@Component
public class GracefulShutdownListener implements ApplicationListener<ContextClosedEvent> {
    
    private static final Logger logger = LoggerFactory.getLogger(GracefulShutdownListener.class);
    
    @Override
    public void onApplicationEvent(ContextClosedEvent event) {
        logger.info("接收到应用关闭事件，开始执行优雅停机流程...");
        
        // Spring Boot 2.3+ 的graceful shutdown会自动处理Tomcat线程池
        // 这里主要用于关闭自定义的线程池
        
        shutdownThreadPools();
        
        logger.info("优雅停机流程执行完成");
    }
    
    /**
     * 关闭所有自定义线程池
     * 注意：如果项目中有自定义的线程池（如定时任务线程池、异步任务线程池等），
     * 需要在这里添加关闭逻辑
     */
    private void shutdownThreadPools() {
        logger.info("检查并关闭自定义线程池...");
        
        // 示例：关闭定时任务线程池（如果有）
        // ThreadPoolTaskScheduler scheduler = ...;
        // shutdownExecutor(scheduler.getScheduledThreadPoolExecutor(), "定时任务线程池");
        
        // 示例：关闭异步任务线程池（如果有）
        // ExecutorService asyncExecutor = ...;
        // shutdownExecutor(asyncExecutor, "异步任务线程池");
        
        logger.info("自定义线程池检查完成");
    }
    
    /**
     * 优雅关闭线程池
     */
    private void shutdownExecutor(ExecutorService executor, String name) {
        if (executor != null && !executor.isShutdown()) {
            logger.info("开始关闭线程池: {}", name);
            executor.shutdown();
            
            try {
                if (!executor.awaitTermination(30, TimeUnit.SECONDS)) {
                    logger.warn("线程池 {} 在30秒内未能正常关闭，强制关闭", name);
                    executor.shutdownNow();
                    
                    if (!executor.awaitTermination(5, TimeUnit.SECONDS)) {
                        logger.error("线程池 {} 未能正常关闭", name);
                    }
                } else {
                    logger.info("线程池 {} 已正常关闭", name);
                }
            } catch (InterruptedException e) {
                logger.error("等待线程池 {} 关闭时被中断", name, e);
                executor.shutdownNow();
                Thread.currentThread().interrupt();
            }
        }
    }
    
    /**
     * Bean销毁前的清理工作
     */
    @PreDestroy
    public void destroy() {
        logger.info("执行Bean销毁前的清理工作...");
        shutdownThreadPools();
    }
}

