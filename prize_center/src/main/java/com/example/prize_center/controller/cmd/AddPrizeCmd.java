package com.example.prize_center.controller.cmd;

public class AddPrizeCmd {
    private String code;
    private int stock;
    private String icon;

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

    public AddPrizeCmd(String code, int stock, String icon) {
        this.code = code;
        this.stock = stock;
        this.icon = icon;
    }

    public AddPrizeCmd() {
    }
}
