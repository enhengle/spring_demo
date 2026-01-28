package com.practise.demo.utils;

import com.alibaba.fastjson.JSON;
import com.practise.demo.model.MetricEvent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Random;

/**
 * 测试数据生成器
 */
@Component
public class TestDataGenerator {

    @Autowired
    private KafkaProducerUtil kafkaProducerUtil;

    @Value("${kafka.topic}")
    private String kafkaTopic;

    private final Random random = new Random();
    private final String[] eventTypes = {"click", "purchase", "view", "add_cart", "checkout"};
    private final String[] regions = {"beijing", "shanghai", "guangzhou", "shenzhen", "hangzhou"};
    private final String[] productIds = {"P001", "P002", "P003", "P004", "P005"};

    /**
     * 生成并发送测试数据
     *
     * @param count 生成数量
     */
    public void generateAndSend(int count) {
        for (int i = 0; i < count; i++) {
            MetricEvent event = generateEvent();
            String json = JSON.toJSONString(event);
            kafkaProducerUtil.send(kafkaTopic, event.getUserId(), json);
        }
    }

    /**
     * 生成单个测试事件
     */
    public MetricEvent generateEvent() {
        return new MetricEvent(
                "U" + String.format("%04d", random.nextInt(1000)),
                eventTypes[random.nextInt(eventTypes.length)],
                productIds[random.nextInt(productIds.length)],
                random.nextDouble() * 1000,
                System.currentTimeMillis(),
                regions[random.nextInt(regions.length)]
        );
    }
}
