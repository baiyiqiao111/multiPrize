package com.example.prize_sender.entity;

public class SystemConfig {
    private int id;
    private String code;
    private String value;

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

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public SystemConfig(int id, String value, String code) {
        this.id = id;
        this.value = value;
        this.code = code;
    }

    public SystemConfig() {
    }
}
