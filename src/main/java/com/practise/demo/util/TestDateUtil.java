package com.practise.demo.util;

import lombok.extern.slf4j.Slf4j;

import java.time.LocalDateTime;
import java.util.Calendar;
import java.util.Date;

/**
 * 测试类，避免重复造轮子
 */
@Slf4j
class TestDateUtil {

    public void runAllTests() {
        log.info("========== 开始运行所有测试用例 ==========");

        testBasicConversions();
        testTimeComparisons();
        testTimeCalculations();
        testSqlDateOperations();
        testLocalDateTimeOperations();
        testCalendarOperations();
        testTimeJudgments();

        log.info("========== 所有测试用例运行完成 ==========");
    }

    public void testBasicConversions() {
        log.info("--- 测试基础转换方法 ---");

        // 测试字符串转日期
        Date date1 = DateUtil.strTurnDate("2024-01-15 10:30:00", DateUtil.YYYY_MM_DD_HH_MM_SS_DATE_FORMAT);
        assert date1 != null : "✅ 字符串转日期成功";
        log.info("✅ 字符串转日期测试通过");

        // 测试日期转字符串
        String dateStr = DateUtil.dateTurnDateStr(date1, DateUtil.YYYY_MM_DD_HH_MM_SS_DATE_FORMAT);
        assert "2024-01-15 10:30:00".equals(dateStr) : "✅ 日期转字符串成功";
        log.info("✅ 日期转字符串测试通过");

        // 测试无效格式
        Date invalidDate = DateUtil.strTurnDate("invalid-date", DateUtil.YYYY_MM_DD_HH_MM_SS_DATE_FORMAT);
        assert invalidDate == null : "✅ 无效格式处理正确";
        log.info("✅ 无效格式处理测试通过");
    }

    public void testTimeComparisons() {
        log.info("--- 测试时间比较方法 ---");

        Date now = new Date();
        Date future = DateUtil.addDays(now, 1);
        Date past = DateUtil.minusDays(now, 1);

        // 测试时间比较
        assert DateUtil.isBefore(past, now) : "✅ 过去时间比较正确";
        assert DateUtil.isAfter(future, now) : "✅ 未来时间比较正确";
        assert DateUtil.isEqual(now, now) : "✅ 相等时间比较正确";

        // 测试N天前/后判断
        assert DateUtil.isNDaysAgo(past, 1) : "✅ N天前判断正确";
        assert DateUtil.isNDaysLater(future, 1) : "✅ N天后判断正确";

        log.info("✅ 时间比较测试通过");
    }

    public void testTimeCalculations() {
        log.info("--- 测试时间计算方法 ---");

        Date baseDate = DateUtil.strTurnDate("2024-01-15 10:30:00", DateUtil.YYYY_MM_DD_HH_MM_SS_DATE_FORMAT);

        // 测试加减天数
        Date nextDay = DateUtil.addDays(baseDate, 1);
        Date prevDay = DateUtil.minusDays(baseDate, 1);
        assert DateUtil.getDaysBetween(baseDate, nextDay) == 1 : "✅ 加天数计算正确";
        assert DateUtil.getDaysBetween(baseDate, prevDay) == 1 : "✅ 减天数计算正确";

        // 测试加减小时
        Date nextHour = DateUtil.addHours(baseDate, 1);
        Date prevHour = DateUtil.minusHours(baseDate, 1);
        assert DateUtil.getTimeDifference(baseDate, nextHour) == 3600000 : "✅ 加小时计算正确";
        assert DateUtil.getTimeDifference(baseDate, prevHour) == 3600000 : "✅ 减小时计算正确";

        log.info("✅ 时间计算测试通过");
    }

    public void testSqlDateOperations() {
        log.info("--- 测试SQL日期操作 ---");

        Date utilDate = new Date();
        java.sql.Date sqlDate = DateUtil.utilDateToSqlDate(utilDate);
        Date convertedUtilDate = DateUtil.sqlDateToUtilDate(sqlDate);

        assert sqlDate != null : "✅ util.Date转sql.Date成功";
        assert convertedUtilDate != null : "✅ sql.Date转util.Date成功";
        assert Math.abs(utilDate.getTime() - convertedUtilDate.getTime()) < 1000 : "✅ 转换精度正确";

        // 测试字符串转SQL日期
        java.sql.Date strSqlDate = DateUtil.strToSqlDate("2024-01-15", DateUtil.YYYY_MM_DD_DATE_FORMAT);
        assert strSqlDate != null : "✅ 字符串转SQL日期成功";

        log.info("✅ SQL日期操作测试通过");
    }

    public void testLocalDateTimeOperations() {
        log.info("--- 测试LocalDateTime操作 ---");

        Date utilDate = new Date();
        LocalDateTime localDateTime = DateUtil.utilDateToLocalDateTime(utilDate);
        Date convertedUtilDate = DateUtil.localDateTimeToUtilDate(localDateTime);

        assert localDateTime != null : "✅ util.Date转LocalDateTime成功";
        assert convertedUtilDate != null : "✅ LocalDateTime转util.Date成功";
        assert Math.abs(utilDate.getTime() - convertedUtilDate.getTime()) < 1000 : "✅ 转换精度正确";

        // 测试字符串转LocalDateTime
        LocalDateTime strLocalDateTime = DateUtil.strToLocalDateTime("2024-01-15 10:30:00", DateUtil.YYYY_MM_DD_HH_MM_SS_DATE_FORMAT);
        assert strLocalDateTime != null : "✅ 字符串转LocalDateTime成功";

        // 测试LocalDateTime转字符串
        String localDateTimeStr = DateUtil.localDateTimeToStr(strLocalDateTime, DateUtil.YYYY_MM_DD_HH_MM_SS_DATE_FORMAT);
        assert "2024-01-15 10:30:00".equals(localDateTimeStr) : "✅ LocalDateTime转字符串成功";

        log.info("✅ LocalDateTime操作测试通过");
    }

    public void testCalendarOperations() {
        log.info("--- 测试Calendar操作 ---");

        Date utilDate = new Date();
        Calendar calendar = DateUtil.utilDateToCalendar(utilDate);
        Date convertedUtilDate = DateUtil.calendarToUtilDate(calendar);

        assert calendar != null : "✅ util.Date转Calendar成功";
        assert convertedUtilDate != null : "✅ Calendar转util.Date成功";
        assert Math.abs(utilDate.getTime() - convertedUtilDate.getTime()) < 1000 : "✅ 转换精度正确";

        // 测试Calendar字段获取
        assert DateUtil.getYear(calendar) > 0 : "✅ 获取年份成功";
        assert DateUtil.getMonth(calendar) >= 0 : "✅ 获取月份成功";
        assert DateUtil.getDay(calendar) > 0 : "✅ 获取日期成功";

        log.info("✅ Calendar操作测试通过");
    }

    public void testTimeJudgments() {
        log.info("--- 测试时间判断方法 ---");

        Date now = new Date();
        Date yesterday = DateUtil.minusDays(now, 1);
        Date tomorrow = DateUtil.addDays(now, 1);

        // 测试今天/昨天/明天判断
        assert DateUtil.isToday(now) : "✅ 今天判断正确";
        assert DateUtil.isYesterday(yesterday) : "✅ 昨天判断正确";
        assert DateUtil.isTomorrow(tomorrow) : "✅ 明天判断正确";

        // 测试天数差计算
        assert DateUtil.getDaysBetween(now, tomorrow) == 1 : "✅ 天数差计算正确";
        assert DateUtil.getDaysBetween(now, yesterday) == 1 : "✅ 天数差计算正确";

        log.info("✅ 时间判断测试通过");
    }
}