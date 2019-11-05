package com.bubble.house.service.house;

import com.bubble.house.entity.MultiResultEntity;
import com.bubble.house.entity.house.CityEntity;

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
     * 根据城市英文简写获取该城市所有支持的区域信息
     *
     * @param cityEnName 城市英文简写
     * @return 城市所有支持的区域信息
     */
    MultiResultEntity<CityEntity> findAllRegionsByCityEnName(String cityEnName);
}
