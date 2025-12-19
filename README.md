### 一个springboot项目的新手demo

### 环境
```$xslt
环境：window11 + jdk8
开发软件：idea
连接中间件：kafka
```

### index示例
```$xslt

``` 

### 功能说明
```$xslt
kafka简单功能
```
### application.yml 
```$xslt
编码：GBK
``` 

### 进阶功能
```angular2html
· kafka定时发送信息 - 不区分 Kafka分区
· kafka定时发送信息 - 区分 Kafka分区
· 不区分 Kafka分区 消费
· 手动提交ACK
· 区分 Kafka分区 消费
```


### 定时任务讲解
```angular2html
1. 添加定时任务开启开关 ： @EnableScheduling
2. 创建定时任务 SendKafkaDataScheduled
```

### 踩坑史
```angular2html
当手动提交ack和不手动提交ack需要写在一套代码时，不可以用默认的spring.kafka配置，这样会异常。
```


### 优化方案
```angular2html
方案1： 手搓KafkaConfig，为多套Kafka集群、多种配置做准备，减少测试工作量，但是会增加代码量
方案2： 所有的消费都转为手动提交

此分支采用方案1
```