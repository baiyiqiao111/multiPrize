package com.example.prize_sender.mapper;

import com.example.prize_sender.entity.PrizeRecord;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.Date;
import java.util.List;

@Mapper
public interface PrizeRecordMapper {
    void add(PrizeRecord prizeRecord);

    void delete(int id);

    void modify(PrizeRecord prizeRecord);

    PrizeRecord queryById(int id);

    List<PrizeRecord> queryByTime(Date startTime, Date endTime);

    List<PrizeRecord> queryAll(@Param("userId") int userId,
                               @Param("startTime") Date startTime,
                               @Param("endTime") Date endTime,
                               @Param("startIndex") int startIndex,
                               @Param("pageSize") int pageSize);
}
