package com.example.prize_sender.controller;

import com.example.prize_sender.controller.Vo.BaseVo;
import com.example.prize_sender.controller.Vo.MultiPlayRecordStatisticVo;
import com.example.prize_sender.controller.Vo.PlayRecordStatisticVo;
import com.example.prize_sender.controller.Vo.SinglePlayRecordStatisticVo;
import com.example.prize_sender.controller.converter.PlayRecordStatisticVoConverter;
import com.example.prize_sender.entity.PlayRecordStatistic;
import com.example.prize_sender.service.PlayRecordStatisticService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController
@RequestMapping("/playRecordStatistic")
@Slf4j
public class PlayRecordStatisticController {
    @Autowired
    private PlayRecordStatisticService playRecordStatisticService;

    /**
     * 根据 ID 删除播放统计记录
     */
    @DeleteMapping("/delete")
    public BaseVo delete(int id) {

        long start = System.currentTimeMillis();
        long end;

        try {
            playRecordStatisticService.delete(id);

            end = System.currentTimeMillis();
            return new BaseVo(
                    200,
                    end - start,
                    true,
                    null
            );

        } catch (Exception e) {
            log.error("删除播放统计记录失败，id={}", id, e);

            end = System.currentTimeMillis();
            return new BaseVo(
                    500,
                    end - start,
                    false,
                    "其它位置异常"
            );
        }
    }

    /**
     * 查询全部播放统计记录
     */
    @GetMapping("queryAll")
    public MultiPlayRecordStatisticVo queryAll() {

        long start = System.currentTimeMillis();
        long end;

        MultiPlayRecordStatisticVo multiPlayRecordStatisticVo =
                new MultiPlayRecordStatisticVo();

        try {
            List<PlayRecordStatistic> playRecordStatisticList =
                    playRecordStatisticService.queryAll();

            List<PlayRecordStatisticVo> playRecordStatisticVoList =
                    PlayRecordStatisticVoConverter.convertToVoList(
                            playRecordStatisticList
                    );

            end = System.currentTimeMillis();

            BaseVo baseVo = new BaseVo(
                    200,
                    end - start,
                    true,
                    null
            );

            multiPlayRecordStatisticVo.setPlayRecordStatisticVoList(
                    playRecordStatisticVoList
            );
            multiPlayRecordStatisticVo.setBaseVo(baseVo);

            return multiPlayRecordStatisticVo;

        } catch (Exception e) {
            log.error("查询全部播放统计记录失败", e);

            end = System.currentTimeMillis();

            BaseVo baseVo = new BaseVo(
                    500,
                    end - start,
                    false,
                    "其它位置异常"
            );

            multiPlayRecordStatisticVo.setBaseVo(baseVo);
            return multiPlayRecordStatisticVo;
        }
    }

    /**
     * 根据 ID 查询播放统计记录
     */
    @GetMapping("/{id}")
    public SinglePlayRecordStatisticVo queryById(int id) {

        long start = System.currentTimeMillis();
        long end;

        SinglePlayRecordStatisticVo singlePlayRecordStatisticVo =
                new SinglePlayRecordStatisticVo();

        try {
            PlayRecordStatistic playRecordStatistic = playRecordStatisticService.queryById(id);

            end = System.currentTimeMillis();

            if (playRecordStatistic == null) {
                BaseVo baseVo = new BaseVo(
                        404,
                        end - start,
                        false,
                        "播放统计记录不存在"
                );

                singlePlayRecordStatisticVo.setBaseVo(baseVo);
                return singlePlayRecordStatisticVo;
            }

            PlayRecordStatisticVo playRecordStatisticVo =
                    PlayRecordStatisticVoConverter.convertToVo(
                            playRecordStatistic
                    );

            BaseVo baseVo = new BaseVo(
                    200,
                    end - start,
                    true,
                    null
            );

            singlePlayRecordStatisticVo.setPlayRecordStatisticVo(
                    playRecordStatisticVo
            );
            singlePlayRecordStatisticVo.setBaseVo(baseVo);

            return singlePlayRecordStatisticVo;

        } catch (Exception e) {
            log.error("根据 ID 查询播放统计记录失败，id={}", id, e);

            end = System.currentTimeMillis();

            BaseVo baseVo = new BaseVo(
                    500,
                    end - start,
                    false,
                    "其它位置异常"
            );

            singlePlayRecordStatisticVo.setBaseVo(baseVo);
            return singlePlayRecordStatisticVo;
        }
    }
    @GetMapping("/queryByUserIdAndPlayDate")
    public SinglePlayRecordStatisticVo queryByUserIdAndPlayDate(int userId,String playDate) {

        long start = System.currentTimeMillis();
        long end;

        SinglePlayRecordStatisticVo singlePlayRecordStatisticVo =
                new SinglePlayRecordStatisticVo();

        try {
            PlayRecordStatistic playRecordStatistic = playRecordStatisticService.queryByUserIdAndDate(userId,playDate);

            end = System.currentTimeMillis();

            if (playRecordStatistic == null) {
                BaseVo baseVo = new BaseVo(
                        404,
                        end - start,
                        false,
                        "播放统计记录不存在"
                );

                singlePlayRecordStatisticVo.setBaseVo(baseVo);
                return singlePlayRecordStatisticVo;
            }

            PlayRecordStatisticVo playRecordStatisticVo =
                    PlayRecordStatisticVoConverter.convertToVo(
                            playRecordStatistic
                    );

            BaseVo baseVo = new BaseVo(
                    200,
                    end - start,
                    true,
                    null
            );

            singlePlayRecordStatisticVo.setPlayRecordStatisticVo(
                    playRecordStatisticVo
            );
            singlePlayRecordStatisticVo.setBaseVo(baseVo);

            return singlePlayRecordStatisticVo;

        } catch (Exception e) {
            log.error("根据 ID 查询播放统计记录失败，userId={},playDate={}", userId,playDate, e);

            end = System.currentTimeMillis();

            BaseVo baseVo = new BaseVo(
                    500,
                    end - start,
                    false,
                    "其它位置异常"
            );

            singlePlayRecordStatisticVo.setBaseVo(baseVo);
            return singlePlayRecordStatisticVo;
        }
    }
}
