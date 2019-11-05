package com.bubble.house.entity.house;

import javax.persistence.*;
import java.io.Serializable;

/**
 * 城市地铁线路信息
 *
 * @author wugang
 * date: 2019-11-05 17:27
 **/
@Entity
@Table(name = "subway")
public class SubwayEntity implements Serializable {
    private static final long serialVersionUID = 1355669401720113203L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    @Column(name = "city_en_name")
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
