package com.example.prize_sender.controller.Vo;

public class SinglePrizeRecordVo {
    private PrizeRecordVo prizeRecordVo;

    private BaseVo baseVo;

    public PrizeRecordVo getPrizeRecordVo() {
        return prizeRecordVo;
    }

    public void setPrizeRecordVo(PrizeRecordVo prizeRecordVo) {
        this.prizeRecordVo = prizeRecordVo;
    }

    public BaseVo getBaseVo() {
        return baseVo;
    }

    public void setBaseVo(BaseVo baseVo) {
        this.baseVo = baseVo;
    }

    public SinglePrizeRecordVo(PrizeRecordVo prizeRecordVo, BaseVo baseVo) {
        this.prizeRecordVo = prizeRecordVo;
        this.baseVo = baseVo;
    }

    public SinglePrizeRecordVo() {
    }
}
