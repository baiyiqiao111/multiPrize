package com.example.music.entity;

import com.example.music.constant.UserStatus;

import java.util.Date;

public class User {
    private int id;
    private String name;
    private String password;
    private String email;
    private String interests;
    private int age;
    private String gender;
    private Date registerTime;
    private UserStatus status;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Date getRegisterTime() {
        return registerTime;
    }

    public void setRegisterTime(Date registerTime) {
        this.registerTime = registerTime;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getInterests() {
        return interests;
    }

    public void setInterests(String interests) {
        this.interests = interests;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public UserStatus getStatus() {
        return status;
    }

    public void setStatus(UserStatus status) {
        this.status = status;
    }

    public User(int id, String name, String password, String email, String interests, int age, String gender, Date registerTime, UserStatus status) {
        this.id = id;
        this.name = name;
        this.password = password;
        this.email = email;
        this.interests = interests;
        this.age = age;
        this.gender = gender;
        this.registerTime = registerTime;
        this.status = status;
    }

    public User() {
    }
}
