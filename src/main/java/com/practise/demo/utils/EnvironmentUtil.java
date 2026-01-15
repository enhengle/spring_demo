package com.practise.demo.utils;

import org.springframework.context.EnvironmentAware;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

@Component
public class EnvironmentUtil implements EnvironmentAware {

    private static Environment env = null;

    @Override
    public void setEnvironment(Environment environment) {
        env = environment;
    }

    public static String getProperty(String key) {
        return env.getProperty(key);
    }

    public static String getProperty(String key, String defaultValue) {
        return env.getProperty(key) == null ? defaultValue : env.getProperty(key);
    }

    public static Boolean getBoolean(String key) {
        return env.getProperty(key, Boolean.class);
    }

    public static Boolean getBoolean(String key, boolean defaultValue) {
        return env.getProperty(key, Boolean.class) == null ? defaultValue : env.getProperty(key, Boolean.class);
    }

}
