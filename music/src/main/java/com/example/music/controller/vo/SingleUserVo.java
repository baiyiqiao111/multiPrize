package com.example.music.controller.vo;

public class SingleUserVo {
    private UserVo userVo;
    private BaseVo baseVo;

    public UserVo getUserVo() {
        return userVo;
    }

    public void setUserVo(UserVo userVo) {
        this.userVo = userVo;
    }

    public BaseVo getBaseVo() {
        return baseVo;
    }

    public void setBaseVo(BaseVo baseVo) {
        this.baseVo = baseVo;
    }

    public SingleUserVo(UserVo userVo, BaseVo baseVo) {
        this.userVo = userVo;
        this.baseVo = baseVo;
    }

    public SingleUserVo() {
    }
}
