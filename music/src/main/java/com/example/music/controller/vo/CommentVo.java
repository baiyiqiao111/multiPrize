package com.example.music.controller.vo;

import java.util.Date;

public class CommentVo {
    private int id;
    private int userId;
    private int musicId;
    private int parentId;
    private String comment;
    private Date publishTime;

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

    public Date getPublishTime() {
        return publishTime;
    }

    public void setPublishTime(Date publishTime) {
        this.publishTime = publishTime;
    }

    public CommentVo(int id, int userId, int musicId, int parentId, String comment, Date publishTime) {
        this.id = id;
        this.userId = userId;
        this.musicId = musicId;
        this.parentId = parentId;
        this.comment = comment;
        this.publishTime = publishTime;
    }

    public CommentVo() {
    }
}
