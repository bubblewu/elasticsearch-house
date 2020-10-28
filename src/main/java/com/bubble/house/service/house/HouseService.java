package com.bubble.house.service.house;

import com.bubble.house.web.dto.HouseDTO;
import com.bubble.house.web.param.DatatableSearchParam;
import com.bubble.house.web.param.HouseParam;
import com.bubble.house.web.param.RentSearchParam;
import com.bubble.house.service.ServiceMultiResultEntity;
import com.bubble.house.service.ServiceResultEntity;
import com.bubble.house.web.param.MapSearchParam;

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
    ServiceResultEntity<HouseDTO> save(HouseParam houseParam);

    /**
     * House信息展示：分页、筛选、搜索
     */
    ServiceMultiResultEntity<HouseDTO> adminQuery(DatatableSearchParam searchBody);

    /**
     * 查询完整房源信息
     */
    ServiceResultEntity<HouseDTO> findCompleteOne(Long id);

    /**
     * 数据更新
     */
    ServiceResultEntity update(HouseParam houseParam);

    /**
     * 移除图片
     */
    ServiceResultEntity removePhoto(Long id);

    /**
     * 更新封面
     */
    ServiceResultEntity updateCover(Long coverId, Long targetId);

    /**
     * 新增标签
     */
    ServiceResultEntity addTag(Long houseId, String tag);

    /**
     * 移除标签
     */
    ServiceResultEntity removeTag(Long houseId, String tag);

    /**
     * 更新房源状态
     */
    ServiceResultEntity updateStatus(Long id, int status);


    /**
     * 查询房源信息集
     */
    ServiceMultiResultEntity<HouseDTO> query(RentSearchParam rentSearch);

    /**
     * 全地图查询
     */
    ServiceMultiResultEntity<HouseDTO> wholeMapQuery(MapSearchParam mapSearch);

    /**
     * 精确范围数据查询
     */
    ServiceMultiResultEntity<HouseDTO> boundMapQuery(MapSearchParam mapSearch);


}
