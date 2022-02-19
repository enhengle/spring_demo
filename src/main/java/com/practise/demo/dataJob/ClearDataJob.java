package com.practise.demo.dataJob;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * @author lingwang
 * @date 2021/3/15 19:59
 * 定时任务
 */
@Component
public class ClearDataJob {
    private static Logger logger = LoggerFactory.getLogger(ClearDataJob.class);

    @Autowired
    private HomeManager homeManager;

    @Scheduled(cron = "*/5 0 0 * * *")
    public void clearDataJob() {
        logger.info("定时任务开始执行" + new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()));
        clearData(homeManager);
        logger.info("定时任务执行结束" + new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()));
    }

    private static void clearData(HomeManager homeManager) {
        try {
            homeManager.deleteHomeNewsData();
        } catch (Exception e) {
            logger.error("日志删除异常" + e.getMessage());
        }
    }

}
