package com.bubble.house.entity.search;

import java.io.Serializable;

/**
 * House聚合
 *
 * @author wugang
 * date: 2019-11-08 18:19
 **/
public class HouseBucketEntity implements Serializable {
    private static final long serialVersionUID = 2049668310489953126L;

    /**
     * 聚合bucket的key
     */
    private String key;

    /**
     * 聚合结果值
     */
    private long count;

    public HouseBucketEntity(String key, long count) {
        this.key = key;
        this.count = count;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public long getCount() {
        return count;
    }

    public void setCount(long count) {
        this.count = count;
    }

}
