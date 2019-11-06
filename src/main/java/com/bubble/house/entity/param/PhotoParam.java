package com.bubble.house.entity.param;

import java.io.Serializable;

/**
 * Photo参数
 *
 * @author wugang
 * date: 2019-11-05 17:55
 **/
public class PhotoParam implements Serializable {
    private static final long serialVersionUID = -2404906430241116645L;

    private String path;

    private int width;

    private int height;

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
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

}
