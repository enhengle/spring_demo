package com.practise.demo.service;

import com.practise.demo.util.DateUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.io.File;

/**
 * 日志清理服务
 * 
 * @author lingwang
 * @date 2021/3/15 20:02
 */
@Service
public class LogCleanupService {

    private static final Logger logger = LoggerFactory.getLogger(LogCleanupService.class);

    /**
     * 清理30天前的日志文件
     */
    public void cleanupOldLogs() {
        File logDir = new File("logs");
        if (!logDir.exists() || !logDir.isDirectory()) {
            logger.warn("日志目录不存在: {}", logDir.getAbsolutePath());
            return;
        }
        
        File[] files = logDir.listFiles();
        if (files == null || files.length == 0) {
            logger.info("日志目录为空，无需清理");
            return;
        }
        
        String cutoffDate = DateUtil.getDayMonthEnd();
        if (cutoffDate == null) {
            logger.error("获取截止日期失败");
            return;
        }
        
        int deletedCount = 0;
        for (File file : files) {
            if (file.isFile()) {
                String[] fileNameParts = file.getName().split("_");
                if (fileNameParts.length > 0 && cutoffDate.equals(fileNameParts[0])) {
                    if (file.delete()) {
                        logger.info("删除日志文件: {}", file.getName());
                        deletedCount++;
                    } else {
                        logger.warn("删除日志文件失败: {}", file.getName());
                    }
                }
            }
        }
        
        logger.info("日志清理完成，共删除 {} 个文件", deletedCount);
    }
}

