package com.bubble.house.web.controller;

import com.bubble.house.base.api.ApiResponse;
import com.bubble.house.base.api.ApiStatus;
import com.bubble.house.entity.MultiResultEntity;
import com.bubble.house.entity.house.CityEntity;
import com.bubble.house.service.house.AddressService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * 房屋相关接口
 *
 * @author wugang
 * date: 2019-11-05 16:15
 **/
@Controller
public class HouseController {

    private final AddressService addressService;

    public HouseController(AddressService addressService) {
        this.addressService = addressService;
    }

    /**
     * 获取城市支持列表
     */
    @GetMapping("address/support/cities")
    @ResponseBody
    public ApiResponse getSupportCity() {
        MultiResultEntity<CityEntity> result = addressService.findAllCities();
        if (result.getResultSize() == 0) {
            return ApiResponse.ofStatus(ApiStatus.NOT_FOUND);
        }
        return ApiResponse.ofSuccess(result.getResult());
    }


}
