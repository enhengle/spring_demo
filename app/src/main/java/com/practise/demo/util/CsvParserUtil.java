package com.practise.demo.util;

import com.practise.demo.model.dto.BatchImportDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

/**
 * CSV解析工具类
 * 
 * @author system
 */
public class CsvParserUtil {
    
    private static final Logger logger = LoggerFactory.getLogger(CsvParserUtil.class);
    
    /**
     * 解析CSV文本数据
     * 格式：日期,星期,上班时间,下班时间
     * 示例：2025/06/25,星期三,09:30:00,19:56:39
     * 
     * @param csvText CSV文本内容
     * @param skipHeader 是否跳过表头（第一行）
     * @return 解析后的DTO列表
     */
    public static List<BatchImportDTO> parseCsv(String csvText, boolean skipHeader) {
        List<BatchImportDTO> result = new ArrayList<>();
        
        if (csvText == null || csvText.trim().isEmpty()) {
            return result;
        }
        
        String[] lines = csvText.split("\n");
        int startIndex = skipHeader ? 1 : 0;
        
        for (int i = startIndex; i < lines.length; i++) {
            String line = lines[i].trim();
            if (line.isEmpty()) {
                continue;
            }
            
            try {
                BatchImportDTO dto = parseLine(line);
                if (dto != null) {
                    result.add(dto);
                }
            } catch (Exception e) {
                logger.warn("解析第{}行数据失败: {}", i + 1, line, e);
            }
        }
        
        return result;
    }
    
    /**
     * 解析单行CSV数据
     * 支持制表符分隔和逗号分隔
     * 
     * @param line CSV行数据
     * @return BatchImportDTO对象
     */
    private static BatchImportDTO parseLine(String line) {
        if (line == null || line.trim().isEmpty()) {
            return null;
        }
        
        // 支持制表符和逗号分隔
        String[] parts;
        if (line.contains("\t")) {
            parts = line.split("\t");
        } else {
            parts = line.split(",");
        }
        
        if (parts.length < 3) {
            return null;
        }
        
        BatchImportDTO dto = new BatchImportDTO();
        
        // 第一列：日期
        if (parts.length > 0) {
            dto.setDate(parts[0].trim());
        }
        
        // 第二列：星期（可选）
        if (parts.length > 1) {
            dto.setWeekday(parts[1].trim());
        }
        
        // 第三列：上班时间
        if (parts.length > 2) {
            dto.setStartTime(parts[2].trim());
        }
        
        // 第四列：下班时间
        if (parts.length > 3) {
            dto.setEndTime(parts[3].trim());
        }
        
        // 第五列：备注（可选）
        if (parts.length > 4) {
            dto.setRemark(parts[4].trim());
        }
        
        return dto;
    }
}
