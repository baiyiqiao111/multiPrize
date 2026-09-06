package com.example.AI_chatbot.Integration.vo;

import com.example.AI_chatbot.controller.Vo.BaseVo;

import java.util.List;

public class MultiOrderRecordVo {
    private List<OrderRecordVo> orderRecordVoList;
    private BaseVo baseVo;

    public List<OrderRecordVo> getOrderRecordVoList() {
        return orderRecordVoList;
    }

    public void setOrderRecordVoList(
            List<OrderRecordVo> orderRecordVoList) {
        this.orderRecordVoList = orderRecordVoList;
    }

    public BaseVo getBaseVo() {
        return baseVo;
    }

    public void setBaseVo(BaseVo baseVo) {
        this.baseVo = baseVo;
    }
}
