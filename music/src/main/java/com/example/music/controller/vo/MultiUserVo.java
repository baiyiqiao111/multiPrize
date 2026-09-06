package com.example.music.controller.vo;

import java.util.List;

public class MultiUserVo {
    private List<UserVo> userVoList;
    private BaseVo baseVo;

    public List<UserVo> getUserVoList() {
        return userVoList;
    }

    public void setUserVoList(List<UserVo> userVoList) {
        this.userVoList = userVoList;
    }

    public BaseVo getBaseVo() {
        return baseVo;
    }

    public void setBaseVo(BaseVo baseVo) {
        this.baseVo = baseVo;
    }

    public MultiUserVo(List<UserVo> userVoList, BaseVo baseVo) {
        this.userVoList = userVoList;
        this.baseVo = baseVo;
    }

    public MultiUserVo() {
    }
}
