package com.example.AI_chatbot.Integration.client;

import com.example.AI_chatbot.Integration.vo.MultiOrderRecordVo;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * 奖品中心（prize-center）接口，name 为 Eureka 中的服务名，由 Feign 负载均衡到实际实例
 */
@FeignClient(name = "prize-center")
public interface PrizeCenterClient {

    /** 分页查询奖品扣减记录；奖品中心约定时间格式 yyyy-MM-dd'T'HH:mm:ss，入参名是 statTime、size */
    @GetMapping("/orderRecord/all")
    MultiOrderRecordVo queryOrderRecord(@RequestParam("statTime") String statTime,
                                        @RequestParam("endTime") String endTime,
                                        @RequestParam("startIndex") int startIndex,
                                        @RequestParam("size") int size);
}