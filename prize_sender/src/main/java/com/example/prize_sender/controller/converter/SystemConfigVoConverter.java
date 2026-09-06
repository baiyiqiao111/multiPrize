package com.example.prize_sender.controller.converter;

import com.example.prize_sender.controller.Vo.PlayRecordVo;
import com.example.prize_sender.controller.Vo.SystemConfigVo;
import com.example.prize_sender.entity.PlayRecord;
import com.example.prize_sender.entity.SystemConfig;

import java.util.ArrayList;
import java.util.List;

public class SystemConfigVoConverter {
    public static SystemConfigVo convertToVo(SystemConfig systemConfig){

        if(systemConfig == null){
            return null;
        }

        SystemConfigVo systemConfigVo= new SystemConfigVo();

        systemConfigVo.setId(systemConfig.getId());
        systemConfigVo.setCode(systemConfig.getCode());
        systemConfigVo.setValue(systemConfig.getValue());


        return systemConfigVo;
    }

    public static List<SystemConfigVo> convertToVoList(List<SystemConfig> systemConfigList){

        List<SystemConfigVo> systemConfigVoList = new ArrayList<>();

        if(systemConfigList.isEmpty()){
            return systemConfigVoList;
        }

        for(SystemConfig systemConfig : systemConfigList){
            systemConfigVoList.add(convertToVo(systemConfig));
        }

        return systemConfigVoList;
    }
}
