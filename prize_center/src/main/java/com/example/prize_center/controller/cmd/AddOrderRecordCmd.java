package com.example.prize_center.controller.cmd;

public class AddOrderRecordCmd {
    private String outBizNo;
    private String code;
    private int amount;

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

    public AddOrderRecordCmd(String outBizNo, String code, int amount) {
        this.outBizNo = outBizNo;
        this.code = code;
        this.amount = amount;
    }

    public AddOrderRecordCmd() {
    }
}
