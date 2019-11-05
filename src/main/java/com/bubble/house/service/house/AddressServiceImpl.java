package com.bubble.house.service.house;

import com.bubble.house.entity.MultiResultEntity;
import com.bubble.house.entity.house.CityEntity;
import com.bubble.house.entity.house.CityLevel;
import com.bubble.house.entity.house.SubwayEntity;
import com.bubble.house.entity.house.SubwayStationEntity;
import com.bubble.house.repository.CityRepository;
import com.bubble.house.repository.SubwayRepository;
import com.bubble.house.repository.SubwayStationRepository;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * 地点相关服务接口实现
 *
 * @author wugang
 * date: 2019-11-05 16:32
 **/
@Service
public class AddressServiceImpl implements AddressService {

    private CityRepository cityRepository;
    private SubwayRepository subwayRepository;
    private SubwayStationRepository subwayStationRepository;

    public AddressServiceImpl(CityRepository cityRepository, SubwayRepository subwayRepository, SubwayStationRepository subwayStationRepository) {
        this.cityRepository = cityRepository;
        this.subwayRepository = subwayRepository;
        this.subwayStationRepository = subwayStationRepository;
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

    @Override
    public List<SubwayEntity> findAllSubwayByCity(String cityEnName) {
        if (null == cityEnName) {
            return new ArrayList<>();
        }
        List<SubwayEntity> regions = this.subwayRepository.findAllByCityEnName(cityEnName);
        return Optional.of(regions).orElse(new ArrayList<>());
    }

    @Override
    public List<SubwayStationEntity> findAllStationBySubway(Long subwayId) {
        List<SubwayStationEntity> stations = this.subwayStationRepository.findAllBySubwayId(subwayId);
        return Optional.of(stations).orElse(new ArrayList<>());
    }

}
