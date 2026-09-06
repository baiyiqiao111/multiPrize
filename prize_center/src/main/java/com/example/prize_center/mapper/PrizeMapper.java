package com.example.prize_center.mapper;

import com.example.prize_center.entity.Prize;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;
@Mapper
public interface PrizeMapper {
    void add(Prize prize);

    void delete(int id);

    void modify(Prize prize);

    Prize queryById(int id);

    List<Prize> queryAll(int start, int size);

    Prize queryByCode(String code);

}
