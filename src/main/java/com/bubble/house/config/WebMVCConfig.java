package com.bubble.house.config;

import com.alibaba.fastjson.serializer.SerializerFeature;
import com.alibaba.fastjson.support.config.FastJsonConfig;
import com.alibaba.fastjson.support.spring.FastJsonHttpMessageConverter;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.web.servlet.config.annotation.*;

import java.util.List;

/**
 * Web相关的配置类:
 * - thymeleaf模版引擎配置重写WebMvcConfigurer，添加自定义拦截器，消息转换器等
 *
 * @author wugang
 * date: 2019-11-04 14:22
 **/
@Configuration
@EnableWebMvc
public class WebMVCConfig implements WebMvcConfigurer {

    /**
     * 静态资源加载配置
     */
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/static/**").addResourceLocations("classpath:/static/");
    }

    /**
     * 跨域支持：
     * 跨域是指不同域名之间相互访问。
     * 例如：电脑上有2个服务器 192.168.0.11 192.168.0.12
     *      如果第一个服务器上的页面要访问第二个服务，就叫做跨域。
     */
    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
                .allowedOrigins("*")
                .allowCredentials(true)
                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
                .maxAge(3600);
    }

    /**
     * InterceptorRegistry内的addInterceptor需要一个实现HandlerInterceptor接口的拦截器实例，
     * addPathPatterns方法用于设置拦截器的过滤路径规则。
     */
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(new CustomInterceptor()).addPathPatterns("/**");
    }

    /**
     * 视图控制器配置
     */
    @Override
    public void addViewControllers(ViewControllerRegistry registry) {
        registry.addViewController("/").setViewName("/index");
    }

    /**
     * 消息内容转换配置，配置fastJson返回json转换
     */
    @Override
    public void configureMessageConverters(List<HttpMessageConverter<?>> converters) {
        // 创建fastJson消息转换器
        FastJsonHttpMessageConverter fastConverter = new FastJsonHttpMessageConverter();
        // 创建配置类
        FastJsonConfig fastJsonConfig = new FastJsonConfig();
        // 修改配置返回内容的过滤
        fastJsonConfig.setSerializerFeatures(
                SerializerFeature.DisableCircularReferenceDetect,
                SerializerFeature.WriteMapNullValue,
                SerializerFeature.WriteNullStringAsEmpty
        );
        fastConverter.setFastJsonConfig(fastJsonConfig);
        // 将fastjson添加到视图消息转换器列表内
        converters.add(fastConverter);
    }


}
