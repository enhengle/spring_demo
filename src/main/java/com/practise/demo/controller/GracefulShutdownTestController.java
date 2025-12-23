package com.practise.demo.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * 优雅停机测试控制器
 * 用于测试优雅停机功能
 * 
 * @author system
 * @date 2024
 */
@RestController
@RequestMapping("/test/shutdown")
public class GracefulShutdownTestController {
    
    private static final Logger logger = LoggerFactory.getLogger(GracefulShutdownTestController.class);
    
    private final ExecutorService executorService = Executors.newFixedThreadPool(5);
    
    /**
     * 测试长时间运行的接口
     * 用于测试优雅停机时是否能等待请求完成
     * 
     * @param seconds 请求处理时间（秒），默认10秒
     * @return 响应结果
     */
    @GetMapping("/long-running")
    public String longRunningTask(@RequestParam(defaultValue = "10") int seconds) {
        logger.info("开始执行长时间任务，预计耗时: {} 秒", seconds);
        
        try {
            // 模拟长时间处理
            Thread.sleep(seconds * 1000L);
            logger.info("长时间任务执行完成");
            return "任务执行成功，耗时: " + seconds + " 秒";
        } catch (InterruptedException e) {
            logger.error("任务被中断", e);
            Thread.currentThread().interrupt();
            return "任务被中断";
        }
    }
    
    /**
     * 测试异步任务
     * 用于测试优雅停机时是否能等待异步任务完成
     * 
     * @param seconds 任务处理时间（秒），默认5秒
     * @return 响应结果
     */
    @GetMapping("/async-task")
    public CompletableFuture<String> asyncTask(@RequestParam(defaultValue = "5") int seconds) {
        logger.info("开始执行异步任务，预计耗时: {} 秒", seconds);
        
        CompletableFuture<String> future = CompletableFuture.supplyAsync(() -> {
            try {
                Thread.sleep(seconds * 1000L);
                logger.info("异步任务执行完成");
                return "异步任务执行成功，耗时: " + seconds + " 秒";
            } catch (InterruptedException e) {
                logger.error("异步任务被中断", e);
                Thread.currentThread().interrupt();
                return "异步任务被中断";
            }
        }, executorService);
        
        return future;
    }
    
    /**
     * 测试快速响应接口
     * 用于对比测试
     * 
     * @return 响应结果
     */
    @GetMapping("/quick")
    public String quickResponse() {
        logger.info("快速响应接口");
        return "快速响应成功";
    }
    
    /**
     * 获取线程池状态
     * 
     * @return 线程池状态信息
     */
    @GetMapping("/thread-pool-status")
    public String getThreadPoolStatus() {
        if (executorService instanceof java.util.concurrent.ThreadPoolExecutor) {
            java.util.concurrent.ThreadPoolExecutor tpe = 
                (java.util.concurrent.ThreadPoolExecutor) executorService;
            return String.format(
                "线程池状态 - 核心线程数: %d, 最大线程数: %d, 当前线程数: %d, 活跃线程数: %d, 队列大小: %d, 已完成任务数: %d",
                tpe.getCorePoolSize(),
                tpe.getMaximumPoolSize(),
                tpe.getPoolSize(),
                tpe.getActiveCount(),
                tpe.getQueue().size(),
                tpe.getCompletedTaskCount()
            );
        }
        return "无法获取线程池状态";
    }
}

