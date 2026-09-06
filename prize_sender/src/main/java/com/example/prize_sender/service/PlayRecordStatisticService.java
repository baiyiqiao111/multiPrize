package com.example.prize_sender.service;

import com.example.prize_sender.entity.PlayRecordStatistic;

import java.util.Date;
import java.util.List;

public interface PlayRecordStatisticService {
    /**
     * 新增播放统计记录
     */
    void add(PlayRecordStatistic playRecordStatistic);

    /**
     * 根据 id 删除播放统计记录
     */
    void delete(int id);

    /**
     * 修改播放统计记录
     */
    void modify(PlayRecordStatistic playRecordStatistic);

    /**
     * 查询全部播放统计记录
     */
    List<PlayRecordStatistic> queryAll();

    /**
     * 根据 id 查询播放统计记录
     */
    PlayRecordStatistic queryById(int id);

    PlayRecordStatistic queryByUserIdAndDate(int useId,String playDate);
}
