package com.practise.demo.common.constant;

/**
 * 错误码枚举类
 * 格式：（错误码数字，错误码内容）
 * 
 * @author system
 * @date 2024
 */
public enum ErrorCode {
    
    /**
     * 成功
     */
    SUCCESS(0, "操作成功"),
    
    /**
     * 系统错误码 1000-1999
     */
    SYSTEM_ERROR(1000, "系统错误"),
    PARAM_ERROR(1001, "参数错误"),
    DATA_NOT_FOUND(1002, "数据不存在"),
    DATA_ALREADY_EXISTS(1003, "数据已存在"),
    OPERATION_FAILED(1004, "操作失败"),
    
    /**
     * 业务错误码 2000-2999
     */
    BUSINESS_ERROR(2000, "业务处理失败"),
    
    /**
     * 权限错误码 3000-3999
     */
    UNAUTHORIZED(3000, "未授权"),
    FORBIDDEN(3001, "禁止访问");
    
    /**
     * 错误码
     */
    private final int code;
    
    /**
     * 错误信息
     */
    private final String message;
    
    ErrorCode(int code, String message) {
        this.code = code;
        this.message = message;
    }
    
    public int getCode() {
        return code;
    }
    
    public String getMessage() {
        return message;
    }
    
    /**
     * 根据错误码获取枚举
     */
    public static ErrorCode getByCode(int code) {
        for (ErrorCode errorCode : values()) {
            if (errorCode.getCode() == code) {
                return errorCode;
            }
        }
        return SYSTEM_ERROR; // 默认返回系统错误
    }
}
