package com.bubble.house.web.controller;

import com.bubble.house.base.api.ApiResponse;
import com.bubble.house.base.api.ApiStatus;
import com.bubble.house.entity.dto.HouseDTO;
import com.bubble.house.entity.param.RentSearchParam;
import com.bubble.house.entity.result.MultiResultEntity;
import com.bubble.house.entity.house.CityEntity;
import com.bubble.house.entity.house.SubwayEntity;
import com.bubble.house.entity.house.SubwayStationEntity;
import com.bubble.house.entity.result.ResultEntity;
import com.bubble.house.entity.search.RentValueBlockEntity;
import com.bubble.house.service.house.AddressService;
import com.bubble.house.service.house.HouseService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpSession;
import java.util.List;

/**
 * 房屋相关接口
 *
 * @author wugang
 * date: 2019-11-05 16:15
 **/
@Controller
public class HouseController {

    private final AddressService addressService;
    private final HouseService houseService;

    public HouseController(AddressService addressService, HouseService houseService) {
        this.addressService = addressService;
        this.houseService = houseService;
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

    /**
     * 获取对应城市支持区域列表
     */
    @GetMapping("address/support/regions")
    @ResponseBody
    public ApiResponse getSupportRegions(@RequestParam(name = "city_name") String cityEnName) {
        MultiResultEntity<CityEntity> addressResult = addressService.findAllRegionsByCityEnName(cityEnName);
        if (addressResult.getResult() == null || addressResult.getTotal() < 1) {
            return ApiResponse.ofStatus(ApiStatus.NOT_FOUND);
        }
        return ApiResponse.ofSuccess(addressResult.getResult());
    }

    /**
     * 获取具体城市所支持的地铁线路
     */
    @GetMapping("address/support/subway/line")
    @ResponseBody
    public ApiResponse getSupportSubwayLine(@RequestParam(name = "city_name") String cityEnName) {
        List<SubwayEntity> subways = addressService.findAllSubwayByCityEnName(cityEnName);
        if (subways.isEmpty()) {
            return ApiResponse.ofStatus(ApiStatus.NOT_FOUND);
        }
        return ApiResponse.ofSuccess(subways);
    }

    /**
     * 获取对应地铁线路所支持的地铁站点
     */
    @GetMapping("address/support/subway/station")
    @ResponseBody
    public ApiResponse getSupportSubwayStation(@RequestParam(name = "subway_id") Long subwayId) {
        List<SubwayStationEntity> stationDTOS = addressService.findAllStationBySubway(subwayId);
        if (stationDTOS.isEmpty()) {
            return ApiResponse.ofStatus(ApiStatus.NOT_FOUND);
        }
        return ApiResponse.ofSuccess(stationDTOS);
    }


    @GetMapping("rent/house")
    public String rentHousePage(@ModelAttribute RentSearchParam rentSearch,
                                Model model, HttpSession session,
                                RedirectAttributes redirectAttributes) {
        if (rentSearch.getCityEnName() == null) {
            String cityEnNameInSession = (String) session.getAttribute("cityEnName");
            if (cityEnNameInSession == null) {
                redirectAttributes.addAttribute("msg", "must_chose_city");
                return "redirect:/index";
            } else {
                rentSearch.setCityEnName(cityEnNameInSession);
            }
        } else {
            session.setAttribute("cityEnName", rentSearch.getCityEnName());
        }
        // 获取城市信息
        ResultEntity<CityEntity> city = addressService.findCity(rentSearch.getCityEnName());
        if (!city.isSuccess()) {
            redirectAttributes.addAttribute("msg", "must_chose_city");
            // 跳转到index.html页面
            return "redirect:/index";
        }
        model.addAttribute("currentCity", city.getResult());

        MultiResultEntity<CityEntity> addressResult = addressService.findAllRegionsByCityEnName(rentSearch.getCityEnName());
        if (addressResult.getResult() == null || addressResult.getTotal() < 1) {
            redirectAttributes.addAttribute("msg", "must_chose_city");
            return "redirect:/index";
        }

        MultiResultEntity<HouseDTO> serviceMultiResult = houseService.query(rentSearch);

        // 添加视图信息
        model.addAttribute("total", serviceMultiResult.getTotal());
        model.addAttribute("houses", serviceMultiResult.getResult());

        if (rentSearch.getRegionEnName() == null) {
            rentSearch.setRegionEnName("*"); // 匹配所有区域
        }

        model.addAttribute("searchBody", rentSearch);
        model.addAttribute("regions", addressResult.getResult());

        model.addAttribute("priceBlocks", RentValueBlockEntity.PRICE_BLOCK);
        model.addAttribute("areaBlocks", RentValueBlockEntity.AREA_BLOCK);

        model.addAttribute("currentPriceBlock", RentValueBlockEntity.matchPrice(rentSearch.getPriceBlock()));
        model.addAttribute("currentAreaBlock", RentValueBlockEntity.matchArea(rentSearch.getAreaBlock()));

        return "rent-list";
    }

}
