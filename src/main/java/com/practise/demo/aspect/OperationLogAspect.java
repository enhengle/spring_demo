package com.practise.demo.aspect;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.practise.demo.common.annotation.OperationLog;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.lang.reflect.Method;
import java.util.Arrays;

/**
 * 操作日志AOP切面
 * 
 * @author system
 * @date 2024
 */
@Aspect
@Component
public class OperationLogAspect {
    
    private static final Logger logger = LoggerFactory.getLogger(OperationLogAspect.class);
    
    private final ObjectMapper objectMapper = new ObjectMapper();
    
    @Pointcut("@annotation(com.practise.demo.common.annotation.OperationLog)")
    public void operationLogPointcut() {
    }
    
    @Around("operationLogPointcut()")
    public Object around(ProceedingJoinPoint joinPoint) throws Throwable {
        long startTime = System.currentTimeMillis();
        
        // 获取请求信息
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        HttpServletRequest request = attributes != null ? attributes.getRequest() : null;
        
        // 获取方法签名和注解
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        Method method = signature.getMethod();
        OperationLog operationLog = method.getAnnotation(OperationLog.class);
        
        // 获取方法信息
        String className = joinPoint.getTarget().getClass().getName();
        String methodName = method.getName();
        String operationType = operationLog.type().isEmpty() ? methodName : operationLog.type();
        String operationDesc = operationLog.value().isEmpty() ? methodName : operationLog.value();
        
        // 获取请求参数
        Object[] args = joinPoint.getArgs();
        String params = "";
        if (operationLog.recordParams() && args != null && args.length > 0) {
            try {
                params = objectMapper.writeValueAsString(Arrays.asList(args));
            } catch (Exception e) {
                params = Arrays.toString(args);
            }
        }
        
        // 获取请求信息
        String ip = request != null ? getIpAddress(request) : "unknown";
        String url = request != null ? request.getRequestURL().toString() : "unknown";
        String httpMethod = request != null ? request.getMethod() : "unknown";
        
        // 记录开始日志
        logger.info("操作日志 - 开始执行 | 操作类型: {} | 操作描述: {} | 类名: {} | 方法名: {} | IP: {} | URL: {} | 请求方式: {} | 参数: {}",
                operationType, operationDesc, className, methodName, ip, url, httpMethod, params);
        
        Object result = null;
        String resultStr = "";
        Exception exception = null;
        
        try {
            // 执行方法
            result = joinPoint.proceed();
            
            // 记录返回结果
            if (operationLog.recordResult() && result != null) {
                try {
                    resultStr = objectMapper.writeValueAsString(result);
                } catch (Exception e) {
                    resultStr = result.toString();
                }
            }
            
            long endTime = System.currentTimeMillis();
            long duration = endTime - startTime;
            
            // 记录成功日志
            logger.info("操作日志 - 执行成功 | 操作类型: {} | 操作描述: {} | 耗时: {}ms | 返回结果: {}",
                    operationType, operationDesc, duration, resultStr);
            
            return result;
            
        } catch (Exception e) {
            exception = e;
            long endTime = System.currentTimeMillis();
            long duration = endTime - startTime;
            
            // 记录失败日志
            logger.error("操作日志 - 执行失败 | 操作类型: {} | 操作描述: {} | 耗时: {}ms | 异常信息: {}",
                    operationType, operationDesc, duration, e.getMessage(), e);
            
            throw e;
        }
    }
    
    /**
     * 获取客户端IP地址
     */
    private String getIpAddress(HttpServletRequest request) {
        String ip = request.getHeader("X-Forwarded-For");
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("Proxy-Client-IP");
        }
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("WL-Proxy-Client-IP");
        }
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("HTTP_CLIENT_IP");
        }
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("HTTP_X_FORWARDED_FOR");
        }
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getRemoteAddr();
        }
        // 处理多个IP的情况
        if (ip != null && ip.contains(",")) {
            ip = ip.split(",")[0].trim();
        }
        return ip;
    }
}

