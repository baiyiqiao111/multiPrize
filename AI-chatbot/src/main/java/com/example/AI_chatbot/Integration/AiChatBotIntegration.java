package com.example.AI_chatbot.Integration;

import com.example.AI_chatbot.Integration.client.PrizeCenterClient;
import com.example.AI_chatbot.Integration.client.PrizeSenderClient;
import com.example.AI_chatbot.Integration.vo.*;
import com.example.AI_chatbot.controller.Vo.SingleSystemConfigVo;
import com.example.AI_chatbot.controller.Vo.SystemConfigVo;
import com.example.AI_chatbot.exception.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
@Slf4j
@Service
public class AiChatBotIntegration {
    @Autowired
    private PrizeSenderClient prizeSenderClient;
    @Autowired
    private PrizeCenterClient prizeCenterClient;

    public List<PlayRecordVo> queryPlayRecordListByTime(int userId, String startTime, String endTime){
        try {
            MultiPlayRecordVo body = prizeSenderClient.queryPlayRecord(userId, startTime, endTime);
            if(body==null||body.getBaseVo()==null||!body.getBaseVo().isSuccess()) {
                throw new QueryPlayRecordException("调用第三方系查询播放记录失败");
            }
            return  body.getPlayRecordVoList();
        }catch(Exception e){
            log.error("调用第三方系统查询播放记录失败，userId={}, startTime={}, endTime={}", userId, startTime, endTime, e);
            return new ArrayList<>();
        }
    }
    public PlayRecordStatisticVo queryPlayRecordStatisticByPlayDate(int userId, String playDate){
        try {
            SinglePlayRecordStatisticVo body = prizeSenderClient.queryPlayRecordStatistic(userId, playDate);
            if(body==null||body.getBaseVo()==null||!body.getBaseVo().isSuccess()) {
                throw new QueryPlayRecordStatisticException("调用第三方系查询播放总时长记录失败");
            }
            return  body.getPlayRecordStatisticVo();
        }catch(Exception e){
            log.error("调用第三方系统查询播放总时长记录失败，userId={}, playDate={}", userId, playDate, e);
            return null;
        }
    }
    public List<PrizeRecordVo> queryPrizeRecordListByTime(int userId, String startTime,String endTime,int startIndex,int pageSize){
        try {
            MultiPrizeRecordVo body = prizeSenderClient.queryPrizeRecord(userId, startTime, endTime, startIndex, pageSize);
            if(body==null||body.getBaseVo()==null||!body.getBaseVo().isSuccess()) {
                throw new QueryPrizeRecordException("调用第三方系查询发奖记录失败");
            }
            return  body.getPrizeRecordVoList();
        }catch(Exception e){
            log.error("调用第三方系统查询发奖记录失败，userId={}, startTime={}, endTime={}", userId, startTime, endTime, e);
            return new ArrayList<>();
        }
    }
    public List<OrderRecordVo> queryOrderRecordListByTime(Date startTime,Date endTime,int startIndex,int pageSize){
        //奖品中心接口约定的格式是 yyyy-MM-dd'T'HH:mm:ss，且入参名是 statTime、size
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
        try {
            MultiOrderRecordVo body = prizeCenterClient.queryOrderRecord(formatter.format(startTime), formatter.format(endTime), startIndex, pageSize);
            if(body==null||body.getBaseVo()==null||!body.getBaseVo().isSuccess()) {
                throw new QueryOrderRecordException("调用第三方系查询奖品扣减记录失败");
            }
            return  body.getOrderRecordVoList();
        }catch(Exception e){
            log.error("调用第三方系统查询奖品扣减记录失败，startTime={}, endTime={}", startTime, endTime, e);
            return new ArrayList<>();
        }
    }
    public SystemConfigVo querySystemConfigVo(String code){
        try {
            SingleSystemConfigVo body = prizeSenderClient.querySystemConfigByCode(code);
            if(body==null||body.getBaseVo()==null||!body.getBaseVo().isSuccess()) {
                throw new QuerySystemConfigException("调用第三方系统查询系统配置失败");
            }
            return  body.getSystemConfigVo();
        }catch(Exception e){
            log.error("调用第三方系统查询系统配置失败，code={}", code, e);
            return null;
        }
    }
}