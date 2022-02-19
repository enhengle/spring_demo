package com.practise.demo.response;

/**
 * @author lingwang
 * @date 2021/3/15 15:11
 * 设置通用返回接口
 */
public class Response<T> {
    private String message;
    private int code;
    private T date;

    public Response(String message, int code, T date) {
        this.message = message;
        this.code = code;
        this.date = date;
    }

    public Response(String message, int code) {
        this.message = message;
        this.code = code;
    }

    public Response(T date) {
        this.date = date;
    }

    public Response() {
    }

    public static <T> Response<T> ok(T date) {
        return new Response<>(date);
    }

    public static <T> Response<T> error(int code, String message) {
        return new Response<T>(message, code);
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

    public T getDate() {
        return date;
    }

    public void setDate(T date) {
        this.date = date;
    }
}
