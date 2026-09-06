package com.example.prize_sender.entity;

import java.util.Date;

public class PrizeRecord {
    private int id;
    private int userId;
    private String scene;
    private String code;
    private String dateStr;
    private int stage;
    private int amount;
    private Date prizeTime;
    private String outBizNo;

    public String getOutBizNo() {
        return outBizNo;
    }

    public void setOutBizNo(String outBizNo) {
        this.outBizNo = outBizNo;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public String getScene() {
        return scene;
    }

    public void setScene(String scene) {
        this.scene = scene;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getDateStr() {
        return dateStr;
    }

    public void setDateStr(String dateStr) {
        this.dateStr = dateStr;
    }

    public int getStage() {
        return stage;
    }

    public void setStage(int stage) {
        this.stage = stage;
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

    public PrizeRecord(int id, int userId, String scene, String code, String dateStr, int stage, int amount, Date prizeTime, String outBizNo) {
        this.id = id;
        this.userId = userId;
        this.scene = scene;
        this.code = code;
        this.dateStr = dateStr;
        this.stage = stage;
        this.amount = amount;
        this.prizeTime = prizeTime;
        this.outBizNo = outBizNo;
    }

    public PrizeRecord() {
    }
}
