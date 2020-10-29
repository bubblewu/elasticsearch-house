package com.bubble.house.repository.house;

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

    /**
     * 根据houseId更新封面
     *
     * @param id    houseId
     * @param cover 封面
     */
    @Modifying
    @Query("update HouseEntity as house set house.cover = :cover where house.id = :id")
    void updateCover(@Param(value = "id") Long id, @Param(value = "cover") String cover);

    /**
     * 根据houseId更新房屋状态
     *
     * @param id     houseId
     * @param status 房屋状态 0-未审核 1-审核通过 2-已出租 3-逻辑删除
     */
    @Modifying
    @Query("update HouseEntity as house set house.status = :status where house.id = :id")
    void updateStatus(@Param(value = "id") Long id, @Param(value = "status") int status);

    /**
     * 根据houseId更新被看次数
     *
     * @param houseId houseId
     */
    @Modifying
    @Query("update HouseEntity as house set house.watchTimes = house.watchTimes + 1 where house.id = :id")
    void updateWatchTimes(@Param(value = "id") Long houseId);

}
