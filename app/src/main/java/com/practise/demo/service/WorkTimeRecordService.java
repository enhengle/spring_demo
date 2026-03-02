package com.practise.demo.service;

import com.practise.demo.model.dto.WorkTimeRecordDTO;
import com.practise.demo.model.entity.WorkTimeRecord;
import com.practise.demo.model.vo.AverageWorkTimeVO;

import java.time.LocalDate;
import java.util.List;

/**
 * 上下班时间记录Service接口
 * 
 * @author system
 */
public interface WorkTimeRecordService {
    
    /**
     * 保存或更新上下班时间记录
     * 
     * @param dto 记录DTO
     * @return 保存后的记录
     */
    WorkTimeRecord saveOrUpdate(WorkTimeRecordDTO dto);
    
    /**
     * 根据ID查询记录
     * 
     * @param id 记录ID
     * @return 记录
     */
    WorkTimeRecord getById(Long id);
    
    /**
     * 查询所有记录
     * 
     * @return 记录列表
     */
    List<WorkTimeRecord> listAll();
    
    /**
     * 根据日期范围查询记录
     * 
     * @param startDate 开始日期
     * @param endDate 结束日期
     * @return 记录列表
     */
    List<WorkTimeRecord> listByDateRange(LocalDate startDate, LocalDate endDate);
    
    /**
     * 删除记录
     * 
     * @param id 记录ID
     */
    void deleteById(Long id);
    
    /**
     * 计算平均工时
     * 
     * @return 平均工时信息
     */
    AverageWorkTimeVO calculateAverageWorkTime();
    
    /**
     * 按条件计算平均工时
     * 
     * @param startDate 开始日期（可选）
     * @param endDate 结束日期（可选）
     * @param onlyWorkday 是否只统计工作日（可选）
     * @param excludeOvertime 是否剔除加班（超过8小时的部分，可选）
     * @param timeRangeType 时间范围类型（week-周，month-月，year-年，custom-自定义）
     * @param timeRangeValue 时间范围值（周：yyyy-Www，月：yyyy-MM，年：yyyy，自定义：null）
     * @return 平均工时信息
     */
    AverageWorkTimeVO calculateAverageWorkTime(LocalDate startDate, LocalDate endDate, 
                                               Boolean onlyWorkday, Boolean excludeOvertime,
                                               String timeRangeType, String timeRangeValue);
    
    /**
     * 批量导入记录
     * 
     * @param importList 导入记录列表
     * @return 导入结果
     */
    com.practise.demo.model.vo.BatchImportResultVO batchImport(List<com.practise.demo.model.dto.BatchImportDTO> importList);
}
