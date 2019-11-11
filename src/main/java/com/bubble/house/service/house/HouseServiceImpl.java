package com.bubble.house.service.house;

import com.bubble.house.base.HouseSort;
import com.bubble.house.base.ToolKits;
import com.bubble.house.entity.dto.HouseDTO;
import com.bubble.house.entity.dto.HouseDetailDTO;
import com.bubble.house.entity.dto.HousePictureDTO;
import com.bubble.house.entity.house.*;
import com.bubble.house.entity.param.DatatableSearchParam;
import com.bubble.house.entity.param.HouseParam;
import com.bubble.house.entity.param.PhotoParam;
import com.bubble.house.entity.param.RentSearchParam;
import com.bubble.house.entity.result.MultiResultEntity;
import com.bubble.house.entity.result.ResultEntity;
import com.bubble.house.repository.*;
import com.qiniu.common.QiniuException;
import com.qiniu.http.Response;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.PostConstruct;
import javax.persistence.criteria.Predicate;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * House相关服务接口实现
 *
 * @author wugang
 * date: 2019-11-05 18:28
 **/
@Service
public class HouseServiceImpl implements HouseService {

    @Value("${qiniu.cdn.prefix}")
    private String cdnPrefix;
    private ModelMapper modelMapper;

    @PostConstruct
    public void init() {
        modelMapper = new ModelMapper();
    }

    private final SubwayRepository subwayRepository;
    private final SubwayStationRepository subwayStationRepository;
    private final HouseRepository houseRepository;
    private final HouseDetailRepository houseDetailRepository;
    private final HouseTagRepository houseTagRepository;
    private final HousePictureRepository housePictureRepository;
    private final HouseSubscribeRepository subscribeRepository;
    private final QiNiuService qiNiuService;

    public HouseServiceImpl(SubwayRepository subwayRepository, SubwayStationRepository subwayStationRepository,
                            HouseRepository houseRepository, HouseDetailRepository houseDetailRepository,
                            HouseTagRepository houseTagRepository, HousePictureRepository housePictureRepository,
                            HouseSubscribeRepository subscribeRepository, QiNiuService qiNiuService) {
        this.subwayRepository = subwayRepository;
        this.subwayStationRepository = subwayStationRepository;
        this.houseRepository = houseRepository;
        this.houseDetailRepository = houseDetailRepository;
        this.houseTagRepository = houseTagRepository;
        this.housePictureRepository = housePictureRepository;
        this.subscribeRepository = subscribeRepository;
        this.qiNiuService = qiNiuService;
    }

    @Override
    public ResultEntity<HouseDTO> save(HouseParam houseParam) {
        HouseDetailEntity detail = new HouseDetailEntity();
        ResultEntity<HouseDTO> subwayValidationResult = wrapperDetailInfo(detail, houseParam);
        if (subwayValidationResult != null) {
            return subwayValidationResult;
        }

        HouseEntity house = new HouseEntity();
        this.modelMapper.map(houseParam, house);

        Date now = new Date();
        house.setCreateTime(now);
        house.setLastUpdateTime(now);
        // 当前操作用户ID
        house.setAdminId(ToolKits.getLoginUserId());
        house = this.houseRepository.save(house);

        detail.setHouseId(house.getId());
        detail = this.houseDetailRepository.save(detail);

        List<HousePictureEntity> pictures = generatePictures(houseParam, house.getId());
        Iterable<HousePictureEntity> housePictures = this.housePictureRepository.saveAll(pictures);

        HouseDTO houseDTO = this.modelMapper.map(house, HouseDTO.class);
        HouseDetailDTO houseDetailDTO = this.modelMapper.map(detail, HouseDetailDTO.class);

        houseDTO.setHouseDetail(houseDetailDTO);

        List<HousePictureDTO> pictureDTOS = new ArrayList<>();
        housePictures.forEach(housePicture -> pictureDTOS.add(this.modelMapper.map(housePicture, HousePictureDTO.class)));
        houseDTO.setPictures(pictureDTOS);
        houseDTO.setCover(this.cdnPrefix + houseDTO.getCover());

        List<String> tags = houseParam.getTags();
        if (tags != null && !tags.isEmpty()) {
            List<HouseTagEntity> houseTags = new ArrayList<>();
            for (String tag : tags) {
                houseTags.add(new HouseTagEntity(house.getId(), tag));
            }
            this.houseTagRepository.saveAll(houseTags);
            houseDTO.setTags(tags);
        }

        return new ResultEntity<>(true, null, houseDTO);
    }

    /**
     * 图片对象列表信息填充
     */
    private List<HousePictureEntity> generatePictures(HouseParam houseParam, Long houseId) {
        List<HousePictureEntity> pictures = new ArrayList<>();
        if (houseParam.getPhotos() == null || houseParam.getPhotos().isEmpty()) {
            return pictures;
        }
        for (PhotoParam photoParam : houseParam.getPhotos()) {
            HousePictureEntity picture = new HousePictureEntity();
            picture.setHouseId(houseId);
            picture.setCdnPrefix(this.cdnPrefix);
            picture.setPath(photoParam.getPath());
            picture.setWidth(photoParam.getWidth());
            picture.setHeight(photoParam.getHeight());
            pictures.add(picture);
        }
        return pictures;
    }

    /**
     * 房源详细信息对象填充
     */
    private ResultEntity<HouseDTO> wrapperDetailInfo(HouseDetailEntity houseDetail, HouseParam houseParam) {
        Optional<SubwayEntity> subwayOp = this.subwayRepository.findById(houseParam.getSubwayLineId());
        if (subwayOp.isPresent()) {
            SubwayEntity subway = subwayOp.get();
            Optional<SubwayStationEntity> subwayStationOp = this.subwayStationRepository.findById(houseParam.getSubwayStationId());
            if (!subwayStationOp.isPresent() || !subway.getId().equals(subwayStationOp.get().getSubwayId())) {
                return new ResultEntity<>(false, "Not valid subway station!");
            } else {
                SubwayStationEntity subwayStation = subwayStationOp.get();

                houseDetail.setSubwayLineId(subway.getId());
                houseDetail.setSubwayLineName(subway.getName());

                houseDetail.setSubwayStationId(subwayStation.getId());
                houseDetail.setSubwayStationName(subwayStation.getName());

                houseDetail.setDescription(houseParam.getDescription());
                houseDetail.setDetailAddress(houseParam.getDetailAddress());
                houseDetail.setLayoutDesc(houseParam.getLayoutDesc());
                houseDetail.setRentWay(houseParam.getRentWay());
                houseDetail.setRoundService(houseParam.getRoundService());
                houseDetail.setTraffic(houseParam.getTraffic());
            }
        } else {
            return new ResultEntity<>(false, "Not valid subway line!");
        }
        return null;
    }

    @Override
    public MultiResultEntity<HouseDTO> adminQuery(DatatableSearchParam searchBody) {
        List<HouseDTO> houseDTOS = new ArrayList<>();
        // 分页查询
        Sort sort = Sort.by(Sort.Direction.fromString(searchBody.getDirection()), searchBody.getOrderBy());
        int page = searchBody.getStart() / searchBody.getLength();

        Pageable pageable = PageRequest.of(page, searchBody.getLength(), sort);
        // 添加搜索排序功能
        Specification<HouseEntity> specification = (root, query, cb) -> {
            // 只能查询当前用户下未删除的房源信息
            Predicate predicate = cb.equal(root.get("adminId"), ToolKits.getLoginUserId());
            predicate = cb.and(predicate, cb.notEqual(root.get("status"), HouseStatus.DELETED.getValue()));

            if (searchBody.getCity() != null) {
                predicate = cb.and(predicate, cb.equal(root.get("cityEnName"), searchBody.getCity()));
            }
            if (searchBody.getStatus() != null) {
                predicate = cb.and(predicate, cb.equal(root.get("status"), searchBody.getStatus()));
            }
            if (searchBody.getCreateTimeMin() != null) {
                predicate = cb.and(predicate, cb.greaterThanOrEqualTo(root.get("createTime"), searchBody.getCreateTimeMin()));
            }
            if (searchBody.getCreateTimeMax() != null) {
                predicate = cb.and(predicate, cb.lessThanOrEqualTo(root.get("createTime"), searchBody.getCreateTimeMax()));
            }
            if (searchBody.getTitle() != null) {
                predicate = cb.and(predicate, cb.like(root.get("title"), "%" + searchBody.getTitle() + "%"));
            }
            return predicate;
        };

        Page<HouseEntity> houses = houseRepository.findAll(specification, pageable);
//        Page<HouseEntity> houses = houseRepository.findAll(pageable);
        // 直接全部查询
//        Iterable<HouseEntity> houses = houseRepository.findAll();
        houses.forEach(house -> {
            HouseDTO houseDTO = modelMapper.map(house, HouseDTO.class);
            houseDTO.setCover(this.cdnPrefix + house.getCover());
            houseDTOS.add(houseDTO);
        });

        return new MultiResultEntity<>(houses.getTotalElements(), houseDTOS);
//        return new MultiResultEntity<>(houseDTOS.size(), houseDTOS);
    }

    @Override
    public ResultEntity<HouseDTO> findCompleteOne(Long id) {
        Optional<HouseEntity> houseOp = houseRepository.findById(id);
        if (!houseOp.isPresent()) {
            return ResultEntity.notFound();
        }
        HouseDetailEntity detail = houseDetailRepository.findByHouseId(id);
        List<HousePictureEntity> pictures = housePictureRepository.findAllByHouseId(id);

        HouseDetailDTO detailDTO = modelMapper.map(detail, HouseDetailDTO.class);
        List<HousePictureDTO> pictureDTOS = new ArrayList<>();
        for (HousePictureEntity picture : pictures) {
            HousePictureDTO pictureDTO = modelMapper.map(picture, HousePictureDTO.class);
            pictureDTOS.add(pictureDTO);
        }
        List<HouseTagEntity> tags = houseTagRepository.findAllByHouseId(id);
        List<String> tagList = new ArrayList<>();
        for (HouseTagEntity tag : tags) {
            tagList.add(tag.getName());
        }
        HouseEntity house = houseOp.get();
        HouseDTO result = modelMapper.map(house, HouseDTO.class);
        result.setHouseDetail(detailDTO);
        result.setPictures(pictureDTOS);
        result.setTags(tagList);

        if (ToolKits.getLoginUserId() > 0) { // 已登录用户
            HouseSubscribeEntity subscribe = subscribeRepository.findByHouseIdAndUserId(house.getId(), ToolKits.getLoginUserId());
            if (subscribe != null) {
                result.setSubscribeStatus(subscribe.getStatus());
            }
        }
        return ResultEntity.of(result);
    }

    @Override
    @Transactional
    public ResultEntity update(HouseParam houseParam) {
        Optional<HouseEntity> houseOp = this.houseRepository.findById(houseParam.getId());
        if (!houseOp.isPresent()) {
            return ResultEntity.notFound();
        }
        HouseEntity house = houseOp.get();
        HouseDetailEntity detail = this.houseDetailRepository.findByHouseId(house.getId());
        if (detail == null) {
            return ResultEntity.notFound();
        }
        ResultEntity wrapperResult = wrapperDetailInfo(detail, houseParam);
        if (wrapperResult != null) {
            return wrapperResult;
        }
        this.houseDetailRepository.save(detail);
        List<HousePictureEntity> pictures = generatePictures(houseParam, houseParam.getId());
        this.housePictureRepository.saveAll(pictures);

        if (houseParam.getCover() == null) {
            houseParam.setCover(house.getCover());
        }

        this.modelMapper.map(houseParam, house);
        house.setLastUpdateTime(new Date());
        houseRepository.save(house);

//        if (house.getStatus() == HouseStatus.PASSES.getValue()) {
//            this.searchService.index(house.getId());
//        }

        return ResultEntity.success();
    }

    @Override
    public ResultEntity removePhoto(Long id) {
        Optional<HousePictureEntity> pictureOp = this.housePictureRepository.findById(id);
        if (pictureOp.isPresent()) {
            try {
                Response response = this.qiNiuService.delete(pictureOp.get().getPath());
                if (response.isOK()) {
                    this.housePictureRepository.deleteById(id);
                    return ResultEntity.success();
                } else {
                    return new ResultEntity(false, response.error);
                }
            } catch (QiniuException e) {
                e.printStackTrace();
                return new ResultEntity(false, e.getMessage());
            }
        } else {
            return ResultEntity.notFound();
        }
    }

    @Override
    @Transactional
    public ResultEntity updateCover(Long coverId, Long targetId) {
        Optional<HousePictureEntity> coverOp = this.housePictureRepository.findById(coverId);
        if (!coverOp.isPresent()) {
            return ResultEntity.notFound();
        }
        this.houseRepository.updateCover(targetId, coverOp.get().getPath());
        return ResultEntity.success();
    }

    @Override
    @Transactional
    public ResultEntity addTag(Long houseId, String tag) {
        Optional<HouseEntity> houseOp = this.houseRepository.findById(houseId);
        if (!houseOp.isPresent()) {
            return ResultEntity.notFound();
        }

        HouseTagEntity houseTag = this.houseTagRepository.findByNameAndHouseId(tag, houseId);
        if (houseTag != null) {
            return new ResultEntity(false, "标签已存在");
        }
        this.houseTagRepository.save(new HouseTagEntity(houseId, tag));
        return ResultEntity.success();
    }

    @Override
    @Transactional
    public ResultEntity removeTag(Long houseId, String tag) {
        Optional<HouseEntity> houseOp = this.houseRepository.findById(houseId);
        if (!houseOp.isPresent()) {
            return ResultEntity.notFound();
        }

        HouseTagEntity houseTag = this.houseTagRepository.findByNameAndHouseId(tag, houseId);
        if (houseTag == null) {
            return new ResultEntity(false, "标签不存在");
        }

        this.houseTagRepository.deleteById(houseTag.getId());
        return ResultEntity.success();
    }


    @Override
    @Transactional
    public ResultEntity updateStatus(Long id, int status) {
        Optional<HouseEntity> houseOp = this.houseRepository.findById(id);
        if (!houseOp.isPresent()) {
            return ResultEntity.notFound();
        }
        HouseEntity house = houseOp.get();
        if (house.getStatus() == status) {
            return new ResultEntity(false, "状态没有发生变化");
        }
        if (house.getStatus() == HouseStatus.RENTED.getValue()) {
            return new ResultEntity(false, "已出租的房源不允许修改状态");
        }
        if (house.getStatus() == HouseStatus.DELETED.getValue()) {
            return new ResultEntity(false, "已删除的资源不允许操作");
        }
        this.houseRepository.updateStatus(id, status);

//        // 上架更新索引 其他情况都要删除索引
//        if (status == HouseStatus.PASSES.getValue()) {
//            searchService.index(id);
//        } else {
//            searchService.remove(id);
//        }
        return ResultEntity.success();
    }


    @Override
    public MultiResultEntity<HouseDTO> query(RentSearchParam rentSearch) {
//        Sort sort = Sort.by(Sort.Direction.DESC, "lastUpdateTime");
//        Sort sort;
//        String sortField;
//        if (rentSearch.getOrderBy().isEmpty()) {
//            sortField = "lastUpdateTime";
//        } else {
//            sortField = rentSearch.getOrderBy();
//        }
//        if ("asc".equals(rentSearch.getOrderDirection())) { // 升序
//            sort = Sort.by(Sort.Direction.ASC, sortField);
//        } else {
//            sort = Sort.by(Sort.Direction.DESC, sortField);
//        }
        Sort sort = HouseSort.generateSort(rentSearch.getOrderBy(), rentSearch.getOrderDirection());
        int page = rentSearch.getStart() / rentSearch.getSize();
        Pageable pageable = PageRequest.of(page, rentSearch.getSize(), sort);
        // 条件
        Specification specification = (Specification) (root, criteriaQuery, criteriaBuilder) -> {
            Predicate predicate = criteriaBuilder.equal(root.get("status"), HouseStatus.PASSES.getValue());
            predicate = criteriaBuilder.and(predicate, criteriaBuilder.equal(root.get("cityEnName"), rentSearch.getCityEnName()));
            return predicate;
        };
        Page<HouseEntity> houses = this.houseRepository.findAll(specification, pageable);

        List<HouseDTO> houseDTOS = new ArrayList<>();
        houses.forEach(house -> {
            HouseDTO houseDTO = this.modelMapper.map(house, HouseDTO.class);
            houseDTO.setCover(this.cdnPrefix + house.getCover());
            // house详情信息
            HouseDetailEntity houseDetailEntity = this.houseDetailRepository.findByHouseId(house.getId());
            HouseDetailDTO houseDetailDTO = this.modelMapper.map(houseDetailEntity, HouseDetailDTO.class);
            houseDTO.setHouseDetail(houseDetailDTO);
            // tags信息
            List<String> tags = this.houseTagRepository.findAllByHouseId(house.getId()).stream().map(HouseTagEntity::getName).collect(Collectors.toList());
            houseDTO.setTags(tags);
            // pictures
            List<HousePictureDTO> pictures = this.housePictureRepository.findAllByHouseId(house.getId()).stream().map(hp -> this.modelMapper.map(hp, HousePictureDTO.class)).collect(Collectors.toList());
            houseDTO.setPictures(pictures);
            houseDTOS.add(houseDTO);
        });
        return new MultiResultEntity<>(houses.getTotalElements(), houseDTOS);
    }

}
