package com.bubble.house.base.util;

import com.bubble.house.entity.house.HouseDetailEntity;
import com.bubble.house.entity.house.HouseEntity;
import com.bubble.house.entity.house.HouseSubscribeEntity;
import com.bubble.house.entity.house.HouseTagEntity;
import com.bubble.house.repository.house.HouseDetailRepository;
import com.bubble.house.repository.house.HouseRepository;
import com.bubble.house.repository.house.HouseTagRepository;
import com.bubble.house.service.ServiceMultiResultEntity;
import com.bubble.house.web.dto.house.HouseDTO;
import com.bubble.house.web.dto.house.HouseDetailDTO;
import com.bubble.house.web.dto.house.HouseSubscribeDTO;
import com.google.common.collect.Lists;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.util.Pair;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author wugang
 * date: 2020-10-29 17:30
 **/
@Component
public class HouseUtils {

    @Value("${qiniu.cdn.prefix}")
    private String cdnPrefix;
    private final ModelMapper modelMapper;
    private final HouseRepository houseRepository;
    private final HouseDetailRepository houseDetailRepository;
    private final HouseTagRepository houseTagRepository;

    public HouseUtils(ModelMapper modelMapper, HouseRepository houseRepository,
                      HouseDetailRepository houseDetailRepository, HouseTagRepository houseTagRepository) {
        this.modelMapper = modelMapper;
        this.houseRepository = houseRepository;
        this.houseDetailRepository = houseDetailRepository;
        this.houseTagRepository = houseTagRepository;
    }

    /**
     * 渲染完整的HouseDTO信息
     *
     * @param houseIds 房源ID集合
     * @return List<HouseDTO>
     */
    public List<HouseDTO> wrapperHouseResult(List<Long> houseIds) {
        List<HouseDTO> result = Lists.newArrayList();
        Map<Long, HouseDTO> idToHouseMap = new HashMap<>();
        Iterable<HouseEntity> houses = houseRepository.findAllById(houseIds);
        houses.forEach(house -> {
            HouseDTO houseDTO = modelMapper.map(house, HouseDTO.class);
            houseDTO.setCover(this.cdnPrefix + house.getCover());
            idToHouseMap.put(house.getId(), houseDTO);
        });

        wrapperHouseList(houseIds, idToHouseMap);
        // 矫正顺序
        for (Long houseId : houseIds) {
            result.add(idToHouseMap.get(houseId));
        }
        return result;
    }

    /**
     * 渲染详细信息 及 标签
     *
     * @param houseIds     房源ID
     * @param idToHouseMap Map<Long, HouseDTO>
     */
    private void wrapperHouseList(List<Long> houseIds, Map<Long, HouseDTO> idToHouseMap) {
        List<HouseDetailEntity> details = houseDetailRepository.findAllByHouseIdIn(houseIds);
        details.forEach(houseDetail -> {
            HouseDTO houseDTO = idToHouseMap.get(houseDetail.getHouseId());
            HouseDetailDTO detailDTO = modelMapper.map(houseDetail, HouseDetailDTO.class);
            houseDTO.setHouseDetail(detailDTO);
        });
        List<HouseTagEntity> houseTags = houseTagRepository.findAllByHouseIdIn(houseIds);
        houseTags.forEach(houseTag -> {
            HouseDTO house = idToHouseMap.get(houseTag.getHouseId());
            house.getTags().add(houseTag.getName());
        });
    }


    /**
     * 预约看房信息包装
     *
     * @param page Page<HouseSubscribeEntity>
     * @return ServiceMultiResultEntity<Pair < HouseDTO, HouseSubscribeDTO>>
     */
    public ServiceMultiResultEntity<Pair<HouseDTO, HouseSubscribeDTO>> wrapper(Page<HouseSubscribeEntity> page) {
        List<Pair<HouseDTO, HouseSubscribeDTO>> result = new ArrayList<>();
        if (page.getSize() < 1) {
            return new ServiceMultiResultEntity<>(page.getTotalElements(), result);
        }
        List<HouseSubscribeDTO> subscribeDTOS = new ArrayList<>();
        List<Long> houseIds = new ArrayList<>();
        page.forEach(houseSubscribe -> {
            subscribeDTOS.add(modelMapper.map(houseSubscribe, HouseSubscribeDTO.class));
            houseIds.add(houseSubscribe.getHouseId());
        });

        Map<Long, HouseDTO> idToHouseMap = new HashMap<>();
        Iterable<HouseEntity> houses = houseRepository.findAllById(houseIds);
        houses.forEach(house -> {
            idToHouseMap.put(house.getId(), modelMapper.map(house, HouseDTO.class));
        });
        for (HouseSubscribeDTO subscribeDTO : subscribeDTOS) {
            Pair<HouseDTO, HouseSubscribeDTO> pair = Pair.of(idToHouseMap.get(subscribeDTO.getHouseId()), subscribeDTO);
            result.add(pair);
        }
        return new ServiceMultiResultEntity<>(page.getTotalElements(), result);
    }

}
