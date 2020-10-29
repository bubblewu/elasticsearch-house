package com.bubble.house.service.house;

import com.bubble.house.service.ServiceMultiResultEntity;
import com.bubble.house.service.ServiceResultEntity;
import com.bubble.house.web.dto.house.HouseDTO;
import com.bubble.house.web.param.DatatableSearchParam;
import com.bubble.house.web.param.HouseParam;
import com.bubble.house.web.param.MapSearchParam;
import com.bubble.house.web.param.RentSearchParam;

/**
 * 房屋管理相关服务接口
 *
 * @author wugang
 * date: 2019-11-05 18:27
 **/
public interface HouseService {

    /**
     * 新增House信息
     *
     * @param houseParam 房屋信息
     * @return ServiceResultEntity<HouseDTO>
     */
    ServiceResultEntity<HouseDTO> save(HouseParam houseParam);

    /**
     * 房源数据更新
     *
     * @param houseParam 房屋信息
     * @return ServiceResultEntity
     */
    ServiceResultEntity update(HouseParam houseParam);


    /**
     * House信息展示：分页、筛选、搜索
     *
     * @param searchBody searchBody
     * @return ServiceMultiResultEntity<HouseDTO>
     */
    ServiceMultiResultEntity<HouseDTO> adminQuery(DatatableSearchParam searchBody);

    /**
     * 查询完整房源信息
     *
     * @param id 房源ID
     * @return ServiceResultEntity<HouseDTO>
     */
    ServiceResultEntity<HouseDTO> findCompleteOne(Long id);

    /**
     * 删除房源图片
     *
     * @param id 房源ID
     * @return ServiceResultEntity
     */
    ServiceResultEntity removePhoto(Long id);

    /**
     * 更新房源封面
     *
     * @param coverId  封面ID
     * @param targetId 图片ID
     * @return ServiceResultEntity
     */
    ServiceResultEntity updateCover(Long coverId, Long targetId);

    /**
     * 新增标签
     *
     * @param houseId 房源ID
     * @param tag     标签
     * @return ServiceResultEntity
     */
    ServiceResultEntity addTag(Long houseId, String tag);

    /**
     * 移除标签
     *
     * @param houseId 房源ID
     * @param tag     标签
     * @return ServiceResultEntity
     */
    ServiceResultEntity removeTag(Long houseId, String tag);

    /**
     * 更新房源状态
     *
     * @param id     房源ID
     * @param status 房屋状态 0-未审核 1-审核通过 2-已出租 3-逻辑删除
     * @return ServiceResultEntity
     */
    ServiceResultEntity updateStatus(Long id, int status);


    /**
     * 查询房源信息集
     *
     * @param rentSearch 查询参数RentSearchParam
     * @return ServiceMultiResultEntity<HouseDTO>
     */
    ServiceMultiResultEntity<HouseDTO> query(RentSearchParam rentSearch);


}
