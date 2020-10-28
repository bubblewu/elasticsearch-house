package com.bubble.house.config;

import com.bubble.house.config.security.AuthFilter;
import com.bubble.house.config.security.AuthProvider;
import com.bubble.house.config.security.LoginAuthFailHandler;
import com.bubble.house.config.security.LoginUrlEntryPoint;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

/**
 * Web安全认证
 *
 * @author wugang
 * date: 2019-11-06 10:38
 **/
@EnableWebSecurity
// 启用方法安全设置
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {

    /**
     * HTTP权限控制
     */
    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.addFilterBefore(authFilter(), UsernamePasswordAuthenticationFilter.class);
        // 资源访问权限
        http.authorizeRequests()
                // permitAll不需要验证, 管理员登录入口
                .antMatchers("/admin/login").permitAll()
                // 静态资源
                .antMatchers("/static/**").permitAll()
                // 用户登录入口
                .antMatchers("/user/login").permitAll()
                // 需要相应的角色才能访问
                .antMatchers("/admin/**").hasRole("ADMIN")
                .antMatchers("/user/**").hasAnyRole("ADMIN", "USER")
                .antMatchers("/api/user/**").hasAnyRole("ADMIN", "USER")
                .and()
                // 基于Form表单登录验证
                .formLogin()
                // 配置角色登录处理入口
                .loginProcessingUrl("/login")
                // 登录验证失败处理器
                .failureHandler(authFailHandler())
                .and()
                // 登出注销配置
                .logout().logoutUrl("/logout").logoutSuccessUrl("/logout/page")
                // 登出后删除session，使会话失效
                .deleteCookies("JSESSIONID").invalidateHttpSession(true)
                .and()
                // 路由Mapping后的跳转登录入口
                .exceptionHandling()
                .authenticationEntryPoint(urlEntryPoint())
                // 无权访问的提示页面
                .accessDeniedPage("/status/403");

        http.csrf().disable();
        http.headers().frameOptions().sameOrigin();
    }

    /**
     * 自定义认证策略
     */
    @Autowired
    public void configGlobal(AuthenticationManagerBuilder auth) throws Exception {
        auth.authenticationProvider(authProvider()).eraseCredentials(true);
    }

    @Bean
    public AuthProvider authProvider() {
        return new AuthProvider();
    }

    /**
     * 根据访问路径来进行路由Mapping后的跳转登录入口
     */
    @Bean
    public LoginUrlEntryPoint urlEntryPoint() {
        // 默认走用户的登录入口
        return new LoginUrlEntryPoint("/user/login");
    }

    /**
     * 登录验证失败处理器
     */
    @Bean
    public LoginAuthFailHandler authFailHandler() {
        return new LoginAuthFailHandler(urlEntryPoint());
    }

    @Bean
    @Override
    public AuthenticationManager authenticationManager() {
        AuthenticationManager authenticationManager = null;
        try {
            authenticationManager = super.authenticationManager();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return authenticationManager;
    }

    @Bean
    public AuthFilter authFilter() {
        AuthFilter authFilter = new AuthFilter();
        authFilter.setAuthenticationManager(authenticationManager());
        authFilter.setAuthenticationFailureHandler(authFailHandler());
        return authFilter;
    }

}
