package com.practise.demo.util;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

/**
 * 中国国家日历工具类
 * 判断工作日和节假日
 * 
 * @author system
 */
public class ChineseCalendarUtil {
    
    /**
     * 2024年法定节假日（简化版，实际应该从API或配置文件获取）
     */
    private static final Set<LocalDate> HOLIDAYS_2024 = new HashSet<>();
    
    /**
     * 2024年调休工作日（周末需要上班的日期）
     */
    private static final Set<LocalDate> WORKDAYS_2024 = new HashSet<>();
    
    static {
        // 2024年法定节假日
        // 元旦：2024-01-01
        HOLIDAYS_2024.add(LocalDate.of(2024, 1, 1));
        
        // 春节：2024-02-10 至 2024-02-17
        for (int i = 10; i <= 17; i++) {
            HOLIDAYS_2024.add(LocalDate.of(2024, 2, i));
        }
        
        // 清明节：2024-04-04 至 2024-04-06
        for (int i = 4; i <= 6; i++) {
            HOLIDAYS_2024.add(LocalDate.of(2024, 4, i));
        }
        
        // 劳动节：2024-05-01 至 2024-05-05
        for (int i = 1; i <= 5; i++) {
            HOLIDAYS_2024.add(LocalDate.of(2024, 5, i));
        }
        
        // 端午节：2024-06-10
        HOLIDAYS_2024.add(LocalDate.of(2024, 6, 10));
        
        // 中秋节：2024-09-15 至 2024-09-17
        for (int i = 15; i <= 17; i++) {
            HOLIDAYS_2024.add(LocalDate.of(2024, 9, i));
        }
        
        // 国庆节：2024-10-01 至 2024-10-07
        for (int i = 1; i <= 7; i++) {
            HOLIDAYS_2024.add(LocalDate.of(2024, 10, i));
        }
        
        // 2024年调休工作日（周末需要上班的日期）
        // 春节调休：2024-02-04, 2024-02-18
        WORKDAYS_2024.add(LocalDate.of(2024, 2, 4));
        WORKDAYS_2024.add(LocalDate.of(2024, 2, 18));
        
        // 清明节调休：2024-04-07
        WORKDAYS_2024.add(LocalDate.of(2024, 4, 7));
        
        // 劳动节调休：2024-04-28, 2024-05-11
        WORKDAYS_2024.add(LocalDate.of(2024, 4, 28));
        WORKDAYS_2024.add(LocalDate.of(2024, 5, 11));
        
        // 国庆节调休：2024-09-29, 2024-10-12
        WORKDAYS_2024.add(LocalDate.of(2024, 9, 29));
        WORKDAYS_2024.add(LocalDate.of(2024, 10, 12));
    }
    
    /**
     * 判断指定日期是否为工作日
     * 
     * @param date 日期
     * @return true-工作日，false-节假日
     */
    public static boolean isWorkday(LocalDate date) {
        // 如果是调休工作日，直接返回true
        if (WORKDAYS_2024.contains(date)) {
            return true;
        }
        
        // 如果是法定节假日，返回false
        if (HOLIDAYS_2024.contains(date)) {
            return false;
        }
        
        // 判断是否为周末
        DayOfWeek dayOfWeek = date.getDayOfWeek();
        return dayOfWeek != DayOfWeek.SATURDAY && dayOfWeek != DayOfWeek.SUNDAY;
    }
    
    /**
     * 判断指定日期是否为节假日
     * 
     * @param date 日期
     * @return true-节假日，false-工作日
     */
    public static boolean isHoliday(LocalDate date) {
        return !isWorkday(date);
    }
}
