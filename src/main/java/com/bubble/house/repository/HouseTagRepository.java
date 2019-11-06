package com.bubble.house.repository;

import com.bubble.house.entity.house.HouseTagEntity;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

/**
 * tag
 *
 * @author wugang
 * date: 2019-11-05 18:44
 **/
public interface HouseTagRepository extends CrudRepository<HouseTagEntity, Long> {
    HouseTagEntity findByNameAndHouseId(String name, Long houseId);

    List<HouseTagEntity> findAllByHouseId(Long id);

    List<HouseTagEntity> findAllByHouseIdIn(List<Long> houseIds);
}
