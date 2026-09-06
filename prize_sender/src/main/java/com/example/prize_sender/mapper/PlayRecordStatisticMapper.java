package com.example.prize_sender.mapper;

import com.example.prize_sender.entity.PlayRecordStatistic;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;
@Mapper
public interface PlayRecordStatisticMapper {
    // 新增
    void add(PlayRecordStatistic playRecordStatistic);

    // 删除
    void delete(int id);

    // 修改
    void modify(PlayRecordStatistic playRecordStatistic);

    // 查询全部
    List<PlayRecordStatistic> queryAll();

    // 根据id查询
    PlayRecordStatistic queryById(int id);

    PlayRecordStatistic queryByUserIdAndDate(int userId,String playDate);
}
