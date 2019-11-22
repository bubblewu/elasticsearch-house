package com.bubble.house.base.search;

/**
 * House的index常量信息定义
 *
 * @author wugang
 * date: 2019-11-11 18:24
 **/
public class HouseIndexConstants {

    public static final String INDEX_NAME = "es-house";
    public static final String INDEX_TYPE = "house";
    // suggest name
    public static final String SUGGESTION_NAME = "autocomplete";
    // suggest count
    public static final int SUGGESTION_COUNT = 6;

    // kafka topic
    public static final String INDEX_TOPIC = "house_build";


    public static final String HOUSE_ID = "houseId";

    public static final String TITLE = "title";

    public static final String PRICE = "price";
    public static final String AREA = "area";
    public static final String CREATE_TIME = "createTime";
    public static final String LAST_UPDATE_TIME = "lastUpdateTime";
    public static final String CITY_EN_NAME = "cityEnName";
    public static final String REGION_EN_NAME = "regionEnName";
    public static final String DIRECTION = "direction";
    public static final String DISTANCE_TO_SUBWAY = "distanceToSubway";
    public static final String STREET = "street";
    public static final String DISTRICT = "district";
    public static final String DESCRIPTION = "description";
    public static final String LAYOUT_DESC = "layoutDesc";
    public static final String TRAFFIC = "traffic";
    public static final String ROUND_SERVICE = "roundService";
    public static final String RENT_WAY = "rentWay";
    public static final String SUBWAY_LINE_NAME = "subwayLineName";
    public static final String SUBWAY_STATION_NAME = "subwayStationName";
    public static final String TAGS = "tags";

    public static final String AGG_DISTRICT = "agg_district";
    public static final String AGG_REGION = "agg_region";

}
