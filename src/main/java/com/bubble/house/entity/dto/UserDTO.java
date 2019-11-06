package com.bubble.house.entity.dto;

import java.io.Serializable;

/**
 * User
 *
 * @author wugang
 * date: 2019-11-06 10:47
 **/
public class UserDTO implements Serializable {
    private static final long serialVersionUID = 3765122694904190190L;

    private Long id;
    private String name;
    private String avatar;
    private String phoneNumber;
    private String lastLoginTime;

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

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getLastLoginTime() {
        return lastLoginTime;
    }

    public void setLastLoginTime(String lastLoginTime) {
        this.lastLoginTime = lastLoginTime;
    }

}
