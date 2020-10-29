package com.bubble.house.service.house;

import com.bubble.house.entity.house.HouseSubscribeStatus;
import com.bubble.house.service.ServiceMultiResultEntity;
import com.bubble.house.service.ServiceResultEntity;
import com.bubble.house.web.dto.house.HouseDTO;
import com.bubble.house.web.dto.house.HouseSubscribeDTO;
import org.springframework.data.util.Pair;

import java.util.Date;

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

    /**
     * 获取对应状态的预约列表
     *
     * @param status 预约状态
     * @param start  开始
     * @param size   大小
     * @return ServiceMultiResultEntity<Pair < HouseDTO, HouseSubscribeDTO>>
     */
    ServiceMultiResultEntity<Pair<HouseDTO, HouseSubscribeDTO>> querySubscribeList(HouseSubscribeStatus status, int start, int size);

    /**
     * 预约看房
     *
     * @param houseId   房源ID
     * @param orderTime 预约时间
     * @param telephone 手机号
     * @param desc      用户描述
     * @return ServiceResultEntity
     */
    ServiceResultEntity subscribe(Long houseId, Date orderTime, String telephone, String desc);

    /**
     * 取消预约
     *
     * @param houseId 房源ID
     * @return ServiceResultEntity
     */
    ServiceResultEntity cancelSubscribe(Long houseId);

    /**
     * 管理员查询预约信息接口
     *
     * @param start 开始
     * @param size  大小
     * @return ServiceMultiResultEntity<Pair < HouseDTO, HouseSubscribeDTO>>
     */
    ServiceMultiResultEntity<Pair<HouseDTO, HouseSubscribeDTO>> findSubscribeList(int start, int size);

    /**
     * 完成预约
     *
     * @param houseId 房源ID
     * @return ServiceResultEntity
     */
    ServiceResultEntity finishSubscribe(Long houseId);

}
