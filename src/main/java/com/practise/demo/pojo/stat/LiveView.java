package com.practise.demo.pojo.stat;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonInclude;

//此处table_name请置换成表名
@TableName(value = "table_name")
public class LiveView {
    @TableField(value = "id")
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String id;
    @TableField(value = "day_date")
    private String dayDate;
    @TableField(value = "live_user")
    private String liveUser;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    @TableField(value = "created_at")
    private String createdAt;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getDayDate() {
        return dayDate;
    }

    public void setDayDate(String dayDate) {
        this.dayDate = dayDate;
    }

    public String getLiveUser() {
        return liveUser;
    }

    public void setLiveUser(String liveUser) {
        this.liveUser = liveUser;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

}
