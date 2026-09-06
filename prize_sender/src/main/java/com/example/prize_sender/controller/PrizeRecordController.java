package com.example.prize_sender.controller;

import com.example.prize_sender.controller.Vo.BaseVo;
import com.example.prize_sender.controller.Vo.MultiPrizeRecordVo;
import com.example.prize_sender.controller.Vo.PrizeRecordVo;
import com.example.prize_sender.controller.Vo.SinglePrizeRecordVo;
import com.example.prize_sender.controller.converter.PrizeRecordVoConverter;
import com.example.prize_sender.entity.PrizeRecord;
import com.example.prize_sender.service.PrizeRecordService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.List;

@RestController
@RequestMapping("/prizeRecord")
@CrossOrigin
@Slf4j
public class PrizeRecordController {
    @Autowired
    private PrizeRecordService prizeRecordService;

    @DeleteMapping("/delete")
    public BaseVo delete(int id) {
        long start = System.currentTimeMillis();
        long end;

        try {
            prizeRecordService.delete(id);

            end = System.currentTimeMillis();
            return new BaseVo(200, end - start, true, null);

        } catch (Exception e) {
            log.error(e.getMessage());
            end = System.currentTimeMillis();
            return new BaseVo(500, end - start, false, "其它位置异常");
        }
    }

    @GetMapping("/queryAll")
    public MultiPrizeRecordVo queryAll(
            int userId,
            @RequestParam(required = false)
            @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
            Date startTime,
            @RequestParam(required = false)
            @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
            Date endTime,
            int startIndex,
            int pageSize) {

        long start = System.currentTimeMillis();
        long end;

        MultiPrizeRecordVo multiPrizeRecordVo = new MultiPrizeRecordVo();

        try {
            List<PrizeRecord> prizeRecordList =
                    prizeRecordService.queryAll(
                            userId,
                            startTime,
                            endTime,
                            startIndex,
                            pageSize
                    );

            List<PrizeRecordVo> prizeRecordVoList =
                    PrizeRecordVoConverter.convertToVoList(prizeRecordList);

            end = System.currentTimeMillis();

            BaseVo baseVo = new BaseVo(
                    200,
                    end - start,
                    true,
                    null
            );

            multiPrizeRecordVo.setPrizeRecordVoList(prizeRecordVoList);
            multiPrizeRecordVo.setBaseVo(baseVo);

            return multiPrizeRecordVo;

        } catch (Exception e) {
            log.error(e.getMessage());
            end = System.currentTimeMillis();

            BaseVo baseVo = new BaseVo(
                    500,
                    end - start,
                    false,
                    "其它位置异常"
            );

            multiPrizeRecordVo.setBaseVo(baseVo);
            return multiPrizeRecordVo;
        }
    }

    @GetMapping("/{id}")
    public SinglePrizeRecordVo queryById(@PathVariable int id) {
        long start = System.currentTimeMillis();
        long end;

        SinglePrizeRecordVo singlePrizeRecordVo =
                new SinglePrizeRecordVo();

        try {
            PrizeRecord prizeRecord =
                    prizeRecordService.queryById(id);

            end = System.currentTimeMillis();

            if (prizeRecord == null) {
                BaseVo baseVo = new BaseVo(
                        404,
                        end - start,
                        false,
                        "发奖记录不存在"
                );

                singlePrizeRecordVo.setBaseVo(baseVo);
                return singlePrizeRecordVo;
            }

            PrizeRecordVo prizeRecordVo =
                    PrizeRecordVoConverter.convertToVo(prizeRecord);

            BaseVo baseVo = new BaseVo(
                    200,
                    end - start,
                    true,
                    null
            );

            singlePrizeRecordVo.setPrizeRecordVo(prizeRecordVo);
            singlePrizeRecordVo.setBaseVo(baseVo);

            return singlePrizeRecordVo;

        } catch (Exception e) {
            log.error(e.getMessage());
            end = System.currentTimeMillis();

            BaseVo baseVo = new BaseVo(
                    500,
                    end - start,
                    false,
                    "其它位置异常"
            );

            singlePrizeRecordVo.setBaseVo(baseVo);
            return singlePrizeRecordVo;
        }
    }
}
