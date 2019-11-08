package com.bubble.house.service.house;

import com.bubble.house.entity.dto.HouseDTO;
import com.bubble.house.entity.param.DatatableSearchParam;
import com.bubble.house.entity.param.HouseParam;
import com.bubble.house.entity.result.MultiResultEntity;
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

    /**
     * House信息展示：分页、筛选、搜索
     */
    MultiResultEntity<HouseDTO> adminQuery(DatatableSearchParam searchBody);

    /**
     * 查询完整房源信息
     */
    ResultEntity<HouseDTO> findCompleteOne(Long id);

    /**
     * 数据更新
     */
    ResultEntity update(HouseParam houseParam);

    /**
     * 移除图片
     */
    ResultEntity removePhoto(Long id);

    /**
     * 更新封面
     */
    ResultEntity updateCover(Long coverId, Long targetId);

    /**
     * 新增标签
     */
    ResultEntity addTag(Long houseId, String tag);

    /**
     * 移除标签
     */
    ResultEntity removeTag(Long houseId, String tag);

    /**
     * 更新房源状态
     */
    ResultEntity updateStatus(Long id, int status);

}
