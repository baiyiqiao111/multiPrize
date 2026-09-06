package com.example.prize_sender.controller;

import com.example.prize_sender.controller.Vo.BaseVo;
import com.example.prize_sender.controller.Vo.MultiPlayRecordVo;
import com.example.prize_sender.controller.Vo.PlayRecordVo;
import com.example.prize_sender.controller.Vo.SinglePlayRecordVo;
import com.example.prize_sender.controller.converter.PlayRecordVoConverter;
import com.example.prize_sender.entity.PlayRecord;
import com.example.prize_sender.service.PlayRecordService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.List;

@RestController
@RequestMapping("/playRecord")
@CrossOrigin
@Slf4j
public class PlayRecordController {

    @Autowired
    private PlayRecordService playRecordService;


    @DeleteMapping("delete")
    public BaseVo delete(int id){
        long start = System.currentTimeMillis();
        long end;

        try {
            playRecordService.delete(id);

            end = System.currentTimeMillis();
            return new BaseVo(200, end - start, true, null);

        } catch (Exception e) {
            log.error(e.getMessage());
            end = System.currentTimeMillis();
            return new BaseVo(500, end - start, false, "其它位置异常");
        }
    }

    @GetMapping("queryAll")
    public MultiPlayRecordVo queryAll(int userId,
                                      @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") Date startTime,
                                      @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") Date endTime){
        long start = System.currentTimeMillis();
        long end;
        MultiPlayRecordVo multiPlayRecordVo = new MultiPlayRecordVo();

        try {

            List<PlayRecord> playRecordList = playRecordService.queryAll(userId,startTime, endTime);
            List<PlayRecordVo> playRecordVoList = PlayRecordVoConverter.convertToVoList(playRecordList);

            end = System.currentTimeMillis();
            BaseVo baseVo = new BaseVo(200, end - start, true, null);

            multiPlayRecordVo.setPlayRecordVoList(playRecordVoList);
            multiPlayRecordVo.setBaseVo(baseVo);

            return multiPlayRecordVo;

        } catch (Exception e) {
            log.error(e.getMessage());
            end = System.currentTimeMillis();
            BaseVo baseVo = new BaseVo(500, end - start, false, "其它位置异常");
            multiPlayRecordVo.setBaseVo(baseVo);
            return multiPlayRecordVo;
        }
    }

    @GetMapping("/{id}")
    public SinglePlayRecordVo queryById(@PathVariable int id){
        long start = System.currentTimeMillis();
        long end;
        SinglePlayRecordVo singlePlayRecordVo = new SinglePlayRecordVo();

        try {
            PlayRecord playRecord = playRecordService.queryById(id);
            PlayRecordVo playRecordVo = PlayRecordVoConverter.convertToVo(playRecord);
            end = System.currentTimeMillis();
            BaseVo baseVo = new BaseVo(500, end - start, false, "音乐不存在");
            singlePlayRecordVo.setBaseVo(baseVo);
            singlePlayRecordVo.setPlayRecordVo(playRecordVo);
            return singlePlayRecordVo;

        } catch (Exception e) {
            log.error(e.getMessage());
            end = System.currentTimeMillis();
            BaseVo baseVo = new BaseVo(500, end - start, false, "其它位置异常");
            singlePlayRecordVo.setBaseVo(baseVo);
            return singlePlayRecordVo;
        }
    }
}