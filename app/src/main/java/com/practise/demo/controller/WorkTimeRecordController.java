package com.practise.demo.controller;

import com.practise.demo.common.response.Response;
import com.practise.demo.model.dto.BatchImportDTO;
import com.practise.demo.model.dto.WorkTimeRecordDTO;
import com.practise.demo.model.entity.WorkTimeRecord;
import com.practise.demo.model.vo.AverageWorkTimeVO;
import com.practise.demo.model.vo.BatchImportResultVO;
import com.practise.demo.service.WorkTimeRecordService;
import com.practise.demo.util.CsvParserUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.time.LocalDate;
import java.util.List;

/**
 * 上下班时间记录Controller
 * 
 * @author system
 */
@RestController
@RequestMapping("/api/work-time")
@CrossOrigin(origins = "*")
public class WorkTimeRecordController {
    
    @Autowired
    private WorkTimeRecordService workTimeRecordService;
    
    /**
     * 保存或更新记录
     */
    @PostMapping("/save")
    public Response<WorkTimeRecord> saveOrUpdate(@Valid @RequestBody WorkTimeRecordDTO dto) {
        WorkTimeRecord record = workTimeRecordService.saveOrUpdate(dto);
        return Response.ok(record);
    }
    
    /**
     * 根据ID查询记录
     */
    @GetMapping("/{id}")
    public Response<WorkTimeRecord> getById(@PathVariable Long id) {
        WorkTimeRecord record = workTimeRecordService.getById(id);
        return Response.ok(record);
    }
    
    /**
     * 查询所有记录
     */
    @GetMapping("/list")
    public Response<List<WorkTimeRecord>> listAll() {
        List<WorkTimeRecord> list = workTimeRecordService.listAll();
        return Response.ok(list);
    }
    
    /**
     * 根据日期范围查询记录
     */
    @GetMapping("/list-by-date")
    public Response<List<WorkTimeRecord>> listByDateRange(
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate startDate,
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate endDate) {
        List<WorkTimeRecord> list = workTimeRecordService.listByDateRange(startDate, endDate);
        return Response.ok(list);
    }
    
    /**
     * 删除记录
     */
    @DeleteMapping("/{id}")
    public Response<Void> deleteById(@PathVariable Long id) {
        workTimeRecordService.deleteById(id);
        return Response.ok();
    }
    
    /**
     * 计算平均工时（全部数据）
     */
    @GetMapping("/average")
    public Response<AverageWorkTimeVO> calculateAverageWorkTime() {
        AverageWorkTimeVO vo = workTimeRecordService.calculateAverageWorkTime();
        return Response.ok(vo);
    }
    
    /**
     * 按条件计算平均工时
     */
    @GetMapping("/average/query")
    public Response<AverageWorkTimeVO> calculateAverageWorkTimeQuery(
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate endDate,
            @RequestParam(required = false) Boolean onlyWorkday,
            @RequestParam(required = false) Boolean excludeOvertime,
            @RequestParam(required = false) String timeRangeType,
            @RequestParam(required = false) String timeRangeValue) {
        AverageWorkTimeVO vo = workTimeRecordService.calculateAverageWorkTime(
                startDate, endDate, onlyWorkday, excludeOvertime, timeRangeType, timeRangeValue);
        return Response.ok(vo);
    }
    
    /**
     * 批量导入记录（JSON格式）
     */
    @PostMapping("/batch-import")
    public Response<BatchImportResultVO> batchImport(@RequestBody List<BatchImportDTO> importList) {
        BatchImportResultVO result = workTimeRecordService.batchImport(importList);
        return Response.ok(result);
    }
    
    /**
     * 批量导入记录（CSV格式）
     */
    @PostMapping("/batch-import-csv")
    public Response<BatchImportResultVO> batchImportCsv(@RequestBody String csvText) {
        // 解析CSV文本
        List<BatchImportDTO> importList = CsvParserUtil.parseCsv(csvText, true);
        // 执行批量导入
        BatchImportResultVO result = workTimeRecordService.batchImport(importList);
        return Response.ok(result);
    }
}
