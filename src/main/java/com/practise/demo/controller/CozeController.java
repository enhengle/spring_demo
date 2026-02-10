package com.practise.demo.controller;

import com.practise.demo.common.constant.ErrorCode;
import com.practise.demo.model.dto.CozeWorkflowRequestDTO;
import com.practise.demo.model.dto.CozeWorkflowResponseDTO;
import com.practise.demo.response.Response;
import com.practise.demo.service.CozeService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

/**
 * Coze控制器
 * 
 * @author system
 */
@RestController
@RequestMapping("/api/coze")
@Tag(name = "Coze SQL识别", description = "Coze SQL识别工作流接口")
public class CozeController {
    
    private static final Logger logger = LoggerFactory.getLogger(CozeController.class);
    
    @Autowired
    private CozeService cozeService;
    
    /**
     * 执行SQL识别工作流（阻塞模式或流式模式）
     */
    @PostMapping("/workflow")
    @Operation(summary = "执行SQL识别工作流", description = "支持阻塞模式和流式模式")
    public Object executeWorkflow(@Validated @RequestBody CozeWorkflowRequestDTO request) {
        logger.info("接收Coze工作流请求：input={}, responseMode={}", 
                request.getInput(), request.getResponseMode());
        
        // 根据响应模式选择处理方式
        String responseMode = request.getResponseMode();
        if (responseMode == null || responseMode.isEmpty()) {
            responseMode = "blocking";
        }
        
        if ("streaming".equalsIgnoreCase(responseMode)) {
            // 流式模式：返回SSE
            SseEmitter emitter = new SseEmitter(300000L); // 5分钟超时
            emitter.onTimeout(() -> {
                logger.warn("SSE连接超时");
                emitter.complete();
            });
            emitter.onError((ex) -> {
                logger.error("SSE连接错误", ex);
                emitter.completeWithError(ex);
            });
            cozeService.executeWorkflowStream(request, emitter);
            return emitter;
        } else {
            // 阻塞模式：等待完整响应
            try {
                CozeWorkflowResponseDTO response = cozeService.executeWorkflow(request);
                return Response.ok(response);
            } catch (Exception e) {
                logger.error("Coze工作流执行失败", e);
                return Response.error(ErrorCode.OPERATION_FAILED.getCode(), 
                        "Coze工作流执行失败: " + e.getMessage());
            }
        }
    }
}
