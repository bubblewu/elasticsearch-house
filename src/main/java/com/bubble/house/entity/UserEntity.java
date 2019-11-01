package com.bubble.house.entity;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

/**
 * 用户
 *
 * @author wugang
 * date: 2019-11-01 18:05
 **/
@Entity
@Table(name = "user")
public class UserEntity implements Serializable {
    private static final long serialVersionUID = -3364211098133184025L;

    @Id
    // 为一个实体生成一个唯一标识的主键
    // - IDENTITY 主键由数据库生成, 采用数据库自增长, Oracle不支持这种方式
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;  // 用户唯一id
    private String name;
    private String password;
    private String email;
    @Column(name = "phone_number")
    private String phoneNumber;
    private int status;  // 用户状态 0-正常 1-封禁
    @Column(name = "create_time")
    private Date createTime;  // 用户账号创建时间
    @Column(name = "last_login_time")
    private Date lastLoginTime;  // 上次登录时间
    @Column(name = "last_update_time")
    private Date lastUpdateTime;  // 上次更新记录时间
    private String avatar;  // 头像

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

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
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

    public Date getLastLoginTime() {
        return lastLoginTime;
    }

    public void setLastLoginTime(Date lastLoginTime) {
        this.lastLoginTime = lastLoginTime;
    }

    public Date getLastUpdateTime() {
        return lastUpdateTime;
    }

    public void setLastUpdateTime(Date lastUpdateTime) {
        this.lastUpdateTime = lastUpdateTime;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }
}
