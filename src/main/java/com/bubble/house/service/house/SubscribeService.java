package com.bubble.house.service.house;

import com.bubble.house.entity.result.ServiceResultEntity;

/**
 * 预约管理服务
 *
 * @author wugang
 * date: 2020-10-27 18:13
 **/
public interface SubscribeService {

    /* --- 房源预约管理 --- */

    /**
     * 加入预约清单
     *
     * @param houseId 房源ID
     * @return ResultEntity
     */
    ServiceResultEntity addSubscribeOrder(Long houseId);

}
