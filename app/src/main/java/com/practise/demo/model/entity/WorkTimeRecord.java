package com.practise.demo.model.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;

import java.time.LocalDate;
import java.time.LocalTime;

/**
 * 上下班时间记录实体
 * 
 * @author system
 */
@TableName("work_time_record")
public class WorkTimeRecord extends BaseEntity {
    
    private static final long serialVersionUID = 1L;
    
    /**
     * 日期
     */
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate workDate;
    
    /**
     * 上班时间
     */
    @JsonFormat(pattern = "HH:mm:ss")
    private LocalTime startTime;
    
    /**
     * 下班时间
     */
    @JsonFormat(pattern = "HH:mm:ss")
    private LocalTime endTime;
    
    /**
     * 是否工作日（1-工作日，0-节假日）
     */
    private Integer isWorkday;
    
    /**
     * 工作时长（分钟）
     */
    private Long workMinutes;
    
    /**
     * 备注
     */
    private String remark;
    
    public LocalDate getWorkDate() {
        return workDate;
    }
    
    public void setWorkDate(LocalDate workDate) {
        this.workDate = workDate;
    }
    
    public LocalTime getStartTime() {
        return startTime;
    }
    
    public void setStartTime(LocalTime startTime) {
        this.startTime = startTime;
    }
    
    public LocalTime getEndTime() {
        return endTime;
    }
    
    public void setEndTime(LocalTime endTime) {
        this.endTime = endTime;
    }
    
    public Integer getIsWorkday() {
        return isWorkday;
    }
    
    public void setIsWorkday(Integer isWorkday) {
        this.isWorkday = isWorkday;
    }
    
    public Long getWorkMinutes() {
        return workMinutes;
    }
    
    public void setWorkMinutes(Long workMinutes) {
        this.workMinutes = workMinutes;
    }
    
    public String getRemark() {
        return remark;
    }
    
    public void setRemark(String remark) {
        this.remark = remark;
    }
}
