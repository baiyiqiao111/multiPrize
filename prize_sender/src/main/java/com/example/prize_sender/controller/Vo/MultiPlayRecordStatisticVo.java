package com.example.prize_sender.controller.Vo;

import java.util.List;

public class MultiPlayRecordStatisticVo {

    private List<PlayRecordStatisticVo> playRecordStatisticVoList;
    private BaseVo baseVo;

    public List<PlayRecordStatisticVo>
    getPlayRecordStatisticVoList() {
        return playRecordStatisticVoList;
    }

    public void setPlayRecordStatisticVoList(
            List<PlayRecordStatisticVo> playRecordStatisticVoList) {
        this.playRecordStatisticVoList =
                playRecordStatisticVoList;
    }

    public BaseVo getBaseVo() {
        return baseVo;
    }

    public void setBaseVo(BaseVo baseVo) {
        this.baseVo = baseVo;
    }
}