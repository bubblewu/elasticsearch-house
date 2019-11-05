package com.bubble.house.entity;

import java.io.Serializable;
import java.util.List;

/**
 * 通用多结果Service返回结构
 *
 * @author wugang
 * date: 2019-11-05 16:23
 **/
public class MultiResultEntity<T> implements Serializable {
    private static final long serialVersionUID = -4332117581412537479L;

    private long total;
    private List<T> result;

    public MultiResultEntity(long total, List<T> result) {
        this.total = total;
        this.result = result;
    }

    public long getTotal() {
        return total;
    }

    public void setTotal(long total) {
        this.total = total;
    }

    public List<T> getResult() {
        return result;
    }

    public void setResult(List<T> result) {
        this.result = result;
    }

    public int getResultSize() {
        if (this.result == null) {
            return 0;
        }
        return this.result.size();
    }

}
