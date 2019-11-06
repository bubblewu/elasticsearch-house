package com.bubble.house.base.api;

import java.io.Serializable;

/**
 * Datatables响应结构：
 * Datatables是一款jquery表格插件。它是一个高度灵活的工具，可以将任何HTML表格添加高级的交互功能。
 *
 * @author wugang
 * date: 2019-11-06 16:06
 **/
public class ApiDataTableResponse extends ApiResponse implements Serializable {
    private static final long serialVersionUID = -4290347329572642270L;
    private int draw;
    private long recordsTotal;
    private long recordsFiltered;

    public ApiDataTableResponse(ApiStatus status) {
        this(status.getCode(), status.getMsg(), null);
    }

    public ApiDataTableResponse(int code, String message, Object data) {
        super(code, message, data);
    }

    public int getDraw() {
        return draw;
    }

    public void setDraw(int draw) {
        this.draw = draw;
    }

    public long getRecordsTotal() {
        return recordsTotal;
    }

    public void setRecordsTotal(long recordsTotal) {
        this.recordsTotal = recordsTotal;
    }

    public long getRecordsFiltered() {
        return recordsFiltered;
    }

    public void setRecordsFiltered(long recordsFiltered) {
        this.recordsFiltered = recordsFiltered;
    }
}