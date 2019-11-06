package com.bubble.house.service.house;

import com.bubble.house.entity.BaiDuMapEntity;
import com.bubble.house.entity.result.MultiResultEntity;
import com.bubble.house.entity.house.CityEntity;
import com.bubble.house.entity.house.CityLevel;
import com.bubble.house.entity.house.SubwayEntity;
import com.bubble.house.entity.house.SubwayStationEntity;
import com.bubble.house.entity.result.ResultEntity;
import com.bubble.house.repository.CityRepository;
import com.bubble.house.repository.SubwayRepository;
import com.bubble.house.repository.SubwayStationRepository;
import org.springframework.stereotype.Service;

import java.util.*;

/**
 * 地点相关服务接口实现
 *
 * @author wugang
 * date: 2019-11-05 16:32
 **/
@Service
public class AddressServiceImpl implements AddressService {

    private final CityRepository cityRepository;
    private final SubwayRepository subwayRepository;
    private final SubwayStationRepository subwayStationRepository;

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
    public Map<CityLevel, CityEntity> findCityAndRegion(String cityEnName, String regionEnName) {
        Map<CityLevel, CityEntity> result = new HashMap<>();

        CityEntity city = this.cityRepository.findByEnNameAndLevel(cityEnName, CityLevel.CITY.getValue());
        CityEntity region = this.cityRepository.findByEnNameAndBelongTo(regionEnName, city.getEnName());

        result.put(CityLevel.CITY, city);
        result.put(CityLevel.REGION, region);
        return result;
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
    public List<SubwayEntity> findAllSubwayByCityEnName(String cityEnName) {
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

    @Override
    public ResultEntity<SubwayEntity> findSubway(Long subwayId) {
        if (subwayId == null) {
            return ResultEntity.notFound();
        }
        Optional<SubwayEntity> subwayOp = subwayRepository.findById(subwayId);
        return subwayOp.map(ResultEntity::of).orElseGet(ResultEntity::notFound);
    }

    @Override
    public ResultEntity<SubwayStationEntity> findSubwayStation(Long stationId) {
        if (null == stationId) {
            return ResultEntity.notFound();
        }
        Optional<SubwayStationEntity> subwayStationOp = subwayStationRepository.findById(stationId);
        return subwayStationOp.map(ResultEntity::of).orElseGet(ResultEntity::notFound);
    }

    @Override
    public ResultEntity<CityEntity> findCity(String cityEnName) {
        if (null == cityEnName) {
            return ResultEntity.notFound();
        }
        CityEntity city = cityRepository.findByEnNameAndLevel(cityEnName, CityLevel.CITY.getValue());
        return city == null ? ResultEntity.notFound() : ResultEntity.of(city);
    }

    @Override
    public ResultEntity<BaiDuMapEntity> getBaiDuMapLocation(String city, String address) {
        return null;
    }

    @Override
    public ResultEntity lbsUpload(BaiDuMapEntity location, String title, String address, long houseId, int price, int area) {
        return null;
    }

    @Override
    public ResultEntity removeLbs(Long houseId) {
        return null;
    }

}
