package com.bubble.house.service.house;

import com.bubble.house.base.util.HouseUtils;
import com.bubble.house.entity.BaiDuMapEntity;
import com.bubble.house.service.ServiceMultiResultEntity;
import com.bubble.house.service.ServiceResultEntity;
import com.bubble.house.service.search.SearchService;
import com.bubble.house.web.dto.house.HouseDTO;
import com.bubble.house.web.param.MapSearchParam;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.Lists;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.List;

/**
 * 地图查询服务实现
 *
 * @author wugang
 * date: 2020-10-29 17:26
 **/
@Service
public class MapServiceImpl implements MapService {
    private final Logger logger = LoggerFactory.getLogger(MapServiceImpl.class);

    private final SearchService searchService;
    private final HouseUtils houseUtils;
    private final ObjectMapper objectMapper;

    public MapServiceImpl(SearchService searchService, HouseUtils houseUtils, ObjectMapper objectMapper) {
        this.searchService = searchService;
        this.houseUtils = houseUtils;
        this.objectMapper = objectMapper;
    }

    @Override
    public ServiceMultiResultEntity<HouseDTO> wholeMapQuery(MapSearchParam mapSearch) {
        ServiceMultiResultEntity<Long> serviceResult = searchService.mapQuery(mapSearch.getCityEnName(), mapSearch.getOrderBy(), mapSearch.getOrderDirection(), mapSearch.getStart(), mapSearch.getSize());

        if (serviceResult.getTotal() == 0) {
            return new ServiceMultiResultEntity<>(0, Lists.newArrayList());
        }
        List<HouseDTO> houses = houseUtils.wrapperHouseResult(serviceResult.getResult());
        return new ServiceMultiResultEntity<>(serviceResult.getTotal(), houses);
    }

    @Override
    public ServiceMultiResultEntity<HouseDTO> boundMapQuery(MapSearchParam mapSearch) {
        ServiceMultiResultEntity<Long> serviceResult = searchService.mapQuery(mapSearch);
        if (serviceResult.getTotal() == 0) {
            return new ServiceMultiResultEntity<>(0, Lists.newArrayList());
        }

        List<HouseDTO> houses = houseUtils.wrapperHouseResult(serviceResult.getResult());
        return new ServiceMultiResultEntity<>(serviceResult.getTotal(), houses);
    }


    private static final String BAIDU_MAP_GEOCONV_API = "http://api.map.baidu.com/geocoder/v2/?";
    private static final String BAIDU_MAP_KEY = "6QtSF673D1pYl3eQkEXfwp8ZgsQpB77U";

    @Override
    public ServiceResultEntity<BaiDuMapEntity> getBaiDuMapLocation(String city, String address) {
        logger.debug("开始获取[{}-{}]的地理编码信息", city, address);
        String encodeAddress;
        String encodeCity;
        try {
            encodeAddress = URLEncoder.encode(address, "UTF-8");
            encodeCity = URLEncoder.encode(city, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            logger.error("[{} - {}] Encode 失败", city, address);
            return new ServiceResultEntity<>(false, "Encode house address失败");
        }
        // 百度地理编码服务
        HttpClient httpClient = HttpClients.createDefault();
        StringBuilder sb = new StringBuilder(BAIDU_MAP_GEOCONV_API);
        sb.append("address=").append(encodeAddress).append("&")
                .append("city=").append(encodeCity).append("&")
                .append("output=json&")
                .append("ak=").append(BAIDU_MAP_KEY);

        HttpGet get = new HttpGet(sb.toString());
        try {
            HttpResponse response = httpClient.execute(get);
            if (response.getStatusLine().getStatusCode() != HttpStatus.SC_OK) {
                logger.error("[{} - {}] 获取百度坐标失败", city, address);
                return new ServiceResultEntity<>(false, "获取百度坐标信息失败");
            }
            String result = EntityUtils.toString(response.getEntity(), "UTF-8");
            JsonNode jsonNode = objectMapper.readTree(result);
            int status = jsonNode.get("status").asInt();
            if (status != 0) {
                logger.error("[{} - {}] 获取百度坐标失败", city, address);
                return new ServiceResultEntity<>(false, "获取百度坐标信息失败, Status: " + status);
            } else {
                BaiDuMapEntity location = new BaiDuMapEntity();
                JsonNode jsonLocation = jsonNode.get("result").get("location");
                location.setLongitude(jsonLocation.get("lng").asDouble());
                location.setLatitude(jsonLocation.get("lat").asDouble());
                return ServiceResultEntity.of(location);
            }
        } catch (IOException e) {
            logger.error("[{} - {}] 获取百度坐标异常", city, address);
            return new ServiceResultEntity<>(false, "获取百度坐标信息出现异常");
        }
    }

    /**
     * POI数据管理接口：http://lbsyun.baidu.com/index.php?title=lbscloud/api/geodata
     */
    private static final String LBS_CREATE_API = "http://api.map.baidu.com/geodata/v3/poi/create";
    private static final String LBS_QUERY_API = "http://api.map.baidu.com/geodata/v3/poi/list?";
    private static final String LBS_UPDATE_API = "http://api.map.baidu.com/geodata/v3/poi/update";
    private static final String LBS_DELETE_API = "http://api.map.baidu.com/geodata/v3/poi/delete";
    private final String GEO_TABLE_ID = "175730";

    @Override
    public ServiceResultEntity lbsUpload(BaiDuMapEntity location, String title, String address, long houseId, int price, int area) {
        logger.debug("开始上传LBS数据到百度存储, House:{}", houseId);
        HttpClient httpClient = HttpClients.createDefault();
        List<NameValuePair> nvps = Lists.newArrayList();
        nvps.add(new BasicNameValuePair("latitude", String.valueOf(location.getLatitude())));
        nvps.add(new BasicNameValuePair("longitude", String.valueOf(location.getLongitude())));
        // 3：百度坐标系
        nvps.add(new BasicNameValuePair("coord_type", "3"));
        nvps.add(new BasicNameValuePair("geotable_id", GEO_TABLE_ID));
        nvps.add(new BasicNameValuePair("ak", BAIDU_MAP_KEY));
        nvps.add(new BasicNameValuePair("houseId", String.valueOf(houseId)));
        nvps.add(new BasicNameValuePair("price", String.valueOf(price)));
        nvps.add(new BasicNameValuePair("area", String.valueOf(area)));
        nvps.add(new BasicNameValuePair("title", title));
        nvps.add(new BasicNameValuePair("address", address));

        HttpPost post;
        if (isLbsDataExists(houseId)) {
            post = new HttpPost(LBS_UPDATE_API);
        } else {
            post = new HttpPost(LBS_CREATE_API);
        }

        try {
            post.setEntity(new UrlEncodedFormEntity(nvps, "UTF-8"));
            HttpResponse response = httpClient.execute(post);
            String result = EntityUtils.toString(response.getEntity(), "UTF-8");
            if (response.getStatusLine().getStatusCode() != HttpStatus.SC_OK) {
                logger.error("上传LBS数据到百度云存储失败, Response: {}", result);
                return new ServiceResultEntity(false, "上传LBS数据到百度云存储失败");
            } else {
                JsonNode jsonNode = objectMapper.readTree(result);
                int status = jsonNode.get("status").asInt();
                if (status != 0) {
                    String message = jsonNode.get("message").asText();
                    logger.error("上传LBS数据到百度云存储失败, Status: {}. Message: {}", status, message);
                    return new ServiceResultEntity(false, "上传LBS数据到百度云存储失败");
                } else {
                    return ServiceResultEntity.success();
                }
            }

        } catch (IOException e) {
            logger.error("上传LBS数据到百度云存储异常");
            return new ServiceResultEntity(false, "上传LBS数据到百度云存储异常");
        }
    }

    private boolean isLbsDataExists(Long houseId) {
        HttpClient httpClient = HttpClients.createDefault();
        StringBuilder sb = new StringBuilder(LBS_QUERY_API);
        sb.append("geotable_id=").append(GEO_TABLE_ID).append("&")
                .append("ak=").append(BAIDU_MAP_KEY).append("&")
                .append("houseId=").append(houseId).append(",").append(houseId);
        HttpGet get = new HttpGet(sb.toString());
        try {
            HttpResponse response = httpClient.execute(get);
            String result = EntityUtils.toString(response.getEntity(), "UTF-8");
            if (response.getStatusLine().getStatusCode() != HttpStatus.SC_OK) {
                logger.error("获取LBS数据失败, Response: {}", result);
                return false;
            }

            JsonNode jsonNode = objectMapper.readTree(result);
            int status = jsonNode.get("status").asInt();
            if (status != 0) {
                logger.error("获取LBS数据失败, Status: {}", status);
                return false;
            } else {
                long size = jsonNode.get("size").asLong();
                if (size > 0) {
                    return true;
                } else {
                    return false;
                }
            }
        } catch (IOException e) {
            logger.error("获取LBS数据异常, House: {}", houseId);
            return false;
        }
    }

    @Override
    public ServiceResultEntity removeLbs(Long houseId) {
        logger.debug("开始移除LBS数据, House: {}", houseId);
        HttpClient httpClient = HttpClients.createDefault();
        List<NameValuePair> nvps = Lists.newArrayListWithCapacity(3);
        nvps.add(new BasicNameValuePair("geotable_id", GEO_TABLE_ID));
        nvps.add(new BasicNameValuePair("ak", BAIDU_MAP_KEY));
        nvps.add(new BasicNameValuePair("houseId", String.valueOf(houseId)));

        HttpPost delete = new HttpPost(LBS_DELETE_API);
        try {
            delete.setEntity(new UrlEncodedFormEntity(nvps, "UTF-8"));
            HttpResponse response = httpClient.execute(delete);
            String result = EntityUtils.toString(response.getEntity(), "UTF-8");
            if (response.getStatusLine().getStatusCode() != HttpStatus.SC_OK) {
                logger.error("移除LBS数据失败, Response: {}", response);
                return new ServiceResultEntity(false, "移除LBS数据失败");
            }

            JsonNode jsonNode = objectMapper.readTree(result);
            int status = jsonNode.get("status").asInt();
            if (status != 0) {
                String message = jsonNode.get("message").asText();
                logger.error("移除LBS数据失败, Message: {}", message);
                return new ServiceResultEntity(false, "移除LBS数据失败, Message: {}" + message);
            }
            return ServiceResultEntity.success();
        } catch (IOException e) {
            logger.error("移除LBS数据异常");
            return new ServiceResultEntity(false, "移除LBS数据异常");
        }
    }

}
