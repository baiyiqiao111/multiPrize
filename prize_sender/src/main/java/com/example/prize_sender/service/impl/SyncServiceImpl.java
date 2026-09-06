package com.example.prize_sender.service.impl;

import com.example.prize_sender.constant.DemoConstant;
import com.example.prize_sender.controller.SyncController;
import com.example.prize_sender.controller.cmd.SyncCmd;
import com.example.prize_sender.entity.PlayRecord;
import com.example.prize_sender.entity.PlayRecordStatistic;
import com.example.prize_sender.entity.PrizeRecord;
import com.example.prize_sender.entity.SystemConfig;
import com.example.prize_sender.integration.CoinSenderIntegration;
import com.example.prize_sender.mapper.PrizeRecordMapper;
import com.example.prize_sender.service.PlayRecordService;
import com.example.prize_sender.service.PlayRecordStatisticService;
import com.example.prize_sender.service.SyncService;
import com.example.prize_sender.service.SystemConfigService;
import com.example.prize_sender.util.DateTimeUtil;
import com.example.prize_sender.util.RuleUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Calendar;
import java.util.Date;
import java.util.List;
@Service
@Slf4j
public class SyncServiceImpl implements SyncService {
    @Autowired
    private PlayRecordService playRecordService;
    @Autowired
    private PlayRecordStatisticService playRecordStatisticService;
    @Autowired
    private SystemConfigService systemConfigService;
    @Autowired
    private CoinSenderIntegration coinSenderIntegration;
    @Autowired
    private PrizeRecordMapper prizeRecordMapper;
    @Override
    public void sync(SyncCmd syncCmd) {
        PlayRecord playRecord = new PlayRecord();
        playRecord.setMusicId(syncCmd.getMusicId());
        playRecord.setUserId(syncCmd.getUserId());
        playRecord.setDuration(syncCmd.getDuration());
        playRecordService.add(playRecord);

        Date start = DateTimeUtil.getStartOfDay();
        Date end = DateTimeUtil.getEndOfDay();
        List<PlayRecord> playRecords = playRecordService.queryAll(syncCmd.getUserId(), start, end);

        int totalDuration = 0;
        for(int i=0; i<playRecords.size();i++){
            totalDuration = totalDuration+playRecords.get(i).getDuration();
        }

        String dateStr = DateTimeUtil.getDateStr(new Date(),"yyyy-MM-dd");
        PlayRecordStatistic recordStatistic = playRecordStatisticService.queryByUserIdAndDate(syncCmd.getUserId(), dateStr);
        if(recordStatistic==null){
            PlayRecordStatistic playRecordStatistic = new PlayRecordStatistic();
            playRecordStatistic.setUserId(syncCmd.getUserId());
            playRecordStatistic.setPlayDate(dateStr);
            playRecordStatistic.setTotalDuration(totalDuration);
            playRecordStatisticService.add(playRecordStatistic);
        }else{
            recordStatistic.setTotalDuration(totalDuration);
            playRecordStatisticService.modify(recordStatistic);
        }
        String amountRule = systemConfigService.queryByCode(DemoConstant.PRIZE_AMOUNT_RULE).getValue();
        String stageRule = systemConfigService.queryByCode(DemoConstant.PRIZE_STAGE_RULE).getValue();
        int stage = RuleUtil.calculatePrizeStage(stageRule,totalDuration);
        int amount = RuleUtil.calculatePrizeAmount(amountRule,totalDuration);
        log.info("stage={}",stage);
        log.info("amount={}",amount);
        if(stage==0||amount==0){
            return;
        }
        try {
            sendPrize(stage, syncCmd, amount, dateStr);
        }catch(DuplicateKeyException e){
            log.info("此阶段已发奖无需重复发放,stage={}",stage);
        }

    }
    @Transactional
    public void sendPrize(int stage, SyncCmd syncCmd,int amount,String dateStr) {
        String outBizNo = syncCmd.getScene()+"_"+syncCmd.getUserId()+"_"+syncCmd.getPrizeCode()+"_"+dateStr+"_"+stage;
        log.info("outBizNo={}",outBizNo);
        coinSenderIntegration.send(syncCmd.getPrizeCode(),amount,outBizNo);
        PrizeRecord prizeRecord = new PrizeRecord();
        prizeRecord.setOutBizNo(outBizNo);
        prizeRecord.setStage(stage);
        prizeRecord.setUserId(syncCmd.getUserId());
        prizeRecord.setCode(syncCmd.getPrizeCode());
        prizeRecord.setScene(syncCmd.getScene());
        prizeRecord.setAmount(amount);
        prizeRecord.setDateStr(dateStr);
        prizeRecordMapper.add(prizeRecord);
    }
}
