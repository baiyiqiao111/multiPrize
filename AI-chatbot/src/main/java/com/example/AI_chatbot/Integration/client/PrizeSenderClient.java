package com.example.AI_chatbot.Integration.client;

import com.example.AI_chatbot.Integration.vo.MultiPlayRecordVo;
import com.example.AI_chatbot.Integration.vo.MultiPrizeRecordVo;
import com.example.AI_chatbot.Integration.vo.SinglePlayRecordStatisticVo;
import com.example.AI_chatbot.controller.Vo.SingleSystemConfigVo;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * 发奖系统（prize-sender）接口，name 为 Eureka 中的服务名，由 Feign 负载均衡到实际实例
 */
@FeignClient(name = "prize-sender")
public interface PrizeSenderClient {

    /** 查询播放记录，时间格式 yyyy-MM-dd HH:mm:ss，为空表示不限 */
    @GetMapping("/playRecord/queryAll")
    MultiPlayRecordVo queryPlayRecord(@RequestParam("userId") int userId,
                                      @RequestParam(value = "startTime", required = false) String startTime,
                                      @RequestParam(value = "endTime", required = false) String endTime);

    /** 查询某天的播放总时长，playDate 格式 yyyy-MM-dd */
    @GetMapping("/playRecordStatistic/queryByUserIdAndPlayDate")
    SinglePlayRecordStatisticVo queryPlayRecordStatistic(@RequestParam("userId") int userId,
                                                         @RequestParam("playDate") String playDate);

    /** 分页查询发奖记录，时间格式 yyyy-MM-dd HH:mm:ss，为空表示不限 */
    @GetMapping("/prizeRecord/queryAll")
    MultiPrizeRecordVo queryPrizeRecord(@RequestParam("userId") int userId,
                                        @RequestParam(value = "startTime", required = false) String startTime,
                                        @RequestParam(value = "endTime", required = false) String endTime,
                                        @RequestParam("startIndex") int startIndex,
                                        @RequestParam("pageSize") int pageSize);

    @GetMapping("/system-config/code/{code}")
    SingleSystemConfigVo querySystemConfigByCode(@PathVariable("code") String code);
}