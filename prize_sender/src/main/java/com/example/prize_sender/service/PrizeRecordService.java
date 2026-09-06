package com.example.prize_sender.service;

import com.example.prize_sender.entity.PrizeRecord;

import java.util.Date;
import java.util.List;

public interface PrizeRecordService {
    void add(PrizeRecord prizeRecord);

    void delete(int id);

    void modify(PrizeRecord prizeRecord);

    PrizeRecord queryById(int id);

    List<PrizeRecord> queryAll(int userId,
                               Date startTime,
                               Date endTime,
                               int startIndex,
                               int pageSize);
}
