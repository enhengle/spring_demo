package com.practise.demo.service;

import com.alibaba.fastjson.JSON;
import com.practise.demo.model.MetricEvent;
import com.practise.demo.model.RealtimeMetric;
import com.practise.demo.utils.ClickHouseUtil;
import com.practise.demo.utils.RedisUtil;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.spark.SparkConf;
import org.apache.spark.streaming.Duration;
import org.apache.spark.streaming.api.java.JavaDStream;
import org.apache.spark.streaming.api.java.JavaInputDStream;
import org.apache.spark.streaming.api.java.JavaStreamingContext;
import org.apache.spark.streaming.kafka010.ConsumerStrategies;
import org.apache.spark.streaming.kafka010.KafkaUtils;
import org.apache.spark.streaming.kafka010.LocationStrategies;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 实时流处理服务
 * Kafka → Spark Streaming → 实时指标计算 → Redis/ClickHouse
 */
@Service
public class RealtimeStreamingService {

    private static final Logger logger = LoggerFactory.getLogger(RealtimeStreamingService.class);

    @Autowired
    private Map<String, Object> kafkaConsumerConfigs;

    @Autowired
    private RedisUtil redisUtil;

    @Autowired
    private ClickHouseUtil clickHouseUtil;

    @Value("${kafka.topic}")
    private String kafkaTopic;

    @Value("${spark.streaming.batch-interval}")
    private String batchInterval;

    @Value("${spark.streaming.checkpoint-dir}")
    private String checkpointDir;

    private JavaStreamingContext streamingContext;
    private boolean isRunning = false;

    @PostConstruct
    public void init() {
        logger.info("RealtimeStreamingService 初始化");
    }

    /**
     * 启动流处理
     */
    public synchronized void start() {
        if (isRunning) {
            logger.warn("流处理已在运行中");
            return;
        }

        try {
            // 解析批次间隔（如 "5s" -> 5000ms）
            long batchIntervalMs = parseDuration(batchInterval);

            // 创建 Spark 配置
            SparkConf sparkConf = new SparkConf()
                    .setAppName("RealtimeMetricsStreaming")
                    .setMaster("local[2]")  // 本地模式，2个线程
                    .set("spark.streaming.stopGracefullyOnShutdown", "true")
                    .set("spark.serializer", "org.apache.spark.serializer.KryoSerializer");

            // 创建 StreamingContext
            streamingContext = new JavaStreamingContext(sparkConf, new Duration(batchIntervalMs));

            // 设置检查点目录
            streamingContext.checkpoint(checkpointDir);

            // 创建 Kafka 输入流
            Set<String> topics = Collections.singleton(kafkaTopic);
            JavaInputDStream<ConsumerRecord<String, String>> kafkaStream = KafkaUtils.createDirectStream(
                    streamingContext,
                    LocationStrategies.PreferConsistent(),
                    ConsumerStrategies.<String, String>Subscribe(topics, kafkaConsumerConfigs)
            );

            // 处理数据流
            processStream(kafkaStream);

            // 启动流处理
            streamingContext.start();
            isRunning = true;
            logger.info("流处理启动成功: topic={}, batchInterval={}ms", kafkaTopic, batchIntervalMs);

            // 等待流处理结束
            streamingContext.awaitTermination();
        } catch (Exception e) {
            logger.error("启动流处理失败", e);
            isRunning = false;
            throw new RuntimeException("启动流处理失败", e);
        }
    }

    /**
     * 停止流处理
     */
    public synchronized void stop() {
        if (!isRunning || streamingContext == null) {
            logger.warn("流处理未运行");
            return;
        }

        try {
            streamingContext.stop(true, true);
            isRunning = false;
            logger.info("流处理已停止");
        } catch (Exception e) {
            logger.error("停止流处理失败", e);
        }
    }

    /**
     * 处理数据流
     */
    private void processStream(JavaInputDStream<ConsumerRecord<String, String>> kafkaStream) {
        // 1. 提取消息值并解析为 MetricEvent
        JavaDStream<MetricEvent> events = kafkaStream.map(record -> {
            try {
                String value = record.value();
                return JSON.parseObject(value, MetricEvent.class);
            } catch (Exception e) {
                logger.error("解析消息失败: {}", record.value(), e);
                return null;
            }
        }).filter(event -> event != null);

        // 2. 计算实时指标
        events.foreachRDD(rdd -> {
            if (!rdd.isEmpty()) {
                List<MetricEvent> eventList = rdd.collect();
                logger.info("处理批次数据: count={}", eventList.size());

                // 计算指标
                List<RealtimeMetric> metrics = calculateMetrics(eventList);

                // 写入 Redis
                if (!metrics.isEmpty()) {
                    redisUtil.batchSet(metrics);
                }

                // 写入 ClickHouse
                if (!metrics.isEmpty()) {
                    clickHouseUtil.batchInsert(metrics);
                }

                logger.info("指标计算完成: metricCount={}", metrics.size());
            }
        });
    }

    /**
     * 计算实时指标
     */
    private List<RealtimeMetric> calculateMetrics(List<MetricEvent> events) {
        List<RealtimeMetric> metrics = new ArrayList<>();
        long currentTime = System.currentTimeMillis();
        long windowStart = currentTime - 60000; // 1分钟窗口
        long windowEnd = currentTime;

        // 1. 总计数
        long totalCount = events.size();
        metrics.add(new RealtimeMetric(
                "total_count",
                "count",
                (double) totalCount,
                windowStart,
                windowEnd,
                null,
                null
        ));

        // 2. 总金额
        double totalAmount = events.stream()
                .filter(e -> e.getAmount() != null)
                .mapToDouble(MetricEvent::getAmount)
                .sum();
        metrics.add(new RealtimeMetric(
                "total_amount",
                "sum",
                totalAmount,
                windowStart,
                windowEnd,
                null,
                null
        ));

        // 3. 平均金额
        if (totalCount > 0 && totalAmount > 0) {
            metrics.add(new RealtimeMetric(
                    "avg_amount",
                    "avg",
                    totalAmount / totalCount,
                    windowStart,
                    windowEnd,
                    null,
                    null
            ));
        }

        // 4. 按事件类型分组统计
        Map<String, Long> eventTypeCount = events.stream()
                .filter(e -> e.getEventType() != null)
                .collect(Collectors.groupingBy(
                        MetricEvent::getEventType,
                        Collectors.counting()
                ));
        eventTypeCount.forEach((eventType, count) -> {
            metrics.add(new RealtimeMetric(
                    "event_type_count:" + eventType,
                    "count",
                    count.doubleValue(),
                    windowStart,
                    windowEnd,
                    "eventType",
                    eventType
            ));
        });

        // 5. 按地区分组统计
        Map<String, Long> regionCount = events.stream()
                .filter(e -> e.getRegion() != null)
                .collect(Collectors.groupingBy(
                        MetricEvent::getRegion,
                        Collectors.counting()
                ));
        regionCount.forEach((region, count) -> {
            metrics.add(new RealtimeMetric(
                    "region_count:" + region,
                    "count",
                    count.doubleValue(),
                    windowStart,
                    windowEnd,
                    "region",
                    region
            ));
        });

        // 6. 按产品ID分组统计金额
        Map<String, Double> productAmount = events.stream()
                .filter(e -> e.getProductId() != null && e.getAmount() != null)
                .collect(Collectors.groupingBy(
                        MetricEvent::getProductId,
                        Collectors.summingDouble(MetricEvent::getAmount)
                ));
        productAmount.forEach((productId, amount) -> {
            metrics.add(new RealtimeMetric(
                    "product_amount:" + productId,
                    "sum",
                    amount,
                    windowStart,
                    windowEnd,
                    "productId",
                    productId
            ));
        });

        return metrics;
    }

    /**
     * 解析持续时间字符串（如 "5s" -> 5000）
     */
    private long parseDuration(String duration) {
        if (duration.endsWith("s")) {
            return Long.parseLong(duration.substring(0, duration.length() - 1)) * 1000;
        } else if (duration.endsWith("m")) {
            return Long.parseLong(duration.substring(0, duration.length() - 1)) * 60 * 1000;
        } else {
            return Long.parseLong(duration);
        }
    }

    public boolean isRunning() {
        return isRunning;
    }
}
