package com.bubble.house.service.house;

import com.bubble.house.entity.BaiDuMapEntity;
import com.bubble.house.entity.house.CityLevel;
import com.bubble.house.service.ServiceMultiResultEntity;
import com.bubble.house.service.ServiceResultEntity;
import com.bubble.house.web.dto.house.CityDTO;
import com.bubble.house.web.dto.house.SubwayDTO;
import com.bubble.house.web.dto.house.SubwayStationDTO;

import java.util.List;
import java.util.Map;

/**
 * 城市信息相关服务接口
 *
 * @author wugang
 * date: 2019-11-05 16:31
 **/
public interface CityService {

    /**
     * 获取所有支持的城市列表
     *
     * @return 城市列表
     */
    ServiceMultiResultEntity<CityDTO> findAllCities();

    /**
     * 根据英文简写获取具体区域的信息
     *
     * @param cityEnName   英文简写
     * @param regionEnName 区域简写
     * @return 城市信息
     */
    Map<CityLevel, CityDTO> findCityAndRegion(String cityEnName, String regionEnName);


    /**
     * 根据城市英文简写获取该城市所有支持的区域信息
     *
     * @param cityEnName 城市英文简写
     * @return 城市所有支持的区域信息
     */
    ServiceMultiResultEntity<CityDTO> findAllRegionsByCityEnName(String cityEnName);

    /**
     * 获取该城市所有的地铁线路
     *
     * @param cityEnName 城市英文简写
     * @return 地铁线路集合
     */
    List<SubwayDTO> findAllSubwayByCityEnName(String cityEnName);

    /**
     * 获取地铁线路所有的站点
     *
     * @param subwayId 地铁ID
     * @return 站点集合
     */
    List<SubwayStationDTO> findAllStationBySubway(Long subwayId);

    /**
     * 获取地铁线信息
     *
     * @param subwayId 地铁ID
     * @return ServiceResultEntity<SubwayDTO>
     */
    ServiceResultEntity<SubwayDTO> findSubway(Long subwayId);

    /**
     * 获取地铁站点信息
     *
     * @param stationId 站点信息
     * @return ServiceResultEntity<SubwayStationDTO>
     */
    ServiceResultEntity<SubwayStationDTO> findSubwayStation(Long stationId);

    /**
     * 根据城市英文简写获取城市详细信息
     *
     * @param cityEnName 城市英文名
     * @return ServiceResultEntity<CityDTO>
     */
    ServiceResultEntity<CityDTO> findCity(String cityEnName);

}
