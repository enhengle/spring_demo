package com.practise.demo.model.dto;

import lombok.Data;
import java.util.List;

/**
 * 讯飞星火聊天响应DTO
 * 参考文档：https://www.xfyun.cn/doc/spark/X1http.html
 * 兼容OpenAI API格式
 * 
 * @author system
 */
@Data
public class SparkChatResponse {
    
    /**
     * 响应ID
     */
    private String id;
    
    /**
     * 对象类型
     */
    private String object;
    
    /**
     * 创建时间
     */
    private Long created;
    
    /**
     * 模型版本
     */
    private String model;
    
    /**
     * 选择结果列表
     */
    private List<Choice> choices;
    
    /**
     * Token使用情况
     */
    private Usage usage;
    
    @Data
    public static class Choice {
        /**
         * 选择索引
         */
        private Integer index;
        
        /**
         * 消息内容
         */
        private Message message;
        
        /**
         * 完成原因
         */
        private String finishReason;
    }
    
    @Data
    public static class Message {
        /**
         * 角色
         */
        private String role;
        
        /**
         * 内容
         */
        private String content;
    }
    
    @Data
    public static class Usage {
        /**
         * 输入token数
         */
        private Integer promptTokens;
        
        /**
         * 输出token数
         */
        private Integer completionTokens;
        
        /**
         * 总token数
         */
        private Integer totalTokens;
    }
}
