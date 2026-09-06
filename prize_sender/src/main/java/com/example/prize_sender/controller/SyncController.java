package com.example.prize_sender.controller;

import com.example.prize_sender.controller.Vo.BaseVo;
import com.example.prize_sender.controller.cmd.SyncCmd;
import com.example.prize_sender.service.SyncService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/sync")
@CrossOrigin
@Slf4j
public class SyncController {
    @Autowired
    private SyncService syncService;
    @PostMapping("/sync")
    public BaseVo sync(SyncCmd sycCmd){
        long start = System.currentTimeMillis();
        long end;

        try {
            syncService.sync(sycCmd);

            end = System.currentTimeMillis();
            return new BaseVo(
                    200,
                    end - start,
                    true,
                    null
            );

        } catch (Exception e) {
            log.error("删除播放统计记录失败", e);

            end = System.currentTimeMillis();
            return new BaseVo(
                    500,
                    end - start,
                    false,
                    "其它位置异常"
            );
        }
    }
}
