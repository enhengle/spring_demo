package com.practise.demo.common;

import java.lang.annotation.*;

/**
 * @author lingwang
 * @date 2022/2/19 16:35
 * 日志打印自定义注解
 */
@Target({ElementType.TYPE, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Inherited
public @interface LogPrint {
}
