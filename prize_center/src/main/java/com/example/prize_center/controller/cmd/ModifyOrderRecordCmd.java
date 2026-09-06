package com.example.prize_center.controller.cmd;

public class ModifyOrderRecordCmd {
    private int id;
    private String code;
    private int amount;

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

    public int getAmount() {
        return amount;
    }

    public void setAmount(int amount) {
        this.amount = amount;
    }

    public ModifyOrderRecordCmd(int id, String code, int amount) {
        this.id = id;
        this.code = code;
        this.amount = amount;
    }

    public ModifyOrderRecordCmd() {
    }
}
