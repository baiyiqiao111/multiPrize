package com.example.prize_sender.entity;

import java.util.Date;

public class PlayRecordStatistic {
    private int id;
    private int userId;
    private int totalDuration;
    private String playDate;
    private Date lastSyncTime;

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

    public int getTotalDuration() {
        return totalDuration;
    }

    public void setTotalDuration(int totalDuration) {
        this.totalDuration = totalDuration;
    }

    public String getPlayDate() {
        return playDate;
    }

    public void setPlayDate(String playDate) {
        this.playDate = playDate;
    }

    public Date getLastSyncTime() {
        return lastSyncTime;
    }

    public void setLastSyncTime(Date lastSyncTime) {
        this.lastSyncTime = lastSyncTime;
    }

    public PlayRecordStatistic(int id, int userId, int totalDuration, String playDate, Date lastSyncTime) {
        this.id = id;
        this.userId = userId;
        this.totalDuration = totalDuration;
        this.playDate = playDate;
        this.lastSyncTime = lastSyncTime;
    }

    public PlayRecordStatistic() {
    }
}
