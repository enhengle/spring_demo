package com.practise.demo.service.impl;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.practise.demo.mapper.stat.LiveViewMapper;
import com.practise.demo.pojo.stat.LiveView;
import com.practise.demo.service.LiveViewService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

/**
 * @author lingwang
 * @date 2022/2/19 15:52
 */
@Service
public class LiveViewServiceImpl implements LiveViewService {

    @Resource
    private LiveViewMapper liveViewMapper;

    @Override
    public List<LiveView> findAll(int page, int size) {
        Page pagable = new Page<>(page, size);
        return liveViewMapper.findAll(pagable);
    }
}
