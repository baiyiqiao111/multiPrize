package com.example.prize_sender.service.impl;

import com.example.prize_sender.controller.cmd.AddSystemConfigCmd;
import com.example.prize_sender.controller.cmd.UpdateSystemConfigCmd;
import com.example.prize_sender.entity.SystemConfig;
import com.example.prize_sender.exception.SystemConfigNotExistException;
import com.example.prize_sender.mapper.SystemConfigMapper;
import com.example.prize_sender.service.SystemConfigService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SystemConfigServiceImpl implements SystemConfigService {
    @Autowired
    private SystemConfigMapper systemConfigMapper;

    @Override
    public void add(AddSystemConfigCmd addSystemConfigCmd) {
        SystemConfig systemConfig = new SystemConfig();
        systemConfig.setCode(addSystemConfigCmd.getCode());
        systemConfig.setValue(addSystemConfigCmd.getValue());
        systemConfigMapper.add(systemConfig);
    }

    @Override
    public void delete(int id) {
        systemConfigMapper.delete(id);
    }

    @Override
    public void modify(UpdateSystemConfigCmd updateSystemConfigCmd) {
        SystemConfig systemConfig = systemConfigMapper.queryById(updateSystemConfigCmd.getId());
        if(systemConfig==null){
            throw new SystemConfigNotExistException("系统不存在");
        }
        systemConfig.setValue(updateSystemConfigCmd.getValue());
        systemConfig.setCode(updateSystemConfigCmd.getCode());
        systemConfigMapper.modify(systemConfig);
    }

    @Override
    public SystemConfig queryById(int id) {
        return systemConfigMapper.queryById(id);
    }

    @Override
    public SystemConfig queryByCode(String code) {
        return systemConfigMapper.queryByCode(code);
    }

    @Override
    public List<SystemConfig> queryAll(int start, int size) {
        return systemConfigMapper.queryAll(start,size);
    }
}
