package com.example.prize_center.controller.vo;

public class BaseVo {
    private int code;
    private long cost;
    private  boolean success;
    private  String errorMessage;

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public long getCost() {
        return cost;
    }

    public void setCost(long cost) {
        this.cost = cost;
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public void setErrorMassage(String errorMassage) {
        this.errorMessage = errorMassage;
    }

    public BaseVo(int code, long cost, boolean success, String errorMassage) {
        this.code = code;
        this.cost = cost;
        this.success = success;
        this.errorMessage = errorMassage;
    }

    public BaseVo() {
    }
}
