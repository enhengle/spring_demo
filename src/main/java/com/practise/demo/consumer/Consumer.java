package com.practise.demo.consumer;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.annotation.TopicPartition;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Component;

import java.util.Optional;

/**
 * @author lingwang
 * @date 2021/6/29 14:55
 */
@Component
public class Consumer {

    /*
     * 监听topic
     * 默认从 offset 消费
     * enable-auto-commit: false、auto-offset-reset: latest、ack-mode: manual
     * consumer和listen的配置不可以配上上述配置，否则无法自动提交offset，导致重复消费
     * */
    @KafkaListener(topics = "enheng", groupId = "startWeb", containerFactory = "kafkaCluster1AutoCommitFactory")
    public void listen(ConsumerRecord<?, ?> record) {
        Optional<?> kafkaMessage = Optional.ofNullable(record.value());
        if (kafkaMessage.isPresent()) {
            Object message = kafkaMessage.get();
            System.out.println("startWeb--->" + record);
            System.out.println("startWeb--->" + message);
        }
    }


    /*
     * 监听topic
     * enable-auto-commit: false、auto-offset-reset: latest、ack-mode: manual
     * consumer和listen的配置 一定要 配上上述配置，否则会报错，配置内容可以自行搭配
     * */
    @KafkaListener(topics = "enheng", groupId = "submitAck", containerFactory = "kafkaCluster1ManualCommitFactory")
    public void listen(ConsumerRecord<?, ?> record, Acknowledgment ack) {
        Optional<?> kafkaMessage = Optional.ofNullable(record.value());
        if (kafkaMessage.isPresent()) {
            Object message = kafkaMessage.get();
            System.out.println("submitAck--->" + record);
            System.out.println("submitAck--->" + message);
        }
        // 手动提交offset
        ack.acknowledge();
    }

    /*
     * 监听topic
     * 指定消费分区
     * enable-auto-commit: false、auto-offset-reset: latest、ack-mode: manual
     * consumer和listen的配置 一定要 配上上述配置，否则会报错，配置内容可以自行搭配
     * */
    @KafkaListener(groupId = "consumerByPartitionZero", containerFactory = "kafkaCluster1AutoCommitFactory",
            topicPartitions = {
                    @TopicPartition(topic = "enheng", partitions = {"0"})
            })
    public void listenByPartition(ConsumerRecord<?, ?> record) {
        Optional<?> kafkaMessage = Optional.ofNullable(record.value());
        if (kafkaMessage.isPresent()) {
            Object message = kafkaMessage.get();
            System.out.println("consumerByPartitionZero--->" + record);
            System.out.println("consumerByPartitionZero--->" + message);
        }
    }

}
