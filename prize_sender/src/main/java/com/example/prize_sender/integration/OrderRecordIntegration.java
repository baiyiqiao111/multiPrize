package com.example.prize_sender.integration;

import com.example.prize_sender.controller.Vo.BaseVo;
import com.example.prize_sender.exception.SendPrizeFailException;
import com.example.prize_sender.integration.vo.MultiOrderRecordVo;
import com.example.prize_sender.integration.vo.OrderRecordVo;
import com.example.prize_sender.util.DateTimeUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Slf4j
@Service
public class OrderRecordIntegration {
    @Autowired
    private RestTemplate restTemplate;
    public List<OrderRecordVo> queryOrderRecordListByTime(Date startTime, Date endTime){
        String url=String.format("http://107.22.136.67:8082/orderRecord/queryByTime?startTime=%s&endTime=%s", DateTimeUtil.getDateStr(startTime,"yyyy-MM-dd'T'HH:mm:ss"),DateTimeUtil.getDateStr(endTime,"yyyy-MM-dd'T'HH:mm:ss"));
        HttpHeaders httpHeaders = new HttpHeaders();
        HttpEntity<Object> httpEntity = new HttpEntity<>(null, httpHeaders);
        try {
            ResponseEntity<MultiOrderRecordVo> response = restTemplate.exchange(url, HttpMethod.GET,httpEntity, MultiOrderRecordVo.class);
            MultiOrderRecordVo body = response.getBody();
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
