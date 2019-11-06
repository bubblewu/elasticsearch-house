package com.bubble.house.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.session.data.redis.config.annotation.web.http.EnableRedisHttpSession;

/**
 * Redis进行session会话配置
 *
 * @author wugang
 * date: 2019-11-06 15:43
 **/
@Configuration
// maxInactiveIntervalInSeconds 默认是1800秒过期，设置为2H
@EnableRedisHttpSession(maxInactiveIntervalInSeconds = 2 * 60 * 60)
public class RedisSessionConfig {

    @Bean
    public RedisTemplate<String, String> redisTemplate(RedisConnectionFactory factory) {
        return new StringRedisTemplate(factory);
    }

}
