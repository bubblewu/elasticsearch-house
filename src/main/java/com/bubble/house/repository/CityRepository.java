package com.bubble.house.repository;

import com.bubble.house.entity.house.CityEntity;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

/**
 * 城市信息
 *
 * @author wugang
 * date: 2019-11-05 16:34
 **/
public interface CityRepository extends CrudRepository<CityEntity, Long> {

    /**
     * 获取所有对应行政级别的信息
     */
    List<CityEntity> findAllByLevel(String level);

    CityEntity findByEnNameAndLevel(String enName, String level);

    CityEntity findByEnNameAndBelongTo(String enName, String belongTo);

    List<CityEntity> findAllByLevelAndBelongTo(String level, String belongTo);

}
