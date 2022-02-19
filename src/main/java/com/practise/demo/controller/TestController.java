package com.practise.demo.controller;

import com.practise.demo.response.Response;
import com.practise.demo.service.TestService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author lingwang
 * @date 2022/2/19 14:49
 */
@RestController
@RequestMapping("/test")
public class TestController {

    @Autowired
    private TestService testService;

    //    获取端口
    @GetMapping("/get_port")
    public Response getPort() {
        return Response.ok(testService.getPort());
    }
}
