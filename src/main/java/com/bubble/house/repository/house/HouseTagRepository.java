package com.bubble.house.repository.house;

import com.bubble.house.entity.house.HouseTagEntity;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

/**
 * 房屋标签
 *
 * @author wugang
 * date: 2019-11-05 18:44
 **/
public interface HouseTagRepository extends CrudRepository<HouseTagEntity, Long> {

    /**
     * 根据标签名字和房屋ID获取标签信息
     *
     * @param name    标签
     * @param houseId 房屋ID
     * @return 房屋标签
     */
    HouseTagEntity findByNameAndHouseId(String name, Long houseId);

    /**
     * 根据houseId获取标签集合
     *
     * @param id houseId
     * @return 标签集合
     */
    List<HouseTagEntity> findAllByHouseId(Long id);

    /**
     * 根据houseId获取标签集合
     *
     * @param houseIds 房屋ID集合
     * @return 标签集合
     */
    List<HouseTagEntity> findAllByHouseIdIn(List<Long> houseIds);

}
