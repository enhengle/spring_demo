package com.practise.demo.model;

import java.io.Serializable;

/**
 * 实时指标计算结果
 */
public class RealtimeMetric implements Serializable {
    private String metricKey;      // 指标键，如：total_count, total_amount, region_count:beijing
    private String metricType;     // 指标类型，如：count, sum, avg
    private Double metricValue;    // 指标值
    private Long windowStart;      // 窗口开始时间
    private Long windowEnd;        // 窗口结束时间
    private String dimension;      // 维度，如：region, productId
    private String dimensionValue; // 维度值

    public RealtimeMetric() {
    }

    public RealtimeMetric(String metricKey, String metricType, Double metricValue, 
                         Long windowStart, Long windowEnd, String dimension, String dimensionValue) {
        this.metricKey = metricKey;
        this.metricType = metricType;
        this.metricValue = metricValue;
        this.windowStart = windowStart;
        this.windowEnd = windowEnd;
        this.dimension = dimension;
        this.dimensionValue = dimensionValue;
    }

    public String getMetricKey() {
        return metricKey;
    }

    public void setMetricKey(String metricKey) {
        this.metricKey = metricKey;
    }

    public String getMetricType() {
        return metricType;
    }

    public void setMetricType(String metricType) {
        this.metricType = metricType;
    }

    public Double getMetricValue() {
        return metricValue;
    }

    public void setMetricValue(Double metricValue) {
        this.metricValue = metricValue;
    }

    public Long getWindowStart() {
        return windowStart;
    }

    public void setWindowStart(Long windowStart) {
        this.windowStart = windowStart;
    }

    public Long getWindowEnd() {
        return windowEnd;
    }

    public void setWindowEnd(Long windowEnd) {
        this.windowEnd = windowEnd;
    }

    public String getDimension() {
        return dimension;
    }

    public void setDimension(String dimension) {
        this.dimension = dimension;
    }

    public String getDimensionValue() {
        return dimensionValue;
    }

    public void setDimensionValue(String dimensionValue) {
        this.dimensionValue = dimensionValue;
    }

    @Override
    public String toString() {
        return "RealtimeMetric{" +
                "metricKey='" + metricKey + '\'' +
                ", metricType='" + metricType + '\'' +
                ", metricValue=" + metricValue +
                ", windowStart=" + windowStart +
                ", windowEnd=" + windowEnd +
                ", dimension='" + dimension + '\'' +
                ", dimensionValue='" + dimensionValue + '\'' +
                '}';
    }
}
