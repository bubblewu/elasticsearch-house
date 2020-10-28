package com.bubble.house.service.house;

import com.bubble.house.entity.BaiDuMapEntity;
import com.bubble.house.entity.house.CityEntity;
import com.bubble.house.entity.house.CityLevel;
import com.bubble.house.entity.house.SubwayEntity;
import com.bubble.house.entity.house.SubwayStationEntity;
import com.bubble.house.service.ServiceMultiResultEntity;
import com.bubble.house.service.ServiceResultEntity;
import com.bubble.house.repository.CityRepository;
import com.bubble.house.repository.SubwayRepository;
import com.bubble.house.repository.SubwayStationRepository;
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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * 地点相关服务接口实现
 *
 * @author wugang
 * date: 2019-11-05 16:32
 **/
@Service
public class AddressServiceImpl implements AddressService {
    private final static Logger LOGGER = LoggerFactory.getLogger(AddressServiceImpl.class);


    private final CityRepository cityRepository;
    private final SubwayRepository subwayRepository;
    private final SubwayStationRepository subwayStationRepository;

    public AddressServiceImpl(CityRepository cityRepository, SubwayRepository subwayRepository, SubwayStationRepository subwayStationRepository) {
        this.cityRepository = cityRepository;
        this.subwayRepository = subwayRepository;
        this.subwayStationRepository = subwayStationRepository;
    }

    @Autowired
    private ObjectMapper objectMapper;

    @Override
    public ServiceMultiResultEntity<CityEntity> findAllCities() {
        List<CityEntity> cityEntityList = this.cityRepository.findAllByLevel(CityLevel.CITY.getValue());
        LOGGER.debug("加载城市信息：{}", cityEntityList.size());
        return new ServiceMultiResultEntity<>(cityEntityList.size(), cityEntityList);
    }

    @Override
    public Map<CityLevel, CityEntity> findCityAndRegion(String cityEnName, String regionEnName) {
        LOGGER.debug("开始加载[{}-{}]信息", cityEnName, regionEnName);
        Map<CityLevel, CityEntity> result = new HashMap<>();
        CityEntity city = this.cityRepository.findByEnNameAndLevel(cityEnName, CityLevel.CITY.getValue());
        CityEntity region = this.cityRepository.findByEnNameAndBelongTo(regionEnName, city.getEnName());
        result.put(CityLevel.CITY, city);
        result.put(CityLevel.REGION, region);
        return result;
    }

    @Override
    public ServiceMultiResultEntity<CityEntity> findAllRegionsByCityEnName(String cityEnName) {
        LOGGER.debug("开始加载城市[{}]下的县区信息", cityEnName);
        if (null == cityEnName) {
            return new ServiceMultiResultEntity<>(0, null);
        }
        List<CityEntity> regions = this.cityRepository.findAllByLevelAndBelongTo(CityLevel.REGION.getValue(), cityEnName);
        return new ServiceMultiResultEntity<>(regions.size(), regions);
    }

    @Override
    public List<SubwayEntity> findAllSubwayByCityEnName(String cityEnName) {
        LOGGER.debug("开始加载城市[{}]下的地铁信息", cityEnName);
        if (null == cityEnName) {
            return Lists.newArrayList();
        }
        List<SubwayEntity> regions = this.subwayRepository.findAllByCityEnName(cityEnName);
        return Optional.of(regions).orElse(Lists.newArrayList());
    }

    @Override
    public List<SubwayStationEntity> findAllStationBySubway(Long subwayId) {
        LOGGER.debug("开始加载地铁[{}]的地铁站点信息", subwayId);
        List<SubwayStationEntity> stations = this.subwayStationRepository.findAllBySubwayId(subwayId);
        return Optional.of(stations).orElse(Lists.newArrayList());
    }

    @Override
    public ServiceResultEntity<SubwayEntity> findSubway(Long subwayId) {
        LOGGER.debug("开始加载地铁[{}]的地铁线路信息", subwayId);
        if (subwayId == null) {
            return ServiceResultEntity.notFound();
        }
        Optional<SubwayEntity> subwayOp = subwayRepository.findById(subwayId);
        return subwayOp.map(ServiceResultEntity::of).orElseGet(ServiceResultEntity::notFound);
    }

    @Override
    public ServiceResultEntity<SubwayStationEntity> findSubwayStation(Long stationId) {
        LOGGER.debug("开始加载地铁站[{}]的信息", stationId);
        if (null == stationId) {
            return ServiceResultEntity.notFound();
        }
        Optional<SubwayStationEntity> subwayStationOp = subwayStationRepository.findById(stationId);
        return subwayStationOp.map(ServiceResultEntity::of).orElseGet(ServiceResultEntity::notFound);
    }

    @Override
    public ServiceResultEntity<CityEntity> findCity(String cityEnName) {
        LOGGER.debug("开始加载城市[{}]的信息", cityEnName);
        if (null == cityEnName) {
            return ServiceResultEntity.notFound();
        }
        CityEntity city = cityRepository.findByEnNameAndLevel(cityEnName, CityLevel.CITY.getValue());
        return city == null ? ServiceResultEntity.notFound() : ServiceResultEntity.of(city);
    }

    private static final String BAIDU_MAP_GEOCONV_API = "http://api.map.baidu.com/geocoder/v2/?";
    private static final String BAIDU_MAP_KEY = "6QtSF673D1pYl3eQkEXfwp8ZgsQpB77U";

    @Override
    public ServiceResultEntity<BaiDuMapEntity> getBaiDuMapLocation(String city, String address) {
        LOGGER.debug("开始获取[{}-{}]的地理编码信息", city, address);
        String encodeAddress;
        String encodeCity;

        try {
            encodeAddress = URLEncoder.encode(address, "UTF-8");
            encodeCity = URLEncoder.encode(city, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            LOGGER.error("[{} - {}] Encode 失败", city, address);
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
                LOGGER.error("[{} - {}] 获取百度坐标失败", city, address);
                return new ServiceResultEntity<>(false, "获取百度坐标信息失败");
            }
            String result = EntityUtils.toString(response.getEntity(), "UTF-8");
            JsonNode jsonNode = objectMapper.readTree(result);
            int status = jsonNode.get("status").asInt();
            if (status != 0) {
                LOGGER.error("[{} - {}] 获取百度坐标失败", city, address);
                return new ServiceResultEntity<>(false, "获取百度坐标信息失败, Status: " + status);
            } else {
                BaiDuMapEntity location = new BaiDuMapEntity();
                JsonNode jsonLocation = jsonNode.get("result").get("location");
                location.setLongitude(jsonLocation.get("lng").asDouble());
                location.setLatitude(jsonLocation.get("lat").asDouble());
                return ServiceResultEntity.of(location);
            }
        } catch (IOException e) {
            LOGGER.error("[{} - {}] 获取百度坐标异常", city, address);
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
        LOGGER.debug("开始上传LBS数据到百度存储, House:{}", houseId);
        HttpClient httpClient = HttpClients.createDefault();
        List<NameValuePair> nvps = Lists.newArrayList();
        nvps.add(new BasicNameValuePair("latitude", String.valueOf(location.getLatitude())));
        nvps.add(new BasicNameValuePair("longitude", String.valueOf(location.getLongitude())));
        nvps.add(new BasicNameValuePair("coord_type", "3")); // 3：百度坐标系
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
                LOGGER.error("上传LBS数据到百度云存储失败, Response: {}", result);
                return new ServiceResultEntity(false, "上传LBS数据到百度云存储失败");
            } else {
                JsonNode jsonNode = objectMapper.readTree(result);
                int status = jsonNode.get("status").asInt();
                if (status != 0) {
                    String message = jsonNode.get("message").asText();
                    LOGGER.error("上传LBS数据到百度云存储失败, Status: {}. Message: {}", status, message);
                    return new ServiceResultEntity(false, "上传LBS数据到百度云存储失败");
                } else {
                    return ServiceResultEntity.success();
                }
            }

        } catch (IOException e) {
            LOGGER.error("上传LBS数据到百度云存储异常");
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
                LOGGER.error("获取LBS数据失败, Response: {}", result);
                return false;
            }

            JsonNode jsonNode = objectMapper.readTree(result);
            int status = jsonNode.get("status").asInt();
            if (status != 0) {
                LOGGER.error("获取LBS数据失败, Status: {}", status);
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
            LOGGER.error("获取LBS数据异常, House: {}", houseId);
            return false;
        }
    }

    @Override
    public ServiceResultEntity removeLbs(Long houseId) {
        LOGGER.debug("开始移除LBS数据, House: {}", houseId);
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
                LOGGER.error("移除LBS数据失败, Response: {}", response);
                return new ServiceResultEntity(false, "移除LBS数据失败");
            }

            JsonNode jsonNode = objectMapper.readTree(result);
            int status = jsonNode.get("status").asInt();
            if (status != 0) {
                String message = jsonNode.get("message").asText();
                LOGGER.error("移除LBS数据失败, Message: {}", message);
                return new ServiceResultEntity(false, "移除LBS数据失败, Message: {}" + message);
            }
            return ServiceResultEntity.success();
        } catch (IOException e) {
            LOGGER.error("移除LBS数据异常");
            return new ServiceResultEntity(false, "移除LBS数据异常");
        }
    }

}
