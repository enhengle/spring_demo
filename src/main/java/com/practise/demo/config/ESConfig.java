package com.practise.demo.config;

import org.apache.http.HttpHost;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestHighLevelClient;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.net.URL;

/**
 * @author lingwang
 * @date 2022/2/19 21:58
 */
@Configuration
public class ESConfig implements InitializingBean {


    @Value("${es.config.cluster-name}")
    private String esClusterName;
    @Value("${es.config.cluster-nodes}")
    private String esClusterNodes;
    @Value("${es.config.username}")
    private String esUserName;
    @Value("${es.config.password}")
    private String esPassword;
    @Value("${es.config.connection-request-timeout}")
    private int esConnectionRequestTimeout;
    @Value("${es.config.connect-timeout}")
    private int esConnectTimeout;
    @Value("${es.config.socket-timeout}")
    private int esSocketTimeout;

    public static String ES_CLUSTER_NAME = "";
    public static String ES_CLUSTER_NODES = "";
    public static String ES_USERNAME = "";
    public static String ES_PASSWORD = "";
    public static int ES_CONNECTION_REQUEST_TIMEOUT = -1;
    public static int ES_CONNECT_TIMEOUT = -1;
    public static int ES_SOCKET_TIMEOUT = -1;

    @Override
    public void afterPropertiesSet() {
        ES_CLUSTER_NAME = esClusterName;
        ES_CLUSTER_NODES = esClusterNodes;
        ES_USERNAME = esUserName;
        ES_PASSWORD = esPassword;
        ES_CONNECTION_REQUEST_TIMEOUT = esConnectionRequestTimeout;
        ES_CONNECT_TIMEOUT = esConnectTimeout;
        ES_SOCKET_TIMEOUT = esSocketTimeout;
    }

    @Bean(name = "esRestHighLevelClient")
    public RestHighLevelClient restHighLevelClient()  {
        String[] urlList = ESConfig.ES_CLUSTER_NODES.split(",");
        HttpHost[] nodes = new HttpHost[urlList.length];
        try {
            for (int i = 0; i < urlList.length; i++) {
                URL url = new URL(urlList[i]);
                HttpHost node = new HttpHost(url.getHost(), url.getPort(), url.getProtocol());
                nodes[i] = node;
            }
        }catch (Exception e){
            System.out.println("初始化失败");
        }
        final CredentialsProvider credentialsProvider = new BasicCredentialsProvider();
        credentialsProvider.setCredentials(AuthScope.ANY, new UsernamePasswordCredentials(ESConfig.ES_USERNAME, ESConfig.ES_PASSWORD));

        return new RestHighLevelClient(RestClient.builder(nodes)
                .setRequestConfigCallback(
                        requestConfigBuilder -> {
                            requestConfigBuilder.setConnectTimeout(ESConfig.ES_CONNECT_TIMEOUT);
                            requestConfigBuilder.setSocketTimeout(ESConfig.ES_SOCKET_TIMEOUT);
                            requestConfigBuilder.setConnectionRequestTimeout(ESConfig.ES_CONNECTION_REQUEST_TIMEOUT);
                            return requestConfigBuilder;
                        }
                ).setHttpClientConfigCallback(
                        httpAsyncClientBuilder -> httpAsyncClientBuilder.setDefaultCredentialsProvider(credentialsProvider)
                )
        );

    }

}
