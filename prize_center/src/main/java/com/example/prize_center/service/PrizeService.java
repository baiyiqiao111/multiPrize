package com.example.prize_center.service;

import com.example.prize_center.controller.cmd.AddPrizeCmd;
import com.example.prize_center.controller.cmd.ModifyPrizeCmd;
import com.example.prize_center.entity.Prize;

import java.util.List;

public interface PrizeService {
    void add(AddPrizeCmd addPrizeCmd);

    void delete(int id);

    void modify(ModifyPrizeCmd modifyPrizeCmd);

    Prize queryById(int id);

    List<Prize> queryAll(int start,int size);

    void decrease(String code, int amount,String outBizNo);
}
