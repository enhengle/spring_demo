package com.practise.demo.model.vo;

import java.math.BigDecimal;

/**
 * 平均工时视图对象
 * 
 * @author system
 */
public class AverageWorkTimeVO {
    
    /**
     * 总记录数
     */
    private Integer totalRecords;
    
    /**
     * 工作日记录数
     */
    private Integer workdayRecords;
    
    /**
     * 总工作时长（小时）
     */
    private BigDecimal totalHours;
    
    /**
     * 工作日总工作时长（小时）
     */
    private BigDecimal workdayTotalHours;
    
    /**
     * 剔除加班后的总工作时长（小时，按8小时标准）
     */
    private BigDecimal normalTotalHours;
    
    /**
     * 平均工作时长（小时）
     */
    private BigDecimal averageHours;
    
    /**
     * 工作日平均工作时长（小时）
     */
    private BigDecimal workdayAverageHours;
    
    /**
     * 剔除加班后的平均工作时长（小时）
     */
    private BigDecimal normalAverageHours;
    
    /**
     * 平均工作时长（分钟）
     */
    private Long averageMinutes;
    
    /**
     * 工作日平均工作时长（分钟）
     */
    private Long workdayAverageMinutes;
    
    /**
     * 剔除加班后的平均工作时长（分钟）
     */
    private Long normalAverageMinutes;
    
    /**
     * 加班总时长（小时）
     */
    private BigDecimal overtimeHours;
    
    /**
     * 统计时间范围描述
     */
    private String timeRange;
    
    public Integer getTotalRecords() {
        return totalRecords;
    }
    
    public void setTotalRecords(Integer totalRecords) {
        this.totalRecords = totalRecords;
    }
    
    public Integer getWorkdayRecords() {
        return workdayRecords;
    }
    
    public void setWorkdayRecords(Integer workdayRecords) {
        this.workdayRecords = workdayRecords;
    }
    
    public BigDecimal getTotalHours() {
        return totalHours;
    }
    
    public void setTotalHours(BigDecimal totalHours) {
        this.totalHours = totalHours;
    }
    
    public BigDecimal getWorkdayTotalHours() {
        return workdayTotalHours;
    }
    
    public void setWorkdayTotalHours(BigDecimal workdayTotalHours) {
        this.workdayTotalHours = workdayTotalHours;
    }
    
    public BigDecimal getNormalTotalHours() {
        return normalTotalHours;
    }
    
    public void setNormalTotalHours(BigDecimal normalTotalHours) {
        this.normalTotalHours = normalTotalHours;
    }
    
    public BigDecimal getAverageHours() {
        return averageHours;
    }
    
    public void setAverageHours(BigDecimal averageHours) {
        this.averageHours = averageHours;
    }
    
    public BigDecimal getWorkdayAverageHours() {
        return workdayAverageHours;
    }
    
    public void setWorkdayAverageHours(BigDecimal workdayAverageHours) {
        this.workdayAverageHours = workdayAverageHours;
    }
    
    public BigDecimal getNormalAverageHours() {
        return normalAverageHours;
    }
    
    public void setNormalAverageHours(BigDecimal normalAverageHours) {
        this.normalAverageHours = normalAverageHours;
    }
    
    public Long getAverageMinutes() {
        return averageMinutes;
    }
    
    public void setAverageMinutes(Long averageMinutes) {
        this.averageMinutes = averageMinutes;
    }
    
    public Long getWorkdayAverageMinutes() {
        return workdayAverageMinutes;
    }
    
    public void setWorkdayAverageMinutes(Long workdayAverageMinutes) {
        this.workdayAverageMinutes = workdayAverageMinutes;
    }
    
    public Long getNormalAverageMinutes() {
        return normalAverageMinutes;
    }
    
    public void setNormalAverageMinutes(Long normalAverageMinutes) {
        this.normalAverageMinutes = normalAverageMinutes;
    }
    
    public BigDecimal getOvertimeHours() {
        return overtimeHours;
    }
    
    public void setOvertimeHours(BigDecimal overtimeHours) {
        this.overtimeHours = overtimeHours;
    }
    
    public String getTimeRange() {
        return timeRange;
    }
    
    public void setTimeRange(String timeRange) {
        this.timeRange = timeRange;
    }
}
