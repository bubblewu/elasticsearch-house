package com.bubble.house.web.dto.house;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.io.Serializable;

/**
 * 城市DTO，数据传输类型：只为web层提供数据视图，与后端数据分割，防止造成混淆。
 *
 * @author wugang
 * date: 2020-10-29 14:57
 **/
public class CityDTO implements Serializable {
    private static final long serialVersionUID = 8952652425718084327L;

    private Long id;
    /**
     * 需要进行Json的字段转换
     */
    @JsonProperty(value = "belong_to")
    private String belongTo;
    @JsonProperty(value = "en_name")
    private String enName;
    @JsonProperty(value = "cn_name")
    private String cnName;
    private String level;
    private Double baiduMapLongitude;
    private Double baiduMapLatitude;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getBelongTo() {
        return belongTo;
    }

    public void setBelongTo(String belongTo) {
        this.belongTo = belongTo;
    }

    public String getEnName() {
        return enName;
    }

    public void setEnName(String enName) {
        this.enName = enName;
    }

    public String getCnName() {
        return cnName;
    }

    public void setCnName(String cnName) {
        this.cnName = cnName;
    }

    public String getLevel() {
        return level;
    }

    public void setLevel(String level) {
        this.level = level;
    }

    public Double getBaiduMapLongitude() {
        return baiduMapLongitude;
    }

    public void setBaiduMapLongitude(Double baiduMapLongitude) {
        this.baiduMapLongitude = baiduMapLongitude;
    }

    public Double getBaiduMapLatitude() {
        return baiduMapLatitude;
    }

    public void setBaiduMapLatitude(Double baiduMapLatitude) {
        this.baiduMapLatitude = baiduMapLatitude;
    }
}
