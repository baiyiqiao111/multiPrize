package com.example.AI_chatbot.Integration.vo;

import com.example.AI_chatbot.controller.Vo.BaseVo;

public class SinglePlayRecordStatisticVo {
    private PlayRecordStatisticVo playRecordStatisticVo;
    private BaseVo baseVo;

    public PlayRecordStatisticVo getPlayRecordStatisticVo() {
        return playRecordStatisticVo;
    }

    public void setPlayRecordStatisticVo(
            PlayRecordStatisticVo playRecordStatisticVo) {
        this.playRecordStatisticVo = playRecordStatisticVo;
    }

    public BaseVo getBaseVo() {
        return baseVo;
    }

    public void setBaseVo(BaseVo baseVo) {
        this.baseVo = baseVo;
    }
}
