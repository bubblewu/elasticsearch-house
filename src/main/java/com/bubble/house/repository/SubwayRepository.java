package com.bubble.house.repository;

import com.bubble.house.entity.house.SubwayEntity;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

/**
 * 城市地铁信息
 *
 * @author wugang
 * date: 2019-11-05 17:32
 **/
public interface SubwayRepository extends CrudRepository<SubwayEntity, Long> {

    List<SubwayEntity> findAllByCityEnName(String cityEnName);

}
