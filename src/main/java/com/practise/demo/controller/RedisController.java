package com.practise.demo.controller;

import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

/**
 * @author lingwang
 * @date 2022/2/19 19:40
 */
@RestController
@RequestMapping("redis")
public class RedisController {

    @Resource
    private StringRedisTemplate stringRedisTemplate;

    /*
    * 查看该key是否存在
    * */
    @RequestMapping(value = "/isExist", method = RequestMethod.POST)
    public Boolean isExist(@RequestParam(value = "key", required = false, defaultValue = "key") String key) {
        /*
         * false:不存在
         * true:存在
         * */
        return stringRedisTemplate.hasKey("enheng");
    }

    /*
    * 新建一个string的缓存
    * */
    @RequestMapping(value = "/putString", method = RequestMethod.POST)
    public Boolean putString(@RequestParam(value = "key", required = false, defaultValue = "key") String key,
                             @RequestParam(value = "value", required = false, defaultValue = "value") String value) {
        try {
            stringRedisTemplate.opsForValue().set(key, value);
        } catch (Exception e) {
            System.out.println(e);
            return false;
        }
        return true;
    }

    /*
    * 获取string的值
    * */
    @RequestMapping(value = "/getString", method = RequestMethod.POST)
    public String getString(@RequestParam(value = "key", required = false, defaultValue = "key") String key) {
        return stringRedisTemplate.opsForValue().get(key);
    }

    /*
    * 向list左右插入一个数据
    * */
    @RequestMapping(value = "putList", method = RequestMethod.POST)
    public Boolean putList(
            @RequestParam(value = "list", required = false, defaultValue = "list") String list,
            @RequestParam(value = "value", required = false, defaultValue = "value") String value) {
        try {
            stringRedisTemplate.opsForList().leftPush(list, value);
            stringRedisTemplate.opsForList().rightPush(list, value);
        } catch (Exception e) {
            System.out.println(e);
            return false;
        }
        return true;

    }

    /*
    * 获取list信息
    * */
    @RequestMapping(value = "getList", method = RequestMethod.POST)
    public Object getList(
            @RequestParam(value = "list", required = false, defaultValue = "list") String list,
            @RequestParam(value = "right", required = false, defaultValue = "0") long right,
            @RequestParam(value = "left", required = false, defaultValue = "-1") long left) {
        return stringRedisTemplate.opsForList().range(list, right, left);
    }

    /*
     * 左右删除list数据
     * */
    @RequestMapping(value = "popList", method = RequestMethod.POST)
    public Object popList(
            @RequestParam(value = "list", required = false, defaultValue = "list") String list,
            @RequestParam(value = "right", required = false, defaultValue = "0") long right,
            @RequestParam(value = "left", required = false, defaultValue = "-1") long left) {
        try {
            stringRedisTemplate.opsForList().leftPop(list);
            stringRedisTemplate.opsForList().rightPop(list);
        } catch (Exception e) {
            System.out.println(e);
            return false;
        }
        return true;

    }

}
