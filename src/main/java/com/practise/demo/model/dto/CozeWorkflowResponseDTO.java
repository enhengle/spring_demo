package com.practise.demo.model.dto;

import lombok.Data;
import java.util.List;
import java.util.Map;

/**
 * Coze工作流响应DTO
 * 
 * @author system
 */
@Data
public class CozeWorkflowResponseDTO {
    
    /**
     * 工作流执行结果
     */
    private String result;
    
    /**
     * SQL识别结果（如果工作流返回SQL）
     */
    private String sql;
    
    /**
     * SQL类型（SELECT, INSERT, UPDATE, DELETE等）
     */
    private String sqlType;
    
    /**
     * SQL关键字列表
     */
    private List<String> keywords;
    
    /**
     * 是否正常（true=正常，false=异常）
     */
    private Boolean isValid;
    
    /**
     * 完整响应数据
     */
    private Map<String, Object> data;
    
    /**
     * 任务ID
     */
    private String taskId;
    
    /**
     * 执行状态
     */
    private String status;
    
    /**
     * 错误信息（如果有）
     */
    private String error;
}
