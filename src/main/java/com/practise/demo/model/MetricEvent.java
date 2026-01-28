package com.practise.demo.model;

import java.io.Serializable;

/**
 * 指标事件模型
 */
public class MetricEvent implements Serializable {
    private String userId;
    private String eventType;
    private String productId;
    private Double amount;
    private Long timestamp;
    private String region;

    public MetricEvent() {
    }

    public MetricEvent(String userId, String eventType, String productId, Double amount, Long timestamp, String region) {
        this.userId = userId;
        this.eventType = eventType;
        this.productId = productId;
        this.amount = amount;
        this.timestamp = timestamp;
        this.region = region;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getEventType() {
        return eventType;
    }

    public void setEventType(String eventType) {
        this.eventType = eventType;
    }

    public String getProductId() {
        return productId;
    }

    public void setProductId(String productId) {
        this.productId = productId;
    }

    public Double getAmount() {
        return amount;
    }

    public void setAmount(Double amount) {
        this.amount = amount;
    }

    public Long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Long timestamp) {
        this.timestamp = timestamp;
    }

    public String getRegion() {
        return region;
    }

    public void setRegion(String region) {
        this.region = region;
    }

    @Override
    public String toString() {
        return "MetricEvent{" +
                "userId='" + userId + '\'' +
                ", eventType='" + eventType + '\'' +
                ", productId='" + productId + '\'' +
                ", amount=" + amount +
                ", timestamp=" + timestamp +
                ", region='" + region + '\'' +
                '}';
    }
}
