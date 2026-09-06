package com.example.prize_sender.controller;

import com.example.prize_sender.controller.Vo.*;
import com.example.prize_sender.controller.cmd.AddSystemConfigCmd;
import com.example.prize_sender.controller.cmd.UpdateSystemConfigCmd;
import com.example.prize_sender.controller.converter.SystemConfigVoConverter;
import com.example.prize_sender.entity.SystemConfig;
import com.example.prize_sender.service.SystemConfigService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/system-config")
@CrossOrigin
@Slf4j
public class SystemConfigController {
    @Autowired
    private SystemConfigService systemConfigService;

    @PostMapping("/add")
    public BaseVo add(AddSystemConfigCmd addSystemConfigCmd){
        long start = System.currentTimeMillis();
        long end;

        try {
            systemConfigService.add(addSystemConfigCmd);

            end = System.currentTimeMillis();
            return new BaseVo(200, end - start, true, null);

        } catch (Exception e) {
            log.error(e.getMessage());
            end = System.currentTimeMillis();
            return new BaseVo(500, end - start, false, "其它位置异常");
        }
    }

    @PutMapping("/modify")
    public BaseVo modify(UpdateSystemConfigCmd updateSystemConfigCmd){
        long start = System.currentTimeMillis();
        long end;

        try {
            systemConfigService.modify(updateSystemConfigCmd);

            end = System.currentTimeMillis();
            return new BaseVo(200, end - start, true, null);

        } catch (Exception e) {
            log.error(e.getMessage());
            end = System.currentTimeMillis();
            return new BaseVo(500, end - start, false, "其它位置异常");
        }
    }

    @DeleteMapping("/delete")
    public BaseVo delete(int id){
        long start = System.currentTimeMillis();
        long end;

        try {
            systemConfigService.delete(id);

            end = System.currentTimeMillis();
            return new BaseVo(200, end - start, true, null);

        } catch (Exception e) {
            log.error(e.getMessage());
            end = System.currentTimeMillis();
            return new BaseVo(500, end - start, false, "其它位置异常");
        }
    }

    @GetMapping("/id/{id}")
    public SingleSystemConfigVo queryById(@PathVariable int id){
        long start = System.currentTimeMillis();
        long end;
        SingleSystemConfigVo singleSystemConfigVo = new SingleSystemConfigVo();

        try {
            SystemConfig systemConfig = systemConfigService.queryById(id);
            SystemConfigVo systemConfigVo = SystemConfigVoConverter.convertToVo(systemConfig);
            end = System.currentTimeMillis();
            BaseVo baseVo = new BaseVo(200, end - start, true, null);
            singleSystemConfigVo.setBaseVo(baseVo);
            singleSystemConfigVo.setSystemConfigVo(systemConfigVo);
            return singleSystemConfigVo;

        } catch (Exception e) {
            log.error(e.getMessage());
            end = System.currentTimeMillis();
            BaseVo baseVo = new BaseVo(500, end - start, false, "其它位置异常");
            singleSystemConfigVo.setBaseVo(baseVo);
            return singleSystemConfigVo;
        }
    }

    @GetMapping("/code/{code}")
    public SingleSystemConfigVo queryByCode(@PathVariable String code){
        long start = System.currentTimeMillis();
        long end;
        SingleSystemConfigVo singleSystemConfigVo = new SingleSystemConfigVo();

        try {
            SystemConfig systemConfig = systemConfigService.queryByCode(code);
            SystemConfigVo systemConfigVo = SystemConfigVoConverter.convertToVo(systemConfig);
            end = System.currentTimeMillis();
            BaseVo baseVo = new BaseVo(200, end - start, true, null);
            singleSystemConfigVo.setBaseVo(baseVo);
            singleSystemConfigVo.setSystemConfigVo(systemConfigVo);
            return singleSystemConfigVo;

        } catch (Exception e) {
            log.error(e.getMessage());
            end = System.currentTimeMillis();
            BaseVo baseVo = new BaseVo(500, end - start, false, "其它位置异常");
            singleSystemConfigVo.setBaseVo(baseVo);
            return singleSystemConfigVo;
        }
    }

    @GetMapping("/page/{start}/{size}")
    public MultiSystemConfigVo queryAll(@PathVariable int start, @PathVariable int size){
        long begin = System.currentTimeMillis();
        long end;
        MultiSystemConfigVo multiSystemConfigVo = new MultiSystemConfigVo();

        try {
            List<SystemConfig> systemConfigs = systemConfigService.queryAll(start, size);
            List<SystemConfigVo> systemConfigVoList = SystemConfigVoConverter.convertToVoList(systemConfigs);
            end = System.currentTimeMillis();
            BaseVo baseVo = new BaseVo(200, end - begin, true, null);
            multiSystemConfigVo.setBaseVo(baseVo);
            multiSystemConfigVo.setSystemConfigVoList(systemConfigVoList);
            return multiSystemConfigVo;

        } catch (Exception e) {
            log.error(e.getMessage());
            end = System.currentTimeMillis();
            BaseVo baseVo = new BaseVo(500, end - begin, false, "其它位置异常");
            multiSystemConfigVo.setBaseVo(baseVo);
            return multiSystemConfigVo;
        }
    }
}