package com.practise.demo.common.exception;

import com.practise.demo.common.constant.ErrorCode;
import com.practise.demo.common.response.Response;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.validation.BindException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import java.util.Set;

/**
 * 全局异常处理器
 * 
 * @author system
 */
@RestControllerAdvice
public class GlobalExceptionHandler {
    
    private static final Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);
    
    /**
     * 处理业务异常
     */
    @ExceptionHandler(value = ServerException.class)
    @ResponseBody
    public Response<?> serverErrorHandler(ServerException e) {
        ErrorCode errorCode = e.getErrorCode() != null ? e.getErrorCode() : ErrorCode.BUSINESS_ERROR;
        String message = e.getErrorMessage() != null ? e.getErrorMessage() : e.getMessage();
        logger.error("业务异常: code={}, message={}", errorCode.getCode(), message, e);
        return Response.error(errorCode.getCode(), message);
    }
    
    /**
     * 处理参数校验异常（@Valid）
     */
    @ExceptionHandler(value = MethodArgumentNotValidException.class)
    @ResponseBody
    public Response<?> handleMethodArgumentNotValidException(MethodArgumentNotValidException e) {
        FieldError fieldError = e.getBindingResult().getFieldError();
        String message = fieldError != null ? fieldError.getDefaultMessage() : "参数校验失败";
        logger.error("参数校验异常: {}", message);
        return Response.error(ErrorCode.PARAM_ERROR.getCode(), message);
    }
    
    /**
     * 处理参数绑定异常
     */
    @ExceptionHandler(value = BindException.class)
    @ResponseBody
    public Response<?> handleBindException(BindException e) {
        FieldError fieldError = e.getBindingResult().getFieldError();
        String message = fieldError != null ? fieldError.getDefaultMessage() : "参数绑定失败";
        logger.error("参数绑定异常: {}", message);
        return Response.error(ErrorCode.PARAM_ERROR.getCode(), message);
    }
    
    /**
     * 处理约束校验异常
     */
    @ExceptionHandler(value = ConstraintViolationException.class)
    @ResponseBody
    public Response<?> handleConstraintViolationException(ConstraintViolationException e) {
        Set<ConstraintViolation<?>> violations = e.getConstraintViolations();
        String message = violations.iterator().hasNext() 
                ? violations.iterator().next().getMessage() 
                : "约束校验失败";
        logger.error("约束校验异常: {}", message);
        return Response.error(ErrorCode.PARAM_ERROR.getCode(), message);
    }
    
    /**
     * 处理其他异常
     */
    @ExceptionHandler(value = Exception.class)
    @ResponseBody
    public Response<?> errorHandler(Exception e) {
        logger.error("系统异常: {}", e.getMessage(), e);
        return Response.error(ErrorCode.SYSTEM_ERROR.getCode(), "系统异常，请联系管理员");
    }
}
