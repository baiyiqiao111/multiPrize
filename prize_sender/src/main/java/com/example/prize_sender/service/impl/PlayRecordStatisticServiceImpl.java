package com.example.prize_sender.service.impl;

import com.example.prize_sender.entity.PlayRecord;
import com.example.prize_sender.entity.PlayRecordStatistic;
import com.example.prize_sender.exception.RecordStatisticNotExistException;
import com.example.prize_sender.mapper.PlayRecordStatisticMapper;
import com.example.prize_sender.service.PlayRecordService;
import com.example.prize_sender.service.PlayRecordStatisticService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
@Service
public class PlayRecordStatisticServiceImpl implements PlayRecordStatisticService {
    @Autowired
    private PlayRecordStatisticMapper playRecordStatisticMapper;

    @Override
    public void add(PlayRecordStatistic playRecordStatistic) {
        playRecordStatisticMapper.add(playRecordStatistic);
    }

    @Override
    public void delete(int id) {
        playRecordStatisticMapper.delete(id);
    }

    @Override
    public void modify(PlayRecordStatistic playRecordStatistic) {
        PlayRecordStatistic recordStatistic = playRecordStatisticMapper.queryByUserIdAndDate(playRecordStatistic.getUserId(), playRecordStatistic.getPlayDate());
        if(recordStatistic==null){
            throw new RecordStatisticNotExistException("用户当天总时长记录不存在");
        }
        recordStatistic.setTotalDuration(playRecordStatistic.getTotalDuration());
        playRecordStatisticMapper.modify(recordStatistic);
    }

    @Override
    public List<PlayRecordStatistic> queryAll() {
        return playRecordStatisticMapper.queryAll();
    }

    @Override
    public PlayRecordStatistic queryById(int id) {
        return playRecordStatisticMapper.queryById(id);
    }

    @Override
    public PlayRecordStatistic queryByUserIdAndDate(int useId, String playDate) {
        return playRecordStatisticMapper.queryByUserIdAndDate(useId,playDate);
    }
}
