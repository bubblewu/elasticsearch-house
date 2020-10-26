package com.bubble.house.base.api;

import java.io.Serializable;

/**
 * APi数据标准格式封装
 *
 * @author wugang
 * date: 2019-11-04 16:33
 **/
public class ApiResponse implements Serializable {
    private static final long serialVersionUID = 2500176763890028056L;

    /**
     * HTTP状态码
     */
    private int code;
    /**
     * HTTP自定义请求响应信息
     */
    private String msg;
    /**
     * 当前API请求的目标数据
     */
    private Object data;
    /**
     * 表示数据集是否还有更多的信息
     */
    private boolean more;

    public ApiResponse() {
        this.code = ApiStatus.SUCCESS.getCode();
        this.msg = ApiStatus.SUCCESS.getMsg();
    }

    public ApiResponse(int code, String msg, Object data) {
        this.code = code;
        this.msg = msg;
        this.data = data;
    }

    public static ApiResponse ofMessage(int code, String message) {
        return new ApiResponse(code, message, null);
    }

    public static ApiResponse ofSuccess(Object data) {
        return new ApiResponse(ApiStatus.SUCCESS.getCode(), ApiStatus.SUCCESS.getMsg(), data);
    }

    public static ApiResponse ofStatus(ApiStatus status) {
        return new ApiResponse(status.getCode(), status.getMsg(), null);
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public Object getData() {
        return data;
    }

    public void setData(Object data) {
        this.data = data;
    }

    public boolean isMore() {
        return more;
    }

    public void setMore(boolean more) {
        this.more = more;
    }

}
