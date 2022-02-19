package com.practise.demo.producer;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

/**
 * @author lingwang
 * @date 2021/6/29 14:55
 */
@RestController
@RequestMapping("/kafka")
public class SendController {

    @Resource
    private Producer producer;

    /*
     * 发送消息
     * */
    @GetMapping("/send")
    public void send() {
        producer.send();
    }
}
