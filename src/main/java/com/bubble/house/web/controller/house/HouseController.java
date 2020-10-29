package com.bubble.house.web.controller.house;

import com.bubble.house.base.api.ApiResponse;
import com.bubble.house.base.api.ApiStatus;
import com.bubble.house.entity.house.CityLevel;
import com.bubble.house.entity.search.HouseBucketEntity;
import com.bubble.house.entity.search.RentValueBlockEntity;
import com.bubble.house.service.ServiceMultiResultEntity;
import com.bubble.house.service.ServiceResultEntity;
import com.bubble.house.service.house.CityService;
import com.bubble.house.service.house.HouseService;
import com.bubble.house.service.house.MapService;
import com.bubble.house.service.search.SearchService;
import com.bubble.house.service.user.UserService;
import com.bubble.house.web.dto.house.CityDTO;
import com.bubble.house.web.dto.house.HouseDTO;
import com.bubble.house.web.dto.house.SubwayDTO;
import com.bubble.house.web.dto.house.SubwayStationDTO;
import com.bubble.house.web.dto.user.UserDTO;
import com.bubble.house.web.param.MapSearchParam;
import com.bubble.house.web.param.RentSearchParam;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpSession;
import java.util.List;
import java.util.Map;

/**
 * 房屋相关接口
 *
 * @author wugang
 * date: 2019-11-05 16:15
 **/
@Controller
public class HouseController {

    private final CityService cityService;
    private final HouseService houseService;
    private final MapService mapService;
    private final UserService userService;
    private final SearchService searchService;

    public HouseController(CityService cityService, HouseService houseService,
                           UserService userService, SearchService searchService,
                           MapService mapService) {
        this.cityService = cityService;
        this.houseService = houseService;
        this.userService = userService;
        this.searchService = searchService;
        this.mapService = mapService;
    }

    /**
     * 获取城市支持列表
     */
    @GetMapping("address/support/cities")
    @ResponseBody
    public ApiResponse getSupportCity() {
        ServiceMultiResultEntity<CityDTO> result = cityService.findAllCities();
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
        ServiceMultiResultEntity<CityDTO> addressResult = cityService.findAllRegionsByCityEnName(cityEnName);
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
        List<SubwayDTO> subways = cityService.findAllSubwayByCityEnName(cityEnName);
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
        List<SubwayStationDTO> stationDTOS = cityService.findAllStationBySubway(subwayId);
        if (stationDTOS.isEmpty()) {
            return ApiResponse.ofStatus(ApiStatus.NOT_FOUND);
        }
        return ApiResponse.ofSuccess(stationDTOS);
    }


    /**
     * 房源信息浏览排序
     */
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
        ServiceResultEntity<CityDTO> city = cityService.findCity(rentSearch.getCityEnName());
        if (!city.isSuccess()) {
            redirectAttributes.addAttribute("msg", "must_chose_city");
            // 跳转到index.html页面
            return "redirect:/index";
        }
        model.addAttribute("currentCity", city.getResult());

        ServiceMultiResultEntity<CityDTO> addressResult = cityService.findAllRegionsByCityEnName(rentSearch.getCityEnName());
        if (addressResult.getResult() == null || addressResult.getTotal() < 1) {
            redirectAttributes.addAttribute("msg", "must_chose_city");
            return "redirect:/index";
        }

        ServiceMultiResultEntity<HouseDTO> serviceMultiResult = houseService.query(rentSearch);

        // 添加视图信息
        model.addAttribute("total", serviceMultiResult.getTotal());
        model.addAttribute("houses", serviceMultiResult.getResult());

        if (rentSearch.getRegionEnName() == null) {
            // 匹配所有区域
            rentSearch.setRegionEnName("*");
        }

        model.addAttribute("searchBody", rentSearch);
        model.addAttribute("regions", addressResult.getResult());

        model.addAttribute("priceBlocks", RentValueBlockEntity.PRICE_BLOCK);
        model.addAttribute("areaBlocks", RentValueBlockEntity.AREA_BLOCK);

        model.addAttribute("currentPriceBlock", RentValueBlockEntity.matchPrice(rentSearch.getPriceBlock()));
        model.addAttribute("currentAreaBlock", RentValueBlockEntity.matchArea(rentSearch.getAreaBlock()));

        return "rent-list";
    }

    /**
     * search-as-you-type：搜索自动补全接口
     *
     * @param prefix 输入前缀
     * @return 搜索建议
     */
    @GetMapping("rent/house/autocomplete")
    @ResponseBody
    public ApiResponse autocomplete(@RequestParam(value = "prefix") String prefix) {
        if (prefix.isEmpty()) {
            return ApiResponse.ofStatus(ApiStatus.BAD_REQUEST);
        }
        ServiceResultEntity<List<String>> result = this.searchService.suggest(prefix);
        return ApiResponse.ofSuccess(result.getResult());
    }

    /**
     * 房源信息详情页
     */
    @GetMapping("rent/house/show/{id}")
    public String show(@PathVariable(value = "id") Long houseId,
                       Model model) {
        if (houseId <= 0) {
            return "status/404";
        }
        ServiceResultEntity<HouseDTO> serviceResult = houseService.findCompleteOne(houseId);
        if (!serviceResult.isSuccess()) {
            return "status/404";
        }

        HouseDTO houseDTO = serviceResult.getResult();
        Map<CityLevel, CityDTO> addressMap = cityService.findCityAndRegion(houseDTO.getCityEnName(), houseDTO.getRegionEnName());
        CityDTO city = addressMap.get(CityLevel.CITY);
        CityDTO region = addressMap.get(CityLevel.REGION);
        model.addAttribute("city", city);
        model.addAttribute("region", region);

        ServiceResultEntity<UserDTO> userDTOServiceResult = userService.findById(houseDTO.getAdminId());
        // 经纪人
        model.addAttribute("agent", userDTOServiceResult.getResult());
        model.addAttribute("house", houseDTO);
        // 聚合数据：房源信息在小区中的数量
        ServiceResultEntity<Long> aggResult = searchService.aggregateDistrictHouse(city.getEnName(), region.getEnName(), houseDTO.getDistrict());
        model.addAttribute("houseCountInDistrict", aggResult.getResult());
//        model.addAttribute("houseCountInDistrict", 0);
        return "house-detail";
    }

    /**
     * 地图找房
     */
    @GetMapping("rent/house/map")
    public String rentMapPage(@RequestParam(value = "cityEnName") String cityEnName, Model model,
                              HttpSession session, RedirectAttributes redirectAttributes) {
        ServiceResultEntity<CityDTO> city = cityService.findCity(cityEnName);
        if (!city.isSuccess()) {
            redirectAttributes.addAttribute("msg", "must_chose_city");
            return "redirect:/index";
        } else {
            session.setAttribute("cityName", cityEnName);
            model.addAttribute("city", city.getResult());
        }
        ServiceMultiResultEntity<CityDTO> regions = cityService.findAllRegionsByCityEnName(cityEnName);

        ServiceMultiResultEntity<HouseBucketEntity> serviceResult = searchService.mapAggregate(cityEnName);

        model.addAttribute("aggData", serviceResult.getResult());
        model.addAttribute("total", serviceResult.getTotal());
        model.addAttribute("regions", regions.getResult());
        return "rent-map";
    }

    /**
     * 地图找房：房源信息列表
     */
    @GetMapping("rent/house/map/houses")
    @ResponseBody
    public ApiResponse rentMapHouses(@ModelAttribute MapSearchParam mapSearch) {
        if (mapSearch.getCityEnName() == null) {
            return ApiResponse.ofMessage(HttpStatus.BAD_REQUEST.value(), "必须选择城市");
        }
        ServiceMultiResultEntity<HouseDTO> serviceMultiResult;
        if (mapSearch.getLevel() < 13) {
            serviceMultiResult = mapService.wholeMapQuery(mapSearch);
        } else {
            // 小地图查询必须要传递地图边界参数
            // 房源信息列表和地图界面联动
            serviceMultiResult = mapService.boundMapQuery(mapSearch);
        }

        ApiResponse response = ApiResponse.ofSuccess(serviceMultiResult.getResult());
        response.setMore(serviceMultiResult.getTotal() > (mapSearch.getStart() + mapSearch.getSize()));
        return response;

    }

}
