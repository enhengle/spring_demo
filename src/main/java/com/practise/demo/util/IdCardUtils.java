package com.practise.demo.util;

import lombok.extern.slf4j.Slf4j;

import java.util.regex.Pattern;

/**
 * 身份证工具类
 */
@Slf4j
public class IdCardUtils {

    private static final String ID_CARD_REGEX = "^[1-9]\\d{5}(18|19|20)\\d{2}((0[1-9])|(1[0-2]))(([0-2][1-9])|10|20|30|31)\\d{3}[0-9Xx]$";
    private static final Pattern ID_CARD_PATTERN = Pattern.compile(ID_CARD_REGEX);
    
    // 身份证号码加权因子
    private static final int[] WEIGHT_FACTORS = {7, 9, 10, 5, 8, 4, 2, 1, 6, 3, 7, 9, 10, 5, 8, 4, 2};
    
    // 身份证号码校验码
    private static final char[] CHECK_CODES = {'1', '0', 'X', '9', '8', '7', '6', '5', '4', '3', '2'};

    /**
     * 验证身份证号码是否合规
     */
    public static boolean isValidIdCard(String idCard) {
        if (idCard == null || idCard.trim().isEmpty()) {
            return false;
        }
        
        String trimmedIdCard = idCard.trim();
        
        // 基本格式验证
        if (!ID_CARD_PATTERN.matcher(trimmedIdCard).matches()) {
            return false;
        }
        
        // 长度验证
        if (trimmedIdCard.length() != 18) {
            return false;
        }
        
        // 地区码验证
        if (!isValidRegionCode(trimmedIdCard.substring(0, 6))) {
            return false;
        }
        
        // 出生日期验证
        if (!isValidBirthDate(trimmedIdCard.substring(6, 14))) {
            return false;
        }
        
        // 校验码验证
        return isValidCheckCode(trimmedIdCard);
    }

    /**
     * 验证地区码
     */
    private static boolean isValidRegionCode(String regionCode) {
        try {
            int code = Integer.parseInt(regionCode);
            // 地区码范围：110000-820000
            return code >= 110000 && code <= 820000;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    /**
     * 验证出生日期
     */
    private static boolean isValidBirthDate(String birthDate) {
        try {
            int year = Integer.parseInt(birthDate.substring(0, 4));
            int month = Integer.parseInt(birthDate.substring(4, 6));
            int day = Integer.parseInt(birthDate.substring(6, 8));
            
            // 年份范围：1900-当前年份
            int currentYear = java.time.LocalDate.now().getYear();
            if (year < 1900 || year > currentYear) {
                return false;
            }
            
            // 月份范围：1-12
            if (month < 1 || month > 12) {
                return false;
            }
            
            // 日期范围：1-31（简化验证）
            if (day < 1 || day > 31) {
                return false;
            }
            
            // 更精确的日期验证
            try {
                java.time.LocalDate.of(year, month, day);
                return true;
            } catch (Exception e) {
                return false;
            }
            
        } catch (NumberFormatException e) {
            return false;
        }
    }

    /**
     * 验证校验码
     */
    private static boolean isValidCheckCode(String idCard) {
        try {
            int sum = 0;
            for (int i = 0; i < 17; i++) {
                sum += Character.getNumericValue(idCard.charAt(i)) * WEIGHT_FACTORS[i];
            }
            
            int remainder = sum % 11;
            char expectedCheckCode = CHECK_CODES[remainder];
            char actualCheckCode = Character.toUpperCase(idCard.charAt(17));
            
            return expectedCheckCode == actualCheckCode;
            
        } catch (Exception e) {
            log.error("验证校验码失败: {}", idCard, e);
            return false;
        }
    }

    /**
     * 获取身份证信息
     */
    public static IdCardInfo getIdCardInfo(String idCard) {
        if (!isValidIdCard(idCard)) {
            return null;
        }
        
        try {
            String regionCode = idCard.substring(0, 6);
            String birthDate = idCard.substring(6, 14);
            String sequenceCode = idCard.substring(14, 17);
            char checkCode = idCard.charAt(17);
            
            int year = Integer.parseInt(birthDate.substring(0, 4));
            int month = Integer.parseInt(birthDate.substring(4, 6));
            int day = Integer.parseInt(birthDate.substring(6, 8));
            
            // 判断性别（奇数为男性，偶数为女性）
            int sequence = Integer.parseInt(sequenceCode);
            String gender = (sequence % 2 == 1) ? "男" : "女";
            
            // 计算年龄
            int currentYear = java.time.LocalDate.now().getYear();
            int age = currentYear - year;
            
            return IdCardInfo.builder()
                    .regionCode(regionCode)
                    .birthDate(birthDate)
                    .year(year)
                    .month(month)
                    .day(day)
                    .sequenceCode(sequenceCode)
                    .checkCode(String.valueOf(checkCode))
                    .gender(gender)
                    .age(age)
                    .build();
                    
        } catch (Exception e) {
            log.error("解析身份证信息失败: {}", idCard, e);
            return null;
        }
    }

    /**
     * 获取地区名称（简化版）
     */
    public static String getRegionName(String regionCode) {
        if (regionCode == null || regionCode.length() != 6) {
            return "未知地区";
        }
        
        // 这里可以集成完整的地区码数据库
        // 简化实现，只返回部分常见地区
        switch (regionCode.substring(0, 2)) {
            case "11": return "北京市";
            case "12": return "天津市";
            case "13": return "河北省";
            case "14": return "山西省";
            case "15": return "内蒙古自治区";
            case "21": return "辽宁省";
            case "22": return "吉林省";
            case "23": return "黑龙江省";
            case "31": return "上海市";
            case "32": return "江苏省";
            case "33": return "浙江省";
            case "34": return "安徽省";
            case "35": return "福建省";
            case "36": return "江西省";
            case "37": return "山东省";
            case "41": return "河南省";
            case "42": return "湖北省";
            case "43": return "湖南省";
            case "44": return "广东省";
            case "45": return "广西壮族自治区";
            case "46": return "海南省";
            case "50": return "重庆市";
            case "51": return "四川省";
            case "52": return "贵州省";
            case "53": return "云南省";
            case "54": return "西藏自治区";
            case "61": return "陕西省";
            case "62": return "甘肃省";
            case "63": return "青海省";
            case "64": return "宁夏回族自治区";
            case "65": return "新疆维吾尔自治区";
            case "71": return "台湾省";
            case "81": return "香港特别行政区";
            case "82": return "澳门特别行政区";
            default: return "未知地区";
        }
    }

    /**
     * 获取出生日期（格式化）
     */
    public static String getFormattedBirthDate(String idCard) {
        IdCardInfo info = getIdCardInfo(idCard);
        if (info != null) {
            return String.format("%d年%d月%d日", info.getYear(), info.getMonth(), info.getDay());
        }
        return null;
    }

    /**
     * 获取年龄
     */
    public static int getAge(String idCard) {
        IdCardInfo info = getIdCardInfo(idCard);
        return info != null ? info.getAge() : -1;
    }

    /**
     * 获取性别
     */
    public static String getGender(String idCard) {
        IdCardInfo info = getIdCardInfo(idCard);
        return info != null ? info.getGender() : null;
    }

    /**
     * 身份证信息内部类
     */
    public static class IdCardInfo {
        private String regionCode;
        private String birthDate;
        private int year;
        private int month;
        private int day;
        private String sequenceCode;
        private String checkCode;
        private String gender;
        private int age;

        // 使用Builder模式
        public static IdCardInfoBuilder builder() {
            return new IdCardInfoBuilder();
        }

        public static class IdCardInfoBuilder {
            private IdCardInfo info = new IdCardInfo();

            public IdCardInfoBuilder regionCode(String regionCode) {
                info.regionCode = regionCode;
                return this;
            }

            public IdCardInfoBuilder birthDate(String birthDate) {
                info.birthDate = birthDate;
                return this;
            }

            public IdCardInfoBuilder year(int year) {
                info.year = year;
                return this;
            }

            public IdCardInfoBuilder month(int month) {
                info.month = month;
                return this;
            }

            public IdCardInfoBuilder day(int day) {
                info.day = day;
                return this;
            }

            public IdCardInfoBuilder sequenceCode(String sequenceCode) {
                info.sequenceCode = sequenceCode;
                return this;
            }

            public IdCardInfoBuilder checkCode(String checkCode) {
                info.checkCode = checkCode;
                return this;
            }

            public IdCardInfoBuilder gender(String gender) {
                info.gender = gender;
                return this;
            }

            public IdCardInfoBuilder age(int age) {
                info.age = age;
                return this;
            }

            public IdCardInfo build() {
                return info;
            }
        }

        // Getters
        public String getRegionCode() { return regionCode; }
        public String getBirthDate() { return birthDate; }
        public int getYear() { return year; }
        public int getMonth() { return month; }
        public int getDay() { return day; }
        public String getSequenceCode() { return sequenceCode; }
        public String getCheckCode() { return checkCode; }
        public String getGender() { return gender; }
        public int getAge() { return age; }

        @Override
        public String toString() {
            return String.format("身份证信息{地区码='%s', 出生日期='%s', 性别='%s', 年龄=%d}", 
                    regionCode, birthDate, gender, age);
        }
    }
} 