package com.example.prize_sender.service.impl;

import com.example.prize_sender.entity.PrizeRecord;
import com.example.prize_sender.exception.PrizeRecordNotExistException;
import com.example.prize_sender.mapper.PrizeRecordMapper;
import com.example.prize_sender.service.PrizeRecordService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
@Service
public class PrizeRecordServiceImpl implements PrizeRecordService {
    @Autowired
    private PrizeRecordMapper prizeRecordMapper;

    @Override
    public void add(PrizeRecord prizeRecord) {
        prizeRecordMapper.add(prizeRecord);
    }

    @Override
    public void delete(int id) {
        prizeRecordMapper.delete(id);
    }

    @Override
    public void modify(PrizeRecord prizeRecord) {
        PrizeRecord prizeRecordInDb = prizeRecordMapper.queryById(prizeRecord.getId());
        if(prizeRecordInDb==null){
            throw new PrizeRecordNotExistException("发奖记录不存在");
        }
        prizeRecordInDb.setId(prizeRecordInDb.getId());
        //prizeRecordInDb.setPrizeTime(prizeRecord.getPrizeTime());
        prizeRecordInDb.setAmount(prizeRecord.getAmount());
        prizeRecordInDb.setCode(prizeRecord.getCode());
        prizeRecordInDb.setScene(prizeRecord.getScene());
        prizeRecordInDb.setDateStr(prizeRecord.getDateStr());
        prizeRecordInDb.setUserId(prizeRecord.getUserId());
        prizeRecordInDb.setStage(prizeRecord.getStage());
        prizeRecordInDb.setOutBizNo(prizeRecord.getOutBizNo());
        prizeRecordMapper.modify(prizeRecordInDb);
    }

    @Override
    public PrizeRecord queryById(int id) {
        return prizeRecordMapper.queryById(id);
    }

    @Override
    public List<PrizeRecord> queryAll(int userId, Date startTime, Date endTime, int startIndex, int pageSize) {
        return prizeRecordMapper.queryAll(
                userId,
                startTime,
                endTime,
                startIndex,
                pageSize
        );
    }
}
