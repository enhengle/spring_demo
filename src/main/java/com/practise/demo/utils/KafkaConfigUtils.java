package com.practise.demo.utils;

import org.springframework.boot.autoconfigure.kafka.KafkaProperties;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.listener.ContainerProperties;

import java.util.Map;

public class KafkaConfigUtils {
    /**
     * 构建消费工厂（集群配置 + 提交策略）
     *
     * @param clusterProperties 集群基础配置
     * @param commitProperties  提交策略配置
     * @param ackMode           ACK模式
     * @return 容器工厂
     */
    public static ConcurrentKafkaListenerContainerFactory<String, String> buildContainerFactory(
            KafkaProperties clusterProperties,
            KafkaProperties.Consumer commitProperties,
            ContainerProperties.AckMode ackMode
    ) {
        // 1. 构建集群基础配置（bootstrap-servers等）
        Map<String, Object> props = clusterProperties.buildConsumerProperties();
        // 2. 合并提交策略配置（enable-auto-commit等）
        props.putAll(commitProperties.buildProperties());
        // 3. 创建ConsumerFactory
        ConsumerFactory<String, String> consumerFactory = new DefaultKafkaConsumerFactory<>(props);
        // 4. 创建容器工厂并设置ACK模式
        ConcurrentKafkaListenerContainerFactory<String, String> factory = new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(consumerFactory);
        factory.getContainerProperties()
                .setAckMode(ackMode);
        return factory;
    }
}
