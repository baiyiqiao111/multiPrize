package com.example.music.controller.cmd;

import java.util.Date;

public class AddMusicCmd {
    private String title;
    private String content;
    private String tags;
    private String pictureUrl;
    private String soundUrl;
    private String author;


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

    public AddMusicCmd(String title, String content, String tags, String soundUrl, String pictureUrl, String author, Date publishTime) {
        this.title = title;
        this.content = content;
        this.tags = tags;
        this.soundUrl = soundUrl;
        this.pictureUrl = pictureUrl;
        this.author = author;
    }

    public AddMusicCmd() {
    }
}
