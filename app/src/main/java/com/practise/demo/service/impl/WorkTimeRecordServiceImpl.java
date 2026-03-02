package com.practise.demo.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.practise.demo.mapper.WorkTimeRecordMapper;
import com.practise.demo.model.dto.BatchImportDTO;
import com.practise.demo.model.dto.WorkTimeRecordDTO;
import com.practise.demo.model.entity.WorkTimeRecord;
import com.practise.demo.model.vo.AverageWorkTimeVO;
import com.practise.demo.model.vo.BatchImportResultVO;
import com.practise.demo.service.WorkTimeRecordService;
import com.practise.demo.util.ChineseCalendarUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.time.temporal.WeekFields;
import java.util.List;
import java.util.Locale;

/**
 * 上下班时间记录Service实现类
 * 
 * @author system
 */
@Service
public class WorkTimeRecordServiceImpl implements WorkTimeRecordService {
    
    private static final Logger logger = LoggerFactory.getLogger(WorkTimeRecordServiceImpl.class);
    
    @Autowired
    private WorkTimeRecordMapper workTimeRecordMapper;
    
    @Override
    @Transactional(rollbackFor = Exception.class)
    public WorkTimeRecord saveOrUpdate(WorkTimeRecordDTO dto) {
        WorkTimeRecord record;
        
        if (dto.getId() != null) {
            // 更新
            record = workTimeRecordMapper.selectById(dto.getId());
            if (record == null) {
                throw new RuntimeException("记录不存在");
            }
        } else {
            // 新增
            record = new WorkTimeRecord();
            record.setCreateTime(LocalDateTime.now());
        }
        
        // 复制属性
        BeanUtils.copyProperties(dto, record);
        
        // 判断是否为工作日
        boolean isWorkday = ChineseCalendarUtil.isWorkday(dto.getWorkDate());
        record.setIsWorkday(isWorkday ? 1 : 0);
        
        // 计算工作时长
        if (dto.getStartTime() != null && dto.getEndTime() != null) {
            LocalTime startTime = dto.getStartTime();
            LocalTime endTime = dto.getEndTime();
            
            // 如果下班时间小于上班时间，说明跨天了
            if (endTime.isBefore(startTime)) {
                // 跨天处理：下班时间+24小时
                Duration duration = Duration.between(startTime, endTime.plusHours(24));
                record.setWorkMinutes(duration.toMinutes());
            } else {
                Duration duration = Duration.between(startTime, endTime);
                record.setWorkMinutes(duration.toMinutes());
            }
        } else {
            record.setWorkMinutes(null);
        }
        
        record.setUpdateTime(LocalDateTime.now());
        
        if (dto.getId() != null) {
            workTimeRecordMapper.updateById(record);
        } else {
            workTimeRecordMapper.insert(record);
        }
        
        return record;
    }
    
    @Override
    public WorkTimeRecord getById(Long id) {
        return workTimeRecordMapper.selectById(id);
    }
    
    @Override
    public List<WorkTimeRecord> listAll() {
        LambdaQueryWrapper<WorkTimeRecord> wrapper = new LambdaQueryWrapper<>();
        wrapper.orderByDesc(WorkTimeRecord::getWorkDate);
        return workTimeRecordMapper.selectList(wrapper);
    }
    
    @Override
    public List<WorkTimeRecord> listByDateRange(LocalDate startDate, LocalDate endDate) {
        return workTimeRecordMapper.selectByDateRange(startDate, endDate);
    }
    
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteById(Long id) {
        workTimeRecordMapper.deleteById(id);
    }
    
    @Override
    public AverageWorkTimeVO calculateAverageWorkTime() {
        WorkTimeRecordMapper.WorkTimeStatistics statistics = workTimeRecordMapper.selectWorkTimeStatistics();
        
        AverageWorkTimeVO vo = new AverageWorkTimeVO();
        vo.setTimeRange("全部数据");
        
        if (statistics != null && statistics.getTotal() != null && statistics.getTotal() > 0) {
            vo.setTotalRecords(statistics.getTotal());
            vo.setWorkdayRecords(statistics.getWorkdayCount() != null ? statistics.getWorkdayCount() : 0);
            
            Long totalMinutes = statistics.getTotalMinutes() != null ? statistics.getTotalMinutes() : 0L;
            Long workdayMinutes = statistics.getWorkdayMinutes() != null ? statistics.getWorkdayMinutes() : 0L;
            
            if (totalMinutes > 0) {
                // 总工作时长（小时）
                BigDecimal totalHours = BigDecimal.valueOf(totalMinutes)
                        .divide(BigDecimal.valueOf(60), 2, RoundingMode.HALF_UP);
                vo.setTotalHours(totalHours);
                
                // 工作日总工时
                BigDecimal workdayTotalHours = BigDecimal.valueOf(workdayMinutes)
                        .divide(BigDecimal.valueOf(60), 2, RoundingMode.HALF_UP);
                vo.setWorkdayTotalHours(workdayTotalHours);
                
                // 平均工作时长（小时）
                BigDecimal averageHours = totalHours.divide(
                        BigDecimal.valueOf(vo.getTotalRecords()), 2, RoundingMode.HALF_UP);
                vo.setAverageHours(averageHours);
                
                // 平均工作时长（分钟）
                vo.setAverageMinutes(totalMinutes / vo.getTotalRecords());
                
                // 工作日平均工时
                if (vo.getWorkdayRecords() > 0) {
                    BigDecimal workdayAverageHours = workdayTotalHours.divide(
                            BigDecimal.valueOf(vo.getWorkdayRecords()), 2, RoundingMode.HALF_UP);
                    vo.setWorkdayAverageHours(workdayAverageHours);
                    vo.setWorkdayAverageMinutes(workdayMinutes / vo.getWorkdayRecords());
                } else {
                    vo.setWorkdayAverageHours(BigDecimal.ZERO);
                    vo.setWorkdayAverageMinutes(0L);
                }
            } else {
                initEmptyVO(vo);
            }
        } else {
            initEmptyVO(vo);
        }
        
        return vo;
    }
    
    @Override
    @Transactional(rollbackFor = Exception.class)
    public BatchImportResultVO batchImport(List<BatchImportDTO> importList) {
        BatchImportResultVO result = new BatchImportResultVO();
        result.setTotalCount(importList != null ? importList.size() : 0);
        result.setSuccessCount(0);
        result.setFailCount(0);
        
        if (importList == null || importList.isEmpty()) {
            result.setFailCount(0);
            return result;
        }
        
        for (int i = 0; i < importList.size(); i++) {
            BatchImportDTO importDTO = importList.get(i);
            int row = i + 1; // 行号从1开始
            
            try {
                // 转换为WorkTimeRecordDTO
                WorkTimeRecordDTO dto = importDTO.toWorkTimeRecordDTO();
                
                // 验证必填字段
                if (dto.getWorkDate() == null) {
                    result.addError(row, "日期格式错误或为空");
                    result.setFailCount(result.getFailCount() + 1);
                    continue;
                }
                
                if (dto.getStartTime() == null) {
                    result.addError(row, "上班时间格式错误或为空");
                    result.setFailCount(result.getFailCount() + 1);
                    continue;
                }
                
                // 保存记录（如果日期已存在，则更新）
                LambdaQueryWrapper<WorkTimeRecord> wrapper = new LambdaQueryWrapper<>();
                wrapper.eq(WorkTimeRecord::getWorkDate, dto.getWorkDate())
                       .eq(WorkTimeRecord::getDeleted, 0);
                WorkTimeRecord existingRecord = workTimeRecordMapper.selectOne(wrapper);
                
                if (existingRecord != null) {
                    // 更新现有记录
                    dto.setId(existingRecord.getId());
                }
                
                // 保存或更新
                saveOrUpdate(dto);
                result.setSuccessCount(result.getSuccessCount() + 1);
                
            } catch (Exception e) {
                logger.error("批量导入第{}行数据失败", row, e);
                result.addError(row, "导入失败: " + e.getMessage());
                result.setFailCount(result.getFailCount() + 1);
            }
        }
        
        return result;
    }
    
    @Override
    public AverageWorkTimeVO calculateAverageWorkTime(LocalDate startDate, LocalDate endDate, 
                                                       Boolean onlyWorkday, Boolean excludeOvertime,
                                                       String timeRangeType, String timeRangeValue) {
        // 根据时间范围类型计算日期范围
        LocalDate[] dateRange = calculateDateRange(timeRangeType, timeRangeValue, startDate, endDate);
        LocalDate actualStartDate = dateRange[0];
        LocalDate actualEndDate = dateRange[1];
        
        // 查询统计信息
        WorkTimeRecordMapper.WorkTimeStatistics statistics = workTimeRecordMapper
                .selectWorkTimeStatisticsByRange(actualStartDate, actualEndDate, onlyWorkday);
        
        AverageWorkTimeVO vo = new AverageWorkTimeVO();
        vo.setTimeRange(formatTimeRange(timeRangeType, timeRangeValue, actualStartDate, actualEndDate));
        
        if (statistics == null) {
            initEmptyVO(vo);
            return vo;
        }
        
        Integer totalCount = statistics.getTotal() != null ? statistics.getTotal() : 0;
        Integer workdayCount = statistics.getWorkdayCount() != null ? statistics.getWorkdayCount() : 0;
        Long totalMinutes = statistics.getTotalMinutes() != null ? statistics.getTotalMinutes() : 0L;
        Long workdayMinutes = statistics.getWorkdayMinutes() != null ? statistics.getWorkdayMinutes() : 0L;
        
        vo.setTotalRecords(totalCount);
        vo.setWorkdayRecords(workdayCount);
        
        if (totalCount == 0) {
            initEmptyVO(vo);
            return vo;
        }
        
        // 计算总工时
        BigDecimal totalHours = BigDecimal.valueOf(totalMinutes)
                .divide(BigDecimal.valueOf(60), 2, RoundingMode.HALF_UP);
        vo.setTotalHours(totalHours);
        
        // 计算工作日总工时
        BigDecimal workdayTotalHours = BigDecimal.valueOf(workdayMinutes)
                .divide(BigDecimal.valueOf(60), 2, RoundingMode.HALF_UP);
        vo.setWorkdayTotalHours(workdayTotalHours);
        
        // 计算平均工时（所有记录）
        BigDecimal averageHours = totalHours.divide(
                BigDecimal.valueOf(totalCount), 2, RoundingMode.HALF_UP);
        vo.setAverageHours(averageHours);
        vo.setAverageMinutes(totalMinutes / totalCount);
        
        // 计算工作日平均工时
        if (workdayCount > 0) {
            BigDecimal workdayAverageHours = workdayTotalHours.divide(
                    BigDecimal.valueOf(workdayCount), 2, RoundingMode.HALF_UP);
            vo.setWorkdayAverageHours(workdayAverageHours);
            vo.setWorkdayAverageMinutes(workdayMinutes / workdayCount);
        } else {
            vo.setWorkdayAverageHours(BigDecimal.ZERO);
            vo.setWorkdayAverageMinutes(0L);
        }
        
        // 计算剔除加班后的工时（标准工时8小时）
        if (excludeOvertime != null && excludeOvertime) {
            // 查询所有记录，计算剔除加班后的工时
            List<WorkTimeRecord> records = workTimeRecordMapper.selectByDateRange(actualStartDate, actualEndDate);
            long normalMinutes = 0;
            int normalCount = 0;
            long overtimeMinutes = 0;
            
            for (WorkTimeRecord record : records) {
                if (record.getWorkMinutes() != null) {
                    long minutes = record.getWorkMinutes();
                    long standardMinutes = 8 * 60; // 8小时 = 480分钟
                    
                    if (onlyWorkday != null && onlyWorkday && record.getIsWorkday() != 1) {
                        continue; // 如果只统计工作日，跳过非工作日
                    }
                    
                    if (minutes > standardMinutes) {
                        // 超过8小时，只计算8小时
                        normalMinutes += standardMinutes;
                        overtimeMinutes += (minutes - standardMinutes);
                    } else {
                        normalMinutes += minutes;
                    }
                    normalCount++;
                }
            }
            
            if (normalCount > 0) {
                BigDecimal normalTotalHours = BigDecimal.valueOf(normalMinutes)
                        .divide(BigDecimal.valueOf(60), 2, RoundingMode.HALF_UP);
                vo.setNormalTotalHours(normalTotalHours);
                
                BigDecimal normalAverageHours = normalTotalHours.divide(
                        BigDecimal.valueOf(normalCount), 2, RoundingMode.HALF_UP);
                vo.setNormalAverageHours(normalAverageHours);
                vo.setNormalAverageMinutes(normalMinutes / normalCount);
                
                BigDecimal overtimeHours = BigDecimal.valueOf(overtimeMinutes)
                        .divide(BigDecimal.valueOf(60), 2, RoundingMode.HALF_UP);
                vo.setOvertimeHours(overtimeHours);
            } else {
                vo.setNormalTotalHours(BigDecimal.ZERO);
                vo.setNormalAverageHours(BigDecimal.ZERO);
                vo.setNormalAverageMinutes(0L);
                vo.setOvertimeHours(BigDecimal.ZERO);
            }
        }
        
        return vo;
    }
    
    /**
     * 根据时间范围类型计算日期范围
     */
    private LocalDate[] calculateDateRange(String timeRangeType, String timeRangeValue, 
                                           LocalDate startDate, LocalDate endDate) {
        LocalDate now = LocalDate.now();
        
        if (timeRangeType == null || "all".equals(timeRangeType)) {
            // 全部数据
            return new LocalDate[]{null, null};
        }
        
        if ("custom".equals(timeRangeType)) {
            // 自定义范围
            return new LocalDate[]{startDate, endDate};
        }
        
        if ("week".equals(timeRangeType)) {
            // 周
            if ("current".equals(timeRangeValue)) {
                // 当前周
                return getWeekRange(now);
            } else if (timeRangeValue != null && timeRangeValue.matches("\\d{4}-W\\d{2}")) {
                // 指定周：2025-W01
                return parseWeekRange(timeRangeValue);
            } else {
                return getWeekRange(now);
            }
        }
        
        if ("month".equals(timeRangeType)) {
            // 月
            if ("current".equals(timeRangeValue)) {
                // 当前月
                return getMonthRange(now);
            } else if (timeRangeValue != null && timeRangeValue.matches("\\d{4}-\\d{2}")) {
                // 指定月：2025-01
                return parseMonthRange(timeRangeValue);
            } else {
                return getMonthRange(now);
            }
        }
        
        if ("year".equals(timeRangeType)) {
            // 年
            if ("current".equals(timeRangeValue)) {
                // 当前年
                return getYearRange(now);
            } else if (timeRangeValue != null && timeRangeValue.matches("\\d{4}")) {
                // 指定年：2025
                return parseYearRange(timeRangeValue);
            } else {
                return getYearRange(now);
            }
        }
        
        return new LocalDate[]{null, null};
    }
    
    /**
     * 获取周的范围
     */
    private LocalDate[] getWeekRange(LocalDate date) {
        WeekFields weekFields = WeekFields.of(Locale.CHINA);
        LocalDate monday = date.with(weekFields.dayOfWeek(), 1);
        LocalDate sunday = monday.plusDays(6);
        return new LocalDate[]{monday, sunday};
    }
    
    /**
     * 解析周范围
     */
    private LocalDate[] parseWeekRange(String weekValue) {
        // 格式：2025-W01
        String[] parts = weekValue.split("-W");
        int year = Integer.parseInt(parts[0]);
        int week = Integer.parseInt(parts[1]);
        
        LocalDate firstDayOfYear = LocalDate.of(year, 1, 1);
        WeekFields weekFields = WeekFields.of(Locale.CHINA);
        LocalDate monday = firstDayOfYear.with(weekFields.weekOfWeekBasedYear(), week)
                .with(weekFields.dayOfWeek(), 1);
        LocalDate sunday = monday.plusDays(6);
        return new LocalDate[]{monday, sunday};
    }
    
    /**
     * 获取月的范围
     */
    private LocalDate[] getMonthRange(LocalDate date) {
        YearMonth yearMonth = YearMonth.from(date);
        LocalDate firstDay = yearMonth.atDay(1);
        LocalDate lastDay = yearMonth.atEndOfMonth();
        return new LocalDate[]{firstDay, lastDay};
    }
    
    /**
     * 解析月范围
     */
    private LocalDate[] parseMonthRange(String monthValue) {
        // 格式：2025-01
        YearMonth yearMonth = YearMonth.parse(monthValue);
        LocalDate firstDay = yearMonth.atDay(1);
        LocalDate lastDay = yearMonth.atEndOfMonth();
        return new LocalDate[]{firstDay, lastDay};
    }
    
    /**
     * 获取年的范围
     */
    private LocalDate[] getYearRange(LocalDate date) {
        int year = date.getYear();
        LocalDate firstDay = LocalDate.of(year, 1, 1);
        LocalDate lastDay = LocalDate.of(year, 12, 31);
        return new LocalDate[]{firstDay, lastDay};
    }
    
    /**
     * 解析年范围
     */
    private LocalDate[] parseYearRange(String yearValue) {
        int year = Integer.parseInt(yearValue);
        LocalDate firstDay = LocalDate.of(year, 1, 1);
        LocalDate lastDay = LocalDate.of(year, 12, 31);
        return new LocalDate[]{firstDay, lastDay};
    }
    
    /**
     * 格式化时间范围描述
     */
    private String formatTimeRange(String timeRangeType, String timeRangeValue, 
                                   LocalDate startDate, LocalDate endDate) {
        if (timeRangeType == null || "all".equals(timeRangeType)) {
            return "全部数据";
        }
        
        if ("custom".equals(timeRangeType)) {
            if (startDate != null && endDate != null) {
                return startDate + " 至 " + endDate;
            }
            return "自定义范围";
        }
        
        if ("week".equals(timeRangeType)) {
            if ("current".equals(timeRangeValue)) {
                return "当前周";
            }
            return timeRangeValue != null ? timeRangeValue + "周" : "当前周";
        }
        
        if ("month".equals(timeRangeType)) {
            if ("current".equals(timeRangeValue)) {
                return "当前月";
            }
            return timeRangeValue != null ? timeRangeValue : "当前月";
        }
        
        if ("year".equals(timeRangeType)) {
            if ("current".equals(timeRangeValue)) {
                return "当前年";
            }
            return timeRangeValue != null ? timeRangeValue + "年" : "当前年";
        }
        
        return "全部数据";
    }
    
    /**
     * 初始化空VO
     */
    private void initEmptyVO(AverageWorkTimeVO vo) {
        vo.setTotalRecords(0);
        vo.setWorkdayRecords(0);
        vo.setTotalHours(BigDecimal.ZERO);
        vo.setWorkdayTotalHours(BigDecimal.ZERO);
        vo.setNormalTotalHours(BigDecimal.ZERO);
        vo.setAverageHours(BigDecimal.ZERO);
        vo.setWorkdayAverageHours(BigDecimal.ZERO);
        vo.setNormalAverageHours(BigDecimal.ZERO);
        vo.setAverageMinutes(0L);
        vo.setWorkdayAverageMinutes(0L);
        vo.setNormalAverageMinutes(0L);
        vo.setOvertimeHours(BigDecimal.ZERO);
    }
}
