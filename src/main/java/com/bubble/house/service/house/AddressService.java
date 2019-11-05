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
     */
    MultiResultEntity<CityEntity> findAllCities();

}
