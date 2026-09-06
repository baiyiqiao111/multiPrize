package com.example.music.controller.cmd;

import java.util.Date;

public class AddCommentCmd {
    private int userId;
    private int musicId;
    private int parentId;
    private String comment;

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

    public int getParentId() {
        return parentId;
    }

    public void setParentId(int parentId) {
        this.parentId = parentId;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public AddCommentCmd(int userId, int musicId, int parentId, String comment) {
        this.userId = userId;
        this.musicId = musicId;
        this.parentId = parentId;
        this.comment = comment;
    }

    public AddCommentCmd() {
    }
}
