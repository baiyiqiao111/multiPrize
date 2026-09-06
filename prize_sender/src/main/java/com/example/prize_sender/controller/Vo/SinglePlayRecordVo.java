package com.example.prize_sender.controller.Vo;

public class SinglePlayRecordVo {
    private BaseVo baseVo;
    private PlayRecordVo playRecordVo;

    public BaseVo getBaseVo() {
        return baseVo;
    }

    public void setBaseVo(BaseVo baseVo) {
        this.baseVo = baseVo;
    }

    public PlayRecordVo getPlayRecordVo() {
        return playRecordVo;
    }

    public void setPlayRecordVo(PlayRecordVo playRecordVo) {
        this.playRecordVo = playRecordVo;
    }

    public SinglePlayRecordVo() {
    }

    public SinglePlayRecordVo(BaseVo baseVo, PlayRecordVo playRecordVo) {
        this.baseVo = baseVo;
        this.playRecordVo = playRecordVo;
    }

}
