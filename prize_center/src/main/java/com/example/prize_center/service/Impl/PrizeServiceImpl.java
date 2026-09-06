package com.example.prize_center.service.Impl;

import com.example.prize_center.controller.cmd.AddPrizeCmd;
import com.example.prize_center.controller.cmd.ModifyPrizeCmd;
import com.example.prize_center.entity.OrderRecord;
import com.example.prize_center.entity.Prize;
import com.example.prize_center.exception.PrizeNotExistException;
import com.example.prize_center.exception.PrizeStockNotEnoughException;
import com.example.prize_center.mapper.OrderRecordMapper;
import com.example.prize_center.mapper.PrizeMapper;
import com.example.prize_center.service.PrizeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;

@Service
public class PrizeServiceImpl implements PrizeService {
    @Autowired
    private PrizeMapper prizeMapper;
    @Autowired
    private OrderRecordMapper orderRecordMapper;

    @Override
    public void add(AddPrizeCmd addPrizeCmd) {
        Prize prize = new Prize();
        prize.setIcon(addPrizeCmd.getIcon());
        prize.setStock(addPrizeCmd.getStock());
        prize.setCode(addPrizeCmd.getCode());
        prizeMapper.add(prize);
    }

    @Override
    public void delete(int id) {
        prizeMapper.delete(id);
    }

    @Override
    public void modify(ModifyPrizeCmd modifyPrizeCmd) {

        Prize oldPrize = prizeMapper.queryById(modifyPrizeCmd.getId());

        if (oldPrize == null) {
            throw new PrizeNotExistException("奖品不存在");
        }

        oldPrize.setStock(modifyPrizeCmd.getStock());
        oldPrize.setIcon(modifyPrizeCmd.getIcon());

        prizeMapper.modify(oldPrize);
    }

    @Override
    public Prize queryById(int id) {
        return prizeMapper.queryById(id);
    }

    @Override
    public List<Prize> queryAll(int start, int size) {
        return prizeMapper.queryAll(start, size);
    }
    
    @Transactional
    @Override
    public void decrease(String code, int amount,String outBizNo) {
        Prize prize = prizeMapper.queryByCode(code);
        if(prize==null){
            throw new PrizeNotExistException("奖品不存在");
        }
        int stock = prize.getStock();
        if(stock<amount){
            throw new PrizeStockNotEnoughException("库存不足");
        }
        prize.setStock(stock-amount);
        prizeMapper.modify(prize);
        OrderRecord orderRecord = new OrderRecord();
        orderRecord.setOutBizNo(outBizNo);
        orderRecord.setAmount(amount);
        orderRecord.setCode(code);
        orderRecordMapper.add(orderRecord);
    }
}
