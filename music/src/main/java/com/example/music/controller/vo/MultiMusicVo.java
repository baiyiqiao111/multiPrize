package com.example.music.controller.vo;

import java.util.List;

public class MultiMusicVo {
    private List<MusicVo> musicVoList;
    private BaseVo baseVo;

    public BaseVo getBaseVo() {
        return baseVo;
    }

    public void setBaseVo(BaseVo baseVo) {
        this.baseVo = baseVo;
    }

    public List<MusicVo> getMusicVoList() {
        return musicVoList;
    }

    public void setMusicVoList(List<MusicVo> musicVoList) {
        this.musicVoList = musicVoList;
    }


    public MultiMusicVo(List<MusicVo> musicVoList, BaseVo baseVo) {
        this.musicVoList = musicVoList;
        this.baseVo = baseVo;
    }

    public MultiMusicVo() {
    }
}
