package com.example.music.controller.vo;

public class UploadVo {
    private String url;
    private BaseVo baseVo;

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public BaseVo getBaseVo() {
        return baseVo;
    }

    public void setBaseVo(BaseVo baseVo) {
        this.baseVo = baseVo;
    }

    public UploadVo(String url, BaseVo baseVo) {
        this.url = url;
        this.baseVo = baseVo;
    }

    public UploadVo() {
    }
}