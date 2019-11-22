package com.bubble.house.config;

import org.apache.http.HttpHost;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestClientBuilder;
import org.elasticsearch.client.RestHighLevelClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Elasticsearch配置
 *
 * @author wugang
 * date: 2019-11-11 18:43
 **/
@Configuration
public class ESConfig {
    @Value("${elasticsearch.host}")
    private String host;
    @Value("${elasticsearch.port}")
    private int port;
    @Value("${elasticsearch.scheme}")
    public String scheme;

    @Bean
    public RestClientBuilder restClientBuilder() {
        return RestClient.builder(new HttpHost(host, port, scheme));
    }

    // 及时关闭资源
    @Bean(destroyMethod = "close")
    public RestHighLevelClient restHighLevelClient(@Autowired RestClientBuilder restClientBuilder) {
        return new RestHighLevelClient(restClientBuilder);
    }

}
