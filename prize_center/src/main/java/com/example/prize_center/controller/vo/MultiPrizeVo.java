package com.example.prize_center.controller.vo;

import java.util.List;

public class MultiPrizeVo {
    private BaseVo baseVo;
    private List<PrizeVo> prizeVoList;

    public BaseVo getBaseVo() {
        return baseVo;
    }

    public void setBaseVo(BaseVo baseVo) {
        this.baseVo = baseVo;
    }

    public List<PrizeVo> getPrizeVoList() {
        return prizeVoList;
    }

    public void setPrizeVoList(List<PrizeVo> prizeVoList) {
        this.prizeVoList = prizeVoList;
    }

    public MultiPrizeVo(BaseVo baseVo, List<PrizeVo> prizeVoList) {
        this.baseVo = baseVo;
        this.prizeVoList = prizeVoList;
    }

    public MultiPrizeVo() {
    }
}
