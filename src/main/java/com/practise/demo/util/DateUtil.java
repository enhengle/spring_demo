package com.practise.demo.util;


import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * @author lingwang
 * @date 2021/3/15 20:14
 * 日期处理-常用方法
 * 此处需注意，不用try_catch
 */
public class DateUtil {

    //年月日
    public static String YMD = "yyyy-MM-dd";

    /*
     * 校验并将string类型转成Date类型
     * */
    public static Date checkStringToDate(String day) throws ParseException {
        SimpleDateFormat format = new SimpleDateFormat(YMD);
        Date date = format.parse(day);
        return date;
    }

    /*
     * 校验并将string类型转成Date类型
     * */
    public static String checkDateToString(Date day, String format) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(format);
        return simpleDateFormat.format(day);
    }


    /*
     * 校验并将日期转换成想要的格式
     * */
    public static String changeFormatString(String date, String format1, String format2) throws ParseException {
        String endDate = "";
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(format1);
        Date currentDate = simpleDateFormat.parse(endDate);
        simpleDateFormat = new SimpleDateFormat(format2);
        endDate = simpleDateFormat.format(currentDate);
        return endDate;
    }


    /*
     * 获取指定日期的第N天日期
     * */
    public static Date getDayBefore(Date date, int before) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        int day_current = calendar.get(Calendar.DATE);
        calendar.set(Calendar.DATE, day_current + before);
        return calendar.getTime();
    }

    public static Date getDayBefore(String date, int before) throws ParseException {
        return getDayBefore(checkStringToDate(date), before);
    }


    /*
     * 获取指定日期的前N月日期
     * */
    public static Date getMonthBefore(Date date, int before) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        int month_current = calendar.get(Calendar.DATE);
        calendar.set(Calendar.MONTH, month_current + before - 1);
        return calendar.getTime();
    }

    public static Date getMonthBefore(String date, int before) throws ParseException {
        return getMonthBefore(checkStringToDate(date), before);
    }

    /*
     * 获取周一
     * */
    public static Date getMonday(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.setFirstDayOfWeek(calendar.MONDAY);
        int num = calendar.get(Calendar.DAY_OF_WEEK);
        if (num == 1) {
            num = 8;
        }
        calendar.add(Calendar.DATE, calendar.getFirstDayOfWeek() - num);
        return calendar.getTime();
    }

    public static Date getMonday(String date) throws ParseException {
        return getMonday(checkStringToDate(date));
    }

    /*
     * 获取周末
     * */
    public static Date getSunday(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        int num = calendar.get(Calendar.DAY_OF_WEEK) - 1;
        if (num == 0) {
            num = 7;
        }
        calendar.add(Calendar.DATE, -num + 7);
        return calendar.getTime();
    }

    public static Date getSunday(String date) throws ParseException {
        return getSunday(checkStringToDate(date));
    }

    /*
     * 获取当月第一天
     * */
    public static Date getMonthFirstDay(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.set(Calendar.DAY_OF_MONTH, 1);
        return calendar.getTime();
    }

    public static Date getMonthFirstDay(String date) throws ParseException {
        return getMonthFirstDay(checkStringToDate(date));
    }

    /*
     * 获取N月前的第一天
     * */
    public static Date getMonthFirstDay(Date date, int before) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.set(Calendar.MONTH, calendar.get(calendar.MONTH) + before);
        calendar.set(Calendar.DAY_OF_MONTH, 1);
        return calendar.getTime();
    }

    public static Date getMonthFirstDay(String date, int before) throws ParseException {
        return getMonthFirstDay(checkStringToDate(date), before);
    }

    /*
     * 获取当月最后一天
     * */
    public static Date getMonthLastDay(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.set(Calendar.DAY_OF_MONTH, 0);
        return calendar.getTime();
    }

    public static Date getMonthLastDay(String date) throws ParseException {
        return getMonthLastDay(checkStringToDate(date));
    }

    /*
     * 获取N月前的最后一天
     * */
    public static Date getMonthLastDay(Date date, int before) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.set(Calendar.MONTH, calendar.get(calendar.MONTH) + before);
        calendar.set(Calendar.DAY_OF_MONTH, 0);
        return calendar.getTime();
    }

    public static Date getMonthLastDay(String date, int before) throws ParseException {
        return getMonthLastDay(checkStringToDate(date), before);
    }

    /*
     * 获取指定年的第一天
     * */
    public static Date getYearFirstDay(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.set(Calendar.DAY_OF_YEAR, 1);
        return calendar.getTime();
    }

    public static Date getYearFirstDay(String date) throws ParseException {
        return getYearFirstDay(checkStringToDate(date));
    }

    public static Date getYearFirstDay(Date date,int num) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        int year_current = calendar.get(Calendar.YEAR);
        calendar.set(Calendar.YEAR,year_current+num);
        calendar.set(Calendar.DAY_OF_YEAR, 1);
        return calendar.getTime();
    }
    public static Date getYearFirstDay(String date,int num) throws ParseException {
        return getYearFirstDay(checkStringToDate(date),num);
    }

    /*
     * 获取指定年的最后一天
     * */
    public static Date getYearLastDay(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.set(Calendar.DAY_OF_YEAR, 0);
        return calendar.getTime();
    }

    public static Date getYearLastDay(String date) throws ParseException {
        return getYearFirstDay(checkStringToDate(date));
    }
    public static Date getYearLastDay(Date date,int num) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        int year_current = calendar.get(Calendar.YEAR);
        calendar.set(Calendar.YEAR,year_current+num);
        calendar.set(Calendar.DAY_OF_YEAR, 1);
        return calendar.getTime();
    }
    public static Date getYearLastDay(String date,int num) throws ParseException {
        return getYearLastDay(checkStringToDate(date),num);
    }


}
