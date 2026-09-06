package com.example.prize_sender.service;

import com.example.prize_sender.controller.cmd.AddSystemConfigCmd;
import com.example.prize_sender.controller.cmd.UpdateSystemConfigCmd;
import com.example.prize_sender.entity.SystemConfig;

import java.util.List;

public interface SystemConfigService {
    void add(AddSystemConfigCmd addSystemConfigCmd);
    void delete(int id);
    void modify(UpdateSystemConfigCmd updateSystemConfigCmd);
    SystemConfig queryById(int id);
    SystemConfig queryByCode(String code);
    List<SystemConfig> queryAll(int start, int size);
}
