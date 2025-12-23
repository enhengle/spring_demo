package com.practise.demo.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * 日期处理工具类
 * 
 * @author lingwang
 * @date 2021/3/15 20:14
 */
public class DateUtil {

    private static final Logger logger = LoggerFactory.getLogger(DateUtil.class);

    /**
     * 获取当前时间的前30天
     *
     * @return 日期字符串，格式：yyyy-MM-dd
     */
    public static String getDayMonthEnd() {
        try {
            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
            Date date = new Date();
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(date);
            calendar.add(Calendar.DATE, -30);
            return format.format(calendar.getTime());
        } catch (Exception e) {
            logger.error("日期转换错误: {}", e.getMessage(), e);
            return null;
        }
    }
}

