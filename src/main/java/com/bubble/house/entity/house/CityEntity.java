package com.bubble.house.entity.house;

import com.fasterxml.jackson.annotation.JsonProperty;

import javax.persistence.*;
import java.io.Serializable;

/**
 * 地址实体
 *
 * @author wugang
 * date: 2019-11-05 16:24
 **/
@Entity
@Table(name = "support_address")
public class CityEntity implements Serializable {
    private static final long serialVersionUID = -2788967462875723469L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @JsonProperty(value = "belong_to")
    @Column(name = "belong_to")
    private String belongTo;  // 上一级行政单位名
    @JsonProperty(value = "en_name")
    @Column(name = "en_name")
    private String enName;  // 行政单位英文名缩写
    @JsonProperty(value = "cn_name")
    @Column(name = "cn_name")
    private String cnName;  // 行政单位中文名
    private String level; // 行政级别 市-city 地区-region
    @Column(name = "baidu_map_lng")
    private double baiduMapLongitude;  // 百度地图经度
    @Column(name = "baidu_map_lat")
    private double baiduMapLatitude;  // 百度地图纬度

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

    public double getBaiduMapLongitude() {
        return baiduMapLongitude;
    }

    public void setBaiduMapLongitude(double baiduMapLongitude) {
        this.baiduMapLongitude = baiduMapLongitude;
    }

    public double getBaiduMapLatitude() {
        return baiduMapLatitude;
    }

    public void setBaiduMapLatitude(double baiduMapLatitude) {
        this.baiduMapLatitude = baiduMapLatitude;
    }
}
