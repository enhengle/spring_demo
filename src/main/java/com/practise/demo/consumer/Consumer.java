package com.practise.demo.consumer;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.annotation.KafkaListener;
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
     * */
    @KafkaListener(topics = "enheng")
    public void listen(ConsumerRecord<?, ?> record) {
        Optional<?> kafkaMessage = Optional.ofNullable(record.value());
        if (kafkaMessage.isPresent()) {
            Object message = kafkaMessage.get();
            System.out.println("--->" + record);
            System.out.println("--->" + message);
            System.out.println(message);
        }
    }

}
