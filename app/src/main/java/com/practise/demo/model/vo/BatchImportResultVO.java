package com.practise.demo.model.vo;

import java.util.ArrayList;
import java.util.List;

/**
 * 批量导入结果VO
 * 
 * @author system
 */
public class BatchImportResultVO {
    
    /**
     * 总记录数
     */
    private Integer totalCount;
    
    /**
     * 成功数量
     */
    private Integer successCount;
    
    /**
     * 失败数量
     */
    private Integer failCount;
    
    /**
     * 失败记录详情
     */
    private List<ImportError> errors;
    
    public BatchImportResultVO() {
        this.errors = new ArrayList<>();
    }
    
    public Integer getTotalCount() {
        return totalCount;
    }
    
    public void setTotalCount(Integer totalCount) {
        this.totalCount = totalCount;
    }
    
    public Integer getSuccessCount() {
        return successCount;
    }
    
    public void setSuccessCount(Integer successCount) {
        this.successCount = successCount;
    }
    
    public Integer getFailCount() {
        return failCount;
    }
    
    public void setFailCount(Integer failCount) {
        this.failCount = failCount;
    }
    
    public List<ImportError> getErrors() {
        return errors;
    }
    
    public void setErrors(List<ImportError> errors) {
        this.errors = errors;
    }
    
    public void addError(int row, String message) {
        errors.add(new ImportError(row, message));
    }
    
    /**
     * 导入错误信息
     */
    public static class ImportError {
        private Integer row;
        private String message;
        
        public ImportError(Integer row, String message) {
            this.row = row;
            this.message = message;
        }
        
        public Integer getRow() {
            return row;
        }
        
        public void setRow(Integer row) {
            this.row = row;
        }
        
        public String getMessage() {
            return message;
        }
        
        public void setMessage(String message) {
            this.message = message;
        }
    }
}
