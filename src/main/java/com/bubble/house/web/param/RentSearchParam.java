package com.bubble.house.web.param;

import java.io.Serializable;

/**
 * 租房搜索信息
 *
 * @author wugang
 * date: 2019-11-08 18:04
 **/
public class RentSearchParam implements Serializable {
    private static final long serialVersionUID = 9058052820519822044L;

    /**
     * 城市英文名
     */
    private String cityEnName;
    /**
     * 区县
     */
    private String regionEnName;
    /**
     * 价格区间
     */
    private String priceBlock;
    /**
     * 便利区间
     */
    private String areaBlock;
    /**
     * 房间数
     */
    private int room;
    /**
     * 朝向
     */
    private int direction;
    /**
     * 关键词
     */
    private String keywords;
    /**
     * 租赁方式：合租、整租
     */
    private int rentWay = -1;
    /**
     * 默认排序字段：最后更新时间
     */
    private String orderBy = "lastUpdateTime";
    /**
     * 默认排序方式：降序
     */
    private String orderDirection = "desc";
    /**
     * 索引开始位置
     */
    private int start = 0;
    /**
     * 索引结束位置
     */
    private int size = 5;


    public int getStart() {
        return start > 0 ? start : 0;
    }

    public void setStart(int start) {
        this.start = start;
    }

    public int getSize() {
        if (this.size < 1) {
            return 5;
        } else if (this.size > 100) {
            return 100;
        } else {
            return this.size;
        }
    }

    public void setSize(int size) {
        this.size = size;
    }

    public int getRentWay() {
        if (rentWay > -2 && rentWay < 2) {
            return rentWay;
        } else {
            return -1;
        }
    }


    public int getDirection() {
        return direction;
    }

    public void setDirection(int direction) {
        this.direction = direction;
    }

    public String getCityEnName() {
        return cityEnName;
    }

    public void setCityEnName(String cityEnName) {
        this.cityEnName = cityEnName;
    }


    public void setRentWay(int rentWay) {
        this.rentWay = rentWay;
    }

    public String getRegionEnName() {
        return regionEnName;
    }

    public void setRegionEnName(String regionEnName) {
        this.regionEnName = regionEnName;
    }

    public String getPriceBlock() {
        return priceBlock;
    }

    public void setPriceBlock(String priceBlock) {
        this.priceBlock = priceBlock;
    }

    public String getAreaBlock() {
        return areaBlock;
    }

    public void setAreaBlock(String areaBlock) {
        this.areaBlock = areaBlock;
    }

    public int getRoom() {
        return room;
    }

    public void setRoom(int room) {
        this.room = room;
    }

    public String getKeywords() {
        return keywords;
    }

    public void setKeywords(String keywords) {
        this.keywords = keywords;
    }

    public String getOrderBy() {
        return orderBy;
    }

    public void setOrderBy(String orderBy) {
        this.orderBy = orderBy;
    }

    public String getOrderDirection() {
        return orderDirection;
    }

    public void setOrderDirection(String orderDirection) {
        this.orderDirection = orderDirection;
    }

    @Override
    public String toString() {
        return "RentSearch {" +
                "cityEnName='" + cityEnName + '\'' +
                ", regionEnName='" + regionEnName + '\'' +
                ", priceBlock='" + priceBlock + '\'' +
                ", areaBlock='" + areaBlock + '\'' +
                ", room=" + room +
                ", direction=" + direction +
                ", keywords='" + keywords + '\'' +
                ", rentWay=" + rentWay +
                ", orderBy='" + orderBy + '\'' +
                ", orderDirection='" + orderDirection + '\'' +
                ", start=" + start +
                ", size=" + size +
                '}';
    }

}
