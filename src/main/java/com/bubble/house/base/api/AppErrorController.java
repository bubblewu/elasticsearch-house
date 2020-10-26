package com.bubble.house.base.api;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.servlet.error.ErrorAttributes;
import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.context.request.ServletWebRequest;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Map;

/**
 * 自定义页面拦截器：Web错误全局配置
 * 用于拦截项目运行中的无法预知的情况，通过统一拦截器进行异常拦截。
 * - 页面异常拦截器：
 * - API异常拦截器：
 * <p>
 * 不让Springboot自动生成whitelabel页面，由我们自定义实现
 *
 * @author wugang
 * date: 2019-11-04 17:12
 **/
// 注意：注解使用@RestController在方法上返回的是string类型的，返回的页面不会跳转
//@RestController
@Controller
public class AppErrorController implements ErrorController {
    private static final String ERROR_PATH = "/error";

    @Value("${server.error.path}")
    private String errorPath;

    private final ErrorAttributes errorAttributes;

    @Autowired
    public AppErrorController(ErrorAttributes errorAttributes) {
        this.errorAttributes = errorAttributes;
    }

    @Override
    public String getErrorPath() {
        return ERROR_PATH;
    }

    /**
     * Web页面错误处理
     */
    @RequestMapping(value = ERROR_PATH, produces = "text/html")
    public String errorPageHandler(HttpServletRequest request, HttpServletResponse response) {
        int status = response.getStatus();
        switch (status) {
            /*
             * 返回状态码拦截处理
             */
            case 403:
                return "status/403";
            case 404:
                return "status/404";
            case 500:
                return "status/500";
            default:
                return "index";
        }
    }

    /**
     * API接口错误处理：除了Web页面之外的错误处理，如Json、Xml等
     */
    @RequestMapping(value = ERROR_PATH)
    @ResponseBody
    public ApiResponse errorApiHandler(HttpServletRequest request) {
        ServletWebRequest servletWebRequest = new ServletWebRequest(request);
        Map<String, Object> attr = this.errorAttributes.getErrorAttributes(servletWebRequest, false);
        int status = getStatus(request);
        return ApiResponse.ofMessage(status, String.valueOf(attr.getOrDefault("message", "error")));
    }

    private int getStatus(HttpServletRequest request) {
        Integer status = (Integer) request.getAttribute("javax.servlet.error.status_code");
        if (status != null) {
            return status;
        }
        return 500;
    }

}
