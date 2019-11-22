package com.bubble.house.service.house;

import com.bubble.house.entity.BaiDuMapEntity;
import com.bubble.house.entity.house.CityEntity;
import com.bubble.house.entity.house.CityLevel;
import com.bubble.house.entity.house.SubwayEntity;
import com.bubble.house.entity.house.SubwayStationEntity;
import com.bubble.house.entity.result.MultiResultEntity;
import com.bubble.house.entity.result.ResultEntity;
import com.bubble.house.repository.CityRepository;
import com.bubble.house.repository.SubwayRepository;
import com.bubble.house.repository.SubwayStationRepository;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.*;

/**
 * 地点相关服务接口实现
 *
 * @author wugang
 * date: 2019-11-05 16:32
 **/
@Service
public class AddressServiceImpl implements AddressService {

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
    public MultiResultEntity<CityEntity> findAllCities() {
        List<CityEntity> cityEntityList = this.cityRepository.findAllByLevel(CityLevel.CITY.getValue());
        return new MultiResultEntity<>(cityEntityList.size(), cityEntityList);
    }

    @Override
    public Map<CityLevel, CityEntity> findCityAndRegion(String cityEnName, String regionEnName) {
        Map<CityLevel, CityEntity> result = new HashMap<>();

        CityEntity city = this.cityRepository.findByEnNameAndLevel(cityEnName, CityLevel.CITY.getValue());
        CityEntity region = this.cityRepository.findByEnNameAndBelongTo(regionEnName, city.getEnName());

        result.put(CityLevel.CITY, city);
        result.put(CityLevel.REGION, region);
        return result;
    }

    @Override
    public MultiResultEntity<CityEntity> findAllRegionsByCityEnName(String cityEnName) {
        if (null == cityEnName) {
            return new MultiResultEntity<>(0, null);
        }
        List<CityEntity> regions = this.cityRepository.findAllByLevelAndBelongTo(CityLevel.REGION.getValue(), cityEnName);
        return new MultiResultEntity<>(regions.size(), regions);
    }

    @Override
    public List<SubwayEntity> findAllSubwayByCityEnName(String cityEnName) {
        if (null == cityEnName) {
            return new ArrayList<>();
        }
        List<SubwayEntity> regions = this.subwayRepository.findAllByCityEnName(cityEnName);
        return Optional.of(regions).orElse(new ArrayList<>());
    }

    @Override
    public List<SubwayStationEntity> findAllStationBySubway(Long subwayId) {
        List<SubwayStationEntity> stations = this.subwayStationRepository.findAllBySubwayId(subwayId);
        return Optional.of(stations).orElse(new ArrayList<>());
    }

    @Override
    public ResultEntity<SubwayEntity> findSubway(Long subwayId) {
        if (subwayId == null) {
            return ResultEntity.notFound();
        }
        Optional<SubwayEntity> subwayOp = subwayRepository.findById(subwayId);
        return subwayOp.map(ResultEntity::of).orElseGet(ResultEntity::notFound);
    }

    @Override
    public ResultEntity<SubwayStationEntity> findSubwayStation(Long stationId) {
        if (null == stationId) {
            return ResultEntity.notFound();
        }
        Optional<SubwayStationEntity> subwayStationOp = subwayStationRepository.findById(stationId);
        return subwayStationOp.map(ResultEntity::of).orElseGet(ResultEntity::notFound);
    }

    @Override
    public ResultEntity<CityEntity> findCity(String cityEnName) {
        if (null == cityEnName) {
            return ResultEntity.notFound();
        }
        CityEntity city = cityRepository.findByEnNameAndLevel(cityEnName, CityLevel.CITY.getValue());
        return city == null ? ResultEntity.notFound() : ResultEntity.of(city);
    }

    private static final String BAIDU_MAP_GEOCONV_API = "http://api.map.baidu.com/geocoder/v2/?";
    private static final String BAIDU_MAP_KEY = "6QtSF673D1pYl3eQkEXfwp8ZgsQpB77U";

    @Override
    public ResultEntity<BaiDuMapEntity> getBaiDuMapLocation(String city, String address) {
        String encodeAddress;
        String encodeCity;

        try {
            encodeAddress = URLEncoder.encode(address, "UTF-8");
            encodeCity = URLEncoder.encode(city, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            System.err.println("Error to encode house address");
            return new ResultEntity<>(false, "Error to encode house address");
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
                return new ResultEntity<>(false, "Can not get BaiDu map location");
            }
            String result = EntityUtils.toString(response.getEntity(), "UTF-8");
            JsonNode jsonNode = objectMapper.readTree(result);
            int status = jsonNode.get("status").asInt();
            if (status != 0) {
                return new ResultEntity<>(false, "Error to get map location for status: " + status);
            } else {
                BaiDuMapEntity location = new BaiDuMapEntity();
                JsonNode jsonLocation = jsonNode.get("result").get("location");
                location.setLongitude(jsonLocation.get("lng").asDouble());
                location.setLatitude(jsonLocation.get("lat").asDouble());
                return ResultEntity.of(location);
            }
        } catch (IOException e) {
            System.err.println("Error to fetch baidumap api");
            return new ResultEntity<>(false, "Error to fetch baidumap api");
        }
    }

    /**
     * POI数据管理接口：http://lbsyun.baidu.com/index.php?title=lbscloud/api/geodata
     */
    private static final String LBS_CREATE_API = "http://api.map.baidu.com/geodata/v3/poi/create";
    private static final String LBS_QUERY_API = "http://api.map.baidu.com/geodata/v3/poi/list?";
    private static final String LBS_UPDATE_API = "http://api.map.baidu.com/geodata/v3/poi/update";
    private static final String LBS_DELETE_API = "http://api.map.baidu.com/geodata/v3/poi/delete";
    private final String GEO_TABLE_ID= "175730";

    @Override
    public ResultEntity lbsUpload(BaiDuMapEntity location, String title, String address, long houseId, int price, int area) {
        HttpClient httpClient = HttpClients.createDefault();
        List<NameValuePair> nvps = new ArrayList<>();
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
                System.err.println("Can not upload lbs data for response: " + result);
                return new ResultEntity(false, "Can not upload baidu lbs data");
            } else {
                JsonNode jsonNode = objectMapper.readTree(result);
                int status = jsonNode.get("status").asInt();
                if (status != 0) {
                    String message = jsonNode.get("message").asText();
                    System.err.println(String.format("Error to upload lbs data for status: %s, and message: %s", status, message));
                    return new ResultEntity(false, "Error to upload lbs data");
                } else {
                    return ResultEntity.success();
                }
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
        return new ResultEntity(false);
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
                System.err.println("Can not get lbs data for response: " + result);
                return false;
            }

            JsonNode jsonNode = objectMapper.readTree(result);
            int status = jsonNode.get("status").asInt();
            if (status != 0) {
                System.err.println("Error to get lbs data for status: " + status);
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
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public ResultEntity removeLbs(Long houseId) {
        HttpClient httpClient = HttpClients.createDefault();
        List<NameValuePair> nvps = new ArrayList<>();
        nvps.add(new BasicNameValuePair("geotable_id", GEO_TABLE_ID));
        nvps.add(new BasicNameValuePair("ak", BAIDU_MAP_KEY));
        nvps.add(new BasicNameValuePair("houseId", String.valueOf(houseId)));

        HttpPost delete = new HttpPost(LBS_DELETE_API);
        try {
            delete.setEntity(new UrlEncodedFormEntity(nvps, "UTF-8"));
            HttpResponse response = httpClient.execute(delete);
            String result = EntityUtils.toString(response.getEntity(), "UTF-8");
            if (response.getStatusLine().getStatusCode() != HttpStatus.SC_OK) {
                System.err.println("Error to delete lbs data for response: " + result);
                return new ResultEntity(false);
            }

            JsonNode jsonNode = objectMapper.readTree(result);
            int status = jsonNode.get("status").asInt();
            if (status != 0) {
                String message = jsonNode.get("message").asText();
                System.err.println("Error to delete lbs data for message: " + message);
                return new ResultEntity(false, "Error to delete lbs data for: " + message);
            }
            return ResultEntity.success();
        } catch (IOException e) {
            System.err.println("Error to delete lbs data.");
            return new ResultEntity(false);
        }

    }

}
