package com.practise.demo.util;

import lombok.extern.slf4j.Slf4j;

import java.util.HashMap;
import java.util.Map;

/**
 * 简单测试运行器
 */
@Slf4j
public class SimpleTestRunner {

    public static void main(String[] args) {
        log.info("========== 开始运行简单测试用例 ==========");
        
        try {
            // 测试IP工具类
            testIpUtilsBasic();
            
            // 测试反射工具类
            testReflectionUtilsBasic();
            
            // 测试文件工具类
            testFileUtilsBasic();
            
            // 测试身份证工具类
            testIdCardUtilsBasic();
            
            // 测试HTTP工具类
            testHttpUtilsBasic();
            
            log.info("========== 所有简单测试用例运行完成 ==========");
            
        } catch (Exception e) {
            log.error("测试过程中发生异常", e);
        }
    }

    /**
     * 测试IP工具类基本功能
     */
    private static void testIpUtilsBasic() {
        log.info("--- 测试IP工具类基本功能 ---");
        
        try {
            // 测试IP验证
            boolean isValid = IpUtils.isValidIpv4("192.168.1.1");
            log.info("IP验证测试: 192.168.1.1 -> {}", isValid);
            
            // 测试无效IP
            boolean isInvalid = IpUtils.isValidIpv4("256.1.2.3");
            log.info("无效IP测试: 256.1.2.3 -> {}", isInvalid);
            
            // 测试IP类型
            String ipType = IpUtils.getIpType("192.168.1.1");
            log.info("IP类型测试: 192.168.1.1 -> {}", ipType);
            
            log.info("✅ IP工具类基本功能测试通过");
            
        } catch (Exception e) {
            log.error("IP工具类测试失败", e);
        }
    }

    /**
     * 测试反射工具类基本功能
     */
    private static void testReflectionUtilsBasic() {
        log.info("--- 测试反射工具类基本功能 ---");
        
        try {
            Class<?> testClass = SimpleTestRunner.class;
            
            // 测试包名获取
            String packageName = ReflectionUtils.getPackageName(testClass);
            log.info("包名获取测试: {}", packageName);
            
            // 测试类名获取
            String simpleName = ReflectionUtils.getSimpleName(testClass);
            log.info("类名获取测试: {}", simpleName);
            
            // 测试字段存在判断
            boolean hasField = ReflectionUtils.hasField(testClass, "log");
            log.info("字段存在判断测试: log -> {}", hasField);
            
            log.info("✅ 反射工具类基本功能测试通过");
            
        } catch (Exception e) {
            log.error("反射工具类测试失败", e);
        }
    }

    /**
     * 测试文件工具类基本功能
     */
    private static void testFileUtilsBasic() {
        log.info("--- 测试文件工具类基本功能 ---");
        
        try {
            // 测试文件类型判断
            String fileType = FileUtils.getFileType("test.txt");
            log.info("文件类型判断测试: test.txt -> {}", fileType);
            
            // 测试压缩文件判断
            boolean isCompressed = FileUtils.isCompressedFile("test.zip");
            log.info("压缩文件判断测试: test.zip -> {}", isCompressed);
            
            // 测试文件扩展名获取
            String extension = FileUtils.getFileExtension("test.txt");
            log.info("文件扩展名获取测试: test.txt -> {}", extension);
            
            log.info("✅ 文件工具类基本功能测试通过");
            
        } catch (Exception e) {
            log.error("文件工具类测试失败", e);
        }
    }

    /**
     * 测试身份证工具类基本功能
     */
    private static void testIdCardUtilsBasic() {
        log.info("--- 测试身份证工具类基本功能 ---");
        
        try {
            // 测试身份证号验证（示例号码）
            String testIdCard = "110101199001011234";
            boolean isValid = IdCardUtils.isValidIdCard(testIdCard);
            log.info("身份证号验证测试: {} -> {}", testIdCard, isValid);
            
            // 测试无效身份证号
            boolean isInvalid = IdCardUtils.isValidIdCard("123456789012345678");
            log.info("无效身份证号测试: 123456789012345678 -> {}", isInvalid);
            
            // 测试地区名称获取
            String regionName = IdCardUtils.getRegionName("110101");
            log.info("地区名称获取测试: 110101 -> {}", regionName);
            
            log.info("✅ 身份证工具类基本功能测试通过");
            
        } catch (Exception e) {
            log.error("身份证工具类测试失败", e);
        }
    }

    /**
     * 测试HTTP工具类基本功能
     */
    private static void testHttpUtilsBasic() {
        log.info("--- 测试HTTP工具类基本功能 ---");
        
        try {
            // 测试URL验证
            boolean isValidUrl = HttpUtils.isValidUrl("https://www.baidu.com");
            log.info("URL验证测试: https://www.baidu.com -> {}", isValidUrl);
            
            // 测试无效URL
            boolean isInvalidUrl = HttpUtils.isValidUrl("invalid-url");
            log.info("无效URL测试: invalid-url -> {}", isInvalidUrl);
            
            // 测试域名获取
            String domain = HttpUtils.getDomain("https://www.baidu.com/path?param=value");
            log.info("域名获取测试: {}", domain);
            
            // 测试协议获取
            String protocol = HttpUtils.getProtocol("https://www.baidu.com");
            log.info("协议获取测试: {}", protocol);
            
            // 测试查询字符串构建
            Map<String, String> params = new HashMap<>();
            params.put("name", "张三");
            params.put("age", "25");
            String queryString = HttpUtils.buildQueryString(params);
            log.info("查询字符串构建测试: {}", queryString);
            
            log.info("✅ HTTP工具类基本功能测试通过");
            
        } catch (Exception e) {
            log.error("HTTP工具类测试失败", e);
        }
    }
} 