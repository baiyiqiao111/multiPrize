package com.example.prize_sender.controller.Vo;

import java.util.List;

public class MultiSystemConfigVo {
    private BaseVo baseVo;
    private List<SystemConfigVo> systemConfigVoList;

    public BaseVo getBaseVo() {
        return baseVo;
    }

    public void setBaseVo(BaseVo baseVo) {
        this.baseVo = baseVo;
    }

    public List<SystemConfigVo> getSystemConfigVoList() {
        return systemConfigVoList;
    }

    public void setSystemConfigVoList(List<SystemConfigVo> systemConfigVoList) {
        this.systemConfigVoList = systemConfigVoList;
    }

    public MultiSystemConfigVo(BaseVo baseVo, List<SystemConfigVo> systemConfigVoList) {
        this.baseVo = baseVo;
        this.systemConfigVoList = systemConfigVoList;
    }

    public MultiSystemConfigVo() {
    }
}
