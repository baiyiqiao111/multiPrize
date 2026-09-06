package com.example.prize_sender.controller.converter;

import com.example.prize_sender.controller.Vo.PlayRecordStatisticVo;
import com.example.prize_sender.entity.PlayRecordStatistic;
import com.example.prize_sender.service.PlayRecordStatisticService;

import java.util.ArrayList;
import java.util.List;

public class PlayRecordStatisticVoConverter {

    public static PlayRecordStatisticVo convertToVo(PlayRecordStatistic playRecordStatistic){

        PlayRecordStatisticVo playRecordStatisticVo = new PlayRecordStatisticVo();

        playRecordStatisticVo.setId(playRecordStatistic.getId());
        playRecordStatisticVo.setUserId(playRecordStatistic.getUserId());
        playRecordStatisticVo.setTotalDuration(playRecordStatistic.getTotalDuration());
        playRecordStatisticVo.setPlayDate(playRecordStatistic.getPlayDate());
        playRecordStatisticVo.setLastSyncTime(playRecordStatistic.getLastSyncTime());

        return playRecordStatisticVo;
    }

    public static List<PlayRecordStatisticVo> convertToVoList(List<PlayRecordStatistic> playRecordStatisticList){

        List<PlayRecordStatisticVo> playRecordStatisticVoList = new ArrayList<>();

        for (PlayRecordStatistic playRecordStatistic : playRecordStatisticList){
            playRecordStatisticVoList.add(convertToVo(playRecordStatistic));
        }

        return playRecordStatisticVoList;
    }
}