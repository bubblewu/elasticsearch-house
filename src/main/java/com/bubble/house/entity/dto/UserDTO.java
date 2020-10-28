package com.bubble.house.entity.dto;

import java.io.Serializable;

/**
 * User：
 * DTO: 即数据传输对象。用于表现层和应用层之间的数据交互
 * 简单来说Model面向业务，我们是通过业务来定义Model的。而DTO是面向界面UI，是通过UI的需求来定义的。
 * 通过DTO我们实现了表现层与Model之间的解耦，表现层不引用Model，
 * 如果开发过程中我们的模型改变了，而界面没变，我们就只需要改Model而不需要去改表现层中的东西。
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

    @Override
    public String toString() {
        return "UserDTO{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", avatar='" + avatar + '\'' +
                ", phoneNumber='" + phoneNumber + '\'' +
                ", lastLoginTime='" + lastLoginTime + '\'' +
                '}';
    }
}
