package com.example.prize_sender.integration;

import com.example.prize_sender.controller.Vo.BaseVo;
import com.example.prize_sender.exception.SendPrizeFailException;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.producer.internals.Sender;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Slf4j
@Service
public class CoinSenderIntegration {
    @Autowired
    private RestTemplate restTemplate;
    public void  send(String code, int amount, String outBizNo){
        String url=String.format("http://107.22.136.67:8082/prize/decrease?code=%s&amount=%d&outBizNo=%s",code,amount,outBizNo);
        HttpHeaders httpHeaders = new HttpHeaders();
        HttpEntity<Object> httpEntity = new HttpEntity<>(null, httpHeaders);
        try {
            ResponseEntity<BaseVo> response = restTemplate.exchange(url, HttpMethod.PUT,httpEntity,BaseVo.class);
            BaseVo body = response.getBody();
            if(body==null||!body.isSuccess()) {
                throw new SendPrizeFailException("调用第三方系统发放奖励失败");
            }
        }catch(Exception e){
            log.error("调用第三方系统发放奖励失败");
        }
    }
}
