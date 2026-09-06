package com.example.prize_sender.service.impl;

import com.example.prize_sender.entity.PlayRecord;
import com.example.prize_sender.exception.PlayRecordNotExistException;
import com.example.prize_sender.mapper.PlayRecordMapper;
import com.example.prize_sender.service.PlayRecordService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
@Service
public class PlayRecordServiceImpl implements PlayRecordService {
    @Autowired
    private PlayRecordMapper playRecordMapper;

    @Override
    public void add(PlayRecord playRecord) {
        playRecordMapper.add(playRecord);
    }

    @Override
    public void delete(int id) {
        playRecordMapper.delete(id);
    }

    @Override
    public void modify(PlayRecord playRecord) {
        PlayRecord record = playRecordMapper.queryById(playRecord.getId());
        if(record==null){
            throw new PlayRecordNotExistException("播放记录不存在");
        }
        record.setDuration(playRecord.getDuration());
        record.setUserId(playRecord.getUserId());
        record.setMusicId(playRecord.getMusicId());
        playRecordMapper.modify(playRecord);
    }

    @Override
    public List<PlayRecord> queryAll(int userId, Date startTime, Date endTime) {
        return playRecordMapper.queryAll(userId,startTime,endTime);
    }

    @Override
    public PlayRecord queryById(int id) {
        return playRecordMapper.queryById(id);
    }
}
