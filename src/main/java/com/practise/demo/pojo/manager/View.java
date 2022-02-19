package com.practise.demo.pojo.manager;

import com.baomidou.mybatisplus.annotation.TableName;

//此处table_name请置换成表名
@TableName(value = "table_name")
public class View {
    private Long id;
    private String appId;
    private Integer state;
    private String createdAt;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getAppId() {
        return appId;
    }

    public void setAppId(String appId) {
        this.appId = appId;
    }

    public Integer getState() {
        return state;
    }

    public void setState(Integer state) {
        this.state = state;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }
}
