package com.practise.demo.model.dto;

import com.fasterxml.jackson.annotation.JsonFormat;

import java.time.LocalDate;
import java.time.LocalTime;

/**
 * 批量导入记录DTO
 * 
 * @author system
 */
public class BatchImportDTO {
    
    /**
     * 日期（格式：yyyy/MM/dd 或 yyyy-MM-dd）
     */
    private String date;
    
    /**
     * 星期（可选，用于显示）
     */
    private String weekday;
    
    /**
     * 上班时间（格式：HH:mm:ss）
     */
    private String startTime;
    
    /**
     * 下班时间（格式：HH:mm:ss）
     */
    private String endTime;
    
    /**
     * 备注
     */
    private String remark;
    
    public String getDate() {
        return date;
    }
    
    public void setDate(String date) {
        this.date = date;
    }
    
    public String getWeekday() {
        return weekday;
    }
    
    public void setWeekday(String weekday) {
        this.weekday = weekday;
    }
    
    public String getStartTime() {
        return startTime;
    }
    
    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }
    
    public String getEndTime() {
        return endTime;
    }
    
    public void setEndTime(String endTime) {
        this.endTime = endTime;
    }
    
    public String getRemark() {
        return remark;
    }
    
    public void setRemark(String remark) {
        this.remark = remark;
    }
    
    /**
     * 转换为WorkTimeRecordDTO
     */
    public WorkTimeRecordDTO toWorkTimeRecordDTO() {
        WorkTimeRecordDTO dto = new WorkTimeRecordDTO();
        
        // 解析日期（支持 yyyy/MM/dd 和 yyyy-MM-dd 格式）
        if (date != null && !date.trim().isEmpty()) {
            String dateStr = date.trim();
            try {
                if (dateStr.contains("/")) {
                    String[] parts = dateStr.split("/");
                    if (parts.length == 3) {
                        dto.setWorkDate(LocalDate.of(
                            Integer.parseInt(parts[0].trim()),
                            Integer.parseInt(parts[1].trim()),
                            Integer.parseInt(parts[2].trim())
                        ));
                    }
                } else if (dateStr.contains("-")) {
                    dto.setWorkDate(LocalDate.parse(dateStr));
                }
            } catch (Exception e) {
                // 日期解析失败，返回null
                return null;
            }
        }
        
        // 解析上班时间
        if (startTime != null && !startTime.trim().isEmpty()) {
            try {
                String timeStr = startTime.trim();
                String[] timeParts = timeStr.split(":");
                if (timeParts.length >= 2) {
                    int hour = Integer.parseInt(timeParts[0].trim());
                    int minute = Integer.parseInt(timeParts[1].trim());
                    int second = timeParts.length >= 3 ? Integer.parseInt(timeParts[2].trim()) : 0;
                    dto.setStartTime(LocalTime.of(hour, minute, second));
                }
            } catch (Exception e) {
                // 时间解析失败，不设置
            }
        }
        
        // 解析下班时间
        if (endTime != null && !endTime.trim().isEmpty()) {
            try {
                String timeStr = endTime.trim();
                String[] timeParts = timeStr.split(":");
                if (timeParts.length >= 2) {
                    int hour = Integer.parseInt(timeParts[0].trim());
                    int minute = Integer.parseInt(timeParts[1].trim());
                    int second = timeParts.length >= 3 ? Integer.parseInt(timeParts[2].trim()) : 0;
                    dto.setEndTime(LocalTime.of(hour, minute, second));
                }
            } catch (Exception e) {
                // 时间解析失败，不设置
            }
        }
        
        dto.setRemark(remark);
        
        return dto;
    }
}
