package com.bubble.house.service.house;

import com.bubble.house.entity.MultiResultEntity;
import com.bubble.house.entity.house.CityEntity;
import com.bubble.house.entity.house.CityLevel;
import com.bubble.house.repository.CityRepository;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 地点相关服务接口实现
 *
 * @author wugang
 * date: 2019-11-05 16:32
 **/
@Service
public class AddressServiceImpl implements AddressService {

    private CityRepository cityRepository;

    public AddressServiceImpl(CityRepository cityRepository) {
        this.cityRepository = cityRepository;
    }

    @Override
    public MultiResultEntity<CityEntity> findAllCities() {
        List<CityEntity> cityEntityList = this.cityRepository.findAllByLevel(CityLevel.CITY.getValue());
        return new MultiResultEntity<>(cityEntityList.size(), cityEntityList);
    }

    @Override
    public MultiResultEntity<CityEntity> findAllRegionsByCityEnName(String cityEnName) {
        if (null == cityEnName) {
            return new MultiResultEntity<>(0, null);
        }
        List<CityEntity> regions = this.cityRepository.findAllByLevelAndBelongTo(CityLevel.REGION.getValue(), cityEnName);
        return new MultiResultEntity<>(regions.size(), regions);
    }

}
