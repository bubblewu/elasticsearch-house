package com.bubble.house.repository.house;

import com.bubble.house.entity.house.HousePictureEntity;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

/**
 * house 图片
 *
 * @author wugang
 * date: 2019-11-05 18:42
 **/
public interface HousePictureRepository extends CrudRepository<HousePictureEntity, Long> {

    /**
     * 根据houseId获取图片集合
     *
     * @param id houseId
     * @return List<HousePictureEntity>
     */
    List<HousePictureEntity> findAllByHouseId(Long id);

}
