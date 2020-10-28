package com.bubble.house.service;

import java.io.Serializable;

/**
 * 单一服务接口通用结构
 *
 * @author wugang
 * date: 2019-11-05 18:25
 **/
public class ServiceResultEntity<T> implements Serializable {
    private static final long serialVersionUID = 8366884940162742457L;

    private boolean success;
    private String message;
    private T result;

    public ServiceResultEntity(boolean success) {
        this.success = success;
    }

    public ServiceResultEntity(boolean success, String message) {
        this.success = success;
        this.message = message;
    }

    public ServiceResultEntity(boolean success, String message, T result) {
        this.success = success;
        this.message = message;
        this.result = result;
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public T getResult() {
        return result;
    }

    public void setResult(T result) {
        this.result = result;
    }

    public static <T> ServiceResultEntity<T> success() {
        return new ServiceResultEntity<>(true);
    }

    public static <T> ServiceResultEntity<T> of(T result) {
        ServiceResultEntity<T> serviceResult = new ServiceResultEntity<>(true);
        serviceResult.setResult(result);
        return serviceResult;
    }

    public static <T> ServiceResultEntity<T> notFound() {
        return new ServiceResultEntity<>(false, Message.NOT_FOUND.getValue());
    }

    public enum Message {
        /**
         * 服务返回信息
         */
        NOT_FOUND("Not Found Resource!"),
        NOT_LOGIN("User not login!");

        private String value;

        Message(String value) {
            this.value = value;
        }

        public String getValue() {
            return value;
        }
    }

}
