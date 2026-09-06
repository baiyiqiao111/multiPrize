package com.example.prize_sender.mapper;

import com.example.prize_sender.entity.PlayRecord;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.Date;
import java.util.List;
@Mapper
public interface PlayRecordMapper {
    void add(PlayRecord playRecord);
    void delete(int id);
    void modify(PlayRecord playRecord);
    List<PlayRecord> queryAll(@Param("userId") int userId,
                              @Param("startTime") Date startTime,
                              @Param("endTime") Date endTime);
    PlayRecord queryById(int id);
}
