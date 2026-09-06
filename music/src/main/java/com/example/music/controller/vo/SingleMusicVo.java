package com.example.music.controller.vo;

public class SingleMusicVo {
    private MusicVo musicVo;
    private BaseVo baseVo;

    public MusicVo getMusicVo() {
        return musicVo;
    }

    public void setMusicVo(MusicVo musicVo) {
        this.musicVo = musicVo;
    }

    public BaseVo getBaseVo() {
        return baseVo;
    }

    public void setBaseVo(BaseVo baseVo) {
        this.baseVo = baseVo;
    }

    public SingleMusicVo(MusicVo musicVo, BaseVo baseVo) {
        this.musicVo = musicVo;
        this.baseVo = baseVo;
    }

    public SingleMusicVo() {
    }
}
