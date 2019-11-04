package com.bubble.house.config;

import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * 自定义拦截器
 *
 * @author wugang
 * date: 2019-11-04 15:23
 **/
public class CustomInterceptor extends HandlerInterceptorAdapter {

    /**
     * preHandle：
     * 调用时间：Controller方法处理之前
     * 执行顺序：链式Interceptor情况下，Interceptor按照声明的顺序一个接一个执行
     * 若返回false，则中断执行，注意：不会进入afterCompletion
     */
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        System.out.println("Interceptor preHandler method is running !");
        return super.preHandle(request, response, handler);
    }

    /**
     * postHandle：
     * 调用前提：preHandle返回true
     * 调用时间：Controller方法处理完之后，DispatcherServlet进行视图的渲染之前，也就是说在这个方法中你可以对ModelAndView进行操作
     * 执行顺序：链式Interceptor情况下，Intercepto
     * 备注：postHandle虽然post打头，但post、get方法都能处理
     */
    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {
        System.out.println("Interceptor postHandler method is running !");
        super.postHandle(request, response, handler, modelAndView);
    }

    /**
     * afterCompletion：
     * 调用前提：preHandle返回true
     * 调用时间：DispatcherServlet进行视图的渲染之后
     * 多用于清理资源
     */
    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
        System.out.println("Interceptor afterCompletion method is running !");
        super.afterCompletion(request, response, handler, ex);
    }

    /**
     * 该方法用于处理异步请求，当请求的是异步方法的时候会触发该方法时，
     * 异步请求先运行 preHandle，接着运行 afterConcurrentHandlingStarted。
     * 而Interceptor的postHandle方法则是需要等到Controller的异步执行完才能执行
     */
    @Override
    public void afterConcurrentHandlingStarted(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        System.out.println("Interceptor afterConcurrentHandlingStarted method is running !");
        super.afterConcurrentHandlingStarted(request, response, handler);
    }

}
