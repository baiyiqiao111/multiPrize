package com.example.prize_sender.integration.client;

import com.example.prize_sender.controller.Vo.BaseVo;
import com.example.prize_sender.integration.vo.MultiOrderRecordVo;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * 奖品中心（prize-center）接口，name 为 Eureka 中的服务名，由 Feign 负载均衡到实际实例
 */
@FeignClient(name = "prize-center")
public interface PrizeCenterClient {

    /** 扣减奖品库存，outBizNo 用于幂等 */
    @PutMapping("/prize/decrease")
    BaseVo decrease(@RequestParam("code") String code,
                    @RequestParam("amount") int amount,
                    @RequestParam("outBizNo") String outBizNo);

    /** 按时间段查询奖品扣减记录，时间格式 yyyy-MM-dd'T'HH:mm:ss */
    @GetMapping("/orderRecord/queryByTime")
    MultiOrderRecordVo queryOrderRecordByTime(@RequestParam("startTime") String startTime,
                                              @RequestParam("endTime") String endTime);
}