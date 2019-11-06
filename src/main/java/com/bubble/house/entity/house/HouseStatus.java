package com.bubble.house.entity.house;

/**
 * 房源状态
 *
 * @author wugang
 * date: 2019-11-06 16:19
 **/
public enum HouseStatus {
    NOT_AUDITED(0), // 未审核
    PASSES(1), // 审核通过
    RENTED(2), // 已出租
    DELETED(3); // 逻辑删除

    private int value;

    HouseStatus(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }

}
