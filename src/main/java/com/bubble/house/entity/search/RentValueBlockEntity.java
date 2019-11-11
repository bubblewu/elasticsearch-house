package com.bubble.house.entity.search;

import com.google.common.collect.ImmutableMap;

import java.io.Serializable;
import java.util.Map;

/**
 * 带区间的常用数值定义
 *
 * @author wugang
 * date: 2019-11-08 18:42
 **/
public class RentValueBlockEntity implements Serializable {
    private static final long serialVersionUID = -2734369010272565639L;

    /**
     * 价格区间定义
     */
    public static final Map<String, RentValueBlockEntity> PRICE_BLOCK;

    /**
     * 面积区间定义
     */
    public static final Map<String, RentValueBlockEntity> AREA_BLOCK;

    /**
     * 无限制区间
     */
    public static final RentValueBlockEntity ALL = new RentValueBlockEntity("*", -1, -1);

    static {
        PRICE_BLOCK = ImmutableMap.<String, RentValueBlockEntity>builder()
                .put("*-1000", new RentValueBlockEntity("*-1000", -1, 1000))
                .put("1000-3000", new RentValueBlockEntity("1000-3000", 1000, 3000))
                .put("3000-*", new RentValueBlockEntity("3000-*", 3000, -1))
                .build();

        AREA_BLOCK = ImmutableMap.<String, RentValueBlockEntity>builder()
                .put("*-30", new RentValueBlockEntity("*-30", -1, 30))
                .put("30-50", new RentValueBlockEntity("30-50", 30, 50))
                .put("50-*", new RentValueBlockEntity("50-*", 50, -1))
                .build();
    }

    private String key;
    private int min;
    private int max;

    public RentValueBlockEntity(String key, int min, int max) {
        this.key = key;
        this.min = min;
        this.max = max;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public int getMin() {
        return min;
    }

    public void setMin(int min) {
        this.min = min;
    }

    public int getMax() {
        return max;
    }

    public void setMax(int max) {
        this.max = max;
    }

    public static RentValueBlockEntity matchPrice(String key) {
        RentValueBlockEntity block = PRICE_BLOCK.get(key);
        if (block == null) {
            return ALL;
        }
        return block;
    }

    public static RentValueBlockEntity matchArea(String key) {
        RentValueBlockEntity block = AREA_BLOCK.get(key);
        if (block == null) {
            return ALL;
        }
        return block;
    }

}
