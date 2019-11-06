package com.bubble.house.entity.house;

import javax.persistence.*;
import java.io.Serializable;

/**
 * 房屋图片
 *
 * @author wugang
 * date: 2019-11-05 18:38
 **/
@Entity
@Table(name = "house_picture")
public class HousePictureEntity implements Serializable {
    private static final long serialVersionUID = -8325362802869112397L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "house_id")
    private Long houseId;

    private String path;

    @Column(name = "cdn_prefix")
    private String cdnPrefix;

    private int width;

    private int height;

    private String location;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getHouseId() {
        return houseId;
    }

    public void setHouseId(Long houseId) {
        this.houseId = houseId;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getCdnPrefix() {
        return cdnPrefix;
    }

    public void setCdnPrefix(String cdnPrefix) {
        this.cdnPrefix = cdnPrefix;
    }

    public int getWidth() {
        return width;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public int getHeight() {
        return height;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

}
