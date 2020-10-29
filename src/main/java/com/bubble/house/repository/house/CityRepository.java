package com.bubble.house.repository.house;

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
     * 根据行政级别获取城市集合信息
     *
     * @param level 行政级别：市-city 地区-region
     * @return 城市集合
     */
    List<CityEntity> findAllByLevel(String level);

    /**
     * 根据城市英文名和行政级别获取城市信息
     *
     * @param enName 城市英文名
     * @param level  行政级别：市-city 地区-region
     * @return 城市信息
     */
    CityEntity findByEnNameAndLevel(String enName, String level);

    /**
     * 根据城市英文名和上一级行政单位名获取城市信息
     *
     * @param enName   城市英文名
     * @param belongTo 上一级行政单位名
     * @return 城市信息
     */
    CityEntity findByEnNameAndBelongTo(String enName, String belongTo);

    /**
     * 根据行政级别和上一级行政单位名获取城市集合信息
     *
     * @param level    行政级别：市-city 地区-region
     * @param belongTo 上一级行政单位名
     * @return 城市集合
     */
    List<CityEntity> findAllByLevelAndBelongTo(String level, String belongTo);

}
