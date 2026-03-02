package com.practise.demo.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.practise.demo.model.entity.WorkTimeRecord;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.time.LocalDate;
import java.util.List;

/**
 * 上下班时间记录Mapper
 * 
 * @author system
 */
@Mapper
public interface WorkTimeRecordMapper extends BaseMapper<WorkTimeRecord> {
    
    /**
     * 查询指定日期范围内的记录
     * 
     * @param startDate 开始日期
     * @param endDate 结束日期
     * @return 记录列表
     */
    @Select("SELECT * FROM work_time_record WHERE work_date >= #{startDate} AND work_date <= #{endDate} AND deleted = 0 ORDER BY work_date DESC")
    List<WorkTimeRecord> selectByDateRange(@Param("startDate") LocalDate startDate, @Param("endDate") LocalDate endDate);
    
    /**
     * 计算平均工时
     * 
     * @return 总记录数和总工作时长（分钟）
     */
    @Select("SELECT COUNT(*) as total, " +
            "SUM(CASE WHEN is_workday = 1 THEN 1 ELSE 0 END) as workdayCount, " +
            "COALESCE(SUM(work_minutes), 0) as totalMinutes, " +
            "COALESCE(SUM(CASE WHEN is_workday = 1 THEN work_minutes ELSE 0 END), 0) as workdayMinutes " +
            "FROM work_time_record WHERE deleted = 0 AND work_minutes IS NOT NULL")
    WorkTimeStatistics selectWorkTimeStatistics();
    
    /**
     * 按日期范围和条件计算平均工时
     * 
     * @param startDate 开始日期
     * @param endDate 结束日期
     * @param onlyWorkday 是否只统计工作日
     * @return 统计结果
     */
    WorkTimeStatistics selectWorkTimeStatisticsByRange(@Param("startDate") LocalDate startDate, 
                                                       @Param("endDate") LocalDate endDate,
                                                       @Param("onlyWorkday") Boolean onlyWorkday);
    
    /**
     * 统计结果内部类
     */
    class WorkTimeStatistics {
        private Integer total;
        private Integer workdayCount;
        private Long totalMinutes;
        private Long workdayMinutes;
        
        public Integer getTotal() {
            return total;
        }
        
        public void setTotal(Integer total) {
            this.total = total;
        }
        
        public Integer getWorkdayCount() {
            return workdayCount;
        }
        
        public void setWorkdayCount(Integer workdayCount) {
            this.workdayCount = workdayCount;
        }
        
        public Long getTotalMinutes() {
            return totalMinutes;
        }
        
        public void setTotalMinutes(Long totalMinutes) {
            this.totalMinutes = totalMinutes;
        }
        
        public Long getWorkdayMinutes() {
            return workdayMinutes;
        }
        
        public void setWorkdayMinutes(Long workdayMinutes) {
            this.workdayMinutes = workdayMinutes;
        }
    }
}
