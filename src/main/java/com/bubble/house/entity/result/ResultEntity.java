package com.bubble.house.entity.result;

import java.io.Serializable;

/**
 * 单一结果
 *
 * @author wugang
 * date: 2019-11-05 18:25
 **/
public class ResultEntity<T> implements Serializable {
    private static final long serialVersionUID = 8366884940162742457L;

    private boolean success;
    private String message;
    private T result;

    public ResultEntity(boolean success) {
        this.success = success;
    }

    public ResultEntity(boolean success, String message) {
        this.success = success;
        this.message = message;
    }

    public ResultEntity(boolean success, String message, T result) {
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

    public static <T> ResultEntity<T> success() {
        return new ResultEntity<>(true);
    }

    public static <T> ResultEntity<T> of(T result) {
        ResultEntity<T> serviceResult = new ResultEntity<>(true);
        serviceResult.setResult(result);
        return serviceResult;
    }

    public static <T> ResultEntity<T> notFound() {
        return new ResultEntity<>(false, Message.NOT_FOUND.getValue());
    }

    public enum Message {
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
