package com.practise.demo.producer;

import java.time.LocalDateTime;

/**
 * @author lingwang
 * @date 2021/6/29 14:55
 */
public class Message {
    private String id;
    private String msg;
    private LocalDateTime sendTime;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public LocalDateTime getSendTime() {
        return sendTime;
    }

    public void setSendTime(LocalDateTime sendTime) {
        this.sendTime = sendTime;
    }

    @Override
    public String toString() {
        return "Message{" +
                "id='" + id + '\'' +
                ", msg='" + msg + '\'' +
                ", sendTime=" + sendTime +
                '}';
    }

    public Message() {
    }
}
