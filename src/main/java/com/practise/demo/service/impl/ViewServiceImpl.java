package com.practise.demo.service.impl;

import com.practise.demo.mapper.manager.ViewMapper;
import com.practise.demo.pojo.manager.View;
import com.practise.demo.service.ViewService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

/**
 * @author lingwang
 * @date 2022/2/19 15:45
 */
@Service
public class ViewServiceImpl implements ViewService {

    @Resource
    private ViewMapper viewMapper;

    public List<View> findList(int state) {
        return viewMapper.findList(state);
    }
}
