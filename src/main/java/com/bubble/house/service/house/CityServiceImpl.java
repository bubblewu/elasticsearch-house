package com.bubble.house.service.house;

import com.bubble.house.entity.house.CityEntity;
import com.bubble.house.entity.house.CityLevel;
import com.bubble.house.entity.house.SubwayEntity;
import com.bubble.house.entity.house.SubwayStationEntity;
import com.bubble.house.repository.house.CityRepository;
import com.bubble.house.repository.house.SubwayRepository;
import com.bubble.house.repository.house.SubwayStationRepository;
import com.bubble.house.service.ServiceMultiResultEntity;
import com.bubble.house.service.ServiceResultEntity;
import com.bubble.house.web.dto.house.CityDTO;
import com.bubble.house.web.dto.house.SubwayDTO;
import com.bubble.house.web.dto.house.SubwayStationDTO;
import com.google.common.collect.Lists;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.*;

/**
 * 地点相关服务接口实现
 *
 * @author wugang
 * date: 2019-11-05 16:32
 **/
@Service
public class CityServiceImpl implements CityService {
    private final Logger logger = LoggerFactory.getLogger(CityServiceImpl.class);

    private final CityRepository cityRepository;
    private final SubwayRepository subwayRepository;
    private final SubwayStationRepository subwayStationRepository;
    private final ModelMapper modelMapper;

    public CityServiceImpl(CityRepository cityRepository, SubwayRepository subwayRepository,
                           SubwayStationRepository subwayStationRepository, ModelMapper modelMapper) {
        this.cityRepository = cityRepository;
        this.subwayRepository = subwayRepository;
        this.subwayStationRepository = subwayStationRepository;
        this.modelMapper = modelMapper;
    }

    @Override
    public ServiceMultiResultEntity<CityDTO> findAllCities() {
        List<CityEntity> cityList = cityRepository.findAllByLevel(CityLevel.CITY.getValue());
        List<CityDTO> cityDTOList = new ArrayList<>();
        for (CityEntity city : cityList) {
            CityDTO cityDTO = modelMapper.map(city, CityDTO.class);
            cityDTOList.add(cityDTO);
        }
        logger.debug("加载城市信息：{}", cityDTOList.size());
        return new ServiceMultiResultEntity<>(cityDTOList.size(), cityDTOList);
    }

    @Override
    public Map<CityLevel, CityDTO> findCityAndRegion(String cityEnName, String regionEnName) {
        logger.debug("开始加载[{}-{}]信息", cityEnName, regionEnName);
        Map<CityLevel, CityDTO> result = new HashMap<>();
        CityEntity city = this.cityRepository.findByEnNameAndLevel(cityEnName, CityLevel.CITY.getValue());
        CityEntity region = this.cityRepository.findByEnNameAndBelongTo(regionEnName, city.getEnName());
        result.put(CityLevel.CITY, modelMapper.map(city, CityDTO.class));
        result.put(CityLevel.REGION, modelMapper.map(region, CityDTO.class));
        return result;
    }

    @Override
    public ServiceMultiResultEntity<CityDTO> findAllRegionsByCityEnName(String cityEnName) {
        logger.debug("开始加载城市[{}]下的县区信息", cityEnName);
        if (null == cityEnName) {
            return new ServiceMultiResultEntity<>(0, null);
        }
        List<CityDTO> cityDTOList = new ArrayList<>();
        List<CityEntity> regions = this.cityRepository.findAllByLevelAndBelongTo(CityLevel.REGION.getValue(), cityEnName);
        regions.forEach(region -> cityDTOList.add(modelMapper.map(region, CityDTO.class)));
        return new ServiceMultiResultEntity<>(cityDTOList.size(), cityDTOList);
    }

    @Override
    public List<SubwayDTO> findAllSubwayByCityEnName(String cityEnName) {
        logger.debug("开始加载城市[{}]下的地铁信息", cityEnName);
        List<SubwayDTO> result = new ArrayList<>();
        if (null == cityEnName || cityEnName.isEmpty()) {
            return Lists.newArrayList();
        }
        List<SubwayEntity> subwayList = this.subwayRepository.findAllByCityEnName(cityEnName);
        subwayList.forEach(subway -> result.add(modelMapper.map(subway, SubwayDTO.class)));
        return result;
    }

    @Override
    public List<SubwayStationDTO> findAllStationBySubway(Long subwayId) {
        logger.debug("开始加载地铁[{}]的地铁站点信息", subwayId);
        List<SubwayStationDTO> result = new ArrayList<>();
        List<SubwayStationEntity> stations = subwayStationRepository.findAllBySubwayId(subwayId);
        stations.forEach(station -> result.add(modelMapper.map(station, SubwayStationDTO.class)));
        return result;
    }

    @Override
    public ServiceResultEntity<SubwayDTO> findSubway(Long subwayId) {
        logger.debug("开始加载地铁[{}]的地铁线路信息", subwayId);
        if (subwayId == null) {
            return ServiceResultEntity.notFound();
        }
        Optional<SubwayEntity> subwayOp = subwayRepository.findById(subwayId);
        return subwayOp.map(subwayEntity -> ServiceResultEntity.of(modelMapper.map(subwayEntity, SubwayDTO.class)))
                .orElseGet(ServiceResultEntity::notFound);
    }

    @Override
    public ServiceResultEntity<SubwayStationDTO> findSubwayStation(Long stationId) {
        logger.debug("开始加载地铁站[{}]的信息", stationId);
        if (null == stationId) {
            return ServiceResultEntity.notFound();
        }

        Optional<SubwayStationEntity> subwayStationOp = subwayStationRepository.findById(stationId);
        return subwayStationOp.map(subwayStationEntity -> ServiceResultEntity.of(modelMapper.map(subwayStationEntity, SubwayStationDTO.class)))
                .orElseGet(ServiceResultEntity::notFound);
    }

    @Override
    public ServiceResultEntity<CityDTO> findCity(String cityEnName) {
        logger.debug("开始加载城市[{}]的信息", cityEnName);
        if (null == cityEnName || cityEnName.isEmpty()) {
            return ServiceResultEntity.notFound();
        }
        CityEntity city = cityRepository.findByEnNameAndLevel(cityEnName, CityLevel.CITY.getValue());
        return city == null ? ServiceResultEntity.notFound() : ServiceResultEntity.of(modelMapper.map(city, CityDTO.class));
    }

}
