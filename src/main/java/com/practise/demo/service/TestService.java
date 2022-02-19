package com.practise.demo.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

/**
 * @author lingwang
 * @date 2022/2/19 14:51
 */
@Service
public class TestService {

    private static Logger logger = LoggerFactory.getLogger(TestService.class);

    @Value("${server.port}")
    private int port;

    public int getPort() {
        logger.info("获取配置端口:{}",port);
        return port;
    }

}
