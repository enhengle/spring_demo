package com.practise.demo.myExceptionHandler;

import com.practise.demo.response.Response;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * @author lingwang
 * @date 2021/3/15 15:17
 * 全局异常处理
 */
@RestControllerAdvice
public class ServerExceptionHandler {
    //日志打印
    private static Logger logger = LoggerFactory.getLogger(ServerExceptionHandler.class);

    @ExceptionHandler(value = ServerException.class)
    @ResponseBody
    public Response serverErrorHandler(ServerException e) {
        logger.error(e.getMessage(), e);
        return Response.error(0, e.getMessage());
    }

    @ExceptionHandler(value = Exception.class)
    @ResponseBody
    public Response errorHanlder(Exception e) {
        logger.error(e.getMessage(), e);
        return Response.error(0, e.getMessage());
    }
}
