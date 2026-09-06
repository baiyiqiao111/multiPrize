package com.example.AI_chatbot.Integration.vo;

import com.example.AI_chatbot.controller.Vo.BaseVo;

import java.util.List;

public class MultiPlayRecordVo {
    private List<PlayRecordVo> playRecordVoList;
    private BaseVo baseVo;

    public List<PlayRecordVo> getPlayRecordVoList() {
        return playRecordVoList;
    }

    public void setPlayRecordVoList(List<PlayRecordVo> playRecordVoList) {
        this.playRecordVoList = playRecordVoList;
    }

    public BaseVo getBaseVo() {
        return baseVo;
    }

    public void setBaseVo(BaseVo baseVo) {
        this.baseVo = baseVo;
    }

    public MultiPlayRecordVo(List<PlayRecordVo> playRecordVoList, BaseVo baseVo) {
        this.playRecordVoList = playRecordVoList;
        this.baseVo = baseVo;
    }

    public MultiPlayRecordVo() {
    }
}
