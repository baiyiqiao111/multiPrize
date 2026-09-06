package com.example.prize_center.controller;

import com.example.prize_center.controller.cmd.AddPrizeCmd;
import com.example.prize_center.controller.cmd.ModifyPrizeCmd;
import com.example.prize_center.controller.converter.PrizeVoConverter;
import com.example.prize_center.controller.vo.BaseVo;
import com.example.prize_center.controller.vo.MultiPrizeVo;
import com.example.prize_center.controller.vo.PrizeVo;
import com.example.prize_center.controller.vo.SinglePrizeVo;
import com.example.prize_center.entity.Prize;
import com.example.prize_center.service.PrizeService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/prize")
@Slf4j
public class
PrizeController {
    @Autowired
    private PrizeService prizeService;

    @PostMapping("/add")
    public BaseVo add(@RequestBody AddPrizeCmd addPrizeCmd) {
        long start = System.currentTimeMillis();
        long end;

        try {
            prizeService.add(addPrizeCmd);

            end = System.currentTimeMillis();
            return new BaseVo(200, end - start, true, null);

        } catch (IllegalArgumentException e) {
            log.error(e.getMessage());
            end = System.currentTimeMillis();
            return new BaseVo(500, end - start, false, e.getMessage());

        } catch (Exception e) {
            log.error(e.getMessage());
            end = System.currentTimeMillis();
            return new BaseVo(500, end - start, false, "其它位置异常");
        }
    }

    @DeleteMapping("/delete")
    public BaseVo delete(int id) {
        long start = System.currentTimeMillis();
        long end;

        try {
            prizeService.delete(id);

            end = System.currentTimeMillis();
            return new BaseVo(200, end - start, true, null);

        } catch (Exception e) {
            log.error(e.getMessage());
            end = System.currentTimeMillis();
            return new BaseVo(500, end - start, false, "其它位置异常");
        }
    }

    @PutMapping("/modify")
    public BaseVo modify(@RequestBody ModifyPrizeCmd modifyPrizeCmd) {
        long start = System.currentTimeMillis();
        long end;

        try {
            prizeService.modify(modifyPrizeCmd);

            end = System.currentTimeMillis();
            return new BaseVo(200, end - start, true, null);

        } catch (RuntimeException e) {
            log.error(e.getMessage());
            end = System.currentTimeMillis();
            return new BaseVo(500, end - start, false, e.getMessage());

        } catch (Exception e) {
            log.error(e.getMessage());
            end = System.currentTimeMillis();
            return new BaseVo(500, end - start, false, "其它位置异常");
        }
    }

    @GetMapping("/all")
    public MultiPrizeVo queryAll(int startIndex, int size) {
        long start = System.currentTimeMillis();
        long end;

        MultiPrizeVo multiPrizeVo = new MultiPrizeVo();

        try {
            List<Prize> prizeList = prizeService.queryAll(startIndex,size);
            List<PrizeVo> prizeVoList =
                    PrizeVoConverter.convertToVoList(prizeList);

            end = System.currentTimeMillis();
            BaseVo baseVo =
                    new BaseVo(200, end - start, true, null);

            multiPrizeVo.setPrizeVoList(prizeVoList);
            multiPrizeVo.setBaseVo(baseVo);

            return multiPrizeVo;

        } catch (Exception e) {
            log.error(e.getMessage());
            end = System.currentTimeMillis();

            BaseVo baseVo =
                    new BaseVo(500, end - start, false, "其它位置异常");

            multiPrizeVo.setBaseVo(baseVo);
            return multiPrizeVo;
        }
    }

    @GetMapping("/id")
    public SinglePrizeVo queryById(int id) {
        long start = System.currentTimeMillis();
        long end;

        SinglePrizeVo singlePrizeVo = new SinglePrizeVo();

        try {
            Prize prize = prizeService.queryById(id);

            if (prize == null) {
                end = System.currentTimeMillis();

                BaseVo baseVo =
                        new BaseVo(500, end - start, false, "奖品不存在");

                singlePrizeVo.setBaseVo(baseVo);
                return singlePrizeVo;
            }

            PrizeVo prizeVo = PrizeVoConverter.convertToVo(prize);

            end = System.currentTimeMillis();

            BaseVo baseVo =
                    new BaseVo(200, end - start, true, null);

            singlePrizeVo.setPrizeVo(prizeVo);
            singlePrizeVo.setBaseVo(baseVo);

            return singlePrizeVo;

        } catch (Exception e) {
            log.error(e.getMessage());
            end = System.currentTimeMillis();

            BaseVo baseVo =
                    new BaseVo(500, end - start, false, "其它位置异常");

            singlePrizeVo.setBaseVo(baseVo);
            return singlePrizeVo;
        }
    }
    @PutMapping("/decrease")
    public BaseVo decrease(String code, int amount, String outBizNo){
        long start = System.currentTimeMillis();
        long end;

        try {
            prizeService.decrease(code, amount,outBizNo);

            end = System.currentTimeMillis();
            return new BaseVo(200, end - start, true, null);

        }catch(DuplicateKeyException e) {
            end = System.currentTimeMillis();
            return new BaseVo(200, end - start, true, null);
        } catch (Exception e) {
            log.error(e.getMessage());
            end = System.currentTimeMillis();
            return new BaseVo(500, end - start, false, "其它位置异常");
        }
    }
}

