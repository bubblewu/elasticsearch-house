package com.bubble.house.web.dto.house;

import java.io.Serializable;

/**
 * 地铁站点信息
 *
 * @author wugang
 * date: 2020-10-29 15:36
 **/
public class SubwayStationDTO implements Serializable {
    private static final long serialVersionUID = 5157787767039983481L;

    private Long id;
    private Long subwayId;
    private String name;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getSubwayId() {
        return subwayId;
    }

    public void setSubwayId(Long subwayId) {
        this.subwayId = subwayId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

}
