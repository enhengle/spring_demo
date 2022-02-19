package com.practise.demo.response;

/**
 * @author lingwang
 * @date 2021/3/24 17:03
 * 异常抛出常量管理
 */
public enum EnumCode {
    Success(0, "success");

    EnumCode() {
    }

    EnumCode(int code, String msg) {
        this.code = code;
        this.msg = msg;
    }

    //    返回码
    private int code;
    //    返回错误信息
    private String msg;

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

}
