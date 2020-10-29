package com.bubble.house.repository.house;

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

    /**
     * 根据ID获取房屋详情
     *
     * @param houseId 房屋ID
     * @return HouseDetailEntity
     */
    HouseDetailEntity findByHouseId(Long houseId);

    /**
     * 根据多个ID获取房屋详情
     *
     * @param houseIds 房屋ID集合
     * @return List<HouseDetailEntity>
     */
    List<HouseDetailEntity> findAllByHouseIdIn(List<Long> houseIds);
}