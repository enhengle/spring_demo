### 一个springboot项目的新手demo

### 环境
```$xslt
环境搭建：jdk8 + window11
开发软件：idea
orm框架：jpa
数据源：mysql
```

### 功能说明
```$xslt
①可支持数据源切换，需手动配置文件
②完成日志打印，且错误日志与正常日志进行分离、日期分离
③自定义注解
④完成切面打印日志功能
⑥全局错误捕捉
```

### 目录说明
```$xslt
│
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
│  │  │              │      ManagerConfig.java  数据库配置
│  │  │              │      MyConfig.java   跨域配置
│  │  │              │
│  │  │              ├─controller   控制层
│  │  │              │      ViewController.java
│  │  │              │
│  │  │              ├─myExceptionHandler   全局错误捕捉层
│  │  │              │      ServerException.java    自定义错误
│  │  │              │      ServerExceptionHandler.java 全局捕捉
│  │  │              │
│  │  │              ├─pojo 数据库映射层
│  │  │              │  └─manager
│  │  │              │          View.java
│  │  │              │
│  │  │              ├─repository   sql业务层
│  │  │              │  └─manager
│  │  │              │          ViewRepository.java
│  │  │              │
│  │  │              ├─response 返回层
│  │  │              │      EnumCode.java   自定义错误码
│  │  │              │      Response.java   自定义返回结构
│  │  │              │
│  │  │              └─service  业务层
│  │  │                  │  ViewService.java
│  │  │                  │
│  │  │                  └─impl 业务具体操作层
│  │  │                          ViewServiceImpl.java
│  │  │
│  │  └─resources
│  │          application.properties    配置
│  │          logback-spring.xml    日志配置
```