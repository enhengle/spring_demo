package com.practise.demo.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.practise.demo.model.entity.User;
import org.apache.ibatis.annotations.Mapper;

/**
 * 用户Mapper接口
 * 
 * @author system
 * @date 2024
 */
@Mapper
public interface UserMapper extends BaseMapper<User> {
    
}

