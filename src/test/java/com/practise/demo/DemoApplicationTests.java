package com.practise.demo;

import com.practise.demo.service.TestService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class DemoApplicationTests {

    @Autowired
    private TestService service;

    @Test
    void contextLoads() {
        System.out.println(service.getPort());
    }

}
