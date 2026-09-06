package com.example.prize_center.entity;

import java.util.Date;

public class Prize {
    private int id;
    private String code;
    private int stock;
    private String icon;
    private Date updateTime;

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

    public int getStock() {
        return stock;
    }

    public void setStock(int stock) {
        this.stock = stock;
    }

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

    public Date getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(Date updateTime) {
        this.updateTime = updateTime;
    }

    public Prize(int id, String code, int stock, String icon, Date updateTime) {
        this.id = id;
        this.code = code;
        this.stock = stock;
        this.icon = icon;
        this.updateTime = updateTime;
    }

    public Prize() {
    }
}
