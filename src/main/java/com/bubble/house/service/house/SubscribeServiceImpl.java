package com.bubble.house.service.house;

import com.bubble.house.base.util.HouseUtils;
import com.bubble.house.base.util.LoginUserUtils;
import com.bubble.house.entity.house.HouseEntity;
import com.bubble.house.entity.house.HouseSubscribeEntity;
import com.bubble.house.entity.house.HouseSubscribeStatus;
import com.bubble.house.repository.house.HouseRepository;
import com.bubble.house.repository.house.HouseSubscribeRepository;
import com.bubble.house.service.ServiceMultiResultEntity;
import com.bubble.house.service.ServiceResultEntity;
import com.bubble.house.web.dto.house.HouseDTO;
import com.bubble.house.web.dto.house.HouseSubscribeDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.util.Pair;
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
    private final HouseUtils houseUtils;

    public SubscribeServiceImpl(HouseSubscribeRepository subscribeRepository, HouseRepository houseRepository,
                                HouseUtils houseUtils) {
        this.subscribeRepository = subscribeRepository;
        this.houseRepository = houseRepository;
        this.houseUtils = houseUtils;
    }

    @Override
    @Transactional
    public ServiceResultEntity addSubscribeOrder(Long houseId) {
        Long userId = LoginUserUtils.getLoginUserId();
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

    @Override
    public ServiceMultiResultEntity<Pair<HouseDTO, HouseSubscribeDTO>> querySubscribeList(HouseSubscribeStatus status, int start, int size) {
        Long userId = LoginUserUtils.getLoginUserId();
        String[] properties = new String[]{"createTime"};
        Pageable pageable = PageRequest.of(start / size,
                size,
                Sort.by(Sort.Direction.DESC, properties)
        );
        Page<HouseSubscribeEntity> page = subscribeRepository.findAllByUserIdAndStatus(userId, status.getValue(), pageable);
        return houseUtils.wrapper(page);
    }

    @Override
    @Transactional
    public ServiceResultEntity subscribe(Long houseId, Date orderTime, String telephone, String desc) {
        Long userId = LoginUserUtils.getLoginUserId();
        HouseSubscribeEntity subscribe = subscribeRepository.findByHouseIdAndUserId(houseId, userId);
        if (subscribe == null) {
            return new ServiceResultEntity(false, "无预约记录");
        }
        if (subscribe.getStatus() != HouseSubscribeStatus.IN_ORDER_LIST.getValue()) {
            return new ServiceResultEntity(false, "无法预约");
        }
        subscribe.setStatus(HouseSubscribeStatus.IN_ORDER_TIME.getValue());
        subscribe.setLastUpdateTime(new Date());
        subscribe.setTelephone(telephone);
        subscribe.setDesc(desc);
        subscribe.setOrderTime(orderTime);
        subscribeRepository.save(subscribe);
        return ServiceResultEntity.success();
    }

    @Override
    @Transactional
    public ServiceResultEntity cancelSubscribe(Long houseId) {
        Long userId = LoginUserUtils.getLoginUserId();
        HouseSubscribeEntity subscribe = subscribeRepository.findByHouseIdAndUserId(houseId, userId);
        if (subscribe == null) {
            return new ServiceResultEntity(false, "无预约记录");
        }
        subscribeRepository.deleteById(subscribe.getId());
        return ServiceResultEntity.success();
    }

    @Override
    public ServiceMultiResultEntity<Pair<HouseDTO, HouseSubscribeDTO>> findSubscribeList(int start, int size) {
        Long userId = LoginUserUtils.getLoginUserId();
        String[] properties = new String[]{"orderTime"};
        Pageable pageable = PageRequest.of(start / size, size,
                Sort.by(Sort.Direction.DESC, properties));
        Page<HouseSubscribeEntity> page = subscribeRepository.findAllByAdminIdAndStatus(userId, HouseSubscribeStatus.IN_ORDER_TIME.getValue(), pageable);
        return houseUtils.wrapper(page);
    }

    @Override
    @Transactional
    public ServiceResultEntity finishSubscribe(Long houseId) {
        Long adminId = LoginUserUtils.getLoginUserId();
        HouseSubscribeEntity subscribe = subscribeRepository.findByHouseIdAndAdminId(houseId, adminId);
        if (subscribe == null) {
            return new ServiceResultEntity(false, "无预约记录");
        }
        subscribeRepository.updateStatus(subscribe.getId(), HouseSubscribeStatus.FINISH.getValue());
        houseRepository.updateWatchTimes(houseId);
        return ServiceResultEntity.success();
    }
}
