package com.bubble.house.repository.house;

import com.bubble.house.entity.house.HouseSubscribeEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;

/**
 * 预约看房
 *
 * @author wugang
 * date: 2019-11-05 18:43
 **/
public interface HouseSubscribeRepository extends PagingAndSortingRepository<HouseSubscribeEntity, Long> {

    /**
     * 根据houseId和loginUserId获取房屋约看信息
     *
     * @param houseId     房屋ID
     * @param loginUserId 登录用户ID
     * @return 房屋约看信息
     */
    HouseSubscribeEntity findByHouseIdAndUserId(Long houseId, Long loginUserId);

    /**
     * 根据用户ID和status获取看房信息
     *
     * @param userId   登录用户ID
     * @param status   预约状态 1-加入待看清单 2-已预约看房时间 3-看房完成
     * @param pageable Pageable翻页
     * @return 房屋约看信息的分页Page<HouseSubscribeEntity>
     */
    Page<HouseSubscribeEntity> findAllByUserIdAndStatus(Long userId, int status, Pageable pageable);

    /**
     * 根据管理员ID和status获取看房信息
     *
     * @param adminId  管理员ID：房源发布者id
     * @param status   预约状态 1-加入待看清单 2-已预约看房时间 3-看房完成
     * @param pageable Pageable翻页
     * @return 房屋约看信息的分页Page<HouseSubscribeEntity>
     */
    Page<HouseSubscribeEntity> findAllByAdminIdAndStatus(Long adminId, int status, Pageable pageable);

    /**
     * 根据adminId和houseId获取房屋约看信息
     *
     * @param houseId 房屋ID
     * @param adminId 管理员ID：房源发布者
     * @return 房屋约看信息的分页
     */
    HouseSubscribeEntity findByHouseIdAndAdminId(Long houseId, Long adminId);

    /**
     * 更新房屋的约看状态
     *
     * @param id     house_subscribe表主键ID
     * @param status 预约状态 1-加入待看清单 2-已预约看房时间 3-看房完成
     */
    @Modifying
    @Query("update HouseSubscribeEntity as subscribe set subscribe.status = :status where subscribe.id = :id")
    void updateStatus(@Param(value = "id") Long id, @Param(value = "status") int status);
}