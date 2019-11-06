package com.bubble.house.repository;

import com.bubble.house.entity.house.HouseSubscribeEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;

/**
 * 看房
 *
 * @author wugang
 * date: 2019-11-05 18:43
 **/
public interface HouseSubscribeRepository extends PagingAndSortingRepository<HouseSubscribeEntity, Long> {

    HouseSubscribeEntity findByHouseIdAndUserId(Long houseId, Long loginUserId);

    Page<HouseSubscribeEntity> findAllByUserIdAndStatus(Long userId, int status, Pageable pageable);

    Page<HouseSubscribeEntity> findAllByAdminIdAndStatus(Long adminId, int status, Pageable pageable);

    HouseSubscribeEntity findByHouseIdAndAdminId(Long houseId, Long adminId);

    @Modifying
    @Query("update HouseSubscribeEntity as subscribe set subscribe.status = :status where subscribe.id = :id")
    void updateStatus(@Param(value = "id") Long id, @Param(value = "status") int status);
}