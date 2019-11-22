package com.bubble.house.service.house;

import com.bubble.house.entity.BaiDuMapEntity;
import com.bubble.house.entity.result.MultiResultEntity;
import com.bubble.house.entity.house.CityEntity;
import com.bubble.house.entity.house.CityLevel;
import com.bubble.house.entity.house.SubwayEntity;
import com.bubble.house.entity.house.SubwayStationEntity;
import com.bubble.house.entity.result.ResultEntity;

import java.util.List;
import java.util.Map;

/**
 * 地点相关服务接口
 *
 * @author wugang
 * date: 2019-11-05 16:31
 **/
public interface AddressService {

    /**
     * 获取所有支持的城市列表
     *
     * @return 城市列表
     */
    MultiResultEntity<CityEntity> findAllCities();

    /**
     * 根据英文简写获取具体区域的信息
     *
     * @param cityEnName  英文简写
     * @param regionEnName  区域简写
     * @return 城市信息
     */
    Map<CityLevel, CityEntity> findCityAndRegion(String cityEnName, String regionEnName);


    /**
     * 根据城市英文简写获取该城市所有支持的区域信息
     *
     * @param cityEnName 城市英文简写
     * @return 城市所有支持的区域信息
     */
    MultiResultEntity<CityEntity> findAllRegionsByCityEnName(String cityEnName);

    /**
     * 获取该城市所有的地铁线路
     *
     * @param cityEnName 城市英文简写
     * @return 地铁线路集合
     */
    List<SubwayEntity> findAllSubwayByCityEnName(String cityEnName);

    /**
     * 获取地铁线路所有的站点
     *
     * @param subwayId 地铁ID
     * @return 站点集合
     */
    List<SubwayStationEntity> findAllStationBySubway(Long subwayId);

    /**
     * 获取地铁线信息
     */
    ResultEntity<SubwayEntity> findSubway(Long subwayId);

    /**
     * 获取地铁站点信息
     */
    ResultEntity<SubwayStationEntity> findSubwayStation(Long stationId);

    /**
     * 根据城市英文简写获取城市详细信息
     */
    ResultEntity<CityEntity> findCity(String cityEnName);

    /**
     * 根据城市以及具体地位获取百度地图的经纬度
     * （需请求百度地图API，建议加缓存、）
     */
    ResultEntity<BaiDuMapEntity> getBaiDuMapLocation(String city, String address);

    /**
     * 上传百度LBS数据
     */
    ResultEntity lbsUpload(BaiDuMapEntity location, String title, String address,
                            long houseId, int price, int area);

    /**
     * 移除百度LBS数据
     */
    ResultEntity removeLbs(Long houseId);

}
