package com.example.prize_center.controller.cmd;

public class ModifyPrizeCmd {
    private int id;
    private int stock;
    private String icon;
    private String code;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
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

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public ModifyPrizeCmd(int id, int stock, String icon, String code) {
        this.id = id;
        this.stock = stock;
        this.icon = icon;
        this.code = code;
    }

    public ModifyPrizeCmd() {
    }
}
