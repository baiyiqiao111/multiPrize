package com.example.prize_center.service;

import com.example.prize_center.controller.cmd.AddOrderRecordCmd;
import com.example.prize_center.controller.cmd.ModifyOrderRecordCmd;
import com.example.prize_center.entity.OrderRecord;

import java.util.Date;
import java.util.List;

public interface OrderRecordService {
    void add(OrderRecord orderRecord);

    void delete(int id);

    void modify(OrderRecord orderRecord);

    OrderRecord queryById(int id);

    OrderRecord queryByOutBizNo(String outBizNo);

    List<OrderRecord> queryAll(Date startTime, Date endTime, int start, int size);

    List<OrderRecord> queryByTime(Date startTime, Date endTime);
}
