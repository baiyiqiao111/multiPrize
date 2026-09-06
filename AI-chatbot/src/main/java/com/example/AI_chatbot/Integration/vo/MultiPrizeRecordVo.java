package com.example.AI_chatbot.Integration.vo;

import com.example.AI_chatbot.controller.Vo.BaseVo;

import java.util.List;

public class MultiPrizeRecordVo {
    private List<PrizeRecordVo> prizeRecordVoList;

    private BaseVo baseVo;

    public List<PrizeRecordVo> getPrizeRecordVoList() {
        return prizeRecordVoList;
    }

    public void setPrizeRecordVoList(List<PrizeRecordVo> prizeRecordVoList) {
        this.prizeRecordVoList = prizeRecordVoList;
    }

    public BaseVo getBaseVo() {
        return baseVo;
    }

    public void setBaseVo(BaseVo baseVo) {
        this.baseVo = baseVo;
    }

    public MultiPrizeRecordVo(List<PrizeRecordVo> prizeRecordVoList, BaseVo baseVo) {
        this.prizeRecordVoList = prizeRecordVoList;
        this.baseVo = baseVo;
    }

    public MultiPrizeRecordVo() {
    }
}
