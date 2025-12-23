package com.practise.demo.controller;

import com.practise.demo.common.annotation.OperationLog;
import com.practise.demo.response.Response;
import com.practise.demo.service.TestService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 测试控制器
 * 
 * @author lingwang
 * @date 2022/2/19 14:49
 */
@Tag(name = "测试接口", description = "测试相关接口")
@RestController
@RequestMapping("/test")
public class TestController {

    @Autowired
    private TestService testService;

    /**
     * 获取端口
     */
    @Operation(summary = "获取端口", description = "获取当前服务端口号")
    @OperationLog(value = "获取端口", type = "查询", recordParams = false)
    @GetMapping("/get_port")
    public Response<Integer> getPort() {
        return Response.ok(testService.getPort());
    }
}
