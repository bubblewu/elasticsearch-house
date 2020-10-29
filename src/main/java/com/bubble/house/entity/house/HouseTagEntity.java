package com.bubble.house.entity.house;

import javax.persistence.*;
import java.io.Serializable;

/**
 * 房屋标签
 *
 * @author wugang
 * date: 2019-11-05 18:40
 **/
@Entity
@Table(name = "house_tag")
public class HouseTagEntity implements Serializable {
    private static final long serialVersionUID = -4892689195975289643L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "house_id")
    private Long houseId;

    private String name;

    public HouseTagEntity() {
    }

    public HouseTagEntity(Long houseId, String name) {
        this.houseId = houseId;
        this.name = name;
    }

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

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

}
