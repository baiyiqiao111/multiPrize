package com.example.prize_center.controller.converter;

import com.example.prize_center.controller.vo.OrderRecordVo;
import com.example.prize_center.entity.OrderRecord;

import java.util.ArrayList;
import java.util.List;

public class OrderRecordVoConverter {
    public static OrderRecordVo convertToVo(OrderRecord orderRecord) {
        if (orderRecord == null) {
            return null;
        }

        OrderRecordVo orderRecordVo = new OrderRecordVo();

        orderRecordVo.setId(orderRecord.getId());
        orderRecordVo.setOutBizNo(orderRecord.getOutBizNo());
        orderRecordVo.setCode(orderRecord.getCode());
        orderRecordVo.setAmount(orderRecord.getAmount());
        orderRecordVo.setPrizeTime(orderRecord.getPrizeTime());

        return orderRecordVo;
    }

    public static List<OrderRecordVo> convertToVoList(
            List<OrderRecord> orderRecordList) {

        List<OrderRecordVo> orderRecordVoList = new ArrayList<>();

        if (orderRecordList == null) {
            return orderRecordVoList;
        }

        for (OrderRecord orderRecord : orderRecordList) {
            orderRecordVoList.add(convertToVo(orderRecord));
        }

        return orderRecordVoList;
    }
}
