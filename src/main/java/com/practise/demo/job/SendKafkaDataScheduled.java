package com.practise.demo.job;

import com.practise.demo.producer.Message;
import com.practise.demo.producer.Producer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.util.UUID;

/**
 * 定期 发送 Kafka数据
 */
@Component
public class SendKafkaDataScheduled {
    @Resource
    private Producer producer;

    @Autowired
    private KafkaTemplate kafkaTemplate;

    @Value("${kafka.enheng.default.partition:0}")
    private int defaultPartition;


    // 是否发送kafka的开关按钮
    @Value("${kafka.send.switch:true}")
    private boolean sendSwitch;

    @Scheduled(fixedRate = 5000) // 每5秒执行一次
//    @Scheduled(cron = "0/5 * * * * ?") // 每5秒执行一次，等同于fixedRate的另一种表达方式
    public void sendKafkaDataNotPartitioned() {
        if (sendSwitch) {
            // 不指定分区
            producer.send();
        }
    }

    @Scheduled(fixedRate = 5000) // 每5秒执行一次
    public void sendKafkaDataToPartitionedOne() {
//        if (sendSwitch) {
//            producer.send(defaultPartition, null);
//        }
    }

}
