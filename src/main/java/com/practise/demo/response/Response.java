package com.practise.demo.response;

import com.practise.demo.common.constant.ErrorCode;
import io.swagger.v3.oas.annotations.media.Schema;

/**
 * 通用返回结果类
 * 
 * @author lingwang
 * @date 2021/3/15 15:11
 */
@Schema(description = "统一响应结果")
public class Response<T> {
    
    @Schema(description = "响应消息")
    private String message;
    
    @Schema(description = "响应码")
    private int code;
    
    @Schema(description = "响应数据")
    private T data;

    public Response(String message, int code, T data) {
        this.message = message;
        this.code = code;
        this.data = data;
    }

    public Response(String message, int code) {
        this.message = message;
        this.code = code;
    }

    public Response(T data) {
        this.data = data;
        this.code = ErrorCode.SUCCESS.getCode();
        this.message = ErrorCode.SUCCESS.getMessage();
    }

    public Response() {
        this.code = ErrorCode.SUCCESS.getCode();
        this.message = ErrorCode.SUCCESS.getMessage();
    }

    /**
     * 成功响应
     */
    public static <T> Response<T> ok(T data) {
        return new Response<>(data);
    }
    
    /**
     * 成功响应（无数据）
     */
    public static <T> Response<T> ok() {
        Response<T> response = new Response<>();
        response.setCode(ErrorCode.SUCCESS.getCode());
        response.setMessage(ErrorCode.SUCCESS.getMessage());
        return response;
    }

    /**
     * 错误响应
     */
    public static <T> Response<T> error(int code, String message) {
        return new Response<>(message, code);
    }
    
    /**
     * 错误响应（使用错误码枚举）
     */
    public static <T> Response<T> error(ErrorCode errorCode) {
        return new Response<>(errorCode.getMessage(), errorCode.getCode());
    }
    
    /**
     * 错误响应（使用错误码枚举和自定义消息）
     */
    public static <T> Response<T> error(ErrorCode errorCode, String customMessage) {
        return new Response<>(customMessage, errorCode.getCode());
    }
    
    /**
     * 错误响应（使用错误码数字）
     */
    public static <T> Response<T> error(int code) {
        ErrorCode errorCode = ErrorCode.getByCode(code);
        return new Response<>(errorCode.getMessage(), errorCode.getCode());
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }
    
    // 兼容旧版本
    @Deprecated
    public T getDate() {
        return data;
    }

    @Deprecated
    public void setDate(T date) {
        this.data = date;
    }
}
