package com.practise.demo.config;
//
//import io.lettuce.core.ClientOptions;
//import io.lettuce.core.ReadFrom;
//import io.lettuce.core.SocketOptions;
//import io.lettuce.core.TimeoutOptions;
//import io.lettuce.core.cluster.ClusterClientOptions;
//import io.lettuce.core.cluster.ClusterTopologyRefreshOptions;
//import io.lettuce.core.resource.ClientResources;
//import io.lettuce.core.resource.DefaultClientResources;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.beans.factory.annotation.Value;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.data.redis.connection.lettuce.LettuceClientConfiguration;
//import org.springframework.data.redis.connection.lettuce.MutableLettuceClientConfiguration;
//
//import java.time.Duration;
//
///**
// * Redis连接配置增强类
// * 提供重连、重试、心跳等高级配置
// */
//@Slf4j
//@Configuration
//public class RedisConnectionConfig {
//
//    @Value("${redis.connection.auto-reconnect:true}")
//    private boolean autoReconnect;
//
//    @Value("${redis.connection.cancel-commands-on-reconnect:true}")
//    private boolean cancelCommandsOnReconnect;
//
//    @Value("${redis.connection.suspend-reconnect-on-protocol-failure:false}")
//    private boolean suspendReconnectOnProtocolFailure;
//
//    @Value("${redis.connection.heartbeat-interval:30}")
//    private int heartbeatInterval;
//
//    @Value("${redis.connection.connection-timeout:5000}")
//    private int connectionTimeout;
//
//    @Value("${redis.connection.socket-timeout:3000}")
//    private int socketTimeout;
//
//    @Value("${redis.connection.max-retries:3}")
//    private int maxRetries;
//
//    @Value("${redis.connection.retry-delay:1000}")
//    private int retryDelay;
//
//    /**
//     * 单机Redis客户端选项
//     */
//    @Bean("singleRedisClientOptions")
//    public ClientOptions singleRedisClientOptions() {
//        log.info("配置单机Redis客户端选项 - 自动重连: {}, 心跳间隔: {}s", autoReconnect, heartbeatInterval);
//
//        ClientOptions build = ClientOptions.builder()
//                .autoReconnect(autoReconnect)
//                .cancelCommandsOnReconnectFailure(cancelCommandsOnReconnect)
//                .suspendReconnectOnProtocolFailure(suspendReconnectOnProtocolFailure)
//                .socketOptions(socketOptions())
//                .timeoutOptions(timeoutOptions())
//                .build();
//        return build;
//    }
//
//
//    /**
//     * 客户端资源配置
//     */
//    @Bean("redisClientResources")
//    public ClientResources redisClientResources() {
//        log.info("配置Redis客户端资源 - 最大重试次数: {}, 重试延迟: {}ms", maxRetries, retryDelay);
//
//        return DefaultClientResources.builder()
//                .ioThreadPoolSize(Runtime.getRuntime().availableProcessors())
//                .computationThreadPoolSize(Runtime.getRuntime().availableProcessors())
//                .reconnectDelay(Delay.of(Duration.ofMillis(retryDelay)))
//                .build();
//    }
//
//    /**
//     * Socket选项配置
//     */
//    private SocketOptions socketOptions() {
//        return SocketOptions.builder()
//                .connectTimeout(Duration.ofMillis(connectionTimeout))
//                .keepAlive(true)
//                .tcpNoDelay(true)
//                .build();
//    }
//
//    /**
//     * 超时选项配置
//     */
//    private TimeoutOptions timeoutOptions() {
//        return TimeoutOptions.builder()
//                .fixedTimeout(Duration.ofMillis(socketTimeout))
//                .build();
//    }
//
//    /**
//     * 单机Redis连接配置
//     * 使用MutableLettuceClientConfiguration解决类型不匹配问题
//     */
//    @Bean("singleRedisClientConfiguration")
//    public LettuceClientConfiguration singleRedisClientConfiguration() {
//        log.info("配置单机Redis客户端配置");
//
//        // 使用MutableLettuceClientConfiguration来避免类型不匹配问题
//        MutableLettuceClientConfiguration configuration = new MutableLettuceClientConfiguration();
//        configuration.setClientOptions(singleRedisClientOptions());
//        configuration.setClientResources(redisClientResources());
//        configuration.setReadFrom(ReadFrom.MASTER_PREFERRED);
//
//        return configuration;
//    }
//
//
//    /**
//     * 延迟策略实现
//     */
//    private static class Delay extends io.lettuce.core.resource.Delay {
//        private final Duration duration;
//
//        private Delay(Duration duration) {
//            this.duration = duration;
//        }
//
//        public static Delay of(Duration duration) {
//            return new Delay(duration);
//        }
//
//        @Override
//        public Duration createDelay(long attempt) {
//            return duration;
//        }
//    }
//}