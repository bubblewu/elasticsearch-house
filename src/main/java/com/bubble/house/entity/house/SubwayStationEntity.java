package com.bubble.house.entity.house;

import javax.persistence.*;
import java.io.Serializable;

/**
 * 地铁站点信息
 *
 * @author wugang
 * date: 2019-11-05 17:29
 **/
@Entity
@Table(name = "subway_station")
public class SubwayStationEntity implements Serializable {
    private static final long serialVersionUID = -1095963295930454157L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "subway_id")
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
