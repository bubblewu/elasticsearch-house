package com.bubble.house.web.controller;

import com.alibaba.fastjson.JSONObject;
import com.bubble.house.base.api.ApiResponse;
import com.bubble.house.base.api.ApiStatus;
import com.bubble.house.entity.QiNiuEntity;
import com.bubble.house.service.house.QiNiuService;
import com.qiniu.http.Response;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;

/**
 * 后台管理中心
 *
 * @author wugang
 * date: 2019-11-05 11:32
 **/
@Controller
public class AdminController {

    @Value("${spring.http.multipart.location}")
    private String imageLocation;

    private final QiNiuService qiNiuService;

    public AdminController(QiNiuService qiNiuService) {
        this.qiNiuService = qiNiuService;
    }

    /**
     * 后台管理中心
     */
    @GetMapping("/admin/center")
    public String adminCenterPage() {
        return "admin/center";
    }

    /**
     * 欢迎页
     */
    @GetMapping("/admin/welcome")
    public String welcomePage() {
        return "admin/welcome";
    }

    /**
     * 管理员登录页
     */
    @GetMapping("/admin/login")
    public String adminLoginPage() {
        return "admin/login";
    }

    /**
     * 房源列表页
     */
    @GetMapping("admin/house/list")
    public String houseListPage() {
        return "admin/house-list";
    }

    /**
     * 新增房源功能页
     */
    @GetMapping("admin/add/house")
    public String addHousePage() {
        return "admin/house-add";
    }

    /**
     * 上传图片接口：本地或七牛云
     */
    @PostMapping(value = "admin/upload/photo", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @ResponseBody
    public ApiResponse uploadPhoto(@RequestParam("file") MultipartFile file) {
        if (file.isEmpty()) {
            return ApiResponse.ofStatus(ApiStatus.NOT_VALID_PARAM);
        }
        try {
//            // 上传到本地
//            String fileName = file.getOriginalFilename();
//            File target = new File(imageLocation + fileName);
//            file.transferTo(target);
//            return ApiResponse.ofStatus(ApiStatus.SUCCESS);

            // 上传到七牛云服务器
            InputStream inputStream = file.getInputStream();
            Response response = qiNiuService.uploadFile(inputStream);
            if (response.isOK()) {
                QiNiuEntity qiNiuEntity = JSONObject.parseObject(response.bodyString(), QiNiuEntity.class);
                return ApiResponse.ofSuccess(qiNiuEntity);
            } else {
                return ApiResponse.ofMessage(response.statusCode, response.getInfo());
            }
        } catch (IOException e) {
            return ApiResponse.ofStatus(ApiStatus.INTERNAL_SERVER_ERROR);
        }
    }

}
