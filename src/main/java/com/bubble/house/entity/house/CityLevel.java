package com.bubble.house.entity.house;

/**
 * 城市行政级别：行政级别 市-city 地区-region
 *
 * @author wugang
 * date: 2019-11-05 16:38
 **/
public enum CityLevel {
    /**
     * 市
     */
    CITY("city"),
    /**
     * 地区
     */
    REGION("region");

    private final String value;

    CityLevel(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    public static CityLevel of(String value) {
        for (CityLevel level : CityLevel.values()) {
            if (level.getValue().equals(value)) {
                return level;
            }
        }
        throw new IllegalArgumentException();
    }

}
