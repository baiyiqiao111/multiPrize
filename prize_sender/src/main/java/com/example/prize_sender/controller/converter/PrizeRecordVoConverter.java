package com.example.prize_sender.controller.converter;

import com.example.prize_sender.controller.Vo.PrizeRecordVo;
import com.example.prize_sender.entity.PrizeRecord;

import java.util.ArrayList;
import java.util.List;

public class PrizeRecordVoConverter {
    public static PrizeRecordVo convertToVo(PrizeRecord prizeRecord){

        PrizeRecordVo prizeRecordVo = new PrizeRecordVo();

        prizeRecordVo.setId(prizeRecord.getId());
        prizeRecordVo.setUserId(prizeRecord.getUserId());
        prizeRecordVo.setScene(prizeRecord.getScene());
        prizeRecordVo.setCode(prizeRecord.getCode());
        prizeRecordVo.setDateStr(prizeRecord.getDateStr());
        prizeRecordVo.setStage(prizeRecord.getStage());
        prizeRecordVo.setAmount(prizeRecord.getAmount());
        prizeRecordVo.setPrizeTime(prizeRecord.getPrizeTime());
        prizeRecordVo.setOutBizNo(prizeRecord.getOutBizNo());

        return prizeRecordVo;
    }

    public static List<PrizeRecordVo> convertToVoList(List<PrizeRecord> prizeRecordList){

        List<PrizeRecordVo> prizeRecordVoList = new ArrayList<>();

        for (PrizeRecord prizeRecord : prizeRecordList){
            prizeRecordVoList.add(convertToVo(prizeRecord));
        }

        return prizeRecordVoList;
    }
}
