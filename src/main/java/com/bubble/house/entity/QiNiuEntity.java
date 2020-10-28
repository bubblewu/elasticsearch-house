package com.bubble.house.entity;

import java.io.Serializable;

/**
 * 七牛云服务
 * 参考：https://developer.qiniu.com/kodo/sdk/1239/java
 *
 * @author wugang
 * date: 2019-11-05 15:12
 **/
public class QiNiuEntity implements Serializable {
    private static final long serialVersionUID = 8424779362117980614L;

    private String key;
    private String hash;
    private String bucket;
    private int width;
    private int height;

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getHash() {
        return hash;
    }

    public void setHash(String hash) {
        this.hash = hash;
    }

    public String getBucket() {
        return bucket;
    }

    public void setBucket(String bucket) {
        this.bucket = bucket;
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

    @Override
    public String toString() {
        return "QiNiuDTO{" +
                "key='" + key + '\'' +
                ", hash='" + hash + '\'' +
                ", bucket='" + bucket + '\'' +
                ", width=" + width +
                ", height=" + height +
                '}';
    }
}
