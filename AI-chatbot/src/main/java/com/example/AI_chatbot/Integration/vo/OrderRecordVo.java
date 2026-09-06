package com.example.AI_chatbot.Integration.vo;

import java.util.Date;

public class OrderRecordVo {
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

    public String getOutBizNo() {
        return outBizNo;
    }

    public void setOutBizNo(String outBizNo) {
        this.outBizNo = outBizNo;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
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

    public OrderRecordVo(String code, int id, String outBizNo, int amount, Date prizeTime) {
        this.code = code;
        this.id = id;
        this.outBizNo = outBizNo;
        this.amount = amount;
        this.prizeTime = prizeTime;
    }

    public OrderRecordVo() {
    }
}
