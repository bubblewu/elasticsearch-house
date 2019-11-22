package com.bubble.house.base.search;

import java.io.Serializable;

/**
 * 搜索输入提示
 *
 * @author wugang
 * date: 2019-11-11 18:16
 **/
public class HouseSuggest implements Serializable {
    private static final long serialVersionUID = -4548727266353478876L;

    private String input;
    private int weight = 10; // 默认权重

    public String getInput() {
        return input;
    }

    public void setInput(String input) {
        this.input = input;
    }

    public int getWeight() {
        return weight;
    }

    public void setWeight(int weight) {
        this.weight = weight;
    }

}
