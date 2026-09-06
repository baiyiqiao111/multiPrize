package com.example.prize_sender.controller.cmd;

public class SyncCmd {
    private  int userId;
    private  int musicId;
    private  int duration;
    private String scene;
    private String prizeCode;

    public String getScene() {
        return scene;
    }

    public void setScene(String scene) {
        this.scene = scene;
    }

    public String getPrizeCode() {
        return prizeCode;
    }

    public void setPrizeCode(String prizeCode) {
        this.prizeCode = prizeCode;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public int getMusicId() {
        return musicId;
    }

    public void setMusicId(int musicId) {
        this.musicId = musicId;
    }

    public int getDuration() {
        return duration;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }

    public SyncCmd(int userId, int musicId, int duration, String scene, String prizeCode) {
        this.userId = userId;
        this.musicId = musicId;
        this.duration = duration;
        this.scene = scene;
        this.prizeCode = prizeCode;
    }

    public SyncCmd() {
    }
}
