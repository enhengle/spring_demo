package com.practise.demo.common.response;

import java.io.Serializable;

/**
 * 统一响应类
 * 
 * @author system
 */
public class Response<T> implements Serializable {
    
    private static final long serialVersionUID = 1L;
    
    private Integer code;
    private String message;
    private T data;
    
    public Response() {
    }
    
    public Response(Integer code, String message, T data) {
        this.code = code;
        this.message = message;
        this.data = data;
    }
    
    public static <T> Response<T> ok() {
        return new Response<>(200, "操作成功", null);
    }
    
    public static <T> Response<T> ok(T data) {
        return new Response<>(200, "操作成功", data);
    }
    
    public static <T> Response<T> ok(String message, T data) {
        return new Response<>(200, message, data);
    }
    
    public static <T> Response<T> error(Integer code, String message) {
        return new Response<>(code, message, null);
    }
    
    public static <T> Response<T> error(String message) {
        return new Response<>(500, message, null);
    }
    
    public Integer getCode() {
        return code;
    }
    
    public void setCode(Integer code) {
        this.code = code;
    }
    
    public String getMessage() {
        return message;
    }
    
    public void setMessage(String message) {
        this.message = message;
    }
    
    public T getData() {
        return data;
    }
    
    public void setData(T data) {
        this.data = data;
    }
}
