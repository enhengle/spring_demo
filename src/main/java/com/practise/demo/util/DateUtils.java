package com.practise.demo.util;



import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;

/**
 * 日期工具类
 */
public class DateUtils {

    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    private static final DateTimeFormatter DATETIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    /**
     * 获取N天前的00:00:00
     * @param days 天数（正数表示N天前，负数表示N天后）
     * @return 日期时间字符串，格式：yyyy-MM-dd 00:00:00
     */
    public static String getNDaysAgoStart(int days) {
        LocalDate targetDate = LocalDate.now().minusDays(days);
        LocalDateTime startTime = targetDate.atTime(LocalTime.MIN); // 00:00:00
        return startTime.format(DATETIME_FORMATTER);
    }

    /**
     * 获取N天前的00:00:00
     * @param days 天数（正数表示N天前，负数表示N天后）
     * @return Date对象
     */
    public static Date getNDaysAgoStartDate(int days) {
        LocalDate targetDate = LocalDate.now().minusDays(days);
        LocalDateTime startTime = targetDate.atTime(LocalTime.MIN); // 00:00:00
        return java.sql.Timestamp.valueOf(startTime);
    }

    /**
     * 获取当天的23:59:59
     * @return 日期时间字符串，格式：yyyy-MM-dd 23:59:59
     */
    public static String getTodayEnd() {
        LocalDate today = LocalDate.now();
        LocalDateTime endTime = today.atTime(LocalTime.MAX); // 23:59:59.999999999
        return endTime.format(DATETIME_FORMATTER);
    }

    /**
     * 获取当天的23:59:59
     * @return Date对象
     */
    public static Date getTodayEndDate() {
        LocalDate today = LocalDate.now();
        LocalDateTime endTime = today.atTime(LocalTime.MAX); // 23:59:59.999999999
        return java.sql.Timestamp.valueOf(endTime);
    }

    /**
     * 获取指定日期的00:00:00
     * @param date 指定日期
     * @return 日期时间字符串，格式：yyyy-MM-dd 00:00:00
     */
    public static String getDateStart(LocalDate date) {
        LocalDateTime startTime = date.atTime(LocalTime.MIN);
        return startTime.format(DATETIME_FORMATTER);
    }

    /**
     * 获取指定日期的23:59:59
     * @param date 指定日期
     * @return 日期时间字符串，格式：yyyy-MM-dd 23:59:59
     */
    public static String getDateEnd(LocalDate date) {
        LocalDateTime endTime = date.atTime(LocalTime.MAX);
        return endTime.format(DATETIME_FORMATTER);
    }

    /**
     * 获取N天前的00:00:00和当天的23:59:59
     * @param days 天数（正数表示N天前，负数表示N天后）
     * @return 包含开始和结束时间的数组，[0]为开始时间，[1]为结束时间
     */
    public static String[] getNDaysRange(int days) {
        String startTime = getNDaysAgoStart(days);
        String endTime = getTodayEnd();
        return new String[]{startTime, endTime};
    }

    /**
     * 获取N天前的00:00:00和当天的23:59:59
     * @param days 天数（正数表示N天前，负数表示N天后）
     * @return 包含开始和结束时间的数组，[0]为开始时间，[1]为结束时间
     */
    public static Date[] getNDaysRangeDate(int days) {
        Date startTime = getNDaysAgoStartDate(days);
        Date endTime = getTodayEndDate();
        return new Date[]{startTime, endTime};
    }

    /**
     * 获取当前日期字符串
     * @return 格式：yyyy-MM-dd
     */
    public static String getCurrentDate() {
        return LocalDate.now().format(DATE_FORMATTER);
    }

    /**
     * 获取当前日期时间字符串
     * @return 格式：yyyy-MM-dd HH:mm:ss
     */
    public static String getCurrentDateTime() {
        return LocalDateTime.now().format(DATETIME_FORMATTER);
    }
}