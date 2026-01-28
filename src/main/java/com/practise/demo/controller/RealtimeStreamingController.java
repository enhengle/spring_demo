package com.practise.demo.controller;

import com.alibaba.fastjson.JSON;
import com.practise.demo.model.MetricEvent;
import com.practise.demo.service.RealtimeStreamingService;
import com.practise.demo.utils.KafkaProducerUtil;
import com.practise.demo.utils.RedisUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

/**
 * 实时流处理控制器
 */
@RestController
@RequestMapping("/api/realtime")
public class RealtimeStreamingController {

    @Autowired
    private RealtimeStreamingService streamingService;

    @Autowired
    private KafkaProducerUtil kafkaProducerUtil;

    @Autowired
    private RedisUtil redisUtil;

    @Value("${kafka.topic}")
    private String kafkaTopic;

    /**
     * 启动流处理
     * GET /api/realtime/start
     */
    @GetMapping("/start")
    public Map<String, Object> start() {
        Map<String, Object> result = new HashMap<>();
        try {
            // 在新线程中启动流处理，避免阻塞
            new Thread(() -> {
                try {
                    streamingService.start();
                } catch (Exception e) {
                    // 流处理异常已在服务中记录
                }
            }).start();

            result.put("success", true);
            result.put("message", "流处理启动中...");
            result.put("status", "starting");
        } catch (Exception e) {
            result.put("success", false);
            result.put("message", "启动流处理失败: " + e.getMessage());
        }
        return result;
    }

    /**
     * 停止流处理
     * GET /api/realtime/stop
     */
    @GetMapping("/stop")
    public Map<String, Object> stop() {
        Map<String, Object> result = new HashMap<>();
        try {
            streamingService.stop();
            result.put("success", true);
            result.put("message", "流处理已停止");
            result.put("status", "stopped");
        } catch (Exception e) {
            result.put("success", false);
            result.put("message", "停止流处理失败: " + e.getMessage());
        }
        return result;
    }

    /**
     * 获取流处理状态
     * GET /api/realtime/status
     */
    @GetMapping("/status")
    public Map<String, Object> status() {
        Map<String, Object> result = new HashMap<>();
        result.put("success", true);
        result.put("isRunning", streamingService.isRunning());
        result.put("status", streamingService.isRunning() ? "running" : "stopped");
        return result;
    }

    /**
     * 发送测试数据到 Kafka
     * POST /api/realtime/send-test-data
     * Body: {"count": 10}
     */
    @PostMapping("/send-test-data")
    public Map<String, Object> sendTestData(@RequestBody(required = false) Map<String, Object> request) {
        Map<String, Object> result = new HashMap<>();
        try {
            int count = request != null && request.containsKey("count") 
                    ? (Integer) request.get("count") 
                    : 10;

            String[] eventTypes = {"click", "purchase", "view", "add_cart", "checkout"};
            String[] regions = {"beijing", "shanghai", "guangzhou", "shenzhen", "hangzhou"};
            String[] productIds = {"P001", "P002", "P003", "P004", "P005"};

            Random random = new Random();
            int successCount = 0;

            for (int i = 0; i < count; i++) {
                MetricEvent event = new MetricEvent(
                        "U" + String.format("%04d", random.nextInt(1000)),
                        eventTypes[random.nextInt(eventTypes.length)],
                        productIds[random.nextInt(productIds.length)],
                        random.nextDouble() * 1000,
                        System.currentTimeMillis(),
                        regions[random.nextInt(regions.length)]
                );

                String json = JSON.toJSONString(event);
                kafkaProducerUtil.send(kafkaTopic, event.getUserId(), json);
                successCount++;
            }

            result.put("success", true);
            result.put("message", "测试数据发送成功");
            result.put("sentCount", successCount);
            result.put("topic", kafkaTopic);
        } catch (Exception e) {
            result.put("success", false);
            result.put("message", "发送测试数据失败: " + e.getMessage());
        }
        return result;
    }

    /**
     * 从 Redis 获取指标
     * GET /api/realtime/metrics/redis?pattern=metric:*
     */
    @GetMapping("/metrics/redis")
    public Map<String, Object> getMetricsFromRedis(@RequestParam(defaultValue = "metric:*") String pattern) {
        Map<String, Object> result = new HashMap<>();
        try {
            java.util.Set<String> keys = redisUtil.keys(pattern);
            Map<String, Object> metrics = new HashMap<>();
            if (keys != null) {
                for (String key : keys) {
                    Object value = redisUtil.get(key);
                    metrics.put(key, value);
                }
            }
            result.put("success", true);
            result.put("metrics", metrics);
            result.put("count", metrics.size());
        } catch (Exception e) {
            result.put("success", false);
            result.put("message", "获取 Redis 指标失败: " + e.getMessage());
        }
        return result;
    }

    /**
     * 获取特定指标
     * GET /api/realtime/metrics/redis/{key}
     */
    @GetMapping("/metrics/redis/{key}")
    public Map<String, Object> getMetric(@PathVariable String key) {
        Map<String, Object> result = new HashMap<>();
        try {
            Object value = redisUtil.get(key);
            result.put("success", true);
            result.put("key", key);
            result.put("value", value);
        } catch (Exception e) {
            result.put("success", false);
            result.put("message", "获取指标失败: " + e.getMessage());
        }
        return result;
    }
}
