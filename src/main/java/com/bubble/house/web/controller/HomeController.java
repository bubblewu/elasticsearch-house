package com.bubble.house.web.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * 首页
 *
 * @author wugang
 * date: 2019-11-04 14:45
 **/
@Controller
public class HomeController {

    @GetMapping(value = {"/", "/index"})
    public String index(Model model) {
        // 动态加载：指定变量数据
//        model.addAttribute("name", "Bubble");
//        System.out.println("打开index...");
        return "index";
    }

//    @GetMapping(value = "test/status")
//    @ResponseBody
//    public ApiResponse get() {
//        return ApiResponse.ofMessage(200, "请求成功");
//    }

    @GetMapping("/404")
    public String notFoundPage() {
        return "404";
    }

    @GetMapping("/403")
    public String accessError() {
        return "403";
    }

    @GetMapping("/500")
    public String internalError() {
        return "500";
    }

    @GetMapping("/logout/page")
    public String logoutPage() {
        return "logout";  // 登出页面
    }


}
