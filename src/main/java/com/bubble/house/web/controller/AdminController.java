package com.bubble.house.web.controller;

import com.alibaba.fastjson.JSONObject;
import com.bubble.house.base.api.ApiDataTableResponse;
import com.bubble.house.base.api.ApiResponse;
import com.bubble.house.base.api.ApiStatus;
import com.bubble.house.entity.QiNiuEntity;
import com.bubble.house.entity.dto.HouseDTO;
import com.bubble.house.entity.dto.HouseDetailDTO;
import com.bubble.house.entity.house.CityEntity;
import com.bubble.house.entity.house.CityLevel;
import com.bubble.house.entity.house.SubwayEntity;
import com.bubble.house.entity.house.SubwayStationEntity;
import com.bubble.house.entity.param.DatatableSearchParam;
import com.bubble.house.entity.param.HouseParam;
import com.bubble.house.entity.result.MultiResultEntity;
import com.bubble.house.entity.result.ResultEntity;
import com.bubble.house.service.house.AddressService;
import com.bubble.house.service.house.HouseService;
import com.bubble.house.service.house.QiNiuService;
import com.qiniu.http.Response;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.Valid;
import java.io.IOException;
import java.io.InputStream;
import java.util.Map;

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
    private final AddressService addressService;
    private final HouseService houseService;

    public AdminController(QiNiuService qiNiuService, AddressService addressService, HouseService houseService) {
        this.qiNiuService = qiNiuService;
        this.addressService = addressService;
        this.houseService = houseService;
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
     * House信息展示：分页、筛选、搜索
     */
    @PostMapping("admin/houses")
    @ResponseBody
    public ApiDataTableResponse houses(@ModelAttribute DatatableSearchParam searchBody) {
        MultiResultEntity<HouseDTO> result = houseService.adminQuery(searchBody);
        ApiDataTableResponse response = new ApiDataTableResponse(ApiStatus.SUCCESS);
        response.setData(result.getResult());
        response.setRecordsFiltered(result.getTotal());
        response.setRecordsTotal(result.getTotal());
        response.setDraw(searchBody.getDraw());
        return response;
    }

    /**
     * 新增房源功能页
     */
    @GetMapping("admin/add/house")
    public String addHousePage() {
        return "admin/house-add";
    }

    /**
     * 新增房源接口
     */
    @PostMapping("admin/add/house")
    @ResponseBody
    public ApiResponse addHouse(@Valid @ModelAttribute("form-house-add") HouseParam houseParam, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return new ApiResponse(HttpStatus.BAD_REQUEST.value(), bindingResult.getAllErrors().get(0).getDefaultMessage(), null);
        }
        if (houseParam.getPhotos() == null || houseParam.getCover() == null) {
            return ApiResponse.ofMessage(HttpStatus.BAD_REQUEST.value(), "必须上传图片");
        }

        Map<CityLevel, CityEntity> addressMap = this.addressService.findCityAndRegion(houseParam.getCityEnName(), houseParam.getRegionEnName());
        if (addressMap.keySet().size() != 2) {
            return ApiResponse.ofStatus(ApiStatus.NOT_VALID_PARAM);
        }

        ResultEntity<HouseDTO> result = this.houseService.save(houseParam);
        if (result.isSuccess()) {
            return ApiResponse.ofSuccess(result.getResult());
        }
        return ApiResponse.ofSuccess(ApiStatus.NOT_VALID_PARAM);
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

    /**
     * 房源信息编辑页
     */
    @GetMapping("admin/house/edit")
    public String houseEditPage(@RequestParam(value = "id") Long id, Model model) {
        if (id == null || id < 1) {
            return "404";
        }
        ResultEntity<HouseDTO> serviceResult = houseService.findCompleteOne(id);
        if (!serviceResult.isSuccess()) {
            return "404";
        }
        HouseDTO result = serviceResult.getResult();
        model.addAttribute("house", result);

        Map<CityLevel, CityEntity> addressMap = addressService.findCityAndRegion(result.getCityEnName(), result.getRegionEnName());
        model.addAttribute("city", addressMap.get(CityLevel.CITY));
        model.addAttribute("region", addressMap.get(CityLevel.REGION));

        HouseDetailDTO detailDTO = result.getHouseDetail();
        ResultEntity<SubwayEntity> subwayServiceResult = addressService.findSubway(detailDTO.getSubwayLineId());
        if (subwayServiceResult.isSuccess()) {
            model.addAttribute("subway", subwayServiceResult.getResult());
        }

        ResultEntity<SubwayStationEntity> subwayStationServiceResult = addressService.findSubwayStation(detailDTO.getSubwayStationId());
        if (subwayStationServiceResult.isSuccess()) {
            model.addAttribute("station", subwayStationServiceResult.getResult());
        }
        return "admin/house-edit";
    }

    /**
     * 编辑接口
     */
    @PostMapping("admin/house/edit")
    @ResponseBody
    public ApiResponse saveHouse(@Valid @ModelAttribute("form-house-edit") HouseParam houseParam, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return new ApiResponse(HttpStatus.BAD_REQUEST.value(), bindingResult.getAllErrors().get(0).getDefaultMessage(), null);
        }
        Map<CityLevel, CityEntity> addressMap = addressService.findCityAndRegion(houseParam.getCityEnName(), houseParam.getRegionEnName());
        if (addressMap.keySet().size() != 2) {
            return ApiResponse.ofSuccess(ApiStatus.NOT_VALID_PARAM);
        }

        ResultEntity result = houseService.update(houseParam);
        if (result.isSuccess()) {
            return ApiResponse.ofSuccess(null);
        }

        ApiResponse response = ApiResponse.ofStatus(ApiStatus.BAD_REQUEST);
        response.setMsg(result.getMessage());
        return response;
    }

}
