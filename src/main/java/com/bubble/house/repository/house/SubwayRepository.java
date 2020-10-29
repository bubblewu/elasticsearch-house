package com.bubble.house.repository.house;

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

    /**
     * 根据城市英文名查找地铁信息集合
     *
     * @param cityEnName 城市英文名
     * @return 地铁信息集合
     */
    List<SubwayEntity> findAllByCityEnName(String cityEnName);

}
