package com.bubble.house.service.house;

import com.bubble.house.base.LoginUserUtil;
import com.bubble.house.entity.house.HouseEntity;
import com.bubble.house.entity.house.HouseSubscribeEntity;
import com.bubble.house.entity.house.HouseSubscribeStatus;
import com.bubble.house.entity.result.ServiceResultEntity;
import com.bubble.house.repository.HouseRepository;
import com.bubble.house.repository.HouseSubscribeRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.Optional;

/**
 * 房源预约管理服务实现
 *
 * @author wugang
 * date: 2020-10-27 18:14
 **/
@Service
public class SubscribeServiceImpl implements SubscribeService {
    private final Logger logger = LoggerFactory.getLogger(SubscribeServiceImpl.class);

    private final HouseSubscribeRepository subscribeRepository;
    private final HouseRepository houseRepository;

    public SubscribeServiceImpl(HouseSubscribeRepository subscribeRepository, HouseRepository houseRepository) {
        this.subscribeRepository = subscribeRepository;
        this.houseRepository = houseRepository;
    }

    @Override
    @Transactional
    public ServiceResultEntity addSubscribeOrder(Long houseId) {
        Long userId = LoginUserUtil.getLoginUserId();
        HouseSubscribeEntity subscribe = subscribeRepository.findByHouseIdAndUserId(houseId, userId);
        if (subscribe != null) {
            return new ServiceResultEntity(false, "已加入预约");
        }

        Optional<HouseEntity> houseOp = houseRepository.findById(houseId);
        if (!houseOp.isPresent()) {
            return new ServiceResultEntity(false, "查无此房");
        }
        HouseEntity house = houseOp.get();
        subscribe = new HouseSubscribeEntity();
        Date now = new Date();
        subscribe.setCreateTime(now);
        subscribe.setLastUpdateTime(now);
        subscribe.setUserId(userId);
        subscribe.setHouseId(houseId);
        subscribe.setStatus(HouseSubscribeStatus.IN_ORDER_LIST.getValue());
        subscribe.setAdminId(house.getAdminId());
        subscribeRepository.save(subscribe);
        return ServiceResultEntity.success();
    }
}
