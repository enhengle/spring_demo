package com.practise.demo.producer;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * @author lingwang
 * @date 2021/6/29 14:55
 */
@Component
public class Producer {
    @Autowired
    private KafkaTemplate kafkaTemplate;

    /*
     * 发送消息
     * */
    public void send() {
        try {
            Message message = new Message();
            message.setId("en" + System.currentTimeMillis());
            message.setMsg(UUID.randomUUID().toString());
            message.setSendTime(LocalDateTime.now());
            kafkaTemplate.send("enheng", message.toString());
        } catch (Exception e) {
            System.out.println(e);
        }
    }


    /*
     * 发送消息, 指定分区
     * */
    public void send(Integer partition, String key) {
        try {
            Message message = new Message();
            message.setId("en" + System.currentTimeMillis());
            message.setMsg(UUID.randomUUID().toString());
            message.setSendTime(LocalDateTime.now());
            kafkaTemplate.send("enheng", partition, key, message.toString());
        } catch (Exception e) {
            System.out.println(e);
        }
    }
}
