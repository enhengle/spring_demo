package com.practise.demo.model.dto;

import com.fasterxml.jackson.annotation.JsonFormat;

import javax.validation.constraints.NotNull;
import java.time.LocalDate;
import java.time.LocalTime;

/**
 * 上下班时间记录DTO
 * 
 * @author system
 */
public class WorkTimeRecordDTO {
    
    private Long id;
    
    @NotNull(message = "日期不能为空")
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate workDate;
    
    @NotNull(message = "上班时间不能为空")
    @JsonFormat(pattern = "HH:mm:ss")
    private LocalTime startTime;
    
    @JsonFormat(pattern = "HH:mm:ss")
    private LocalTime endTime;
    
    private String remark;
    
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
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
    
    public String getRemark() {
        return remark;
    }
    
    public void setRemark(String remark) {
        this.remark = remark;
    }
}
