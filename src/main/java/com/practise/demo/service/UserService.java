package com.practise.demo.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.practise.demo.common.constant.ErrorCode;
import com.practise.demo.mapper.UserMapper;
import com.practise.demo.model.entity.User;
import com.practise.demo.exception.ServerException;
import org.springframework.stereotype.Service;

/**
 * 用户服务类（示例）
 * 
 * @author system
 * @date 2024
 */
@Service
public class UserService extends ServiceImpl<UserMapper, User> {
    
    /**
     * 根据用户名查询用户
     */
    public User getUserByUsername(String username) {
        LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(User::getUsername, username);
        wrapper.eq(User::getDeleted, 0);
        return this.getOne(wrapper);
    }
    
    /**
     * 创建用户
     */
    public User createUser(User user) {
        // 检查用户名是否已存在
        User existUser = getUserByUsername(user.getUsername());
        if (existUser != null) {
            throw new ServerException(ErrorCode.DATA_ALREADY_EXISTS);
        }
        
        // 保存用户
        this.save(user);
        return user;
    }
    
    /**
     * 根据ID获取用户
     */
    public User getUserById(Long id) {
        User user = this.getById(id);
        if (user == null || user.getDeleted() == 1) {
            throw new ServerException(ErrorCode.DATA_NOT_FOUND);
        }
        return user;
    }
}

