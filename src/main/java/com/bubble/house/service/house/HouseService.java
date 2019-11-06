package com.bubble.house.service.house;

import com.bubble.house.entity.dto.HouseDTO;
import com.bubble.house.entity.param.HouseParam;
import com.bubble.house.entity.result.ResultEntity;

/**
 * House相关服务接口
 *
 * @author wugang
 * date: 2019-11-05 18:27
 **/
public interface HouseService {
    /**
     * 新增House信息
     */
    ResultEntity<HouseDTO> save(HouseParam houseParam);

}
