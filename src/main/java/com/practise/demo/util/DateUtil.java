package com.practise.demo.util;

import lombok.extern.slf4j.Slf4j;

import java.text.SimpleDateFormat;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

/**
 * 日期常用工具类
 */
@Slf4j
class DateUtil {

    static final String YYYY_MM_DD_HH_MM_SS_DATE_FORMAT = "yyyy-MM-dd HH:mm:ss";
    static final String YYYY_MM_DD_DATE_FORMAT = "yyyy-MM-dd";
    static final String YYYY_MM_DATE_FORMAT = "yyyy-MM";
    static final String MM_DD_DATE_FORMAT = "MM-dd";
    static final String YYYY_MM_DD_HH_MM_SS_SSS_DATE_FORMAT = "yyyy-MM-dd HH:mm:ss.SSS";

    // ==================== 基础转换方法 ====================

    /**
     * 字符串转换成日期
     */
    public static Date strTurnDate(String str, String format) {
        try {
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat(format);
            Date date = simpleDateFormat.parse(str);
            return date;
        } catch (Exception e) {
            log.info("日志转换异常, 异常原因:{}", e.getMessage(), e);
            return null;
        }
    }

    /**
     * 日期转换成字符串
     */
    public static String dateTurnDateStr(Date date, String format) {
        if (date == null) return null;
        try {
            SimpleDateFormat dateFormat = new SimpleDateFormat(format);
            return dateFormat.format(date);
        } catch (Exception e) {
            log.info("日期转字符串异常, 异常原因:{}", e.getMessage(), e);
            return null;
        }
    }

    /**
     * 时间之间的格式转换
     */
    public static Date dateTurnDate(Date date, String format) {
        try {
            SimpleDateFormat dateFormat = new SimpleDateFormat(format);
            String dateStr = dateFormat.format(date);
            return dateFormat.parse(dateStr);
        } catch (Exception e) {
            log.info("日志转换异常, 异常原因:{}", e.getMessage(), e);
            return null;
        }
    }

    /**
     * 获取当前时间
     */
    public static Date getCurrentDate() {
        return new Date();
    }

    // ==================== 时间比较方法 ====================

    /**
     * 判断时间是否为N天前
     */
    public static boolean isNDaysAgo(Date date, int days) {
        if (date == null) return false;
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.DAY_OF_MONTH, -days);
        Date nDaysAgo = cal.getTime();
        return date.before(nDaysAgo);
    }

    /**
     * 获取时间差值（毫秒）
     */
    public static long getTimeDifference(Date date1, Date date2) {
        if (date1 == null || date2 == null) return 0;
        return Math.abs(date1.getTime() - date2.getTime());
    }

    /**
     * 判断时间是否为N天后
     */
    public static boolean isNDaysLater(Date date, int days) {
        if (date == null) return false;
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.DAY_OF_MONTH, days);
        Date nDaysLater = cal.getTime();
        return date.after(nDaysLater);
    }

    /**
     * 获取时间1是否在时间2之前
     */
    public static boolean isBefore(Date date1, Date date2) {
        if (date1 == null || date2 == null) return false;
        return date1.before(date2);
    }

    /**
     * 获取时间1是否在时间2之后
     */
    public static boolean isAfter(Date date1, Date date2) {
        if (date1 == null || date2 == null) return false;
        return date1.after(date2);
    }

    /**
     * 判断两个时间是否相等
     */
    public static boolean isEqual(Date date1, Date date2) {
        if (date1 == null || date2 == null) return false;
        return date1.equals(date2);
    }

    // ==================== 时间计算方法 ====================

    /**
     * 获取时间的N天后
     */
    public static Date addDays(Date date, int days) {
        if (date == null) return null;
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        cal.add(Calendar.DAY_OF_MONTH, days);
        return cal.getTime();
    }

    /**
     * 获取时间的N天前
     */
    public static Date minusDays(Date date, int days) {
        return addDays(date, -days);
    }

    /**
     * 获取时间的N小时后
     */
    public static Date addHours(Date date, int hours) {
        if (date == null) return null;
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        cal.add(Calendar.HOUR_OF_DAY, hours);
        return cal.getTime();
    }

    /**
     * 获取时间的N小时前
     */
    public static Date minusHours(Date date, int hours) {
        return addHours(date, -hours);
    }

    /**
     * 获取时间的N月后
     */
    public static Date addMonths(Date date, int months) {
        if (date == null) return null;
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        cal.add(Calendar.MONTH, months);
        return cal.getTime();
    }

    /**
     * 获取时间的N月前
     */
    public static Date minusMonths(Date date, int months) {
        return addMonths(date, -months);
    }

    /**
     * 获取时间的N年后
     */
    public static Date addYears(Date date, int years) {
        if (date == null) return null;
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        cal.add(Calendar.YEAR, years);
        return cal.getTime();
    }

    /**
     * 获取时间的N年前
     */
    public static Date minusYears(Date date, int years) {
        return addYears(date, -years);
    }

    /**
     * 获取时间的N秒后
     */
    public static Date addSeconds(Date date, int seconds) {
        if (date == null) return null;
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        cal.add(Calendar.SECOND, seconds);
        return cal.getTime();
    }

    /**
     * 获取时间的N秒前
     */
    public static Date minusSeconds(Date date, int seconds) {
        return addSeconds(date, -seconds);
    }

    /**
     * 获取时间的N分钟后
     */
    public static Date addMinutes(Date date, int minutes) {
        if (date == null) return null;
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        cal.add(Calendar.MINUTE, minutes);
        return cal.getTime();
    }

    /**
     * 获取时间的N分钟前
     */
    public static Date minusMinutes(Date date, int minutes) {
        return addMinutes(date, -minutes);
    }

    /**
     * 获取时间的N周后
     */
    public static Date addWeeks(Date date, int weeks) {
        if (date == null) return null;
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        cal.add(Calendar.WEEK_OF_YEAR, weeks);
        return cal.getTime();
    }

    /**
     * 获取时间的N周前
     */
    public static Date minusWeeks(Date date, int weeks) {
        return addWeeks(date, -weeks);
    }

    // ==================== java.sql.Date 相关方法 ====================

    /**
     * java.util.Date 转 java.sql.Date
     */
    public static java.sql.Date utilDateToSqlDate(Date utilDate) {
        if (utilDate == null) return null;
        return new java.sql.Date(utilDate.getTime());
    }

    /**
     * java.sql.Date 转 java.util.Date
     */
    public static Date sqlDateToUtilDate(java.sql.Date sqlDate) {
        if (sqlDate == null) return null;
        return new Date(sqlDate.getTime());
    }

    /**
     * 字符串转 java.sql.Date
     */
    public static java.sql.Date strToSqlDate(String str, String format) {
        try {
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat(format);
            Date utilDate = simpleDateFormat.parse(str);
            return utilDateToSqlDate(utilDate);
        } catch (Exception e) {
            log.info("字符串转SQL日期异常, 异常原因:{}", e.getMessage(), e);
            return null;
        }
    }

    // ==================== java.time.LocalDateTime 相关方法 ====================

    /**
     * java.util.Date 转 LocalDateTime
     */
    public static LocalDateTime utilDateToLocalDateTime(Date utilDate) {
        if (utilDate == null) return null;
        return utilDate.toInstant()
                .atZone(ZoneId.systemDefault())
                .toLocalDateTime();
    }

    /**
     * LocalDateTime 转 java.util.Date
     */
    public static Date localDateTimeToUtilDate(LocalDateTime localDateTime) {
        if (localDateTime == null) return null;
        return Date.from(localDateTime.atZone(ZoneId.systemDefault()).toInstant());
    }

    /**
     * 字符串转 LocalDateTime
     */
    public static LocalDateTime strToLocalDateTime(String str, String format) {
        try {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern(format);
            return LocalDateTime.parse(str, formatter);
        } catch (Exception e) {
            log.info("字符串转LocalDateTime异常, 异常原因:{}", e.getMessage(), e);
            return null;
        }
    }

    /**
     * LocalDateTime 转字符串
     */
    public static String localDateTimeToStr(LocalDateTime localDateTime, String format) {
        if (localDateTime == null) return null;
        try {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern(format);
            return localDateTime.format(formatter);
        } catch (Exception e) {
            log.info("LocalDateTime转字符串异常, 异常原因:{}", e.getMessage(), e);
            return null;
        }
    }

    // ==================== LocalDateTime 时间计算方法 ====================

    /**
     * 获取LocalDateTime的N天前
     */
    public static LocalDateTime getLocalDateTimeNDaysAgo(LocalDateTime localDateTime, int days) {
        if (localDateTime == null) return null;
        return localDateTime.minusDays(days);
    }

    /**
     * 获取LocalDateTime的N天后
     */
    public static LocalDateTime getLocalDateTimeNDaysLater(LocalDateTime localDateTime, int days) {
        if (localDateTime == null) return null;
        return localDateTime.plusDays(days);
    }

    /**
     * 获取LocalDateTime的N年前
     */
    public static LocalDateTime getLocalDateTimeNYearsAgo(LocalDateTime localDateTime, int years) {
        if (localDateTime == null) return null;
        return localDateTime.minusYears(years);
    }

    /**
     * 获取LocalDateTime的N年后
     */
    public static LocalDateTime getLocalDateTimeNYearsLater(LocalDateTime localDateTime, int years) {
        if (localDateTime == null) return null;
        return localDateTime.plusYears(years);
    }

    /**
     * 获取LocalDateTime的N月前
     */
    public static LocalDateTime getLocalDateTimeNMonthsAgo(LocalDateTime localDateTime, int months) {
        if (localDateTime == null) return null;
        return localDateTime.minusMonths(months);
    }

    /**
     * 获取LocalDateTime的N月后
     */
    public static LocalDateTime getLocalDateTimeNMonthsLater(LocalDateTime localDateTime, int months) {
        if (localDateTime == null) return null;
        return localDateTime.plusMonths(months);
    }

    /**
     * 获取LocalDateTime的N周前
     */
    public static LocalDateTime getLocalDateTimeNWeeksAgo(LocalDateTime localDateTime, int weeks) {
        if (localDateTime == null) return null;
        return localDateTime.minusWeeks(weeks);
    }

    /**
     * 获取LocalDateTime的N周后
     */
    public static LocalDateTime getLocalDateTimeNWeeksLater(LocalDateTime localDateTime, int weeks) {
        if (localDateTime == null) return null;
        return localDateTime.plusWeeks(weeks);
    }

    /**
     * 获取LocalDateTime的N小时前
     */
    public static LocalDateTime getLocalDateTimeNHoursAgo(LocalDateTime localDateTime, int hours) {
        if (localDateTime == null) return null;
        return localDateTime.minusHours(hours);
    }

    /**
     * 获取LocalDateTime的N小时后
     */
    public static LocalDateTime getLocalDateTimeNHoursLater(LocalDateTime localDateTime, int hours) {
        if (localDateTime == null) return null;
        return localDateTime.plusHours(hours);
    }

    /**
     * 获取LocalDateTime的N分钟前
     */
    public static LocalDateTime getLocalDateTimeNMinutesAgo(LocalDateTime localDateTime, int minutes) {
        if (localDateTime == null) return null;
        return localDateTime.minusMinutes(minutes);
    }

    /**
     * 获取LocalDateTime的N分钟后
     */
    public static LocalDateTime getLocalDateTimeNMinutesLater(LocalDateTime localDateTime, int minutes) {
        if (localDateTime == null) return null;
        return localDateTime.plusMinutes(minutes);
    }

    /**
     * 获取LocalDateTime的N秒前
     */
    public static LocalDateTime getLocalDateTimeNSecondsAgo(LocalDateTime localDateTime, int seconds) {
        if (localDateTime == null) return null;
        return localDateTime.minusSeconds(seconds);
    }

    /**
     * 获取LocalDateTime的N秒后
     */
    public static LocalDateTime getLocalDateTimeNSecondsLater(LocalDateTime localDateTime, int seconds) {
        if (localDateTime == null) return null;
        return localDateTime.plusSeconds(seconds);
    }

    /**
     * 获取LocalDateTime的N纳秒前
     */
    public static LocalDateTime getLocalDateTimeNNanosAgo(LocalDateTime localDateTime, long nanos) {
        if (localDateTime == null) return null;
        return localDateTime.minusNanos(nanos);
    }

    /**
     * 获取LocalDateTime的N纳秒后
     */
    public static LocalDateTime getLocalDateTimeNNanosLater(LocalDateTime localDateTime, long nanos) {
        if (localDateTime == null) return null;
        return localDateTime.plusNanos(nanos);
    }

    // ==================== LocalDateTime 时间比较方法 ====================

    /**
     * 判断LocalDateTime1是否在LocalDateTime2之前
     */
    public static boolean isLocalDateTimeBefore(LocalDateTime localDateTime1, LocalDateTime localDateTime2) {
        if (localDateTime1 == null || localDateTime2 == null) return false;
        return localDateTime1.isBefore(localDateTime2);
    }

    /**
     * 判断LocalDateTime1是否在LocalDateTime2之后
     */
    public static boolean isLocalDateTimeAfter(LocalDateTime localDateTime1, LocalDateTime localDateTime2) {
        if (localDateTime1 == null || localDateTime2 == null) return false;
        return localDateTime1.isAfter(localDateTime2);
    }

    /**
     * 判断两个LocalDateTime是否相等
     */
    public static boolean isLocalDateTimeEqual(LocalDateTime localDateTime1, LocalDateTime localDateTime2) {
        if (localDateTime1 == null || localDateTime2 == null) return false;
        return localDateTime1.equals(localDateTime2);
    }

    /**
     * 判断LocalDateTime是否为N天前
     */
    public static boolean isLocalDateTimeNDaysAgo(LocalDateTime localDateTime, int days) {
        if (localDateTime == null) return false;
        LocalDateTime nDaysAgo = LocalDateTime.now().minusDays(days);
        return localDateTime.isBefore(nDaysAgo);
    }

    /**
     * 判断LocalDateTime是否为N天后
     */
    public static boolean isLocalDateTimeNDaysLater(LocalDateTime localDateTime, int days) {
        if (localDateTime == null) return false;
        LocalDateTime nDaysLater = LocalDateTime.now().plusDays(days);
        return localDateTime.isAfter(nDaysLater);
    }

    // ==================== LocalDateTime 字符串格式方法 ====================

    /**
     * LocalDateTime转字符串（默认格式：yyyy-MM-dd HH:mm:ss）
     */
    public static String localDateTimeToString(LocalDateTime localDateTime) {
        return localDateTimeToStr(localDateTime, YYYY_MM_DD_HH_MM_SS_DATE_FORMAT);
    }

    /**
     * LocalDateTime转日期字符串（yyyy-MM-dd）
     */
    public static String localDateTimeToDateString(LocalDateTime localDateTime) {
        return localDateTimeToStr(localDateTime, YYYY_MM_DD_DATE_FORMAT);
    }

    /**
     * LocalDateTime转时间字符串（HH:mm:ss）
     */
    public static String localDateTimeToTimeString(LocalDateTime localDateTime) {
        if (localDateTime == null) return null;
        try {
            DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm:ss");
            return localDateTime.format(timeFormatter);
        } catch (Exception e) {
            log.info("LocalDateTime转时间字符串异常, 异常原因:{}", e.getMessage(), e);
            return null;
        }
    }

    /**
     * LocalDateTime转年月字符串（yyyy-MM）
     */
    public static String localDateTimeToYearMonthString(LocalDateTime localDateTime) {
        return localDateTimeToStr(localDateTime, YYYY_MM_DATE_FORMAT);
    }

    /**
     * LocalDateTime转月日字符串（MM-dd）
     */
    public static String localDateTimeToMonthDayString(LocalDateTime localDateTime) {
        return localDateTimeToStr(localDateTime, MM_DD_DATE_FORMAT);
    }

    /**
     * LocalDateTime转毫秒时间戳字符串
     */
    public static String localDateTimeToMillisString(LocalDateTime localDateTime) {
        if (localDateTime == null) return null;
        try {
            return String.valueOf(localDateTime.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli());
        } catch (Exception e) {
            log.info("LocalDateTime转毫秒时间戳异常, 异常原因:{}", e.getMessage(), e);
            return null;
        }
    }

    /**
     * LocalDateTime转秒时间戳字符串
     */
    public static String localDateTimeToSecondsString(LocalDateTime localDateTime) {
        if (localDateTime == null) return null;
        try {
            return String.valueOf(localDateTime.atZone(ZoneId.systemDefault()).toEpochSecond());
        } catch (Exception e) {
            log.info("LocalDateTime转秒时间戳异常, 异常原因:{}", e.getMessage(), e);
            return null;
        }
    }

    // ==================== LocalDateTime 实用工具方法 ====================

    /**
     * 获取LocalDateTime的毫秒时间戳
     */
    public static long getLocalDateTimeTimeInMillis(LocalDateTime localDateTime) {
        if (localDateTime == null) return 0;
        return localDateTime.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli();
    }

    /**
     * 获取LocalDateTime的秒时间戳
     */
    public static long getLocalDateTimeTimeInSeconds(LocalDateTime localDateTime) {
        if (localDateTime == null) return 0;
        return localDateTime.atZone(ZoneId.systemDefault()).toEpochSecond();
    }

    /**
     * 获取LocalDateTime的年份
     */
    public static int getLocalDateTimeYear(LocalDateTime localDateTime) {
        if (localDateTime == null) return 0;
        return localDateTime.getYear();
    }

    /**
     * 获取LocalDateTime的月份（1-12）
     */
    public static int getLocalDateTimeMonth(LocalDateTime localDateTime) {
        if (localDateTime == null) return 0;
        return localDateTime.getMonthValue();
    }

    /**
     * 获取LocalDateTime的日期
     */
    public static int getLocalDateTimeDay(LocalDateTime localDateTime) {
        if (localDateTime == null) return 0;
        return localDateTime.getDayOfMonth();
    }

    /**
     * 获取LocalDateTime的小时
     */
    public static int getLocalDateTimeHour(LocalDateTime localDateTime) {
        if (localDateTime == null) return 0;
        return localDateTime.getHour();
    }

    /**
     * 获取LocalDateTime的分钟
     */
    public static int getLocalDateTimeMinute(LocalDateTime localDateTime) {
        if (localDateTime == null) return 0;
        return localDateTime.getMinute();
    }

    /**
     * 获取LocalDateTime的秒
     */
    public static int getLocalDateTimeSecond(LocalDateTime localDateTime) {
        if (localDateTime == null) return 0;
        return localDateTime.getSecond();
    }

    /**
     * 获取LocalDateTime的纳秒
     */
    public static int getLocalDateTimeNano(LocalDateTime localDateTime) {
        if (localDateTime == null) return 0;
        return localDateTime.getNano();
    }

    /**
     * 获取LocalDateTime的星期几（1-7，1代表星期一）
     */
    public static int getLocalDateTimeDayOfWeek(LocalDateTime localDateTime) {
        if (localDateTime == null) return 0;
        return localDateTime.getDayOfWeek().getValue();
    }

    /**
     * 获取LocalDateTime的星期几中文描述
     */
    public static String getLocalDateTimeDayOfWeekString(LocalDateTime localDateTime) {
        if (localDateTime == null) return null;
        String[] weekDays = {"星期一", "星期二", "星期三", "星期四", "星期五", "星期六", "星期日"};
        int dayOfWeek = localDateTime.getDayOfWeek().getValue();
        return weekDays[dayOfWeek - 1];
    }

    /**
     * 获取LocalDateTime是否为周末
     */
    public static boolean isLocalDateTimeWeekend(LocalDateTime localDateTime) {
        if (localDateTime == null) return false;
        int dayOfWeek = localDateTime.getDayOfWeek().getValue();
        return dayOfWeek == 6 || dayOfWeek == 7; // 星期六或星期日
    }

    /**
     * 获取LocalDateTime是否为工作日
     */
    public static boolean isLocalDateTimeWorkday(LocalDateTime localDateTime) {
        return !isLocalDateTimeWeekend(localDateTime);
    }

    /**
     * 获取LocalDateTime的季度
     */
    public static int getLocalDateTimeQuarter(LocalDateTime localDateTime) {
        if (localDateTime == null) return 0;
        int month = localDateTime.getMonthValue();
        return (month - 1) / 3 + 1;
    }

    /**
     * 获取LocalDateTime的季度字符串
     */
    public static String getLocalDateTimeQuarterString(LocalDateTime localDateTime) {
        if (localDateTime == null) return null;
        int quarter = getLocalDateTimeQuarter(localDateTime);
        return "第" + quarter + "季度";
    }


    /**
     * 获取LocalDateTime的年份中的第几天
     */
    public static int getLocalDateTimeDayOfYear(LocalDateTime localDateTime) {
        if (localDateTime == null) return 0;
        return localDateTime.getDayOfYear();
    }

    /**
     * 获取LocalDateTime的月份中的第几周的第几天
     */
    public static int getLocalDateTimeDayOfWeekInMonth(LocalDateTime localDateTime) {
        if (localDateTime == null) return 0;
        int dayOfMonth = localDateTime.getDayOfMonth();
        int dayOfWeek = localDateTime.getDayOfWeek().getValue();
        return (dayOfMonth - 1) / 7 + 1;
    }

    /**
     * 获取LocalDateTime的时区
     */
    public static ZoneId getLocalDateTimeZoneId(LocalDateTime localDateTime) {
        if (localDateTime == null) return null;
        return ZoneId.systemDefault();
    }

    /**
     * 获取LocalDateTime的时区ID
     */
    public static String getLocalDateTimeZoneIdString(LocalDateTime localDateTime) {
        if (localDateTime == null) return null;
        return ZoneId.systemDefault().getId();
    }

    /**
     * 获取LocalDateTime的时区偏移量（秒）
     */
    public static int getLocalDateTimeZoneOffsetSeconds(LocalDateTime localDateTime) {
        if (localDateTime == null) return 0;
        return ZoneId.systemDefault().getRules().getOffset(localDateTime).getTotalSeconds();
    }

    /**
     * 获取LocalDateTime的时区偏移量字符串（如：+08:00）
     */
    public static String getLocalDateTimeZoneOffsetString(LocalDateTime localDateTime) {
        if (localDateTime == null) return null;
        ZoneOffset offset = ZoneId.systemDefault().getRules().getOffset(localDateTime);
        return offset.getId();
    }

    /**
     * 获取LocalDateTime的时区偏移量小时数
     */
    public static int getLocalDateTimeZoneOffsetHours(LocalDateTime localDateTime) {
        if (localDateTime == null) return 0;
        ZoneOffset offset = ZoneId.systemDefault().getRules().getOffset(localDateTime);
        return offset.getTotalSeconds() / 3600;
    }

    /**
     * 获取LocalDateTime的时区偏移量分钟数
     */
    public static int getLocalDateTimeZoneOffsetMinutes(LocalDateTime localDateTime) {
        if (localDateTime == null) return 0;
        ZoneOffset offset = ZoneId.systemDefault().getRules().getOffset(localDateTime);
        return (offset.getTotalSeconds() % 3600) / 60;
    }

    /**
     * 获取LocalDateTime的时区偏移量秒数
     */
    public static int getLocalDateTimeZoneOffsetSecondsOnly(LocalDateTime localDateTime) {
        if (localDateTime == null) return 0;
        ZoneOffset offset = ZoneId.systemDefault().getRules().getOffset(localDateTime);
        return offset.getTotalSeconds() % 60;
    }

    // ==================== LocalDateTime 时间判断方法 ====================

    /**
     * 判断LocalDateTime是否为今天
     */
    public static boolean isLocalDateTimeToday(LocalDateTime localDateTime) {
        if (localDateTime == null) return false;
        LocalDate today = LocalDate.now();
        return localDateTime.toLocalDate().equals(today);
    }

    /**
     * 判断LocalDateTime是否为昨天
     */
    public static boolean isLocalDateTimeYesterday(LocalDateTime localDateTime) {
        if (localDateTime == null) return false;
        LocalDate yesterday = LocalDate.now().minusDays(1);
        return localDateTime.toLocalDate().equals(yesterday);
    }

    /**
     * 判断LocalDateTime是否为明天
     */
    public static boolean isLocalDateTimeTomorrow(LocalDateTime localDateTime) {
        if (localDateTime == null) return false;
        LocalDate tomorrow = LocalDate.now().plusDays(1);
        return localDateTime.toLocalDate().equals(tomorrow);
    }

    /**
     * 判断LocalDateTime是否为本周
     */
    public static boolean isLocalDateTimeThisWeek(LocalDateTime localDateTime) {
        if (localDateTime == null) return false;
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime startOfWeek = now.with(DayOfWeek.MONDAY).withHour(0).withMinute(0).withSecond(0).withNano(0);
        LocalDateTime endOfWeek = startOfWeek.plusDays(7);
        return localDateTime.isAfter(startOfWeek) && localDateTime.isBefore(endOfWeek);
    }

    /**
     * 判断LocalDateTime是否为本月
     */
    public static boolean isLocalDateTimeThisMonth(LocalDateTime localDateTime) {
        if (localDateTime == null) return false;
        LocalDateTime now = LocalDateTime.now();
        return localDateTime.getYear() == now.getYear() && localDateTime.getMonthValue() == now.getMonthValue();
    }

    /**
     * 判断LocalDateTime是否为本年
     */
    public static boolean isLocalDateTimeThisYear(LocalDateTime localDateTime) {
        if (localDateTime == null) return false;
        return localDateTime.getYear() == LocalDateTime.now().getYear();
    }

    /**
     * 获取两个LocalDateTime之间的天数差
     */
    public static long getLocalDateTimeDaysBetween(LocalDateTime localDateTime1, LocalDateTime localDateTime2) {
        if (localDateTime1 == null || localDateTime2 == null) return 0;
        return Math.abs(Duration.between(localDateTime1, localDateTime2).toDays());
    }

    /**
     * 获取两个LocalDateTime之间的小时差
     */
    public static long getLocalDateTimeHoursBetween(LocalDateTime localDateTime1, LocalDateTime localDateTime2) {
        if (localDateTime1 == null || localDateTime2 == null) return 0;
        return Math.abs(Duration.between(localDateTime1, localDateTime2).toHours());
    }

    /**
     * 获取两个LocalDateTime之间的分钟差
     */
    public static long getLocalDateTimeMinutesBetween(LocalDateTime localDateTime1, LocalDateTime localDateTime2) {
        if (localDateTime1 == null || localDateTime2 == null) return 0;
        return Math.abs(Duration.between(localDateTime1, localDateTime2).toMinutes());
    }

    /**
     * 获取两个LocalDateTime之间的秒差
     */
    public static long getLocalDateTimeSecondsBetween(LocalDateTime localDateTime1, LocalDateTime localDateTime2) {
        if (localDateTime1 == null || localDateTime2 == null) return 0;
        return Math.abs(Duration.between(localDateTime1, localDateTime2).getSeconds());
    }

    /**
     * 获取两个LocalDateTime之间的毫秒差
     */
    public static long getLocalDateTimeMillisBetween(LocalDateTime localDateTime1, LocalDateTime localDateTime2) {
        if (localDateTime1 == null || localDateTime2 == null) return 0;
        return Math.abs(Duration.between(localDateTime1, localDateTime2).toMillis());
    }

    /**
     * 获取两个LocalDateTime之间的纳秒差
     */
    public static long getLocalDateTimeNanosBetween(LocalDateTime localDateTime1, LocalDateTime localDateTime2) {
        if (localDateTime1 == null || localDateTime2 == null) return 0;
        return Math.abs(Duration.between(localDateTime1, localDateTime2).toNanos());
    }

    // ==================== LocalDateTime 转换方法 ====================

    /**
     * LocalDateTime 转 LocalDate
     */
    public static LocalDate localDateTimeToLocalDate(LocalDateTime localDateTime) {
        if (localDateTime == null) return null;
        return localDateTime.toLocalDate();
    }

    /**
     * LocalDateTime 转 LocalTime
     */
    public static LocalTime localDateTimeToLocalTime(LocalDateTime localDateTime) {
        if (localDateTime == null) return null;
        return localDateTime.toLocalTime();
    }

    /**
     * LocalDateTime 转 Instant
     */
    public static Instant localDateTimeToInstant(LocalDateTime localDateTime) {
        if (localDateTime == null) return null;
        return localDateTime.atZone(ZoneId.systemDefault()).toInstant();
    }

    /**
     * LocalDateTime 转 ZonedDateTime
     */
    public static ZonedDateTime localDateTimeToZonedDateTime(LocalDateTime localDateTime) {
        if (localDateTime == null) return null;
        return localDateTime.atZone(ZoneId.systemDefault());
    }

    /**
     * LocalDateTime 转 OffsetDateTime
     */
    public static OffsetDateTime localDateTimeToOffsetDateTime(LocalDateTime localDateTime) {
        if (localDateTime == null) return null;
        return localDateTime.atZone(ZoneId.systemDefault()).toOffsetDateTime();
    }

    /**
     * LocalDate 转 LocalDateTime（默认时间00:00:00）
     */
    public static LocalDateTime localDateToLocalDateTime(LocalDate localDate) {
        if (localDate == null) return null;
        return localDate.atStartOfDay();
    }

    /**
     * LocalTime 转 LocalDateTime（默认日期今天）
     */
    public static LocalDateTime localTimeToLocalDateTime(LocalTime localTime) {
        if (localTime == null) return null;
        return LocalDate.now().atTime(localTime);
    }

    // ==================== java.util.Calendar 相关方法 ====================

    /**
     * java.util.Date 转 Calendar
     */
    public static Calendar utilDateToCalendar(Date utilDate) {
        if (utilDate == null) return null;
        Calendar cal = Calendar.getInstance();
        cal.setTime(utilDate);
        return cal;
    }

    /**
     * Calendar 转 java.util.Date
     */
    public static Date calendarToUtilDate(Calendar calendar) {
        if (calendar == null) return null;
        return calendar.getTime();
    }

    /**
     * 获取Calendar的年份
     */
    public static int getYear(Calendar calendar) {
        if (calendar == null) return 0;
        return calendar.get(Calendar.YEAR);
    }

    /**
     * 获取Calendar的月份（0-11）
     */
    public static int getMonth(Calendar calendar) {
        if (calendar == null) return 0;
        return calendar.get(Calendar.MONTH);
    }

    /**
     * 获取Calendar的日期
     */
    public static int getDay(Calendar calendar) {
        if (calendar == null) return 0;
        return calendar.get(Calendar.DAY_OF_MONTH);
    }

    /**
     * 获取Calendar的小时
     */
    public static int getHour(Calendar calendar) {
        if (calendar == null) return 0;
        return calendar.get(Calendar.HOUR_OF_DAY);
    }

    /**
     * 获取Calendar的分钟
     */
    public static int getMinute(Calendar calendar) {
        if (calendar == null) return 0;
        return calendar.get(Calendar.MINUTE);
    }

    /**
     * 获取Calendar的秒
     */
    public static int getSecond(Calendar calendar) {
        if (calendar == null) return 0;
        return calendar.get(Calendar.SECOND);
    }


    // ==================== Calendar 时间计算方法 ====================

    /**
     * 获取Calendar的N天前
     */
    public static Calendar getNDaysAgo(Calendar calendar, int days) {
        if (calendar == null) return null;
        Calendar result = (Calendar) calendar.clone();
        result.add(Calendar.DAY_OF_MONTH, -days);
        return result;
    }

    /**
     * 获取Calendar的N天后
     */
    public static Calendar getNDaysLater(Calendar calendar, int days) {
        if (calendar == null) return null;
        Calendar result = (Calendar) calendar.clone();
        result.add(Calendar.DAY_OF_MONTH, days);
        return result;
    }

    /**
     * 获取Calendar的N年前
     */
    public static Calendar getNYearsAgo(Calendar calendar, int years) {
        if (calendar == null) return null;
        Calendar result = (Calendar) calendar.clone();
        result.add(Calendar.YEAR, -years);
        return result;
    }

    /**
     * 获取Calendar的N年后
     */
    public static Calendar getNYearsLater(Calendar calendar, int years) {
        if (calendar == null) return null;
        Calendar result = (Calendar) calendar.clone();
        result.add(Calendar.YEAR, years);
        return result;
    }

    /**
     * 获取Calendar的N月前
     */
    public static Calendar getNMonthsAgo(Calendar calendar, int months) {
        if (calendar == null) return null;
        Calendar result = (Calendar) calendar.clone();
        result.add(Calendar.MONTH, -months);
        return result;
    }

    /**
     * 获取Calendar的N月后
     */
    public static Calendar getNMonthsLater(Calendar calendar, int months) {
        if (calendar == null) return null;
        Calendar result = (Calendar) calendar.clone();
        result.add(Calendar.MONTH, months);
        return result;
    }

    // ==================== Calendar 时间比较方法 ====================

    /**
     * 判断Calendar1是否在Calendar2之前
     */
    public static boolean isCalendarBefore(Calendar calendar1, Calendar calendar2) {
        if (calendar1 == null || calendar2 == null) return false;
        return calendar1.before(calendar2);
    }

    /**
     * 判断Calendar1是否在Calendar2之后
     */
    public static boolean isCalendarAfter(Calendar calendar1, Calendar calendar2) {
        if (calendar1 == null || calendar2 == null) return false;
        return calendar1.after(calendar2);
    }

    /**
     * 判断两个Calendar是否相等
     */
    public static boolean isCalendarEqual(Calendar calendar1, Calendar calendar2) {
        if (calendar1 == null || calendar2 == null) return false;
        return calendar1.equals(calendar2);
    }

    // ==================== Calendar 字符串格式方法 ====================

    /**
     * Calendar转字符串（默认格式：yyyy-MM-dd HH:mm:ss）
     */
    public static String calendarToString(Calendar calendar) {
        return calendarToString(calendar, YYYY_MM_DD_HH_MM_SS_DATE_FORMAT);
    }

    /**
     * Calendar转字符串（指定格式）
     */
    public static String calendarToString(Calendar calendar, String format) {
        if (calendar == null) return null;
        try {
            SimpleDateFormat dateFormat = new SimpleDateFormat(format);
            return dateFormat.format(calendar.getTime());
        } catch (Exception e) {
            log.info("Calendar转字符串异常, 异常原因:{}", e.getMessage(), e);
            return null;
        }
    }

    /**
     * 字符串转Calendar（默认格式：yyyy-MM-dd HH:mm:ss）
     */
    public static Calendar stringToCalendar(String dateStr) {
        return stringToCalendar(dateStr, YYYY_MM_DD_HH_MM_SS_DATE_FORMAT);
    }

    /**
     * 字符串转Calendar（指定格式）
     */
    public static Calendar stringToCalendar(String dateStr, String format) {
        if (dateStr == null || dateStr.trim().isEmpty()) return null;
        try {
            SimpleDateFormat dateFormat = new SimpleDateFormat(format);
            Date date = dateFormat.parse(dateStr);
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(date);
            return calendar;
        } catch (Exception e) {
            log.info("字符串转Calendar异常, 异常原因:{}", e.getMessage(), e);
            return null;
        }
    }

    /**
     * Calendar转日期字符串（yyyy-MM-dd）
     */
    public static String calendarToDateString(Calendar calendar) {
        return calendarToString(calendar, YYYY_MM_DD_DATE_FORMAT);
    }

    /**
     * Calendar转时间字符串（HH:mm:ss）
     */
    public static String calendarToTimeString(Calendar calendar) {
        if (calendar == null) return null;
        try {
            SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm:ss");
            return timeFormat.format(calendar.getTime());
        } catch (Exception e) {
            log.info("Calendar转时间字符串异常, 异常原因:{}", e.getMessage(), e);
            return null;
        }
    }

    /**
     * Calendar转年月字符串（yyyy-MM）
     */
    public static String calendarToYearMonthString(Calendar calendar) {
        return calendarToString(calendar, YYYY_MM_DATE_FORMAT);
    }

    /**
     * Calendar转月日字符串（MM-dd）
     */
    public static String calendarToMonthDayString(Calendar calendar) {
        return calendarToString(calendar, MM_DD_DATE_FORMAT);
    }

    // ==================== Calendar 实用工具方法 ====================

    /**
     * 获取Calendar的毫秒时间戳
     */
    public static long getCalendarTimeInMillis(Calendar calendar) {
        if (calendar == null) return 0;
        return calendar.getTimeInMillis();
    }

    /**
     * 设置Calendar的毫秒时间戳
     */
    public static Calendar setCalendarTimeInMillis(Calendar calendar, long timeInMillis) {
        if (calendar == null) return null;
        Calendar result = (Calendar) calendar.clone();
        result.setTimeInMillis(timeInMillis);
        return result;
    }

    /**
     * 获取Calendar的星期几（1-7，1代表星期日）
     */
    public static int getDayOfWeek(Calendar calendar) {
        if (calendar == null) return 0;
        return calendar.get(Calendar.DAY_OF_WEEK);
    }

    /**
     * 获取Calendar的星期几中文描述
     */
    public static String getDayOfWeekString(Calendar calendar) {
        if (calendar == null) return null;
        String[] weekDays = {"星期日", "星期一", "星期二", "星期三", "星期四", "星期五", "星期六"};
        int dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK);
        return weekDays[dayOfWeek - 1];
    }

    /**
     * 获取Calendar是否为周末
     */
    public static boolean isWeekend(Calendar calendar) {
        if (calendar == null) return false;
        int dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK);
        return dayOfWeek == Calendar.SATURDAY || dayOfWeek == Calendar.SUNDAY;
    }

    /**
     * 获取Calendar是否为工作日
     */
    public static boolean isWorkday(Calendar calendar) {
        return !isWeekend(calendar);
    }

    /**
     * 获取Calendar的季度
     */
    public static int getQuarter(Calendar calendar) {
        if (calendar == null) return 0;
        int month = calendar.get(Calendar.MONTH);
        return (month / 3) + 1;
    }

    /**
     * 获取Calendar的季度字符串
     */
    public static String getQuarterString(Calendar calendar) {
        if (calendar == null) return null;
        int quarter = getQuarter(calendar);
        return "第" + quarter + "季度";
    }

    /**
     * 获取Calendar的年份中的第几周
     */
    public static int getWeekOfYear(Calendar calendar) {
        if (calendar == null) return 0;
        return calendar.get(Calendar.WEEK_OF_YEAR);
    }

    /**
     * 获取Calendar的月份中的第几周
     */
    public static int getWeekOfMonth(Calendar calendar) {
        if (calendar == null) return 0;
        return calendar.get(Calendar.WEEK_OF_MONTH);
    }

    /**
     * 获取Calendar的年份中的第几天
     */
    public static int getDayOfYear(Calendar calendar) {
        if (calendar == null) return 0;
        return calendar.get(Calendar.DAY_OF_YEAR);
    }

    /**
     * 获取Calendar的月份中的第几天
     */
    public static int getDayOfMonth(Calendar calendar) {
        if (calendar == null) return 0;
        return calendar.get(Calendar.DAY_OF_MONTH);
    }

    /**
     * 获取Calendar的月份中的第几周的第几天
     */
    public static int getDayOfWeekInMonth(Calendar calendar) {
        if (calendar == null) return 0;
        return calendar.get(Calendar.DAY_OF_WEEK_IN_MONTH);
    }

    /**
     * 获取Calendar的时区
     */
    public static TimeZone getTimeZone(Calendar calendar) {
        if (calendar == null) return null;
        return calendar.getTimeZone();
    }

    /**
     * 设置Calendar的时区
     */
    public static Calendar setTimeZone(Calendar calendar, TimeZone timeZone) {
        if (calendar == null || timeZone == null) return null;
        Calendar result = (Calendar) calendar.clone();
        result.setTimeZone(timeZone);
        return result;
    }

    /**
     * 获取Calendar的时区ID
     */
    public static String getTimeZoneID(Calendar calendar) {
        if (calendar == null) return null;
        TimeZone timeZone = calendar.getTimeZone();
        return timeZone != null ? timeZone.getID() : null;
    }

    /**
     * 获取Calendar的时区显示名称
     */
    public static String getTimeZoneDisplayName(Calendar calendar) {
        if (calendar == null) return null;
        TimeZone timeZone = calendar.getTimeZone();
        return timeZone != null ? timeZone.getDisplayName() : null;
    }

    /**
     * 获取Calendar的时区偏移量（毫秒）
     */
    public static int getTimeZoneOffset(Calendar calendar) {
        if (calendar == null) return 0;
        TimeZone timeZone = calendar.getTimeZone();
        return timeZone != null ? timeZone.getOffset(calendar.getTimeInMillis()) : 0;
    }

    /**
     * 获取Calendar的时区偏移量字符串（如：+08:00）
     */
    public static String getTimeZoneOffsetString(Calendar calendar) {
        if (calendar == null) return null;
        int offset = getTimeZoneOffset(calendar);
        int hours = Math.abs(offset) / (60 * 60 * 1000);
        int minutes = (Math.abs(offset) % (60 * 60 * 1000)) / (60 * 1000);
        String sign = offset >= 0 ? "+" : "-";
        return String.format("%s%02d:%02d", sign, hours, minutes);
    }

    /**
     * 获取Calendar的时区偏移量小时数
     */
    public static int getTimeZoneOffsetHours(Calendar calendar) {
        if (calendar == null) return 0;
        int offset = getTimeZoneOffset(calendar);
        return offset / (60 * 60 * 1000);
    }

    /**
     * 获取Calendar的时区偏移量分钟数
     */
    public static int getTimeZoneOffsetMinutes(Calendar calendar) {
        if (calendar == null) return 0;
        int offset = getTimeZoneOffset(calendar);
        return (offset % (60 * 60 * 1000)) / (60 * 1000);
    }

    /**
     * 获取Calendar的时区偏移量秒数
     */
    public static int getTimeZoneOffsetSeconds(Calendar calendar) {
        if (calendar == null) return 0;
        int offset = getTimeZoneOffset(calendar);
        return (offset % (60 * 1000)) / 1000;
    }

    /**
     * 获取Calendar的时区偏移量毫秒数
     */
    public static int getTimeZoneOffsetMillis(Calendar calendar) {
        if (calendar == null) return 0;
        int offset = getTimeZoneOffset(calendar);
        return offset % 1000;
    }

    /**
     * 获取Calendar的时区偏移量总秒数
     */
    public static int getTimeZoneOffsetTotalSeconds(Calendar calendar) {
        if (calendar == null) return 0;
        int offset = getTimeZoneOffset(calendar);
        return offset / 1000;
    }

    /**
     * 获取Calendar的时区偏移量总分钟数
     */
    public static int getTimeZoneOffsetTotalMinutes(Calendar calendar) {
        if (calendar == null) return 0;
        int offset = getTimeZoneOffset(calendar);
        return offset / (60 * 1000);
    }

    /**
     * 获取Calendar的时区偏移量总小时数
     */
    public static int getTimeZoneOffsetTotalHours(Calendar calendar) {
        if (calendar == null) return 0;
        int offset = getTimeZoneOffset(calendar);
        return offset / (60 * 60 * 1000);
    }

    /**
     * 获取Calendar的时区偏移量总天数
     */
    public static int getTimeZoneOffsetTotalDays(Calendar calendar) {
        if (calendar == null) return 0;
        int offset = getTimeZoneOffset(calendar);
        return offset / (24 * 60 * 60 * 1000);
    }

    /**
     * 获取Calendar的时区偏移量总周数
     */
    public static int getTimeZoneOffsetTotalWeeks(Calendar calendar) {
        if (calendar == null) return 0;
        int offset = getTimeZoneOffset(calendar);
        return offset / (7 * 24 * 60 * 60 * 1000);
    }


    /**
     * 获取Calendar的时区偏移量总周数（字符串）
     */
    public static String getTimeZoneOffsetTotalWeeksString(Calendar calendar) {
        if (calendar == null) return null;
        int weeks = getTimeZoneOffsetTotalWeeks(calendar);
        return weeks + "周";
    }

    /**
     * 获取Calendar的时区偏移量总天数（字符串）
     */
    public static String getTimeZoneOffsetTotalDaysString(Calendar calendar) {
        if (calendar == null) return null;
        int days = getTimeZoneOffsetTotalDays(calendar);
        return days + "天";
    }

    /**
     * 获取Calendar的时区偏移量总小时数（字符串）
     */
    public static String getTimeZoneOffsetTotalHoursString(Calendar calendar) {
        if (calendar == null) return null;
        int hours = getTimeZoneOffsetTotalHours(calendar);
        return hours + "小时";
    }

    /**
     * 获取Calendar的时区偏移量总分钟数（字符串）
     */
    public static String getTimeZoneOffsetTotalMinutesString(Calendar calendar) {
        if (calendar == null) return null;
        int minutes = getTimeZoneOffsetTotalMinutes(calendar);
        return minutes + "分钟";
    }

    /**
     * 获取Calendar的时区偏移量总秒数（字符串）
     */
    public static String getTimeZoneOffsetTotalSecondsString(Calendar calendar) {
        if (calendar == null) return null;
        int seconds = getTimeZoneOffsetTotalSeconds(calendar);
        return seconds + "秒";
    }

    /**
     * 获取Calendar的时区偏移量总毫秒数（字符串）
     */
    public static String getTimeZoneOffsetTotalMillisString(Calendar calendar) {
        if (calendar == null) return null;
        int millis = getTimeZoneOffsetMillis(calendar);
        return millis + "毫秒";
    }

    /**
     * 获取Calendar的时区偏移量总纳秒数（字符串）
     */
    public static String getTimeZoneOffsetTotalNanosString(Calendar calendar) {
        if (calendar == null) return null;
        int millis = getTimeZoneOffsetMillis(calendar);
        int nanos = millis * 1000000;
        return nanos + "纳秒";
    }

    /**
     * 获取Calendar的时区偏移量总皮秒数（字符串）
     */
    public static String getTimeZoneOffsetTotalPicosString(Calendar calendar) {
        if (calendar == null) return null;
        int millis = getTimeZoneOffsetMillis(calendar);
        long picos = millis * 1000000000L;
        return picos + "皮秒";
    }

    /**
     * 获取Calendar的时区偏移量总飞秒数（字符串）
     */
    public static String getTimeZoneOffsetTotalFemtosString(Calendar calendar) {
        if (calendar == null) return null;
        int millis = getTimeZoneOffsetMillis(calendar);
        long femtos = millis * 1000000000000L;
        return femtos + "飞秒";
    }

    /**
     * 获取Calendar的时区偏移量总阿秒数（字符串）
     */
    public static String getTimeZoneOffsetTotalAttosString(Calendar calendar) {
        if (calendar == null) return null;
        int millis = getTimeZoneOffsetMillis(calendar);
        long attos = millis * 1000000000000000L;
        return attos + "阿秒";
    }

    /**
     * 获取Calendar的时区偏移量总仄秒数（字符串）
     */
    public static String getTimeZoneOffsetTotalZeptosString(Calendar calendar) {
        if (calendar == null) return null;
        int millis = getTimeZoneOffsetMillis(calendar);
        long zeptos = millis * 1000000000000000000L;
        return zeptos + "仄秒";
    }

    // ==================== 时间判断方法 ====================

    /**
     * 判断是否为今天
     */
    public static boolean isToday(Date date) {
        if (date == null) return false;
        Calendar cal1 = Calendar.getInstance();
        Calendar cal2 = Calendar.getInstance();
        cal1.setTime(date);
        cal2.setTime(new Date());
        return cal1.get(Calendar.YEAR) == cal2.get(Calendar.YEAR) &&
                cal1.get(Calendar.DAY_OF_YEAR) == cal2.get(Calendar.DAY_OF_YEAR);
    }

    /**
     * 判断是否为昨天
     */
    public static boolean isYesterday(Date date) {
        if (date == null) return false;
        Calendar cal1 = Calendar.getInstance();
        Calendar cal2 = Calendar.getInstance();
        cal1.setTime(date);
        cal2.setTime(new Date());
        cal2.add(Calendar.DAY_OF_YEAR, -1);
        return cal1.get(Calendar.YEAR) == cal2.get(Calendar.YEAR) &&
                cal1.get(Calendar.DAY_OF_YEAR) == cal2.get(Calendar.DAY_OF_YEAR);
    }

    /**
     * 判断是否为明天
     */
    public static boolean isTomorrow(Date date) {
        if (date == null) return false;
        Calendar cal1 = Calendar.getInstance();
        Calendar cal2 = Calendar.getInstance();
        cal1.setTime(date);
        cal2.setTime(new Date());
        cal2.add(Calendar.DAY_OF_YEAR, 1);
        return cal1.get(Calendar.YEAR) == cal2.get(Calendar.YEAR) &&
                cal1.get(Calendar.DAY_OF_YEAR) == cal2.get(Calendar.DAY_OF_YEAR);
    }

    /**
     * 获取两个日期之间的天数差
     */
    public static long getDaysBetween(Date date1, Date date2) {
        if (date1 == null || date2 == null) return 0;
        long diffInMillies = Math.abs(date2.getTime() - date1.getTime());
        return diffInMillies / (24 * 60 * 60 * 1000);
    }
}