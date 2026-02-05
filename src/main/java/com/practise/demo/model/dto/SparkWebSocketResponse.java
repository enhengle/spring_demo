package com.practise.demo.model.dto;

import lombok.Data;
import java.util.List;
import java.util.Map;

/**
 * 讯飞星火WebSocket响应DTO
 * 参考文档：https://www.xfyun.cn/doc/spark/X1ws.html
 * 
 * @author system
 */
@Data
public class SparkWebSocketResponse {
    
    /**
     * 响应头
     */
    private Header header;
    
    /**
     * 响应负载
     */
    private Payload payload;
    
    @Data
    public static class Header {
        /**
         * 错误码，0表示成功
         */
        private Integer code;
        
        /**
         * 错误信息
         */
        private String message;
        
        /**
         * 会话ID
         */
        private String sid;
        
        /**
         * 状态：0-首次结果，1-中间结果，2-最后一个结果
         */
        private Integer status;
    }
    
    @Data
    public static class Payload {
        /**
         * 选择结果
         */
        private Choices choices;
        
        /**
         * Token使用情况
         */
        private Usage usage;
    }
    
    @Data
    public static class Choices {
        /**
         * 文本选择（数组）
         */
        private List<Text> text;
        
        /**
         * 状态
         */
        private Integer status;
    }
    
    @Data
    public static class Text {
        /**
         * 角色
         */
        private String role;
        
        /**
         * 内容
         */
        private String content;
        
        /**
         * 内容索引
         */
        private Integer index;
    }
    
    @Data
    public static class Usage {
        /**
         * 输入token数
         */
        private Map<String, Integer> text;
    }
}
