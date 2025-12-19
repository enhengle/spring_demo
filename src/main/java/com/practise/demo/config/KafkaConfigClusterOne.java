package com.practise.demo.config;

import com.practise.demo.utils.KafkaConfigUtils;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.kafka.KafkaProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.listener.ContainerProperties;
import org.springframework.stereotype.Component;

/**
 * 方案1 ， 所有需要多个kafka集群，则复制一份该class，改一下集群相关名称 + bean名称
 */
@Component
@EnableKafka
public class KafkaConfigClusterOne {

    // ========== 集群1基础配置 ==========
    @Primary
    @Bean("kafkaCluster1Properties")
    @ConfigurationProperties(prefix = "spring.kafka-cluster1")
    public KafkaProperties kafkaCluster1Properties() {
        return new KafkaProperties();
    }

    // ========== 集群1-自动提交配置 ==========
    @Bean("kafkaCluster1AutoCommitProperties")
    @ConfigurationProperties(prefix = "spring.kafka-cluster1.auto-commit")
    public KafkaProperties.Consumer kafkaCluster1AutoCommitProperties() {
        return new KafkaProperties.Consumer();
    }


    @Bean("kafkaCluster1AutoCommitFactory")
    public ConcurrentKafkaListenerContainerFactory<String, String> kafkaCluster1AutoCommitFactory(
            @Qualifier("kafkaCluster1Properties") KafkaProperties cluster1Props,
            @Qualifier("kafkaCluster1AutoCommitProperties") KafkaProperties.Consumer cluter1AutoCommitProps
    ) {
        return KafkaConfigUtils.buildContainerFactory(cluster1Props, cluter1AutoCommitProps, ContainerProperties.AckMode.BATCH);
    }

    // ========== 集群1-手动提交配置 ==========
    @Bean("kafkaCluster1ManualCommitProperties")
    @ConfigurationProperties(prefix = "spring.kafka-cluster1.manual-commit")
    public KafkaProperties.Consumer kafkaCluster1ManualCommitProperties() {
        return new KafkaProperties.Consumer();
    }

    @Bean("kafkaCluster1ManualCommitFactory")
    public ConcurrentKafkaListenerContainerFactory<String, String> kafkaCluster1ManualCommitFactory(
            @Qualifier("kafkaCluster1Properties") KafkaProperties cluster1Props,
            @Qualifier("kafkaCluster1ManualCommitProperties") KafkaProperties.Consumer cluter1ManualCommitProps
    ) {
        return KafkaConfigUtils.buildContainerFactory(cluster1Props, cluter1ManualCommitProps, ContainerProperties.AckMode.MANUAL_IMMEDIATE);
    }
}
