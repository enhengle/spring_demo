package com.practise.demo.exception;

import com.practise.demo.common.constant.ErrorCode;

/**
 * 业务异常类
 * 支持直接使用错误码枚举构造异常
 * 
 * @author lingwang
 * @date 2021/3/15 15:15
 */
public class ServerException extends RuntimeException {
    
    /**
     * 错误码
     */
    private ErrorCode errorCode;
    
    /**
     * 错误码对应的错误信息
     */
    private String errorMessage;
    
    /**
     * 使用错误码枚举构造异常（推荐使用）
     */
    public ServerException(ErrorCode errorCode) {
        super(errorCode.getMessage());
        this.errorCode = errorCode;
        this.errorMessage = errorCode.getMessage();
    }
    
    /**
     * 使用错误码枚举和自定义消息构造异常
     */
    public ServerException(ErrorCode errorCode, String customMessage) {
        super(customMessage);
        this.errorCode = errorCode;
        this.errorMessage = customMessage;
    }
    
    /**
     * 使用错误码枚举、自定义消息和原因构造异常
     */
    public ServerException(ErrorCode errorCode, String customMessage, Throwable cause) {
        super(customMessage, cause);
        this.errorCode = errorCode;
        this.errorMessage = customMessage;
    }
    
    /**
     * 使用错误码枚举和原因构造异常
     */
    public ServerException(ErrorCode errorCode, Throwable cause) {
        super(errorCode.getMessage(), cause);
        this.errorCode = errorCode;
        this.errorMessage = errorCode.getMessage();
    }
    
    // 保留原有构造方法以兼容旧代码
    public ServerException() {
        super();
    }

    public ServerException(String message) {
        super(message);
        this.errorCode = ErrorCode.SYSTEM_ERROR;
        this.errorMessage = message;
    }

    public ServerException(String message, Throwable cause) {
        super(message, cause);
        this.errorCode = ErrorCode.SYSTEM_ERROR;
        this.errorMessage = message;
    }

    public ServerException(Throwable cause) {
        super(cause);
        this.errorCode = ErrorCode.SYSTEM_ERROR;
        this.errorMessage = cause != null ? cause.getMessage() : ErrorCode.SYSTEM_ERROR.getMessage();
    }

    public ServerException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
        this.errorCode = ErrorCode.SYSTEM_ERROR;
        this.errorMessage = message;
    }
    
    public ErrorCode getErrorCode() {
        return errorCode;
    }
    
    public String getErrorMessage() {
        return errorMessage;
    }
    
    public int getCode() {
        return errorCode != null ? errorCode.getCode() : ErrorCode.SYSTEM_ERROR.getCode();
    }
}

