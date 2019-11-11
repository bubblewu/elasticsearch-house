package com.bubble.house.service.search;

import com.bubble.house.entity.param.RentSearchParam;
import com.bubble.house.entity.result.MultiResultEntity;
import com.bubble.house.entity.result.ResultEntity;
import com.bubble.house.entity.search.HouseBucketEntity;
import com.bubble.house.entity.search.MapSearchEntity;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 搜索服务
 *
 * @author wugang
 * date: 2019-11-08 18:22
 **/
@Service
public class SearchServiceImpl implements SearchService {

    @Override
    public void index(Long houseId) {

    }

    @Override
    public void remove(Long houseId) {

    }

    @Override
    public MultiResultEntity<Long> query(RentSearchParam rentSearch) {
        return null;
    }

    @Override
    public ResultEntity<List<String>> suggest(String prefix) {
        return null;
    }

    @Override
    public ResultEntity<Long> aggregateDistrictHouse(String cityEnName, String regionEnName, String district) {
        return null;
    }

    @Override
    public MultiResultEntity<HouseBucketEntity> mapAggregate(String cityEnName) {
        return null;
    }

    @Override
    public MultiResultEntity<Long> mapQuery(String cityEnName, String orderBy, String orderDirection, int start, int size) {
        return null;
    }

    @Override
    public MultiResultEntity<Long> mapQuery(MapSearchEntity mapSearch) {
        return null;
    }
}
