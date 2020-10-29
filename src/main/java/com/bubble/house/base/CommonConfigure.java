package com.bubble.house.base;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * 统一配置：用于实例化并自动配置所需的工具
 *
 * @author wugang
 * date: 2020-10-28 18:08
 **/
@Configuration
public class CommonConfigure {

    /**
     * Gson序列化和反序列化（替换fastjson）
     */
    @Bean
    public Gson gson() {
        return new Gson();
    }

    /**
     * jackson序列化和反序列化
     *
     * @return ObjectMapper
     */
    @Bean
    public ObjectMapper objectMapper() {
        return new ObjectMapper();
    }

}
