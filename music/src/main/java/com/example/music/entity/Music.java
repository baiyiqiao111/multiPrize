package com.example.music.entity;

import java.util.Date;

public class Music {
    private int id;          // 音乐ID
    private String title;        // 标题
    private String content;      // 内容介绍
    private String tags;         // 标签
    private Date publishTime;    // 发布时间
    private String pictureUrl;   // 封面图片
    private String soundUrl;     // 音频地址
    private String author;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getTags() {
        return tags;
    }

    public void setTags(String tags) {
        this.tags = tags;
    }

    public Date getPublishTime() {
        return publishTime;
    }

    public void setPublishTime(Date publishTime) {
        this.publishTime = publishTime;
    }

    public String getPictureUrl() {
        return pictureUrl;
    }

    public void setPictureUrl(String pictureUrl) {
        this.pictureUrl = pictureUrl;
    }

    public String getSoundUrl() {
        return soundUrl;
    }

    public void setSoundUrl(String soundUrl) {
        this.soundUrl = soundUrl;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public Music(int id, String title, String content, String tags, Date publishTime, String pictureUrl, String soundUrl, String author) {
        this.id = id;
        this.title = title;
        this.content = content;
        this.tags = tags;
        this.publishTime = publishTime;
        this.pictureUrl = pictureUrl;
        this.soundUrl = soundUrl;
        this.author = author;
    }

    public Music() {
    }
}
