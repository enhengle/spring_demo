package com.practise.demo.common.exception;

import com.practise.demo.common.constant.ErrorCode;

/**
 * 业务异常
 * 
 * @author system
 */
public class ServerException extends RuntimeException {
    
    private static final long serialVersionUID = 1L;
    
    private ErrorCode errorCode;
    private String errorMessage;
    
    public ServerException(ErrorCode errorCode) {
        super(errorCode.getMessage());
        this.errorCode = errorCode;
        this.errorMessage = errorCode.getMessage();
    }
    
    public ServerException(ErrorCode errorCode, String errorMessage) {
        super(errorMessage);
        this.errorCode = errorCode;
        this.errorMessage = errorMessage;
    }
    
    public ServerException(String message) {
        super(message);
        this.errorCode = ErrorCode.BUSINESS_ERROR;
        this.errorMessage = message;
    }
    
    public ErrorCode getErrorCode() {
        return errorCode;
    }
    
    public void setErrorCode(ErrorCode errorCode) {
        this.errorCode = errorCode;
    }
    
    public String getErrorMessage() {
        return errorMessage;
    }
    
    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }
}
