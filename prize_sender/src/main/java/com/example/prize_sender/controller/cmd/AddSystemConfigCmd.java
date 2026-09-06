package com.example.prize_sender.controller.cmd;

public class AddSystemConfigCmd {
    private String code;
    private String value;

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public AddSystemConfigCmd(String code, String value) {
        this.code = code;
        this.value = value;
    }

    public AddSystemConfigCmd() {
    }
}
