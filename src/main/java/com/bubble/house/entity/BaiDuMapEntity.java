package com.bubble.house.entity;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.io.Serializable;

/**
 * 百度地图位置信息
 *
 * @author wugang
 * date: 2019-11-06 17:18
 **/
public class BaiDuMapEntity implements Serializable {
    private static final long serialVersionUID = -5758696463077606067L;

    // 经度
    @JsonProperty("lon")
    private double longitude;
    // 纬度
    @JsonProperty("lat")
    private double latitude;

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

}
