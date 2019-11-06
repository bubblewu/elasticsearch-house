package com.bubble.house.repository;

import com.bubble.house.entity.house.HouseDetailEntity;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

/**
 * 房屋详情
 *
 * @author wugang
 * date: 2019-11-05 18:37
 **/
public interface HouseDetailRepository extends CrudRepository<HouseDetailEntity, Long> {
    HouseDetailEntity findByHouseId(Long houseId);

    List<HouseDetailEntity> findAllByHouseIdIn(List<Long> houseIds);
}