package com.example.wiki.network;

public class DataCallBack<T> {

    public static final int SUCCESS = 101;
    public static final int FAILURE = 102;

    private int status;
    private String error;
    private T data;

    public DataCallBack(int status, String error, T data) {
        this.status = status;
        this.error = error;
        this.data = data;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }
}
