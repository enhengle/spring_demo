package com.practise.demo;

import com.practise.demo.util.DateUtil;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.text.ParseException;

@SpringBootTest
class DemoApplicationTests {

    public static String YMD = "yyyy-MM-dd";

    @Test
    void contextLoads() throws ParseException {
        String date = DateUtil.checkDateToString(DateUtil.getDayBefore("2022-01-01", 01), YMD);
        System.out.print("获取2022-01-01后一天的日期:");
        System.out.println(date);
        date = DateUtil.checkDateToString(DateUtil.getDayBefore("2022-01-01", -1), YMD);
        System.out.print("获取2022-01-01前一天的日期:");
        System.out.println(date);
        date = DateUtil.checkDateToString(DateUtil.getMonthBefore("2022-01-01", 1), YMD);
        System.out.print("获取2022-01-01后一个月的日期:");
        System.out.println(date);
        date = DateUtil.checkDateToString(DateUtil.getMonthBefore("2022-01-01", -1), YMD);
        System.out.print("获取2022-01-01前一个月的日期:");
        System.out.println(date);
        date = DateUtil.checkDateToString(DateUtil.getMonday("2022-01-01"), YMD);
        System.out.print("获取2022-01-01周一的日期:");
        System.out.println(date);
        date = DateUtil.checkDateToString(DateUtil.getSunday("2022-01-01"), YMD);
        System.out.print("获取2022-01-01周末的日期:");
        System.out.println(date);

        date = DateUtil.checkDateToString(DateUtil.getMonthLastDay("2022-01-02"), YMD);
        System.out.print("获取2022-01-02月末的日期:");
        System.out.println(date);

        date = DateUtil.checkDateToString(DateUtil.getMonthLastDay("2022-01-02",2), YMD);
        System.out.print("获取2022-01-02后两月月末的日期:");
        System.out.println(date);
        date = DateUtil.checkDateToString(DateUtil.getMonthLastDay("2022-01-02",-2), YMD);
        System.out.print("获取2022-01-02前两月月末的日期:");
        System.out.println(date);
        date = DateUtil.checkDateToString(DateUtil.getMonthLastDay("2022-01-02",2), YMD);
        System.out.print("获取2022-01-02后两月月末的日期:");
        System.out.println(date);
        date = DateUtil.checkDateToString(DateUtil.getMonthLastDay("2022-01-02",-2), YMD);
        System.out.print("获取2022-01-02前两月月末的日期:");
        System.out.println(date);

        date = DateUtil.checkDateToString(DateUtil.getYearFirstDay("2022-01-02"), YMD);
        System.out.print("获取2022-01-02当年第一天的日期:");
        System.out.println(date);
        date = DateUtil.checkDateToString(DateUtil.getYearFirstDay("2022-01-02",2), YMD);
        System.out.print("获取2022-01-02当年后两年第一天的日期:");
        System.out.println(date);
        date = DateUtil.checkDateToString(DateUtil.getYearFirstDay("2022-01-02",-2), YMD);
        System.out.print("获取2022-01-02当年前两年第一天的日期:");
        System.out.println(date);


        date = DateUtil.checkDateToString(DateUtil.getYearLastDay("2022-01-02"), YMD);
        System.out.print("获取2022-01-02当年最后一天的日期:");
        System.out.println(date);
        date = DateUtil.checkDateToString(DateUtil.getYearLastDay("2022-01-02",2), YMD);
        System.out.print("获取2022-01-02当年后两年最后一天的日期:");
        System.out.println(date);
        date = DateUtil.checkDateToString(DateUtil.getYearLastDay("2022-01-02",-2), YMD);
        System.out.print("获取2022-01-02当年前两年最后一天的日期:");
        System.out.println(date);
    }

}
