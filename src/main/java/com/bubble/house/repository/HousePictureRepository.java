package com.bubble.house.repository;

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
    List<HousePictureEntity> findAllByHouseId(Long id);
}
