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
        System.out.println("打开index...");
        return "index";
    }

}
