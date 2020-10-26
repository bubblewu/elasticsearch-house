package com.bubble.house.service.search;

import com.bubble.house.base.HouseSort;
import com.bubble.house.base.search.HouseIndexConstants;
import com.bubble.house.base.search.HouseIndexMessage;
import com.bubble.house.base.search.HouseIndexTemplate;
import com.bubble.house.base.search.HouseSuggest;
import com.bubble.house.entity.BaiDuMapEntity;
import com.bubble.house.entity.house.*;
import com.bubble.house.entity.param.RentSearchParam;
import com.bubble.house.entity.result.MultiResultEntity;
import com.bubble.house.entity.result.ResultEntity;
import com.bubble.house.entity.search.HouseBucketEntity;
import com.bubble.house.entity.search.MapSearchEntity;
import com.bubble.house.entity.search.RentValueBlockEntity;
import com.bubble.house.repository.CityRepository;
import com.bubble.house.repository.HouseDetailRepository;
import com.bubble.house.repository.HouseRepository;
import com.bubble.house.repository.HouseTagRepository;
import com.bubble.house.service.house.AddressService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import org.apache.lucene.search.TotalHits;
import org.elasticsearch.action.DocWriteResponse;
import org.elasticsearch.action.delete.DeleteRequest;
import org.elasticsearch.action.delete.DeleteResponse;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.support.IndicesOptions;
import org.elasticsearch.action.support.replication.ReplicationResponse;
import org.elasticsearch.action.update.UpdateRequest;
import org.elasticsearch.action.update.UpdateResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.client.indices.AnalyzeRequest;
import org.elasticsearch.client.indices.AnalyzeResponse;
import org.elasticsearch.client.indices.CreateIndexRequest;
import org.elasticsearch.common.geo.GeoPoint;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.xcontent.XContentType;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.index.query.RangeQueryBuilder;
import org.elasticsearch.rest.RestStatus;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.search.aggregations.AggregationBuilder;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.aggregations.bucket.terms.Terms;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.search.sort.SortOrder;
import org.elasticsearch.search.suggest.Suggest;
import org.elasticsearch.search.suggest.SuggestBuilder;
import org.elasticsearch.search.suggest.SuggestBuilders;
import org.elasticsearch.search.suggest.SuggestionBuilder;
import org.elasticsearch.search.suggest.completion.CompletionSuggestion;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;

/**
 * 搜索服务
 *
 * @author wugang
 * date: 2019-11-08 18:22
 **/
@Service
public class SearchServiceImpl implements SearchService {
    private final static Logger LOGGER = LoggerFactory.getLogger(SearchServiceImpl.class);

    private final HouseRepository houseRepository;
    private final HouseDetailRepository houseDetailRepository;
    private final CityRepository cityRepository;
    private final HouseTagRepository tagRepository;
    private final AddressService addressService;
    private final RestHighLevelClient restHighLevelClient;
    private final KafkaTemplate<String, String> kafkaTemplate;

    public SearchServiceImpl(HouseRepository houseRepository, HouseDetailRepository houseDetailRepository,
                             CityRepository cityRepository, HouseTagRepository tagRepository, AddressService addressService,
                             RestHighLevelClient restHighLevelClient,
                             KafkaTemplate<String, String> kafkaTemplate) {
        this.houseRepository = houseRepository;
        this.houseDetailRepository = houseDetailRepository;
        this.cityRepository = cityRepository;
        this.tagRepository = tagRepository;
        this.addressService = addressService;
        this.restHighLevelClient = restHighLevelClient;
        this.kafkaTemplate = kafkaTemplate;
    }

    private ModelMapper modelMapper;
    private ObjectMapper objectMapper;

    @PostConstruct
    public void init() {
        this.modelMapper = new ModelMapper();
        this.objectMapper = new ObjectMapper();
    }

    @KafkaListener(topics = HouseIndexConstants.INDEX_TOPIC)
    private void handleMassage(String content) {
        try {
            HouseIndexMessage message = objectMapper.readValue(content, HouseIndexMessage.class);
            switch (message.getOperation()) {
                case HouseIndexMessage.INDEX:
                    this.createOrUpdateIndex(message);
                    break;
                case HouseIndexMessage.REMOVE:
                    this.removeIndex(message);
                    break;
                default:
                    LOGGER.error("[Kafka] 不支持的操作类型: {}", content);
                    break;
            }
        } catch (JsonProcessingException e) {
            LOGGER.error("[Kafka] Json处理异常: {}", content);
        }
    }

    private void removeIndex(HouseIndexMessage message) {
        Long houseId = message.getHouseId();
        DeleteRequest deleteRequest = new DeleteRequest(HouseIndexConstants.INDEX_NAME);
        deleteRequest.id(String.valueOf(houseId));
        LOGGER.debug("Delete Query: {}" + deleteRequest.toString());
        try {
            DeleteResponse deleteResponse = this.restHighLevelClient.delete(deleteRequest, RequestOptions.DEFAULT);
            ReplicationResponse.ShardInfo shardInfo = deleteResponse.getShardInfo();
            DocWriteResponse.Result result = deleteResponse.getResult();
            if (result == DocWriteResponse.Result.DELETED) {
                LOGGER.info("删除[{}]成功.", houseId);

                ResultEntity serviceResult = addressService.removeLbs(houseId);
                if (!serviceResult.isSuccess()) {
                    LOGGER.error("删除LBS data [{}]失败.", houseId);
                    // 重新加入消息队列
//                    this.remove(houseId, message.getRetry() + 1);
                }
            } else {
                LOGGER.info("删除[{}]失败.", houseId);
                // 重新加入消息队列
//                this.remove(houseId, message.getRetry() + 1);
            }
        } catch (IOException e) {
            LOGGER.info("删除[{}]异常.", houseId);
        }
    }

    private void createOrUpdateIndex(HouseIndexMessage message) {
        Long houseId = message.getHouseId();
        Optional<HouseEntity> houseOp = houseRepository.findById(houseId);
        if (!houseOp.isPresent()) {
            LOGGER.error("House: [{}]不存在", houseId);
            this.index(houseId, message.getRetry() + 1);
            return;
        }
        HouseEntity house = houseOp.get();
        HouseIndexTemplate indexTemplate = new HouseIndexTemplate();
        modelMapper.map(house, indexTemplate);
        HouseDetailEntity detail = houseDetailRepository.findByHouseId(houseId);
        if (detail == null) {
            LOGGER.error("Detail为NULL");
        }
        modelMapper.map(detail, indexTemplate);

        CityEntity city = cityRepository.findByEnNameAndLevel(house.getCityEnName(), CityLevel.CITY.getValue());
        CityEntity region = cityRepository.findByEnNameAndLevel(house.getRegionEnName(), CityLevel.REGION.getValue());

        String address = city.getCnName() + region.getCnName() + house.getStreet() + house.getDistrict() + detail.getDetailAddress();
        ResultEntity<BaiDuMapEntity> location = addressService.getBaiDuMapLocation(city.getCnName(), address);
        if (!location.isSuccess()) {
            this.index(message.getHouseId(), message.getRetry() + 1);
            return;
        }
        indexTemplate.setLocation(location.getResult());

        List<HouseTagEntity> tags = tagRepository.findAllByHouseId(houseId);
        if (tags != null && !tags.isEmpty()) {
            List<String> tagStrings = new ArrayList<>();
            tags.forEach(houseTag -> tagStrings.add(houseTag.getName()));
            indexTemplate.setTags(tagStrings);
        }

        SearchRequest searchRequest = new SearchRequest(HouseIndexConstants.INDEX_NAME);
        SearchSourceBuilder sourceBuilder = new SearchSourceBuilder();
        sourceBuilder.query(QueryBuilders.termQuery(HouseIndexConstants.HOUSE_ID, houseId));
        System.out.println(searchRequest.toString());
        searchRequest.source(sourceBuilder);
        try {
            SearchResponse searchResponse = this.restHighLevelClient.search(searchRequest, RequestOptions.DEFAULT);
            boolean success;
            long totalHit = searchResponse.getHits().getTotalHits().value;
            if (totalHit == 0) {
                success = create(indexTemplate);
            } else if (totalHit == 1) {
                String esId = searchResponse.getHits().getAt(0).getId();
                success = update(esId, indexTemplate);
            } else {
                success = deleteAndCreate(totalHit, indexTemplate);
            }

            ResultEntity serviceResult = addressService.lbsUpload(location.getResult(), house.getStreet() + house.getDistrict(),
                    city.getCnName() + region.getCnName() + house.getStreet() + house.getDistrict(),
                    message.getHouseId(), house.getPrice(), house.getArea());

            if (!success || !serviceResult.isSuccess()) {
                this.index(message.getHouseId(), message.getRetry() + 1);
            } else {
                LOGGER.info("更新索引中的[{}]成功", houseId);
            }
        } catch (IOException e) {
            LOGGER.info("更新索引中的[{}]异常", houseId);
        }
    }

    private void buildSetting(CreateIndexRequest request) {
        request.settings(Settings.builder().put("index.number_of_shards", 3)
                .put("index.number_of_replicas", 2));
    }

    private boolean create(HouseIndexTemplate indexTemplate) {
        if (!updateSuggest(indexTemplate)) {
            return false;
        }
        String indexName = HouseIndexConstants.INDEX_NAME;
        IndexRequest request = new IndexRequest(indexName);
        try {
//            Object json = JSON.toJSON(indexTemplate);
//            Map<String, Object> dataMap = ConvertUtils.convertBean2Map(indexTemplate);
            request.id(String.valueOf(indexTemplate.getHouseId()))
//                    .source(json, XContentType.JSON);
//                    .source(dataMap);
                    .source(objectMapper.writeValueAsBytes(indexTemplate), XContentType.JSON);

            IndexResponse response = this.restHighLevelClient.index(request, RequestOptions.DEFAULT);
            if (response.status() == RestStatus.CREATED) {
                LOGGER.info("索引中添加[{}]成功", indexTemplate.getHouseId());
                return true;
            } else {
                LOGGER.info("索引中添加[{}]失败", indexTemplate.getHouseId());
            }
        } catch (JsonProcessingException e) {
            LOGGER.info("索引中添加[{}]时，Json处理异常", indexTemplate.getHouseId());
        } catch (IOException e) {
            LOGGER.info("索引中添加[{}]异常", indexTemplate.getHouseId());
        }
        return false;
    }

    private boolean update(String esId, HouseIndexTemplate indexTemplate) {
        if (!updateSuggest(indexTemplate)) {
            return false;
        }
        try {
            UpdateRequest updateRequest = new UpdateRequest(HouseIndexConstants.INDEX_NAME, esId);
//            updateRequest.upsert(objectMapper.writeValueAsBytes(indexTemplate), XContentType.JSON);
            updateRequest.doc(objectMapper.writeValueAsBytes(indexTemplate), XContentType.JSON);

            UpdateResponse response = this.restHighLevelClient.update(updateRequest, RequestOptions.DEFAULT);
            if (response.status() == RestStatus.OK) {
                LOGGER.info("索引中更新[{}]成功", indexTemplate.getHouseId());
                return true;
            }
        } catch (JsonProcessingException e) {
            LOGGER.info("索引中更新[{}]时，Json处理异常", indexTemplate.getHouseId());
        } catch (IOException e) {
            LOGGER.info("索引中更新[{}]异常", indexTemplate.getHouseId());
        }
        return false;
    }

    private boolean deleteAndCreate(long totalHit, HouseIndexTemplate indexTemplate) {
        DeleteRequest deleteRequest = new DeleteRequest(HouseIndexConstants.INDEX_NAME);
        deleteRequest.id(String.valueOf(indexTemplate.getHouseId()));
        try {
            DeleteResponse deleteResponse = this.restHighLevelClient.delete(deleteRequest, RequestOptions.DEFAULT);
            ReplicationResponse.ShardInfo shardInfo = deleteResponse.getShardInfo();
            DocWriteResponse.Result result = deleteResponse.getResult();
            if (result == DocWriteResponse.Result.DELETED) {
                LOGGER.info("索引中删除[{}]成功", indexTemplate.getHouseId());
                return true;
            } else if (shardInfo.getFailed() > 0) {
                for (ReplicationResponse.ShardInfo.Failure failure : shardInfo.getFailures()) {
                    System.err.println("del error: " + indexTemplate.getHouseId());
                    LOGGER.info("索引中删除[{}]失败, INFO: {}", indexTemplate.getHouseId(), failure.reason());
                }
            } else {
                LOGGER.info("索引中删除[{}]失败", indexTemplate.getHouseId());
            }
        } catch (IOException e) {
            LOGGER.info("索引中删除[{}]异常", indexTemplate.getHouseId());

        }
        return create(indexTemplate);
    }

    private void index(Long houseId, int retry) {
        if (retry > HouseIndexMessage.MAX_RETRY) {
            LOGGER.info("重试次数超过{}, 请检查House:[{}]", HouseIndexMessage.MAX_RETRY, houseId);
            return;
        }
        HouseIndexMessage message = new HouseIndexMessage(houseId, HouseIndexMessage.INDEX, retry);
        try {
            kafkaTemplate.send(HouseIndexConstants.INDEX_TOPIC, objectMapper.writeValueAsString(message));
        } catch (JsonProcessingException e) {
            LOGGER.info("Json encode error for {} ", message);
        }
    }

    @Override
    public void index(Long houseId) {
        this.index(houseId, 0);
    }

    private void remove(Long houseId, int retry) {
        if (retry > HouseIndexMessage.MAX_RETRY) {
            LOGGER.info("重试次数超过{}, 请检查House:[{}]", HouseIndexMessage.MAX_RETRY, houseId);
            return;
        }
        HouseIndexMessage message = new HouseIndexMessage(houseId, HouseIndexMessage.REMOVE, retry);
        try {
            this.kafkaTemplate.send(HouseIndexConstants.INDEX_TOPIC, objectMapper.writeValueAsString(message));
        } catch (JsonProcessingException e) {
            LOGGER.info("Json encode error for {} ", message);
        }
    }

    @Override
    public void remove(Long houseId) {
        this.remove(houseId, 0);
    }

    @Override
    public MultiResultEntity<Long> query(RentSearchParam rentSearch) {
        // city_en_name
        BoolQueryBuilder boolQuery = QueryBuilders.boolQuery();
        boolQuery.filter(QueryBuilders.termQuery(HouseIndexConstants.CITY_EN_NAME, rentSearch.getCityEnName()));
        if (rentSearch.getRegionEnName() != null && !"*".equals(rentSearch.getRegionEnName())) {
            boolQuery.filter(QueryBuilders.termQuery(HouseIndexConstants.REGION_EN_NAME, rentSearch.getRegionEnName()));
        }
        // area
        RentValueBlockEntity area = RentValueBlockEntity.matchArea(rentSearch.getAreaBlock());
        if (!RentValueBlockEntity.ALL.equals(area)) {
            RangeQueryBuilder rangeQueryBuilder = QueryBuilders.rangeQuery(HouseIndexConstants.AREA);
            if (area.getMax() > 0) {
                rangeQueryBuilder.lte(area.getMax());
            }
            if (area.getMin() > 0) {
                rangeQueryBuilder.gte(area.getMin());
            }
            boolQuery.filter(rangeQueryBuilder);
        }
        // price
        RentValueBlockEntity price = RentValueBlockEntity.matchPrice(rentSearch.getPriceBlock());
        if (!RentValueBlockEntity.ALL.equals(price)) {
            RangeQueryBuilder rangeQuery = QueryBuilders.rangeQuery(HouseIndexConstants.PRICE);
            if (price.getMax() > 0) {
                rangeQuery.lte(price.getMax());
            }
            if (price.getMin() > 0) {
                rangeQuery.gte(price.getMin());
            }
            boolQuery.filter(rangeQuery);
        }
        // direction
        if (rentSearch.getDirection() > 0) {
            boolQuery.filter(
                    QueryBuilders.termQuery(HouseIndexConstants.DIRECTION, rentSearch.getDirection())
            );
        }
        // rent_way
        if (rentSearch.getRentWay() > -1) {
            boolQuery.filter(
                    QueryBuilders.termQuery(HouseIndexConstants.RENT_WAY, rentSearch.getRentWay())
            );
        }
        // 标题加权
        // should/must
        boolQuery.must(
                QueryBuilders.matchQuery(HouseIndexConstants.TITLE, rentSearch.getKeywords())
                        .boost(2.0f)
        );
        boolQuery.must(
                // 所有关键词匹配
                QueryBuilders.multiMatchQuery(rentSearch.getKeywords(),
//                        HouseIndexConstants.TITLE, // 标题
                        HouseIndexConstants.TRAFFIC,  // 交通情况
                        HouseIndexConstants.DISTRICT, // 小区
                        HouseIndexConstants.ROUND_SERVICE, //
                        HouseIndexConstants.SUBWAY_LINE_NAME, // 地铁线
                        HouseIndexConstants.SUBWAY_STATION_NAME // 地铁站
                ));

        // search
        SearchRequest request = new SearchRequest(HouseIndexConstants.INDEX_NAME);
        SearchSourceBuilder builder = new SearchSourceBuilder();
        builder.query(boolQuery)
                .sort(
                        HouseSort.getSortKey(rentSearch.getOrderBy()),
                        SortOrder.fromString(rentSearch.getOrderDirection())
                )
                .from(rentSearch.getStart())
                .size(rentSearch.getSize())
                .fetchSource(HouseIndexConstants.HOUSE_ID, null); // hit.getSourceAsMap()时，只返回houseId这个字段
        request.source(builder)
                .indicesOptions(IndicesOptions.lenientExpandOpen())
//                .preference("_primary")
                .requestCache(true);
        System.out.println(request.toString());

        try {
            List<Long> houseIds = Lists.newArrayList();
            SearchResponse response = this.restHighLevelClient.search(request, RequestOptions.DEFAULT);
            if (response.status() == RestStatus.OK) {
                SearchHits hits = response.getHits();
                TotalHits totalHits = hits.getTotalHits();
                LOGGER.info("命中：{}", totalHits.value);
                SearchHit[] searchHits = hits.getHits();
                for (SearchHit hit : searchHits) {
//                    System.out.println(hit.getSourceAsMap());
                    String houseId = String.valueOf(hit.getSourceAsMap().get(HouseIndexConstants.HOUSE_ID));
                    houseIds.add(Long.parseLong(houseId));
                }
                return new MultiResultEntity<>(totalHits.value, houseIds);
            } else {
                LOGGER.error("Search status is not ok for: {}", request);
                return new MultiResultEntity<>(0, houseIds);
            }
        } catch (IOException e) {
            LOGGER.error("Query Error...");
        }

        return new MultiResultEntity<>(0, Lists.newArrayList());
    }

    @Override
    public ResultEntity<List<String>> suggest(String prefix) {
        SearchRequest searchRequest = new SearchRequest(HouseIndexConstants.INDEX_NAME);
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
        SuggestionBuilder suggestion = SuggestBuilders.completionSuggestion("suggest").prefix(prefix).size(HouseIndexConstants.SUGGESTION_COUNT);

        SuggestBuilder suggestBuilder = new SuggestBuilder();
        suggestBuilder.addSuggestion(HouseIndexConstants.SUGGESTION_NAME, suggestion);
        searchSourceBuilder.suggest(suggestBuilder);
        searchRequest.source(searchSourceBuilder);
        try {
            SearchResponse response = this.restHighLevelClient.search(searchRequest, RequestOptions.DEFAULT);
            if (response.status() == RestStatus.OK) {
                Suggest suggest = response.getSuggest();
                if (null == suggest) {
                    return ResultEntity.of(Lists.newArrayList());
                }
                Suggest.Suggestion result = suggest.getSuggestion(HouseIndexConstants.SUGGESTION_NAME);
                int maxSuggest = 0;
                Set<String> suggestSet = Sets.newHashSet();
                for (Object term : result.getEntries()) {
                    if (term instanceof CompletionSuggestion.Entry) {
                        CompletionSuggestion.Entry item = (CompletionSuggestion.Entry) term;
                        if (item.getOptions().isEmpty()) {
                            continue;
                        }
                        for (CompletionSuggestion.Entry.Option option : item.getOptions()) {
                            String tip = option.getText().string();
                            if (suggestSet.contains(tip)) {
                                continue;
                            }
                            suggestSet.add(tip);
                            maxSuggest++;
                        }
                    }
                    if (maxSuggest > HouseIndexConstants.SUGGESTION_COUNT) {
                        break;
                    }
                }
                List<String> suggests = Lists.newArrayList(suggestSet.toArray(new String[]{}));
                return ResultEntity.of(suggests);
            } else {
                return ResultEntity.of(Lists.newArrayList());
            }
        } catch (IOException e) {
            LOGGER.error("异常");
        }

        return ResultEntity.of(Lists.newArrayList());
    }

    private boolean updateSuggest(HouseIndexTemplate indexTemplate) {
        // 需要被分词的字段信息
        String[] texts = {indexTemplate.getTitle(), indexTemplate.getLayoutDesc(), indexTemplate.getRoundService(),
                indexTemplate.getDescription(), indexTemplate.getSubwayLineName(), indexTemplate.getSubwayStationName()};
        AnalyzeRequest analyzeRequest = AnalyzeRequest.withIndexAnalyzer(
                HouseIndexConstants.INDEX_NAME,
                "ik_smart",
                texts
        );
        try {
            AnalyzeResponse response = this.restHighLevelClient.indices().analyze(analyzeRequest, RequestOptions.DEFAULT);
            List<AnalyzeResponse.AnalyzeToken> tokens = response.getTokens();
            if (null == tokens) {
                System.out.println("Can not analyze token for house: " + indexTemplate.getHouseId());
                return false;
            }
            List<HouseSuggest> suggests = Lists.newArrayList();
            for (AnalyzeResponse.AnalyzeToken token : tokens) {
                // 排序数字类型 & 小于2个字符的分词结果
                if ("<NUM>".equals(token.getType()) || token.getTerm().length() < 2) {
                    continue;
                }
                HouseSuggest suggest = new HouseSuggest();
                suggest.setInput(token.getTerm());
                suggests.add(suggest);
            }

            // 定制化小区自动补全
            HouseSuggest suggest = new HouseSuggest();
            suggest.setInput(indexTemplate.getDistrict());
            suggests.add(suggest);

            indexTemplate.setSuggest(suggests);
            return true;
        } catch (IOException e) {
            LOGGER.info("异常");
        }
        return false;
    }

    @Override
    public ResultEntity<Long> aggregateDistrictHouse(String cityEnName, String regionEnName, String district) {
        BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery()
                .filter(QueryBuilders.termQuery(HouseIndexConstants.CITY_EN_NAME, cityEnName))
                .filter(QueryBuilders.termQuery(HouseIndexConstants.REGION_EN_NAME, regionEnName))
                .filter(QueryBuilders.termQuery(HouseIndexConstants.DISTRICT, district));
        SearchRequest searchRequest = new SearchRequest(HouseIndexConstants.INDEX_NAME);
        SearchSourceBuilder builder = new SearchSourceBuilder();
        builder.query(boolQueryBuilder)
                .aggregation(
                        AggregationBuilders.terms(HouseIndexConstants.AGG_DISTRICT).field(HouseIndexConstants.DISTRICT))
                .size(0);
        System.out.println(searchRequest.toString());
        try {
            SearchResponse response = this.restHighLevelClient.search(searchRequest, RequestOptions.DEFAULT);
            if (response.status() == RestStatus.OK) {
                Terms terms = response.getAggregations().get(HouseIndexConstants.AGG_DISTRICT);
                if (null != terms) { // ES中无该数据时
                    if (null != terms.getBuckets() && !terms.getBuckets().isEmpty()) {
                        return ResultEntity.of(terms.getBucketByKey(district).getDocCount());
                    }
                }
            } else {
                LOGGER.error("Failed to Aggregate for: {} ", HouseIndexConstants.AGG_DISTRICT);
            }
        } catch (IOException e) {
            LOGGER.info("aggregate district house 异常");
        }

        return ResultEntity.of(0L);
    }

    @Override
    public MultiResultEntity<HouseBucketEntity> mapAggregate(String cityEnName) {
        BoolQueryBuilder boolQuery = QueryBuilders.boolQuery();
        boolQuery.filter(QueryBuilders.termQuery(HouseIndexConstants.CITY_EN_NAME, cityEnName));
        AggregationBuilder aggregationBuilder = AggregationBuilders.terms(HouseIndexConstants.AGG_REGION)
                .field(HouseIndexConstants.REGION_EN_NAME);

        SearchRequest searchRequest = new SearchRequest(HouseIndexConstants.INDEX_NAME);
        SearchSourceBuilder sourceBuilder = new SearchSourceBuilder();
        sourceBuilder.query(boolQuery).aggregation(aggregationBuilder);
        searchRequest.source(sourceBuilder);
        System.out.println(searchRequest.toString());

        try {
            SearchResponse response = this.restHighLevelClient.search(searchRequest, RequestOptions.DEFAULT);
            List<HouseBucketEntity> buckets = Lists.newArrayList();
            if (response.status() != RestStatus.OK) {
                LOGGER.error("Aggregate status is not ok for: {}", searchRequest.toString());
                return new MultiResultEntity<>(0, buckets);
            }
            Terms terms = response.getAggregations().get(HouseIndexConstants.AGG_REGION);
            for (Terms.Bucket bucket : terms.getBuckets()) {
                buckets.add(new HouseBucketEntity(bucket.getKeyAsString(), bucket.getDocCount()));
            }
            return new MultiResultEntity<>(response.getHits().getTotalHits().value, buckets);
        } catch (IOException e) {
            LOGGER.error("map aggregate error.");
        }

        return new MultiResultEntity<>(0, Lists.newArrayList());
    }

    @Override
    public MultiResultEntity<Long> mapQuery(String cityEnName, String orderBy, String orderDirection, int start, int size) {
        BoolQueryBuilder boolQuery = QueryBuilders.boolQuery();
        boolQuery.filter(QueryBuilders.termQuery(HouseIndexConstants.CITY_EN_NAME, cityEnName));

        SearchRequest searchRequest = new SearchRequest(HouseIndexConstants.INDEX_NAME);
        SearchSourceBuilder sourceBuilder = new SearchSourceBuilder();
        sourceBuilder.query(boolQuery)
                .sort(HouseSort.getSortKey(orderBy), SortOrder.fromString(orderDirection))
                .from(start)
                .size(size);
        searchRequest.source(sourceBuilder);

        try {
            SearchResponse response = this.restHighLevelClient.search(searchRequest, RequestOptions.DEFAULT);
            List<Long> houseIds = Lists.newArrayList();

            if (response.status() != RestStatus.OK) {
                LOGGER.error("Search status is not ok for: {}", searchRequest.toString());
                return new MultiResultEntity<>(0, houseIds);
            }
            for (SearchHit hit : response.getHits()) {
                houseIds.add(Long.parseLong(String.valueOf(hit.getSourceAsMap().get(HouseIndexConstants.HOUSE_ID))));
            }
            return new MultiResultEntity<>(response.getHits().getTotalHits().value, houseIds);
        } catch (IOException e) {
            LOGGER.error("异常");
        }
        return new MultiResultEntity<>(0, Lists.newArrayList());
    }

    @Override
    public MultiResultEntity<Long> mapQuery(MapSearchEntity mapSearch) {
        BoolQueryBuilder boolQuery = QueryBuilders.boolQuery();
        boolQuery.filter(QueryBuilders.termQuery(HouseIndexConstants.CITY_EN_NAME, mapSearch.getCityEnName()));

        boolQuery.filter(
                // Geo查询
                QueryBuilders.geoBoundingBoxQuery("location")
                        .setCorners( // 设置边角
                                new GeoPoint(mapSearch.getLeftLatitude(), mapSearch.getLeftLongitude()),
                                new GeoPoint(mapSearch.getRightLatitude(), mapSearch.getRightLongitude())
                        ));

        SearchRequest searchRequest = new SearchRequest(HouseIndexConstants.INDEX_NAME);
        SearchSourceBuilder sourceBuilder = new SearchSourceBuilder();
        sourceBuilder.query(boolQuery)
                .sort(HouseSort.getSortKey(mapSearch.getOrderBy()),
                        SortOrder.fromString(mapSearch.getOrderDirection()))
                .from(mapSearch.getStart())
                .size(mapSearch.getSize());
        searchRequest.source(sourceBuilder);
        try {
            SearchResponse response = this.restHighLevelClient.search(searchRequest, RequestOptions.DEFAULT);
            List<Long> houseIds = Lists.newArrayList();

            if (response.status() != RestStatus.OK) {
                LOGGER.error("Search status is not ok for: {}", searchRequest.toString());
                return new MultiResultEntity<>(0, houseIds);
            }
            for (SearchHit hit : response.getHits()) {
                houseIds.add(Long.parseLong(String.valueOf(hit.getSourceAsMap().get(HouseIndexConstants.HOUSE_ID))));
            }
            return new MultiResultEntity<>(response.getHits().getTotalHits().value, houseIds);
        } catch (IOException e) {
            LOGGER.error("异常");
        }
        return new MultiResultEntity<>(0, Lists.newArrayList());
    }

}
