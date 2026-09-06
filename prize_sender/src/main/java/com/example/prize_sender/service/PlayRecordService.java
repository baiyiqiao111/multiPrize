package com.example.prize_sender.service;

import com.example.prize_sender.entity.PlayRecord;

import java.util.Date;
import java.util.List;

public interface PlayRecordService {
    void add(PlayRecord playRecord);

    void delete(int id);

    void modify(PlayRecord playRecord);

    List<PlayRecord> queryAll(int useId, Date startTime, Date endTime);

    PlayRecord queryById(int id);
}
