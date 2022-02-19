### 一个springboot项目的新手demo

### 环境
```$xslt
window11+jdk8+idea
```

### 目录说明
```$xslt
├─src
│  ├─main
│  │  ├─java
│  │  │  └─com
│  │  │      └─practise
│  │  │          └─demo
│  │  │              │  DemoApplication.java    主函数
│  │  │              │
│  │  │              ├─config   配置层
│  │  │              │      MyConfig.java   跨域配置  
│  │  │              │
│  │  │              ├─controller   控制层
│  │  │              │      TestController.java 测试接口
│  │  │              │
│  │  │              ├─dataJob  定时任务层
│  │  │              │      ClearDataJob.java   清除日志配置
│  │  │              │      HomeManager.java    清除日志方法
│  │  │              │
│  │  │              ├─myExceptionHandler   异常处理层
│  │  │              │      ServerException.java    自定义异常
│  │  │              │      ServerExceptionHandler.java 全局捕捉异常
│  │  │              │
│  │  │              ├─response 返回层
│  │  │              │      EnumCode.java   错误码常量
│  │  │              │      Response.java   返回方法
│  │  │              │
│  │  │              ├─service  业务层
│  │  │              │      TestService.java    测试业务
│  │  │              │
│  │  │              └─util 常用方法层
│  │  │                      DateUtil.java  日期方法
│  │  │
│  │  └─resources
│  │          application.properties    配置文件
│  │          logback-spring.xml    日志配置
```