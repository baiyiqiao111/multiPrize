package com.example.prize_sender.integration;

import com.example.prize_sender.exception.SendPrizeFailException;
import com.example.prize_sender.integration.client.PrizeCenterClient;
import com.example.prize_sender.integration.vo.MultiOrderRecordVo;
import com.example.prize_sender.integration.vo.OrderRecordVo;
import com.example.prize_sender.util.DateTimeUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Slf4j
@Service
public class OrderRecordIntegration {
    @Autowired
    private PrizeCenterClient prizeCenterClient;
    public List<OrderRecordVo> queryOrderRecordListByTime(Date startTime, Date endTime){
        try {
            MultiOrderRecordVo body = prizeCenterClient.queryOrderRecordByTime(
                    DateTimeUtil.getDateStr(startTime,"yyyy-MM-dd'T'HH:mm:ss"),
                    DateTimeUtil.getDateStr(endTime,"yyyy-MM-dd'T'HH:mm:ss"));
            if(body==null||body.getBaseVo()==null||!body.getBaseVo().isSuccess()) {
                throw new SendPrizeFailException("调用第三方系统发放奖励失败");
            }
            return  body.getOrderRecordVoList();
        }catch(Exception e){
            log.error("调用第三方系统发放奖励失败");
            return new ArrayList<>();
        }
    }
}