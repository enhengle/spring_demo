package com.practise.demo.job;

import com.practise.demo.service.LogCleanupService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * 定时任务 - 清理数据
 * 
 * @author lingwang
 * @date 2021/3/15 19:59
 */
@Component
public class ClearDataJob {
    
    private static final Logger logger = LoggerFactory.getLogger(ClearDataJob.class);

    @Autowired
    private LogCleanupService logCleanupService;

    @Scheduled(cron = "0 0 0 * * *")  // 每天凌晨执行
    public void clearDataJob() {
        logger.info("定时任务开始执行: {}", new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()));
        try {
            logCleanupService.cleanupOldLogs();
            logger.info("定时任务执行结束: {}", new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()));
        } catch (Exception e) {
            logger.error("定时任务执行异常: {}", e.getMessage(), e);
        }
    }
}

