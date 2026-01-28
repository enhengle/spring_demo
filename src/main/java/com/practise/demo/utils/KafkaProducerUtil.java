package com.practise.demo.utils;

import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.util.Map;

/**
 * Kafka 生产者工具类（用于测试数据发送）
 */
@Component
public class KafkaProducerUtil {

    private static final Logger logger = LoggerFactory.getLogger(KafkaProducerUtil.class);

    @Autowired
    private Map<String, Object> kafkaProducerConfigs;

    private Producer<String, String> producer;

    @PostConstruct
    public void init() {
        producer = new KafkaProducer<>(kafkaProducerConfigs);
        logger.info("Kafka Producer 初始化成功");
    }

    @PreDestroy
    public void destroy() {
        if (producer != null) {
            producer.close();
            logger.info("Kafka Producer 已关闭");
        }
    }

    /**
     * 发送消息到 Kafka
     *
     * @param topic 主题
     * @param key   键
     * @param value 值
     */
    public void send(String topic, String key, String value) {
        try {
            ProducerRecord<String, String> record = new ProducerRecord<>(topic, key, value);
            producer.send(record, (metadata, exception) -> {
                if (exception != null) {
                    logger.error("发送消息失败: topic={}, key={}", topic, key, exception);
                } else {
                    logger.debug("发送消息成功: topic={}, partition={}, offset={}", 
                            metadata.topic(), metadata.partition(), metadata.offset());
                }
            });
        } catch (Exception e) {
            logger.error("发送消息异常: topic={}, key={}", topic, key, e);
            throw new RuntimeException("发送消息失败", e);
        }
    }

    /**
     * 发送消息到 Kafka（无键）
     *
     * @param topic 主题
     * @param value 值
     */
    public void send(String topic, String value) {
        send(topic, null, value);
    }

    /**
     * 同步发送消息
     *
     * @param topic 主题
     * @param key   键
     * @param value 值
     */
    public void sendSync(String topic, String key, String value) {
        try {
            ProducerRecord<String, String> record = new ProducerRecord<>(topic, key, value);
            producer.send(record).get();
            logger.debug("同步发送消息成功: topic={}, key={}", topic, key);
        } catch (Exception e) {
            logger.error("同步发送消息失败: topic={}, key={}", topic, key, e);
            throw new RuntimeException("同步发送消息失败", e);
        }
    }
}
