package com.example.prize_center.mapper;

import com.example.prize_center.entity.OrderRecord;
import org.apache.ibatis.annotations.Mapper;

import java.util.Date;
import java.util.List;
@Mapper
public interface OrderRecordMapper {
    void add(OrderRecord orderRecord);

    void delete(int id);

    void modify(OrderRecord orderRecord);

    OrderRecord queryById(int id);

    OrderRecord queryByOutBizNo(String outBizNo);

    List<OrderRecord> queryAll(Date startTime, Date endTime, int start, int size);

    List<OrderRecord> queryByTime(Date startTime, Date endTime);
}
