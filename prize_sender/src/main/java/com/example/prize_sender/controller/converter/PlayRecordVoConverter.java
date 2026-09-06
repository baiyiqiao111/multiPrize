package com.example.prize_sender.controller.converter;

import com.example.prize_sender.controller.Vo.PlayRecordVo;
import com.example.prize_sender.entity.PlayRecord;

import java.util.ArrayList;
import java.util.List;

public class PlayRecordVoConverter {
    public static PlayRecordVo convertToVo(PlayRecord playRecord){

        if(playRecord == null){
            return null;
        }

        PlayRecordVo playRecordVo = new PlayRecordVo();

        playRecordVo.setId(playRecord.getId());
        playRecordVo.setUserId(playRecord.getUserId());
        playRecordVo.setMusicId(playRecord.getMusicId());
        playRecordVo.setDuration(playRecord.getDuration());
        playRecordVo.setSyncTime(playRecord.getSyncTime());

        return playRecordVo;
    }

    public static List<PlayRecordVo> convertToVoList(List<PlayRecord> playRecordList){

        List<PlayRecordVo> playRecordVoList = new ArrayList<>();

        if(playRecordList == null){
            return playRecordVoList;
        }

        for(PlayRecord playRecord : playRecordList){
            playRecordVoList.add(convertToVo(playRecord));
        }

        return playRecordVoList;
    }
}
