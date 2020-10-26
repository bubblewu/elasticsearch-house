package com.bubble.house.base.api;

/**
 * 状态码信息
 *
 * @author wugang
 * date: 2019-11-04 16:37
 **/
public enum ApiStatus {
    /**
     * API状态码信息
     */
    SUCCESS(200, "OK"),
    BAD_REQUEST(400, "Bad Request"),
    NOT_FOUND(404, "Not Found"),
    INTERNAL_SERVER_ERROR(500, "Unknown Internal Error"),
    NOT_VALID_PARAM(40005, "Not valid Params"),
    NOT_SUPPORTED_OPERATION(40006, "Operation not supported"),
    NOT_LOGIN(50000, "Not Login");

    private int code;
    private String msg;

    ApiStatus(int code, String msg) {
        this.code = code;
        this.msg = msg;
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

}
