package com.bubble.house.service.house;

import com.bubble.house.entity.BaiDuMapEntity;
import com.bubble.house.service.ServiceMultiResultEntity;
import com.bubble.house.service.ServiceResultEntity;
import com.bubble.house.web.dto.house.HouseDTO;
import com.bubble.house.web.param.MapSearchParam;

/**
 * 地图查询服务
 *
 * @author wugang
 * date: 2020-10-29 17:26
 **/
public interface MapService {

    /**
     * 全地图查询
     *
     * @param mapSearch 地图查询参数
     * @return ServiceMultiResultEntity<HouseDTO>
     */
    ServiceMultiResultEntity<HouseDTO> wholeMapQuery(MapSearchParam mapSearch);

    /**
     * 精确范围数据查询
     *
     * @param mapSearch 地图查询参数
     * @return ServiceMultiResultEntity<HouseDTO>
     */
    ServiceMultiResultEntity<HouseDTO> boundMapQuery(MapSearchParam mapSearch);


    /**
     * 根据城市以及具体地位获取百度地图的经纬度
     * （需请求百度地图API，建议加缓存、）
     *
     * @param city    城市
     * @param address 地址
     * @return ServiceResultEntity<BaiDuMapEntity>
     */
    ServiceResultEntity<BaiDuMapEntity> getBaiDuMapLocation(String city, String address);

    /**
     * 上传百度LBS数据
     *
     * @param location 百度地图信息实体
     * @param title    标题
     * @param address  地址
     * @param houseId  房屋ID
     * @param price    价格
     * @param area     区域
     * @return ServiceResultEntity
     */
    ServiceResultEntity lbsUpload(BaiDuMapEntity location, String title, String address,
                                  long houseId, int price, int area);

    /**
     * 移除百度LBS数据
     *
     * @param houseId 房屋ID
     * @return ServiceResultEntity
     */
    ServiceResultEntity removeLbs(Long houseId);

}
