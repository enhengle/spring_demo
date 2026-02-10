package com.practise.demo.model.dto;

import lombok.Data;
import javax.validation.constraints.NotBlank;
import java.util.Map;

/**
 * Coze工作流请求DTO
 * 
 * @author system
 */
@Data
public class CozeWorkflowRequestDTO {
    
    /**
     * 用户输入的自然语言（用于SQL识别）
     */
    @NotBlank(message = "输入内容不能为空")
    private String input;
    
    /**
     * 响应模式：blocking（阻塞模式，等待完整响应）或 streaming（流式模式，实时返回）
     * 默认为 blocking
     */
    private String responseMode = "blocking";
    
    /**
     * 工作流参数（可选）
     */
    private Map<String, Object> parameters;
}
