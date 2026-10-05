package com.example.prize_sender.integration;

import com.example.prize_sender.controller.Vo.BaseVo;
import com.example.prize_sender.exception.SendPrizeFailException;
import com.example.prize_sender.integration.client.PrizeCenterClient;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class CoinSenderIntegration {
    @Autowired
    private PrizeCenterClient prizeCenterClient;
    public void  send(String code, int amount, String outBizNo){
        try {
            BaseVo body = prizeCenterClient.decrease(code, amount, outBizNo);
            if(body==null||!body.isSuccess()) {
                throw new SendPrizeFailException("调用第三方系统发放奖励失败");
            }
        }catch(Exception e){
            log.error("调用第三方系统发放奖励失败");
        }
    }
}