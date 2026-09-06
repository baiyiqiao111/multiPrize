package com.example.music.entity;

public class UserToken {
    private int userId;
    private String name;

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public UserToken(int userId, String name) {
        this.userId = userId;
        this.name = name;
    }

    public UserToken() {
    }
}
