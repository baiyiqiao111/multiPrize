package com.example.prize_center.service.Impl;

import com.example.prize_center.controller.cmd.AddOrderRecordCmd;
import com.example.prize_center.controller.cmd.ModifyOrderRecordCmd;
import com.example.prize_center.entity.OrderRecord;
import com.example.prize_center.exception.OrderRecordNotExistException;
import com.example.prize_center.mapper.OrderRecordMapper;
import com.example.prize_center.service.OrderRecordService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

@Service
public class OrderRecordServiceImpl implements OrderRecordService {
    @Autowired
    private OrderRecordMapper orderRecordMapper;

    @Override
    public void add(OrderRecord orderRecord) {
        orderRecordMapper.add(orderRecord);
    }

    @Override
    public void delete(int id) {

        OrderRecord orderRecord = orderRecordMapper.queryById(id);

        if (orderRecord == null) {
            throw new IllegalArgumentException("订单记录不存在");
        }

        orderRecordMapper.delete(id);
    }

    @Override
    public void modify(OrderRecord orderRecord) {

        OrderRecord orderRecordModify =
                orderRecordMapper.queryById(orderRecord.getId());

        if (orderRecord == null) {
            throw new OrderRecordNotExistException("订单记录不存在");
        }

        if (orderRecord.getAmount() <= 0) {
            throw new IllegalArgumentException("奖品数量必须大于0");
        }

        orderRecord.setCode(orderRecord.getCode());
        orderRecord.setAmount(orderRecord.getAmount());

        orderRecordMapper.modify(orderRecord);
    }

    @Override
    public OrderRecord queryById(int id) {
        return orderRecordMapper.queryById(id);
    }

    @Override
    public OrderRecord queryByOutBizNo(String outBizNo) {
        return orderRecordMapper.queryByOutBizNo(outBizNo);
    }

    @Override
    public List<OrderRecord> queryAll(Date startTime, Date endTime, int start, int size) {
        return orderRecordMapper.queryAll(startTime,endTime,start,size);
    }

    @Override
    public List<OrderRecord> queryByTime(Date startTime, Date endTime) {
        return orderRecordMapper.queryByTime(startTime,endTime);
    }
}
