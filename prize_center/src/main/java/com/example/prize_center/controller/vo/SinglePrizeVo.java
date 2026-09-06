package com.example.prize_center.controller.vo;

public class SinglePrizeVo {
    private BaseVo baseVo;
    private PrizeVo prizeVo;

    public BaseVo getBaseVo() {
        return baseVo;
    }

    public void setBaseVo(BaseVo baseVo) {
        this.baseVo = baseVo;
    }

    public PrizeVo getPrizeVo() {
        return prizeVo;
    }

    public void setPrizeVo(PrizeVo prizeVo) {
        this.prizeVo = prizeVo;
    }

    public SinglePrizeVo(BaseVo baseVo, PrizeVo prizeVo) {
        this.baseVo = baseVo;
        this.prizeVo = prizeVo;
    }

    public SinglePrizeVo() {
    }
}
