package com.bubble.house.web.dto.house;

import java.io.Serializable;
import java.util.Date;

/**
 * 预约看房实体类
 *
 * @author wugang
 * date: 2019-11-05 17:34
 **/
public class HouseSubscribeDTO implements Serializable {
    private static final long serialVersionUID = 7750559341792414246L;
    private Long id;
    private Long houseId;
    private Long userId;
    private Long adminId;
    /**
     * 预约状态 1-加入待看清单 2-已预约看房时间 3-看房完成
     */
    private int status;
    private Date createTime;
    private Date lastUpdateTime;
    private Date orderTime;
    private String telephone;
    private String desc;

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

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public Long getAdminId() {
        return adminId;
    }

    public void setAdminId(Long adminId) {
        this.adminId = adminId;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
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

    public Date getOrderTime() {
        return orderTime;
    }

    public void setOrderTime(Date orderTime) {
        this.orderTime = orderTime;
    }

    public String getTelephone() {
        return telephone;
    }

    public void setTelephone(String telephone) {
        this.telephone = telephone;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }
}
