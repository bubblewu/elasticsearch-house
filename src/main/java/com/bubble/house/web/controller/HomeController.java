package com.bubble.house.web.controller;

import com.bubble.house.base.api.ApiResponse;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;

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
        model.addAttribute("name", "Bubble");
        System.out.println("打开index...");
        return "index";
    }

    @GetMapping(value = "test/status")
    @ResponseBody
    public ApiResponse get() {
        return ApiResponse.ofMessage(200, "请求成功");
    }

}
