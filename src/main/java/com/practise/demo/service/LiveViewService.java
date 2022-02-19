package com.practise.demo.service;

import com.practise.demo.pojo.stat.LiveView;

import java.util.List;

/**
 * @author lingwang
 * @date 2022/2/19 15:51
 */
public interface LiveViewService {

    List<LiveView> findAll(int page, int size);
}
