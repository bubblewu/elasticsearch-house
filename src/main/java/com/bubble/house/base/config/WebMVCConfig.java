package com.bubble.house.base.config;

import com.bubble.house.base.config.interceptor.CustomInterceptor;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.*;
import org.thymeleaf.extras.springsecurity5.dialect.SpringSecurityDialect;
import org.thymeleaf.spring5.SpringTemplateEngine;
import org.thymeleaf.spring5.templateresolver.SpringResourceTemplateResolver;
import org.thymeleaf.spring5.view.ThymeleafViewResolver;

/**
 * Web相关的配置类:
 * - thymeleaf模版引擎配置重写WebMvcConfigurer，添加自定义拦截器，消息转换器等
 * ApplicationContextAware接口用来获取Spring的上下文
 *
 * @author wugang
 * date: 2019-11-04 14:22
 **/
@Configuration
@EnableWebMvc
public class WebMVCConfig implements WebMvcConfigurer, ApplicationContextAware {
    @Value("${spring.thymeleaf.cache}")
    private boolean thymeleafCacheEnable;

    private ApplicationContext applicationContext;

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }

    /* --- Thymeleaf模版引擎配置 --- */

    /**
     * 模板资源解析器。
     * ConfigurationProperties 绑定配置文件中thymeleaf的前缀
     */
    @Bean
    @ConfigurationProperties(prefix = "spring.thymeleaf")
    public SpringResourceTemplateResolver templateResolver() {
        SpringResourceTemplateResolver templateResolver = new SpringResourceTemplateResolver();
        // 配置Spring上下文
        templateResolver.setApplicationContext(this.applicationContext);
        // 防止中文乱码
        templateResolver.setCharacterEncoding("UTF-8");
        templateResolver.setCacheable(thymeleafCacheEnable);
        return templateResolver;
    }

    /**
     * Thymeleaf标准方言解释器
     *
     * @return SpringTemplateEngine
     */
    @Bean
    public SpringTemplateEngine templateEngine() {
        SpringTemplateEngine templateEngine = new SpringTemplateEngine();
        templateEngine.setTemplateResolver(templateResolver());
        // 支持Spring EL表达式
        templateEngine.setEnableSpringELCompiler(true);
        // 支持SpringSecurity方言
        SpringSecurityDialect securityDialect = new SpringSecurityDialect();
        templateEngine.addDialect(securityDialect);
        return templateEngine;
    }

    /**
     * Thymeleaf视图解析器
     *
     * @return ThymeleafViewResolver
     */
    @Bean
    public ThymeleafViewResolver viewResolver() {
        ThymeleafViewResolver viewResolver = new ThymeleafViewResolver();
        viewResolver.setTemplateEngine(templateEngine());
        return viewResolver;
    }

    /* --- 静态资源加载配置 --- */

    /**
     * 静态资源加载配置
     */
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // 静态资源处理路径和绝对路径
        registry.addResourceHandler("/static/**")
                .addResourceLocations("classpath:/static/");
    }

    /**
     * 跨域支持：
     * 跨域是指不同域名之间相互访问。
     * 例如：电脑上有2个服务器 192.168.0.11 192.168.0.12
     * 如果第一个服务器上的页面要访问第二个服务，就叫做跨域。
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

//    /**
//     * 消息内容转换配置，配置fastJson返回json转换
//     */
//    @Override
//    public void configureMessageConverters(List<HttpMessageConverter<?>> converters) {
//        // 创建fastJson消息转换器
//        FastJsonHttpMessageConverter fastConverter = new FastJsonHttpMessageConverter();
//        // 创建配置类
//        FastJsonConfig fastJsonConfig = new FastJsonConfig();
//        // 修改配置返回内容的过滤
//        fastJsonConfig.setSerializerFeatures(
//                SerializerFeature.DisableCircularReferenceDetect,
//                SerializerFeature.WriteMapNullValue,
//                SerializerFeature.WriteNullStringAsEmpty
//        );
//        fastJsonConfig.setCharset(Charset.defaultCharset());
//        fastConverter.setFastJsonConfig(fastJsonConfig);
//        // 将fastjson添加到视图消息转换器列表内
//        converters.add(fastConverter);
//        System.out.println("FastJson消息内容转换");
//    }

}
