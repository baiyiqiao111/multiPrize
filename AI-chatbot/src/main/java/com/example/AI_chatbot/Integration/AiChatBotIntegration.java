package com.example.AI_chatbot.Integration;

import com.example.AI_chatbot.Integration.vo.*;
import com.example.AI_chatbot.exception.QueryOrderRecordException;
import com.example.AI_chatbot.exception.QueryPlayRecordException;
import com.example.AI_chatbot.exception.QueryPlayRecordStatisticException;
import com.example.AI_chatbot.exception.QueryPrizeRecordException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
@Slf4j
@Service
public class AiChatBotIntegration {
    private static final String PRIZE_SENDER = "http://107.22.136.67:8085";
    private static final String PRIZE_CENTER = "http://107.22.136.67:8082";

    @Autowired
    private RestTemplate restTemplate;
    public List<PlayRecordVo> queryPlayRecordListByTime(int userId, String startTime, String endTime){
        URI uri = UriComponentsBuilder.fromUriString(PRIZE_SENDER + "/playRecord/queryAll")
                .queryParam("userId", userId)
                .queryParam("startTime", startTime)
                .queryParam("endTime", endTime)
                .build()
                .encode()
                .toUri();
        HttpHeaders httpHeaders = new HttpHeaders();
        HttpEntity<Object> httpEntity = new HttpEntity<>(null, httpHeaders);
        try {
            ResponseEntity<MultiPlayRecordVo> response = restTemplate.exchange(uri, HttpMethod.GET,httpEntity, MultiPlayRecordVo.class);
            MultiPlayRecordVo body = response.getBody();
            if(body==null||body.getBaseVo()==null||!body.getBaseVo().isSuccess()) {
                throw new QueryPlayRecordException("调用第三方系查询播放记录失败");
            }
            return  body.getPlayRecordVoList();
        }catch(Exception e){
            log.error("调用第三方系统查询播放记录失败，url={}", uri, e);
            return new ArrayList<>();
        }
    }
    public PlayRecordStatisticVo queryPlayRecordStatisticByPlayDate(int userId, String playDate){
        URI uri = UriComponentsBuilder.fromUriString(PRIZE_SENDER + "/playRecordStatistic/queryByUserIdAndPlayDate")
                .queryParam("userId", userId)
                .queryParam("playDate", playDate)
                .build()
                .encode()
                .toUri();
        HttpHeaders httpHeaders = new HttpHeaders();
        HttpEntity<Object> httpEntity = new HttpEntity<>(null, httpHeaders);
        try {
            ResponseEntity<SinglePlayRecordStatisticVo> response = restTemplate.exchange(uri, HttpMethod.GET,httpEntity, SinglePlayRecordStatisticVo.class);
            SinglePlayRecordStatisticVo body = response.getBody();
            if(body==null||body.getBaseVo()==null||!body.getBaseVo().isSuccess()) {
                throw new QueryPlayRecordStatisticException("调用第三方系查询播放总时长记录失败");
            }
            return  body.getPlayRecordStatisticVo();
        }catch(Exception e){
            log.error("调用第三方系统查询播放总时长记录失败，url={}", uri, e);
            return null;
        }
    }
    public List<PrizeRecordVo> queryPrizeRecordListByTime(int userId, String startTime,String endTime,int startIndex,int pageSize){
        URI uri = UriComponentsBuilder.fromUriString(PRIZE_SENDER + "/prizeRecord/queryAll")
                .queryParam("userId", userId)
                .queryParam("startTime", startTime)
                .queryParam("endTime", endTime)
                .queryParam("startIndex", startIndex)
                .queryParam("pageSize", pageSize)
                .build()
                .encode()
                .toUri();
        HttpHeaders httpHeaders = new HttpHeaders();
        HttpEntity<Object> httpEntity = new HttpEntity<>(null, httpHeaders);
        try {
            ResponseEntity<MultiPrizeRecordVo> response = restTemplate.exchange(uri, HttpMethod.GET,httpEntity, MultiPrizeRecordVo.class);
            MultiPrizeRecordVo body = response.getBody();
            if(body==null||body.getBaseVo()==null||!body.getBaseVo().isSuccess()) {
                throw new QueryPrizeRecordException("调用第三方系查询发奖记录失败");
            }
            return  body.getPrizeRecordVoList();
        }catch(Exception e){
            log.error("调用第三方系统查询发奖记录失败，url={}", uri, e);
            return new ArrayList<>();
        }
    }
    public List<OrderRecordVo> queryOrderRecordListByTime(Date startTime,Date endTime,int startIndex,int pageSize){
        //奖品中心接口约定的格式是 yyyy-MM-dd'T'HH:mm:ss，且入参名是 statTime、size
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
        URI uri = UriComponentsBuilder.fromUriString(PRIZE_CENTER + "/orderRecord/queryAll")
                .queryParam("statTime", formatter.format(startTime))
                .queryParam("endTime", formatter.format(endTime))
                .queryParam("startIndex", startIndex)
                .queryParam("size", pageSize)
                .build()
                .encode()
                .toUri();
        HttpHeaders httpHeaders = new HttpHeaders();
        HttpEntity<Object> httpEntity = new HttpEntity<>(null, httpHeaders);
        try {
            ResponseEntity<MultiOrderRecordVo> response = restTemplate.exchange(uri, HttpMethod.GET,httpEntity, MultiOrderRecordVo.class);
            MultiOrderRecordVo body = response.getBody();
            if(body==null||body.getBaseVo()==null||!body.getBaseVo().isSuccess()) {
                throw new QueryOrderRecordException("调用第三方系查询奖品扣减记录失败");
            }
            return  body.getOrderRecordVoList();
        }catch(Exception e){
            log.error("调用第三方系统查询奖品扣减记录失败，url={}", uri, e);
            return new ArrayList<>();
        }
    }
}