package com.practise.demo.controller;

import com.practise.demo.common.annotation.OperationLog;
import com.practise.demo.model.entity.User;
import com.practise.demo.response.Response;
import com.practise.demo.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * 用户控制器（示例）
 * 
 * @author system
 * @date 2024
 */
@Tag(name = "用户接口", description = "用户相关接口")
@RestController
@RequestMapping("/user")
public class UserController {
    
    @Autowired
    private UserService userService;
    
    /**
     * 根据ID获取用户
     */
    @Operation(summary = "获取用户", description = "根据ID获取用户信息")
    @OperationLog(value = "获取用户", type = "查询", recordParams = true)
    @GetMapping("/{id}")
    public Response<User> getUser(
            @Parameter(description = "用户ID") @PathVariable Long id) {
        User user = userService.getUserById(id);
        return Response.ok(user);
    }
    
    /**
     * 创建用户
     */
    @Operation(summary = "创建用户", description = "创建新用户")
    @OperationLog(value = "创建用户", type = "新增", recordParams = true, recordResult = true)
    @PostMapping
    public Response<User> createUser(@RequestBody User user) {
        User createdUser = userService.createUser(user);
        return Response.ok(createdUser);
    }
}

