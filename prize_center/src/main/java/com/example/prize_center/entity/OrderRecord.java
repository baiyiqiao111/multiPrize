package com.example.prize_center.entity;

import java.util.Date;

public class OrderRecord {
    private int id;
    private String outBizNo;
    private String code;
    private int amount;
    private Date prizeTime;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getOutBizNo() {
        return outBizNo;
    }

    public void setOutBizNo(String outBizNo) {
        this.outBizNo = outBizNo;
    }

    public int getAmount() {
        return amount;
    }

    public void setAmount(int amount) {
        this.amount = amount;
    }

    public Date getPrizeTime() {
        return prizeTime;
    }

    public void setPrizeTime(Date prizeTime) {
        this.prizeTime = prizeTime;
    }

    public OrderRecord(int id, String outBizNo, String code, int amount, Date prizeTime) {
        this.id = id;
        this.outBizNo = outBizNo;
        this.code = code;
        this.amount = amount;
        this.prizeTime = prizeTime;
    }

    public OrderRecord() {
    }
}
