package com.practise.demo.service;

import com.practise.demo.pojo.manager.View;

import java.util.List;

/**
 * @author lingwang
 * @date 2022/2/19 15:46
 */
public interface ViewService {
    List<View> findList(int state);

}
