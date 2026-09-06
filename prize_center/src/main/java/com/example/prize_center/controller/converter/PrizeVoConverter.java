package com.example.prize_center.controller.converter;

import com.example.prize_center.controller.vo.PrizeVo;
import com.example.prize_center.entity.Prize;

import java.util.ArrayList;
import java.util.List;

public class PrizeVoConverter {
    public static PrizeVo convertToVo(Prize prize) {
        if (prize == null) {
            return null;
        }

        PrizeVo prizeVo = new PrizeVo();

        prizeVo.setId(prize.getId());
        prizeVo.setCode(prize.getCode());
        prizeVo.setStock(prize.getStock());
        prizeVo.setIcon(prize.getIcon());
        prizeVo.setUpdateTime(prize.getUpdateTime());

        return prizeVo;
    }

    public static List<PrizeVo> convertToVoList(List<Prize> prizeList) {
        List<PrizeVo> prizeVoList = new ArrayList<>();

        if (prizeList == null) {
            return prizeVoList;
        }

        for (Prize prize : prizeList) {
            prizeVoList.add(convertToVo(prize));
        }

        return prizeVoList;
    }
}
