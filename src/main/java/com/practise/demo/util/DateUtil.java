package com.practise.demo.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * @author lingwang
 * @date 2021/3/15 20:14
 * 日期处理
 */
public class DateUtil {

    private static Logger logger = LoggerFactory.getLogger(DateUtil.class);


    /**
     * 获取当前时间的前30天
     *
     * @return
     */
    public final static String getDayMonthEnd() {
        try {
            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
            Date date = new Date();
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(date);
            calendar.add(Calendar.DATE, -30);
            String endDate = format.format(calendar.getTime());
            return endDate;
        } catch (Exception e) {
            logger.error("日期转换错误  " + e.getMessage());
        }
        return null;
    }
}
