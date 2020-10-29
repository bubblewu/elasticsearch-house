package com.bubble.house.service.search;

import com.bubble.house.entity.search.HouseBucketEntity;
import com.bubble.house.service.ServiceMultiResultEntity;
import com.bubble.house.service.ServiceResultEntity;
import com.bubble.house.web.param.MapSearchParam;
import com.bubble.house.web.param.RentSearchParam;

import java.util.List;

/**
 * 搜索服务
 *
 * @author wugang
 * date: 2019-11-08 18:17
 **/
public interface SearchService {

    /**
     * 为目标房源建立索引
     *
     * @param houseId 房源ID
     */
    void index(Long houseId);

    /**
     * 移除房源索引
     *
     * @param houseId 房源ID
     */
    void remove(Long houseId);

    /**
     * 查询房源接口
     *
     * @param rentSearch 搜索参数
     * @return ServiceMultiResultEntity<Long> 房源ID集合
     */
    ServiceMultiResultEntity<Long> query(RentSearchParam rentSearch);

    /**
     * 获取补全建议关键词
     *
     * @param prefix 搜索前缀
     * @return 补全关键字集合 ServiceResultEntity<List<String>>
     */
    ServiceResultEntity<List<String>> suggest(String prefix);

    /**
     * 聚合特定小区的房源数
     *
     * @param cityEnName   城市英文名
     * @param regionEnName 区县英文名
     * @param district     所在小区
     * @return 房源集合ServiceResultEntity<Long>
     */
    ServiceResultEntity<Long> aggregateDistrictHouse(String cityEnName, String regionEnName, String district);

    /**
     * 聚合城市数据
     *
     * @param cityEnName 城市英文名
     * @return ServiceMultiResultEntity<HouseBucketEntity>
     */
    ServiceMultiResultEntity<HouseBucketEntity> mapAggregate(String cityEnName);

    /**
     * 根据城市和其他条件查询
     *
     * @param cityEnName     城市英文名
     * @param orderBy        排序
     * @param orderDirection 朝向
     * @param start          开始
     * @param size           大小
     * @return ServiceMultiResultEntity<Long>
     */
    ServiceMultiResultEntity<Long> mapQuery(String cityEnName, String orderBy,
                                            String orderDirection, int start, int size);

    /**
     * 精确范围数据GEO查询
     *
     * @param mapSearch map查询参数
     * @return ServiceMultiResultEntity<Long>
     */
    ServiceMultiResultEntity<Long> mapQuery(MapSearchParam mapSearch);

}
