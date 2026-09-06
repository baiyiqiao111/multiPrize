package com.example.prize_center.controller;

import com.example.prize_center.controller.cmd.AddOrderRecordCmd;
import com.example.prize_center.controller.cmd.ModifyOrderRecordCmd;
import com.example.prize_center.controller.converter.OrderRecordVoConverter;
import com.example.prize_center.controller.vo.BaseVo;
import com.example.prize_center.controller.vo.MultiOrderRecordVo;
import com.example.prize_center.controller.vo.OrderRecordVo;
import com.example.prize_center.controller.vo.SingleOrderRecordVo;
import com.example.prize_center.entity.OrderRecord;
import com.example.prize_center.service.OrderRecordService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.List;


@RestController
@RequestMapping("/orderRecord")
@Slf4j
public class OrderRecordController {

    @Autowired
    private OrderRecordService orderRecordService;

    @DeleteMapping("/delete")
    public BaseVo delete(int id) {

        long start = System.currentTimeMillis();
        long end;

        try {
            orderRecordService.delete(id);

            end = System.currentTimeMillis();
            return new BaseVo(200, end - start, true, null);

        } catch (IllegalArgumentException e) {
            log.error(e.getMessage());

            end = System.currentTimeMillis();
            return new BaseVo(
                    500,
                    end - start,
                    false,
                    e.getMessage()
            );

        } catch (Exception e) {
            log.error(e.getMessage());

            end = System.currentTimeMillis();
            return new BaseVo(
                    500,
                    end - start,
                    false,
                    "其它位置异常"
            );
        }
    }


    @GetMapping("/all")
    public MultiOrderRecordVo queryAll(@DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss") Date statTime, @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss") Date endTime, int startIndex, int size) {

        long start = System.currentTimeMillis();
        long end;

        MultiOrderRecordVo multiOrderRecordVo =
                new MultiOrderRecordVo();

        try {
            List<OrderRecord> orderRecordList =
                    orderRecordService.queryAll(statTime,endTime,startIndex,size);

            List<OrderRecordVo> orderRecordVoList =
                    OrderRecordVoConverter.convertToVoList(
                            orderRecordList
                    );

            end = System.currentTimeMillis();

            BaseVo baseVo =
                    new BaseVo(200, end - start, true, null);

            multiOrderRecordVo.setOrderRecordVoList(
                    orderRecordVoList
            );
            multiOrderRecordVo.setBaseVo(baseVo);

            return multiOrderRecordVo;

        } catch (Exception e) {
            log.error(e.getMessage());

            end = System.currentTimeMillis();

            BaseVo baseVo =
                    new BaseVo(
                            500,
                            end - start,
                            false,
                            "其它位置异常"
                    );

            multiOrderRecordVo.setBaseVo(baseVo);
            return multiOrderRecordVo;
        }
    }
    @GetMapping("/queryByTime")
    public MultiOrderRecordVo queryByTime(@DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss") Date startTime, @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss") Date endTime) {

        long start = System.currentTimeMillis();
        long end;

        MultiOrderRecordVo multiOrderRecordVo =
                new MultiOrderRecordVo();

        try {
            List<OrderRecord> orderRecordList =
                    orderRecordService.queryByTime(startTime,endTime);

            List<OrderRecordVo> orderRecordVoList =
                    OrderRecordVoConverter.convertToVoList(
                            orderRecordList
                    );

            end = System.currentTimeMillis();

            BaseVo baseVo =
                    new BaseVo(200, end - start, true, null);

            multiOrderRecordVo.setOrderRecordVoList(
                    orderRecordVoList
            );
            multiOrderRecordVo.setBaseVo(baseVo);

            return multiOrderRecordVo;

        } catch (Exception e) {
            log.error(e.getMessage());

            end = System.currentTimeMillis();

            BaseVo baseVo =
                    new BaseVo(
                            500,
                            end - start,
                            false,
                            "其它位置异常"
                    );

            multiOrderRecordVo.setBaseVo(baseVo);
            return multiOrderRecordVo;
        }
    }

    @GetMapping("/id")
    public SingleOrderRecordVo queryById(int id) {

        long start = System.currentTimeMillis();
        long end;

        SingleOrderRecordVo singleOrderRecordVo =
                new SingleOrderRecordVo();

        try {
            OrderRecord orderRecord =
                    orderRecordService.queryById(id);

            if (orderRecord == null) {
                end = System.currentTimeMillis();

                BaseVo baseVo =
                        new BaseVo(
                                500,
                                end - start,
                                false,
                                "订单记录不存在"
                        );

                singleOrderRecordVo.setBaseVo(baseVo);
                return singleOrderRecordVo;
            }

            OrderRecordVo orderRecordVo =
                    OrderRecordVoConverter.convertToVo(orderRecord);

            end = System.currentTimeMillis();

            BaseVo baseVo =
                    new BaseVo(200, end - start, true, null);

            singleOrderRecordVo.setOrderRecordVo(orderRecordVo);
            singleOrderRecordVo.setBaseVo(baseVo);

            return singleOrderRecordVo;

        } catch (Exception e) {
            log.error(e.getMessage());

            end = System.currentTimeMillis();

            BaseVo baseVo =
                    new BaseVo(
                            500,
                            end - start,
                            false,
                            "其它位置异常"
                    );

            singleOrderRecordVo.setBaseVo(baseVo);
            return singleOrderRecordVo;
        }
    }

    @GetMapping("/outBizNo")
    public SingleOrderRecordVo queryByOutBizNo(String outBizNo) {

        long start = System.currentTimeMillis();
        long end;

        SingleOrderRecordVo singleOrderRecordVo =
                new SingleOrderRecordVo();

        try {
            OrderRecord orderRecord =
                    orderRecordService.queryByOutBizNo(outBizNo);

            if (orderRecord == null) {
                end = System.currentTimeMillis();

                BaseVo baseVo =
                        new BaseVo(
                                500,
                                end - start,
                                false,
                                "订单记录不存在"
                        );

                singleOrderRecordVo.setBaseVo(baseVo);
                return singleOrderRecordVo;
            }

            OrderRecordVo orderRecordVo =
                    OrderRecordVoConverter.convertToVo(orderRecord);

            end = System.currentTimeMillis();

            BaseVo baseVo =
                    new BaseVo(200, end - start, true, null);

            singleOrderRecordVo.setOrderRecordVo(orderRecordVo);
            singleOrderRecordVo.setBaseVo(baseVo);

            return singleOrderRecordVo;

        } catch (Exception e) {
            log.error(e.getMessage());

            end = System.currentTimeMillis();

            BaseVo baseVo =
                    new BaseVo(
                            500,
                            end - start,
                            false,
                            "其它位置异常"
                    );

            singleOrderRecordVo.setBaseVo(baseVo);
            return singleOrderRecordVo;
        }
    }

}