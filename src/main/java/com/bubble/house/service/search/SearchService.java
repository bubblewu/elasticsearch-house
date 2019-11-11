package com.bubble.house.service.search;

import com.bubble.house.entity.param.RentSearchParam;
import com.bubble.house.entity.result.MultiResultEntity;
import com.bubble.house.entity.result.ResultEntity;
import com.bubble.house.entity.search.HouseBucketEntity;
import com.bubble.house.entity.search.MapSearchEntity;

import java.util.List;

/**
 * 搜索服务
 *
 * @author wugang
 * date: 2019-11-08 18:17
 **/
public interface SearchService {

    /**
     * 索引目标房源
     */
    void index(Long houseId);

    /**
     * 移除房源索引
     */
    void remove(Long houseId);

    /**
     * 查询房源接口
     */
    MultiResultEntity<Long> query(RentSearchParam rentSearch);

    /**
     * 获取补全建议关键词
     */
    ResultEntity<List<String>> suggest(String prefix);

    /**
     * 聚合特定小区的房间数
     */
    ResultEntity<Long> aggregateDistrictHouse(String cityEnName, String regionEnName, String district);

    /**
     * 聚合城市数据
     */
    MultiResultEntity<HouseBucketEntity> mapAggregate(String cityEnName);

    /**
     * 城市级别查询
     */
    MultiResultEntity<Long> mapQuery(String cityEnName, String orderBy,
                                     String orderDirection, int start, int size);

    /**
     * 精确范围数据查询
     */
    MultiResultEntity<Long> mapQuery(MapSearchEntity mapSearch);

}
