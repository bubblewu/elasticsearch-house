package com.bubble.house.repository.house;

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

    /**
     * 根据地铁ID查找站点集合
     *
     * @param subwayId 地铁ID
     * @return 站点集合
     */
    List<SubwayStationEntity> findAllBySubwayId(Long subwayId);

}
