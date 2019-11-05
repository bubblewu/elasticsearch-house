package com.bubble.house.repository;

import com.bubble.house.entity.house.SubwayStationEntity;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

/**
 * 地铁站点信息
 *
 * @author wugang
 * date: 2019-11-05 17:34
 **/
public interface SubwayStationRepository extends CrudRepository<SubwayStationEntity, Long> {

    List<SubwayStationEntity> findAllBySubwayId(Long subwayId);

}
