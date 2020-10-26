package com.bubble.house.web.controller;

import com.alibaba.fastjson.JSONObject;
import com.bubble.house.base.api.ApiDataTableResponse;
import com.bubble.house.base.api.ApiResponse;
import com.bubble.house.base.api.ApiStatus;
import com.bubble.house.entity.QiNiuEntity;
import com.bubble.house.entity.dto.HouseDTO;
import com.bubble.house.entity.dto.HouseDetailDTO;
import com.bubble.house.entity.house.*;
import com.bubble.house.entity.param.DatatableSearchParam;
import com.bubble.house.entity.param.HouseParam;
import com.bubble.house.entity.result.MultiResultEntity;
import com.bubble.house.entity.result.ResultEntity;
import com.bubble.house.service.house.AddressService;
import com.bubble.house.service.house.HouseService;
import com.bubble.house.service.house.QiNiuService;
import com.google.common.base.Strings;
import com.qiniu.http.Response;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
    private final static Logger LOGGER = LoggerFactory.getLogger(AdminController.class);

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
        String page = "admin/center";
        LOGGER.debug("进入后台管理中心页面：[{}]", page);
        return page;
    }

    /**
     * 欢迎页
     */
    @GetMapping("/admin/welcome")
    public String welcomePage() {
        String page = "admin/welcome";
        LOGGER.debug("进入欢迎页面：[{}]", page);
        return page;
    }

    /**
     * 管理员登录页
     */
    @GetMapping("/admin/login")
    public String adminLoginPage() {
        String page = "admin/login";
        LOGGER.debug("进入管理员登录页面：[{}]", page);
        return page;
    }

    /**
     * 房源列表页
     */
    @GetMapping("admin/house/list")
    public String houseListPage() {
        String page = "admin/house-list";
        LOGGER.debug("进入房源列表页面：[{}]", page);
        return page;
    }

    /**
     * House信息展示：分页、筛选、搜索
     */
    @PostMapping("admin/houses")
    @ResponseBody
    public ApiDataTableResponse houses(@ModelAttribute DatatableSearchParam searchBody) {
        LOGGER.debug("进入房源信息展示接口：[admin/houses]");
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
        LOGGER.debug("进入新增房源页面：[admin/house-add]");
        return "admin/house-add";
    }

    /**
     * 新增房源接口
     */
    @PostMapping("admin/add/house")
    @ResponseBody
    public ApiResponse addHouse(@Valid @ModelAttribute("form-house-add") HouseParam houseParam, BindingResult bindingResult) {
        LOGGER.debug("进入新增房源接口：[admin/add/house]");
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
     * 房源信息编辑页
     */
    @GetMapping("admin/house/edit")
    public String houseEditPage(@RequestParam(value = "id") Long id, Model model) {
        LOGGER.debug("进入房源信息编辑页面：[admin/house/edit]");
        if (id == null || id < 1) {
            return "status/404";
        }
        ResultEntity<HouseDTO> serviceResult = houseService.findCompleteOne(id);
        if (!serviceResult.isSuccess()) {
            return "status/404";
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
     * House信息编辑后保存接口
     */
    @PostMapping("admin/house/edit")
    @ResponseBody
    public ApiResponse saveHouse(@Valid @ModelAttribute("form-house-edit") HouseParam houseParam, BindingResult bindingResult) {
        LOGGER.debug("进入房源信息编辑后保存接口：[admin/house/edit]");
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

    /**
     * 上传图片接口：本地或七牛云
     */
    @PostMapping(value = "admin/upload/photo", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @ResponseBody
    public ApiResponse uploadPhoto(@RequestParam("file") MultipartFile file) {
        LOGGER.debug("进入图片上传接口：[admin/upload/photo]");
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
     * 移除图片接口
     */
    @DeleteMapping("admin/house/photo")
    @ResponseBody
    public ApiResponse removeHousePhoto(@RequestParam(value = "id") Long id) {
        LOGGER.debug("进入图片删除接口：[admin/house/photo]");

        ResultEntity result = this.houseService.removePhoto(id);
        if (result.isSuccess()) {
            return ApiResponse.ofStatus(ApiStatus.SUCCESS);
        } else {
            return ApiResponse.ofMessage(HttpStatus.BAD_REQUEST.value(), result.getMessage());
        }
    }

    /**
     * 修改封面接口
     */
    @PostMapping("admin/house/cover")
    @ResponseBody
    public ApiResponse updateCover(@RequestParam(value = "cover_id") Long coverId,
                                   @RequestParam(value = "target_id") Long targetId) {
        LOGGER.debug("进入修改房源封面接口：[admin/house/cover]");

        ResultEntity result = this.houseService.updateCover(coverId, targetId);

        if (result.isSuccess()) {
            return ApiResponse.ofStatus(ApiStatus.SUCCESS);
        } else {
            return ApiResponse.ofMessage(HttpStatus.BAD_REQUEST.value(), result.getMessage());
        }
    }

    /**
     * 增加标签接口
     */
    @PostMapping("admin/house/tag")
    @ResponseBody
    public ApiResponse addHouseTag(@RequestParam(value = "house_id") Long houseId,
                                   @RequestParam(value = "tag") String tag) {
        LOGGER.debug("进入增加标签接口：[admin/house/tag]");

        if (houseId < 1 || Strings.isNullOrEmpty(tag)) {
            return ApiResponse.ofStatus(ApiStatus.BAD_REQUEST);
        }
        ResultEntity result = this.houseService.addTag(houseId, tag);
        if (result.isSuccess()) {
            return ApiResponse.ofStatus(ApiStatus.SUCCESS);
        } else {
            return ApiResponse.ofMessage(HttpStatus.BAD_REQUEST.value(), result.getMessage());
        }
    }

    /**
     * 移除标签接口
     */
    @DeleteMapping("admin/house/tag")
    @ResponseBody
    public ApiResponse removeHouseTag(@RequestParam(value = "house_id") Long houseId,
                                      @RequestParam(value = "tag") String tag) {
        LOGGER.debug("进入删除标签接口：[admin/house/tag]");

        if (houseId < 1 || Strings.isNullOrEmpty(tag)) {
            return ApiResponse.ofStatus(ApiStatus.BAD_REQUEST);
        }

        ResultEntity result = this.houseService.removeTag(houseId, tag);
        if (result.isSuccess()) {
            return ApiResponse.ofStatus(ApiStatus.SUCCESS);
        } else {
            return ApiResponse.ofMessage(HttpStatus.BAD_REQUEST.value(), result.getMessage());
        }
    }


    /**
     * 审核接口
     */
    @PutMapping("admin/house/operate/{id}/{operation}")
    @ResponseBody
    public ApiResponse operateHouse(@PathVariable(value = "id") Long id,
                                    @PathVariable(value = "operation") int operation) {
        LOGGER.debug("进入房源信息审核接口：[admin/house/operate/{}/{}]", id, operation);
        if (id <= 0) {
            return ApiResponse.ofStatus(ApiStatus.NOT_VALID_PARAM);
        }
        ResultEntity result;
        switch (operation) {
            case HouseOperation.PASS:
                result = this.houseService.updateStatus(id, HouseStatus.PASSES.getValue());
                break;
            case HouseOperation.PULL_OUT:
                result = this.houseService.updateStatus(id, HouseStatus.NOT_AUDITED.getValue());
                break;
            case HouseOperation.DELETE:
                result = this.houseService.updateStatus(id, HouseStatus.DELETED.getValue());
                break;
            case HouseOperation.RENT:
                result = this.houseService.updateStatus(id, HouseStatus.RENTED.getValue());
                break;
            default:
                return ApiResponse.ofStatus(ApiStatus.BAD_REQUEST);
        }
        if (result.isSuccess()) {
            return ApiResponse.ofSuccess(null);
        }
        return ApiResponse.ofMessage(HttpStatus.BAD_REQUEST.value(),
                result.getMessage());
    }


}
