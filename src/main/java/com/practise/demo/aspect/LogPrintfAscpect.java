package com.practise.demo.aspect;

import com.alibaba.fastjson.JSONObject;
import com.practise.demo.common.LogPrint;
import com.practise.demo.response.EnumCode;
import com.practise.demo.response.Response;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.Arrays;

/**
 * @author lingwang
 * @date 2022/2/19 16:37
 * 切面应用：打印日志
 */
@Aspect
@Component
public class LogPrintfAscpect {

    private final static Logger logger = LoggerFactory.getLogger(LogPrintfAscpect.class);


    /*
     * 切面打印日志
     * 可定义一个类，此处没有定义
     * */
    @Around("@annotation(logPrint)")
    public Object doLog(ProceedingJoinPoint joinPoint, LogPrint logPrint) throws Throwable {
        //获取开始时间
        long begin = System.currentTimeMillis();
        Object result;
        try {
            result = joinPoint.proceed();
        } catch (Exception e) {
            logger.error("接口调用出错，错误信息为：", e);
            result = Response.error(EnumCode.SYSTEM_ERROR);
        }
        //获取请求参数
        Object[] args = joinPoint.getArgs();
        JSONObject resultJson = JSONObject.parseObject(JSONObject.toJSONString(result));

        //获取返回码和返回信息
        Integer code = resultJson.getInteger("code");
        String msg = resultJson.getString("msg");

        // 打印错误结果，error日志与info日志分离，如果不分离，可省略此步
        if (code != null && code != EnumCode.SUCCESS.getCode()) {
            logger.error("code:{},msg:{}", code, msg);
        }
        //获取结束时间，同于计算接口处理时间
        long end = System.currentTimeMillis();
        logger.info("请求参数：{},返回码：{}，返回信息：{}，处理时间：{}", Arrays.toString(args), code, msg, end - begin);

        return result;
    }

}
