### 一个springboot项目的新手demo

### 环境
```$xslt
环境搭建：jdk8 + window11
开发软件：idea
orm框架：mybatis-plus
数据源：mysql
```

### 功能说明
```$xslt
①完成数据源切换(只有mysql)
②完成分页功能
③完成日志打印，且错误日志与正常日志进行分离、日期分离
④自定义注解
⑤完成切面打印日志功能
⑥全局错误捕捉
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
│  │  │              ├─aspect   切面层
│  │  │              │      LogPrintfAscpect.java   切面进行日志打印
│  │  │              │
│  │  │              ├─common   注解层
│  │  │              │      LogPrint.java   自定义日志注解
│  │  │              │
│  │  │              ├─config   配置层
│  │  │              │      MybatisPlusConfig.java  mybatis-plus分页配置
│  │  │              │      MyConfig.java   跨域配置
│  │  │              │
│  │  │              ├─controller   控制层
│  │  │              │      StatController.java
│  │  │              │      ViewController.java
│  │  │              │
│  │  │              ├─mapper   sql业务层
│  │  │              │  ├─manager
│  │  │              │  │      ViewMapper.java
│  │  │              │  │
│  │  │              │  └─stat
│  │  │              │          LiveViewMapper.java
│  │  │              │
│  │  │              ├─myExceptionHandler   全局错误捕捉层
│  │  │              │      ServerException.java    自定义错误
│  │  │              │      ServerExceptionHandler.java 全局捕捉
│  │  │              │
│  │  │              ├─pojo 映射层
│  │  │              │  ├─manager
│  │  │              │  │      View.java
│  │  │              │  │
│  │  │              │  └─stat
│  │  │              │          LiveView.java
│  │  │              │
│  │  │              ├─response 返回层
│  │  │              │      EnumCode.java   自定义错误码
│  │  │              │      Response.java   自定义返回结构
│  │  │              │
│  │  │              └─service  业务层
│  │  │                  │  LiveViewService.java
│  │  │                  │  ViewService.java
│  │  │                  │
│  │  │                  └─impl 业务具体操作层
│  │  │                          LiveViewServiceImpl.java
│  │  │                          ViewServiceImpl.java
│  │  │
│  │  └─resources
│  │          application.properties    配置
│  │          logback-spring.xml    日志配置
```