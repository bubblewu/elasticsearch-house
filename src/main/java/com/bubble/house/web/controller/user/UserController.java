package com.bubble.house.web.controller.user;

import com.bubble.house.base.LoginUserUtil;
import com.bubble.house.base.api.ApiResponse;
import com.bubble.house.base.api.ApiStatus;
import com.bubble.house.entity.result.ServiceResultEntity;
import com.bubble.house.service.house.HouseService;
import com.bubble.house.service.house.SubscribeService;
import com.bubble.house.service.user.UserService;
import org.apache.http.HttpStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * 角色为User：一般用户的处理逻辑接口
 *
 * @author wugang
 * date: 2020-10-27 17:48
 **/
@Controller
public class UserController {
    private final Logger logger = LoggerFactory.getLogger(UserController.class);

    private final UserService userService;
    private final HouseService houseService;
    private final SubscribeService subscribeService;

    public UserController(UserService userService, HouseService houseService, SubscribeService subscribeService) {
        this.userService = userService;
        this.houseService = houseService;
        this.subscribeService = subscribeService;
    }

    /**
     * 普通用户登录页面
     */
    @GetMapping("/user/login")
    public String loginPage() {
        return "user/login";
    }

    /**
     * 普通用户管理页面
     */
    @GetMapping("/user/center")
    public String centerPage() {
        return "user/center";
    }

    @PostMapping(value = "/api/user/info")
    @ResponseBody
    public ApiResponse updateUserInfo(@RequestParam(value = "profile") String profile, @RequestParam(value = "value") String value) {
        if (value.isEmpty()) {
            return ApiResponse.ofStatus(ApiStatus.BAD_REQUEST);
        }
        if ("email".equals(profile) && !LoginUserUtil.checkEmail(value)) {
            return ApiResponse.ofMessage(HttpStatus.SC_BAD_REQUEST, "不支持的邮箱格式");
        }
        ServiceResultEntity result = userService.modifyUserProfile(profile, value);
        if (result.isSuccess()) {
            return ApiResponse.ofSuccess("");
        } else {
            return ApiResponse.ofMessage(HttpStatus.SC_BAD_REQUEST, result.getMessage());
        }
    }

    /**
     * 加入预约清单
     *
     * @param houseId 房源ID
     * @return ApiResponse
     */
    @PostMapping(value = "api/user/house/subscribe")
    @ResponseBody
    public ApiResponse subscribeHouse(@RequestParam(value = "house_id") Long houseId) {
        ServiceResultEntity result = subscribeService.addSubscribeOrder(houseId);
        if (result.isSuccess()) {
            return ApiResponse.ofSuccess("");
        } else {
            return ApiResponse.ofMessage(HttpStatus.SC_BAD_REQUEST, result.getMessage());
        }
    }

}
