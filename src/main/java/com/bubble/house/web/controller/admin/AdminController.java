package com.bubble.house.web.controller.admin;

import com.bubble.house.base.api.ApiDataTableResponse;
import com.bubble.house.base.api.ApiResponse;
import com.bubble.house.base.api.ApiStatus;
import com.bubble.house.entity.house.CityLevel;
import com.bubble.house.entity.house.HouseOperation;
import com.bubble.house.entity.house.HouseStatus;
import com.bubble.house.service.ServiceMultiResultEntity;
import com.bubble.house.service.ServiceResultEntity;
import com.bubble.house.service.house.CityService;
import com.bubble.house.service.house.HouseService;
import com.bubble.house.service.house.QiNiuService;
import com.bubble.house.web.dto.house.*;
import com.bubble.house.web.param.DatatableSearchParam;
import com.bubble.house.web.param.HouseParam;
import com.google.common.base.Strings;
import com.google.gson.Gson;
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
 * 角色为Admin：Admin用户的后台管理中心
 *
 * @author wugang
 * date: 2019-11-05 11:32
 **/
@Controller
public class AdminController {
    private final static Logger LOGGER = LoggerFactory.getLogger(AdminController.class);

    /**
     * 图片上传到本地的文件路径
     */
    @Value("${spring.http.multipart.location}")
    private String imageLocation;

    private final QiNiuService qiNiuService;
    private final CityService cityService;
    private final HouseService houseService;
    private final Gson gson;

    public AdminController(QiNiuService qiNiuService, CityService cityService,
                           HouseService houseService, Gson gson) {
        this.qiNiuService = qiNiuService;
        this.cityService = cityService;
        this.houseService = houseService;
        this.gson = gson;
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
        ServiceMultiResultEntity<HouseDTO> result = houseService.adminQuery(searchBody);
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
        // 应用于static/js/admin/house-add.js
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

        Map<CityLevel, CityDTO> addressMap = this.cityService.findCityAndRegion(houseParam.getCityEnName(), houseParam.getRegionEnName());
        if (addressMap.keySet().size() != 2) {
            return ApiResponse.ofStatus(ApiStatus.NOT_VALID_PARAM);
        }

        ServiceResultEntity<HouseDTO> result = this.houseService.save(houseParam);
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
        ServiceResultEntity<HouseDTO> serviceResult = houseService.findCompleteOne(id);
        if (!serviceResult.isSuccess()) {
            return "status/404";
        }
        HouseDTO result = serviceResult.getResult();
        model.addAttribute("house", result);

        Map<CityLevel, CityDTO> addressMap = cityService.findCityAndRegion(result.getCityEnName(), result.getRegionEnName());
        model.addAttribute("city", addressMap.get(CityLevel.CITY));
        model.addAttribute("region", addressMap.get(CityLevel.REGION));

        HouseDetailDTO detailDTO = result.getHouseDetail();
        ServiceResultEntity<SubwayDTO> subwayServiceResult = cityService.findSubway(detailDTO.getSubwayLineId());
        if (subwayServiceResult.isSuccess()) {
            model.addAttribute("subway", subwayServiceResult.getResult());
        }

        ServiceResultEntity<SubwayStationDTO> subwayStationServiceResult = cityService.findSubwayStation(detailDTO.getSubwayStationId());
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
        Map<CityLevel, CityDTO> addressMap = cityService.findCityAndRegion(houseParam.getCityEnName(), houseParam.getRegionEnName());
        if (addressMap.keySet().size() != 2) {
            return ApiResponse.ofSuccess(ApiStatus.NOT_VALID_PARAM);
        }

        ServiceResultEntity result = houseService.update(houseParam);
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
                QiNiuPutRet qiNiuPutRet = gson.fromJson(response.bodyString(), QiNiuPutRet.class);
                return ApiResponse.ofSuccess(qiNiuPutRet);
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
        ServiceResultEntity result = this.houseService.removePhoto(id);
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

        ServiceResultEntity result = this.houseService.updateCover(coverId, targetId);

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
        ServiceResultEntity result = this.houseService.addTag(houseId, tag);
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

        ServiceResultEntity result = this.houseService.removeTag(houseId, tag);
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
        ServiceResultEntity result;
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
