package com.example.prize_sender.schedule;

import com.example.prize_sender.entity.PrizeRecord;
import com.example.prize_sender.integration.OrderRecordIntegration;
import com.example.prize_sender.integration.vo.OrderRecordVo;
import com.example.prize_sender.mapper.PrizeRecordMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Component
public class CheckSchedule {
    @Autowired
    private PrizeRecordMapper prizeRecordMapper;
    @Autowired
    private OrderRecordIntegration orderRecordIntegration;
    @Scheduled(cron = "0 */1 * * * ?")
    public void scheduledPrint(){
        Date startTime = new Date(System.currentTimeMillis() - 60000L);
        Date endTime = new Date();
        log.info("开始检查从{}到{}的发奖记录",startTime,endTime);
        boolean isNormal = true;
        List<PrizeRecord> prizeRecordList = prizeRecordMapper.queryByTime(startTime,endTime);
        List<OrderRecordVo> orderRecordVos = orderRecordIntegration.queryOrderRecordListByTime(startTime, endTime);
        if(prizeRecordList.size()!=orderRecordVos.size()){
            log.error("近一分钟内发奖流水不一致,发奖方条数为{}，提供方条数为{}",prizeRecordList.size(),orderRecordVos.size());
            isNormal = false;
        }
        Map<String, Integer> prizeRecordMap = match1(prizeRecordList);
        Map<String, Integer> orderRecordMap = match2(orderRecordVos);
        for(String key:prizeRecordMap.keySet()){
            if(prizeRecordMap.get(key)!=orderRecordMap.get(key)){
                log.error("指定outBizNo:{}下，发奖记录存在异常，发奖方的数量是{},提供方的数量是{}",key,prizeRecordMap.get(key),orderRecordMap.get(key));
                isNormal = false;
            }
        }
        if(isNormal){
            log.info("无异常");
        }
    }
    private Map<String,Integer> match1(List<PrizeRecord> prizeRecordList){
        Map<String,Integer> map = new HashMap<>();
        for(int i=0;i<prizeRecordList.size();i++){
            map.put(prizeRecordList.get(i).getOutBizNo(),prizeRecordList.get(i).getAmount());
        }
        return map;
    }
    private Map<String,Integer> match2(List<OrderRecordVo> orderRecordVos){
        Map<String,Integer> map = new HashMap<>();
        for(int i=0;i<orderRecordVos.size();i++){
            map.put(orderRecordVos.get(i).getOutBizNo(),orderRecordVos.get(i).getAmount());
        }
        return map;
    }

}
