package com.bubble.house.base.search;

import com.bubble.house.entity.BaiDuMapEntity;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * House的Index结构
 *
 * @author wugang
 * date: 2019-11-11 18:07
 **/
public class HouseIndexTemplate implements Serializable {
    private static final long serialVersionUID = -6654321317040536683L;

    private Long houseId; // house唯一标识
    private String title; //
    private int price; // 价格
    private int area; // 面积
    private Date createTime; // 创建时间
    private Date lastUpdateTime; // 最近数据更新时间
    private String cityEnName; // 城市标记缩写 如 北京bj
    private String regionEnName; // 地区英文简写 如昌平区 cpq
    private int direction; // 房屋朝向
    private int distanceToSubway; // 距地铁距离 默认-1 附近无地铁
    private String subwayLineName; // 附近地铁线名称
    private String subwayStationName; // 地铁站名
    private String street; // 街道
    private String district; // 所在小区
    private String description; // 详细描述
    private String layoutDesc; // 户型介绍
    private String traffic; // 交通出行
    private String roundService; // 周边配套
    private int rentWay; // 租赁方式
    private List<String> tags; // 标签
    private List<HouseSuggest> suggest; //
    private BaiDuMapEntity location; //


    public BaiDuMapEntity getLocation() {
        return location;
    }

    public void setLocation(BaiDuMapEntity location) {
        this.location = location;
    }

    public Long getHouseId() {
        return houseId;
    }

    public void setHouseId(Long houseId) {
        this.houseId = houseId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public int getPrice() {
        return price;
    }

    public void setPrice(int price) {
        this.price = price;
    }

    public int getArea() {
        return area;
    }

    public void setArea(int area) {
        this.area = area;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public Date getLastUpdateTime() {
        return lastUpdateTime;
    }

    public void setLastUpdateTime(Date lastUpdateTime) {
        this.lastUpdateTime = lastUpdateTime;
    }

    public String getCityEnName() {
        return cityEnName;
    }

    public void setCityEnName(String cityEnName) {
        this.cityEnName = cityEnName;
    }

    public String getRegionEnName() {
        return regionEnName;
    }

    public void setRegionEnName(String regionEnName) {
        this.regionEnName = regionEnName;
    }

    public int getDirection() {
        return direction;
    }

    public void setDirection(int direction) {
        this.direction = direction;
    }

    public int getDistanceToSubway() {
        return distanceToSubway;
    }

    public void setDistanceToSubway(int distanceToSubway) {
        this.distanceToSubway = distanceToSubway;
    }

    public String getSubwayLineName() {
        return subwayLineName;
    }

    public void setSubwayLineName(String subwayLineName) {
        this.subwayLineName = subwayLineName;
    }

    public String getSubwayStationName() {
        return subwayStationName;
    }

    public void setSubwayStationName(String subwayStationName) {
        this.subwayStationName = subwayStationName;
    }

    public String getStreet() {
        return street;
    }

    public void setStreet(String street) {
        this.street = street;
    }

    public String getDistrict() {
        return district;
    }

    public void setDistrict(String district) {
        this.district = district;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getLayoutDesc() {
        return layoutDesc;
    }

    public void setLayoutDesc(String layoutDesc) {
        this.layoutDesc = layoutDesc;
    }

    public String getTraffic() {
        return traffic;
    }

    public void setTraffic(String traffic) {
        this.traffic = traffic;
    }

    public String getRoundService() {
        return roundService;
    }

    public void setRoundService(String roundService) {
        this.roundService = roundService;
    }

    public int getRentWay() {
        return rentWay;
    }

    public void setRentWay(int rentWay) {
        this.rentWay = rentWay;
    }

    public List<String> getTags() {
        return tags;
    }

    public void setTags(List<String> tags) {
        this.tags = tags;
    }

    public List<HouseSuggest> getSuggest() {
        return suggest;
    }

    public void setSuggest(List<HouseSuggest> suggest) {
        this.suggest = suggest;
    }

}
