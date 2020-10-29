package com.bubble.house.web.dto.house;

import java.io.Serializable;

/**
 * 城市地铁信息DTO
 *
 * @author wugang
 * date: 2020-10-29 15:31
 **/
public class SubwayDTO implements Serializable {
    private static final long serialVersionUID = 8791792501409899010L;

    private Long id;
    private String name;
    private String cityEnName;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCityEnName() {
        return cityEnName;
    }

    public void setCityEnName(String cityEnName) {
        this.cityEnName = cityEnName;
    }
}
