package com.example.prize_sender.mapper;

import com.example.prize_sender.entity.SystemConfig;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface SystemConfigMapper {
    void add(SystemConfig systemConfig);
    void delete(int id);
    void modify(SystemConfig systemConfig);
    SystemConfig queryById(int id);
    SystemConfig queryByCode(String code);
    List<SystemConfig> queryAll(@Param("start") int start, @Param("size") int size);
}
