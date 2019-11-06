package com.bubble.house.repository;

import com.bubble.house.entity.house.HouseEntity;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;

/**
 * House信息
 *
 * @author wugang
 * date: 2019-11-05 18:35
 **/
public interface HouseRepository extends PagingAndSortingRepository<HouseEntity, Long>, JpaSpecificationExecutor<HouseEntity> {

    @Modifying
    @Query("update HouseEntity as house set house.cover = :cover where house.id = :id")
    void updateCover(@Param(value = "id") Long id, @Param(value = "cover") String cover);

    @Modifying
    @Query("update HouseEntity as house set house.status = :status where house.id = :id")
    void updateStatus(@Param(value = "id") Long id, @Param(value = "status") int status);

    @Modifying
    @Query("update HouseEntity as house set house.watchTimes = house.watchTimes + 1 where house.id = :id")
    void updateWatchTimes(@Param(value = "id") Long houseId);

}
